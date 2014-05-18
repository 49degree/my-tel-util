package com.skyeyes.base.cmd.bean.impl;

import java.util.Arrays;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;

public class ReceiveVideoData  extends ReceiveCmdBean {
	public byte channelId;
	public byte videoEncodeId ;
	public byte specCode;//特殊编码定义（见请求实时视频）最高位表示是否录像视频0＝实时，1＝录像
	public byte frameCount;
	public int[] frameIds;

	public byte[] data = null;

	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stub
		channelId = body[0];
		videoEncodeId = body[1];
		specCode = body[2];
		frameCount = body[3];
		frameIds = new int[frameCount];
		for(int i=0;i<frameCount;i++){
			frameIds[i] = TypeConversion.bytesToInt(body, 4+i*4);
		}
		
		data = new byte[body.length - (frameCount*4+4)];
		System.arraycopy(body, frameCount*4+4, data, 0, data.length);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("channelId=").append(channelId).append(";");
		buffer.append("videoEncodeId=").append(videoEncodeId).append(";");
		buffer.append("specCode=").append(specCode).append(";");
		buffer.append("frameCount=").append(frameCount).append(";");
		buffer.append("frameIds=").append(Arrays.toString(frameIds)).append(";");
		buffer.append("data file data size=").append(data.length);
		return buffer.toString();
	}
}
