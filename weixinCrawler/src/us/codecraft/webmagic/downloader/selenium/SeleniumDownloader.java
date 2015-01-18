package us.codecraft.webmagic.downloader.selenium;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


import cn.edu.nudt.nlp.crawler.ForwardInfo;
import cn.edu.nudt.nlp.crawler.HibernateUtil;
import cn.edu.nudt.nlp.crawler.Message;
import cn.edu.nudt.nlp.crawler.ReplyInfo;
import cn.edu.nudt.nlp.crawler.UserInfo;
import cn.edu.nudt.nlp.crawler.WeiboInfo;
//import cn.edu.nudt.nlp.crawler.ConfigUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * 使用Selenium调用浏览器进行渲染。目前仅支持chrome。<br>
 * 需要下载Selenium driver支持。<br>
 *
 * @author code4crafter@gmail.com <br>
 * Date: 13-7-26 <br>
 * Time: 下午1:37 <br>
 */
public class SeleniumDownloader implements Downloader, Closeable {

    private volatile WebDriverPool webDriverPool;

    private Logger logger = Logger.getLogger(getClass());

    private int sleepTime = 0;

    private int poolSize = 1;
    
    private String url = "http://weibo.com/jfjb";
    
 // 存储相关参数
  	private HashMap<String, Queue<WeiboInfo>> weiboMap = new HashMap<String, Queue<WeiboInfo>>();
  	private HashMap<String, Queue<ReplyInfo>> replyMap = new HashMap<String, Queue<ReplyInfo>>();
  	private HashMap<String, Queue<ForwardInfo>> forwardMap = new HashMap<String, Queue<ForwardInfo>>();
  	private HashMap<String, Queue<UserInfo>> userMap = new HashMap<String, Queue<UserInfo>>();
  	private int queueSize = 200;

  	public int getQueueSize() {
  		return queueSize;
  	}

  	public void setQueueSize(int queueSize) {
  		this.queueSize = queueSize;
  	}
    
