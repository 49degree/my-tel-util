package com.szxys.mhub.subsystem.virtual;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.szxys.mhub.base.communication.webservice.WebUtils;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.common.Logcat;
import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

public class MsgInterationResuestData {
	
	private final static String Tag = "MhubMessagesage Interation";
	
	private static MsgInterationResuestData instance = null;
	private  MsgInterationResuestData()
	{
		
	}
	
	public static MsgInterationResuestData getInstance()
	{
		if(null ==instance)
		{
			instance = new MsgInterationResuestData();
		}
		return instance;
	}
	
	public void sendUpLoadData(IPlatFormInterface pf,int nUserID)
	{
		ArrayList<OutgoingMessage> MsgComplain = MsgDBImpl.getInstance().getMsgComplainNotSend();
		if(MsgComplain.size()>0)
		{
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			String jsonData = comDataToJson(MsgComplain);
			Logcat.d(Tag, jsonData);
			byte[] sendData;
			try {
				sendData = jsonData.getBytes("UTF-8");
				byte temp[] = WebUtils.toLH(sendData.length);
				byte data[] = new byte[sendData.length+4];
				System.arraycopy(temp, 0, data, 0, 4);
				System.arraycopy(sendData, 0, data, 4,sendData.length);
				reqIdentifying.userID = nUserID;
				reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
				pf.send(reqIdentifying, 0, Platform.NETDATA_REALTIME, Consts.CMD_WEB_SEND_USER_MSG, 0, data, data.length);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void sendDownloadSucessRequest(IPlatFormInterface pf,String MhubMsgInterationID)
	{
		RequestIdentifying reqIdentifying = new RequestIdentifying();
		reqIdentifying.userID = 6;
		reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
		byte sendData[];
		try {
			sendData = MhubMsgInterationID.getBytes("UTF-8");
			pf.send(reqIdentifying, 0, Platform.NETDATA_REALTIME, Consts.CMD_WEB_GET_DOCTOR_MSG_SUCCEEDED, 0,sendData, sendData.length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public ArrayList<MhubMessage> getDoctorBetweenPatienMsg(Object AppType)
	{
		return MsgDBImpl.getInstance().getDoctorBetweenPatienMsg((Integer)AppType);
	}
	//	二进制数据流:
	//  JSON格式：
	//	[         "MsgNum" :消息数目  一次响应最多发送10条
	//		{     
	//			  "DrMsgID":"提醒编号",
	//			  "DrID":"医生编号"
	//		      ,"DrName":"医生姓名"
	//		      ,"PID":"病人编号"
	//	          ,"PName":"病人姓名"
	//	          ,"MsgContent":"消息内容"
	//	          ,"SendTime":"发送时间"
	//	          ,"SourceMsgID":"上级消息编号，表示本提醒是针对病人的该消息进行的发送
	//		  					    默认为0，为0表示没有针对的上级消息"
	//	          ,"AppID":"业务类型"
	//	          }
	//	          ,{另一条需下载的信息}
	//	          ];
	public void saveDoctorMsg(byte[] data,IPlatFormInterface pf,int nUserID)
	{
		byte temp[] = new byte[data.length -4];
		System.arraycopy(data, 4, temp, 0, data.length -4);
		String receiveData = new String(temp);
		String jsonData ="{"+'"'+"message"+'"'+":"+ receiveData +"}";
		String docMsgId = "";
		ArrayList<IncommingMessage> als = new ArrayList<IncommingMessage>();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonData);
			JSONArray jsonArray = jsonObject.getJSONArray("message");//  标示符“message”

			for (int i = 0; i < jsonArray.length(); i++) {
				IncommingMessage values = new IncommingMessage();
				String json_str = jsonArray.getString(i);
				JSONObject jsonObj = new JSONObject(json_str);
				docMsgId +=jsonObj.getString("DrMsgID") +",";
				values.setAlertId(jsonObj.getString("DrMsgID"));
				values.setDoctorId(jsonObj.getString("DrID"));
				values.setDoctorName(jsonObj.getString("DrName"));
				values.setPatientId(jsonObj.getString("PatientID"));
				values.setPatientName(jsonObj.getString("PatientName"));
				values.setContent(jsonObj.getString("MsgContent"));
				values.setTime(parseData(jsonObj.getString("SendTime")));
				values.setSourceMsgId(jsonObj.getString("SourceMsgID"));
				values.setAppId(Integer.valueOf(jsonObj.getString("AppID")));
				als.add(values);

			}
			if(docMsgId !="")
			{
				docMsgId = docMsgId.substring(0,docMsgId.length() -1);
				sendDownloadSucessRequest(pf,docMsgId);
			}		
			if(jsonArray.length()==10)
			{
			
				sendDownLoadRequestData(pf,nUserID);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MsgDBImpl.getInstance().saveMsgFromDoctor(als);
		
	}
	public void sendDownLoadRequestData(IPlatFormInterface pf,int nUserID)
	{
		RequestIdentifying reqIdentifying = new RequestIdentifying();
		reqIdentifying.userID = nUserID;
		reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
		byte[] sendData = WebUtils.toLH(3);
		pf.send(reqIdentifying, 0, Platform.NETDATA_REALTIME, Consts.CMD_WEB_GET_DOCTOR_MSG, 0, sendData, sendData.length);
		//pf.repeatSend(reqIdentifying, spaceTime, mainCmd, subCmd, bySendData, length)
	}
	
	public ArrayList<OutgoingMessage> getMsgComplainSend(Object obj)
	{
		return MsgDBImpl.getInstance().getMsgComplainSend((Integer)obj);
		
	}
	
	public ArrayList<IncommingMessage> getMsgfromDoctor(int num)
	{
		return  MsgDBImpl.getInstance().getMsgfromDoctor(num);
	
	}
	
	public void saveMsgComplain(Object obj)
	{
		MsgDBImpl.getInstance().saveMsgComplain((OutgoingMessage)obj);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteMsgComplain(Object obj)
	{
		MsgDBImpl.getInstance().deleteMsgComplain((ArrayList<Integer>)obj);
	}
	@SuppressWarnings("unchecked")
	public void deleteDoctorMsg(Object obj)
	{
		MsgDBImpl.getInstance().deleteMsgfromDoctor((ArrayList<Integer>)obj);
	}
	private  String comDataToJson(ArrayList<OutgoingMessage> listComplain)
	{
		String jsonData ="";
		String temp ="[";
		
		for(int i = 0;i<listComplain.size();i++)
		{
			String record = "{" + '"' + "PatientID" + '"' + ":"  + listComplain.get(i).getPatientId()  + ","
							+ '"' + "PatientName" + '"' + ":" + '"' + listComplain.get(i).getPatientName() + '"' + "," + '"'
							+ "MsgContent" + '"' + ":" + '"' + listComplain.get(i).getContent() + '"' + "," + '"'
							+ "SendTime" + '"' + ":" + '"' + restoreDate(listComplain.get(i).getTime()) + '"' + "," + '"'
							+ "SourceMsgID" + '"' + ":"  + listComplain.get(i).getSourceMsgId()  + "," + '"'
							+ "DrID" + '"' + ":" +  listComplain.get(i).getDoctorId()  + "," + '"' 
							+ "DrName"+ '"' + ":" + '"' + listComplain.get(i).getDoctorName() + '"' + "," + '"' 
							+ "AppID" + '"' + ":"+  listComplain.get(i).getAppId()  + "," + '"'
							+ "SendSource" + '"' + ":"+  1 +  "," + '"'
							+ "MsgType" + '"' + ":"+ 0 +"}";
			temp = temp + record + ",";
		}
		jsonData =  temp.substring(0, temp.length() - 1) + "]";
		return jsonData;
	}
	
	public static byte[] intToBytes(int n){
	       byte[] b = new byte[4];
	      
	       for(int i = 0;i < 4;i++)
	       {
	        b[i]=(byte)(n>>(24-i*8));
	     
	   } 
	   return b;
	 }  
	
	/**
	 * 形如yyyy-MM-dd HH:mm:ss的格式时间串转换成/Date(1304403691652+0800)/
	 */
	public static String restoreDate(String src) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(src);
			return "/Date(" + date.getTime() + "+0800)/";
		} catch (Exception e) {
			return src;
		}
	}
	
	public static String parseData(String src)
	{
		String result = src;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (src.startsWith("/Date(") && src.endsWith(")/")) {
			result = src.substring(6, src.length() - 2);
			int index = result.indexOf('+');
			if (index != -1) {
				result = result.substring(0, index);
			}
		}
		try {
			return sdf.format(new Date(Long.parseLong(result)));
		} catch (Exception e) {
			// TODO: handle exception
			return result;
		}

	}

}
