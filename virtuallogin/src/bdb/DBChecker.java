package bdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DBChecker {
	public static void main(String[] args) throws FileNotFoundException {
		DBHelper dbhelper=new DBHelper();
		System.out.println(dbhelper.getAllUnusedID().size());
		System.out.println(dbhelper.getAllUsedID().size());
		PrintWriter out=new PrintWriter(new File("userid.txt"));
		for(String s : dbhelper.getAllUsedID())
			out.println(s);
		out.flush();
		out.close();
		dbhelper.closeBDB();
	}
}
