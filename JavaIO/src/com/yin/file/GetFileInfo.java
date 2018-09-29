package com.yin.file;

import java.io.File;

public class GetFileInfo {

	 public static void main(String[] args) {
	        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
	         
	        File f = new File(path);
	        //返回文件的绝对路径
	        //此处返回值为String
	        System.out.println("f的绝对路径名：" + f.getAbsolutePath());
	        //返回文件的绝对路径
	        //此处返回值为File
	        System.out.println("f的绝对路径对象：" + f.getAbsoluteFile());
	        //返回文件或目录的名称
	        System.out.println("f的名称：" + f.getName());
	        //返回文件的相对路径
	        //构造函数中封装的是什么路径，就返回什么路径
	        System.out.println("f的路径：" + f.getPath());
	        //返回父目录的路径
	        //如果在构造函数中的路径不是绝对路径，那么此处返回null
	        System.out.println("f的父目录：" + f.getParent());
	    }
}
