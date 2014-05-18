package com.skyeyes.base.cmd.bean.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.skyeyes.base.cmd.CommandControl;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.SendCmdBean;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.util.ByteIntLong;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.TypeConversion;
import com.skyeyes.base.util.Utils;
import com.skyeyes.base.util.VideoClarity;

public class SendObjectParams extends SendCmdBean{
	public REQUST req = null;
	public Object[] srcParams = null;
	public Object[] sendsParams = null;
	

	@Override
	public byte[] packageBody() throws CommandParseException {
		// TODO Auto-generated method stub
		return parseData();
	}
	
	
	
	private String getDeviceId(){
		return CommandControl.getDeviceId();
	}
	
	private int getIntRandom(){
		return 2;//(int)(Math.random()*Integer.MAX_VALUE);
	}

	
	public void setParams(REQUST req,Object[] params) throws CommandParseException{
		this.req = req;
		this.commandHeader.cmdCode = req.cmd();
		commandHeader.cmdId = req.cmd();
		this.srcParams = params;
		if(params==null)
			params=new Object[]{};
		if(req.argSize()!=params.length){
			throw new CommandParseException(req.toString()+"=>参数不足");
		}
        String nowTime=Utils.getNowStr("yyyy-MM-dd HH:mm:ss");
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try
        {
	        switch(req)
	        {
	        case cmdLogin:
	        	sendsParams=new Object[]{
	        			16,params[1],
	        			16,params[2],
	        			1,params[0],
	        			32,getDeviceId(),
	        			4,getIntRandom()};
	        	break;
	        case cmdEquitLogin:
	        	sendsParams=new Object[]{
	        			16,params[1],
	        			16,params[2],
	        			1,params[0],
	        			32,params[3],
	        			4,getIntRandom()};
	        	break;
	        case cmdSendActive:
	        	sendsParams=new Object[]{1,Integer.parseInt(params[0]+"")};
	        	break;
	        case cmdFlag:
	        	sendsParams=new Object[]{1,params[0],
	        			params[1].toString().length(),params[1]
	        			};
	        	break;
	        case cmdPlayReplay:
	        	sendsParams=new Object[]{
	        			1,params[0],
	    				1,2,
	    				1,VideoClarity.instance().getId(),
	    				8,DateUtil.date2FileTime(fmt.parse(params[1]+"")),
	    				2,params[2],
	    				36,params[3]};
	        	break;
	        case cmdHistoryAlarmList:
	        	sendsParams=new Object[]{
	        			1,params[0],
	    				8,DateUtil.date2FileTime(fmt.parse(params[1]+"")),
	    				8,DateUtil.date2FileTime(fmt.parse(params[2]+"")),
	    				1,params[3]};
	        	break;
	        case cmdHistoryAlarmInfo:
	        	sendsParams=new Object[]{params[0].toString().length(),params[0]};
	        	break;
	        case cmdGps:
	        	String gps=params[0]+"_"+params[1];
	        	sendsParams=new Object[]{gps.length(),gps};
	        	break;
	        case cmdConfirmPolice:
	        	sendsParams=new Object[]{
	        			36,params[0],
	    				1,params[1],
	    				8,params[2],
	    				8,params[3],
	    				params[4].toString().getBytes().length,params[4]};
	        	break;
	        case cmdEquitId:
	        	sendsParams=new Object[]{params[0].toString().length(),params[0]};
	        	break;
	        case cmdEquitInfo:
	        	sendsParams=new Object[]{params[0].toString().length(),params[0]};
	        	break;
	        case cmdUserEquitListNOLogin:
	        	sendsParams=new Object[]{16,params[0],16,params[1]};
	        	break;
	        case cmdSetDuty:
	        	sendsParams=new Object[]{1,params[0]};
	        	break;
	        case cmdUploadEvidence:
	        	sendsParams=new Object[]{
	        			1,params[0],
	        			36,params[1],
	        			params[2].toString().length(),params[2]};
	        	break;
	        case cmdSendAlarm:
	        	sendsParams=new Object[]{
					        			32,getDeviceId(),
					        			4,getIntRandom(),
					    				1,params[0],
					    				1,0,
					    				8,DateUtil.date2FileTime(fmt.parse(nowTime)),
					    				8,params[1],
					    				8,params[2],
					    				params[3].toString().getBytes().length,params[3].toString()};
	        	break;
	        case cmdPlatformAlarmList:
	        	sendsParams=new Object[]{
	        			8,DateUtil.date2FileTime(fmt.parse(params[0]+"")),
	    				8,DateUtil.date2FileTime(fmt.parse(params[1]+"")),
	    				1,params[2],
	    				1,params[3],
	    				1,params[4],
	    				1,params[5]};
	        	break;
	        case cmdPlatformAlarmInfo:
	        	sendsParams=new Object[]{36,params[0]};
	        	break;
	        case cmdHeart:
	        	sendsParams=new Object[]{0,1};
	        	break;
	        case cmdKeepWatchList:
	        	sendsParams=new Object[]{
	        			36,params[0],
	        			8,DateUtil.date2FileTime(fmt.parse(params[1]+"")),
	        			8,DateUtil.date2FileTime(fmt.parse(params[2]+"")),
	        			1,params[3]};
	        	break;
	        case cmdComfirmAlarm:
	        	sendsParams=new Object[]{
	        			36,params[0],
	        			1,params[1],
	        			params[2].toString().length(),params[2]};
	        	break;
	        case cmdGetEquitIO:
	        	sendsParams=new Object[]{
	        			1,params[0]};
	        	break;
	        case cmdSetEquitIO:
	        	sendsParams=new Object[]{
	        			1,params[0],
	        			1,params[1]};
	        	break;
	        case cmdGetPlatDutyList:
	        	sendsParams=new Object[]{
	        			36,params[0],
	        			8,DateUtil.date2FileTime(fmt.parse(params[1]+"")),
	        			8,DateUtil.date2FileTime(fmt.parse(params[2]+""))};
	        	break;
	        	
	        	//add------------------------
	        case cmdReadDeviceIp:
	        	sendsParams=new Object[]{};
	        	break;	
	        	
	        case cmdGetActive:
	        	sendsParams=new Object[]{1,params[0]};
	        	break;
	        case cmdReadDeviceEnv:
	        	sendsParams=new Object[]{};
	        	break;
	        case cmdReqRealVideo:
	        	sendsParams=new Object[]{
	        			1,params[0],
	        			1,3,
	        			1,VideoClarity.instance().getId()};
	        	break;
	        case cmdPushActive:
//	        	sendsParams=new Object[]{
//	        			1,0,
//	        			1,0,
//	        	};

	        	break;
	        case cmdReqVideoChannelPic:
	        	sendsParams=new Object[]{
	    			1,params[0]
		    	};
	        	break;
	        default:
	        	sendsParams=new Object[]{};
	        	break;
	        }
        }catch(Exception e)
        {
        	
        }finally{}
	}
	
	
	
