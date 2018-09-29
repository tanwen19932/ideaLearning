package multithread2;
public class ThreadPoolTester {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println(
      "usage: java ThreadPoolTest numTasks poolSize");
      System.out.println(
      "       numTasks - integer: number of tasks");
      System.out.println(
      "       numThreads - integer: number of thread in thread pool");
      return;
    }
    int numTasks = Integer.parseInt(args[0]);
    int poolSize = Integer.parseInt(args[1]);

   ThreadPool threadPool = new ThreadPool(poolSize);  //create thread pool

   // run task
    for (int i=0; i<numTasks; i++) {
        threadPool.execute(createTask(i));
    }
 
    threadPool.join();  // wait for work thread finish all tasks
//     threadPool.close(); // close thread pool
  }//#main()
  
  /**  define a simple task (print ID) */
  private static Runnable createTask(final int taskID) {
    return new Runnable() {
      public void run() {
        System.out.println("Task " + taskID + ": start");
        try {
          Thread.sleep(500);  // raise time cost to finish a task
        } catch (InterruptedException ex) { }
        System.out.println("Task " + taskID + ": end");
      }
    };
  }
}
