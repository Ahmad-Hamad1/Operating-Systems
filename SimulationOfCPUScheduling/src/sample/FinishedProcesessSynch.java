package sample;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class FinishedProcesessSynch {
    public static LinkedList<Process> finishedProcesses = new LinkedList<>();
    private final Semaphore mutex = new Semaphore(1);
    private static FinishedProcesessSynch obj = null;

    private FinishedProcesessSynch(){

    }

    public synchronized static FinishedProcesessSynch getInstance(){
        if (obj == null)
            obj = new FinishedProcesessSynch();
        return obj;
    }

    public  void waitFinished() throws InterruptedException {
        mutex.acquire();
    }

    public  void signalFinished(){
        mutex.release();
    }
}
