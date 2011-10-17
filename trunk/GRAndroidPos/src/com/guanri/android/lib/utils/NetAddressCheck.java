package com.guanri.android.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络地址合法性检查
 * @author wuxiang
 *
 */
public class NetAddressCheck {
	/**
	 * IP地址合法性检查
	 * @param str
	 * @return
	 */
	public static boolean isValidIPAddress(String str) {
		String temp = "";
		int tag = 0;
		if (str.charAt(0) == '.' || str.charAt(str.length() - 1) == '.')
			return false;
		for (int i = 0; i < str.length(); i++) {

			if (str.charAt(i) == '.') {
				tag++;
				if (Integer.parseInt(temp) > 255)
					return false;
				temp = "";
				continue;
			}
			if (str.charAt(i) < '0' || str.charAt(i) > '9')
				return false;
			temp += String.valueOf(str.charAt(i));
		}
		if (tag != 3)
			return false;
		if (Integer.parseInt(temp) > 255)
			return false;
		return true;
	}
	
	/**
	 * MAC地址格式检查
	 * @param str
	 * @return
	 */
	public static boolean isValidMACAddress(String str){
		String regEx = "[a-z]*[A-Z]*[0-9]*";		
		if(str.length() != 17) {
			return false;
		}    //"12:as:dq:we:12:3r";
		if((str.substring(2, 3).equals(":"))&& (str.substring(5, 6).equals(":"))
				&&(str.substring(8, 9).equals(":")) &&(str.substring(11,12).equals(":"))
				&&(str.substring(14, 15).equals(":"))){
			if((str.substring(0,2).replaceAll(regEx, "").length()==0)
					&&(str.substring(3,5).replaceAll(regEx, "").length()==0)
					&&(str.substring(6,8).replaceAll(regEx, "").length()==0)
					&&(str.substring(9,11).replaceAll(regEx, "").length()==0)
					&&(str.substring(12,14).replaceAll(regEx, "").length()==0)
					&&(str.substring(15,17).replaceAll(regEx, "").length()==0)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	/**
	 * 网络状态检查
	 * @param mActivity Activity
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity mActivity) {
		
		Context context = mActivity.getApplicationContext();
		ConnectivityManager connectivity =(ConnectivityManager)
			context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if(info != null) {
				for(int i=0; i<info.length;i++){
					if(info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
		
	}
}
