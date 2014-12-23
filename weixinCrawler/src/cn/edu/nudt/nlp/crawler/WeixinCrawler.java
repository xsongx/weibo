/**
 * 
 */
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

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.selenium.WebDriverPool;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * 抓取微信网页版的对话信息;<br>
 * 需要用户手动过登陆界面。 判断已经登陆后，会根据用户提供的昵称抓取指定聊天内容，如果未指定，则不加区分的抓取所有新聊天。
 * 
 * 
 * @author Mentor
 * 
 */
public class WeixinCrawler implements PageProcessor {
	private Logger logger = Logger.getLogger(getClass());
	// 抓取相关参数
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
			.setUseGzip(true);
	private String url = "https://wx.qq.com";
	private boolean needUpdate = false;
	private WebDriver webDriver;
	private volatile WebDriverPool webDriverPool;
	private int sleepTime = 2000;
	// 存储相关参数
	private HashMap<String, Queue<Message>> messageMap = new HashMap<String, Queue<Message>>();
	private int queueSize = 100;

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public WeixinCrawler(String chromeDriverPath) {
		System.getProperties().setProperty("webdriver.chrome.driver",
				chromeDriverPath);
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
	public void process(Page page) {
		String name = page.getHtml()
				.xpath("//*[@id='profile']/div[1]/div[2]/span/allText()").get();
		if (name != null) {
			logger.debug("login in as user:" + name);
			Selectable dialog = page.getHtml().xpath(
					"//div[@id='chat_chatmsglist']");
			if (dialog != null) {
				// nickname 不对，必须按照activeColumn取
				Selectable st = page
						.getHtml()
						.xpath("//div[@id='conversationContainer']/div[@class='chatListColumn activeColumn']");
				String nickName = st
						.xpath("//div[@class='info']/div[@class='nickName']/div[@class='left name']/allText()")
						.get();

				// String
				// nickname=page.getHtml().xpath("//p[@id='messagePanelTitle']/allText()").get();

				this.getMessageFromDialog(dialog.get(), nickName);

			}
			Selectable st = page
					.getHtml()
					.xpath("//div[@id='conversationContainer']/div[@class='chatListColumn']");
			if (st != null) {
				List<Selectable> friendlist = st.nodes();
				if (friendlist != null && !friendlist.isEmpty()) {
					for (Selectable friend : friendlist) {

						String unreadInfo = friend
								.xpath("//span[@class='unreadDot' and @style!='display: none;']/allText()")
								.get();
						if (unreadInfo != null) {
							unreadInfo = unreadInfo.trim();

							if (!"0".equals(unreadInfo)
									&& !"null".equalsIgnoreCase(unreadInfo)) {
								String nickName = friend
										.xpath("//div[@class='info']/div[@class='nickName']/div[@class='left name']/allText()")
										.get();
								String id = friend.xpath(
										"/div[@class='chatListColumn']/@id")
										.get();
								logger.debug("find unread message from:" + id
										+ " nickname is:" + nickName
										+ " unreadinfo is:" + unreadInfo);
								this.getMessages(id, nickName);

							}
						}
					}
					needUpdate = true;
				}
			}
		} else {
			String str = page.getHtml()
					.xpath("//div[@class='waiting panelContent']/allText()")
					.get();
			logger.debug(str);

			if (str != null && str.contains("请使用微信扫描二维码")) {
				logger.debug("Waiting the user login by the phone");
				needUpdate = true;
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				logger.error("Wrong page info ,please check the network status.");
				needUpdate = false;
			}
		}

		// TODO Auto-generated method stub

	}

	public void getMessages(String id, String nickname) {
		try {
			webDriver.findElement(By.xpath("//div[@id='" + id + "']")).click();

			Thread.sleep(1000);

			WebElement we = webDriver.findElement(By
					.xpath("//div[@id='chat_chatmsglist']"));
			this.getMessageFromDialog(we.getAttribute("outerHTML"), nickname);
		} catch (InterruptedException e) {
			logger.error(e);
		} catch (NoSuchElementException e) {
			logger.error(e);
		} catch (WebDriverException e) {
			logger.error(e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void getMessageFromDialog(String dialogRawText, String dialogName) {
		Html dialog = new Html(dialogRawText);
		Selectable stlist = dialog.xpath("//div[@class='chatItemContent']");
		if (stlist != null) {
			Queue<Message> q;
			if (messageMap.containsKey(dialogName)) {
				q = messageMap.get(dialogName);
			} else {
				q = new LinkedList<Message>();
				messageMap.put(dialogName, q);
			}
			Session session = HibernateUtil.currentSession();
			Transaction tx = session.beginTransaction();
			for (Selectable st : stlist.nodes()) {
				String username = st.css("img", "username").get();
				if (username == null || username.trim() == "") {
					username = st.css("img", "title").get();
				}
				if (username == null) {
					logger.debug("Can't find username from element:" + st.get());
					username = dialogName;
				}
				String msgid = st.xpath("//div[@msgid]/@msgid").get();
				if (msgid == null || msgid.trim() == "") {
					logger.error("Can't find msgid from element:" + st.get());
					continue;
				}
				// 图片暂时不管
				String content = st.xpath(
						"//div[@class='cloudContent']/allText()").get();
				Message mess = new Message();
				mess.setTime(new Date(System.currentTimeMillis()));
				mess.setMsgid(msgid);
				mess.setUsername(username);
				mess.setContent(content);
				mess.setConversation(dialogName);
				mess.setHtml(st.get());
				if (!q.contains(mess)) {
					q.add(mess);
					logger.debug(mess);
					try {
						session.save(mess);
					} catch (ConstraintViolationException e) {
						logger.error("message " + mess.toString()
								+ " is aready exists in the database!");
						logger.error(e);

					} catch (DataException e) {
						logger.error("message " + mess.toString()
								+ " insert exception!");
						logger.error(e);
					}

					if (q.size() > this.queueSize) {
						q.remove();
					}
				}
			}
			tx.commit();
			HibernateUtil.closeSession();
		} else
			logger.debug("Can not find the div of chatItemContent,dialog is null");

	}

	public boolean running() {
		return needUpdate;
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return this.site;
	}

	public String getDefaultUrl() {
		return url;
	}

	public static void main(String[] args) {
		String chromepath="d:\\chromedriver.exe";
		if(args!=null && args.length>0)
			chromepath=args[0];
      
		WeixinCrawler wc = new WeixinCrawler(chromepath);
		Page p1 = wc.download(new Request(wc.getDefaultUrl()), wc.getSite());
		if (p1 == null)
			System.exit(0);
		wc.process(p1);
		while (wc.running()) {
			p1 = wc.update();
			if (p1 != null)
				wc.process(p1);
		}
		wc.close();

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

	public Page update() {
		try {
			Thread.sleep(sleepTime);

			logger.info("downloading page " + webDriver.getCurrentUrl());

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
					webDriver.getCurrentUrl())));
			page.setUrl(new PlainText(webDriver.getCurrentUrl()));
			page.setRequest(new Request(webDriver.getCurrentUrl()));
			return page;
		} catch (InterruptedException e) {
			logger.error("Interrupted", e);
			this.needUpdate = false;
		} catch (WebDriverException e) {
			this.needUpdate = false;
			logger.error("WebDriverException", e);
		}
		return null;
	}

	public void close() {
		webDriverPool.closeAll();
		HibernateUtil.closeSession();
	}

}
