package tw.thread.basic;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class WaitNotifyTimeout {
    public synchronized Object get(long mills) {
        long future = System.currentTimeMillis() + mills;
        long remaining = mills;
        Object result = null;
        while (result == null && remaining > 0) {
            try {
                TimeUnit.SECONDS.sleep(new Random().nextInt(3));
            } catch (InterruptedException e) {
            }
            remaining = future - System.currentTimeMillis();
            result = new Object();
        }
        return result;
    }

    public static void main(String[] args) {
        SimpleConnectionPool pool = new SimpleConnectionPool(10);
        ExecutorService threadPool = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
        for (int i = 0; i < 0; i++) {

        }
    }
}

class SimpleConnectionPool {
    LinkedList<Connection> pool;

    SimpleConnectionPool(int num) {
        pool = new LinkedList<Connection>();
        for (int i = 0; i < num; i++) {
            pool.add(ConnectionDriver.createConnection());
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                // 添加后需要进行通知，这样其他消费者能够感知到链接池中已经归还了一个链接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    public Connection getConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            long future = System.currentTimeMillis() + mills;
            long remaining = mills;
            Connection result = null;
            if (remaining <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                result = pool.removeFirst();
            }
            while (remaining > 0 && result == null) {
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                } else {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
            }
            return result;
        }
    }
}
