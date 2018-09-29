import java.net.*;
import java.io.*;
import java.util.*;
public class Sender {
  private String host="localhost";
  private int port=8000;
  private Socket socket;
  private static int stopWay=1;  //finish connection way
  private final int NATURAL_STOP=1; //natural
  private final int SUDDEN_STOP=2;  //sudden
  private final int SOCKET_STOP=3;  //close socket
  private final int OUTPUT_STOP=4;  //close output stream

  public Sender()throws IOException{
     socket=new Socket(host,port);
  }
  public static void main(String args[])throws Exception{
    if(args.length>0)stopWay=Integer.parseInt(args[0]);
    new Sender().send();
  }
  private PrintWriter getWriter(Socket socket)throws IOException{
    OutputStream socketOut = socket.getOutputStream();
    return new PrintWriter(socketOut,true);
  }

  
  public void send()throws Exception {
    PrintWriter pw=getWriter(socket);
    for(int i=0;i<20;i++){
      String msg="hello_"+i; 
      pw.println(msg);
      System.out.println("send:"+msg);
      Thread.sleep(500);  
      if(i==2){  //stop program, close connection
        if(stopWay==SUDDEN_STOP){
          System.out.println("sudden stop program");
          System.exit(0);
        }else if(stopWay==SOCKET_STOP){
          System.out.println("close socket and stop program");
          socket.close();
          break;
        }else if(stopWay==OUTPUT_STOP){
          socket.shutdownOutput();
  
        System.out.println("close input and stop program");
          break;
        }
      }  
    }
    
    if(stopWay==NATURAL_STOP){
      socket.close();
    }
  }
}


/****************************************************
 * 锟斤拷锟竭ｏ拷锟斤拷锟斤拷锟斤拷                                     *
 * 锟斤拷源锟斤拷<<Java锟斤拷锟斤拷锟教撅拷锟斤拷>>                       *
 * 锟斤拷锟斤拷支锟斤拷锟斤拷址锟斤拷www.javathinker.org                *
 ***************************************************/
