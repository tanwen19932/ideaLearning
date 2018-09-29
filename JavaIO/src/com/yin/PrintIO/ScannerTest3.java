package com.yin.PrintIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class ScannerTest3 {

	public static void main(String[] args ) {
		 
        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demoTest.txt";
         
        File f = new File(path);
        Path path2 = f.toPath();
        Scanner input = null;
        try {
            //从文件构造Scanner对象，有可能产生异常
            input = new Scanner(path2);
            
            System.out.println(f.getAbsolutePath());
            System.out.print(input.hasNext());
            while(input.hasNext()) {
                String s = input.nextLine();
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            input.close();
        }  
    }

}
