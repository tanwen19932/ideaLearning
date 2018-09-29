import java.net.*;
import java.io.*;

public class PortScanner {
    public static void main(String args[]) {
        String mHost = "localhost";
        if (args.length > 0)
            mHost = args[0];
        new PortScanner().scan(mHost);
    }

    public void scan(String host) {
        Socket socket = null;
        for (int port = 1; port < 1024; port++) {
            try {
                socket = new Socket(host, port);
                System.out.println("There is a server on port " + port);
            } catch (IOException e) {
                System.out.println("Can't connect to port " + port);
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
