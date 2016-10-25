package com.yin.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class AttributeViewTest {

	public static void main(String[] args) {
		Path testPath = Paths.get(".\\src\\com\\yin\\nio\\AttributeViewTest.java");
		BasicFileAttributeView basicView = Files.getFileAttributeView(testPath,
				BasicFileAttributeView.class);
		try {
			BasicFileAttributes basicFileAttributes = basicView.readAttributes();
			PrintStr("创建时间："+new Date(basicFileAttributes.creationTime().toMillis()).toLocaleString());
			PrintStr("最后访问时间："+new Date(basicFileAttributes.lastAccessTime().toMillis()).toLocaleString());
			PrintStr("最后修改时间："+new Date(basicFileAttributes.lastModifiedTime().toMillis()).toLocaleString());
			PrintStr("文件大小："+basicFileAttributes.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static void PrintStr(String str){
		System.out.println(str);
	}

}
