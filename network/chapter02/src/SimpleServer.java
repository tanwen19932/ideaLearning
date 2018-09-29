import java.io.*;
import java.net.*;
public class SimpleServer {
  public static void main(String args[])throws Exception {
      long now = System.currentTimeMillis();
    System.out.println("now:"+now);
    
    ServerSocket serverSocket = new ServerSocket(8000,2);  //the request connection queue length is 2
    long bs = System.currentTimeMillis();
    System.out.println("begin sleep:"+bs+", during:"+ (bs - now));
    Thread.sleep(300000);
    long wake = System.currentTimeMillis();
    System.out.println("wake:"+wake+", during:"+(wake - bs));
  }
}

