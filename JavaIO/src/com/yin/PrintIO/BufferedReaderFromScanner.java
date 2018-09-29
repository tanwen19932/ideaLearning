package com.yin.PrintIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BufferedReaderFromScanner {
	public static void main(String[] args) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("请输入文本：");
        try {
            String str = bufferedReader.readLine();
            System.out.println("你输入的是：" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //关闭流，不耐烦的就直接抛
        	bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
