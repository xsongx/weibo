package mysqldb;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectinFactory {
	@SuppressWarnings("unused")
	private DBConfigBean bean=null;
	private static ConnectinFactory factory=null;
	private Connection conn=null;
	private ConnectinFactory(DBConfigBean bean){
		this.bean=bean;
		try {
			Class.forName(bean.getDriver());
			conn=DriverManager.getConnection(bean.getUrl(),bean.getUsername(),bean.getPassword());
		} catch (Exception e) {
		}
	}
	public static ConnectinFactory getFactory(){
		if(factory==null){
			DBConfigBean bean=DBConfigBean.getDBConfigBean();
			factory=new ConnectinFactory(bean);
		}
		return factory;
	}
	public Connection getConnection(){
		return conn;
	}
}
