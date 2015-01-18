package cn.edu.nudt.nlp.crawler;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;



public class ForwardInfo {
	//微博（转发）表forwardInfo
		private String forward_id;//微博（转发）ID
		private String origin_id;//微博（转发）源ID
		private Date forward_time;//微博（转发）时间
		
		public static void setforward_time(Date forward_time) {
			forward_time = new Date();
		}
		
		public Date getforward_time() {
	    	return forward_time;
	    }
		
		
		
		
		
		
		public String getforward_id() {
	    	return forward_id;
	    }
		public void setforward_id(String forward_id) {
	    	this.forward_id = forward_id;
	    }
		
		public String getorigin_id() {
	    	return origin_id;
	    }
		public void setorigin_id(String origin_id) {
	    	this.origin_id = origin_id;
	    } 
		
		
		
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ForwardInfo other = (ForwardInfo) obj;
			if (forward_id == null) {
				if (other.forward_id != null)
					return false;
			} else if (!forward_id.equals(other.forward_id))
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
		    Transaction fwinfo = session.beginTransaction();  
		    //持久化操作  
		    ForwardInfo forward_id = new ForwardInfo();  
		     
		    session.save(forward_id);  
		    //提交事务  
		    fwinfo.commit();  
		    //关闭Session  
		    session.close();  
		    //关闭SessionFactory  
		    sf.close();  
		}


		}