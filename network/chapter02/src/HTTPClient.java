import java.net.*;
import java.io.*;
public class HTTPClient {
  String host="www.javathinker.org";
  int port=80;
  Socket socket;
  
  public void createSocket()throws Exception{
    socket=new Socket("www.javathinker.org",80);
  }
  

  public void communicate()throws Exception{
    StringBuffer sb=new StringBuffer("GET "+"/index.jsp"+" HTTP/1.1\r\n");
    sb.append("Host: www.javathinker.org\r\n");
    sb.append("Accept: */*\r\n");
    sb.append("Accept-Language: zh-cn\r\n");
    sb.append("Accept-Encoding: gzip, deflate\r\n");
    sb.append("User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)\r\n");
    sb.append("Connection: Keep-Alive\r\n\r\n");

    // send HTTP request
    OutputStream socketOut=socket.getOutputStream();
    socketOut.write(sb.toString().getBytes());
    
   //close output stream (half close)
    socket.shutdownOutput();
       
    // receive response result
    InputStream socketIn=socket.getInputStream();

    // leo define
    boolean lineByLine = false;

    if(!lineByLine) {
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];  
        int len=-1;
        // -1 is end flag
        while((len=socketIn.read(buff))!=-1){
            /*
             * Note: save the whole data in buffer is not good.
             * save the data line by line is better.
             */
          buffer.write(buff,0,len);
        }
        
        System.out.println(new String(buffer.toByteArray()));  //translate byte array to string
        return;
    }

    // save the data line by line is better.
    BufferedReader br=new BufferedReader(new InputStreamReader(socketIn));
    String data;
    while((data=br.readLine())!=null){
      System.out.println(data);
    }

    socket.close();
  }
  
  public static void main(String args[])throws Exception{
    HTTPClient client=new HTTPClient();
    client.createSocket();
    client.communicate();
  } 
}
