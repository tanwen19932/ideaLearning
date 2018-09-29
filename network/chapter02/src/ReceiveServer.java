import java.io.*;
import java.net.*;
public class ReceiveServer {
  public static void main(String args[])throws Exception {
    ServerSocket serverSocket = new ServerSocket(8000);
    Socket s=serverSocket.accept();
    //s.setSoTimeout(20000);   
    InputStream in=s.getInputStream();
    ByteArrayOutputStream buffer=new ByteArrayOutputStream();
    byte[] buff=new byte[1024];
    int len=-1;
    do{
      try{
        len=in.read(buff);
        if(len!=-1)buffer.write(buff,0,len);
      }catch(SocketTimeoutException e){
        System.out.println("�?�?读�???");
        len=0;
      }
    }while(len!=-1);
    System.out.println(new String(buffer.toByteArray()));  //??�????��?�???为�??�?�?
  }
}


/****************************************************
 * �???�?�?????                                     *
 * ?��?�?<<Java�?�?�?�?精解>>                       *
 * ????????�???�?www.javathinker.org                *
 ***************************************************/
