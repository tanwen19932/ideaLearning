package com.yin.javaio;

import java.io.FileOutputStream;

public class FileOutputStreamTest {

	/**字节流写入操作
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
			
//			fileOutputStream.write(int b)
//			fileOutputStream.write(byte[] b)
//			fileOutputStream.write(byte[] b, off, len)
			//上面三个方法是利用fileOutputStream进行写入的三个重载的方法
			
			//测试第一个方法
//			fileOutputStream.write(65);//结果是下面结果图中的第一幅图
			
			//测试第二个方法
//			fileOutputStream.write(("我的世界里没有一丝剩下的只是回忆，" +
//					"\r\n你存在我深深的脑海里!我的梦里，我的心里！").getBytes());//结果是下面结果图中的第二幅图
			
			//测试第三个方法
			byte[] by = ("我的世界里没有一丝剩下的只是回忆，" +
					"\r\n你存在我深深的脑海里!我的梦里，我的心里！").getBytes();
			fileOutputStream.write(by, 0, by.length);//结果和上一个方法相同
			
		} catch (Exception e) {
			
		}finally{
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e2) {
					
				}
			}
		}
	}

}
