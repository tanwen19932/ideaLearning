package tw.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * @author TW
 * @date TW on 2016/9/15.
 */
public class NIOFileUtil {
    boolean EOF = false;
    FileChannel fc = null;

    //一次读取文件，读取的字节缓存数
    ByteBuffer fbb = ByteBuffer.allocate(1024 * 5);
    //每行缓存的字节   根据你的实际需求
    ByteBuffer bb = ByteBuffer.allocate(500);

    public NIOFileUtil(String fileName)
            throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel fc = aFile.getChannel();
        fc.read(fbb);
        fbb.flip();
    }

    public boolean hasNext()
            throws IOException {

        if (EOF) return false;
        if (fbb.position() == fbb.limit()) {//判断当前位置是否到了缓冲区的限制
            if (readByte() == 0) return false;
        }
        while (true) {
            if (fbb.position() == fbb.limit()) {
                if (readByte() == 0) break;
            }
            byte a = fbb.get();
            if (a == 13) {
                if (fbb.position() == fbb.limit()) {
                    if (readByte() == 0) break;
                }
                return true;
            } else {
                if (bb.position() < bb.limit()) {
                    bb.put(a);
                } else {
                    if (readByte() == 0) break;
                }
            }
        }
        return true;
    }

    private int readByte()
            throws IOException {
        //使缓冲区做好了重新读取已包含的数据的准备：它使限制保持不变，并将位置设置为零。
        fbb.rewind();
        //使缓冲区做好了新序列信道读取或相对 get 操作的准备：它将限制设置为当前位置，然后将该位置设置为零。
        fbb.clear();
        if (this.fc.read(fbb) == -1) {
            EOF = true;
            return 0;
        } else {
            fbb.flip();
            return fbb.position();
        }
    }

    public byte[] next() {
        bb.flip();

        //此处很重要，返回byte数组方便，行被分割的情况下合并，否则如果正好达到缓冲区的限制时，一个中文汉字被拆了两个字节，就会显示不正常
        byte tm[] = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
        bb.clear();
        return tm;
    }
}
