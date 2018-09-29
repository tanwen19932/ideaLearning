package structure._6Adapter1;

/**
 * 对象的适配器模式</br>
 * 持有Source类的实例，以达到解决兼容性的问题。</br>
 * @author TW
 *
 */
public class Wrapper implements Targetable {  
  
    private Source source;  
      
    public Wrapper(Source source){  
        super();  
        this.source = source;  
    }  
    @Override  
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }  
  
    @Override  
    public void method1() {  
        source.method1();  
    }  
}  