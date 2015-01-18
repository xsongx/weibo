package cn.edu.nudt.nlp.crawler;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;

public class ReplyInfo {
	
	//微博（转发）评论表replyInfo
		private String comment_id;//微博（转发）评论id
		private String comment_text;//微博（转发）评论正文
		private String userid;//评论作者id
		private String screenNAME;//评论作者当前用户名		
		private Date comment_time;//微博（转发）评论时间
		private String weiboID;//所评论的微博ID
		private String textPrecess;//微博分词词性标注情感词标注
		private String comment_Sentiment;//微博的情感值计算结果
		
		public String gettextPrecess() {
	    	return textPrecess;
	    } 
	    public void settextPrecess(String textPrecess) {
	    	this.textPrecess = textPrecess;
	    }
	    public String getcomment_Sentiment() {
	    	return comment_Sentiment;
	    } 
	    public void setcomment_Sentiment(String comment_Sentiment) {
	    	this.comment_Sentiment = comment_Sentiment;
	    }
		
		
		
		
		
		
		
		
		public Date getcomment_time() {
			return comment_time;
		}
		
	    public static void setcomment_time(Date comment_time) {
	    	comment_time = new Date();
		}
		
	    public String getcomment_text() {
	    	return comment_text;
	    } 
	    public void setcomment_text(String comment_text) {
	    	this.comment_text = comment_text;
	    }
	    
	    public String getuserid() {
	    	return userid;
	    } 
	    public void setuserid(String userid) {
	    	this.userid = userid;
	    }
	    
	    public String getcomment_id() {
	    	return comment_id;
	    }    
	    public void setcomment_id(String comment_id) {
	    	this.comment_id = comment_id;
	    }

	public String getscreenNAME() {
		return screenNAME;
	}
	public void setscreenNAME(String screenNAME) {
    	this.screenNAME = screenNAME;
    }
	

	public String getweiboID() {
		return weiboID;
	}
	public void setweiboID(String weiboID) {
    	this.weiboID = weiboID;
    }
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReplyInfo other = (ReplyInfo) obj;
		if (comment_id == null) {
			if (other.comment_id != null)
				return false;
		} else if (!comment_id.equals(other.comment_id))
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
	    Transaction rpinfo = session.beginTransaction();  
	    //持久化操作  
	    ReplyInfo comment_id = new ReplyInfo();  
	    ReplyInfo.setcomment_time(new Date());  
	    session.save(comment_id);  
	    //提交事务  
	    rpinfo.commit();  
	    //关闭Session  
	    session.close();  
	    //关闭SessionFactory  
	    sf.close();  
	}


	}
		
