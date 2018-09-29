import java.net.*;
import java.io.*;

public class MailSender{
  private String smtpServer="smtp.mydomain.com";  //SMTP??件�???��?��??主�?��??
  //private String smtpServer="localhost";
  private int port=25;

  public static void main(String[] args){
    Message msg=new Message("tom@abc.com",   //??????????件�?��??
                            "linda@def.com",  //?��?��??????件�?��??
                           "hello",  //??件�??�?
                           "hi,I miss you very much."); //??件�?��??
    new MailSender().sendMail(msg);
  }

  public void sendMail(Message msg){
    Socket socket=null;
    try{
      socket = new Socket(smtpServer,port);  //�??��?��??件�???��??
      BufferedReader br =getReader(socket);
      PrintWriter pw = getWriter(socket);
      String localhost= InetAddress.getLocalHost().getHostName();   //客�?�主?��????�?

      sendAndReceive(null,br,pw); //�?�???为�??��?��???��?��????�??��??
      sendAndReceive("HELO " + localhost,br,pw);
      sendAndReceive("MAIL FROM: <" + msg.from+">",br,pw);
      sendAndReceive("RCPT TO: <" + msg.to+">",br,pw);
      sendAndReceive("DATA",br,pw);  //?��??��?�???????件�??�?
      pw.println(msg.data);  //??????件�??�?
      System.out.println("Client>"+msg.data);
      sendAndReceive(".",br,pw);  //??件�????�?�?
      sendAndReceive("QUIT",br,pw);  //�?????�?
    }catch (IOException e){
      e.printStackTrace();
    }finally{
      try{
        if(socket!=null)socket.close();
      }catch (IOException e) {e.printStackTrace();}
    }
  }

  /** ????�?�?�?�?串�?并�?��?��?�????��?��????�??��??*/
  private void sendAndReceive(String str,BufferedReader br,PrintWriter pw) throws IOException{
    if (str != null){
      System.out.println("Client>"+str);
      pw.println(str);  //????�?str�?�?串�??�?�?�???????\r\n????
    }
    String response;
    if ((response = br.readLine()) != null)
      System.out.println("Server>"+response);
  }

   private PrintWriter getWriter(Socket socket)throws IOException{
     OutputStream socketOut = socket.getOutputStream();
     return new PrintWriter(socketOut,true);
   }
   private BufferedReader getReader(Socket socket)throws IOException{
     InputStream socketIn = socket.getInputStream();
     return new BufferedReader(new InputStreamReader(socketIn));
  }
}

class Message{  //表示??�?
  String from;  //??????????件�?��??
  String to;  //?��?��??????件�?��??
  String subject;  //??件�??�?
  String content;  //??件�?��??
  String data;  //??件�??容�???????件�??�???正�??
  public Message(String from,String to, String subject, String content){
    this.from=from;
    this.to=to;
    this.subject=subject;
    this.content=content;
    data="Subject:"+subject+"\r\n"+content;
  }
}


/****************************************************
 * �???�?�?????                                     *
 * ?��?�?<<Java�?�?�?�?精解>>                       *
 * ????????�???�?www.javathinker.org                *
 ***************************************************/
