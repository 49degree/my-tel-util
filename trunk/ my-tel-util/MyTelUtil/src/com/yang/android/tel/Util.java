package com.yang.android.tel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class Util {
	public static final String PREFS_NAME = "MyRefuseTel";  
	
	/**
	 * 获取电话接口
	 */
	public static ITelephony getIPhone(TelephonyManager tManager) {
		ITelephony iTelephony = null;
		// 初始化iTelephony
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iTelephony ;
	}
	
	public static String mapToString(Map<String,String> params){
		StringBuffer paramsStr = new StringBuffer("{");
		for(String key:params.keySet()){
			paramsStr.append(paramsStr.length()==1?"":",");
			paramsStr.append("'").append(key).append("':'").append(params.get(key)==null?"":params.get(key).trim()).append("'");
		}
		paramsStr.append("}");
		return paramsStr.toString();
	}
}
