package rmi.rmiClient2;
import java.rmi.registry.LocateRegistry;  
import java.util.HashMap;  
  
import javax.management.MBeanServer;  
import javax.management.MBeanServerFactory;  
import javax.management.ObjectInstance;  
import javax.management.ObjectName;  
import javax.management.remote.JMXAuthenticator;  
import javax.management.remote.JMXConnectorServer;  
import javax.management.remote.JMXConnectorServerFactory;  
import javax.management.remote.JMXServiceURL;  
import javax.security.auth.Subject;  
  
public class JMXAgent {  
    /** 
     * @param args 
     *            the command line arguments 
     */  
    public static void main(String[] args) throws Exception {  
        System.out.println("--------------JMX Agent----------- ");  
        LocateRegistry.createRegistry(1099);  
        MBeanServer server = MBeanServerFactory.createMBeanServer();  
        ObjectName helloName = new ObjectName("rmiClient2:name=Hello");  
        Hello hello = new Hello();  
        HashMap<String, Object> prop = new HashMap<String, Object>();  
        prop.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator() {  
            public Subject authenticate(Object credentials) {  
                if (credentials instanceof String) {  
                    if (credentials.equals("Hello")) {  
                        return new Subject();  
                    }  
                }  
                throw new SecurityException("not authicated");  
            }  
        });  
        JMXConnectorServer cserver = JMXConnectorServerFactory  
                .newJMXConnectorServer(new JMXServiceURL(  
                        "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"),  
                        prop, server);  
        server.registerMBean(hello, helloName);  
        cserver.start();  
        for (ObjectInstance object : server.queryMBeans(null, null)) {  
            System.out.println(object.getObjectName());  
        }  
        System.out.println(hello);  
        System.out.println("start.....");  
        System.out.println("\n");  
    }  
}  