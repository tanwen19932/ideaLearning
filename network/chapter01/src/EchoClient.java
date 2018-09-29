import java.io.*;
import java.net.Socket;

public class EchoClient {
  private String mHost="localhost";
  private int mPort=8000;
  private Socket mSocket;
  
  public EchoClient()throws IOException{
     mSocket=new Socket(mHost,mPort);  
  }
  public static void main(String args[])throws IOException{
    new EchoClient().talk();
  }
  private PrintWriter getWriter(Socket socket)throws IOException{
    OutputStream socketOut = socket.getOutputStream();
    return new PrintWriter(socketOut,true);
  }
  private BufferedReader getReader(Socket socket)throws IOException{
    InputStream socketIn = socket.getInputStream();
    return new BufferedReader(new InputStreamReader(socketIn));
  }
  public void talk()throws IOException {
    try{
      BufferedReader br=getReader(mSocket);
      PrintWriter pw=getWriter(mSocket);
      BufferedReader localReader=new BufferedReader(new InputStreamReader(System.in));
      String msg=null;
      while((msg=localReader.readLine())!=null){

        pw.println(msg);
        System.out.println("echo:"+br.readLine());

        if(msg.equals("bye"))
          break;
      }
    }catch(IOException e){
       e.printStackTrace();
    }finally{
       try{mSocket.close();}catch(IOException e){e.printStackTrace();}
    }
  }
}

