package action._15Observer;
public class MySubject extends AbstractSubject {
  
    @Override  
    public void operation() {
        System.out.println("den");
        System.out.println("");
        System.out.println("update self!");
        notifyObservers();
//        if ( != null) {
//            se saf
//        }
    }
  
}