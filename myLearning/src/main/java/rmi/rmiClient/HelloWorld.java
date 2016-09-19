package rmi.rmiClient;

public class HelloWorld
        implements HelloWorldMBean {

    private String greeting = null;

    public HelloWorld(String greeting) {
        this.greeting = greeting;
    }

    public HelloWorld() {
        this.greeting = "Hello World! I am Standard MBean";
    }

    @Override
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String getGreeting() {
        return greeting;
    }

    @Override
    public void printGreeting() {
        System.out.println(greeting);
    }

}  