package mysqldb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DBConfigBean {
	private String username=null;
	private String password=null;
	private String driver=null;
	private String url=null;
	public DBConfigBean(String username,String password,String driver,String url){
		this.username=username;
		this.password=password;
		this.driver=driver;
		this.url=url;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getDriver() {
		return driver;
	}
	public String getUrl() {
		return url;
	}
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("username : "+username+'\n');
		sb.append("password : "+password+'\n');
		sb.append("driver : "+driver+'\n');
		sb.append("url : "+url);
		return sb.toString();
	}
	public static DBConfigBean getDBConfigBean(){
		Properties property=new Properties();
		try {
			property.load(new FileReader(new File("mysql.properties")));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		String username=property.getProperty("username");
		String password=property.getProperty("password");
		String driver=property.getProperty("driver");
		String url=property.getProperty("url");
		DBConfigBean config=new DBConfigBean(username, password, driver, url);
		return config;
	}
}
