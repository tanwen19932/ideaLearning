package com.yin.PrintIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintWriterAppendFile {

	public static void main(String[] args) {
        String path = "demoTest.txt";
         
        //创建文件对象
        File file = new File(path);
        PrintWriter p = null;
        try {
            //利用FileWriter方式构建PrintWriter对象，实现追加
            p = new PrintWriter(new FileWriter(file,true));
            p.println("wqnmglb 这一句就是追加的 看到没");
             
            p.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //我们来小心翼翼的关闭流，好吧^_^
            p.close();
        }
    }

}
