package create._4Builder;


public class Cat {
	
	String name;
	int height;
	int weight;
	public Cat(Builder builder){
		this.name = builder.name;
		this.height = builder.height;
		this.weight = builder.weight;
	}

	@Override
	public String toString() {
		return "Cat{" +
				"height=" + height +
				", name='" + name + '\'' +
				", weight=" + weight +
				'}';
	}

	static class Builder{
		String name = "";
		int weight =0;
		int height =0 ;

		public Builder buildHeight(int height) {
			this.height = height;
			return this;
		}

		public Builder buildWeight(int weight) {
			this.weight = weight;
			return this;
		}

		public Builder buildName(String name) {
			this.name = name;
			return this;
		}

		public Cat build(){
			return new Cat(this);
		}
	}
	public static void main(String[] args){
		Cat cat = new Builder().buildHeight(10).build();
		System.out.println(cat);
	}
}
