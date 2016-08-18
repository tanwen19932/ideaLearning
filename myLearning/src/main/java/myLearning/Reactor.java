package myLearning;//package myLearning;
//
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.Set;
//
//public class Reactor implements Runnable{
//  final Selector selector;
//  final ServerSocketChannel serverSocket;
//  Reactor(int port) throws IOException {
//    selector = Selector.open();
//    serverSocket = ServerSocketChannel.open();
//    InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(),port);
//    serverSocket.socket().bind(address);
//
//    serverSocket.configureBlocking(false);
//    //向selector注册该channel
//     SelectionKey sk =serverSocket.register(selector,SelectionKey.OP_ACCEPT);
//
//    logger.debug("-->Start serverSocket.register!");
//
//    //利用sk的attache功能绑定Acceptor 如果有事情，触发Acceptor
//    sk.attach(new Acceptor());
//    logger.debug("-->attach(new Acceptor()!");
//  }
//
//
//  public void run() { // normally in a new Thread
//    try {
//    while (!Thread.interrupted())
//    {
//      selector.select();
//      Set selected = selector.selectedKeys();
//      Iterator it = selected.iterator();
//      //Selector如果发现channel有OP_ACCEPT或READ事件发生，下列遍历就会进行。
//      while (it.hasNext())
//        //来一个事件 第一次触发一个accepter线程
//        //以后触发SocketReadHandler
//        dispatch((SelectionKey)(it.next()));
//        selected.clear();
//      }
//    }catch (IOException ex) {
//        logger.debug("reactor stop!"+ex);
//    }
//  }
//
//  //运行Acceptor或SocketReadHandler
//  void dispatch(SelectionKey k) {
//    Runnable r = (Runnable)(k.attachment());
//    if (r != null){
//      // r.run();
//
//    }
//  }
//
//  class Acceptor implements Runnable { // inner
//    public void run() {
//    try {
//      logger.debug("-->ready for accept!");
//      SocketChannel c = serverSocket.accept();
//      if (c != null)
//        //调用Handler来处理channel
//        new SocketReadHandler(selector, c);
//      }
//    catch(IOException ex) {
//      logger.debug("accept stop!"+ex);
//    }
//    }
//  }
//}