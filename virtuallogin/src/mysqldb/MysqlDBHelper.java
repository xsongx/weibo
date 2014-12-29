package mysqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MysqlDBHelper {
	private Connection conn = null;
	private PreparedStatement prestatfans = null;
	private PreparedStatement prestatfollow = null;
	private PreparedStatement prestatinfo = null;

	public MysqlDBHelper() {
		conn = ConnectinFactory.getFactory().getConnection();
		try {
			prestatfans = conn
					.prepareStatement("insert into userfans(userid,fanid) values(?,?);");
			prestatfollow = conn
					.prepareStatement("insert into userfollows(userid,followid) values(?,?);");
			prestatinfo = conn
					.prepareStatement("insert into userinfo(userid) values(?);");
		} catch (SQLException e) {
		}
	}

	public void insertFans(String id, ArrayList<String> fansid) {
		for (String fid : fansid) {
			try {
				prestatfans.setString(1, id);
				prestatfans.setString(2, fid.substring(3));
				prestatfans.execute();
			} catch (SQLException e) {
			}
		}
	}

	public void insertFollows(String id, ArrayList<String> followsid) {
		for (String fid : followsid) {
			try {
				prestatfollow.setString(1, id);
				prestatfollow.setString(2, fid.substring(3));
				prestatfollow.execute();
			} catch (SQLException e) {
			}
		}
	}

	public void insertInfo(String id) {
		try {
			prestatinfo.setString(1, id);
			prestatinfo.execute();
		} catch (SQLException e) {
		}
	}
	public void closeMysql(){
		try {
			prestatfans.close();
			prestatfollow.close();
			prestatinfo.close();
			conn.close();
		} catch (SQLException e) {
		}
	}
}
