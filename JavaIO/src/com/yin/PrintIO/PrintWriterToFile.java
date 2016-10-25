package com.yin.PrintIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PrintWriterToFile {

	public static void main(String[] args) {
        String path = "demoTest.txt";
         
        //创建文件对象
        File file = new File(path);
         
        PrintWriter p = null;
        try {
            //此处构造函数还可以传其他对象，具体参考API文档
            p = new PrintWriter(file);
             
            //向文件写入一行，此外还有print()和printf()方法
            p.println("如果有一天我回到从前");
            p.println("回到最原始的我");
            p.println("你是否会觉得我不错");
             
            //刷新流
            p.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            p.close();
        }  
    }

}
