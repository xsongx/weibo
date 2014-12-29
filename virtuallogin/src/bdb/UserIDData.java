package bdb;

import java.io.Serializable;

public class UserIDData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3798044851629110031L;
	private boolean flag;
	public UserIDData(boolean flag){
		this.flag=flag;
	}
	public boolean isUsed(){
		return flag;
	}
	public String toString(){
		if(flag)
			return "true";
		return "false";
	}
}
