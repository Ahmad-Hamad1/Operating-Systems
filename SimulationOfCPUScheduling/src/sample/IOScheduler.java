package sample;


import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class IOScheduler implements Runnable {
    public static LinkedList<Process> IOQueue = new LinkedList<>();
    private static IOScheduler IO = null;
    private final Semaphore mutex = new Semaphore(1);
    private static boolean IOBusy = false;

    private IOScheduler(){

    }

    public synchronized static IOScheduler getInstance(){
        if(IOScheduler.IO == null){
            IOScheduler.IO = new IOScheduler();
        }
        return IOScheduler.IO;
    }

    public  void waitIOQueue() throws InterruptedException {
        mutex.acquire();
    }

    public  void signalIOQueue(){
        mutex.release();
    }

    public  void addToIOQueue(Process process){
        IOScheduler.IOQueue.add(process);
    }

    @Override
    public void run() {
        Process runningProcess = new Process();
        long time;
        while(true){
            try {
                FinishedProcesessSynch.getInstance().waitFinished();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(FinishedProcesessSynch.finishedProcesses.size() == SimulateController.numberOfProcesses) {
                FinishedProcesessSynch.getInstance().signalFinished();
                break;
            }
            FinishedProcesessSynch.getInstance().signalFinished();
            try {
                TimeListSynchronization.getInstance().waitTimeList();
                if (TimeListSynchronization.canThreadTakeReadTimeVariable.get(1)) {
                    time = TimeListSynchronization.currentTime;
                    TimeListSynchronization.getInstance().signalTimeList();
                    this.waitIOQueue();
                    if (!IOBusy && IOQueue.size() > 0) {
                        runningProcess = IOQueue.poll();
                        runningProcess.setAddedToIODevice(time);
                        IOBusy = true;
                    }
                    this.signalIOQueue();
                    if (IOBusy) {
                        long remainingTime = time - runningProcess.getAddedToIODevice();
                        if (remainingTime == runningProcess.getIOBurstTime()) {
                            runningProcess.updateIOBurst(0L);
                            if (runningProcess.getLastIOBurst() == 0) {
                                runningProcess.setFinished(time);
                                runningProcess.setState("Finished");
                                FinishedProcesessSynch.getInstance().waitFinished();
                                FinishedProcesessSynch.finishedProcesses.add(runningProcess);
                                TimeDetailsLock.getInstance().lock();
                                TimeDetailsLock.specificTimeDetails.computeIfAbsent(TimeListSynchronization.currentTime, k -> new LinkedList<>());
                                TimeDetailsLock.specificTimeDetails.get(TimeListSynchronization.currentTime).add("Process " + runningProcess.getPid()+" terminated");
                                TimeDetailsLock.getInstance().unlock();
                                System.out.println("Finished: " + runningProcess.getPid()+" "+runningProcess.getFinished());
                                FinishedProcesessSynch.getInstance().signalFinished();
                            } else {
                                runningProcess.setState("Ready");
                                GlobalQueue.getInstance().readWriteLock();
                                GlobalQueue.global.add(runningProcess);
                                TimeDetailsLock.getInstance().lock();
                                TimeDetailsLock.specificTimeDetails.computeIfAbsent(TimeListSynchronization.currentTime, k -> new LinkedList<>());
                                TimeDetailsLock.specificTimeDetails.get(TimeListSynchronization.currentTime).add("Process " + runningProcess.getPid()+" is moved from IO queue to global queue");
                                TimeDetailsLock.getInstance().unlock();
                                GlobalQueue.getInstance().readWriteUnlock();
                            }
                            this.waitIOQueue();
                            if (IOQueue.size() == 0)
                                IOBusy = false;
                            else {
                                runningProcess = IOQueue.poll();
                                runningProcess.setAddedToIODevice(time);
                            }
                            this.signalIOQueue();

                        } else {
                            runningProcess.updateIOBurst(runningProcess.getIOBurstTime() - remainingTime);
                            runningProcess.setAddedToIODevice(time);
                        }
                    }
                    TimeListSynchronization.getInstance().waitTimeList();
                    TimeListSynchronization.canThreadTakeReadTimeVariable.set(1, false);
                    TimeListSynchronization.getInstance().signalTimeList();
                    continue;
                }
                TimeListSynchronization.getInstance().signalTimeList();
            }
            catch(InterruptedException e){
                    e.printStackTrace();
                }
        }
    }
}