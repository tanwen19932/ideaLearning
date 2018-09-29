package cxf;

import javax.xml.ws.Endpoint;

public class webServiceApp {
    public static void main(String[] args) {
        System.out.println( "web service start" );
        HelloWorldImpl implementor = new HelloWorldImpl();
        String address = "http://localhost:8083/helloWorld";
        Endpoint.publish( address, implementor );
        System.out.println( "web service started" );
    }
}