package com.skyeyes.base.cmd;

import java.util.List;

import com.skyeyes.base.cmd.bean.CmdHeaderBean;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceivReadDeviceNetInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveChannelPic;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelListStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceChannelName;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceEnv;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceStatus;
import com.skyeyes.base.cmd.bean.impl.ReceiveGetEquitIO;
import com.skyeyes.base.cmd.bean.impl.ReceiveHeart;
import com.skyeyes.base.cmd.bean.impl.ReceiveHistoryVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveOpenCloseDoor;
import com.skyeyes.base.cmd.bean.impl.ReceiveReadDeviceList;
import com.skyeyes.base.cmd.bean.impl.ReceiveRealVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveSetEquitIO;
import com.skyeyes.base.cmd.bean.impl.ReceiveStatusChange;
import com.skyeyes.base.cmd.bean.impl.ReceiveStopVideo;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoData;
import com.skyeyes.base.cmd.bean.impl.ReceiveVideoFinish;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAllManuByDay;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAllManuByMouse;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgDayManuByMouse;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgHourManuByDay;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgManuStayTimeByDay;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgManuStayTimeByMouse;
import com.skyeyes.base.exception.CommandParseException;




/**
 * 命令上传下载控制
 * @author Administrator
 *
 */
public class CommandControl {
	public final static byte[] CMD_HEADER = new byte[]{0x48,0x72,0x49,0x3c};//数据报开始标志
	public final static byte[] CMD_LASTER = new byte[]{0x3e,0x68,0x52,0x69};//数据包结束标志
	
	public static int loginId;
	
