package com.yin.nio;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class FileChannelTest {
	public static void main(String[] args) {
		String pathin = ".\\src\\com\\yin\\google.guava.nio\\FileChannelTest.java";
		String pathout = "a.txt";
		File filein = new File(pathin);
		File fileout = new File(pathout);
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(filein);
			fileOutputStream = new FileOutputStream(fileout);
			FileChannel fileChannelIn = fileInputStream.getChannel();
			FileChannel fileChannelOut = fileOutputStream.getChannel();
			MappedByteBuffer mappedByteBuffer = fileChannelIn.map(MapMode.READ_ONLY, 0, filein.length());
			fileChannelOut.write(mappedByteBuffer);
			mappedByteBuffer.clear();
			System.out.println(mappedByteBuffer.toString());
			//代码中如果含有中文，使用下面的解析器进行重新解析，就不会出现乱码的情况
			Charset charset = Charset.forName("UTF-8");
			CharsetDecoder charsetDecoder = charset.newDecoder();
			CharBuffer charBuffer = charsetDecoder.decode(mappedByteBuffer);
			System.out.println(charBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
