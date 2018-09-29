package nio_tw.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author TW
 * @date TW on 2017/3/28.
 */
public class Server {
    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        Selector selector = null;
        Set<SocketChannel> clients=new HashSet<SocketChannel>();
        try {
            ssc = ServerSocketChannel.open();
            ServerSocket serverSocket=ssc.socket();
            serverSocket.bind(new InetSocketAddress(12345));
            ssc.configureBlocking(false);
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                selector.select();
                System.out.println("======select========");
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        System.out.println("接受新的连接");
                        //新的连接即 SocketChanel 注册到 Selector 之中
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client=server.accept();//接受客户端的请求
                        client.configureBlocking(false);//客户端 也设置为 非阻塞
                        clients.add(client);
                        client.register(key.selector(), SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        SocketChannel client=(SocketChannel) key.channel();
                        if(!client.isConnected()){
                            clients.remove(client);
                            client.socket().close();
                            client.close();
                        }
                        ByteBuffer buffer=ByteBuffer.allocate(1024);
                        StringBuffer sb = new StringBuffer();
                        int size = client.read(buffer);
                        while(size>0){
                            buffer.flip();
                            sb.append(new String(buffer.array(),0,size));
                            buffer.clear();
                            size = client.read(buffer);
                        }
                        Iterator<SocketChannel> it=clients.iterator();
                        while(it.hasNext()){
                            SocketChannel other=it.next();
                            if(other != client){
                                buffer.flip();
                                other.write(ByteBuffer.wrap(sb.toString().getBytes()));
                            }
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
