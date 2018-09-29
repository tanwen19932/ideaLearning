package com.yin.javaio;

import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * 二进制文件的复制
 * @author Administrator
 * 2015年3月15日 14:21:20
 */
public class BinaryFileToCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\G.E.M.邓紫棋-泡沫.mp3";
		String pathCopy = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\邓紫棋-泡沫.mp3";
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(path);
			fileOutputStream = new FileOutputStream(pathCopy);
			byte[] by = new byte[1024];
			
			while (fileInputStream.read(by)>0) {
				fileOutputStream.write(by);
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
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

}