	public static List<String> deviceIdList;
	/**
	 * 解析收到的命令
	 * @return
	 */
	public static ReceiveCmdBean parseReceiveCmd(byte[] receiveBuffer) throws CommandParseException{
		CmdHeaderBean cmdHeaderBean = null;
		try{
			//解析头部
			cmdHeaderBean = CmdHeaderBean.parseCommandHeader(receiveBuffer);
			
			//解析命令体
			ReceiveCmdBean receiveCmdBean = null;
			
			//cmdHeaderBean.cmdCode为0标示应答数据，则cmdHeaderBean.cmdId区别应答类型
			REQUST reqCmd = null;
			
			if(cmdHeaderBean.cmdCode==0){
				reqCmd = REQUST.valueOfByCmdId(cmdHeaderBean.cmdId);
			}else{
				reqCmd = REQUST.valueOfByCmd(cmdHeaderBean.cmdCode);
			}
			System.out.println("reqCmd："+reqCmd);
			switch(reqCmd){
	        case cmdLogin:
	        	break;
	        case cmdEquitLogin:
	        	receiveCmdBean = new ReceivLogin();
	        	break;
	        case cmdEquitInfo:
	        	receiveCmdBean = new ReceiveDeviceInfo();
	        	break;
	        case cmdUserEquitList:
	        case cmdUserEquitListNOLogin:
	        	receiveCmdBean = new ReceiveReadDeviceList();
	        	break;
	        case cmdHeart:
	        	receiveCmdBean = new ReceiveHeart();
	        	break;
	        case cmdGetEquitIO:
	        	receiveCmdBean = new ReceiveGetEquitIO();
	        	break;
	        case cmdSetEquitIO:
	        	receiveCmdBean = new ReceiveSetEquitIO();
	        	break;
	        case cmdReadDeviceIp:
	        	receiveCmdBean = new ReceivReadDeviceNetInfo();
	        	break;
	        case cmdGetActive:
	        	receiveCmdBean = new ReceiveDeviceStatus();
	        	break;
	        case cmdReadDeviceEnv:
	        	receiveCmdBean = new ReceiveDeviceEnv();
	        	break;
	        case cmdReqRealVideo:
	        	receiveCmdBean = new ReceiveRealVideo();
	        	break;
	        case cmdPushActive:
	        	receiveCmdBean = new ReceiveStatusChange();
	        	break;
	        case cmdReqVideoChannelListStatus:
	        	receiveCmdBean = new ReceiveDeviceChannelListStatus();
	        	break;	   
	        case cmdReqVideoChannelPic:
	        	receiveCmdBean = new ReceiveChannelPic();
	        	break;
	        case cmdVideoChannelName:
	        	receiveCmdBean = new ReceiveDeviceChannelName();
	        	break;	
	        case cmdReqStopVideo:
	        	receiveCmdBean = new ReceiveStopVideo();
	        	break;
	        case cmdRevFrame:
	        	receiveCmdBean = new ReceiveVideoData();
	        	break;
	        case cmdRevVideoFinish:
	        	receiveCmdBean = new ReceiveVideoFinish();
	        	break;
	        case cmdEquitRegInfo:
	        	System.out.println("cmdEquitRegInfo");
	        	receiveCmdBean = new ReceiveDeviceRegisterInfo();
	        	break;
	        case cmdReqAllManuByDay://按日统计总人流
	        	receiveCmdBean = new ReceiveAllManuByDay();
	        	break;
	        case cmdReqAllManuByMouse://按月统计总人流
	        	receiveCmdBean = new ReceiveAllManuByMouse();
	        	break;
	        case cmdReqAvgHourManuByDay://按日统计每小时人流
	        	receiveCmdBean = new ReceiveAvgHourManuByDay();
	        	break;
	        case cmdReqAvgDayManuByMouse://按月统计每天人流
	        	receiveCmdBean = new ReceiveAvgDayManuByMouse();
	        	break;
	        case cmdReqAvgManuStayTimeByDay://按日统计平均驻留时间
	        	receiveCmdBean = new ReceiveAvgManuStayTimeByDay();
	        	break;
	        case cmdReqAvgManuStayTimeByMouse://按月统计平均驻留时间	  
	        	receiveCmdBean = new ReceiveAvgManuStayTimeByMouse();
	        	break;
	        case cmdReqOpenCloseDoorList://获取开关门列表
	        	receiveCmdBean = new ReceiveOpenCloseDoor();
	        	break;
	        case cmdReqAlarmList://报警列表
	        	//receiveCmdBean = new ReceiveAvgManuStayTimeByMouse();
	        	break;	     
	        case cmdReqHistoryVideo://请求录像回放         	
	        case cmdReqAlarmVideo://请求报警录像
	        	receiveCmdBean = new ReceiveHistoryVideo();
	        	break;	        	
	        default:
	        	return null;
	        } 
			if(receiveCmdBean!=null){
				receiveCmdBean.setCommandHeader(cmdHeaderBean);
			}
			if(cmdHeaderBean.resultCode==0){//正常数据，解析业务数据
				byte[] body = null;
				if(cmdHeaderBean.cmdCode==0x00){//为请求相应数据
					body = new byte[receiveBuffer.length-19];
					System.arraycopy(receiveBuffer, 15, body, 0, body.length);
				}else{//为请求数据
					body = new byte[receiveBuffer.length-18];
					System.arraycopy(receiveBuffer, 14, body, 0, body.length);
				}
				if(receiveCmdBean!=null){
					receiveCmdBean.parseBody(body);
				}
				
			}
			if(receiveCmdBean!=null)
				System.arraycopy(receiveBuffer, receiveBuffer.length-4, receiveCmdBean.getEnding(), 0, 4);
			
			System.out.println("收到对象："+(receiveCmdBean==null?cmdHeaderBean.toString():receiveCmdBean.toString()));
			
			return receiveCmdBean;
		}catch(CommandParseException e){//出现异常，服务器直接返回upErrorBean对象的ErrorReturnBuffer即可
			return null;
		}
	}
	/**
	 * 
	 * 构造下行数据
	 * @return
	 */
	public static byte[] parseDownComand(SendCmdBean sendCmdBean) throws CommandParseException{
		return sendCmdBean.packageCommand();
	}

	public static enum REQUST{
		cmdDataNotEnough((byte)-3,(byte)0x2F,0),
		cmdSysError((byte)-2,(byte)0x30,0),
		cmdError((byte)-1,(byte)0x31,0),
		
