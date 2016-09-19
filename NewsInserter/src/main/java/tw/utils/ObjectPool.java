package tw.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.TimeUnit;  
  
public abstract class ObjectPool<T> {  
    private ConcurrentLinkedQueue<T> pool;  
  
    private ScheduledExecutorService executorService;  
  
    /** 
     * Creates the pool. 
     *  
     * @param minIdle 
     *            初始化最小的对象池中对象创建数量 
     */  
    public ObjectPool(final int minIdle) {  
        // initialize pool  
        initialize(minIdle);  
    }  
  
    /** 
     * Pool创建 
     *  
     * @param minIdle 
     *            最小的数量 
     * @param maxIdle 
     *            最大数量 
     * @param validationInterval 
     *            检查最大/最小的池中的对象的频率 
     */  
    public ObjectPool(final int minIdle, final int maxIdle,  
            final long validationInterval) {  
        // initialize pool  
        initialize(minIdle);  
  
        // check pool conditions in a separate thread  
        executorService = Executors.newSingleThreadScheduledExecutor();  
        executorService.scheduleWithFixedDelay((Runnable) () -> {
            int size = pool.size();
            if (size < minIdle) {
                int sizeToBeAdded = minIdle - size;
                for (int i = 0; i < sizeToBeAdded; i++) {
                    pool.add(createObject());
                }
            } else if (size > maxIdle) {
                int sizeToBeRemoved = size - maxIdle;
                for (int i = 0; i < sizeToBeRemoved; i++) {
                    pool.poll();
                }
            }
        }, validationInterval, validationInterval, TimeUnit.SECONDS);
    }  
  
    /** 
     * 获取对象，如果没有，那就创建且返回 
     *  
     * @return T borrowed object 
     */  
    public T borrowObject() {  
        T object;  
        if ((object = pool.poll()) == null) {  
            object = createObject();  
        }  
  
        return object;  
    }  
  
    /** 
     * Returns object back to the pool. 
     *  
     * @param object 
     *            object to be returned 
     */  
    public void returnObject(T object) {  
        if (object == null) {  
            return;  
        }  
  
        this.pool.offer(object);  
    }  
  
    /** 
     * Shutdown this pool. 
     */  
    public void shutdown() {  
        if (executorService != null) {  
            executorService.shutdown();  
        }  
    }  
  
    /** 
     * Creates a new object. 
     *  
     * @return T new object 
     */  
    protected abstract T createObject();  
  
    private void initialize(final int minIdle) {  
        pool = new ConcurrentLinkedQueue<T>();  
  
        for (int i = 0; i < minIdle; i++) {  
            pool.add(createObject());  
        }  
    }  
}  