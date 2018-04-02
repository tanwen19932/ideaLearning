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
public class ServerDemo {
    private static ByteBuffer buffer=ByteBuffer.allocate(1024);//1K大小的缓冲池  
    public static void main(String[] args) {  
        try {  
            Selector selector=Selector.open();  
            ServerSocketChannel serverChannel=ServerSocketChannel.open();  
            ServerSocket server=serverChannel.socket();  
            server.bind(new InetSocketAddress(12345));  
            serverChannel.configureBlocking(false);  
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);  
              
            while(true){  
                // This may block for a long time. Upon returning, the   
                // selected set contains keys of the ready channels.  
                int n=selector.select();  
                if (n == 0) {    // nothing to do  
                    continue;  
                }  
                Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();  
                while(iterator.hasNext()){  
                    SelectionKey key=iterator.next();  
                    // Is a new connection coming in?  
                    if(key.isAcceptable()){  
                        //此时的key是，ServerSocketChannel注册的。要获取客户端通道  
                        ServerSocketChannel serverSocketChannel=(ServerSocketChannel)key.channel();  
                        SocketChannel client=serverSocketChannel.accept();  
                        client.configureBlocking(false);//将客户端通道也设置成非阻塞  
                        client.register(selector, SelectionKey.OP_READ);  
                        //想客户端发送消息  
                        buffer.clear();   
                        buffer.put("Hi there!\r\n".getBytes());   
                        buffer.flip();   
                        client.write(buffer);  
                    }  
                    if(key.isReadable()){  
                        SocketChannel socketChannel = (SocketChannel) key.channel();   
                        int count;   
                        buffer.clear(); // Empty buffer   
                        // Loop while data is available; channel is nonblocking   
                        while ((count = socketChannel.read(buffer)) > 0) {   
                            buffer.flip();   
                            // Make buffer readable   
                            // Send the data; don't assume it goes all at once   
                            while (buffer.hasRemaining()) {   
                                socketChannel.write(buffer);   
                            }  
                            // WARNING: the above loop is evil. Because   
                            // it's writing back to the same nonblocking   
                            // channel it read the data from, this code can   
                            // potentially spin in a busy loop. In real life   
                            // you'd do something more useful than this.   
                            buffer.clear();   
                            // Empty buffer   
                        }  
                        if (count < 0) {  
                            // Close channel on EOF, invalidates the key   
                            socketChannel.close();   
                        }  
                    }  
                    iterator.remove();  
                }  
            }  
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  