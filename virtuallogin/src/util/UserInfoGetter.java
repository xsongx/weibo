package util;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import beans.WeiboUserInfo;
import catcher.VirtualLogin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserInfoGetter {
	private VirtualLogin login = null;
	private HttpGet start = null;

	public UserInfoGetter(VirtualLogin login) {
		this.login = login;
	}

	public WeiboUserInfo getUserInfo(String uniquedId) throws ParserException,
			JsonParseException, JsonMappingException, IOException {
		start = new HttpGet("http://weibo.com/" + uniquedId + "/info");
		login.execute(start);
		login.getResponseAsFile("userinfo");
		Map<String, String> map = null;
		try {
			map = ScriptInfoGetter.getScriptInfo("userinfo.html",
					"pl_profile_infoBase");
		} catch (Exception e) {
		}
		if (map != null) {
			WeiboUserInfo info=new WeiboUserInfo();
			info.setUserid(uniquedId);
			Parser parser = new Parser();
			parser.setInputHTML(map.get("html"));
			NodeList infoList=parser.extractAllNodesThatMatch(new HasAttributeFilter("class","pf_item clearfix"));
			for(int i=0;i<infoList.size();i++){
				Div div=(Div) infoList.elementAt(i);
				SimpleNodeIterator it=div.children();
				while(it.hasMoreNodes()){
					Node node=it.nextNode();
					if(node.getClass().getName().equals("org.htmlparser.tags.Div")){
						Div d=(Div) node;
						String s=d.toPlainTextString().trim();
						System.out.println(s);
//						if(s.contains("昵称"))
//							info.setPickname(s.replace("昵称",""));
//						else if(s.contains("所在地"))
//							info.setAddress(s.replace("所在地",""));
//						else if(s.contains("性别"))
//							info.setSex(s.replace("性别",""));
//						else if(s.contains("简介"))
//							info.setDescription(s.replace("简介",""));
					}
				}
			}
			return info;
		}
		return null;
	}
}
