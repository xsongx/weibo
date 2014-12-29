package beans;

public class CallbackForm {
	private int retcode;
	private int servertime;
	private String pcid;
	private String nonce;
	private String pubkey;
	private String rsakv;
	private int exectime;
	public int getRetcode() {
		return retcode;
	}
	public void setRetcode(int retcode) {
		this.retcode = retcode;
	}
	public int getServertime() {
		return servertime;
	}
	public void setServertime(int servertime) {
		this.servertime = servertime;
	}
	public String getPcid() {
		return pcid;
	}
	public void setPcid(String pcid) {
		this.pcid = pcid;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getPubkey() {
		return pubkey;
	}
	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}
	public String getRsakv() {
		return rsakv;
	}
	public void setRsakv(String rsakv) {
		this.rsakv = rsakv;
	}
	public int getExectime() {
		return exectime;
	}
	public void setExectime(int exectime) {
		this.exectime = exectime;
	}
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append("retcode: "+this.getRetcode()+"\n");
		sb.append("servertime: "+this.getServertime()+"\n");
		sb.append("pcid: "+this.getPcid()+"\n");
		sb.append("nonce: "+this.getNonce()+"\n");
		sb.append("pubkey: "+this.getPubkey()+"\n");
		sb.append("rsakv: "+this.getRsakv()+"\n");
		sb.append("exectime: "+this.getExectime()+"\n");
		return sb.toString();
	}
}
