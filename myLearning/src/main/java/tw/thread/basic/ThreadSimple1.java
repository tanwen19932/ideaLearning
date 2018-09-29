package tw.thread.basic;

import java.util.concurrent.TimeUnit;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class ThreadSimple1 {
    public static void main(String[] args){
        Thread thread = new Thread(()-> System.out.println("hello"));
        thread.start();
        Thread thread1 = new Thread(() -> {
            while(true){
                try {
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("休息足够");
                } catch (InterruptedException e) {
                    System.out.println("休想搞掉我");
                }
            }
        });
        thread1.start();
        thread1.interrupt();
        System.out.println(thread1.getState());
    }

}
