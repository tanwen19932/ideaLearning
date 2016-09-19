package temp.connectionPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ResourcePool<T> {
  
    private final int ini;  
    private final int max;  
    private LinkedList<T> freeQueue = null;
    private final AtomicInteger freeCount = new AtomicInteger();
    private ArrayList<T> busyList = null;
    private final AtomicInteger busyCount = new AtomicInteger();  
    private final AtomicInteger aliveCount = new AtomicInteger();  
    private static final Logger LOG = LoggerFactory.getLogger(ResourcePool.class);
    /** Lock held by getConnection */  
    private final ReentrantLock getLock = new ReentrantLock();
    /** Wait pool for waiting getConnection */  
    private final Condition empty = getLock.newCondition();
  
  
    private void signalEmpty() {  
        final ReentrantLock getLock = this.getLock;  
        getLock.lock();  
        try {  
            empty.signal();  
        } finally {  
            getLock.unlock();  
        }  
    }  
  
    public ResourcePool(int ini, int max) {  
        if (ini > max) {  
            throw new IllegalArgumentException();  
        }  
        this.ini = ini;  
        this.max = max;  
        this.freeQueue = new LinkedList<T>();  
        this.busyList = new ArrayList<T>(this.max);  
        for (int i = 0; i < this.ini; i++) {  
            this.freeQueue.offer(this.newConnection());  
            this.freeCount.incrementAndGet();  
            this.aliveCount.incrementAndGet();  
        }  
  
    }  
  
    public T getConnection() {  
        final AtomicInteger freeCount = this.freeCount;  
        final ReentrantLock getLock = this.getLock;  
        T conn = null;  
  
        getLock.lock();  
        try {  
            LOG.info("Step in lock 1 . "+Thread.currentThread().getName() );
            if (freeCount.get() > 0) {  
  
                conn = this.freeQueue.getFirst();  
                if (this.freeCount.decrementAndGet() > 0) {  
                    this.empty.signal();  
                }  
                if (this.busyList.add(conn)) {  
                    this.busyCount.incrementAndGet();  
                }  
  
                return conn;  
            }  
            if (this.aliveCount.get() >=  this.max) {  
                while (this.freeCount.get() <= 0) {  
                    LOG.info("Waiting for free. " + Thread.currentThread().getName());
                    //empty.await(2, TimeUnit.SECONDS);  
                    empty.await();  
                }  
                conn = this.freeQueue.getFirst();  
                this.busyList.add(conn);  
                if (this.freeCount.decrementAndGet() > 0) {  
                    this.empty.signal();  
                }  
                this.busyCount.incrementAndGet();  
                return conn;  
            }  
  
        } catch (InterruptedException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return conn;  
        } finally {  
            getLock.unlock();  
        }  
  
        getLock.lock();  
        try {
            LOG.info("Step in lock 3 . "+Thread.currentThread().getName() );
            if (this.aliveCount.get() < this.max) {  
                conn = this.newConnection();  
                this.busyList.add(conn);  
                this.aliveCount.incrementAndGet();  
                this.busyCount.incrementAndGet();  
            }  
            return conn;  
        } finally {  
            getLock.unlock();  
        }  
  
  
    }  
  
    protected abstract T newConnection();  
  
    public boolean release(T conn) {  
        if (conn == null) {  
            throw new NullPointerException();  
        }  
  
        final ReentrantLock releaseLock = this.getLock;  
  
        boolean rz = false;  
        releaseLock.lock();  
  
        try {  
              
            if (this.freeCount.get() >= this.max || this.busyCount.get() < 0) {
                LOG.error("Pool is full, The conn is not belong to our pool or is already release");
                return false;  
            }  
            if (this.busyList.remove(conn)) {  
                this.busyCount.decrementAndGet();  
                if (this.freeQueue.offer(conn) && this.freeCount.incrementAndGet() > 0) {  
                    this.empty.signal();  
                    //log.info("Released! " + Thread.currentThread().getName());  
                    rz = true;  
                }else{
                    LOG.error("Can't Release");
                }  
            }else{
                LOG.error("Can't Release");
                rz = false;  
            }  
              
        } finally {
            LOG.info(Thread.currentThread().getName() + "Release. alive : " + this.aliveCount.get() + "; free :" + this.freeCount.get() + "; busy : " + this.busyCount.get());
  
            releaseLock.unlock();  
        }  
        signalEmpty();  
      
        return rz;  
    }  
  
}  