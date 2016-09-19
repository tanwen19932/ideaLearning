package tw.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public abstract class MyThreadPool {
	List<Runnable> runnables;
	ExecutorService executorService ;
	
	public MyThreadPool(int nThreads , List runnables) {
		this.executorService = Executors.newFixedThreadPool(nThreads);
		this.runnables = runnables;
	}
	
	public void start() {
		for( Runnable runnable :runnables )
			executorService.submit(runnable);
	}
}
