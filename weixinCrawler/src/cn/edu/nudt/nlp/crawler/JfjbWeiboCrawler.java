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
        /*//page.addTargetRequest(url);      	
    	if (page==null)
    		System.exit(0);
    	String txt=page.getRawText();
    	FileWriter fr = null;
		try {
			fr = new FileWriter("/media/work/gitbase/weibo/weixinCrawler/data/weibo.txt");
			fr.write(txt);
			fr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}    	    	
    	System.out.println(txt);*/
    	
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
		SeleniumDownloader downlder=new SeleniumDownloader("/media/work/gitbase/chromedriver");
		downlder.setSleepTime(6000);
/*		SinaWeibo weibo = new SinaWeibo("xsongx", "jiajia20090924");
		String loginin=weibo.login();
        if(loginin != null) {
            System.out.println("登陆成功！");
            //String url = "http://www.weibo.com/hm";
            //          String source = MyUrlUtil.getResource(url);
            //          System.out.println(source);
        } else {
            System.out.println("登录失败！");
            System.exit(0);
        }*/
        //Login();
		Spider.create(new JfjbWeiboCrawler())
        .pipeline(new FilePipeline("/media/work/gitbase/weibo/weixinCrawler/data/"))
        .setDownloader(downlder)
        .addUrl("http://weibo.com/jfjb")
        .run();
		System.out.println("Weibo crawled!");

	}

}
