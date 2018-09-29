package create._0SimpleFactory;

/**
 *	静态创造
 * @author TW
 */
public class AnimalStoreStatic {
	
	
	public static Animal createDog(){
			return new Dog();
	}
	
	public static Animal createCat(){
		return new Cat();
	}
	
	public AnimalStoreStatic() {
	}
	
	public static void main(String[] args) {
		Animal animal = AnimalStoreStatic.createDog();
		animal.say();
	}
}
