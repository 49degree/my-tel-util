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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

/**
 * HTTP请求处理类
 * @author szluyl
 *
 */
public class HttpRequest {
	public static String TAG = "HttpRequest";

	private String requestUrl = null;//请求URL
	private Map<String, String> paramMap = null;//请求参数

	private Context context = null;// Activity对象

	private HttpResponse httpResponse = null;
	private JSONObject responsJSON = null;
	private String responsString = null;

	public HttpRequest(String requestUrl, Map<String, String> paramMap, Context context) {
		this.context = context;
		this.requestUrl = requestUrl;
		this.paramMap = paramMap;

	}

	public String getResponsString(boolean isPost) {
		try{
			if(isPost)
				this.httpPost();
			else
				this.httpGet();
		}catch(Exception e){}
		return responsString;
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
			
			// 比较下状态码，看看是否成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (this.responsString == null) {
					this.responsString = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
					this.responsString=new String(responsString.getBytes("UTF-8"),"GBK");
				}
			}
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
			// 比较下状态码，看看是否成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					if (this.responsString == null) {
						this.responsString = EntityUtils.toString(httpResponse.getEntity(), HTTP.ISO_8859_1);
						this.responsString=new String(responsString.getBytes("ISO-8859-1"),"GBK");
					}
			}
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
	/**
	 * 验证网络是否可用
	 * @return
	 */
	public static boolean checkInternet(Activity activity) {
		Log.d("haveInternet", "check network connect");
		//activity = null;//模拟器测试不判断网络状况
		if (activity != null) {
			boolean flag = false;
			ConnectivityManager cwjManager = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network = cwjManager.getActiveNetworkInfo();
			if (network != null) {
				flag = network.isAvailable();
			} else {
				NetworkInfo[] info = cwjManager.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						Log.d("haveInternet", "connect state:" + info[i].getTypeName() + ":" + info[i].getState());
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
			Log.d("haveInternet", "connect state:" + flag);
			return flag;
		}
		return true;
	}

	public static void changeNetwork(Handler handler, final Activity activity) {
		handler.post(new Runnable() {
			public void run() {
				Builder b = new AlertDialog.Builder(activity).setTitle("没有可用的网络").setMessage("请开启GPRS或WIFI网络连接");
				b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent mIntent = new Intent("/");
						ComponentName comp = new ComponentName("com.android.settings",
								"com.android.settings.WirelessSettings");
						mIntent.setComponent(comp);
						mIntent.setAction("android.intent.action.VIEW");
						activity.startActivity(mIntent);
						System.exit(0);//推出程序
					}
				}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
						System.exit(0);//推出程序
					}
				}).create();
				b.show();
			}
		});
	}

}