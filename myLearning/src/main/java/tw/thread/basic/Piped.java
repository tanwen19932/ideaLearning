package tw.thread.basic;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Scanner;

/**
 * @author TW
 * @date TW on 2018/3/15.
 */
public class Piped {
    public static void main(String[] args){
        PipedWriter pipedWriter = new PipedWriter();
        PipedReader pipedReader = new PipedReader();
        try {
            pipedWriter.connect(pipedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread writer = new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNext()){
                try {
                    pipedWriter.write(scanner.nextLine());
                } catch (IOException e) {
                }
            }
        });
        Thread reader = new Thread(()->{
            try {
                int receive = -1;
                while((receive=pipedReader.read())!=-1){
                    System.out.print((char)receive);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reader.start();
        writer.start();
    }
}
