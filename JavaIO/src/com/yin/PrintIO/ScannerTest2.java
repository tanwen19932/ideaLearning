package com.yin.PrintIO;

import java.util.Scanner;

public class ScannerTest2 {

	public static void main(String[] args ) {
        //这里的\r\n是换行符，Linux下其实只用\n即可
        Scanner input = new Scanner("hello\r\nworld\r\n");
        //循环读取，hasNext()方法和集合框架里面的一样使
        while(input.hasNext()) {
            //每次读取一行，别的读取方法见API，比较简单
            String s = input.nextLine();
            System.out.println(s);
        }  
    }

}
