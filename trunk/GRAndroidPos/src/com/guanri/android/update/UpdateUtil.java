package com.guanri.android.update;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class UpdateUtil { 
	/**
	 * 判断是否有需要升级
	 * @param activity
	 */
	public static void checkUpdate(Activity activity){
		// 初始化参数信息
		String httpServIp = null;//activity.getResources().getString(R.string.http_serv_ip);
		String httpServPort = null;//activity.getResources().getString(R.string.http_serv_port);
		String httpCheckUpdateLink = null;//activity.getResources().getString(R.string.http_check_update_link);

		try{
			PackageInfo packInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			String version = packInfo==null?null:packInfo.versionName;
			if(version==null||"".equals(version)){
				version = "1.0";
			}
			
			StringBuffer requestUrl = new StringBuffer("http://");
			requestUrl.append(httpServIp).append(":").append(httpServPort).append(httpCheckUpdateLink).append("?version=").append(version);
			
			HttpRequest httpRequest = new HttpRequest(requestUrl.toString(), null,activity);
			
			
			JSONObject jo = httpRequest.getResponsJSON();
			
			
			if(jo.getString("success").equals("true")){//有需要升级
				String filePath = jo.getString("filePath");
				String fileName = jo.getString("fileName");
				String updateMsg = "";
				if(!jo.isNull("updateMsg")){
					updateMsg = jo.getString("updateMsg");
				}
				Intent updateIntent = new Intent("com.etelecom.android.iknow.update.Update");
				Bundle bundle = new Bundle();
				bundle.putString("updateMsg", updateMsg);
				bundle.putString("fileName", fileName);
				bundle.putString("filePath", new StringBuffer("http://").append(
						httpServIp).append(":").append(httpServPort).append("/apf/").append(filePath).toString());
				updateIntent.putExtras(bundle);
				activity.startActivity(updateIntent);
			}
		}catch(JSONException e){
			
		}catch(PackageManager.NameNotFoundException ne){
			
		}
	}
}
