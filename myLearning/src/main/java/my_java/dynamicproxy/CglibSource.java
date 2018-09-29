package my_java.dynamicproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author TW
 * @date TW on 2017/4/17.
 */
public class CglibSource {
    public void sayHello(){
        System.out.println("Hello cglib");
    }
}
class CglibProxy implements MethodInterceptor{
    private Enhancer enhancer = new Enhancer();
    public Object getProxy(Class clazz){
        //设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        //通过字节码技术动态创建子类实例
        return enhancer.create();
    }
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("前置代理");
        //通过代理类调用父类中的方法
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("后置代理");
        return null;
    }

}
class CGLibTest{
    public static void main(String[] args){
        CglibProxy proxy = new CglibProxy();
        CglibSource proxyImpl = (CglibSource) proxy.getProxy(CglibSource.class);
        System.out.println(proxyImpl.getClass().getName());
        proxyImpl.sayHello();
    }
}
