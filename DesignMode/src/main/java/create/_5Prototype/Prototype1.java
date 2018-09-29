package create._5Prototype;

public class Prototype1 implements Cloneable {  
  
    public Object clone() throws CloneNotSupportedException {  
        Prototype1 proto = (Prototype1) super.clone();  
        return proto;  
    }  
}  