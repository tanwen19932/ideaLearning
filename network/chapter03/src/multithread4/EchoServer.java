package multithread4;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EchoServer {
  private int mPort=8000;
  private ServerSocket mServerSocket;
  private ExecutorService mExecutorService; // thread pool
  private final int POOL_SIZE=4;  //single CPU working thread number in thread pool
  
  private int mPortForShutdown=8001;  // listen shutdown server cmd
  private ServerSocket mServerSocketForShutdown;
  private boolean mIsShutdown=false; // server is shutdown or not

  private Thread mShutdownThread=new Thread(){   // charge of shutdown server
    public void start(){
      this.setDaemon(true);  // set daemon thread
      super.start();
    }

    public void run(){
      while (!mIsShutdown) {
        Socket socketForShutdown=null;
        try {
          socketForShutdown= mServerSocketForShutdown.accept();
          BufferedReader br = new BufferedReader(
                            new InputStreamReader(socketForShutdown.getInputStream()));
          String command=br.readLine();
         if(command.equals("shutdown")){
            long beginTime=System.currentTimeMillis(); 
            socketForShutdown.getOutputStream().write("Server is shuting down\r\n".getBytes());
            mIsShutdown=true;
            //request shutdown thread pool
//thread pool will not accpet new task, but will continue to finish task in work queue
            mExecutorService.shutdown();  
            
            //wait for close thread pool, timeout 30 seconds each time
            while(!mExecutorService.isTerminated())
              mExecutorService.awaitTermination(30,TimeUnit.SECONDS); 
            
            mServerSocket.close(); // close ServerSocket communicate with EchoClient 
            long endTime=System.currentTimeMillis(); 
            socketForShutdown.getOutputStream().write(("Server shutdown, "+
                "cost"+(endTime-beginTime)+"millseconds\r\n").getBytes());
            socketForShutdown.close();
            mServerSocketForShutdown.close();
            
          }else{
            socketForShutdown.getOutputStream().write("wrong cmd\r\n".getBytes());
            socketForShutdown.close();
          }  
        }catch (Exception e) {
           e.printStackTrace();
        } 
      } 
    }
  };

  public EchoServer() throws IOException {
    mServerSocket = new ServerSocket(mPort);
    mServerSocket.setSoTimeout(60000); //the time out of waiting customer connect is 60 second
    mServerSocketForShutdown = new ServerSocket(mPortForShutdown);

    //create thread pool
    mExecutorService= Executors.newFixedThreadPool( 
	    Runtime.getRuntime().availableProcessors() * POOL_SIZE);
    
    mShutdownThread.start(); //launch thread that charge shutdown server
    System.out.println("Serve launch");
  }
  
  public void service() {
    while (!mIsShutdown) {
      Socket socket=null;
      try {
        socket = mServerSocket.accept();  
        socket.setSoTimeout(60000);  //the time out of waiting customer send data is 60 second
        mExecutorService.execute(new Handler(socket));  //maybe RejectedExecutionException
      }catch(SocketTimeoutException e){
         //not handle timeout exception waiting for customer connection
      }catch(RejectedExecutionException e){
         try{
           if(socket!=null)socket.close();
         }catch(IOException x){}
         return;
      }catch(SocketException e) {
         // if during serverSocket.accept(),
         //the exception due to ServerSocket shutdown by ShutdownThread, then exit method service()
         if(e.getMessage().indexOf("socket closed")!=-1)return;
       }catch(IOException e) {
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
