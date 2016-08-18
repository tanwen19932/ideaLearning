package create._4Builder;

import com.sun.org.apache.xpath.internal.operations.String;

public class Cat {
	
	String name;
	int height;
	int weight;
	Builder builder = new Builder();
	
	class Builder{

		int weight =0;
		int height =0 ;

		public Cat buildHeight(int height) {
			Cat.this.height = height;
			return Cat.this;
		}

		public Cat buildWeight(int weight) {
			Cat.this.weight = weight;
			return Cat.this;
		}

		private Cat buildName(String name) {
			Cat.this.name = name;
			return Cat.this;
		}


	}
	public static void main(String[] args){
		Cat cat = new Cat().builder.buildHeight(10);
		System.out.println(cat.height);
	}
}
