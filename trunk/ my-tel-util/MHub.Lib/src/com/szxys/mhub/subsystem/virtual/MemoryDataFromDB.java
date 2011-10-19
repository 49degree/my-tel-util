package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.virtual.IncommingMessage;
import com.szxys.mhub.subsystem.virtual.OutgoingMessage;

public class MemoryDataFromDB {
	static private ArrayList<OutgoingMessage> arrayComplain;
	static private ArrayList<IncommingMessage> arrayDocMsg;
	static private ArrayList<MhubMessage> arrayMhubMessage;
	
	public static  void saveComplainMsg(ArrayList<OutgoingMessage> vaule)
	{
		if(arrayComplain != null)
		{
			arrayComplain.clear();
		}
		arrayComplain = vaule;
		
	}
	
	public static  void saveMhubMsg(ArrayList<MhubMessage> vaule)
	{
		if(arrayMhubMessage != null)
		{
			arrayMhubMessage.clear();
		}
		arrayMhubMessage = vaule;
		
	}
	public static ArrayList<MhubMessage>  getMhubMessage()
	{
		return arrayMhubMessage;
	}
	public static ArrayList<OutgoingMessage>  getComplainMsg()
	{
		return arrayComplain;
	}
	
	public  static void saveDoctorMsg(ArrayList<IncommingMessage> vaule)
	{
		if(arrayDocMsg != null)
		{
			arrayDocMsg.clear();
		}
		arrayDocMsg = vaule;
	}
	
	public static ArrayList<IncommingMessage>  getDoctorMsg()
	{
		return arrayDocMsg;
	}
}
