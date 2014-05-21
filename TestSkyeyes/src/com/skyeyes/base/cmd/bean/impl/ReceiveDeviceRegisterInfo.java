package com.skyeyes.base.cmd.bean.impl;

import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.TypeConversion;
	
/**
 * 获取设备注册信息
 * @author Administrator
 * 
 * 机器码（UTF8编码）	
 * 是否试用机小于0x80表示非试用，大
 * 于等于0x80表示试用机	
 * 试用到期年10位（默认+2000）	
 * 试用到期月	
 * 试用到期日	
 * 试用到期时	
 * 试用到期分	
 * 试用到期秒	
 * 可用视频路数	
 * 可用分析路数	
 * 托管方案是否验证1＝是，0＝否	
 * 联动输入端口数	
 * 联动输出端口数	
 * 未用	
 * 1＝启用越界，0＝不启用	
 * 1＝启用徘徊，0＝不启用	
 * 1＝启用人脸，0＝不启用	
 * 1＝启用偷窃，0＝不启用	
 * 1＝启用滞留，0＝不启用	
 * 1＝启用脱岗，0＝不启用	
 * 1＝启用聚集，0＝不启用	
 * 1＝启用烟火，0＝不启用	
 * 1＝启用速度异常，0＝不启用	
 * 保留	
 * 平台允许的最大客户数（保安数）	
 * 平台允许的最大管理设备数	
 * 设备类型0＝墅安居，1＝店铺安，2＝安保平台
 *
 */
public class ReceiveDeviceRegisterInfo extends ReceiveCmdBean {
	public String deviceCode = "";//机器码（UTF8编码）
	public byte videoChannelCount;//可用视频路数
	public byte deviceType;//设备类型0＝墅安居，1＝店铺安，2＝安保平台
	@Override
	public void parseBody(byte[] body) throws CommandParseException {
		// TODO Auto-generated method stu
		deviceCode = TypeConversion.asciiToString(body,0,32);
		videoChannelCount = body[39];
		deviceType = body[252];
	}
	public String toString(){
		StringBuffer buffer = new StringBuffer().append(super.toString());
		buffer.append("deviceCode=").append(deviceCode).append(";");
		buffer.append("videoChannelCount=").append(videoChannelCount).append(";");
		buffer.append("deviceType=").append(deviceType).append(";");
		return buffer.toString();
	}
}
