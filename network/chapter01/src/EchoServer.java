import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
  private int mPort=8000;
  private ServerSocket mServerSocket;

  public EchoServer() throws IOException {
    mServerSocket = new ServerSocket(mPort);
    System.out.println("server launched, listen:"+mPort);
  }

  public String echo(String msg) {
    return "echo:" + msg;
  }

  private PrintWriter getWriter(Socket socket)throws IOException{
    OutputStream socketOut = socket.getOutputStream();
    return new PrintWriter(socketOut,true);
  }
  private BufferedReader getReader(Socket socket)throws IOException{
    InputStream socketIn = socket.getInputStream();
    return new BufferedReader(new InputStreamReader(socketIn));
  }

  public void service() {
    while (true) {
      Socket socket=null;
      try {
        socket = mServerSocket.accept();  //wait client connect
        System.out.println("New connection accepted " 
                        +socket.getInetAddress() + ":" +socket.getPort());
        BufferedReader br =getReader(socket);
        PrintWriter pw = getWriter(socket);

        String msg = null;
        while ((msg = br.readLine()) != null) {
          System.out.println(msg); 
          pw.println(echo(msg));
          if (msg.equals("bye")) {
              // finish communication by client send msg "bye"
              System.out.println("recv bye");
              break;
          }
        }
      }catch (IOException e) {
         e.printStackTrace();
      }finally {
         try{
           System.out.println("finally, close");
           if(socket!=null)socket.close();  //disconnect
         }catch (IOException e) {e.printStackTrace();}
      }
    }
  }

  public static void main(String args[])throws IOException {
    new EchoServer().service();
  }
}
