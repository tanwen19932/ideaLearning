package com.yin.file;

import java.io.File;

public class GetFileChildfile {

	 public static void main(String[] args) {
	        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
	         
	        File f = new File(path);
	         
	        //方式一：list()
	        //返回一个包含指定目录下所有文件名的字符串数组
	        //如果不是一个目录则返回null
	        String[] files = f.list();
	        for (String x : files) {
	            System.out.println(x);
	        }
	         
	        //方式二：listFiles()
	        //返回File数组
	        /*
	        File[] files = f.listFiles();
	        for (File x : files) {
	            //如果需要包含路径，则直接打印x即可
	            System.out.println(x.getName());
	        }
	        */
	         
	    }

}
