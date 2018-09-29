package structure._7Decorator;


/**与Adapter的模式不同</br>
 * Adapter是已知类 通过接口实现方法</br>
 * 这个通过实现相同接口后 装饰原来方法</br>
 * @author TW
 *
 */
public class Decorator implements Sourceable {  
  
    private Sourceable source;  
      
    public Decorator(Sourceable source){  
        super();  
        this.source = source;  
    }  
    @Override  
    public void method() {  
        System.out.println("before decorator!");  
        source.method();  
        System.out.println("after decorator!");  
    }  
} 