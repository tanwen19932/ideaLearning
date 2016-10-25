package com.yin.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockTest {

	public static void main(String[] args) {
		FileChannel fileChannel = null;
		try {
			fileChannel = new FileOutputStream("a.txt").getChannel();
			FileLock fileLock = fileChannel.tryLock();
			Thread.sleep(10000);
			fileLock.release();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (fileChannel != null) {
					fileChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
