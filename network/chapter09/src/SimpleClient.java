import java.io.*;
import java.net.*;
import java.util.*;
public class SimpleClient {
  public void receive()throws Exception{
    Socket socket = new Socket("localhost",8000);
    InputStream in=socket.getInputStream();
    ObjectInputStream ois=new ObjectInputStream(in);
    Object object1=ois.readObject();
    Object object2=ois.readObject();
    float f = ois.readFloat();
    System.out.println(object1);
    System.out.println(object2);
    System.out.println(f);
    System.out.println("Is object1 object2 are same:"
                       +(object1==object2));
  }
  public static void main(String args[])throws Exception {
    new SimpleClient().receive(); 
  }
}
//doc.api.java
/*
An ObjectInputStream deserializes primitive data and objects previously written using an ObjectOutputStream. 

ObjectOutputStream and ObjectInputStream can provide an application with persistent storage for graphs of objects
 when used with a FileOutputStream and FileInputStream respectively. 
 ObjectInputStream is used to recover those objects previously serialized. 
 Other uses include passing objects between hosts using a socket stream or for marshaling
 and unmarshaling arguments and parameters in a remote communication system.
 
  For example to read from a stream as written by the example in ObjectOutputStream: 


        FileInputStream fis = new FileInputStream("t.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);

        int i = ois.readInt();
        String today = (String) ois.readObject();
        Date date = (Date) ois.readObject();

        ois.close();


*/