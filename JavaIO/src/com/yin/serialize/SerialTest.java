package com.yin.serialize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
	class Person implements Serializable {
	    private String name;
	    private int age;
	     
	    public Person(String name, int age) {
	        this.name = name;
	        this.age = age;
	    }
	     
	    public String toString() {
	        return "Name:" + this.name + ", Age:" + this.age;
	    }
	}
	 
	public class SerialTest {
	    public static void main(String[] args) {
	        String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demoTest.txt";
	         
	        Person p1 = new Person("zhangsan",12);
	        Person p2 = new Person("lisi",14);
	         
	        //此处创建文件写入流的引用是要给ObjectOutputStream的构造函数玩儿
	        FileOutputStream fos = null;
	        ObjectOutputStream oos = null;
	        try {
	            fos = new FileOutputStream(path);
	            oos = new ObjectOutputStream(fos);
	             
	            //这里可以写入对象，也可以写入其他类型数据
	            oos.writeObject(p1);
	            oos.writeObject(p2);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                oos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
