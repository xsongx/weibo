package cn.edu.nudt.nlp.crawler;

import static us.codecraft.webmagic.selector.Selectors.regex;
import static us.codecraft.webmagic.selector.Selectors.xpath;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.downloader.selenium.WebDriverPool;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;


/**
 * 抓取解放军报微博平台军报记者的微博;<br>
 * 模拟登陆网页;<br>
 * 模拟点击下一页，抓取页面信息中：微博ID、内容、发布时间、URL、转发数、评论数、点赞数、follow用户名+评论、转发列表;<br>
 * 抽取页面信息中后续的微博URL地址加入待抓取队列
 * 
 * @author zhong.mx
 * 
 */
public class WeiboCrawler implements PageProcessor {
	      // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Logger logger = Logger.getLogger(getClass());
	private Site site = Site.me().setRetryTimes(3).setSleepTime(2000)
			.setUseGzip(true);
	private String url = "http://weibo.com/jfjb";
	private boolean needUpdate = false;
	private WebDriver webDriver;
	private volatile WebDriverPool webDriverPool;
	private int sleepTime = 6000;	
	
	public WeiboCrawler(){
		System.getProperties().setProperty("webdriver.chrome.driver",
				"d:\\chromedriver.exe");
		try {

			if (webDriverPool == null) {
				synchronized (this) {
					webDriverPool = new WebDriverPool(1);
				}
			}

			webDriver = webDriverPool.get();
		} catch (InterruptedException e) {
			logger.warn("interrupted", e);
		} catch (WebDriverException e) {
			logger.error("WebDriverException", e);
			System.exit(0);
		}
	}
	
	//登陆
	public boolean login(){
		ConfigUtil cu=new ConfigUtil("user.properties");
		String durl=cu.getProperty("defaulturl");
		String user=cu.getProperty("username");
		String pswd=cu.getProperty("password");
		if(user==null ||user==""||pswd==null || pswd==""){
			logger.error("Can't find user information from the configure file, login failed");
			return false;
		}
		if(durl!=null && durl.length()>1){
			url=durl;
		}
		webDriver.get(url);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WebDriver.Options manage = webDriver.manage();
		if (site.getCookies() != null) {
			for (Map.Entry<String, String> cookieEntry : site.getCookies()
					.entrySet()) {
				Cookie cookie = new Cookie(cookieEntry.getKey(),
						cookieEntry.getValue());
				manage.addCookie(cookie);
				//18711798186
			}
		}
		try{
		   WebElement loginElement = webDriver.findElement(By.xpath("//a[@node-type='loginBtn']"));
		    loginElement.click();
			
		}
		catch(NoSuchElementException e) {
			logger.info("Already login?");
			logger.error(e.getMessage());
			return true;
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WebElement userElement=webDriver.findElement(By.xpath(("//input[@node-type='username']")));
		userElement.clear();
		userElement.sendKeys(user);
		WebElement pswdElement=webDriver.findElement(By.xpath(("//input[@node-type='password']")));
		pswdElement.clear();
		pswdElement.sendKeys(pswd);
		WebElement subElement=webDriver.findElement(By.xpath(("//a[@node-type='submitBtn']")));
		subElement.click();
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try{
			   WebElement loginElement = webDriver.findElement(By.xpath("//a[@node-type='loginBtn']"));
			  
				   return false;				
			}
			catch(NoSuchElementException e) {
				logger.info("Already login!");
				return true;
			}
	}
	
	
	
	
	
	
	//Downloader是webmagic中下载页面的接口
	//Request对象封装了待抓取的URL及其他信息，Page包含了页面下载后的Html及其他信息。Task是一个包装了任务对应的Site信息的抽象接口。
	//下载微博首页
	public Page download(Request request, Site site) {
		try {
			logger.info("downloading page " + request.getUrl());
			webDriver.get(request.getUrl());
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			WebDriver.Options manage = webDriver.manage();
			if (site.getCookies() != null) {
				for (Map.Entry<String, String> cookieEntry : site.getCookies()
						.entrySet()) {
					Cookie cookie = new Cookie(cookieEntry.getKey(),
							cookieEntry.getValue());
					manage.addCookie(cookie);
				}
			}
			
			WebElement webElement = webDriver.findElement(By.xpath("/html"));
			String content = webElement.getAttribute("outerHTML");
			Page page = new Page();
			page.setRawText(content);
			page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content,
					request.getUrl())));
			page.setUrl(new PlainText(request.getUrl()));
			page.setRequest(request);
			return page;
		} catch (WebDriverException e) {
			logger.error("WebDriverException", e);
			this.needUpdate = false;
			return null;

		}
	}

	@Override
