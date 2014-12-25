package cn.edu.nudt.nlp.crawler;

import static us.codecraft.webmagic.selector.Selectors.regex;
import static us.codecraft.webmagic.selector.Selectors.xpath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
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

import redis.clients.jedis.Pipeline;

import us.codecraft.webmagic.*;
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
import us.codecraft.webmagic.Spider;

/**
 * 抓取解放军报微博平台军报记者的微博;<br>
 * 模拟登陆网页;<br>
 * 模拟点击下一页，抓取页面信息中：微博ID、内容、发布时间、URL、转发数、评论数、点赞数、follow用户名+评论、转发列表;<br>
 * 抽取页面信息中后续的微博URL地址假如待抓取队列
 * 
 * @author zhong.mx
 * 
 */
public class WeiboCrawler implements PageProcessor {
	      // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Logger logger = Logger.getLogger(getClass());
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
			.setUseGzip(true);
	private String url = "http://weibo.com/jfjb";
	private boolean needUpdate = false;
	private WebDriver webDriver;
	private volatile WebDriverPool webDriverPool;
	private int sleepTime = 4000;	
	
	public WeiboCrawler(){
		System.getProperties().setProperty("webdriver.chrome.driver",
				"/usr/local/bin/chromedriver");
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
	@Override
	// process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
         
				
    	// 部分二：定义如何抽取页面信息，并保存下来
    	WebDriver driver;
    	driver = new ChromeDriver();
    	driver.get("http://weibo.com/jfjb");
        
         //模拟登陆代码
    	WebElement loginUN = driver.findElement(By.name("username"));
    	loginUN.sendKeys("username");
    	WebElement loginPW = driver.findElement(By.name("password"));
    	loginUN.sendKeys("password");
    	WebElement loginclick = driver.findElement(By.name("submit"));
    	loginclick.submit();
    	
	}
    	
    	
  //抽取微博ID、正文、发布时间、源URL、转发数、评论数、点赞数、转发列表
    //将源URL加入待抽取队列中
	private void processweibo(Page page) { 
        
        String weiboID = page.getRequest().getExtra("weiboID").toString();
        String weiboTIME = page.getRequest().getExtra("weiboTIME").toString();
        String weiboURL = page.getRequest().getExtra("weiboURL").toString();
        String weiboZFS = page.getRequest().getExtra("weiboZFS").toString();
        String weiboPLS = page.getRequest().getExtra("weiboPLS").toString();
        String weiboDZS = page.getRequest().getExtra("weiboDZS").toString();
        
        //定义正则匹配式 
        String re0="weiboID"; //匹配微博ID
        String re1="weiboTIME"; //匹配微博发布时间
        String re2="weiboURL";//匹配微博URL源
        String re3="weiboZFS";//匹配微博转发数
        String re4="weiboPLS";//匹配微博评论数
        String re5="weiboDZS";//匹配微博点赞数
        
        //xpath定位元素位置并抽取值
        List<String> wbID = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re0).all();
        List<String> wbTIME = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re1).all();
        List<String> wbURL = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re2).all();
        List<String> wbZFS = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re3).all();
        List<String> wbPLS = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re4).all();
        List<String> wbDZS = page.getHtml().xpath("//body/table[@class='t12']/tbody/tr").regex(re5).all();
        
        int i=wbID.size();
        for(int j=0;j<i;j++){
           System.out.println(wbID+" "+wbTIME+" "+wbURL+" 转发数："+wbZFS+" 评论数："+wbPLS+" 点赞数："+wbDZS);
        }
        
     // 部分三：从页面发现微博url源地址加入抽取队列中       
        page.addTargetRequests(wbURL);      	
          
	}	
	
	
	//抽取微博follow信息：用户名+评论时间+评论ID+评论内容
	private void processfollow(Page page) {
            List<String> text = page.getHtml().xpath("//*[@id=\"newAlexa\"]/table/tbody/tr/td").all();
            for (String weibotext : text) {
                String link = xpath("//@href").select(weibotext);
                String title = xpath("/text()").select(weibotext);
                if(link==null)
                     continue;
                //System.out.println(title+": "+link+"\n\n");
                Request request = new Request(link).setPriority(0).putExtra("text", title);
                page.addTargetRequest(request);
            
        }
		

        
        }
  
	 @Override
	    public Site getSite() {
	        if (site == null) {
	            site = Site.me().setDomain("weibo.com");
	        }
	        return site;
	    }
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
		
	//主程序入口
	 public static void main(String[] args) {
		 WeiboCrawler wc=new WeiboCrawler();
		// Spider.create(wc).pipeline(new FilePipeline("/media/work/gitbase/weibo/weixinCrawler/data/")).run();
		 Page p1 = wc.download(new Request(wc.url), wc.getSite());
			if (p1 == null)
				System.exit(0);
			FileWriter fr = null;
			try {
				fr = new FileWriter("/media/work/gitbase/weibo/weixinCrawler/data/weibo2.txt");
				fr.write(p1.getRawText());
				fr.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
			System.out.println(p1.getRawText());    
		
			 
	    }
	}
	