package multithread2;
import java.util.LinkedList;

/*
 * leo add comment:
 * this sample is good at commented out the method inherit from ThreadGroup or Thread.
 */
public class ThreadPool extends ThreadGroup {
  private boolean mIsClosed=false;  // thread pool is close or not
  private LinkedList<Runnable> mWorkQueue;  // work queue
  private static int sThreadPoolID;  // thread pool id
  private int mThreadID;  //work thread id

  public ThreadPool(int poolSize) { //poolSize means works threads number in thread pool
    super("ThreadPool-" + (sThreadPoolID++));
    //doc.api.java:
    /*
     * Changes the daemon status of this thread group.
     * A daemon thread group is automatically destroyed when its last thread is stopped
     *  or its last thread group is destroyed.*/
    setDaemon(true);
    mWorkQueue = new LinkedList<Runnable>();  //create working thread
    for (int i=0; i<poolSize; i++)
      new WorkThread().start();  //create and launch work thread
  }
 
/** add a new task to work queue, task will be executed by work thread */
  public synchronized void execute(Runnable task) {
    if (mIsClosed) {
      throw new IllegalStateException();
    }
    if (task != null) {
      mWorkQueue.add(task);
      notify();  // Wakes up a single thread that is waiting in getTask() 
    }
  }

  //doc.api.java
  /*
   * As in the one argument version, interrupts and spurious wakeups are possible, and this method should always be used in a loop: 

     synchronized (obj) {
         while (<condition does not hold>)
             obj.wait();
         ... // Perform action appropriate to condition
     }
*/
  /** pick up a task from work queue, work thread will call this method */
  protected synchronized Runnable getTask()throws InterruptedException{
    while (mWorkQueue.size() == 0) {
      if (mIsClosed) return null;
      wait();  // wait if no task in work queue
    }
    return mWorkQueue.removeFirst();
  }

  /** close thread pool */
  public synchronized void close() {
    if (!mIsClosed) {
      mIsClosed = true;
      mWorkQueue.clear(); // clear work queue
      interrupt();  // interrupt all work thread, inherit from ThreadGroup
    }
 }

  /** wait work thread complete all tasks */
  public void join() {
    synchronized (this) {
      mIsClosed = true;
      notifyAll();  // Wakes up a single thread that is waiting in getTask()
    }

    Thread[] threads = new Thread[activeCount()];
    //enumerate() inherit from ThreadGroup
    
    //doc.api.java:Copies into the specified array every active thread in this thread group and its subgroups.
    int count = enumerate(threads);  
    for (int i=0; i<count; i++) { //wait all work threads to die
      try {
        threads[i].join();  // wait a work thread to die
      }catch(InterruptedException ex) { }
    }
  }

  /**  inner class : work thread */
  private class WorkThread extends Thread {
    public WorkThread() {
      //add into current ThreadPool
      super(ThreadPool.this,"WorkThread-" + (mThreadID++));
    }
//doc.api.java
/*public Thread(ThreadGroup group,
        Runnable target,
        String name)
Allocates a new Thread object so that it has target as its run object, 
has the specified name as its name,
and belongs to the thread group referred to by group.*/

    public void run() {
      while (!isInterrupted()) {  //isInterrupted() inherit from Thread
        Runnable task = null;
        try { // pick up task
          task = getTask();
        }catch (InterruptedException ex){}

        // if getTask() return null or thread is interrupted during execute getTask(), then stop the thread
        if (task == null) return;
        
        try { //run task
          task.run();
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }//#while
    }//#run()
  }//#WorkThread
}
