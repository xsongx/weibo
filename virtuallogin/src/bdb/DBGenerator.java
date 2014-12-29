package bdb;

import java.io.File;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class DBGenerator {
	private Environment env;
	private static final String CLASS_CATALOG="java_class_catalog";
	private static final String UNUSED_USERID_STORE="unused_userid_store";
	private static final String USED_USERID_STORE="used_userid_store";
	private static final String DBLOCATION="bdb";
	private StoredClassCatalog javaCatalog;
	private Database unused_useridDb;
	private Database used_useridDb;
	public DBGenerator(){
		System.out.println("数据库建立中...");
		
		EnvironmentConfig envConfig=new EnvironmentConfig();
		envConfig.setTransactional(false);
		envConfig.setAllowCreate(true);
		
		env=new Environment(new File(DBLOCATION),envConfig);
		
		DatabaseConfig dbConfig=new DatabaseConfig();
		dbConfig.setTransactional(false);
		dbConfig.setAllowCreate(true);
		Database catalogDb=env.openDatabase(null, CLASS_CATALOG, dbConfig);
		javaCatalog=new StoredClassCatalog(catalogDb);
		
		unused_useridDb=env.openDatabase(null,UNUSED_USERID_STORE, dbConfig);
		used_useridDb=env.openDatabase(null,USED_USERID_STORE,dbConfig);
	}
	public void close(){
		unused_useridDb.close();
		used_useridDb.close();
		javaCatalog.close();
		env.close();
	}
	public final Environment getEnvironment() {
		return env;
	}
	public final StoredClassCatalog getClassCatalog() {
		return javaCatalog;
	}
	public final Database getUnusedUserIDDb(){
		return unused_useridDb;
	}
	public final Database getUsedUserIDDb(){
		return used_useridDb;
	}
}
