package com.yin.PrintIO;

import java.io.IOException;
import java.io.OutputStream;

public class SystemOutTest {
	public static void main(String[] args) {
        //别忘了，OutputStream是所有字节写入流的父类
        OutputStream out = System.out;
        try {
            //写入数据，只能是数组，所以用getBytes()方法
            out.write("Hello，son of bitch！\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
