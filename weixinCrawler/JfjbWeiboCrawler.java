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
import org.json.JSONException;
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
public class JfjbWeiboCrawler implements PageProcessor {

    private Site site;
    private String url = "http://weibo.com/jfjb";
    private int sleeptime=4000;

    @Override
    public void process(Page page) {
            	
    	// 定义如何抽取页面信息，并保存下来		
		if (page.getUrl().toString().startsWith("http://weibo.com/jfjb")) {
            System.out.println("从微博首页中抽取URL...\n");
            List<String> wburlall=homepageprocess(page);
            page.addTargetRequests(wburlall);
        } else { 
        	if (page.getUrl().regex("http://www\\.weibo\\.com/d+/S").toString() != null); { 
            System.out.println("从独立微博中抽取所有信息...\n");
            weibopageprocess(page);            
            
        }  
        }
    }
  //定制微博首页的处理逻辑：模拟下拉；点击下一页；抽取所有微博的href；保存到待抽取队列。
  		public  List<String>  homepageprocess(Page page) {
  	        //从首页中抽取微博网址链接加入后续抽取队列中		

  			List<String>  weiboURL = page.getHtml().xpath("//*[@node-type='feed_list_item_date']/@href").all();
  			try{
  				//查找下一页元素
  				String nextpage=page.getHtml().xpath("//a[@class='page next S_txt1 S_line1']/@href").get();
  				weiboURL.add(0, nextpage);
  			}
  			catch(NoSuchElementException e){
  				System.out.println("No more page!");
  			};
  			System.out.println("weiboURL:"+weiboURL+"\n");  			
  	        return weiboURL;
  	    }
  	

  		
  	//定制微博转发列表的处理逻辑：模拟点击转发；抽取所有转发微博的href；保存到待抽取队列。
  		public  List<String>  forwardprocess(Page page) {
  	        //抽取微博网址链接加入后续抽取队列中		

  			
  			List<String>  forwardurl = page.getHtml().xpath("//*a[@suda-data='key=tblog_home_new&value=feed_tran_time']/@href").all();
  			try{
  				//查找下一页元素
  				String nextpage=page.getHtml().xpath("//a[@class='page next S_txt1 S_line1']/@href").get();
  				forwardurl.add(0, nextpage);
  			}
  			catch(NoSuchElementException e){
  				System.out.println("No more page!");
  			};
  			System.out.println("forwardurl:"+forwardurl+"\n");  			
  	        return forwardurl;
  	    }
  		
  	
  		
