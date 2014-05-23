package com.skyeyes.base.cmd;

import java.util.Date;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.util.DateUtil;



public class Constants {
	public enum ChannelUseStatus{
		off,
		on;
		
	}
	
	public enum ChannelVideoStatus{
		on,
		off;
		
	}
	
	public static class ManuCountType{
		public final static int manuNum = 0;
		public final static int avgTime = 1;
	}
	
	public static class ManuCountTimeUnit{
		public final static int second = 0;
		public final static int min = 1;
		public final static int hour = 2;
		public final static int day = 3;
		public final static int monse = 4;
		public final static int year = 5;
	}

	public static Object[] getQueryManuParams(REQUST requst,byte channelId,long beginTime){
    	int queryType = Constants.ManuCountType.manuNum;
    	int countNum = 1;
    	int countDuration = 1;//时长1天
    	int countTimeUnit = Constants.ManuCountTimeUnit.day;//单位天
		switch(requst){
        case cmdReqAllManuByDay://按日统计总人流
        	queryType = Constants.ManuCountType.manuNum;
        	countNum = 1;
        	countDuration = 1;//时长1天
        	countTimeUnit = Constants.ManuCountTimeUnit.day;//单位天
        	break;
        case cmdReqAllManuByMouse://按月统计总人流
        	queryType = Constants.ManuCountType.manuNum;;
        	countNum = 1;
        	countDuration = 1;//时长1月
        	countTimeUnit = Constants.ManuCountTimeUnit.monse;//单位月
        	break;
        case cmdReqAvgHourManuByDay://按日统计每小时人流
        	queryType = Constants.ManuCountType.manuNum;;
        	countNum = 24;
        	countDuration = 1;//时长1小时
        	countTimeUnit = Constants.ManuCountTimeUnit.hour;//单位小时
        	break;
        case cmdReqAvgDayManuByMouse://按月统计每天人流
        	queryType = Constants.ManuCountType.manuNum;;
        	countNum = DateUtil.getDaysOfMonthByDate(new Date(beginTime));
        	countDuration = 1;//时长1天
        	countTimeUnit = Constants.ManuCountTimeUnit.day;
        	break;
        case cmdReqAvgManuStayTimeByDay://按日统计平均驻留时间
        	queryType = Constants.ManuCountType.avgTime;
        	countNum = 1;
        	countDuration = 1;
        	countTimeUnit = Constants.ManuCountTimeUnit.day;
        	break;
        case cmdReqAvgManuStayTimeByMouse://按月统计平均驻留时间
        	queryType = Constants.ManuCountType.avgTime;
        	countNum = 1;
        	countDuration = 1;
        	countTimeUnit = Constants.ManuCountTimeUnit.monse;//单位月
        	break;
		}
		Object[] params = new Object[]{
			1,channelId,
			8,beginTime,
			1,queryType,
			4,countNum,
			4,countDuration,
			1,countTimeUnit
		};
		return params;
	}
	
}
