package com.xys.ecg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimesUtils {

	/**
	 * ����ʱ���ַ�����ʱ���ʽ��ȡʱ��ĺ�����
	 * @param time
	 * @param timeFormat
	 * @return
	 */
	public static long string2TimeMill(String time,String timeFormat){
		try{
			SimpleDateFormat sf = new SimpleDateFormat(timeFormat);
			return sf.parse(time).getTime();
		}catch(ParseException e){
			return 0L;
		}

	}
}
