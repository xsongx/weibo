package weibo_preprocess;

import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.io.FileWriter;
import weibo_preprocess.regx.Regtest;

/**
 * 标点符号删除处理语句为line（45），若需保留标点符号，请注释掉第45行即可
 */


public class PreTreater {

	private String id;
	private String text;
		
	public String mark;
	public static String FileName = "E:/处理结果.txt";
	
	public PreTreater()
	{
		
	}
	
	public PreTreater(String id,String text) {  		
		this.id =id;
		this.text =text;		
		//Delete(id,text);
	}

	public void Delete(String id,String text)
	{
		 String temp = text; 		 
		 
		 temp = DeleteByPattern(Regtest.regHashtag,temp,id,mark);		 
		 temp = DeleteByPattern(Regtest.regExpression,temp,id,mark);		 
		 temp = DeleteByPattern(Regtest.regURL,temp,id,mark);		 
		 temp = DeleteByPattern(Regtest.regAt,temp,id,mark); 
		 temp = DeleteByPattern(Regtest.regEmail,temp,id,mark);
		 temp = DeleteByPattern(Regtest.regChiEngSimb,temp,id,mark); 
		 temp = DeleteByPattern(Regtest.regDigi,temp,id,mark); 
		 //标点符号删除语句。若要保留标点符号，请注释掉下面两行!!!		 
	     //temp = DeleteByPattern(Regtest.uncommon,temp,id,mark); 
		 //temp = DeleteByPattern(Regtest.punc,temp,id,mark);  
		 temp = DeleteByPattern(Regtest.regChiEngSimb,temp,id,mark);
		 
		 System.out.println("ID:"+id);
		 System.out.println("修改前:"+text);
		 System.out.println("修改后:"+temp);	 
		 AppendToFile(FileName,"ID:"+	id	+"\r\n"	); //+"\r\n"
		 AppendToFile(FileName,"修改前:"+text	+"\r\n");
		 AppendToFile(FileName,"修改后:"+temp	+"\r\n"); 
		 AppendToFile(FileName,"-----------------------------------------"+"\r\n"); 
	}
	
	public void AppendToFile(String FileName,String content)
	{
		 try {
	            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
	            FileWriter writer = new FileWriter(FileName, true);
	            writer.write(content);
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }  
	}
		
	
	public  String DeleteByPattern(String regex,String text,String id,String mark)
	{   
		 
		FindUpdateStr(regex,text,id);
		{
		if(regex.equals(Regtest.regExpression))
		mark= "&EP" ;	
		
		else if (regex.equals(Regtest.regHashtag))
		mark="&HT"  ;
		
		else if  (regex.equals(Regtest.regURL))
		mark="&URL" ;
		
		else if (regex.equals(Regtest.regAt))
		mark="&UN"; 
		else 
		mark=""; 
		}		
		return text.replaceAll(regex, mark) ;
	}
	
	
	public  void FindUpdateStr(String regex,String text,String id)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(text); 
		String str = "";
		while(m.find())
		{
			str += m.group();
		} 
		if(regex.equals(Regtest.regExpression))
			UpdateExpression(str,id);
					
		else if(regex.equals(Regtest.regHashtag))
			UpdateHashtag(str,id);
			
		else if(regex.equals(Regtest.regURL))
			UpdateURL(str,id);
			
		else if(regex.equals(Regtest.regAt))
			UpdateUsername(str,id);
	}
	
	
	public void UpdateExpression(String str,String id)
	{ 
		String sql =  "update  temptable set Expression='"+str+"' where id="+id ; 
		Update(sql);
		
	}
	public void UpdateHashtag(String str,String id)
	{
		String sql =  "update  temptable set Hashtag='"+str+"' where id="+id ; 
		Update(sql);
		
	}
	public void UpdateURL(String str,String id)
	{
		String sql =  "update  temptable set URL='"+str+"' where id="+id ; 
		Update(sql);
		
	}
	public void UpdateUsername(String str,String id)
	{
		String sql =  "update  temptable set Username='"+str+"' where id="+id ; 
		Update(sql);
		
	}
		
	public void Update(String sql)
	{
		try {
			Statement stmt = SqlConnect.GetConnect().createStatement();
            stmt.executeUpdate(sql);
            // System.out.println("数据更新成功!");
        }catch (Exception e) {
            System.out.println("数据更新失败!");
        }
	}
	
	
	
	//测试代码----ok
	
	/**  public static void main(String[] dfe){
		 
		String text = "  ,,,,，，，，，”：::“下划线__————--午觉#一醒来#就可以吃#晚饭#~@包青天 我的人生圆满了zhong.mx@163.com！！！[害羞][害羞][害羞]我在 123456789123456789 :http://t.cn/zYsPk5D  ";
	    PreTreater pt = new PreTreater();
	    System.out.println("原文----"+text);
	    String id = "1";
        text = pt.DeleteByPattern(Regtest.regExpression, text,id);
        System.out.println("url---"+pt.DeleteByPattern(Regtest.regURL,text,id)); 
        System.out.println("hashtag---"+pt.DeleteByPattern(Regtest.regHashtag,text,id));
        System.out.println("at---"+pt.DeleteByPattern(Regtest.regAt,text,id));              
        System.out.println("emial----"+pt.DeleteByPattern(Regtest.regEmail,text,id));  		
        System.out.println("ChiEngSimb----"+pt.DeleteByPattern(Regtest.regChiEngSimb,text,id));
        System.out.println("regDigi----"+pt.DeleteByPattern(Regtest.regDigi,text,id));
        System.out.println("punc----"+pt.DeleteByPattern(Regtest.punc,text,id));
        System.out.println("uncommon----"+pt.DeleteByPattern(Regtest.uncommon,text,id)); 
                   
	}
	*/
}


