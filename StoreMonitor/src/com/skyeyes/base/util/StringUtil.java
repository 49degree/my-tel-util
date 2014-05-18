package com.skyeyes.base.util;

import java.util.List;

import android.widget.TextView;

/**
 * 字符串处理帮助类
 * @author
 */
public class StringUtil {

	/**
	 * 字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		if(str==null||str.trim().equals(""))
			return true;
		else
			return false;
	}
	
	/**
	 * TextView是否为空
	 * @param str
	 * @return
	 */
	public static boolean isTextViewNull(TextView text){
		if(text==null||text.getText().toString().trim().equals(""))
			return true;
		else
			return false;
	}
	
	/**
	 * 获取控件TextView的值
	 * @param str
	 * @return
	 */
	public static String getTextViewValue(TextView text){
		if(text==null||text.getText().toString().trim().equals(""))
			return "";
		else
			return text.getText().toString().trim();
	}
	
	
	/**
	 * 将列表转化成字符串
	 * @param list
	 * @return
	 */
	public static String formatListToStr(List<String> list){
		StringBuffer result = new StringBuffer();
		if(list== null || list.size()==0){
			return null;
		}
		for(String s:list){
			result.append(s).append("\n");
		}
		result.deleteCharAt(result.length()-1);
		return result.toString();
	}
	
	/**
	 * 拼接多个String
	 * @param string
	 * @return 返回String类型
	 */
	public static String spliceStrings(String...strings){
		StringBuffer buffer = new StringBuffer();
		for(String s:strings){
			buffer.append(s);
			s = null;
		}
		return buffer.toString();
	}
	
	/**
	 * 拼接List<String>
	 * @return 返回String类型
	 */
	public static String spliceList(List<String> strings,String spliceTag){
		StringBuffer buffer = new StringBuffer();
		int size = strings.size();
		if(0 == size) return null;
		for (int i = 0; i < strings.size(); i++) {
			if (i != 0) {
				buffer.append(spliceTag);
			}
			buffer.append(strings.get(i));
		}
		return buffer.toString();
	}
}
