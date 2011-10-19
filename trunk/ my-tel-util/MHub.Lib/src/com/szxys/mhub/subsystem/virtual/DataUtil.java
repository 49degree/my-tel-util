package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;

public class DataUtil {
	static private ArrayList<ErrorMessageEntity> arrayMessage;
	public static  void saveMsg(ArrayList<ErrorMessageEntity> vaule)
	{
		if(arrayMessage!=null)
		{
			arrayMessage.clear();
		}
		arrayMessage=vaule;
	}
	public static ArrayList<ErrorMessageEntity>  getMsg()
	{
		return arrayMessage;
	}

}
