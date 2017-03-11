package java.nio;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created with .
 * Date: 14-5-27
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
public class HttpTest {


    private static byte[] request = null;

    static {
        StringBuffer temp = new StringBuffer();
        temp.append("GET http://195.23.32.196/wiseapibulk/rest.svc/socialmedia/XML/4c7e54f6-57a5-43d6-9528-b6ad98d1ed52?index=10 HTTP/1.1\r\n");
        temp.append("Host: 195.23.32.196\r\n");
        temp.append("Connection: keep-alive\r\n");
        //        temp.append("Cache-Control: max-age=0\r\n");
        temp.append("User-Agent: Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.47 Safari/536.11\r\n");
        temp.append("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n");
        //        temp.append("Accept-Encoding: gzip,deflate,sdch\r\n");
        temp.append("Accept-Language: zh-CN,zh;q=0.8\r\n");
        //        temp.append("Accept-Charset: GBK,utf-8;q=0.7,*;q=0.3\r\n");
        temp.append("\r\n");
        request = temp.toString().getBytes();
    }

    public static void sendHttpRequest()
            throws Exception {
        try {

            final SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("195.23.32.196", 80));
            final Charset charset = Charset.forName("utf8");// 创建GBK字符集
            socketChannel.configureBlocking(false);//配置通道使用非阻塞模式
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while (!socketChannel.finishConnect()) {
                Thread.sleep(10);
            }
            socketChannel.write(ByteBuffer.wrap(request));

            int read = 0;
            ByteBuffer buffer = ByteBuffer.allocate(1024);// 创建1024字节的缓冲
            StringBuffer content = new StringBuffer();
            boolean isFile = false;
            while ((read = socketChannel.read(buffer)) != -1) {
                if (read == 0) {
                    continue;
                }
                buffer.flip();// flip方法在读缓冲区字节操作之前调用。
                //                charset.decode(buffer);
                //                byteArrayOutputStream.write( buffer.array(), 0, buffer.remaining());
                content.append(charset.decode(buffer).toString());
                //                if(line.length()==1)
                //                	isFile = true;
                //               System.out.println( line.indexOf("\r\n") +"  "+ line.indexOf("\r\n", 2) + line.indexOf("\r\n", 3));
                //                if( )
                //                if(isFile)

                // 使用Charset.decode方法将字节转换为字符串
                buffer.clear();// 清空缓冲
            }
            System.out.println(content.toString());
            //            GZIPInputStream gzipOutputStream = new GZIPInputStream( new ByteArrayInputStream(byteArrayOutputStream.toByteArray()) );
            //            BufferedReader bin = new BufferedReader(new InputStreamReader(gzipOutputStream, "utf-8"));
            //            String line ;
            //            while( (line = bin.readLine()) != null){
            //            	System.out.println(line);
            //            }
            System.out.println("----------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
            throws Exception {
        sendHttpRequest();
    }


}
