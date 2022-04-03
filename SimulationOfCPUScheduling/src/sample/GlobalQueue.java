package sample;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class GlobalQueue {
    public static final LinkedList<Process> global = new LinkedList<>();
    private final Semaphore writeSemaphore = new Semaphore(1);
    private static GlobalQueue obj = null;

    private GlobalQueue(){

    }

    public synchronized static GlobalQueue getInstance(){
        if(obj == null)
            obj = new GlobalQueue();
        return obj;
    }

    // Get read write lock
    public  void readWriteLock() throws InterruptedException {
        writeSemaphore.acquire();
    }

    //Release read write lock
    public  void readWriteUnlock(){
        writeSemaphore.release();
    }

}