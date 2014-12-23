package cn.edu.nudt.nlp.crawler;

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
public class HuabanProcessor implements PageProcessor {
	 
    private Site site;
 
    @Override
    public void process(Page page) {
    	//按正则匹配表达式进行抽取URL，添加到要抓取的URL列表中
        page.addTargetRequests(page.getHtml().links().regex("http://huaban\\.com/.*").all());
        //
        if (page.getUrl().toString().contains("pins")) {
            page.putField("img", page.getHtml().xpath("//div[@id='pin_img']/img/@src").toString());
        } else {
            page.getResultItems().setSkip(true);
        }
    }
 
    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me().setDomain("huaban.com").addStartUrl("http://huaban.com/").setSleepTime(1000);
        }
        return site;
    }
 
    public static void main(String[] args) {
        Spider.create(new HuabanProcessor()).thread(5)
                //.scheduler(new RedisScheduler("localhost"))
        //持久化到test文件夹中，以网址为文件夹名，抽取结果保存为html格式        
                .pipeline(new FilePipeline("d:\\网页爬取结果"))
                .downloader(new SeleniumDownloader("d:\\chromedriver.exe"))
                .run();
    }
}

	