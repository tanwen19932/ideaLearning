package tw.utils;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;

public class DownloadManager {
    static final long unitSize = 100 * 1024;// 分配给每个下载线程的字节数

    /**
     * @param args 2012-10-8 void
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.doDownload(
                "http://195.23.32.196/wiseapibulk/rest.svc/bulk/xml/13b860b0-7c69-47fa-8a9f-482d638c32e0?since=2016-06-17T09:30:02Z&interval=1",
                "E:/3.txt");
    }

    public static void doDownload(String remoteFileUrl, String localFileName) throws IOException {
        long fileSize = getRemoteFileSize(remoteFileUrl);
        createFile(localFileName, fileSize);
        long threadCount = fileSize / unitSize;
        System.out.println("共启动线程" + (fileSize % unitSize == 0 ? threadCount : threadCount + 1) + "个");
        long offset = 0;
        if (fileSize <= unitSize) {// 如果远程文件尺寸小于等于unitSize
            DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset, fileSize);
            downloadThread.start();
        } else {// 如果远程文件尺寸大于unitsize
            for (int i = 1; i <= threadCount; i++) {
                DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset, unitSize);
                downloadThread.start();
                offset = offset + unitSize;
            }
            if (fileSize % unitSize != 0)// 如果不能整除，则需要再创建一个线程下载剩余字节
            {
                DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localFileName, offset,
                        fileSize - unitSize * threadCount);
                downloadThread.start();
            }
        }
    }

    // 获取远程文件的大小
    private static long getRemoteFileSize(String remoteFileUrl) throws IOException {

        long result = 0;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
            HttpUtil.config(httpURLConnection);
            httpURLConnection.setRequestMethod("GET");

            result = httpURLConnection.getContentLength();
            System.out.println(" ResponseCode " + httpURLConnection.getResponseCode() + "文件大小 ：" + result);

        } catch (Exception e) {

        }
        return result;
    }

    // 创建指定大小的文件
    private static void createFile(String fileName, long fileSize) throws IOException {
        File newFile = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
        raf.setLength(fileSize);
        raf.close();
    }
}
