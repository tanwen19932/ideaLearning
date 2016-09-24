/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package tw.utils;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author hu
 */
public class FileUtil {
    static Boolean lock = Boolean.FALSE;
    static ConcurrentSkipListSet<String> usingFiles = new ConcurrentSkipListSet();
    //static ArrayBlockingQueue<String> savingStrs = new ArrayBlockingQueue<String>();
    public static String getFileStr(String filePath)
            throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader bf = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = bf.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bf.close();
        return sb.toString();
    }

    public static void mkFolder(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static File mkFile(String fileName) {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static void deleteDir(File dir) {
        File[] filelist = dir.listFiles();
        for (File file : filelist) {
            if (file.isFile()) {
                file.delete();
            } else {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    public static void copy(File origin, File newfile)
            throws FileNotFoundException, IOException {
        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }
        FileInputStream fis = new FileInputStream(origin);
        FileOutputStream fos = new FileOutputStream(newfile);
        byte[] buf = new byte[2048];
        int read;
        while ((read = fis.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }
        fis.close();
        fos.close();
    }

    public static void writeFile(String fileName, String contentStr, String charset)
            throws FileNotFoundException, IOException {
        byte[] content = contentStr.getBytes(charset);
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(content);
        fos.close();
    }

    public static void writeFile(File file, String contentStr, String charset)
            throws FileNotFoundException, IOException {
        byte[] content = contentStr.getBytes(charset);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    public static void writeFileWithParent(String fileName, String contentStr, String charset)
            throws FileNotFoundException, IOException {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        byte[] content = contentStr.getBytes(charset);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }


    public static void writeFile(String fileName, byte[] content)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(content);
        fos.close();
    }

    public static void writeFile(File file, byte[] content)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    public static void writeFileWithParent(String fileName, byte[] content)
            throws FileNotFoundException, IOException {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    public static void writeFileWithParent(File file, byte[] content)
            throws FileNotFoundException, IOException {

        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    public static byte[] readFile(File file)
            throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[2048];
        int read;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((read = fis.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }

        fis.close();
        return bos.toByteArray();
    }

    public static byte[] readFile(String fileName)
            throws IOException {
        File file = new File(fileName);
        return readFile(file);
    }

    public synchronized static String readFile(File file, String charset)
            throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[2048];
        int read;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((read = fis.read(buf)) != -1) {
            bos.write(buf, 0, read);
        }

        fis.close();
        return new String(bos.toByteArray(), charset);
    }

    public synchronized static String readFile(String fileName, String charset)
            throws Exception {
        File file = new File(fileName);
        return readFile(file, charset);
    }

    public synchronized static void fileAppendMethod1(String file, String conent) {
        BufferedWriter out = null;
        try {
            File file1 = new File(file);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public synchronized static void fileAppendMethod2(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fileAppendStr(String fileName, String content)
            throws IOException {
        RandomAccessFile randomFile = null;
        fileName.replaceAll("\\\\", "/");
        if (fileName.charAt(fileName.length() - 1) == '/') {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        //Files.move(Paths.get(fileName), Paths.get("D://1"));

        // 打开一个随机访问文件流，按读写方式
        //FileChannel fc = randomFile.getChannel();
        //FileLock fl;
        //while (true) {
        //    try {
        //        fl = fc.tryLock();
        //        break;
        //    } catch (Exception e) {
        //        System.out.println("有其他线程正在操作该文件，当前线程休眠100毫秒");
        //        try {
        //            Thread.sleep(100);
        //        } catch (InterruptedException e1) {
        //            e1.printStackTrace();
        //        }
        //    }
        //}

        long fileLength = new File(fileName).length();
        if (fileLength > 20 * 1024 * 1024) {
            Path fromPath = Paths.get(fileName);
            Path toPath;
            int count = 1;
            File toFile = new File(fileName + "." + count);
            while (toFile.exists()) {
                count++;
                toFile = new File(fileName + "." + count);
            }
            toPath = Paths.get(toFile.getAbsolutePath());
            while (true) {
                try {

                    synchronized (lock) {
                        Files.move(fromPath, toPath);
                    }
                    //getLock(fileName);
                    //releaseLock(fileName);
                    System.out.println("++++++++move    " + fromPath + "    TO    " + toPath + "     成功+++++++++");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }
        synchronized (lock) {
            try {
                //getLock(fileName);
                randomFile = new RandomAccessFile(fileName, "rw");
                fileLength = randomFile.length();
                // 将写文件指针移到文件尾。
                if (fileLength != 0) {
                    randomFile.seek(fileLength - 1);
                    randomFile.write(("," + content.toString() + "\r\n]").getBytes("utf-8"));
                } else {
                    System.out.println("+++++++新建文件++++      " + fileName);
                    randomFile.seek(0);
                    randomFile.write(("[\r\n" + content.toString() + "]").getBytes("utf-8"));
                }
                // 文件长度，字节数

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (randomFile != null) {
                    try {
                        randomFile.close();
                        //releaseLock(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 追加文件：使用RandomAccessFile
     *
     * @param fileName 文件名
     * @param jo       追加的内容
     */

    public static void fileAppendJson(String fileName, JSONObject jo)
            throws IOException {
        fileAppendStr(fileName, jo.toString());
    }

    public static synchronized void saveUrlToFile(String destUrl, String fileName) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        byte[] buf = new byte[1024];
        int size = 0;
        // 建立链接
        try {
            File file = new File(fileName.substring(0, fileName.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(fileName);
            if (file.exists())
                return;
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            // 连接指定的资源
            httpUrl.connect();
            // 获取网络输入流
            bis = new BufferedInputStream(httpUrl.getInputStream());
            // 建立文件
            fos = new FileOutputStream(file);
            // 保存文件
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpUrl != null)
                httpUrl.disconnect();
        }
    }

    private static void getLock(String fileName) {
        while (true) {
            if (usingFiles.contains(fileName)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                usingFiles.add(fileName);
                return;
            }
        }
    }

    private static void releaseLock(String fileName) {
        while (true) {
            if (usingFiles.contains(fileName)) {
                usingFiles.remove(fileName);
                return;
            }
        }
    }

}
