package com.yin.serialize;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
 
public class DataStreamTest {
    public static void main(String[] args) {
        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demoTest.txt";
         
        DataOutputStream d = null;
            try {
                //此处需要传入一个OutputStream类的对象
                d = new DataOutputStream(new FileOutputStream(path));
                //开始写入基本数据类型
                d.writeInt(12);
                d.writeBoolean(true);
                d.writeDouble(12.2223);
                d.writeChar(97);
                //刷新流
                d.flush();
     
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    d.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
