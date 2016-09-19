package rmi.rmiClient2;

import java.io.Serializable;

public class Hello
        implements HelloMBean, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;

    @Override
    public synchronized void setName(String name) {
        this.name = name;
    }

    @Override
    public synchronized String getName() {
        return name;
    }

    @Override
    public synchronized void sayHello() {
        System.out.println("Hello," + name);
    }

} 