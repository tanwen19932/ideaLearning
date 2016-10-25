package com.yin.javaio;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterTest {
	/**
	 * 字符流写入操作
	 * @param args
	 */
	public static void main(String[] args) {
		String filestr = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter(filestr);
			fileWriter.write("long Yin is a good good boy! Handsome! 正在向牛人迈进！^_^");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
