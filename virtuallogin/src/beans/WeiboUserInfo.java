package beans;

public class WeiboUserInfo {
	private String userid=null;
	private String pickname=null;
	private String sex=null;
	private String description=null;
	private String address=null;
	public WeiboUserInfo(){
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPickname() {
		return pickname;
	}
	public void setPickname(String pickname) {
		this.pickname = pickname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("id : "+userid+'\n');
		sb.append("Í«≥∆ : "+pickname);
		return sb.toString();
	}
}
