package com.guanri.android.lib.utils;

import java.util.Map;

import com.guanri.android.lib.context.MainApplication;

public class StringUtils {
	/**
	 * 解析字符串返回一个字符串二维数组
	 * 1=身份证;2=护照;3=军官证;4=驾照;5=户口本;6=学生证;7=工作证;8=社保号;0=其他
	 * [1][身份证]
	 * [2][护照]
	 * [3][军官证]
	 * .....
	 */																		 
	public static String[][] splitString(String inStr,String firststr,String secondstr ){
		
		String[] str = stringAnalytical(inStr,firststr);
		String[][] strresult = new String[str.length][];
		for (int i = 0; i < str.length; i++) {
			strresult[i] = stringAnalytical(str[i],secondstr);
		}
		return strresult;
	}
	/**
	 * 带分隔符的字符串解析
	 * "1=男;2=女"--;-->{"1=男","2=女"}
	 * @param string 待解析的字符串
	 * @param c 分割符
	 * @return 解析后的字符串数组
	 */
	public static String[] stringAnalytical(String string, String c) {
		String[] strArray = string.split(c);
        return strArray;
    }
	
	
	/**
	 * 获取配置文件中的字符串
	 */
	public static String getStringFromValue(int resId) {
		String str = MainApplication.getInstance().getString(resId);
        return str;
    }
}
