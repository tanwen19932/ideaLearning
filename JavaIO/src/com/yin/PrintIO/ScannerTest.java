package com.yin.PrintIO;

import java.util.Scanner;

public class ScannerTest {

	public static void main(String[] args ) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输出一个整数：");
        int i = input.nextInt();
        System.out.println("你输入的整数是：" + i);
    }

}
