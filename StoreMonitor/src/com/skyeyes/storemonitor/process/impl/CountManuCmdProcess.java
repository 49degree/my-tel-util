package com.skyeyes.storemonitor.process.impl;

import java.util.ArrayList;
import java.util.Date;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu;
import com.skyeyes.base.cmd.bean.impl.ReceiveCountManu.CountManuResultBean;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;

public class CountManuCmdProcess<T extends ReceiveCmdBean> extends DeviceReceiveCmdProcess<T>{
	protected REQUST requst;
	protected String beginTime;//
	long beginTimeDate;
	public CountManuCmdProcess(REQUST requst,String beginTime){
		this.requst = requst;
		this.beginTime = beginTime;
		Date d = DateUtil.pareStringDate(beginTime, DateUtil.TIME_FORMAT_YMDHMS);
		if(d==null)
			beginTimeDate = 0;
		else
			beginTimeDate = d.getTime();
	}

	@Override
	public void onProcess(T receiveCmdBean) {
		if(receiveCmdBean instanceof ReceiveCountManu){
			ReceiveCountManu cmdBean = (ReceiveCountManu)receiveCmdBean;
			// TODO Auto-generated method stub
			ArrayList<CountManuResultBean> countManus = cmdBean.countManuResultBeans;
			switch(requst){
		        case cmdReqAllManuByDay://按日统计总人流
		        case cmdReqAllManuByMouse://按月统计总人流
		        	if(countManus.size()==0){
		        		CountManuResultBean countManuResultBean = new CountManuResultBean();
		        		countManuResultBean.time = beginTimeDate;
		        		countManuResultBean.inManu = 0;
		        		countManuResultBean.outManu = 0;
		        		countManus.add(countManuResultBean);
		        	}
		        	break;
		        case cmdReqAvgHourManuByDay://按日统计每小时人流
		        	int getCount = 0;
		        	if(countManus.size()<24){
		        		ArrayList<CountManuResultBean> countManuResultBeans = new ArrayList<CountManuResultBean>();
		        		for(int i=1;i<=24;i++){
		        			if(getCount<countManus.size()){
		        				CountManuResultBean countManuResultBean = countManus.get(getCount);
		        				if(countManuResultBean.time==beginTimeDate+i*60*60*1000L){
		        					countManuResultBeans.add(countManuResultBean);
		        					getCount++;
		        					continue;
		        				}
		        			}
	        				CountManuResultBean countManuResultBean = new CountManuResultBean();
	        				countManuResultBean.time = beginTimeDate+i*60*60*1000L-1L;
	                		countManuResultBean.inManu = 0;
	    	        		countManuResultBean.outManu = 0;
	    	        		countManuResultBeans.add(countManuResultBean);
		        		}
		        		cmdBean.countManuResultBeans = countManuResultBeans;
		        	}
		        	
		        	break;
		        case cmdReqAvgDayManuByMouse://按月统计每天人流
		        	int days = DateUtil.getDaysOfMonthByDate(new Date(beginTimeDate));
		        	getCount = 0;
		        	if(countManus.size() < days){
		        		ArrayList<CountManuResultBean> countManuResultBeans = new ArrayList<CountManuResultBean>();
		        		for(int i=1;i<=days;i++){
		        			if(getCount<countManus.size()){
		        				CountManuResultBean countManuResultBean = countManus.get(getCount);
		        				if(countManuResultBean.time==beginTimeDate+i*24*60*60*1000L){
		        					countManuResultBean.dayofWeet = DateUtil.getDayOfWeekByDate(new Date(countManuResultBean.time));
		        					countManuResultBeans.add(countManuResultBean);
		        					getCount++;
		        					continue;
		        				}
		        			}
	        				CountManuResultBean countManuResultBean = new CountManuResultBean();
	        				countManuResultBean.time = beginTimeDate+i*24*60*60*1000L-1L;
	        				//System.out.println(DateUtil.getTimeStringFormat(countManuResultBean.time, DateUtil.TIME_FORMAT_YMDHMS)+":"+countManuResultBean.time);
	                		countManuResultBean.inManu = 0;
	    	        		countManuResultBean.outManu = 0;
	    	        		countManuResultBean.dayofWeet = DateUtil.getDayOfWeekByDate(new Date(countManuResultBean.time));
	    	        		countManuResultBeans.add(countManuResultBean);
		        		}
		        		cmdBean.countManuResultBeans = countManuResultBeans;
		        	}	
		        	
		        	break;
		        case cmdReqAvgManuStayTimeByDay://按日统计平均驻留时间
		        case cmdReqAvgManuStayTimeByMouse://按月统计平均驻留时间	
		        	if(countManus.size()==0){
		        		CountManuResultBean countManuResultBean = new CountManuResultBean();
		        		countManuResultBean.time = beginTimeDate;
		        		countManuResultBean.avgTime = 0;
		        		countManus.add(countManuResultBean);
		        	}
		        	break;
			}			
		}


//		for(CountManuResultBean countManuResultBean:receiveCmdBean.countManuResultBeans){
//			System.out.println(DateUtil.getTimeStringFormat(countManuResultBean.time, DateUtil.TIME_FORMAT_YMDHMS)+":"
//					+countManuResultBean.dayofWeet+":"+countManuResultBean.inManu+":"+countManuResultBean.outManu+":"+countManuResultBean.avgTime);
//		}
		
		
	
	}

	@Override
	public void onFailure(String errinfo) {
		// TODO Auto-generated method stub
		
	}

}