	private byte[] parseData()
	{
		int len=0;
		byte[] buffer = null;
		if(sendsParams==null||sendsParams.length==0){
			buffer = new byte[0];
			return buffer;
		}
		
		for(int i=0;i<sendsParams.length;i=i+2)
		{
			Object value=sendsParams[i+1];
			int tempLen=Integer.parseInt(sendsParams[i].toString());
			System.out.println("params"+(i+1)/2+":len:"+tempLen+":value:"+value);//+":"+TypeConversion.byte2hex(TypeConversion.stringToAscii(String.valueOf(value))));
			byte[] temp = new byte[len+tempLen];
			if(len > 0)
				System.arraycopy(buffer, 0, temp, 0,len);
			
			buffer = temp;
			
			if(value instanceof String){
				temp=TypeConversion.stringToAscii(String.valueOf(value));
			}else if(value instanceof Integer){
				temp= TypeConversion.intToBytes((Integer)value);
			}else if(value instanceof Long){
				temp=TypeConversion.longToBytes((Long)value);
			}else if(value instanceof Double){
				temp=ByteIntLong.getBytes(Double.doubleToLongBits(Double.parseDouble(value+"")), false);
			}else if(value instanceof Byte){
				temp = new byte[]{(Byte)value};
			}
			
			
			System.arraycopy(temp, 0, buffer, len,temp.length>tempLen?tempLen:temp.length);
			//System.out.println("parseData:"+TypeConversion.byte2hex(buffer));
			len+=tempLen;
		}
		
		return buffer;
	}
}
