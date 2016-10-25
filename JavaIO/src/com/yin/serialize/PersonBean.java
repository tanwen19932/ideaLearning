package com.yin.serialize;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class PersonBean implements Serializable {
	private String name;
	private transient int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.writeObject(new StringBuffer(name).reverse());
		out.writeInt(age);
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException{
		this.name = ((StringBuffer)in.readObject()).toString();
		this.age = in.readInt();
	}
	@Override
	public String toString() {
		return "name="+this.name + ",age=" + this.age;
	}
	
	//主要用户枚举类和单例类中非常有用
//	private Object readResolve() throws ObjectStreamException{
//		return this;
//	}
	
	private Object writeReplace() throws ObjectStreamException{
		ArrayList<Object> arrayList = new ArrayList<>();
		arrayList.add(this.name);
		arrayList.add(this.age);
		return arrayList;
	}
}
