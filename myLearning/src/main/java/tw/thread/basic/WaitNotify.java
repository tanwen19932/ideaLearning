package tw.thread.basic;

import java.util.concurrent.TimeUnit;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class WaitNotify {
    public static void main(String[] args){
        Object lock = new Object();
        final boolean[] condition = {false};
        Thread thread = new Thread(() -> {
            synchronized (lock) {
                while(!condition[0]){
                    try {
                        lock.wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("你说干活儿就干活儿了哦");
                }
            }
        });
        thread.start();
        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                condition[0] = true;
                lock.notifyAll();
                System.out.println("干活儿了");
            }
        });
        thread2.start();
    }

}
