package create._3_Singleton;

public class Cat {
	String name = "MiMi";
	  /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */  
	private static Cat mimi = null;
	
	private Cat() {
	}
	
	public void say() {
		System.out.println("喵喵喵");
	}
	
	 /* 静态工程方法，创建实例  不加 synchronized 多线程时会有问题*/ 
	public synchronized static Cat getInsance() {
		if (mimi == null) {  
			mimi = new Cat();  
        }  
        return mimi;  
	}
//	但是，synchronized关键字锁住的是这个对象，这样的用法，在性能上会有所下降，
//	因为每次调用getInstance()，都要对对象上锁，事实上，只有在第一次创建对象的时候需要加锁，
//	之后就不需要了，所以，这个地方需要改进。我们改成下面这个：

	public static Cat getInsance2() {
		if (mimi == null) {  
			synchronized(mimi){
				if(mimi == null)
					/*
					 * 1>A、B线程同时进入了第一个if判断
					 * 2>A首先进入synchronized块，由于mimi为null，所以它执行mimi = new Cat();
					 * 3>由于JVM内部的优化机制，JVM先画出了一些分配给Singleton实例的空白内存，并赋值给mimi成员（注意此时JVM没有开始初始化这个实例），然后A离开了synchronized块。
					 * 4>B进入synchronized块，由于mimi此时不是null，因此它马上离开了synchronized块并将结果返回给调用该方法的程序。
					 * 优化看CatTest
					 */
					mimi = new Cat();  
			}
        }  
        return mimi;  
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
    public Object readResolve() {  
        return mimi;  
    }
	
	public static void main(String[] args) {
		Cat cat = Cat.getInsance();
		cat.say();
	}
}
