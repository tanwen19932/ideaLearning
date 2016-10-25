package com.yin.serialize;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
 
//实现Runnable接口，实现一个读的线程
class Read implements Runnable {
    private PipedInputStream in;
    //将需要读的管道流传入到构造函数中
    public Read(PipedInputStream in) {
        this.in = in;
    }
     
    //实现读这一线程
    public void run() {
        try {
            byte[] buf = new byte[1024];
            int temp = 0;
            //循环读取
            //read是一个阻塞方法，需要抛异常
            //此处把打印流的代码也加入进来
            //是因为如果没有读取到数据，那么打印的代码也无效
            while((temp = in.read(buf)) != -1) {
                String str = new String(buf,0,temp);
                System.out.println(str);
            }
        } catch (IOException e) {
            //其实这里应抛出一个自定义异常的
            //暂时我还没弄清楚
            e.printStackTrace();
        } finally {
            try {
                //我已经抛火了，这只是为了提醒自己异常很重要
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }  
}
 
//这里实现一个写的类
class Write implements Runnable {
    private PipedOutputStream out;
    //将管道输入流传进来
    public Write(PipedOutputStream out) {
        this.out = out;
    }
 
    public void run() {
        try {
            //这里开始写出数据
            out.write("管道操作=========看到没，就是这句！！".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //其实应该可以把这个关闭方法写到上面那个try里边
                //但是这样感觉怪怪的，逻辑不大对
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
 
public class Demo {
    public static void main(String[] args) {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream();
        try {
            //连接管道
            in.connect(out);
             
            //创建对象，开启线程
            //此处同样放进try...catch里面
            //因为如果没有链接管道，下面操作无意义
            Read r = new Read(in);
            Write w = new Write(out);
            //把已经实现好run方法的对象放入线程中执行
            new Thread(r).start();
            new Thread(w).start();
             
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}