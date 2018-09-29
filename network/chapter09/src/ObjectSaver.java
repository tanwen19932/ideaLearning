import java.io.*;
import java.util.*;

public class ObjectSaver{

    public static void main(String agrs[]) throws Exception {
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("objectFile.obj"));

        String obj1="hello";
        Date obj2=new Date();
        Customer obj3=new Customer("Tom",20);
        // Serialize
        out.writeObject(obj1);
        out.writeObject(obj2);
        out.writeObject(obj3);
        out.writeInt(123);
        //leo add start
        out.writeInt(456);
        out.writeFloat(0.1f);
        out.writeBoolean(true);
        out.writeChars("chars");
        out.writeUTF("UTFstring字符串");
        out.close();
        //leo add end

        //deserialize
        ObjectInputStream in=new ObjectInputStream(new FileInputStream("objectFile.obj"));
        String obj11 = (String)in.readObject();
        System.out.println("obj11:"+obj11);
        System.out.println("obj11==obj1:"+(obj11==obj1));

        Date obj22 = (Date)in.readObject();
        System.out.println("obj22:"+obj22);
        System.out.println("obj22==obj2:"+(obj22==obj2));

        Customer obj33 = (Customer)in.readObject();
        System.out.println("obj33:"+obj33);
        System.out.println("obj33==obj3:"+(obj33==obj3));

        int var= in.readInt();
        System.out.println("var:"+var);

        // leo add start
        int var2= in.readInt();
        System.out.println("var2:"+var2);
        float f = in.readFloat();
        System.out.println("float:"+f);
        Boolean b = in.readBoolean();
        System.out.println("Boolean:"+b);
//    String chars = in.readObject();
//    System.out.println("String:"+chars);
//    String UTFstring = (String)in.readObject();
//    System.out.println("UTFstring:"+UTFstring);
        // leo add end
        in.close();
    }
}

class Customer implements Serializable{
    private String name;
    private int age;

    public Customer(String name,int age){
        this.name=name;
        this.age=age;
    }

    public String toString(){return "name="+name+",age="+age;}
}
