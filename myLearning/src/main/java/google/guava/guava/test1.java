package google.guava.guava;

import com.google.common.util.concurrent.*;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author TW
 * @date TW on 2016/8/22.
 */
public class test1 {
    Optional<String> string = null;
    public static void test18() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3, new ThreadFactory() {
            private AtomicLong index = new AtomicLong(0);
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "commons-thread-" + index.incrementAndGet());
            }
        });
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPool);
        ListenableFuture<String> listenableFuture = listeningExecutorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(3000);
                //System.out.println(1 / 0);
                return "world";
            }
        });
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                System.out.println("can't get return value");
            }
        }, MoreExecutors.directExecutor());

        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("the result of future is: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("exception:" + t.getMessage());
            }
        });

        Thread.sleep(5000);
    }

    public static void test20() throws InterruptedException {
        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(3));
        ListenableScheduledFuture<String> schedule = listeningScheduledExecutorService.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "world";
            }
        }, 3, TimeUnit.SECONDS);
        Futures.addCallback(schedule, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("hello " + result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        Thread.sleep(4000);
    }

    public static void test19() throws InterruptedException {
        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(3));
        //只有callable才对应有future
        ListenableScheduledFuture<?> listenableScheduledFuture = listeningScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        }, 5, 3, TimeUnit.SECONDS);

        //因为上面的传的是runnable，所以没有返回值，没有返回值就不会触发future的callBack
        Futures.addCallback(listenableScheduledFuture, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("not result: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        Thread.sleep(12000);
    }

    public static void main(String[] args){
        try {
            test18();
            test19();
            test20();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
