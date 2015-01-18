package cn.edu.nudt.nlp.crawler;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;

public class UserInfo {
	
	private String userid;//用户ID
	private String userurl;//用户微博主页
	private String screenname;//用户当前显示名称
	
	
	public String getuserid() {
    	return userid;
    }
	
	public void setuserid(String userid) {
		this.userid = userid;
	}
	
	public String getuserurl() {
    	return userurl;
    }
	
	public void setuserurl(String userurl) {
		this.userurl = userurl;
	}
	
	
	public String getscreenname() {
    	return screenname;
    }
	
	public void setscreenname(String screenname) {
		this.screenname = screenname;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}


	public static void main(String[] args) {  
	    //读取并解析配置文档  
	    Configuration cfg = new Configuration().configure();  
	    //读取并解析映射文件，创建SessionFactory  
	    SessionFactory sf = cfg.buildSessionFactory();  
	    //打开session  
	    Session session = sf.openSession();  
	    //开启事务  
	    Transaction usinfo = session.beginTransaction();  
	    //持久化操作  
	    UserInfo userid = new UserInfo();	    
	    session.save(userid);  
	    //提交事务  
	    usinfo.commit();  
	    //关闭Session  
	    session.close();  
	    //关闭SessionFactory  
	    sf.close();  
	}

}
