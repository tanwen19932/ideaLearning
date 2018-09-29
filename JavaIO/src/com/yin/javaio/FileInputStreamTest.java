package com.yin.javaio;

import java.io.FileInputStream;

public class FileInputStreamTest {

	/**FileInputStream字节流的读取操作
	 * @param args
	 */
	public static void main(String[] args) {
		FileInputStream fileInputStream = null;
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
		
		try {
			fileInputStream = new FileInputStream(path);
			PrintStr(String.valueOf(fileInputStream.read()));//一次读取一个字节，读到末尾返回-1表示结束
			//结果输出为：239  表示读取第一个字符的第一个字节  字节转化为整数表示
			
			byte[] b = new byte[5];
			while (fileInputStream.read(b)>0) {
				fileInputStream.read(b);//读取数据存储在b字节数组中
				PrintStr(new String(b));
			}
			
		} catch (Exception e) {
			
		}finally{
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}
	
	private static void PrintStr(String str){
		System.out.print(str);
	}
}
