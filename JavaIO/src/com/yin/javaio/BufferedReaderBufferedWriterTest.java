package com.yin.javaio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedReaderBufferedWriterTest {

	/**
	 * 利用缓冲字符流实现文本文件的复制功能
	 * 从demo.txt文件的内容复制到test.txt文件中
	 * @param args
	 */
	public static void main( String[] args){
		FileReader fileReader = null;
		FileWriter fileWriter = null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		String path1 = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
		String path2 = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\test.txt";
		try {
			fileReader = new FileReader(path1);
			fileWriter = new FileWriter(path2);
			bufferedReader = new BufferedReader(fileReader);
			bufferedWriter = new BufferedWriter(fileWriter);
			String temp;
			while ((temp = bufferedReader.readLine()) != null) {
			//这两个方法都是可以的
				bufferedWriter.write(temp);
//				bufferedWriter.append(temp);
				bufferedWriter.flush();//调用该方法写入磁盘
			}
		} catch (Exception e) {
			
		}finally{
			//此处不再需要捕捉FileReader和FileWriter对象的异常
            //关闭缓冲区就是关闭缓冲区中的流对象
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
