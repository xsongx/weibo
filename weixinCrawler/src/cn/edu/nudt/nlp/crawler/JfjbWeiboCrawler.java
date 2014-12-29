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
        //page.addTargetRequest(url);      	
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
    	    	
    	System.out.println(txt);
    }
    public static void Login() {
	    WebDriver driver;
		driver = new ChromeDriver();
		driver.get("http://weibo.com/");
	    
	     //模拟登陆代码
		WebElement loginUN = driver.findElement(By.name("username"));
		loginUN.sendKeys("username");
		WebElement loginPW = driver.findElement(By.name("password"));
		loginPW.sendKeys("password");
		WebElement loginclick = driver.findElement(By.name("submit"));
		loginclick.submit();
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
		downlder.setSleepTime(4000);
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
        Login();
		Spider.create(new JfjbWeiboCrawler())
        .pipeline(new FilePipeline("/media/work/gitbase/weibo/weixinCrawler/data/"))
        .setDownloader(downlder)
        .addUrl("http://weibo.com/jfjb")
        .run();
		System.out.println("Weibo crawled!");

	}

}
