package create._0SimpleFactory;

/**
 *  不同方法返回
 * @author TW
 */
public class AnimalStoreMutiMethod {
	
	
	public Animal createDog(){
			return new Dog();
	}
	
	public Animal createCat(){
		return new Cat();
	}
	
	public AnimalStoreMutiMethod() {
	}


	public static void main(String[] args) {
		AnimalStoreMutiMethod animalStore = new AnimalStoreMutiMethod();
		Animal animal = animalStore.createDog();
		animal.say();
	}
}
