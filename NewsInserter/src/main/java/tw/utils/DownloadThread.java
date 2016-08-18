package tw.utils;

import java.net.*;
import java.io.*;

public class DownloadThread extends Thread {
    private String url = null;// 待下载的文件
    private String file = null;// 本地存储路径
    private long offset = 0;// 偏移量
    private long length = 0;// 分配给本线程的下载字节数

    public DownloadThread(String url, String file, long offset, long length) {
        this.url = url;
        this.file = file;
        this.offset = offset;
        this.length = length;

    }

    public void run() {
        long offsetBefore = offset;
        System.out.println(Thread.currentThread().getName() + ".BEGIN!! From " + offset + " Length" + length);
        int retry = 0;
        while (true) {
            try {
                URL url = new URL(this.url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                HttpUtil.config(httpURLConnection);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("RANGE",
                        "bytes=" + this.offset + "-" + (this.offset + this.length - 1));
                BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
                byte[] buff = new byte[1024];
                int bytesRead;
                int insertSize = 0;
                while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {

                    if ((length - insertSize) <= bytesRead) {
                        this.writeFile(file, offset, buff, (int) (length - insertSize));
                        this.offset += (int) (length - insertSize);
                        break;
                    } else
                        this.writeFile(file, offset, buff, bytesRead);
                    this.offset = this.offset + bytesRead;
                    insertSize += bytesRead;
                }
                System.out.println(Thread.currentThread().getName() + ".\tDONE!! From " + offsetBefore + "\tTo" + offset
                        + "\t length" + (offset - offsetBefore));
                System.out.println(Thread.getAllStackTraces().size());
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR retry " + retry++);
                if (retry == 20) {
                    System.err.println(" retry too much times " + retry++);
                    break;
                }
            }
        }
    }

    // 将字节数组以随机存取方式写入文件
    // fileName是被写入的文件
    // offset代表写入文件的位置偏移量
    // bytes是待写入的字节数组
    // realLength是实际需要写入的字节数（realLength<=bytes.length）
    private void writeFile(String fileName, long offset, byte[] bytes, int realLength) throws IOException {
        File newFile = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
        raf.seek(offset);
        raf.write(bytes, 0, realLength);
        raf.close();
    }
}