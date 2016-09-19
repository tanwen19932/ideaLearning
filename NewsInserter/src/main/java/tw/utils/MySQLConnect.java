package tw.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MySQLConnect {
	public static final Logger LOG = LoggerFactory.getLogger(MySQLConnect.class);
	
	protected static String	 JDBC_DRIVER = "com.mysql.jdbc.Driver";
	protected static String	 JDBC_URL = "jdbc:mysql://localhost:3306/jwbasebj_318?characterEncoding=utf8";

	protected static String	 JDBC_URL_HM = "jdbc:mysql://192.168.55.17:3306/uarticles_2016?characterEncoding=utf8";
	
	
	protected static String	 JDBC_URL_AM1 = "jdbc:mysql://54.165.119.201:3306/text2?characterEncoding=utf8";
	protected static String	 JDBC_URL_AM2 = "jdbc:mysql://52.23.181.210:3306/text2?characterEncoding=utf8";
	protected static String	 JDBC_USER_AM	="root";
	protected static String	 JDBC_PWD_AM	= "Test@123.cn";
	
	
	protected static String	 JDBC_USER	="root";
	protected static String	 JDBC_PWD	= "Test@123.cn";
	protected static String	 JDBC_PWD_5	= "Test@123.cn";
	protected static String	 JDBC_PWD_HM= "112358s";
	
	protected static Connection connection = null;
	protected static Connection connection_AM1 = null;
	protected static Connection connection_AM2 = null;
	protected static Connection connection5 = null;
	protected static Connection connection20 = null;
	protected static Connection connection_HM = null;
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		if(connection != null){
			if( connection.isClosed() ) connection = null ;
			else return connection ;
		}
		Class.forName(JDBC_DRIVER);
		connection = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PWD);
		return connection;
	}
	
	
	public static Connection getAM2Connection() throws SQLException, ClassNotFoundException {
		if(connection_AM2 != null){
			if( connection_AM2.isClosed() ) connection_AM2 = null ;
			else return connection_AM2 ;
		}
		Class.forName(JDBC_DRIVER);
		connection_AM2 = DriverManager.getConnection(JDBC_URL_AM2,JDBC_USER_AM,JDBC_PWD_AM);
		return connection_AM2;
	}
	
	public static Connection getAM1Connection() throws SQLException, ClassNotFoundException {
		if(connection_AM1 != null){
			if( connection_AM1.isClosed() ) connection_AM1 = null ;
			else return connection_AM1 ;
		}
		Class.forName(JDBC_DRIVER);
		connection_AM1 = DriverManager.getConnection(JDBC_URL_AM1,JDBC_USER_AM,JDBC_PWD_AM);
		return connection_AM1;
	}
	
	public static Connection get5Connection() throws SQLException, ClassNotFoundException {
		if(connection5 != null){
			if( connection5.isClosed() ) connection5= null;
			else return connection5;
		}
		Class.forName(JDBC_DRIVER);
		connection5 = DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PWD_5);
		return connection5;
	}
	
	public static  Connection getHMConnection() throws SQLException, ClassNotFoundException {
		if(connection_HM != null){
			if( connection_HM.isClosed() ) {
				connection_HM = null;
			}
			else return connection_HM;
		}
		Class.forName(JDBC_DRIVER);
		connection_HM = DriverManager.getConnection(JDBC_URL_HM,JDBC_USER,JDBC_PWD_HM);
		return connection_HM;
	}
	
	
	public static ResultSet select(String sql ,Connection connection) {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean executeSql(String sql , Connection connection) {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(sql);
			boolean rs = preparedStatement.execute();
			preparedStatement.close();
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Connection getConnection(String ip , String table, String user , String password) throws SQLException {
		String url = "jdbc:mysql://"+ip+":3306/"+table+"?characterEncoding=utf8";
		Connection con = DriverManager.getConnection(url, user, password);
		return con;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Connection connection = MySQLConnect.getConnection();
		
		System.out.println(connection.toString());
	}
}
