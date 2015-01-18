package cn.edu.nudt.nlp.crawler;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;

public class WeiboInfo {
	//微博（转发）表weiboInfo
	private String weiboID;//微博（转发）ID
	private String weiboURL;//微博（转发）href	
	private String text;//微博（转发）正文
	private Date   Time;//微博（转发）发布时间
	private Date   Crawl_TIME;//记录微博抓取系统当前时间
	private String repostFromID;//0为原创，非0为源微博ID
	private String AuthorID;//微博（转发）作者id
	private String weiboZFS;//微博（转发）转发数
	private String weiboPLS;//微博（转发）评论数
	private String weiboDZS;//微博（转发）点赞数
	private String weiboSentiment;// 记录微博的情感值计算结果
	
	public Date getTime() {
		return Time;
	}
	
	public static void setTime(Date Time) {
		Time = new Date();
	}
	
	
	
	
	public Date getCrawl_TIME() {
		return Crawl_TIME;
	}
	
    public  void setCrawl_TIME(Date Crawl_TIME) {
		this.Crawl_TIME = Crawl_TIME;
	}
	
    public String getrepostFromID() {
    	return repostFromID;
    }    
    
    public void setrepostFromID(String repostFromID) {
    	this.repostFromID = repostFromID;
    } 
    
    

public String getweiboURL() {
	return weiboURL;
}

public void setweiboURL(String weiboURL) {
	this.weiboURL = weiboURL;
}





public String getweiboID() {
	return weiboID;
}

public void setweiboID(String weiboID) {
	this.weiboID = weiboID;
}





public String gettext() {
	return text;
}
public void settext(String text) {
	this.text = text;
}


public String getAuthorID() {
	return AuthorID;
}
public void setAuthorID(String AuthorID) {
	this.AuthorID = AuthorID;
}





public String getweiboDZS() {
	return weiboDZS;
}
public void setweiboDZS(String weiboDZS) {
	this.weiboDZS = weiboDZS;
}

public String getweiboPLS() {
	return weiboPLS;
}
public void setweiboPLS(String weiboPLS) {
	this.weiboPLS = weiboPLS;
}
public String getweiboZFS() {
	return weiboZFS;
}
public void setweiboZFS(String weiboZFS) {
	this.weiboZFS = weiboZFS;
}

public String getweiboSentiment() {
	return weiboSentiment;
}
public void setweiboSentiment(String weiboSentiment) {
	this.weiboSentiment = weiboSentiment;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((weiboID == null) ? 0 : weiboID.hashCode());
	return result;
}

	

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	WeiboInfo other = (WeiboInfo) obj;
	if (weiboID == null) {
		if (other.weiboID != null)
			return false;
	} else if (!weiboID.equals(other.weiboID))
		return false;
	return true;
}


@SuppressWarnings("deprecation") //不检测过期的方法
public static void main(String[] args) {  
    //读取并解析配置文档  
    Configuration cfg = new Configuration().configure();  
    //读取并解析映射文件，创建SessionFactory  
    SessionFactory sf = cfg.buildSessionFactory();  
    //打开session  
    Session session = sf.openSession();  
    //开启事务  
    Transaction wbinfo = session.beginTransaction();  
    //持久化操作  
    WeiboInfo weibo = new WeiboInfo(); 
    weibo.setweiboID("10000");
    weibo.setweiboURL("");
    weibo.settext("");
    weibo.setAuthorID("");
    weibo.setCrawl_TIME(new Date(System.currentTimeMillis())); 
    weibo.setweiboZFS("");
    weibo.setweiboPLS("");
    weibo.setweiboDZS("");
    weibo.setrepostFromID("");
    session.save(weibo);  
    //提交事务  
    wbinfo.commit();  
    //关闭Session  
    session.close();  
    //关闭SessionFactory  
    sf.close();  
}


}
	

