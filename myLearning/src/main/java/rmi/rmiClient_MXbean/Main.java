package rmi.rmiClient_MXbean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class Main {
    public static void main(String[] args)
            throws Exception {
        //      MBeanServer mbs = MBeanServerFactory.createMBeanServer();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("com.jmx.demo3:type=Libraries");
        List<Book> bookList = new ArrayList<Book>();
        bookList.add(new Book(1, "Thinking in Java", 99, "Bruce Eckel"));
        bookList.add(new Book(2, "Effective Java", 88, "Joshua Bloch"));
        bookList.add(new Book(3, "Core java", 42, "Cay S. Horstmann"));
        Libraries libraries = new Libraries(bookList);
        mbs.registerMBean(libraries, mxbeanName);
        System.out.println("Waiting forever...");
        Thread.sleep(Long.MAX_VALUE);
    }
}  