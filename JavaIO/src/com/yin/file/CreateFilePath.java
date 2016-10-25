package com.yin.file;

import java.io.File;

public class CreateFilePath {

	public static void main(String[] args) {
        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
        //path在此处作为父目录存在
        File f1 = new File(path,"/abc");
        File f2 = new File(path,"/d/e/f/g");
        //创建一个目录
        System.out.println(f1.mkdir());
        //递归创建目录
        System.out.println(f2.mkdirs());
         
    } 

}
