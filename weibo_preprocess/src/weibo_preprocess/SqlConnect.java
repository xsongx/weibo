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
 * �������ݿ��ļ�����ȡid�ֶκ�text�ֶν��д���
 *  
 * �����text�ֶ��г�ȡ����������ͱ��飬�����ݿ��д���Hashtag��Expression�ֶ�д�������Ϣ��
 * 
 * Ϊ������������ָ��range��������¼���д���
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
			System.out.println("��������ʧ��");
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
			        //����MySQL ���ݿ����ӵ�ַ
			
			stmt = conn.createStatement();
			
			//	����ָ��index����¼���д���	
			long index=0;
			while(index<300){
				
				writer  = new BufferedWriter(new FileWriter("id and text"+index+".txt" ));
				
				//while(index)
				rs = stmt.executeQuery("select * from temptable limit  "+ index +", "+range);
				index+=range;
				
				while(rs.next()){
					//�����ݿ�temptable���ж�ȡ�ֶΡ�id��ֵ��text��ֵ
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
		 System.out.println("---sqlConnectִ�����---");
		 
	}
}
