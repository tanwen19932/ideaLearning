package thread;

/**
 * 多线程中的 wait 和 notify 使用
 *
 * @author TW
 * @date TW on 2017/4/28.
 */
public class Wait {
    public static void main(String[] args) {
        final Object obj = new Object();

        Thread wait = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (obj){
                            System.out.println("我睡着了啊!");
                            obj.wait();
                            System.out.println("回到这里");
                        }
                    } catch (InterruptedException e) {
                        System.out.println("被叫醒了 不用 wait 了");
                        break;
                    }
                }
            }
        });


        Thread notify = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1001);
                    System.out.println("我要叫醒你们了");
                    synchronized (obj){
                        obj.notify();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        wait.start();
        notify.start();
    }
}
