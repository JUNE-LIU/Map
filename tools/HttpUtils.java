package com.zhongqihong.tools;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 *  
 * 封装get和post方式请求 
 * HttpClient 维护session 
 * 一个HttpClient对象代表一个浏览器 
 * 高度封装了客户端与服务端交互的，可以直接返回一个JSONObject对象 
 * */
public class HttpUtils {
	private static 	JSONObject jsonObjects;
	private static String entityString;
	private static HttpResponse res=null;
	private static String APPKEY="46rWE3gZRHcmKzmrLEbH6XmoyjYLFYoY";
	private static String outputForm="json";
	//URL接口地址首部 
	private static String Head="http://api.map.baidu.com/place/v2/search?ak=";
	private static String IP=null;
	private static int pageSize=20;//一次返回数据总条数 
	private static String mcode="BC:ED:06:37:9E:F4:2A:37:32:0E:3A:E6:EC:D0:4A:10:5A:72:25:5C;com.zhongqihong.mymap";
	private static HttpClient clients=new DefaultHttpClient();
	@SuppressWarnings("unused")
	public static JSONObject send(String destination,List<NameValuePair> params) throws ClientProtocolException, IOException, JSONException{

		if (params==null) {//表示发送get方式请求，否则就发送Post发送请求(因为get方式不需要params) 
			IP=Head+APPKEY+"&output="+outputForm+"&query="+destination+"&page_size="+pageSize+"&page_num=0&scope=1&region=����&mcode="+mcode;
			System.out.println("IP_Address:--------->"+IP);
			HttpGet get=new HttpGet(IP);
			res=clients.execute(get);
		}else{
			HttpPost post=new HttpPost(IP);
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			res=clients.execute(post);
		}
		if (res.getStatusLine().getStatusCode()==200) {
			HttpEntity entity=res.getEntity();
			entityString=EntityUtils.toString(entity,HTTP.UTF_8);
			System.out.println("httpUtils--------------->"+entityString);
			jsonObjects=new JSONObject(entityString);
		}
		return jsonObjects;
	}
	public static String getEntityString() {
		return entityString;
	}
	public static void setEntityString(String entityString) {
		HttpUtils.entityString = entityString;
	}
}
