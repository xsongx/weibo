package weibo_preprocess;

import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.io.FileWriter;
import weibo_preprocess.regx.Regtest;

/**
 * ������ɾ���������Ϊline��45�������豣�������ţ���ע�͵���45�м���
 */


public class PreTreater {

	private String id;
	private String text;
		
	public String mark;
	public static String FileName = "E:/������.txt";
	
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
		 //������ɾ����䡣��Ҫ���������ţ���ע�͵���������!!!		 
	     //temp = DeleteByPattern(Regtest.uncommon,temp,id,mark); 
		 //temp = DeleteByPattern(Regtest.punc,temp,id,mark);  
		 temp = DeleteByPattern(Regtest.regChiEngSimb,temp,id,mark);
		 
		 System.out.println("ID:"+id);
		 System.out.println("�޸�ǰ:"+text);
		 System.out.println("�޸ĺ�:"+temp);	 
		 AppendToFile(FileName,"ID:"+	id	+"\r\n"	); //+"\r\n"
		 AppendToFile(FileName,"�޸�ǰ:"+text	+"\r\n");
		 AppendToFile(FileName,"�޸ĺ�:"+temp	+"\r\n"); 
		 AppendToFile(FileName,"-----------------------------------------"+"\r\n"); 
	}
	
	public void AppendToFile(String FileName,String content)
	{
		 try {
	            //��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
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
            // System.out.println("���ݸ��³ɹ�!");
        }catch (Exception e) {
            System.out.println("���ݸ���ʧ��!");
        }
	}
	
	
	
	//���Դ���----ok
	
	/**  public static void main(String[] dfe){
		 
		String text = "  ,,,,��������������::���»���__��������--���#һ����#�Ϳ��Գ�#��#~@������ �ҵ�����Բ����zhong.mx@163.com������[����][����][����]���� 123456789123456789 :http://t.cn/zYsPk5D  ";
	    PreTreater pt = new PreTreater();
	    System.out.println("ԭ��----"+text);
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


