/**
 * 
 */
package cn.edu.nudt.nlp.crawler;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * 消息对象
 * 包括消息唯一id，所属对话，发送者姓名，文本内容，html内容
 * @author Mentor
 *
 */
public class Message {
	private String msgid;
	private String conversation;
	private String username;
	private String content;
	private String html;
	private Date time;
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String mid) {
		this.msgid = mid;
	}
	public String getConversation() {
		return conversation;
	}
	public void setConversation(String conversation) {
		this.conversation = conversation;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgid == null) ? 0 : msgid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (msgid == null) {
			if (other.msgid != null)
				return false;
		} else if (!msgid.equals(other.msgid))
			return false;
		return true;
	}
	public String toString(){
		return "id="+msgid+" dialog="+conversation+" user="+username+" content="+content;
		
	}
	public static void main(String[] args){
		Message m=new Message();
		m.setMsgid("test1");
		m.setConversation("test");
		m.setContent("test");
		m.setHtml("test");
		m.setUsername("test");
		m.setTime(new Date(System.currentTimeMillis()));
		
		SessionFactory sf =
                new Configuration().configure().buildSessionFactory();
            Session session = sf.openSession();
            Transaction tx = session.beginTransaction();

            session.saveOrUpdate(m);
            tx.commit();
            session.close();
	}
	

}
