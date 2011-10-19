package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;

import com.szxys.mhub.base.manager.MHubDBHelper;

/**
 * 互动消息数据库操作类
 * 
 * @author 苏佩
 * 
 */
public class MsgDBImpl {
	
	private MHubDBHelper dbhelper;
	
	private static final String Tag ="Interaction DB Operation";
	
	private static MsgDBImpl mInstance = null;
	private MsgDBImpl()
	{
		dbhelper = new  MHubDBHelper();
	}
	
	public static MsgDBImpl getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new MsgDBImpl();
		}
		return mInstance;
	}
	
	//查询用户未发送的消息
	public ArrayList<OutgoingMessage> getMsgComplainNotSend()
	{
		ArrayList<OutgoingMessage> complainList = new  ArrayList<OutgoingMessage>();
		dbhelper.open(false);
		String sql = "select * from mb_outgoing where c_IsSend = 0 order by c_Id asc";
		Cursor cur = dbhelper.query(sql);
		if(cur != null&&cur.getCount() >0)
		{
			
			while(!cur.isAfterLast())
			{
				OutgoingMessage msgComplain  = new OutgoingMessage();
				msgComplain.setAppId(cur.getInt(cur.getColumnIndex("c_AppType")));
				msgComplain.setContent(cur.getString(cur.getColumnIndex("c_Content")));
				msgComplain.setDoctorId(cur.getString(cur.getColumnIndex("c_DoctorsNo")));
				msgComplain.setIsSend(cur.getInt(cur.getColumnIndex("c_IsSend")));
				msgComplain.setDoctorName(cur.getString(cur.getColumnIndex("c_DoctorsName")));
				msgComplain.setPatientId(cur.getString(cur.getColumnIndex("c_PatientNo")));
				msgComplain.setPatientName(cur.getString(cur.getColumnIndex("c_PatientName")));
				msgComplain.setSourceMsgId(cur.getString(cur.getColumnIndex("c_SourceMsgId")));
				msgComplain.setTime(cur.getString(cur.getColumnIndex("c_SendDt")));
				msgComplain.setMhubmsgId(cur.getInt(cur.getColumnIndex("c_Id")));
				complainList.add(msgComplain);
				cur.moveToNext();
			}
			
		}
		cur.close();
		return complainList;
	}
	//查询用户已发送的消息
	public ArrayList<OutgoingMessage> getMsgComplainSend(int num)
	{
		ArrayList<OutgoingMessage> complainList = new  ArrayList<OutgoingMessage>();
		dbhelper.open(false);
		String sql = "select * from mb_outgoing where c_IsSend = 1 order by c_Id asc";
		Cursor cur = dbhelper.query(sql);
		int count =0;
		if(cur != null&&cur.getCount() >0)
		{
			while(!cur.isAfterLast())
			{
				count ++;
				
				OutgoingMessage msgComplain  = new OutgoingMessage();
				msgComplain.setAppId(cur.getInt(cur.getColumnIndex("c_AppType")));
				msgComplain.setContent(cur.getString(cur.getColumnIndex("c_Content")));
				msgComplain.setDoctorId(cur.getString(cur.getColumnIndex("c_DoctorsNo")));
				msgComplain.setIsSend(cur.getInt(cur.getColumnIndex("c_IsSend")));
				msgComplain.setDoctorName(cur.getString(cur.getColumnIndex("c_DoctorsName")));
				msgComplain.setPatientId(cur.getString(cur.getColumnIndex("c_PatientNo")));
				msgComplain.setPatientName(cur.getString(cur.getColumnIndex("c_PatientName")));
				msgComplain.setSourceMsgId(cur.getString(cur.getColumnIndex("c_SourceMsgId")));
				msgComplain.setTime(cur.getString(cur.getColumnIndex("c_SendDt")));
				msgComplain.setMhubmsgId(cur.getInt(cur.getColumnIndex("c_Id")));
				complainList.add(msgComplain);
				if(count>num)break;
				cur.moveToNext();
			}		
		}
		cur.close();
		return complainList;
	}
	//根据子系统业务ID 查询用户发送的消息
	public ArrayList<OutgoingMessage> getMsgComplainSendByAppId(int AppId,int num)
	{
		ArrayList<OutgoingMessage> complainList = new  ArrayList<OutgoingMessage>();
		dbhelper.open(false);
		String sql = "select * from mb_outgoing where c_IsSend = 1 order byc_Id asc where c_AppType =" +AppId;
		Cursor cur = dbhelper.query(sql);
		int count =0;
		if(cur != null&&cur.getCount() >0)
		{
			while(!cur.isAfterLast())
			{
				count ++;
				OutgoingMessage msgComplain  = new OutgoingMessage();
				msgComplain.setAppId(cur.getInt(cur.getColumnIndex("c_AppType")));
				msgComplain.setContent(cur.getString(cur.getColumnIndex("c_Content")));
				msgComplain.setDoctorId(cur.getString(cur.getColumnIndex("c_DoctorsNo")));
				msgComplain.setIsSend(cur.getInt(cur.getColumnIndex("c_IsSend")));
				msgComplain.setDoctorName(cur.getString(cur.getColumnIndex("c_DoctorsName")));
				msgComplain.setPatientId(cur.getString(cur.getColumnIndex("c_PatientNo")));
				msgComplain.setPatientName(cur.getString(cur.getColumnIndex("c_PatientName")));
				msgComplain.setSourceMsgId(cur.getString(cur.getColumnIndex("c_SourceMsgId")));
				msgComplain.setTime(cur.getString(cur.getColumnIndex("c_SendDt")));
				msgComplain.setMhubmsgId(cur.getInt(cur.getColumnIndex("c_Id")));
				complainList.add(msgComplain);
				if(count>num)break;
				cur.moveToNext();
			}		
		}
		cur.close();
		return complainList;
	}
	
	public ArrayList<MhubMessage> getDoctorBetweenPatienMsg(int AppType)
	{
		ArrayList<MhubMessage> DoctorBetweenPatientMsgList = new  ArrayList<MhubMessage>();
		dbhelper.open(false);
		String strSQL = "select * from (" 
						+ "select  c_Id,c_DoctorsName,c_PatientName,c_Content,c_SourceMsgId,c_receiveDT as c_Datetime from  mb_incoming union all "
						+"select  c_Id,c_DoctorsName,c_PatientName,c_Content,c_SourceMsgId,c_sendDT as c_Datetime  from  mb_outgoing where c_IsSend = 1 )" 
						+" order by c_Datetime desc";
		Cursor cur = dbhelper.query(strSQL);
		while(!cur.isAfterLast())
		{
			MhubMessage DoctorBetweenPatMsg  = new MhubMessage();
			DoctorBetweenPatMsg.setContent(cur.getString(cur.getColumnIndex("c_Content")));
			DoctorBetweenPatMsg.setDoctorName(cur.getString(cur.getColumnIndex("c_DoctorsName")));
			DoctorBetweenPatMsg.setPatientName(cur.getString(cur.getColumnIndex("c_PatientName")));
			DoctorBetweenPatMsg.setSourceMsgId(cur.getString(cur.getColumnIndex("c_SourceMsgId")));
			DoctorBetweenPatMsg.setTime(cur.getString(cur.getColumnIndex("c_Datetime")));
			DoctorBetweenPatMsg.setMhubmsgId(cur.getInt(cur.getColumnIndex("c_Id")));
			DoctorBetweenPatientMsgList.add(DoctorBetweenPatMsg);
			cur.moveToNext();
		}
		cur.close();
		return DoctorBetweenPatientMsgList;
	}
	//查询医生回复给我的消息
	public  ArrayList<IncommingMessage> getMsgfromDoctor(int num)
	{
		ArrayList<IncommingMessage> DoctorMsgList = new  ArrayList<IncommingMessage>();
		dbhelper.open(false);
		String sql = "select * from mb_incoming order by c_IsRead desc,c_ReceiveDt desc";
		Cursor cur = dbhelper.query(sql);
		int count=0;
		if(cur != null&&cur.getCount() >0)
		{
			
			while(!cur.isAfterLast())
			{
				IncommingMessage DoctorMsg  = new IncommingMessage();
				DoctorMsg.setAppId(cur.getInt(cur.getColumnIndex("c_AppType")));
				DoctorMsg.setContent(cur.getString(cur.getColumnIndex("c_Content")));
				DoctorMsg.setDoctorId(cur.getString(cur.getColumnIndex("c_DoctorsNo")));
				DoctorMsg.setIsRead(cur.getInt(cur.getColumnIndex("c_IsRead")));
				DoctorMsg.setDoctorName(cur.getString(cur.getColumnIndex("c_DoctorsName")));
				DoctorMsg.setPatientId(cur.getString(cur.getColumnIndex("c_PatientNo")));
				DoctorMsg.setPatientName(cur.getString(cur.getColumnIndex("c_PatientName")));
				DoctorMsg.setSourceMsgId(cur.getString(cur.getColumnIndex("c_SourceMsgId")));
				DoctorMsg.setTime(cur.getString(cur.getColumnIndex("c_ReceiveDt")));
				DoctorMsg.setMhubmsgId(cur.getInt(cur.getColumnIndex("c_Id")));
				DoctorMsgList.add(DoctorMsg);
				if(count>num)break;
				cur.moveToNext();
			}		
		}
		cur.close();
		return DoctorMsgList;
	}
	//保存用户主诉
	public boolean saveMsgComplain(OutgoingMessage msgComplain)
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "insert into mb_outgoing(c_DoctorsNo,c_DoctorsName,c_PatientNo,"
						+"c_PatientName,c_Content,c_SendDt,c_SourceMsgId,c_AppType,c_IsSend)"
						+"values(";
		strSQL += "'"+msgComplain.getDoctorId()+"',";
		strSQL += "'"+msgComplain.getDoctorName()+"',";
		strSQL += "'"+msgComplain.getPatientId()+"',";
		strSQL += "'" + msgComplain.getPatientName()+"',";
		strSQL += "'" +msgComplain.getContent()+"',";
		strSQL += "'" +msgComplain.getTime() +"',";
		strSQL += "'"+msgComplain.getSourceMsgId()+"',";
		strSQL += msgComplain.getAppId()+",";
		strSQL += msgComplain.getIsSend()+")";
		try {
			dbhelper.execSQL(strSQL);
		} catch (Exception e) {
			bResult = false;
			Log.e(Tag,strSQL);
			
		}
		return bResult;
	}
	
	public boolean saveMsgFromDoctor(ArrayList<IncommingMessage> DoctorMsg)
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "insert into mb_incoming(c_AlertId,c_DoctorsNo,c_DoctorsName,c_PatientNo,"
						+"c_PatientName,c_Content,c_ReceiveDt,c_SourceMsgId,c_AppType,c_IsRead) values(";
		for(int i =0;i<DoctorMsg.size();i++)
		{
			String whereSQL ="";
			String SQL = "";
			whereSQL += "'"+DoctorMsg.get(i).getAlertId()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getDoctorId()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getDoctorName()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getPatientId()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getPatientName()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getContent()+"',";
			whereSQL += "'" +DoctorMsg.get(i).getTime()+"',";
			whereSQL += "'"+DoctorMsg.get(i).getSourceMsgId()+"',";
			whereSQL += DoctorMsg.get(i).getAppId()+",";
			whereSQL += DoctorMsg.get(i).getIsRead()+")";
			SQL = strSQL +whereSQL;
			try {
				dbhelper.execSQL(SQL);
			} catch (Exception e) {
				bResult = false;
				Log.e(Tag,SQL);
				
			}
		}
		return bResult;
		
	}
	
	public boolean deleteMsgComplain(ArrayList<Integer> IDList)
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "delete from mb_outgoing where c_Id in (";
		for(int i =0;i<IDList.size();i++)
		{
			strSQL +=IDList.get(i)+",";
		}
		strSQL = strSQL.substring(0,strSQL.length()-1);
		strSQL +=")";
		try {
			dbhelper.execSQL(strSQL);
			
		} catch (Exception e) {
			bResult = false;
			Log.e(Tag, strSQL);
			
		}
		return bResult;
	}
	
	public boolean deleteMsgfromDoctor(ArrayList<Integer> IDList)
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "delete from mb_incoming where c_Id in (";
		for(int i =0;i<IDList.size();i++)
		{
			strSQL +=IDList.get(i)+",";
		}
		strSQL = strSQL.substring(0,strSQL.length()-1);
		strSQL +=")";
		try {
			dbhelper.execSQL(strSQL);
			
		} catch (Exception e) {
			bResult = false;
			Log.e(Tag, strSQL);
			
		}
		return bResult;
		
	}
	
	public boolean updateStatusMsgComplain(int ID[])
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "update table mb_outgoing set c_IsSend = 1 where c_Id in (";
		for(int i =0;i<ID.length;i++)
		{
			strSQL +=ID[i]+",";
		}
		strSQL +=")";
		try {
			dbhelper.execSQL(strSQL);
			
		} catch (Exception e) {
			bResult = false;
			Log.e(Tag, strSQL);
			
		}
		return bResult;
		
	}
	
	public boolean updateStatusMsgfromDoctor(ArrayList<Integer> IDList)
	{
		boolean bResult = true;
		dbhelper.open(true);
		String strSQL = "update table mb_incoming set c_IsRead = 1 where c_Id in (";
		for(int i =0;i<IDList.size();i++)
		{
			strSQL +=IDList.get(i)+",";
		}
		strSQL +=")";
		try {
			dbhelper.execSQL(strSQL);
			
		} catch (Exception e) {
			bResult = false;
			Log.e(Tag, strSQL);
			
		}
		return bResult;
		
	}
}
