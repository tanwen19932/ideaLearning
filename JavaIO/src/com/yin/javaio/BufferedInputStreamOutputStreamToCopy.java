package com.yin.javaio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BufferedInputStreamOutputStreamToCopy {

	/**
	 * 利用字节流缓冲流进行二进制文件的复制
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\G.E.M.邓紫棋-泡沫.mp3";
		String pathCopy = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\邓紫棋-泡沫.mp3";
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			fileInputStream = new FileInputStream(path);
			fileOutputStream = new FileOutputStream(pathCopy);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			byte[] by = new byte[1024];
			while (bufferedInputStream.read(by)>0) {
				bufferedOutputStream.write(by);
				bufferedOutputStream.flush();
			}
		} catch (Exception e) {
			
		}finally{
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (Exception e2) {
					
				}
			}
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (Exception e2) {
					
				}
			}
		}

	}

}
