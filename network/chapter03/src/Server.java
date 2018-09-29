import java.io.*;
import java.net.*;
public class Server {
  private int port=8000;
  private ServerSocket mServerSocket;

  public Server() throws IOException {
    mServerSocket = new ServerSocket(port,3);  // connection request queue length 3
    System.out.println("server launch");
  }

  public void service() {
    while (true) {
      Socket socket=null;
      try {
        socket = mServerSocket.accept();  // get a connection from request queue
        System.out.println("New connection accepted " +
        socket.getInetAddress() + ":" +socket.getPort());
      }catch (IOException e) {
         e.printStackTrace();
      }finally {
         try{
           if(socket!=null)socket.close();
         }catch (IOException e) {e.printStackTrace();}
      }
    }
  }

  public static void main(String args[])throws Exception {
    Server server=new Server();
    Thread.sleep(60000*10);  // sleep 10 minutes
//    server.service();
  }
}
