package cn.edu.nudt.nlp.crawler;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.openqa.selenium.WebElement;

import us.codecraft.webmagic.Page;

public class jfjbwb {
	//微博（转发）表weiboInfo
	private String weiboURL;//微博（转发）href
	private String weiboID;//微博（转发）ID
	private String weiboTEXT;//微博（转发）正文
	private Date weiboTIME;//微博（转发）发布时间
	private String weiboAUTHOR;//微博（转发）作者
	private String weiboZFS;//微博（转发）转发数
	private String weiboPLS;//微博（转发）评论数
	private String weiboDZS;//微博（转发）点赞数
	
	//微博（转发）评论表replyInfo
	private String comment_id;//微博（转发）评论ID
	private String comment_text;//微博（转发）评论正文
	private String comment_author;//微博（转发）评论作者
	private Date comment_time;//微博（转发）评论发布时间
	
	//微博（转发）表forwardInfo
	private String forward_id;//微博（转发）ID
	private String origin_id;//微博（转发）源ID
	private Date forward_time;//微博（转发）时间
	
	
	
		
		
	}
	

