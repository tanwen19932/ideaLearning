package temp.connectionPool;

import java.sql.Connection;

/**
 * 模拟线程启动，去获得连接
 * @author Ran
 *
 */
public class ThreadConnection implements Runnable{
    private IConnectionPool pool;
    @Override
    public void run() {
        pool = ConnectionPoolManager.getInstance().getPool("testPool");
    }

    public Connection getConnection(){
        Connection conn = null;
        if(pool != null && pool.isActive()){
            conn = pool.getConnection();
        }
        return conn;
    }

    public Connection getCurrentConnection(){
        Connection conn = null;
        if(pool != null && pool.isActive()){
            conn = pool.getCurrentConnecton();
        }
        return conn;
    }
}