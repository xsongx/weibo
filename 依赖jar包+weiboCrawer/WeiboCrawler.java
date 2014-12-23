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
 * 抓取解放军报微博平台中军报记者的微博信息和用户评论信息;<br>
 * 模拟登陆网页后，根据指定内容进行抓取;<br>
 * 采用模拟点击，抓取所有微博的用户评论;<br>
 * 抓取微博的转发路径列表。
 * 
 * @author zhong.mx
 * 
 */
public class WeiboCrawler implements PageProcessor {
	      // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site=Site.me().setRetryTimes(3).setCharset("UTF-8")
			.setSleepTime(100000).addStartUrl("http://weibo.com/jfjb");
	
	@Override
	// process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
//网页script源文件		
		//String str = page.getHtml().toString();
    	//System.out.println(str);
		
    	// 部分二：定义如何抽取页面信息，并保存下来
    	WebDriver driver;
    	driver = new ChromeDriver();
    	driver.get("http://weibo.com/jfjb");
        
  //模拟登陆代码
    	//WebElement element = driver.findElement(By.name("password"));
        //element.sendKeys("password");
        //element.submit();
    	
  //抽取微博正文
    	WebElement weibotext = driver.findElement(By.xpath("//div[@class='W_icon icon_feedhot_lite']"));
    	System.out.println(weibotext);	
	}
	
	
//    	page.putField("微博正文", page.getHtml().xpath("//div[@class='WB_feed WB_feed_profile']"
//    			+ "/div[@class='WB_cardwrap WB_feed_type S_bg2 ']/div[@class='WB_feed_detail clearfix']"
//    			+ "/div[@class='WB_detail']/div[@class='WB_text W_f14']/text()").get());
//    	
//          if (page.getResultItems().get("微博正文") == null) {
//            //skip this page
//            page.setSkip(true);
//            }
//	}
        
    	
        // 部分三：从页面发现后续的url地址来抓取
      
	        
//        page.addTargetRequests(page.getHtml().links().regex("href='[^\s]+').all());
//      	
//        if (page.getUrl().toString().contains("pins")) {
//            page.putField("img", page.getHtml().xpath("//div[@id='pin_img']/img/@src").toString());
//        } else {
//            page.getResultItems().setSkip(true);
//        }
//        }
//        List<String> urls = page.getHtml().css("div.pagination").links().regex(".*/java.*").all();
//        page.addTargetRequests(urls); 
       
  
	 @Override
	    public Site getSite() {
	        if (site == null) {
	            site = Site.me().setDomain("weibo.com").setSleepTime(100000);
	        }
	        return site;
	    }
	
	
	
	
	//主程序入口
	 public static void main(String[] args) {
	        Spider.create(new WeiboCrawler()).thread(1)
	                //.scheduler(new RedisScheduler("localhost"))
	        //持久化到网页爬取结果文件夹中，以网址为文件夹名，抽取结果保存为html格式        
	                .addPipeline(new FilePipeline("d:\\网页爬取结果"))
	                .downloader(new SeleniumDownloader("d:\\chromedriver.exe"))
	                .run();
	    }
	}
	 


	