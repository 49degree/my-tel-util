package com.skyeyes.base.cmd.bean.impl;

import java.util.HashMap;

import com.skyeyes.base.cmd.Constants;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;

/**
 * 获取设备通道状态
 * 
 * 可知设备有多少通道
 * @author Administrator
 *
 */
public class ReceiveDeviceChannelListStatus extends ReceiveCmdBean{
	public HashMap<Integer,ChannelStatus> mChannelListStatus = new HashMap<Integer,ChannelStatus>(); 
	
	public static class ChannelStatus{
		Constants.ChannelUseStatus useStatus;
		Constants.ChannelVideoStatus videoStatus;
		ChannelStatus(Constants.ChannelUseStatus useStatus,Constants.ChannelVideoStatus videoStatus){
			this.useStatus = useStatus;
			this.videoStatus = videoStatus;
		}
	}
	
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		for(int i = 0;i<body.length/2;){
			mChannelListStatus.put(i/2, new ChannelStatus(body[i]==0?Constants.ChannelUseStatus.off:Constants.ChannelUseStatus.on,
					body[i+1]==0?Constants.ChannelVideoStatus.on:Constants.ChannelVideoStatus.off));
			i+=2;
		}
	}
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		int i = 0;
		for(Integer channelId:mChannelListStatus.keySet()){
			buffer.append("deviceId=").append(String.valueOf(i++)).append(":useStatus:").
			append(mChannelListStatus.get(channelId).useStatus).append(":videoStatus:").
			append(mChannelListStatus.get(channelId).videoStatus).append(";");
		}
		
		return buffer.toString();
	}
}
