package create._2AbstractFactory;

import create._1FactoryMethod.Animal;

/**抽象工厂就像工厂(多个产品)，而工厂方法则像是工厂的一种产品生产线
 * @author TW
 *
 */
public abstract class AbstractFactory {
	abstract Animal createDog();
	abstract Animal createCat();
}
