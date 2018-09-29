package com.yin.serialize;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
 
public class DataReadTest {
    public static void main(String[] args) {
        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demoTest.txt";
         
        DataInputStream d = null;
            try {
                d = new DataInputStream(new FileInputStream(path));
                //按存储顺序读取基本数据类型
                System.out.println(d.readInt());
                System.out.println(d.readBoolean());
                System.out.println(d.readDouble());
                System.out.println(d.readChar());
                 
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
