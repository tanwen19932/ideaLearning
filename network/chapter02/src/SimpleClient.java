import java.io.*;
import java.net.*;
public class SimpleClient {
  public static void main(String args[])throws Exception {
    Socket s1 = new Socket("localhost",8000);
    System.out.println("first connection success!");
    Socket s2 = new Socket("localhost",8000);
    System.out.println("second connection success!");
    Socket s3 = new Socket("localhost",8000);
    System.out.println("third connection success!");
    Socket s4 = new Socket("localhost",8000);
    System.out.println("fourth connection success!");
    Socket s5 = new Socket("localhost",8000);
    System.out.println("fifth connection success!");
  }
}
