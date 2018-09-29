package multithread2;
import java.io.*;
import java.net.*;
public class EchoServer {
  private int mPort=8000;
  private ServerSocket mServerSocket;
  private ThreadPool mThreadPool;
  private final int POOL_SIZE=4;  //single CPU working thread number in thread pool

  public EchoServer() throws IOException {
    mServerSocket = new ServerSocket(mPort);

    //Runtime availableProcessors() return system CPU number
    //the more CPU, the more work thread in thread pool
    System.out.println("Runtime.getRuntime().availableProcessors():"+Runtime.getRuntime().availableProcessors());
    mThreadPool= new ThreadPool( 
            Runtime.getRuntime().availableProcessors() * POOL_SIZE);

    System.out.println("server launch");
  }

  public void service() {
    while (true) {
      Socket socket=null;
      try {
        socket = mServerSocket.accept();
        mThreadPool.execute(new Handler(socket)); //send thread pool the communication task with client
      }catch (IOException e) {
         e.printStackTrace();
      }
    }
  }

  public static void main(String args[])throws IOException {
    new EchoServer().service();
  }
}

class Handler implements Runnable{
  private Socket socket;
  public Handler(Socket socket){
    this.socket=socket;
  }
  private PrintWriter getWriter(Socket socket)throws IOException{
    OutputStream socketOut = socket.getOutputStream();
    return new PrintWriter(socketOut,true);
  }
  private BufferedReader getReader(Socket socket)throws IOException{
    InputStream socketIn = socket.getInputStream();
    return new BufferedReader(new InputStreamReader(socketIn));
  }
  public String echo(String msg) {
    return "echo:" + msg;
  }
  public void run(){
    try {
      System.out.println("New connection accepted " +
      socket.getInetAddress() + ":" +socket.getPort());
      BufferedReader br =getReader(socket);
      PrintWriter pw = getWriter(socket);

      String msg = null;
      while ((msg = br.readLine()) != null) {
        System.out.println(msg);
        pw.println(echo(msg));
        if (msg.equals("bye"))
          break;
      }
    }catch (IOException e) {
       e.printStackTrace();
    }finally {
       try{
         if(socket!=null)socket.close();
       }catch (IOException e) {e.printStackTrace();}
    }
  }
}
