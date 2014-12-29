package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import mysqldb.MysqlDBHelper;

import org.apache.http.client.methods.HttpGet;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import bdb.DBHelper;
import catcher.VirtualLogin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserFollowGetter {
	private VirtualLogin login = null;
	private HttpGet start = null;
	private static String refer = "http://weibo.com";
	private static String baseFilename = "userfollow";
	private static int pageCount = 1;

	public UserFollowGetter(VirtualLogin login) {
		this.login = login;
	}

	public void startGetFollow(DBHelper bdbhelper, MysqlDBHelper mysqlhelper,
			String uniquedId) throws ParserException, JsonParseException,
			JsonMappingException, IOException {
		ArrayList<String> list = new ArrayList<String>();
		start = new HttpGet("http://weibo.com/" + uniquedId + "/follow");
		login.execute(start);
		login.getResponseAsFile("userfollow1");
		Map<String, String> map = null;
		try {
			map = ScriptInfoGetter.getScriptInfo("userfollow1.html",
					"pl_relation_hisFollow");
		} catch (Exception e) {
		}
		if (map != null) {
			Parser parser = new Parser();
			parser.setInputHTML(map.get("html"));
			NodeList facelist = parser
					.extractAllNodesThatMatch(new HasAttributeFilter("class",
							"W_f14 S_func1"));
			SimpleNodeIterator it = facelist.elements();
			while (it.hasMoreNodes()) {
				Node n = it.nextNode();
				if (n.getClass().getName()
						.equals("org.htmlparser.tags.LinkTag")) {
					LinkTag tag = (LinkTag) n;
					list.add(tag.getAttribute("usercard"));
				}
			}
			bdbhelper.storeID(list);
			mysqlhelper.insertFollows(uniquedId, list);
			parser.setInputHTML(map.get("html"));
			NodeList nextpage = parser
					.extractAllNodesThatMatch(new HasAttributeFilter("class",
							"W_pages W_pages_comment"));
			if (nextpage != null && nextpage.size() > 0) {
				NodeList pagelist = nextpage.elementAt(0).getChildren();
				for (int i = 0; i < pagelist.size(); i++) {
					Node node = pagelist.elementAt(i);
					if (node.getClass().getName()
							.equals("org.htmlparser.tags.LinkTag")) {
						LinkTag pagelink = (LinkTag) node;
						if (pagelink.getAttribute("class").equals("W_btn_c")) {
							String link = pagelink.getLink();
							String btcontent = pagelink.toPlainTextString();
							getMoreFollow(uniquedId, link, btcontent.trim(),
									bdbhelper, mysqlhelper);
							UserFollowGetter.pageCount = 1;
						}
					}
				}
			}
		}
	}

	public void getMoreFollow(String uniquedId, String link, String btcontent,
			DBHelper bdbhelper, MysqlDBHelper mysqlhelper)
			throws ParserException {
		if (btcontent.equals("ÏÂÒ»Ò³")) {
			ArrayList<String> list = new ArrayList<String>();
			HttpGet get = new HttpGet(refer + link);
			login.execute(get);
			int page = getPageCount();
			login.getResponseAsFile(baseFilename + page);
			Map<String, String> map = null;
			try {
				map = ScriptInfoGetter.getScriptInfo(baseFilename + page
						+ ".html", "pl_relation_hisFollow");
			} catch (Exception e) {
			}
			if (map != null) {
				Parser parser = new Parser();
				parser.setInputHTML(map.get("html"));
				NodeList facelist = parser
						.extractAllNodesThatMatch(new HasAttributeFilter(
								"class", "W_f14 S_func1"));
				SimpleNodeIterator it = facelist.elements();
				while (it.hasMoreNodes()) {
					Node n = it.nextNode();
					if (n.getClass().getName()
							.equals("org.htmlparser.tags.LinkTag")) {
						LinkTag tag = (LinkTag) n;
						list.add(tag.getAttribute("usercard"));
					}
				}
				bdbhelper.storeID(list);
				mysqlhelper.insertFollows(uniquedId, list);
				parser.setInputHTML(map.get("html"));
				NodeList nextpage = parser
						.extractAllNodesThatMatch(new HasAttributeFilter(
								"class", "W_pages W_pages_comment"));
				if (nextpage != null && nextpage.size() > 0) {
					NodeList pagelist = nextpage.elementAt(0).getChildren();
					for (int i = 0; i < pagelist.size(); i++) {
						Node node = pagelist.elementAt(i);
						if (node.getClass().getName()
								.equals("org.htmlparser.tags.LinkTag")) {
							LinkTag pagelink = (LinkTag) node;
							if (pagelink.getAttribute("class")
									.equals("W_btn_c")) {
								getMoreFollow(uniquedId, pagelink.getLink(),
										pagelink.toPlainTextString().trim(),
										bdbhelper, mysqlhelper);
							}
						}
					}
				}
			}
		}
		return;
	}

	public static int getPageCount() {
		return ++pageCount;
	}
}
