package com.yin.javaio;

import java.io.FileReader;
import java.io.IOException;

public class FileReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
//		Method1(path);
		Method2(path);
	}
	
	//读取单个字符  一个一个字符的读取
	private static void Method2(String path) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(path);
			int temp1 = fileReader.read();
			PrintMeth((char)temp1+"");
			int temp2 = fileReader.read();
			PrintMeth((char)temp2+"");
			int temp3 = fileReader.read();
			PrintMeth((char)temp3+"");
		} catch (Exception e) {
			
		}finally{
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//利用缓冲进行读取   最常见的方式
	private static void Method1(String path) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(path);
			int i = 0;
			char[] buf = new char[5];
			while ((i= fileReader.read(buf))>0) {
				PrintMeth(new String(buf));
			}
		} catch (Exception e) {
			
		}finally{
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void PrintMeth(String str){
		System.out.println(str);
	}
}
