package com.yin.PrintIO;

import java.io.IOException;
import java.io.InputStream;

public class SystemInTest {

	public static void main(String[] args) {
        //别忘了InputStream是所有字节输入流的父类
        InputStream in = System.in;
        System.out.print("请输入文字： ");
        byte[] buf = new byte[1024];
        int len = 0;
        try {
            //将输入的数据保证到数组中，len记录输入的长度
            len = in.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //用字符串的方式打印数组中的数据
        System.out.println("你的输入是： " + new String(buf,0,len));
        
    }

}
