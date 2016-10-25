package com.yin.file;

import java.io.File;

public class GetAllChileFile {

	 public static void main(String[] args) {
	        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\parent";
	         
	        File f = new File(path);
	        //调用下面的递归方法
	        print(f);
	    }
	     
	    //用递归的方式打印目录列表
	    public static void print(File f) {
	        if(f.isDirectory()){
	            File[] files = f.listFiles();
	            for(File x : files) {
	                print(x);
	            }
	        } else {
	            System.out.println(f);
	        }
	    }

}
