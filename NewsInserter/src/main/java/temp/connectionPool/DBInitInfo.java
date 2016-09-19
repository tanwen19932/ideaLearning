package temp.connectionPool;

import java.util.ArrayList;
import java.util.List;
/**
 * 初始化，模拟加载所有的配置文件
 * @author Ran
 *
 */
public class DBInitInfo {
    public  static List<DBbean>  beans = null;
    static{
        beans = new ArrayList<DBbean>();
        // 这里数据 可以从xml 等配置文件进行获取
        // 为了测试，这里我直接写死
        DBbean beanOracle = new DBbean();
        beanOracle.setDriverName("oracle.jdbc.driver.OracleDriver");
        beanOracle.setUrl("jdbc:oracle:thin:@7MEXGLUY95W1Y56:1521:orcl");
        beanOracle.setUserName("mmsoa");
        beanOracle.setPassword("password1234");

        beanOracle.setMinConnections(5);
        beanOracle.setMaxConnections(100);

        beanOracle.setPoolName("testPool");
        beans.add(beanOracle);
    }
}
