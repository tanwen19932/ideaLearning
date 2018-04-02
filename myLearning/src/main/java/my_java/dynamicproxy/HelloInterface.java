package my_java.dynamicproxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface HelloInterface {
    public void sayHello();
}
class MyProxy implements InvocationHandler {
    Object source ;
    public MyProxy(Object source){
        this.source = source;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Proxy !!!");
        System.out.println(proxy.getClass().getName());
        Object result = method.invoke(source, args);
        return result;
    }
}
class Primitive implements HelloInterface {
    public void sayHello(){
        System.out.println("~~~Primitive ~~~Say hello ~");
    }

    @Override
    public String toString() {
        return "Primitive{num1}";
    }
}
class JDKProxyTest {
    public static void main(String[] args) {
        Primitive primitive = new Primitive();
        HelloInterface proxySubject = (HelloInterface) Proxy.newProxyInstance(
                HelloInterface.class.getClassLoader(),
                new Class[]{HelloInterface.class},
                new MyProxy(primitive));
        proxySubject.sayHello();
        createProxyClassFile();
    }

    public static void createProxyClassFile() {
        String name = "ProxySubject";
        byte[] data = ProxyGenerator.generateProxyClass(name, new Class[]{HelloInterface.class});
        try {
            FileOutputStream out = new FileOutputStream(name + ".class");
            out.write(data);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}