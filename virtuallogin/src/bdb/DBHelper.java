package bdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.sleepycat.collections.StoredMap;

public class DBHelper {
	private DBGenerator generator = null;
	private DBViews view = null;
	private StoredMap<UserIDKey, UserIDData> unusedmap = null;
	private StoredMap<UserIDKey, UserIDData> usedmap = null;

	public DBHelper() {
		generator = new DBGenerator();
		view = new DBViews(generator);
		unusedmap = view.getUnusedUserIDMap();
		usedmap = view.getUsedUserIDMap();
	}
	public void storeID(ArrayList<String> list){
		for(String userid :list){
			UserIDKey key=new UserIDKey(userid.substring(3));
			if(usedmap.containsKey(key)){
				continue;
			}
			else{
				unusedmap.put(key, new UserIDData(true));
			}
		}
	}
	public String getID(){
		Iterator<UserIDKey> it=unusedmap.keySet().iterator();
		if(it.hasNext()){
			UserIDKey key=it.next();
			String id=key.getID();
			it.remove();
			usedmap.put(key, new UserIDData(true));
			return id;
		}
		else
			return null;
	}
	public ArrayList<String> getAllUnusedID(){
		Set<UserIDKey> set=unusedmap.keySet();
		Iterator<UserIDKey> it=set.iterator();
		ArrayList<String> list=new ArrayList<String>();
		while(it.hasNext()){
			UserIDKey key=it.next();
			list.add(key.getID());
		}
		return list;
	}
	public ArrayList<String> getAllUsedID(){
		Set<UserIDKey> set=usedmap.keySet();
		Iterator<UserIDKey> it=set.iterator();
		ArrayList<String> list=new ArrayList<String>();
		while(it.hasNext()){
			UserIDKey key=it.next();
			list.add(key.getID());
		}
		return list;
	}
	public void closeBDB(){
		generator.close();
	}
}
