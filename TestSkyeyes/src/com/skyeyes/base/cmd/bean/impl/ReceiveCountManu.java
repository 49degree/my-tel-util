package com.skyeyes.base.cmd.bean.impl;

import java.util.ArrayList;

import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveCountManu  extends ReceiveCmdBean {
	public ArrayList<CountManuResultBean> countManuResultBeans = new ArrayList<CountManuResultBean>();
	

	
	@Override
	public void parseBody(byte[] body) throws CommandParseException {

		if(body.length>=17){
			for(int i=0;i<body.length/17;i++){
				CountManuResultBean countManuResultBean = new CountManuResultBean();
				countManuResultBean.channelId = body[i*17+0];
				countManuResultBean.time = DateUtil.fileTime2Date(TypeConversion.bytesToLong(body,i*17+1)).getTime();
				countManuResultBean.inManu = TypeConversion.bytesToInt(body,i*17+9);//查询人流时
				countManuResultBean.avgTime = TypeConversion.bytesToInt(body,i*17+9);//查询驻留时间时
				countManuResultBean.outManu = TypeConversion.bytesToInt(body,i*17+13);
				countManuResultBeans.add(countManuResultBean);
			}
		}
		
	}
	
	public static class CountManuResultBean{
		public byte channelId;
		public long time;
		public int inManu;
		public int avgTime;
		public int outManu;
		public int dayofWeet;
		public String toString(){
			return "channelId="+channelId+";time="+time+";inManu="+inManu+";avgTime="+avgTime+";outManu="+outManu;
		}
	}

	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("[");
		for(CountManuResultBean countManuResultBean:countManuResultBeans){
			buffer.append("{");
			buffer.append(countManuResultBean.toString());
			buffer.append("};");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
