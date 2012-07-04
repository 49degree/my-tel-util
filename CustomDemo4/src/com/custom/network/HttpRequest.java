package com.custom.network;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * HTTP请求处理类
 * @author szluyl
 *
 */
public class HttpRequest {
	public static String TAG = "HttpRequest";

	private String requestUrl = null;//请求URL
	private Map<String, String> paramMap = null;//请求参数


	private HttpResponse httpResponse = null;
	private JSONObject responsJSON = null;
	private String responsString = null;

	public HttpRequest(String requestUrl, Map<String, String> paramMap) {
		this.requestUrl = requestUrl;
		this.paramMap = paramMap;

	}

	public JSONObject getResponsJSON(boolean isPost) {
		try{
			if(isPost)
				this.httpPost();
			else
				this.httpGet();
		}catch(Exception e){}
		return this.responsJSON;
	}

	public String getResponsString(boolean isPost ) {
		if (this.responsString == null) {
			getResponsJSON(isPost);
		}
		return responsString;
	}


	private void setResponsJSON(boolean isPost) {
		try {
			// 比较下状态码，看看是否成功
			if (this.responsJSON == null) {
				
				try {
					
					if (httpResponse == null) {
						throw new Exception("请求失败:URL(" + this.requestUrl + ")无法访问！");
					}
					// 比较下状态码，看看是否成功
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						if (this.responsString == null) {
							this.responsString = EntityUtils.toString(httpResponse.getEntity(), HTTP.ISO_8859_1);
							this.responsString=new String(responsString.getBytes("ISO-8859-1"),"GBK");
						}

					} else {
						throw new Exception("请求失败:" + httpResponse.getStatusLine().getStatusCode());
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.responsString = "{\"success\":false,\"type\":1,\"msg\":\"请求失败:URL(" + this.requestUrl + ")无法访问！\"}";
				}
				this.responsJSON = new JSONObject(this.responsString.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				String jsonString = "{\"success\":false,\"msg\":" + e.getMessage() + "}";
				this.responsJSON = new JSONObject(jsonString);
			} catch (JSONException je) {
				je.printStackTrace();
			}
		}finally{
		}
	}
	

	private void httpPost() throws Exception {
		HttpClient httpclient = null;
		try {
			BasicHttpParams httpParameters = new BasicHttpParams();
			// 设置连接超时时间(单位毫秒) 
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			// 设置读数据超时时间(单位毫秒) 
			HttpConnectionParams.setSoTimeout(httpParameters, 120000);
			//连接对象
			httpclient = new DefaultHttpClient(httpParameters);
			//这里设置Url
			HttpPost httppost = new HttpPost(this.requestUrl);
			//设置请求参数
			if (this.paramMap != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(this.paramMap.size());
				for (String key : this.paramMap.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key, this.paramMap.get(key)));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			httpResponse = httpclient.execute(httppost);// 开始执行
			setResponsJSON(true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (ClientProtocolException ce) {
			throw ce;
		}finally{
			try{
				if(httpclient!=null){
					httpclient.getConnectionManager().shutdown();
				}
			}catch(Exception e){
				
			}

			
		}
	}

	
	private void httpGet() throws Exception {
		HttpClient client = null;
        try {
        	StringBuffer bufferParas = new StringBuffer();
            client = new DefaultHttpClient();
           
			//设置请求参数
			if (this.paramMap != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(this.paramMap.size());
				for (String key : this.paramMap.keySet()) {
					if(bufferParas.length()==0)
						bufferParas.append("?");
					bufferParas.append(key).append("=").append(this.paramMap.get(key)).append("&");
				}
			}
			 HttpGet get = new HttpGet(this.requestUrl+bufferParas.toString());
			 httpResponse = client.execute(get);
			 setResponsJSON(false);
        } catch (Exception e) {
            e.printStackTrace();
		}finally{
			try{
				if(client!=null){
					client.getConnectionManager().shutdown();
				}
			}catch(Exception e){
				
			}
		}
	}
	
}
