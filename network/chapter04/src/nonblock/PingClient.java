package nonblock;
import java.net.*;
import java.nio.channels.*;
import java.nio.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

class Target {  //表示�?项任??
  InetSocketAddress address;
  SocketChannel channel;
  Exception failure;
  long connectStart;  //�?�?�??��?��???��??
  long connectFinish = 0;  //�??��?????��???��??
  boolean shown = false;  //该任?��????已�?????

  Target(String host) {
      try {
          address = new InetSocketAddress(InetAddress.getByName(host),80);
      } catch (IOException x) {
          failure = x;
      }
  }

  void show() {  //???�任?��?��???�???
      String result;
      if (connectFinish != 0)
          result = Long.toString(connectFinish - connectStart) + "ms";
      else if (failure != null)
          result = failure.toString();
      else
          result = "Timed out";
      System.out.println(address + " : " + result);
      shown = true;
  }
}

public class PingClient{
  private Selector selector;
  //�??��?��?��?��??交�??任�??
  private LinkedList targets=new LinkedList();
  //�??�已�?�???????�????��??任�??
  private LinkedList finishedTargets=new LinkedList();

  public PingClient()throws IOException{
    selector=Selector.open();
    Connector connector=new Connector();
    Printer printer=new Printer();
    connector.start();
    printer.start();
    receiveTarget();
  }

  public static void main(String args[])throws IOException{
    new PingClient();
  }
  public void addTarget(Target target) {
    //??targets????�????��?�?任�??
     SocketChannel socketChannel = null;
      try {
          socketChannel = SocketChannel.open();
          socketChannel.configureBlocking(false);
          socketChannel.connect(target.address);

          target.channel = socketChannel;
          target.connectStart = System.currentTimeMillis();

           synchronized (targets) {
             targets.add(target);
           }
           selector.wakeup();
       } catch (Exception x) {
          if (socketChannel != null) {
              try {
                  socketChannel.close();
              } catch (IOException xx) { }
          }
          target.failure = x;
          addFinishedTarget(target);
      }
  }

  public void addFinishedTarget(Target target) {
      //??finishedTargets????�????��?�?任�??
      synchronized (finishedTargets) {
      finishedTargets.notify();
      finishedTargets.add(target);
     }
  }

  public void printFinishedTargets() {
    //????finisedTargets????�???任�??
     try {
        for (;;) {
            Target target = null;
            synchronized (finishedTargets) {
                while (finishedTargets.size() == 0)
                    finishedTargets.wait();
                target = (Target)finishedTargets.removeFirst();
            }
            target.show();
        }
    } catch (InterruptedException x) {
        return;
    }
  }

  public void registerTargets(){
    //????targets????�???任�?��???Selector注�??�??�就�?�?�?
    synchronized (targets) {
      while (targets.size() > 0) {
        Target target = (Target)targets.removeFirst();

        try {
          target.channel.register(selector, SelectionKey.OP_CONNECT, target);
        } catch (IOException x) {
              try{target.channel.close();}catch(IOException e){e.printStackTrace();}
              target.failure = x;
              addFinishedTarget(target);
        }
      }
    }
  }

  public void processSelectedKeys() throws IOException {
    //�???�??�就�?�?�?
    for (Iterator it = selector.selectedKeys().iterator(); it.hasNext();) {
      SelectionKey selectionKey = (SelectionKey)it.next();
      it.remove();

      Target target = (Target)selectionKey.attachment();
      SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

      try {
          if (socketChannel.finishConnect()) {
              selectionKey.cancel();
              target.connectFinish = System.currentTimeMillis();
              socketChannel.close();
              addFinishedTarget(target);
          }
      } catch (IOException x) {
          socketChannel.close();
          target.failure = x;
          addFinishedTarget(target);
      }
    }
  }

  public void receiveTarget(){
    //?��?��?��?��??��???��??�???targets????�????�任??
    try{
      BufferedReader localReader=new BufferedReader(new InputStreamReader(System.in));
      String msg=null;
      while((msg=localReader.readLine())!=null){
        if(!msg.equals("bye")){
          Target target=new Target(msg);
          addTarget(target);
        }else{
          shutdown=true;
          selector.wakeup();
          break;
        }
      }
    }catch(IOException e){
       e.printStackTrace();
    }
  }

  boolean shutdown=false;

  public class Printer extends Thread{
    public Printer(){
        setDaemon(true);
    }
    public void run(){
        printFinishedTargets();
    }
  }

  public class Connector extends Thread{
    public void run(){
        while (!shutdown) {
            try {
                registerTargets();
                if (selector.select() > 0) {
                    processSelectedKeys();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
       }
       try{
           selector.close();
       }catch(IOException e){e.printStackTrace();}
    }
  }
}


/****************************************************
 * �???�?�?????                                     *
 * ?��?�?<<Java�?�?�?�?精解>>                       *
 * ????????�???�?www.javathinker.org                *
 ***************************************************/
