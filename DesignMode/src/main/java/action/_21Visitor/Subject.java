package action._21Visitor;
public interface Subject {  
    void accept(Visitor visitor);
    String getSubject();
}  