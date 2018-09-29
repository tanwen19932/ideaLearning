package linger;
import java.io.*;
import java.net.*;
public class SimpleServer {
  public static void main(String args[])throws Exception {
    ServerSocket serverSocket = new ServerSocket(8000);
    Socket s=serverSocket.accept();
    Thread.sleep(10000);  // sleep 10 seconds then read input
    InputStream in=s.getInputStream();
    ByteArrayOutputStream buffer=new ByteArrayOutputStream();
    byte[] buff=new byte[1024];
    int len=-1;
    do{
        len=in.read(buff);
        if(len!=-1)buffer.write(buff,0,len);
     }while(len!=-1);
    System.out.println(new String(buffer.toByteArray()));  //translate bytes to String
  }
}
