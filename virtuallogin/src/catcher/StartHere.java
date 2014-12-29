package catcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import mysqldb.MysqlDBHelper;

import org.htmlparser.util.ParserException;

import util.FansGetter;
import util.FollowGetter;
import util.UserFansGetter;
import util.UserFollowGetter;
import bdb.DBHelper;
import beans.CallResult;

public class StartHere {
	public static void main(String[] args) {
		DBHelper bdbhelper=new DBHelper();
		MysqlDBHelper mysqlhelper=new MysqlDBHelper();
		ArrayList<String> initialid = new ArrayList<>();
		VirtualLogin login = VirtualLogin.getLogin();
		CallResult result = login.login();
		String uinitialid = result.getUniqueid();
		FansGetter fansgetter = new FansGetter(login, uinitialid);
		FollowGetter followgetter = new FollowGetter(login, uinitialid);
		try {
			fansgetter.startGetFans(initialid);
			followgetter.startGetFollows(initialid);
		} catch (ParserException | IOException e) {
		}

		bdbhelper.storeID(initialid);
		String id=bdbhelper.getID();
		while (id!=null) {
			UserFansGetter ufg = new UserFansGetter(login);
			try {
				ufg.startGetFans(bdbhelper,mysqlhelper,id);
			} catch (ParserException | IOException e) {
				bdbhelper.closeBDB();
				mysqlhelper.closeMysql();
				System.exit(0);
			}
			UserFollowGetter uflg = new UserFollowGetter(login);
			try {
				uflg.startGetFollow(bdbhelper,mysqlhelper,id);
			} catch (ParserException | IOException e) {
				bdbhelper.closeBDB();
				mysqlhelper.closeMysql();
				System.exit(0);
			}
			File f = new File("tmp");
			if (f.isDirectory()) {
				File[] flist = f.listFiles();
				for (File tf : flist)
					tf.delete();
			}
//			ArrayList<String> unid=bdbhelper.getAllUnusedID();
//			System.out.println(unid.size());
//			ArrayList<String> usid=bdbhelper.getAllUsedID();
//			System.out.println(usid.size());
			
			// UserInfoGetter infogetter=new UserInfoGetter(login);
			// try {
			// WeiboUserInfo
			// info=infogetter.getUserInfo(initialid.get(0).substring(3));
			// System.out.println(info);
			// } catch (Exception e) {
			// }
			id=bdbhelper.getID();
		}
//		bdbhelper.closeBDB();
//		mysqlhelper.closeMysql();
	}
}