  //定制独立微博页面信息抽取	    
  	 public void weibopageprocess(Page page){
  		//System.out.println("以下为独立微博页面信息\n"+page.getRawText()+"-----------结束--------\n");
  	   
  		
        	//抽取微博源URL
     		String  weiboURL = page.getHtml()
     				.xpath("//*[@node-type='feed_list_item_date']/@href").get();
     		page.addTargetRequest(weiboURL);
     	    System.out.println("从该微博中抽取信息:"+weiboURL);
      		//抽取微博ID
    		 String weiboID = page.getHtml()
    						.xpath("//*div[@class='WB_from S_txt2']/a[@node-type='feed_list_item_date']/@name").get();
    		 System.out.println("微博(转发微博)ID:"+weiboID);

    	   //抽取微博正文
      		String weiboTEXT0 = page.getHtml()
      				.xpath("//*[@node-type='feed_list_content']/allText()").get() ;  
      		String weiboTEXT=weiboTEXT0.replace("置顶", "");  		  		
      		System.out.println("weiboTEXT:"+weiboTEXT);
      		  		
      	   //抽取微博发布时间
      		String weiboTIME = page.getHtml()
      				.xpath("//*div[@class='WB_from S_txt2']/a[@node-type='feed_list_item_date']/@title").get() ;  
      		System.out.println("weiboTIME:"+weiboTIME);
      	   //抽取微博作者
      		String weiboAUTHOR = page.getHtml()
      				.xpath("//*[@node-type='feed_list_content']/@nick-name").get() ;  
      		System.out.println("weiboAUTHOR:"+weiboAUTHOR);
      	  //抽取微博转发数
      		String weiboZFS0 = page.getHtml()
      				.xpath("//*span[@node-type='forward_btn_text']/allText()").get() ; 
      		String weiboZFS=weiboZFS0.replace("转发", "");
      		System.out.println("weiboZFS:"+weiboZFS);
      	//抽取微博评论数
      		String weiboPLS0 = page.getHtml()
      				.xpath("//*ul[@class='WB_row_line WB_row_r4 clearfix S_line2']/li[3]/allText()").get() ;
      		String weiboPLS=weiboPLS0.replace("评论", "");		 
      		System.out.println("weiboPLS:"+weiboPLS);
      	//抽取微博点赞数
      		String weiboDZS = page.getHtml()
      				.xpath("//*span[@node-type='like_status']/allText()").get() ;  
      		System.out.println("weiboDZS:"+weiboDZS);
      		
      	
      		
  			
        List<String> replyInfo0 = page.getHtml().xpath("//*div[@class='WB_feed_repeat S_bg1']/allText()").all();
        for (String replyInfo : replyInfo0)
        {  			
  		//抽取评论ID
  		String comment_id = page.getHtml()
  				.xpath("//*div[@node-type='comment_list']/div[@class='list_li S_line1 clearfix']/@comment_id").get() ;  
  		System.out.println("comment_id:"+comment_id);
  		
  	  //抽取评论作者
  		String comment_author = page.getHtml()
  				.xpath("//*div[@class='WB_text']/[@ucardconf='type=1']/allText()").get() ;  
  		System.out.println("comment_author:"+comment_author);	
  		
  		//抽取评论内容
  		String comment_text0 = page.getHtml()
  				.xpath("//*div[@node-type='replywrap']/div[@class='WB_text']/allText()").get() ;  
  		String comment_text=(comment_text0.replaceAll(comment_author,""));
  		System.out.println("comment_text:"+comment_text);
  		
  		//抽取评论时间
  		String comment_time = page.getHtml()
  				.xpath("//*div[@class='WB_func clearfix']/div[@class='WB_from S_txt2']/allText()").get() ;  
  		System.out.println("comment_time:"+comment_time);
  		
  		
  		}
        
        
  		
//  		WebDriver driver = new ChromeDriver();
//  		
//        WebElement ZFElement=driver.findElement(By.xpath(("//a[@suda-uatrack='key=profile_feed&value=comment']/@href")));
//        ZFElement.click();
//		try {
//			Thread.sleep(sleeptime);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
        forwardprocess(page);
        
		List<String> forwardInfo0 = page.getHtml().xpath("//*div[@class='WB_repeat S_line1']/allText()").all();
        for (String forwardInfo:forwardInfo0) {	
  		
       //抽取转发ID
        String  forward_id = page.getHtml()
				.xpath("//*div[@class='feed_list']/div[@class='list_li S_line1 clearfix']/@mid").get() ;  
		System.out.println("forward_id:"+forward_id);
		
		//抽取转发源ID
		String origin_id = page.getHtml()
				.xpath("//*div[@node-type='feedconfig']/div[@class='WB_cardwrap WB_feed_type S_bg2 ']/@name").get();  
		System.out.println("origin_id:"+origin_id);		
		
		
        //抽取转发url		
		String forward_url = page.getHtml()
				.xpath("//*a[@suda-data='key=tblog_home_new&value=feed_tran_time']/@href").get();
		System.out.println("forward_url:"+forward_url);	
			
		//抽取转发时间
		String forward_time = page.getHtml()
				.xpath("//*a[@suda-data='key=tblog_home_new&value=feed_tran_time']/@title").get() ;  
		System.out.println("forward_time:"+forward_time);
		}
		
        }
        

  	
 
    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("weibo.com").setSleepTime(3000);
        }
        return site;
    }

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		SeleniumDownloader downlder=new SeleniumDownloader("d:\\chromedriver.exe");
		downlder.setSleepTime(6000);
		Spider.create(new JfjbWeiboCrawler()).thread(1)
        .pipeline(new FilePipeline("D:/网页爬取结果"))
        .setDownloader(downlder)
        .addUrl("http://weibo.com/jfjb")
        .run();
		System.out.println("Weibo crawled!");

	}

}
