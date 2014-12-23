package weibo_preprocess.regx;


/**
 * 创建Regtest类，定义网址（regURL）、表情（regExpression）、@用户（regAt）、主题（regHashtag）、Email（regEmail）、非中文英文数字（regChiEngSimb）、单独长串数字（regDigi）
 *   符号（punc）、（uncommon）正则表达式
 *    			
 */

public class Regtest {
	 //定义正则表达式
	public static String   regURL = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
	 
	public static String   regExpression ="\\[[\\u4e00-\\u9fa5A-Za-z0-9]+\\]";
	public static String	regAt=         "\\@[\\u4e00-\\u9fa5A-Za-z0-9_-]+";
	public static String	regHashtag ="#[\\u4e00-\\u9fa5A-Za-z0-9]+#";
	public static String   regEmail  ="[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+";
	
	public static String  regChiEngSimb ="\\u4e00-\\u9fa5A-Za-z0-9";
	public static String   regDigi ="(?<!["+regChiEngSimb+"\\.:："+
										"])" +
										"[0-9]{7,}" +
										"(?!["+
										regChiEngSimb+"\\.:："+"])";

	public static String punc ="[\\pP‘’“”]";
	public static String uncommon ="["+"^"+regChiEngSimb+punc+" "+"]";
	public static String blank="^s*|s*$"; // 空格
	
}
