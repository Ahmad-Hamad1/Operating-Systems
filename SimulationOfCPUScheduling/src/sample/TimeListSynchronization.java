package sample;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class TimeListSynchronization {
    public static final LinkedList<Boolean> canThreadTakeReadTimeVariable = new LinkedList<>();
    public static long currentTime = 0;
    private final Semaphore mutex = new Semaphore(1);
    private static TimeListSynchronization timeListLock = null;

    private TimeListSynchronization(){

    }

    public synchronized static TimeListSynchronization getInstance(){
        if(timeListLock == null)
            timeListLock = new TimeListSynchronization();
        return timeListLock;
    }

    public void waitTimeList() throws InterruptedException {
        mutex.acquire();
    }

    public   void signalTimeList(){
        mutex.release();
    }
}
