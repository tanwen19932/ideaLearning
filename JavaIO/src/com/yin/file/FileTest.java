package com.yin.file;

import java.io.File;

public class FileTest {

	 public static void main(String[] args) {
		  
	        String Path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
	        File f = new File(Path);
	        //判断文件是否可执行
	        System.out.println("f是否可执行:" + f.canExecute());
	        //判断文件是否存在
	        System.out.println("f是否存在:" + f.exists());
	        //判断文件是否可读
	        System.out.println("f是否可读：" + f.canRead());
	        //判断文件是否可写
	        System.out.println("f是否可写：" + f.canWrite());
	        //判断文件是否为绝对路径名
	        System.out.println("f是否绝对路径：" + f.isAbsolute());
	        //判断文件是否为一个标准文件
	        System.out.println("f是否为标准文件：" + f.isFile());
	        //判断文件是否为一个目录
	        System.out.println("f是否为目录：" + f.isDirectory());
	        //判断文件是否隐藏
	        System.out.println("f是否隐藏：" + f.isHidden());  
	    } 
}
