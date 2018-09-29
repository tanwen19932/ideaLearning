package com.yin.serialize;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class serialTestFan {

	public static void main(String[] args) {
        String path = "demoTest.txt";
         
        //好吧，这里代码写得着实有点长了，还要抛异常什么的
        //如果你也看的烦，那就在主方法上抛吧，构造方法里用匿名对象就好了
        //什么？别告诉我你不知道匿名对象
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);
             
            //这里返回的其实是一个Object类对象
            //因为我们已知它是个Person类对象
            //所以，就地把它给向下转型了
            Person p = (Person)ois.readObject();
            System.out.println(p);
            Person p2 = (Person)ois.readObject();
            System.out.println(p2);
//            Person p3 = (Person)ois.readObject();
//            if (p3 == null) {
//				System.out.print("p3==null");
//			}else {
//				System.out.println(p3);
//			}
            
             
            //抛死你，烦烦烦～！！！
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //还是要记得关闭下流
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
