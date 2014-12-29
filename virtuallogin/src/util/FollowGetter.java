package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import catcher.VirtualLogin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class FollowGetter {
	private VirtualLogin login = null;
	private HttpGet start = null;
	private static String baseFilename = "follow";
	private static int pageCount = 1;

	public FollowGetter(VirtualLogin login, String uniquedId) {
		this.login = login;
		start = new HttpGet("http://weibo.com/" + uniquedId + "/myfollow");
	}

	public void startGetFollows(ArrayList<String> uid) throws ParserException,
			JsonParseException, JsonMappingException, IOException {
		login.execute(start);
		login.getResponseAsFile("follow1");
		Map<String, String> map = null;
		try {
			map = ScriptInfoGetter.getScriptInfo("follow1.html",
					"pl_relation_myfollow");
		} catch (Exception e) {
		}
		if (map != null) {
			Parser parser = new Parser();
			parser.setInputHTML(map.get("html"));
			NodeList facelist = parser
					.extractAllNodesThatMatch(new HasAttributeFilter("class",
							"S_func1"));
			SimpleNodeIterator it = facelist.elements();
			while (it.hasMoreNodes()) {
				Node n = it.nextNode();
				if (n.getClass().getName()
						.equals("org.htmlparser.tags.LinkTag")) {
					LinkTag tag = (LinkTag) n;
					uid.add(tag.getAttribute("usercard"));
				}
			}
			parser.setInputHTML(map.get("html"));
			NodeList nextpage = parser
					.extractAllNodesThatMatch(new HasAttributeFilter("class",
							"W_pages_minibtn"));
			if (nextpage != null&&nextpage.size()>0) {
				NodeList pagelist = nextpage.elementAt(0).getChildren();
				for (int i = 0; i < pagelist.size(); i++) {
					Node node = pagelist.elementAt(i);
					if (node.getClass().getName()
							.equals("org.htmlparser.tags.LinkTag")) {
						LinkTag pagelink = (LinkTag) node;
						if (pagelink.getAttribute("class").equals("W_btn_c")) {
							String link = pagelink.getLink();
							String btcontent = pagelink.toPlainTextString();
							getMoreFollows(link, btcontent, uid);
						}
					}
				}
			}
		}
	}

	public void getMoreFollows(String link, String btcontent,
			ArrayList<String> uid) throws ParserException {
		if (btcontent.equals("ÏÂÒ»Ò³")) {
			HttpGet get = new HttpGet("http://weibo.com" + link);
			login.execute(get);
			int page = getPageCount();
			login.getResponseAsFile(baseFilename + page);
			Map<String, String> map = null;
			try {
				map = ScriptInfoGetter.getScriptInfo(baseFilename + page
						+ ".html", "pl_relation_myfollow");
			} catch (Exception e) {
			}
			if (map != null) {
				Parser parser = new Parser();
				parser.setInputHTML(map.get("html"));
				NodeList facelist = parser
						.extractAllNodesThatMatch(new HasAttributeFilter(
								"class", "S_func1"));
				SimpleNodeIterator it = facelist.elements();
				while (it.hasMoreNodes()) {
					Node n = it.nextNode();
					if (n.getClass().getName()
							.equals("org.htmlparser.tags.LinkTag")) {
						LinkTag tag = (LinkTag) n;
						uid.add(tag.getAttribute("usercard"));
					}
				}
				parser.setInputHTML(map.get("html"));
				NodeList nextpage = parser
						.extractAllNodesThatMatch(new HasAttributeFilter(
								"class", "W_pages_minibtn"));
				if (nextpage != null&&nextpage.size()>0) {
					NodeList pagelist = nextpage.elementAt(0).getChildren();
					for (int i = 0; i < pagelist.size(); i++) {
						Node node = pagelist.elementAt(i);
						if (node.getClass().getName()
								.equals("org.htmlparser.tags.LinkTag")) {
							LinkTag pagelink = (LinkTag) node;
							if (pagelink.getAttribute("class")
									.equals("W_btn_c")) {
								getMoreFollows(pagelink.getLink(),
										pagelink.toPlainTextString(), uid);
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
