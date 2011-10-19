package com.guanri.android.lib.utils;

import java.lang.reflect.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class TimeUtils {
	public final static String formatDate = "yyyy-MM-dd";
	public final static String formatTime = "HH:mm:ss";
	public final static String formatHour = "HH";
	public final static String formatMinute = "mm";
	public final static String formatHourMinute = "HH:mm";
	public final static String formatFulltime="yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 根据时间字符串和时间格式获取时间的毫秒数
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


	/**
	 *  获取日期时间 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static String getTimeString(Date time){
		try{
			return getTimeString(time, formatFulltime);
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 *  获取日期对象 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static Date getDateFromString(String time){
		try{
			return getDateFromString(time,formatFulltime);
		}catch(Exception e){
			return null;
		}
	}
	public static Date getDateFromFulltimeString(String time){
		try{
			return getDateFromString(time,formatFulltime);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 *  获取日期时间 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static String getTimeString(Date time,String format){
		try{
			SimpleDateFormat sf = new SimpleDateFormat(format);
			return sf.format(time);
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 *  获取日期对象 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static Date getDateFromString(String time,String format){
		try{
			SimpleDateFormat sf = new SimpleDateFormat(format);
			return sf.parse(time);
		}catch(Exception e){
			return null;
		}
	}
	

	
	

	/**
	 * 比较时间串是否在指定范围  字串格式为yyyy-MM-dd hh:mm:ss
	 * @param objDate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isInTimeRange(String objDate,String startDate,String endDate) {
		boolean isInRange=false;
		Date thisDate=new Date();
		Date startDt=new Date();
		Date endDt=new Date();
		if (startDate.trim().length()>10 && endDate.trim().length()>10) {
			SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				thisDate = curFormater.parse(objDate.trim());
				startDt = curFormater.parse(startDate.trim());
				endDt = curFormater.parse(endDate.trim());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (thisDate.compareTo(startDt)>=0 && thisDate.compareTo(endDt)<=0) {
				isInRange=true;
			}
		}		
		return isInRange;
	}
	/**
	 * 改变时间 Add("yyyy-MM-dd HH:mm:ss",["month"/"day"/"minute"/"second"],int trans)
	 * @param datetime
	 * @param timespan
	 * @param trans
	 * @return
	 */
	public static String changeDateTime(String datetime,String timespan,int trans) { //datetime="2011-03-22 07:21:39"
		String nextDate="";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateNext;
		try {
			dateNext = formatter.parse(datetime);
			Calendar calNextGetup = Calendar.getInstance();
			calNextGetup.setTime(dateNext);	
			if (timespan.equalsIgnoreCase("month")) {
				calNextGetup.add(Calendar.MONTH,trans);
			}
			if (timespan.equalsIgnoreCase("day")) {
				calNextGetup.add(Calendar.DATE,trans);
			}
			if (timespan.equalsIgnoreCase("minute")||timespan.equalsIgnoreCase("min")) {
				calNextGetup.add(Calendar.MINUTE,trans);
			}
			if (timespan.equalsIgnoreCase("second")) {
				calNextGetup.add(Calendar.SECOND,trans);
			}
			nextDate=formatter.format(calNextGetup.getTime().getTime());
		} catch (ParseException e) {
			nextDate=datetime;//原样返回
			e.printStackTrace();
		}
		return nextDate;
	}
	public static Date convertToDate(String dtYYYYmmddAndTime) {
		Date dt=null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (dtYYYYmmddAndTime.trim().length()>10) {
			try {
				dt=new Date();
				dt = formatter.parse(dtYYYYmmddAndTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}				
		return dt;
	}
	public static String changeDateTime(String datetime,int days) { //datetime="2011-05-22"
		String nextDate="";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date dateNext;
		try {
			dateNext = formatter.parse(datetime);
			Calendar calNext = Calendar.getInstance();
			calNext.setTime(dateNext);
			calNext.add(Calendar.DATE,days);
			nextDate=formatter.format(calNext.getTime().getTime());
		} catch (ParseException e) {
			nextDate=datetime;//原样返回
			e.printStackTrace();
		}
		return nextDate;
	}
	/**
	 * 判断两个时间串是否前面的早于后面(或后面时间晚于前面) "yyyy-MM-dd hh:mm:ss"
	 * @param datetime
	 * @param timeAfter
	 * @return
	 */
	public static boolean isRightTimeOrder(String datetime,String timeAfter) { //datetime="2011-03-22 07:21:39"
		boolean isRightOrder=false;
		Date startDt=new Date();
		Date endDt=new Date();
		if (datetime.trim().length()>10 && timeAfter.trim().length()>10) {
			SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				startDt = curFormater.parse(datetime.trim());
				endDt = curFormater.parse(timeAfter.trim());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (startDt.before(endDt)) {
				isRightOrder=true;
			}
		}		
		return isRightOrder;
	}	
	
	/**
	 * 根据出生日期计算年龄 日期格式为 yyyy-MM-dd
	 * @param bithday
	 * @return
	 */
	public static int bithdayToAge(String bithday){
		int age=0;
		try {
			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date=new Date();     
			Date mydate;
			mydate = myFormatter.parse(bithday);
			long day= (date.getTime()-mydate.getTime())/(24*60*60*1000) + 1;
			String agestr = new java.text.DecimalFormat("#0").format(day/365f);
			age  = Integer.valueOf(agestr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return age;
	}
}
