package weibo_preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * 连接数据库文件，读取id字段和text字段进行处理。
 *  
 * 处理后将text字段中抽取出来的主题和表情，在数据库中创建Hashtag、Expression字段写入相关信息。
 * 
 * 为便于灵活处理，采用指定range个数量记录进行处理
 *   
 */

public class SqlConnect {
	public static Connection GetConnect()
	{
		Connection conn = null;
		try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/zmx?user=root&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000");
		}
		catch(Exception e)
		{
			System.out.println("创建连接失败");
		}
		return conn;
	}
	public static void mysqlConnection(){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		final int range=100;
		
		BufferedWriter writer= null;
	
		try {
			
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/zmx?user=root&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000");
			        //本机MySQL 数据库连接地址
			
			stmt = conn.createStatement();
			
			//	读入指定index条记录进行处理	
			long index=0;
			while(index<300){
				
				writer  = new BufferedWriter(new FileWriter("id and text"+index+".txt" ));
				
				//while(index)
				rs = stmt.executeQuery("select * from temptable limit  "+ index +", "+range);
				index+=range;
				
				while(rs.next()){
					//从数据库temptable表中读取字段“id”值“text”值
					String id = rs.getString("id");
					String text = rs.getString("text");
							
					PreTreater pt = new PreTreater(id, text);					
					pt.Delete(id, text);
					
				}//end while
				
				writer.close();
				while (rs.next()) {
				
					System.out.println(rs.getString("text"));
								
				} 
				 
			
			}//end while
			
			
			

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if(rs != null) {
					rs.close();
					rs = null;
				}
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
  
	public static void main(String[] dfe){
		 String FileName = PreTreater.FileName;
		 File file = new File(FileName);
		 if(file.exists())
			 file.delete();
		 mysqlConnection();
		 System.out.println("---sqlConnect执行完毕---");
		 
	}
}
