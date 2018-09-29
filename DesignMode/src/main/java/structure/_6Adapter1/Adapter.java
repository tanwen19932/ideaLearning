package structure._6Adapter1;


/**
 * 类的适配器模式</br>
 * 有一个Source类，拥有一个方法，待适配，目标接口是Targetable，</br>
 * 通过Adapter类，将Source的功能扩展到Targetable里</br>
 * @author TW
 *
 */
public class Adapter extends Source implements Targetable {  
  
    @Override  
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }  
} 