		cmdLogin((byte)0x01,(byte)0x01,3),//登录 cmdLogin((byte)int state,String username,String userpwd)
		cmdEquitLogin((byte)0x01,(byte)0x02,4),//登录 cmdLogin((byte)int state,String username,String userpwd,String Data)
		
		cmdGetActive((byte)0x1c,(byte)0x03,0),//获取布防状态 cmdGetActive((byte))
		cmdSendActive((byte)0x1b,(byte)0x04,1),//设置布防状态 cmdSendActive((byte)int value)
		cmdPushActive((byte)0x17,(byte)0x05,0),//设置布防状态 cmdSendActive((byte)int value)
		cmdFlag((byte)0,(byte)0x06,2),//视频解码心跳 cmdFlag((byte)int flag,String meg)
		
		cmdHistoryAlarmList((byte)35,(byte)0x08,4),//读取历史报警记录列表命令 cmdHistoryAlarmList((byte)int channel,String startTime,String endTime,int flag)
		cmdHistoryAlarmInfo((byte)36,(byte)0x09,1),//读取历史报警记录详细 cmdHistoryAlarmInfo((byte)String id)
		cmdGps((byte)0x3d,(byte)0x0A,2),// cmdGps((byte)double x,double y)
		cmdConfirmPolice((byte)0x3a,(byte)0x0B,5),//cmdConfirmPolice((byte)String id,int type,double x,double y,String bz)
		cmdUserInfo((byte)0x2b,(byte)0x0C,0),//cmdUserInfo((byte))
		cmdEquitId((byte)0x43,(byte)0x0D,1),//cmdEquitId((byte)String id)
		cmdEquitInfo((byte)0x38,(byte)0x0E,1),//cmdEquitInfo((byte)String id)
		cmdUserEquitList((byte)0x39,(byte)0x0F,0),//cmdUserEquitList((byte))
		cmdUserEquitListNOLogin((byte)0x39,(byte)0x10,2),//cmdUserEquitList((byte))
		cmdSetDuty((byte)0x44,(byte)0x11,1),//cmdSetDuty((byte)int state)
		cmdUploadEvidence((byte)0x3e,(byte)0x12,3),//cmdUploadEvidence((byte)int type,String alarmid,String url)
		cmdSendAlarm((byte)0x21,(byte)0x13,4),//发送主动报警信息cmdSendAlarm((byte)int type,double longitude double latitude,String equitid)
		cmdPlatformAlarmList((byte)0x46,(byte)0x14,6),//获取保安平台报警信息cmdPlatformAlarmList((byte)String sTimeStr,String eTimeStr,int ja,int jj,int cj,int bajj)
		cmdPlatformAlarmInfo((byte)0x47,(byte)0x15,1),//获取平台报警详细信息 cmdPlatformAlarmInfo((byte)String alarmid)
		cmdHeart((byte)0x09,(byte)0x16,1),//测试主动心跳 cmdHeart((byte)String alarmid)
		cmdKeepWatchList((byte)0x4a,(byte)0x17,4),//获取巡更列表 cmdKeepWatchList((byte)String userid,String stime,String etime,int type)0＝所有 1＝已过期所有2＝未过期 3＝已过期未执行 4＝已过期已执行
		cmdRevLoginOut((byte)0x31,(byte)0x18,0),//设备退出 cmdRevLoginOut((byte))
		cmdComfirmAlarm((byte)0x16,(byte)0x19,3),//服务中心或保安现场确认报警 cmdComfirmAlarm((byte)string alarmid,int mark,String bz) 0＝确认报警\1＝误报\2=业主
		