//通过实现PageProcessor接口public void process(Page page)来实现页面分析及链接抽取。
//	通过对Page对象的操作，实现爬虫逻辑。Page对象包括两个最重要的方法：addTargetRequests()可以添加URL到待抓取队列，put()可以将结果保存供后续处理。 

	//定制独立微博页面的处理逻辑：模拟下拉；点击下一页；抽取评论列表信息和转发列表信息
    public void process(Page page) {	
	// 定义如何抽取页面信息，并保存下来		
		if (page.getUrl().toString().equals("http://weibo.com/jfjb")) {
            System.out.println("从微博首页中抽取URL...\n");
            List<String> wburlall=homepageprocess(page);
           page.addTargetRequests(wburlall);
        } else { 
        	if (page.getUrl().regex("http://www\\.weibo\\.com/d+/S").toString() != null); { 
            System.out.println("从独立微博中抽取所有信息...\n\n");
            weibopageprocess(page);
        }  
        }
            
	}
	
	
	
//定制微博首页的处理逻辑：模拟下拉；点击下一页；抽取所有微博的href；保存到待抽取队列。
		public  List<String>  homepageprocess(Page page) {
	        //从首页中抽取微博网址链接加入后续抽取队列中		

        List<String>  weiboURL = page.getHtml()
				.xpath("//*[@node-type='feed_list_item_date']/@href").all();
	    System.out.println("weiboURL:"+weiboURL+"\n");
	    //page.addTargetRequests(weiboURL);
	        return weiboURL;
	    }
		
		
//定制独立微博页面信息抽取	    
	public void weibopageprocess(Page page){
		System.out.println("以下为独立微博页面信息\n"+page.getRawText()+"-----------结束--------\n");

		 List<String> WB = page.getHtml().xpath("//*[@class='WB_feed_detail clearfix']/[@class='WB_detail']/allText()").all();
	        for (String WBinfo : WB) {
	            String mid = xpath("//*div[@mid]/allText()").select(WBinfo);
	            String Omid = xpath("//*div[@Omid]/allText()").select(WBinfo);
	            String WB_text=xpath("//*[@node-type='feed_list_content']/allText()").select(WBinfo);
	            String title=xpath("//*div[@node-type='feed_list_item_date']/@title/text()").select(WBinfo);
	            
	            String WB_url = xpath("//*div[@class='WB_from S_txt2']/@href").select(WBinfo);
	                 if(WB_url!=null)
	                 {
	            Request request = new Request(WB_url).setPriority(0).putExtra("WBinfo", WB_url);
	            page.addTargetRequest(request);
	            System.out.println("mid:"+mid+"Omid:"+Omid+"WB_text:"+WB_text+"title:"+title+"\n\n");
	            }
	        }
		
	        List<String> WB_reply = page.getHtml().xpath("//*[@node-type='comment_detail']/allText()").all();
	        for (String replyinfo : WB_reply) {
	            String usercard = xpath("//*div[@node-type='replywrap']/[@class='WB_text']/@usercard/text()").select(replyinfo);
	            String title = xpath("//*div[@class='WB_func clearfix']/[@class='WB_hadle W_fr']/[@class='WB_from s_txt2']/allText()").select(replyinfo);
	            String comment_id=xpath("//*div[@node-type='comment_list']/[@class='list_li S_line1 clearfix']/@comment_id/text()").select(replyinfo);
	            String comment=xpath("//*div[@node-type='replywrap']/[@class='WB_text']/allText()").select(replyinfo);
	            String username = xpath("//*div[@node-type='replywrap']/[@class='WB_text']/@ucardcof/text()").select(replyinfo);
	            String userurl = xpath("//*div[@node-type='replywrap']/[@class='WB_text']/@href").select(replyinfo);
	                 if(userurl!=null){	                 
	            Request request = new Request(userurl).setPriority(0).putExtra("replyinfo", userurl);
	            page.addTargetRequest(request);}
	            System.out.println("usercard:"+usercard+"title:"+title+"comment_id:"+comment_id+"comment:"+comment+"username:"+username+"userurl:"+userurl+"\n\n");
	        }
		
	        List<String> WB_forward = page.getHtml().xpath("//*[@node-type='forward_detail']/allText()").all();
	        for (String forwardinfo : WB_forward) {
	            String forwardmid = xpath("//*[@action-type='feed_list_item']/@mid/text()").select(forwardinfo);
	            System.out.println("forwardmid:"+forwardmid+"\n\n");  
	        }
		
		
		
	}
	

  
	@Override
	    public Site getSite() {
	        if (site == null) {
	            site = Site.me().setDomain("weibo.com");
	        }
	        return site;
	    }
	
//关闭webDriverPool，释放资源
		public void close() {
			webDriverPool.closeAll();
			//HibernateUtil.closeSession();
		} 
		
	
	 
	//主程序入口
	 public static void main(String[] args) {
		 SeleniumDownloader downloader=new SeleniumDownloader("d:\\chromedriver.exe");
		 WeiboCrawler wc=new WeiboCrawler();
	      //模拟登陆
	      boolean login=wc.login();
			if(login)
				System.out.println("login in!");
		
		Spider.create(wc).thread(5).setDownloader(downloader).addUrl("http://weibo.com/jfjb").runAsync();
		  
/*		//处理微博首页
		Page p1 = wc.download(new Request(wc.url), wc.getSite());
		while (p1.)
		Page p1 = wc.download(new Request(wc.url), wc.getSite());
			if (p1 == null)
				System.exit(0);
			//System.out.println(p1.getRawText());
		wc.process(p1);
		wc.close();*/
		
		
		}	
	}