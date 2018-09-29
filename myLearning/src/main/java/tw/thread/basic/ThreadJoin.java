package tw.thread.basic;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class ThreadJoin {
    public static void main(String[] args){
        Thread before = new Thread(() -> {
            System.out.println("老大你先");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread = new Thread(() -> {
            try {
                before.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("后边去垃圾！");

        });
        thread.start();
        before.start();
    }
}