		cmdGetEquitIO((byte)0x10,(byte)0x1A,1),//获取当前设备IO口状态 cmdGetEquitIO((byte)int io) 动作0=断开，1＝闭合
		cmdSetEquitIO((byte)0x15,(byte)0x1B,2),//设置当前设备IO口状态 cmdSetEquitIO((byte)int io,int flag) 动作0=断开，1＝闭合
		cmdGetPlatDutyList((byte)0x47,(byte)0x1C,3),//读取平台中的上下班信息 cmdGetPlatDutyList((byte)String userID,String sTime,String eTime) 动作0=断开，1＝闭合
		cmdContralUserIDList((byte)0x2a,(byte)0x1D,1),//读取中心用户列表 cmdContralUserIDList((byte)String equitID) 
		cmdVideoChannelName((byte)0x41,(byte)0x1E,0),//读取通道名称
		//add-------------------
		cmdReqAllManuByDay((byte)0x54,(byte)0x1F,1),//按日统计总人流
		cmdReqAllManuByMouse((byte)0x54,(byte)0x20,1),//按月统计总人流
		cmdReqAvgHourManuByDay((byte)0x54,(byte)0x21,1),//按日统计每小时人流
		cmdReqAvgDayManuByMouse((byte)0x54,(byte)0x22,1),//按月统计每天人流
		cmdReqAvgManuStayTimeByDay((byte)0x54,(byte)0x23,1),//按日统计平均驻留时间
		cmdReqAvgManuStayTimeByMouse((byte)0x54,(byte)0x24,1),//按月统计平均驻留时间

		
		cmdEquitRegInfo((byte)0x19,(byte)0x25,0),//获取设备注册信息(查询通道数量)
		cmdReadDeviceIp((byte)0x08,(byte)0x26,0),
		cmdReadDeviceEnv((byte)0x22,(byte)0x27,0),
		cmdReqRealVideo((byte)0x02,(byte)0x28,1),//获取通道视频
		cmdRevFrame((byte)0x06,(byte)0x29,0),//视频数据到达
		cmdReqStopVideo((byte)0x04,(byte)0x2A,0),//停止视频直播、录像回放
		cmdRevVideoFinish((byte)0x07,(byte)0x2B,0),//视频数据播放完成
		cmdReqVideoChannelListStatus((byte)0x42,(byte)0x2C,0),//查询通道状态
		cmdReqVideoChannelPic((byte)0x13,(byte)0x2D,1),//获取通道图片
		
		cmdReqOpenCloseDoorList((byte)0x23,(byte)0x2E,2),//获取开关门列表
		cmdReqAlarmList((byte)0x23,(byte)0x2F,2),//报警列表
		
		cmdReqHistoryVideo((byte)0x03,(byte)0x30,3),//请求录像回放 
		cmdReqAlarmVideo((byte)0x03,(byte)0x31,2);//请求报警录像
		
		private byte cmd ;
		private byte cmdId;//返回ID
		private int argSize;
		private REQUST( byte cmd,byte cmdId,int argSize) {
	           this.cmd = cmd;
	           this.cmdId = cmdId;
	           this.argSize=argSize;
	    }
		public byte cmd()
		{
			return cmd;
		}
		
		public byte cmdId()
		{
			return cmdId;
		}
		
		public int argSize()
		{
			return argSize;
		}
		public static REQUST valueOf(int ordinal) {
	        if (ordinal < 0 || ordinal >= values().length) {
	            throw new IndexOutOfBoundsException("Invalid ordinal");
	        }
	        return values()[ordinal];
	    }
		
		public static REQUST valueOfByCmd(byte cmd) {
	        for(int i=0;i<values().length;i++)
	        {
	        	if(REQUST.valueOf(i).cmd()==cmd)
	        		return REQUST.valueOf(i);
	        }
	        return cmdError;
	    }
		
		public static REQUST valueOfByCmdId(byte cmdId) {
	        for(int i=0;i<values().length;i++)
	        {
	        	if(REQUST.valueOf(i).cmdId()==cmdId)
	        		return REQUST.valueOf(i);
	        }
	        return cmdError;
	    }
	}
	
	public static int getLoginId(){
		return loginId;
	}
	
	
	public static String getDeviceId(){
		if(deviceIdList==null||deviceIdList.size()==0)
			return "";
		else
			return deviceIdList.get(0);
	}
}
