package com.yin.javaio;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileTest {

	public FileTest() {
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File file = new File(".");
//		结果：D:\Program Files (x86)\ADT\workspace\JavaIO\.
//		结果：D:\Program Files (x86)\ADT\workspace\JavaIO
//		结果：.
//		结果：null
//		结果：.
//		结果：D:\Program Files (x86)\ADT\workspace\JavaIO
//		结果：117051486208
//		结果：67582537728
//		结果：67582537728
//		结果：;
//		结果：true
//		结果：true
//		结果：true
//		结果：false
//		结果：true
//		结果：false
//		结果：false
		
//		结果：1426246320225
		
//		结果：4096
		
//		结果：.classpath
//		结果：.project
//		结果：bin
//		结果：demo.txt
//		结果：src
		
//		结果：.classpath
//		结果：.project
		
//		结果：;
		
//		结果：\
		myPrint(file.getAbsoluteFile().getAbsolutePath());
		myPrint(file.getCanonicalPath());
		myPrint(file.getName());
		myPrint(file.getParent());
		myPrint(file.getPath());
		myPrint(file.getCanonicalFile().getPath());
		myPrint(file.getTotalSpace()+"");
		myPrint(file.getFreeSpace()+"");
		myPrint(file.getUsableSpace()+"");
		myPrint(File.pathSeparator);
		myPrint(file.canRead()+"");
		myPrint(file.canWrite()+"");
		myPrint(file.exists()+"");
		myPrint(file.isAbsolute()+"");
		myPrint(file.isDirectory()+"");
		myPrint(file.isFile()+"");
		myPrint(file.isHidden()+"");
		myPrint(file.lastModified()+"");
		myPrint(file.length()+"");
		String[] strings = file.list();
		for (String string : strings) {
			myPrint(string);
		}
		String[] strings2 = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(".");
			}
		});
		for (String string : strings2) {
			myPrint(string);
		}
		myPrint(File.pathSeparatorChar+"");
		myPrint(File.separatorChar+"");
		
		
		String filestr = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demo.txt";
		FileWriter fileWriter = null;
		try{
			fileWriter = new FileWriter(filestr);
			fileWriter.write("long Yin is a good good boy! Handsome!^_^");
		}finally{//字符流写入操作
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
	}
	
	private static void myPrint(String str){
		System.out.println("结果："+str);
	}

}
