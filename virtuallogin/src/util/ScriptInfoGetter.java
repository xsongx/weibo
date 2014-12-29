package util;

import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ScriptInfoGetter {
	public static final String fileDir="tmp/";
	public static Parser parser=new Parser();
	public synchronized static Map<String,String> getScriptInfo(String fileName,String pid) throws Exception{
		parser.setResource(fileDir+fileName);
		NodeList list=parser.extractAllNodesThatMatch(new NodeFilter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean accept(Node node) {
				if(node.getText().equals("script"))
					return true;
				else
					return false;
			}
		});
		ObjectMapper mapper=new ObjectMapper();
		Map<String,String> map=null;
		for(int i=0;i<list.size();i++){
			String s=list.elementAt(i).toPlainTextString();
			@SuppressWarnings("unchecked")
			Map<String,String> tempmap=mapper.readValue(s.substring(s.indexOf("(")+1,s.lastIndexOf("")),Map.class);
			if(tempmap.get("pid").equals(pid)){
				map=tempmap;
				break;
			}
		}
		return map;
	}
}