    /**
     * 新建
     *
     * @param chromeDriverPath
     */ 
    public SeleniumDownloader(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     * @return this
     */
    public SeleniumDownloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    public boolean login(WebDriver webDriver){
		//ConfigUtil cu=new ConfigUtil("user.properties");
		String durl="http://weibo.com/jfjb";//cu.getProperty("defaulturl");
		String user="xsongx";//cu.getProperty("username");
		String pswd="jiajia20090924";//cu.getProperty("password");
		if(user==null ||user==""||pswd==null || pswd==""){
			logger.error("Can't find user information from the configure file, login failed");
			return false;
		}
		if(durl!=null && durl.length()>1){
			url=durl;
		}
		//webDriver.get(url);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WebDriver.Options manage = webDriver.manage();
		Site site =Site.me().setRetryTimes(3).setSleepTime(2000)
				.setUseGzip(true);
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
    public void setScroll(WebDriver driver,int height){
    	  try {
    	   String setscroll = "document.documentElement.scrollTop=" + height;
    	   
    	   JavascriptExecutor jse=(JavascriptExecutor) driver;
    	   jse.executeScript(setscroll);
    	  } catch (Exception e) {
    	   System.out.println("Fail to set the scroll.");
    	  }   
    	 } 
    public void windowScroll(WebDriver driver){
    	JavascriptExecutor js = (JavascriptExecutor) driver;
	    //boolean reachedbottom = Boolean.parseBoolean(js.executeScript("return $(document).height() == ($(window).height() + $(window).scrollTop());").toString());
	   int i=0;
	    while (i<3) {
	        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,20000)", "");
	        try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        i++;
	        /*try {
	            //reachedbottom=Boolean.parseBoolean(js.executeScript("return $(document).height() == ($(window).height() + $(window).scrollTop());").toString());
	            WebElement element = driver.findElement(By.xpath("//a[@class='page next S_txt1 S_line1']"));
	            Wait<WebDriver> wait_element = new WebDriverWait(driver, 5);
	            wait_element.until(ExpectedConditions.elementToBeClickable(element));
	            //element.click();
	            System.out.println("!!!!!!!!!!!!!!At Last Get Success!!!!!!!!!!!!!!!!");
	            break;
	        } catch (Exception ex) {
	            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
	            System.out.println(ex.getMessage());
	        }*/
	    }
    }
    
    @Override
    public Page download(Request request, Task task) {
        checkInit();
        WebDriver webDriver;
        String url=request.getUrl();
        Page page = new Page();
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        
        logger.info("downloading page " + url);
        webDriver.get(url);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      
        WebDriver.Options manage = webDriver.manage();
/*        manage.window().maximize();
        System.out.println(manage.window().getSize().toString());*/
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }
        try{
			   WebElement loginElement = webDriver.findElement(By.xpath("//a[@node-type='loginBtn']"));
			   if (loginElement != null){	   
					   login(webDriver);
					  /*//webDriver.get(url);
				       try {
				            Thread.sleep(sleepTime);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
				       if (url.startsWith("http://weibo.com/jfjb")){
				        windowScroll(webDriver);
				       }else{
				    	   commentPage(webDriver);
				       }
				        WebElement webElement = webDriver.findElement(By.xpath("/html"));
				        String content = webElement.getAttribute("outerHTML");
				        Page page = new Page();
				        page.setRawText(content);
				        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
				        page.setUrl(new PlainText(request.getUrl()));
				        page.setRequest(request);
				        webDriverPool.returnToPool(webDriver);
				        return page;*/
					}
			}catch(NoSuchElementException e) {
				logger.info("Already login!");				
			}        
        if (url.startsWith("http://weibo.com/jfjb")){
	        windowScroll(webDriver);
	        WebElement webElement = webDriver.findElement(By.xpath("/html"));
	        String content = webElement.getAttribute("outerHTML");
	        
	        page.setRawText(content);
	        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
	        page.setUrl(new PlainText(request.getUrl()));
	        page.setRequest(request);
	       }else{
	    	   page=weiboPage(webDriver);
	       }
        
        webDriverPool.returnToPool(webDriver);
        return page;
    }
//单个微博页面处理，递归调用抽取所有转发和评论    
    public Page weiboPage(WebDriver webDriver){
    	List<String> reurl=new ArrayList();
    	Page page = new Page();
        //page.setRequest(request);
    	while (true){
    		String pageUrl=webDriver.getCurrentUrl();
        	System.out.println("Current Url:"+pageUrl);
        	WebElement webElement = webDriver.findElement(By.xpath("/html"));
            String content = webElement.getAttribute("outerHTML");            
            page.setRawText(content);
            page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, pageUrl)));
            page.setUrl(new PlainText(pageUrl));
            commentExtraction(page);//可设置评论抓取优先级
	    	try{
	    		WebElement cmtNextPage=webDriver.findElement(By.xpath("//a[class='page next S_txt1 S_line1']/span[@action-type='feed_list_page']"));
	    		//List<WebElement> comments=webDriver.findElements(By.xpath("//div[@class='list_li S_line1 clearfix']"));
	    		cmtNextPage.click();
	    		try {
	                Thread.sleep(sleepTime);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	    	}catch(NoSuchElementException e){
	    		logger.info("No more comments:"+pageUrl);
	    		break;
	    	}
    	}
    	try{
    		WebElement retweetBn=webDriver.findElement(By.xpath("//a[@action-type='fl_forward']"));
    		//List<WebElement> comments=webDriver.findElements(By.xpath("//div[@class='list_li S_line1 clearfix']"));
    		retweetBn.click();
    		try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    		
    		while (true){
        		String pageUrl=webDriver.getCurrentUrl();
            	System.out.println("Current Url:"+pageUrl);
            	WebElement webElement = webDriver.findElement(By.xpath("/html"));
                String content = webElement.getAttribute("outerHTML");
               // Page page = new Page();
                page.setRawText(content);
                page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, pageUrl)));
                page.setUrl(new PlainText(pageUrl));
                reurl.addAll(retweettExtraction(page));
    	    	try{
    	    		WebElement rtNextPage=webDriver.findElement(By.xpath("//span[@action-type='feed_list_page']"));
    	    		//List<WebElement> comments=webDriver.findElements(By.xpath("//div[@class='list_li S_line1 clearfix']"));
    	    		rtNextPage.click();
    	    		try {
    	                Thread.sleep(sleepTime);
    	            } catch (InterruptedException e) {
    	                e.printStackTrace();
    	            }
    	    	}catch(NoSuchElementException e){
    	    		System.out.println("No more retweets:"+pageUrl);
    	    		break;
    	    	}
        	}
    	}catch(NoSuchElementException e){
    		System.out.println("Error: No Retweets");
    		
    	}
    	
    	page.addTargetRequests(reurl);
    	weiboExtraction(page);
    	return page;
    }
    
  //定制独立微博页面信息抽取	    
 	 public void weiboExtraction(Page page){
 		//System.out.println("以下为独立微博页面信息\n"+page.getRawText()+"-----------结束--------\n");  
 					 
 		 //抽取微博URL
 		 String  weiboURL = page.getHtml().xpath("//[@node-type='feed_list_item_date']/@href").get();
 	   	 System.out.println("从该微博中抽取信息:"+weiboURL);
    	 
    	//抽取微博最新抓取时间
  		 Date Crawl_TIME = new Date();
  		 System.out.println("微博最新抓取时间（抓取时系统时间）:"+Crawl_TIME);
  		 
  		 
  		//抽取微博ID
   		 String weiboID =page.getHtml().xpath("//div[@node-type='feedconfig']/div[@class='WB_cardwrap WB_feed_type S_bg2 ']/@mid").get();
   		 System.out.println("微博(转发微博)ID:"+weiboID);
     	 
   	    //抽取微博正文
   	   String weiboTEXT0 = page.getHtml()
   				.xpath("//[@node-type='feed_list_content']/allText()").get() ;  
   		String text=weiboTEXT0.replace("置顶", "");  		  		
   		System.out.println("text:"+text);
   		
   	    //抽取微博发布时间
 		String Time = page.getHtml()
 				.xpath("//div[@class='WB_from S_txt2']/a[@node-type='feed_list_item_date']/@title").get() ;  
 		System.out.println("Time:"+Time);
   		
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
   		
       	     //抽取转发微博源ID
       		 String repostFromID = page.getHtml().
       				 xpath("//div[@node-type='feedconfig']/div[@class='WB_cardwrap WB_feed_type S_bg2 ']/@omid").get();
      		 System.out.println("微博(转发微博)源ID:"+repostFromID);
    	 
       	   //抽取微博作者当前名称
       		String weiboAUTHOR = page.getHtml()
       				.xpath("//img[@class='photo']/@alt").get() ;  
       		System.out.println("weiboAUTHOR:"+weiboAUTHOR);    
       	 
       	    //抽取用户当前名称
      		 String screenname = page.getHtml().xpath("//img[@class='photo']/@alt").get();
      		 System.out.println("抽取用户当前名称:"+screenname);
      		
      		 
      		 //抽取微博作者ID
      		String AuthorID0 = page.getHtml()
      				.xpath("//a[@action-type='fl_favorite']/@diss-data").get() ;
      		String AuthorID=AuthorID0.replace("fuid=", "");
      		System.out.println("AuthorID:"+AuthorID);
      		
      	     //抽取用户ID
     		 String userid0 = page.getHtml().xpath("//a[@action-type='fl_favorite']/@diss-data").get();
     		 String userid=userid0.replace("fuid=", "");
     		 System.out.println("抽取用户ID:"+userid);
    		
     		//抽取用户主页
     		 String userurl = page.getHtml().xpath("//a[@node-type='nav_link']/@href").get();
     		 System.out.println("抽取用户主页:"+userurl);
      	
  			
     	//此处将抽取到的微博数据写入数据库
     		Queue<WeiboInfo> wb;	
     		if (weiboMap.containsKey(weiboID)) {
				wb = weiboMap.get(weiboID);
				wb = weiboMap.get(weiboURL);
				wb = weiboMap.get(text);
				wb = weiboMap.get(AuthorID);
				wb = weiboMap.get(Time);
				wb = weiboMap.get(weiboZFS);
				wb = weiboMap.get(weiboPLS);
				wb = weiboMap.get(weiboDZS);
				wb = weiboMap.get(repostFromID);
				} else {
				wb = new LinkedList<WeiboInfo>();
				weiboMap.put(weiboID, wb);
			}
			Session session = HibernateUtil.currentSession();
			Transaction wbT = session.beginTransaction();
			WeiboInfo weibo = new WeiboInfo();
			weibo.setCrawl_TIME(new Date(System.currentTimeMillis()));			
			wbT.commit();
			HibernateUtil.closeSession();
			
//			//保存微博用户信息到数据库
//			
//     		if (userMap.containsKey(userid)) {
//     			Queue<UserInfo> us;	
//     			us = userMap.get(userid);
//     			us = userMap.get(userurl);
//     			us = userMap.get(screenname);				
//			
//			
//     		System.out.println("WeiboInfo中用户信息写入数据库过程貌似有点问题。。。。");
//			Session usersession = HibernateUtil.currentSession();
//			Transaction usinfo = session.beginTransaction();
//			UserInfo user = new UserInfo();						
//			usinfo.commit();
//			HibernateUtil.closeSession();
//     		}	 
 		 	
     		
 	 }
   		
 	 public void commentExtraction(Page page){
 		Selectable commentlist=page.getHtml().xpath("//div[@class='list_box']/*div[@node-type='comment_list']");
 		if (commentlist!=null){
 			
	      		
 			int i=0;
 			
 			for (Selectable comment:commentlist.nodes()){
 				//逐条抽取评论相关信息并写入数据库
 				if(comment!=null){
					
 					//抽取评论ID
 	          	String comment_id = comment
 	      				.xpath("//div[@class='list_li S_line1 clearfix']/@comment_id").get() ;  
 	      		System.out.println("comment_id:"+comment_id);
 	      	
 	      		
 	      	
 	      		
 	      	  //抽取评论作者ID
 	          	String userid0 = comment
 	      				.xpath("//a[@target='_blank']/img[@ucardconf='type=1']/@usercard").get() ;  
 	      		String userid=userid0.replace("id=","");
 	          	System.out.println("userid:"+userid);

 	      		
 	      		//抽取用户主页
 	      		 String userurl = comment.xpath("//div[@class='WB_face W_fl']/a[@target='_blank']/@href").get();
 	      		 System.out.println("抽取用户主页:"+userurl);
 	      		
 	      	   //抽取评论作者当前用户名
 	      		String screenname = comment
 	      				.xpath("//a[@target='_blank']/img[@ucardconf='type=1']/@alt").get() ;
 	      	
 	      		System.out.println("screenname:"+screenname);	
 	      		
 	      		//抽取评论内容
 	      		String comment_text0 = comment
 	      				.xpath("//div[@node-type='replywrap']/div[@class='WB_text']/allText()").get() ;  
 	      		String comment_text=comment_text0.replaceAll(screenname,"");
 	      		System.out.println("comment_text:"+comment_text);
 	      		
 	      		//抽取评论时间
 	      		String comment_time = comment
 	      				.xpath("//div[@class='WB_from S_txt2']/allText()").get() ;  
 	      		System.out.println("comment_time:"+comment_time);
 				
 				
 				//此处将抽取到的数据写入数据库
 	     		Queue<ReplyInfo> rp;	
 	     		if (replyMap.containsKey(comment_id)) {
 	     			
 	     		   //抽取所评论微博ID
 		          	String weiboID =page.getHtml()  //weiboID在comment外抽取
 		          			.xpath("//div[@class='WB_cardwrap WB_feed_type S_bg2 ']/@mid").get();  
 		      		System.out.println("在评论中抽取weiboID:"+weiboID);	
 	     			
 	     			
 					rp = replyMap.get(comment_id);
 					rp = replyMap.get(weiboID); 					
 					rp = replyMap.get(userid);
 					rp = replyMap.get(comment_time);
 					rp = replyMap.get(comment_text);
 					rp = replyMap.get(screenname);
 							
 					
 				} else {
 					rp = new LinkedList<ReplyInfo>();
 					replyMap.put(comment_id, rp);
 				}
 				Session session = HibernateUtil.currentSession();
 				Transaction rpT = session.beginTransaction();
 				ReplyInfo reply = new ReplyInfo(); 							
 				rpT.commit();
 				HibernateUtil.closeSession();
 	 		 
 				
 				//保存微博用户信息到数据库
// 				Queue<UserInfo> us;	
// 	     		
// 	     			us = userMap.get(userid);
// 	     			us = userMap.get(userurl);
// 	     			us = userMap.get(screenname);
// 					
// 				Session usersession = HibernateUtil.currentSession();
// 				Transaction usinfo = session.beginTransaction();
// 				UserInfo user = new UserInfo();						
// 				usinfo.commit();
// 				HibernateUtil.closeSession();
 				}
 				i++;
 				System.out.println(i);
 			}
 		}
 		
 		
 	 }
 	 public List<String> retweettExtraction(Page page){
 		//将所有转发链接抽取加入到请求列表
 		List<String> reUrl=page.getHtml().xpath("//*div[@class='WB_from S_txt2']/a[@node-type='feed_list_item_date']/@href").all();
 		Selectable rtlist=page.getHtml().xpath("//*div[@class='list_box']/div[@node-type='feed_list']");
 		
 		if (rtlist!=null){
 			//抽取转发源ID
			String origin_id = page.getHtml()
					.xpath("//div[@node-type='feedconfig']/div[@class='WB_cardwrap WB_feed_type S_bg2 ']/@mid").get();  
			System.out.println("origin_id:"+origin_id);	
			
 			int i=0;
 			for (Selectable rt:rtlist.nodes()){
 				
 				//逐条抽取转发相关信息并写入数据库
 			    //抽取转发ID
 		        String  forward_id= rt
 						.xpath("//div[@action-type='feed_list_item']/@mid").get() ;
 		        
 				System.out.println("forward_id:"+forward_id);
 					
 				//抽取转发时间
 				String forward_time = rt
 						.xpath("//div[@class='WB_from S_txt2']/a[@node-type='feed_list_item_date']/@title").get() ;  
 				System.out.println("forward_time:"+forward_time);
 				
 				
 				//抽取转发用户id
 				String userid0 = rt
 						.xpath("//div[@class='WB_face W_fl']/a[@target='_blank']/@usercard").get() ; 
 				String  userid=userid0.replace("id=", "");
 				System.out.println("userid:"+userid);
 				
 				//抽取转发用户当前名称
 				String screenname = rt
 						.xpath("//a[@class='WB_text']/a[@node-type='name']/allText()").get() ;  
 				System.out.println("screenname:"+screenname);
 				
 				//抽取转发用户个人主页
 				String userurl = rt
 						.xpath("//div[@class='WB_face W_fl']/a[@target='_blank']/@href").get() ;  
 				System.out.println("userurl:"+userurl);
 				
 				//此处将抽取到的数据写入数据库
 	     		Queue<ForwardInfo> fw;	
 	     		if (forwardMap.containsKey(forward_id)) {
 	     			fw = forwardMap.get(forward_id);
 	     			fw = forwardMap.get(origin_id); 					
 	     			fw = forwardMap.get(forward_time);
 	     			fw = forwardMap.get(userid);
 	     			fw = forwardMap.get(screenname);
 	     			fw = forwardMap.get(userurl);
 							
 					
 				} else {
 					fw = new LinkedList<ForwardInfo>();
 					forwardMap.put(forward_id, fw);
 				}
 				Session session = HibernateUtil.currentSession();
 				Transaction fwT = session.beginTransaction();
 				ForwardInfo reply = new ForwardInfo(); 							
 				fwT.commit();
 				HibernateUtil.closeSession();
 				
 				
 				//保存微博用户信息到数据库
// 				Queue<UserInfo> us;	
// 	     		
// 	     			us = userMap.get(userid);
// 	     			us = userMap.get(userurl);
// 	     			us = userMap.get(screenname);
// 					
//
// 				Session usersession = HibernateUtil.currentSession();
// 				Transaction usinfo = session.beginTransaction();
// 				UserInfo user = new UserInfo();						
// 				usinfo.commit();
// 				HibernateUtil.closeSession();
 			}
 			i++;
				System.out.println(i);
 		}
 		return reUrl;
 	 }
 		
    
    
    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this){
                webDriverPool = new WebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }
}
