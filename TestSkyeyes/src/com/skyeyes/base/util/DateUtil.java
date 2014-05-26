package com.skyeyes.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理帮助类
 * 
 * @author chao.xu
 */
public class DateUtil {
	// 时间格式
	
	public final static String TIME_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	public final static String TIME_FORMAT_YMDHM = "yyyy-MM-dd HH:mm";
	public final static String TIME_FORMAT_YMD = "yyyy-MM-dd";
	public final static String TIME_FORMAT_YM = "yyyy-MM";
	public final static String TIME_FORMAT_HM = "HH:mm";
	
	   /**
     * Unix 时间 1970-01-01 00:00:00 与 Win32 FileTime 时间 1601-01-01 00:00:00
     * 毫秒数差 
     */
    public final static long UNIX_FILETIME_DIFF = 11644473600000L;
     
    /**
     * Win32 FileTime 采用 100ns 为单位的，定义 100ns 与 1ms 的倍率
     */
    public final static int MILLISECOND_MULTIPLE = 10000;
    

	   /**
     * 将 Win32 的 FileTime 结构转为 Java 中的 Date 类型
     * @param fileTime
     * @return
     */
    public static Date fileTime2Date(long fileTime) {
        return new Date(fileTime / MILLISECOND_MULTIPLE - UNIX_FILETIME_DIFF);
    }
    
    /**
     * 将 Java 中的 Date 类型转为 Win32 的 FileTime 结构
     * @param date
     * @return
     */
    public static long date2FileTime(Date date) {
        return (UNIX_FILETIME_DIFF + date.getTime()) * MILLISECOND_MULTIPLE;
    }

	
    public static Date pareStringDate(String dateStr,String format){
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    	try {
			return simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * 计算某日期所在月份的天数
     * @param date
     * @return
     */
	public static int getDaysOfMonthByDate(Date date) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date); 
		int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}
    
    /**
     * 计算某日期是星期几
     * @param date
     * @return
     */
	public static int getDayOfWeekByDate(Date date) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date); 
		int day = rightNow.get(Calendar.DAY_OF_WEEK);
		if(day>1){
			return day-1;
		}else
			return 7;
	}
	
	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param dateFormat
	 * @return String
	 */
	public static String getTimeStringFormat(Date date, String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param dateFormat
	 * @return String
	 */
	public static String getTimeStringFormat(long date, String dateFormat) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(date);
	}

	public static String getTimeStringFormat(Calendar cal, String dateFormat) {
		return getTimeStringFormat(cal.getTime(), dateFormat);
	}

	/**
	 * 得到默认时间的日期字符串
	 * 
	 * @param dateFormat
	 * @return String
	 */
	public static String getDefaultTimeStringFormat(String dateFormat) {
		return getTimeStringFormat(new Date(), dateFormat);
	}

	/**
	 * 得到相隔day的日期
	 * 
	 * @param date
	 * @param day
	 * @return Date
	 */
	public static Date operationDate(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setTimeInMillis(c.getTimeInMillis() + ((long) day) * 24 * 3600 * 1000);
		return c.getTime();
	}

	/**
	 * 第day天的日期
	 * 
	 * @param c
	 * @param day
	 * @return
	 */
	public static Calendar getCalendarByNumDay(Calendar c, int day) {
		Calendar cc = Calendar.getInstance();
		cc.setTimeInMillis(c.getTimeInMillis() + ((long) day) * 24 * 3600 * 1000);
		return cc;
	}

	/**
	 * 解析日期
	 * 
	 * @param dateString
	 * @param day
	 * @return
	 */
	public static String parseDateString(String dateString, int day) {
		Calendar c = DateUtil.getCalendarByString(dateString, "yyyy-MM-dd");
		Calendar whenCalendar = DateUtil.getCalendarByNumDay(c, day);
		return DateUtil.getDateTimeStringFormat(whenCalendar);
	}

	/**
	 * 根据字符串得到Calendar
	 * 
	 * @param dateString
	 *            "2012-12-13"
	 * @param dateFormat
	 *            yyyy-MM-dd
	 * @return
	 */
	public static Calendar getCalendarByString(String dateString, String dateFormat) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(dateFormat).parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		return c;
	}

	public static String getDateTimeStringFormat(Calendar date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_YMD);
		return simpleDateFormat.format(date.getTime());
	}

	public static String getDateTimeStringFormatHhMm(Calendar date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT_HM);
		return simpleDateFormat.format(date.getTime());
	}



	public static String getSystemDateFormat(int year, int month, int day) {
		String nMonth, nDay;
		if (month < 9) {
			nMonth = "0" + (month + 1);
		} else {
			nMonth = "" + (month + 1);
		}
		if (day < 10) {
			nDay = "0" + day;
		} else {
			nDay = "" + day;
		}
		return year + "-" + nMonth + "-" + nDay;
	}

	/**
	 * 将格式为 yyyy-MM-dd HH:mm:ssss 的时间 转成 yyyy-MM-dd HH:mm
	 */
	public static String getSimpleDateString(String date) {
		return null == date ? "" : date.substring(0, 16);
	}

	/**
	 * 将格式为 yyyy-MM-dd HH:mm:ssss 的时间 转成 HH:mm
	 */
	public static String getSimpleHhMmDateString(String date) {
		return null == date ? "" : date.substring(11, 16);
	}

	/**
	 * 获得指定日期的前N天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */

	public static Calendar getSpecifiedDayBeforeNumDay(String specifiedDay, int beforeDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(TIME_FORMAT_YMD).parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - beforeDay);
		return c;
	}
	
	/**
	 * 一个月前的日期
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */

	public static String getFrontMonthDateString() {
		Calendar c = Calendar.getInstance();
		if(c.get(Calendar.MONTH)==0){
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)-1);
			c.set(Calendar.MONTH, 11);
		}else
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
		return getTimeStringFormat(c.getTime().getTime(),TIME_FORMAT_YMD);
	}

	/**
	 * 获得指定日期的后N天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static Calendar getSpecifiedDayAfterNumDay(String specifiedDay, int beforeDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat(TIME_FORMAT_YMD).parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + beforeDay);
		return c;
	}

	/** 判断是否为当天日期 */
	public static boolean isToday(String date) {
		DateFormat df = new SimpleDateFormat(TIME_FORMAT_YMD);
		try {
			Date dt1 = df.parse(date);
			Date dt2 = df.parse(getDefaultTimeStringFormat(TIME_FORMAT_YMD));
			if (dt1.getTime() > dt2.getTime()) {
				return false;
			} else if (dt1.getTime() < dt2.getTime()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args){
		Object[] params = new Object[] {DateUtil.getFrontMonthDateString()+" 00:00:00",
				DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS)};
		System.out.println(params[0]+":"+params[1]);
	}


}
 