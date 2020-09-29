package fr.iutvalence.automath.app.view.handler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager responsible for spawning new worker units for each job. 
 */ 
public class TaskWorkerHandler extends Thread{
	
	private AtomicBoolean run = new AtomicBoolean(true);

    private Runnable runnable;

    private final Object lock = new Object();

	public TaskWorkerHandler(){
		super("TaskThread");
	}
     
	/**
	 * Take a parameter task and take care of running it
	 * @param newRunnable The task to be performed
	 */
    public void run(Runnable newRunnable) {
        synchronized(lock) {
            if(runnable != null) {
                throw new IllegalStateException("Already in execution");
            }           
            runnable = newRunnable;
            lock.notifyAll();
        }
    }
     
    /**
     * Stop the task thread
     */
	public void stopTaskThread() {
        synchronized (lock) {
            run.set(false);
        }        
    }
     
	/**
	 * Launching the task management thread
	 */
    public void run() {
        boolean ran = false;
        while (run.get()) {
            synchronized (lock) {
                try {
                    waitForRunnable();
                    ran = executeRunnable();
                } catch (Throwable exceptionInRunnable) {
                	exceptionInRunnable.printStackTrace();
                } finally {
                    cleanupRunnable();
                    if (ran) {
                        ran = false;
                    }
                }
            }
        }
    }
     
    /**
     * perform the task if possible
     * @return	<code>true</code> if it has been executed; 
     *         	<code>false</code> otherwise.
     */
    private boolean executeRunnable() {
        if (runnable == null) {
            return false;
        }
        runnable.run();
        return true;        
    }
     
    /**
     * Completed task completion
     */
    private void cleanupRunnable() {
        synchronized (lock) {
            runnable = null;
        }
    }
    
    /**
     * Waiting for the presence of a new spot
     */
    private void waitForRunnable() {
        while (runnable == null && run.get()) {
            try {
                lock.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}