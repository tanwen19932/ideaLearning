package nio_tw.chatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author TW
 * @date TW on 2017/3/28.
 */
public class Client {
    private String name = null;
    SocketChannel client = null;
    Selector readSelector = null;
    Selector writeSelector = null;
    SelectionKey writer = null;
    SelectionKey reader = null;

    public Client(String name) {
        this.name = name;
        try {
            client = SocketChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress("127.0.0.1", 12345));
            readSelector = Selector.open();
            writeSelector = Selector.open();
            //注册监听Connect
            writer = client.register(readSelector, SelectionKey.OP_CONNECT, name);
            reader = client.register(writeSelector, SelectionKey.OP_CONNECT, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runWrite();
            }
        }, "ClientReader");
        readThread.start();
        Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runReader();
            }
        }, "ClientWriter");
        writeThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

    public void runWrite() {
        try {
            while (true) {
                int n = readSelector.select();
                if (n == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable() && client.finishConnect()) {
                        System.out.println("[client]:连接成功");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put("客户端名称:".getBytes());
                        buffer.put(name.getBytes());
                        buffer.flip();
                        client.write(buffer);
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                    if (key.isWritable()) {
                        ReadableByteChannel in = Channels.newChannel(System.in);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        if (in.read(buffer) > 0) {
                            buffer.flip();
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.write(buffer);
                            buffer.clear();
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void runReader() {
        try {
            while (true) {
                int n = readSelector.select();
                if (n == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = readSelector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        System.out.println("[client]:开始接受");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        WritableByteChannel out = Channels.newChannel(System.out);
                        while (client.read(buffer) > 0) {
                            buffer.flip();
                            out.write(buffer);
                        }
                        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Client client = new Client("" + System.currentTimeMillis());
        client.start();
    }
}
