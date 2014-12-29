package bdb;

import java.io.Serializable;

public class UserIDKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4888838375695039313L;
	private String id=null;
	public UserIDKey(String id){
		this.id=id;
	}
	public String getID(){
		return id;
	}
	public String toString(){
		return id;
	}
}
