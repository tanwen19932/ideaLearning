package linger;
import java.io.*;
import java.net.*;
public class SimpleClient {
  public static void main(String args[])throws Exception {
    Socket s = new Socket("localhost",8000);
    //s.setSoLinger(true,0);  // Socket close, lower socket close immediately
    //s.setSoLinger(true,3600);  // Socket close, lower socket close after 3600 seconds
    OutputStream out=s.getOutputStream();
    StringBuffer sb=new StringBuffer();
    for(int i=0;i<10000;i++)sb.append(i);
    out.write(sb.toString().getBytes());  //send 10000 bytes
    System.out.println("close Socket");
    long begin=System.currentTimeMillis();
    s.close();
    long end=System.currentTimeMillis();
    System.out.println("during of closing Socket:"+(end-begin)+"ms");
  }
}

