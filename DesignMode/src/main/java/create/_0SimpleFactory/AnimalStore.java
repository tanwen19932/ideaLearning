package create._0SimpleFactory;

/**
 *	普通Store 提交创造参数
 * @author TW
 */
public class AnimalStore { 
	
	public Animal create(int i ){
		switch (i) {
		case 0:
			return new Dog();

		default:
			return new Cat();
		}
	}
	
	public static void main(String[] args) {
		AnimalStore animalStore = new AnimalStore();
		Animal animal = animalStore.create(0);
		animal.say();
	}
}
