package hbase.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import tw.utils.FileUtil;

import java.io.IOException;
import java.util.*;

/**
 * 
 * @author wuxu
 * 
 */
public class HbaseUtil {
	public static Configuration configuration;
	static {
		System.out.println("初始化Hbase");
//		System.setProperty("hadoop.home.dir", "/hadoop-common-2.2.0-bin-master");
		configuration = HBaseConfiguration.create();
		configuration.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 12000000);  
	}
	
	public static String[] getTables() throws IOException{
		HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
		return hBaseAdmin.getTableNames();
	}

	public static HTable[] getReadHTables(String tableName , int tableN) throws IOException {
		String table_log_name = tableName;
		HTable[]rTableLog = new HTable[tableN];
		for (int i = 0; i < tableN; i++) {
		    rTableLog[i] = new HTable(getConf(), table_log_name);
		    rTableLog[i].setScannerCaching(50);
		}
		return rTableLog;
	}
	
	public static HTable[] getWriteHTables(String tableName , int tableN) throws IOException {
		HTable[] wTableLog = new HTable[tableN];
		try {
			for (int i = 0; i < tableN; i++) {
			    wTableLog[i] = new HTable(HbaseUtil.getConf(), tableName);
			    wTableLog[i].setWriteBufferSize(5 * 1024 * 1024); //5MB
			    wTableLog[i].setAutoFlush(false);
			}
//			putList = Collections.synchronizedList(new LinkedList<>());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < tableN; i++) {
			final int a= i;
		    Thread th = new Thread() {
		        public void run() {
		            while (true) {
		                try {
		                    sleep(1000); //1 second
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
		                                synchronized (wTableLog[a]) {
		                    try {
		                        wTableLog[a].flushCommits();
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                    }
		                }
		            }
		                }
		    };
		    th.setDaemon(true);
		    th.start();
		}
		return wTableLog;
	}
	
	public static Configuration getConf() {
		return configuration;
	}
	// 创建数据库表
	public static void createTable(String tableName, String[] columnFamilys)
			throws Exception {
		// 新建一个数据库管理员
		HBaseAdmin hAdmin = new HBaseAdmin(configuration);
		if (hAdmin.tableExists(tableName)) {
			System.out.println("表 " + tableName + " 已存在！");
//			System.exit(0);
		} else {
			// 新建一个students表的描述
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			// 在描述里添加列族
			for (String columnFamily : columnFamilys) {
				tableDesc.addFamily(new HColumnDescriptor(columnFamily));
			}
			// 根据配置好的描述建表
			hAdmin.createTable(tableDesc);
			System.out.println("创建表 " + tableName + " 成功!");
		}
	}

	// 删除数据库表
	public static void deleteTable(String tableName) throws Exception {
		// 新建一个数据库管理员
		 Scanner s = new Scanner(System.in);
//		System.out.println("sure to delete ? Y / N (uppercase!)");
//		 while (true) { 
//             String line = s.nextLine(); 
//             if ( line.equals("Y") ) break; 
//             else return ; 
//     } 
		HBaseAdmin hAdmin = new HBaseAdmin(configuration);
		if (hAdmin.tableExists(tableName)) {
			// 关闭一个表
//			hAdmin.enableTable(tableName);
			hAdmin.disableTable(tableName);
			hAdmin.deleteTable(tableName);
			System.out.println("删除表 " + tableName + " 成功！");
		} else {
			System.out.println("删除的表 " + tableName + " 不存在！");
//			System.exit(0);
		}
	}
	public static void insertData (String tableName, String rowKey, String family, String qualifier, String value){
	     if(value == null ) return ;   
		 try {
				HTable table = new HTable(getConf(), tableName);
	            Put put = new Put(Bytes.toBytes(rowKey));
	            put.add(Bytes.toBytes(family),Bytes.toBytes(qualifier),Bytes.toBytes(value));
	            table.put(put);
	            System.out.println("insert recored " + rowKey + " to table " + tableName +" ok.");
	        } catch (IOException e) {
	            System.out.println(e);
	        }
	    }
	
	// 删除一条(行)数据
	public static void delRow(String tableName, String rowKey) throws Exception {

		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(configuration);
			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));
			Delete del = new Delete(Bytes.toBytes(rowKey));
			table.delete(del);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 删除多条(行)数据
	public static void delRows(String tableName, List<String> rowkeyList) throws Exception {

		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(configuration);
			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));
			for(String rowKey :rowkeyList){
				Delete del = new Delete(Bytes.toBytes(rowKey));
				table.delete(del);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 添加一条数据
	@SuppressWarnings("deprecation")
	public static void insertData(String tableName, String rowKey,
			String family, Map<String, String> map) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(configuration);
			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			for (Map.Entry<String, String> m : map.entrySet()) {
				put.add(Bytes.toBytes(family), Bytes.toBytes(m.getKey()),
						Bytes.toBytes(m.getValue()));
			}

			table.put(put);
			System.out.println("插入数据成功");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// 添加一条数据多个内容
	@SuppressWarnings("deprecation")
	public static void insertMultiData(String tableName, String rowKey,
			String family, Map<String, String> map) throws Exception {
		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(configuration);
			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowKey));
			for (Map.Entry<String, String> m : map.entrySet()) {
				put.add(Bytes.toBytes(family), Bytes.toBytes(m.getKey()),
						Bytes.toBytes(m.getValue()));
			}
			table.put(put);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 根据rowkey从Hbase中获取新闻信息
	 */
	@SuppressWarnings("deprecation")
	public static void QueryByConditionRowkey(String tableName, String rowkey,
			Map<String, String> map) {
		Connection connection = null;
		try {

			connection = ConnectionFactory.createConnection(configuration);

			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));

			try {
				Get get = new Get(rowkey.getBytes());// 根据rowkey查询
				Result r = table.get(get);
				System.out.println("获得到rowkey:" + new String(r.getRow()));

				if (r.listCells() != null) {
					for (Cell cell : r.listCells()) {
						// String family = Bytes.toString(cell.getFamily());
						String qualifier = (Bytes.toString(cell.getQualifier()))
								.trim();
						System.out.println("value: "
								+ Bytes.toString(cell.getValue()));
						map.put(qualifier, Bytes.toString(cell.getValue()));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从Hbase中获取信息
	 */
	@SuppressWarnings("deprecation")
	public static List<Map<String, String>> QueryAllInfo(String tableName,
			Scan scan) {
		Connection connection = null;
		try {
			connection = ConnectionFactory.createConnection(configuration);
			HTable table = (HTable) connection.getTable(TableName
					.valueOf(tableName));

			try {
				if (scan == null)
					scan = new Scan();
				ResultScanner rs = table.getScanner(scan);
				List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
				for (Result r : rs) {
					if (r.listCells() != null) {
						Map<String, String> map = new HashMap<String, String>();
						String rowkey = "";
						for (Cell cell : r.listCells()) {
							rowkey = Bytes.toString(cell.getRow());
							String qualifier = (Bytes.toString(cell
									.getQualifier())).trim();
							// System.out.println(qualifier+"   "+Bytes.toString(cell.getValue()));
							map.put(qualifier, Bytes.toString(cell.getValue()));
						}
						map.put("rowkey", rowkey);
						lists.add(map);
					}

				}
				return lists;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void getAllRecordToFile (String tableName, String filePath , String family ,String column) {
        try{
             HTable table = new HTable(getConf(), tableName);
             Scan s = new Scan();
             ResultScanner ss = table.getScanner(s);
             int i =0; 
             for(Result r:ss){
            	 i++;
            	 String line = Bytes.toString( r.getValue(Bytes.toBytes(family), Bytes.toBytes(column)) )+"\r\n";
                 FileUtil.fileAppendMethod2(filePath, line);
             }
             System.out.println ("一共有 "+ i + " 条数据");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
	
	public static void getAllRecordNum(String tableName) {
		try{
            HTable table = new HTable(getConf(), tableName);
            Scan s = new Scan();
            long beginDate = System.currentTimeMillis();
            ResultScanner ss = table.getScanner(s);
            int i =0; 
            for(Result r:ss){
           	 i++;
            }
            System.out.println("耗时 ： " + (System.currentTimeMillis()-beginDate)/1000);
            System.out.println ("一共有 "+ i + " 条数据");
       } catch (IOException e){
           e.printStackTrace();
       }
	}
	
	public static String bToS(byte[] bytes) {
		return Bytes.toString(bytes);
	}
	
	public static byte[] sToB(String str) {
		return Bytes.toBytes(str) ;
	}
}
