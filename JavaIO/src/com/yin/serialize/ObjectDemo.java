package com.yin.serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ObjectDemo {

	public static void main(String[] args) {
		PersonBean personBean1 = new PersonBean();
		personBean1.setName("long");
		personBean1.setAge(20);
		PersonBean personBean2 = new PersonBean();
		personBean2.setName("fei");
		personBean2.setAge(25);
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		String path = "D:\\Program Files (x86)\\ADT\\workspace\\JavaIO\\demoTest.txt";
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			FileInputStream fileInputStream = new FileInputStream(path);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(personBean1);
			objectOutputStream.writeObject(personBean2);
			
			objectInputStream = new ObjectInputStream(fileInputStream);
//			PersonBean personBean3 = (PersonBean)objectInputStream.readObject();
//			PersonBean personBean4 = (PersonBean) objectInputStream.readObject();
			ArrayList<Object> personBean3 = (ArrayList<Object>)objectInputStream.readObject();
			ArrayList<Object> personBean4 = (ArrayList<Object>) objectInputStream.readObject();
			System.out.println(personBean3.toString());
			System.out.println(personBean4.toString());
//			System.out.println(personBean3 == personBean1);
//			System.out.println(personBean4 == personBean2);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
