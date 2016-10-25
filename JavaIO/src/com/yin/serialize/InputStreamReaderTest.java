package com.yin.serialize;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;

public class InputStreamReaderTest {

	public static void main(String[] args) {
		method1();
	}

	private static void method1() {
		try(
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);)
		{
			String buffer;
			if ((buffer = bufferedReader.readLine()) != null) {
				System.out.println("输入内容为："+new String(buffer.getBytes(Charset.forName("UTF-8"))));
			}
		}catch (Exception e) {
			
		}
	}

}
