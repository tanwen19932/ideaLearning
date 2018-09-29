package tw.thread.basic;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class TWThreadPool<Job extends Runnable> implements ThreadPool<Job>{




    @Override
    public void execute(Job job) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void addWorkers(int num) {

    }

    @Override
    public void removeWorker(int num) {

    }

    @Override
    public int getJobSize() {
        return 0;
    }


    class worker{}

}
