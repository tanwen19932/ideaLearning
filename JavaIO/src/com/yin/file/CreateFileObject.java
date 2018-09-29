package com.yin.file;

import java.io.File;

public class CreateFileObject {

	public static void main(String[] args) {
        //创建要操作的文件路径和名称
        //其中，File.separator表示系统相关的分隔符，Linux下为：/  Windows下为：\\
        //path在此程序里面代表父目录，不包含子文件
        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
         
        //childPath在此程序里面代表子目录，包含子文件
        String childPath = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
         
        //用父目录和子文件分隔的方式构造File对象
        //也可以写成 new File("D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent","test.txt");
        File f1 = new File(path,"test.txt");
         
        //使用绝对路径来构造File对象
        //也可以写成new File("D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt");
        File f2 = new File(childPath);
         
        //创建父目录的文件对象
        File parent = new File(path);
        //使用已有父目录对象和子文件构建新的File对象
        File f3 = new File(parent,"hello.txt");
         
        System.out.println("f1的路径=" + f1);
        System.out.println("f2的路径=" + f2);
        System.out.println("f3的路径=" + f3);
    }

}
