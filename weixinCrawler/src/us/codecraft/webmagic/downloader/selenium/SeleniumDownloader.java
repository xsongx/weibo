package us.codecraft.webmagic.downloader.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//import cn.edu.nudt.nlp.crawler.ConfigUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

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
		webDriver.get(url);
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
    
    @Override
    public Page download(Request request, Task task) {
        checkInit();
        WebDriver webDriver;
        String url=request.getUrl();
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
					   webDriver.get(url);
				       try {
				            Thread.sleep(sleepTime);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
				        WebElement webElement = webDriver.findElement(By.xpath("/html"));
				        String content = webElement.getAttribute("outerHTML");
				        Page page = new Page();
				        page.setRawText(content);
				        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
				        page.setUrl(new PlainText(request.getUrl()));
				        page.setRequest(request);
				        webDriverPool.returnToPool(webDriver);
				        return page;
					}
			}
			catch(NoSuchElementException e) {
				logger.info("Already login!");
			}        

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        webDriverPool.returnToPool(webDriver);
        return page;
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
