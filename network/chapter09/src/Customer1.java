import java.io.*;
public class Customer1 implements Serializable {
  private static int count; // count Customer
  private static final int MAX_COUNT=1000;
  private String name;
  private transient String password;
  
  static{
     System.out.println("call Customer1 static block");
  }
  public Customer1(){
    System.out.println("call Customer1 constructor without parameter");
    count++;
  }
  public Customer1(String name, String password) {
    System.out.println("call Customer1 constructor with parameter");
    this.name=name;
    this.password=password;
    count++;
  }
  public String toString() {
    return "count="+count
           +" MAX_COUNT="+MAX_COUNT
           +" name="+name
           +" password="+ password;
  }
}
