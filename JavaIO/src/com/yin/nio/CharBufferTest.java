package com.yin.nio;

import java.nio.CharBuffer;

public class CharBufferTest {

	public static void main(String[] args) {
		CharBuffer charBuffer = CharBuffer.allocate(8);
		printlnStr("capacity="+charBuffer.capacity());
		printlnStr("limit="+charBuffer.limit());
		printlnStr("postion="+charBuffer.position());
		charBuffer.put("a");
		charBuffer.put("b");
		charBuffer.put("c");
		printlnStr("==================");
		printlnStr("capacity="+charBuffer.capacity());
		printlnStr("limit="+charBuffer.limit());
		printlnStr("postion="+charBuffer.position());
		/*
		 * 调用flip方法之前获取数据，获取不到。
		 * 原因在于get方法，默认返回的是当前position所在位置的数据。
		 * flip方法之前，当前的position的位置是3  可以看到输出结果中position值。
		 * 而此时位置3的值为空，所以获取不到值
		 */
		printlnStr("第一个="+charBuffer.get());
		charBuffer.flip();
		printlnStr("==================");
		printlnStr("capacity="+charBuffer.capacity());
		printlnStr("limit="+charBuffer.limit());
		printlnStr("postion="+charBuffer.position());
		printlnStr("==================");
		printlnStr("第一个="+charBuffer.get());
		charBuffer.clear();
		printlnStr("capacity="+charBuffer.capacity());
		printlnStr("limit="+charBuffer.limit());
		printlnStr("postion="+charBuffer.position());

	}
	
	private static void printlnStr(String str){
		System.out.println(str);
	}

}
