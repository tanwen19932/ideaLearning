import java.net.*;
import java.io.*;

public class ConnectTester {
    public static void main(String args[]) {
        String mHost = "localhost";
        int mPort = 25;
        if (args.length > 1) {
            mHost = args[0];
            mPort = Integer.parseInt(args[1]);
        }
        new ConnectTester().connect(mHost, mPort);
    }

    public void connect(String host, int port) {
        SocketAddress remoteAddr = new InetSocketAddress(host, port);
        Socket socket = null;
        String result = "";
        try {
            long begin = System.currentTimeMillis();
            socket = new Socket();
            //leo add if else to test throw BindException
//            socket.bind(new InetSocketAddress(InetAddress.getByName("222.34.5.7"), 5678));
            socket.connect(remoteAddr, 1000); // time out 1000ms    

            long end = System.currentTimeMillis();
            result = (end - begin) + "ms"; //calculate connection time
        } catch (BindException e) {
            result = "Local address and port can't be binded";
        } catch (UnknownHostException e) {
            result = "Unknown Host";
        } catch (ConnectException e) {
            result = "Connection Refused";
        } catch (SocketTimeoutException e) {
            result = "TimeOut";
        } catch (IOException e) {
            result = "failure";
        } finally {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(remoteAddr + " : " + result);
    }
}
