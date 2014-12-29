package catcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import beans.CallResult;
import beans.CallbackForm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VirtualLogin {
	public static final String LOGIN_S1_URL = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&callback=sinaSSOController.preloginCallBack&su=dW5kZWZpbmVk&rsakt=mod&client=ssologin.js(v1.4.2)";
	public static final String LOGIN_S2_URL = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.2)";
	public static final String LOGIN_S3_URL = "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack";
	private HttpClient client = null;
	private HttpResponse response = null;
	private HttpUriRequest request = null;
	private static VirtualLogin login=null;

	private VirtualLogin(HttpClient client) {
		this.client = client;
	}

	public static VirtualLogin getLogin() {
		if (login == null)
			login=new VirtualLogin(new DefaultHttpClient());
		return login;
	}

	public HttpClient getClient() {
		return this.client;
	}

	public CallResult login() {
		login.getClient()
				.getParams()
				.setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BEST_MATCH);
		ObjectMapper mapper = new ObjectMapper();

		HttpGet step1 = new HttpGet(LOGIN_S1_URL);
		login.execute(step1);
		String tempContent = login.getResponseBodyAsString();
		String content = tempContent.substring(tempContent.indexOf("(") + 1,
				tempContent.lastIndexOf(""));
		login.abort();
		CallbackForm form1 = null;
		try {
			form1 = mapper.readValue(content, CallbackForm.class);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}

		NameEncodeJSRunner njsrunner = new NameEncodeJSRunner();
		PwdEncodeJSRunner pwdjsrunner = new PwdEncodeJSRunner();
		String su = njsrunner.executeFunction("lolee_k@163.com");
		String sp = pwdjsrunner.executeFunction(form1, "6233053");
		HttpPost post = new HttpPost(VirtualLogin.LOGIN_S2_URL);
		List<NameValuePair> postform = new ArrayList<NameValuePair>();
		postform.add(new BasicNameValuePair("entry", "weibo"));
		postform.add(new BasicNameValuePair("gateway", "1"));
		postform.add(new BasicNameValuePair("from", ""));
		postform.add(new BasicNameValuePair("savestate", "7"));
		postform.add(new BasicNameValuePair("useticket", "1"));
		postform.add(new BasicNameValuePair("vsnf", "1"));
		postform.add(new BasicNameValuePair("ssosimplelogin", "1"));
		postform.add(new BasicNameValuePair("su", su));
		postform.add(new BasicNameValuePair("service", "miniblog"));
		postform.add(new BasicNameValuePair("servertime", String.valueOf(form1
				.getServertime())));
		postform.add(new BasicNameValuePair("nonce", String.valueOf(form1
				.getNonce())));
		postform.add(new BasicNameValuePair("pwencode", "rsa2"));
		postform.add(new BasicNameValuePair("rsakv", String.valueOf(form1
				.getRsakv())));
		postform.add(new BasicNameValuePair("sp", sp));
		postform.add(new BasicNameValuePair("encoding", "UTF-8"));
		postform.add(new BasicNameValuePair("prelt", "685"));
		postform.add(new BasicNameValuePair("url", VirtualLogin.LOGIN_S3_URL));
		postform.add(new BasicNameValuePair("returntype", "META"));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(postform, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		post.setEntity(entity);
		login.execute(post);
		tempContent = login.getResponseBodyAsString();
		
		content = tempContent.substring(tempContent.indexOf("replace") + 9,
				tempContent.indexOf("\");") - 1);
		login.abort();

		HttpGet step3 = new HttpGet(content);
		login.execute(step3);
		tempContent = login.getResponseBodyAsString();
		content = tempContent.substring(tempContent.indexOf("(") + 1,
				tempContent.lastIndexOf(""));

		CallResult result = null;
		try {
			result = mapper.readValue(content, CallResult.class);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		
		login.abort();
		
		return result;
	}

	public void execute(HttpUriRequest request) {
		this.request = request;
		try {
			this.response = client.execute(request);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
	}

	public HttpResponse getResponse() {
		return this.response;
	}

	public String getResponseBodyAsString() {
		BufferedReader reader;
		StringBuffer sb = new StringBuffer();
		String s = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			while ((s = reader.readLine()) != null)
				sb.append(s);
		} catch (IllegalStateException e1) {
		} catch (IOException e1) {
		}
		return sb.toString();
	}

	public void abort() {
		this.request.abort();
	}
	
	public void getResponseAsFile(String name) {
		try {
			FileOutputStream out = new FileOutputStream(new File("tmp/"+name+".html"));
			InputStream in = response.getEntity().getContent();
			byte[] b = new byte[4096];
			int length = 0;
			while ((length = in.read(b)) != -1){
				out.write(b, 0, length);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
		}
	}
}
