package bdb;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;

public class DBViews {
	private StoredMap<UserIDKey, UserIDData> unused_useridMap = null;
	private StoredMap<UserIDKey, UserIDData> used_useridMap = null;

	public DBViews(DBGenerator db) {
		ClassCatalog catalog = db.getClassCatalog();
		SerialBinding<UserIDKey> useridKey = new SerialBinding<>(catalog,
				UserIDKey.class);
		SerialBinding<UserIDData> useridData = new SerialBinding<>(catalog,
				UserIDData.class);

		unused_useridMap = new StoredMap<>(db.getUnusedUserIDDb(), useridKey,
				useridData, true);
		used_useridMap = new StoredMap<>(db.getUsedUserIDDb(), useridKey,
				useridData, true);
	}

	public StoredMap<UserIDKey, UserIDData> getUnusedUserIDMap() {
		return unused_useridMap;
	}
	public StoredMap<UserIDKey, UserIDData> getUsedUserIDMap(){
		return used_useridMap;
	}
}
