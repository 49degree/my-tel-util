package com.szxys.mhub.subsystem.mets.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.szxys.mhub.R;
import com.szxys.mhub.app.MhubApplication;

public class TimeUtils {
	public final static String format = "yyyy年MM月dd日 HH:mm";
	public final static String format1 = "yyyy年MM月dd日 HH时mm分";
	public final static String format2 = "yyyy-MM-dd HH:mm";
	public final static String format3 = "yyyy年MM月dd日 HH:mm:ss";
	public final static String format4 = "yyyy年MM月dd日 HH时mm分ss秒";
	public final static String formatDate = "yyyy年MM月dd日";
	public final static String formatDate2 = "yyyy-MM-dd";
	public final static String formatHour = "HH";
	public final static String formatMinute = "mm";
	public final static String formatFulltime="yyyy-MM-dd hh:mm:ss";
	/**
	 *  获取日期时间 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static String getTimeString(Date time){
		try{
			return TimeUtils.getTimeString(time, format);
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
			return TimeUtils.getDateFromString(time,format);
		}catch(Exception e){
			return null;
		}
	}
	public static Date getDateFromFulltimeString(String time){
		try{
			return TimeUtils.getDateFromString(time,formatFulltime);
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
	 *  获取日期对象 yyyy年MM月dd日 HH:mm
	 * @param time
	 * @return
	 */
	public static String getWeekTimeFromString(String time,String format){
		try{
			SimpleDateFormat sf = new SimpleDateFormat(format);
			int dayOfWeek = sf.parse(time).getDay();
			Context context = MhubApplication.getInstance();
			
			Class newoneClass = Class.forName("com.szxys.mhub.R$string");
			Field field = newoneClass.getField("mets_weekly_day"+dayOfWeek);
			String dayOfWeekString = context.getString(Integer.parseInt(String.valueOf(field.get(newoneClass))));;
			return dayOfWeekString;	
		}catch(Exception e){
			return "";
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
}
