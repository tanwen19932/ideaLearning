import java.net.*;
public class Client {
    public static void main(String args[])throws Exception{
        final int mLength=100;
        String host="localhost";
        int port=8000;

        Socket[] sockets=new Socket[mLength];
        for(int i=0;i<mLength;i++){  // try to establish 100 connections
            sockets[i]=new Socket(host, port);
            System.out.println("第"+(i+1)+"次连接成功");
        }
        Thread.sleep(3000);
        for(int i=0;i<mLength;i++){
            sockets[i].close();  //断开连接
        }
    }
}
