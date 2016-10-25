package com.yin.nio;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathsTest {

	public static void main(String[] args) {
		Path path = Paths.get(".");
		PrintStr("path里面包含的路径数="+path.getNameCount());
		PrintStr("获取根路径="+path.getRoot());
		Path absolutePath = path.toAbsolutePath();
		PrintStr("获取绝对路径="+absolutePath);
		PrintStr("absolutePath的根路径="+absolutePath.getRoot());
		PrintStr("absolutePath包含的路径数="+absolutePath.getNameCount());
		PrintStr("绝对路径的第三个名字="+absolutePath.getName(3).toString());
		
		Path path2 = Paths.get("D:", "Program Files (x86)","ADT");
		PrintStr(path2.toString());
	}
	private static void PrintStr(String str){
		System.out.println(str);
	}
}
