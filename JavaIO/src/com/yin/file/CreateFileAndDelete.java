package com.yin.file;

import java.io.File;
import java.io.IOException;

public class CreateFileAndDelete {

	public static void main(String[] args) {
		 
        String Path = "parent";
        File f = new File(Path);
        try {
            /*因为创建和删除文件涉及到底层操作，所以有可能会引发异常*/
             
            //如果创建成功则会返回true
            //如果已存在该文件，则创建不成功，返回flase，别以为会覆盖
            System.out.println("创建文件:" + f.createNewFile());
             
            //删除文件，成功返回true，否则返回flase
            System.out.println("删除文件：" + f.delete());
             
            //此方法表示在虚拟机退出时删除文件
            //原因在于：程序运行时有可能发生异常造成直接退出
            //清理残余很有必要～！
            f.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

}
