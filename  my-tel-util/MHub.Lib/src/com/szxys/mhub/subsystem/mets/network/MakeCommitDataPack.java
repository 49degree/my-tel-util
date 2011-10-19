package com.szxys.mhub.subsystem.mets.network;

import java.util.ArrayList;

import com.szxys.mhub.subsystem.mets.db.*;

import android.util.Log;


public class MakeCommitDataPack {
	
	public static String makeCommitUrineOrDrink(String userId, ArrayList<String[]> diaries)//组装排尿/饮水数据
	{
		String data='"' + userId + '"';
		if(diaries.size()>0)
		{
			String[] diary01=diaries.get(0);
			 data='"'+diary01[0]+'"'+"\r\n";
			for(int i=0;i<diaries.size();i++)
			{
				String [] diary=diaries.get(i);
				String time="";
				time=time+diary[1].substring(0,4)+diary[1].substring(5,7)+diary[1].substring(8,10)+diary[1].substring(11,13)+diary[1].substring(14,16)+
							diary[1].substring(17,19);
				Log.i("mylog","time:"+time);
				data=data+'"'+time+'"'+"\t"+'"'+diary[2]+'"'+"\t"+'"'+diary[3]+'"'+"\r\n";
			}
		}
		return data;
	}

	
	public static String makeCommitDownAndUp(String userId,ArrayList<String[]> diaries)//组装起床/睡觉时间数据
	{
		String data="";
		for(int i=0;i<diaries.size();i++)
		{
			String [] diary=diaries.get(i);
			data=data+'"'+diary[0]+'"'+"\t"+'"'+diary[1]+'"'+"\t"+'"'+diary[2]+'"'+"\r\n";
		}
		return data;
	}
	
	public static String makeCommitComplaint(String userId,ArrayList<String[]> diaries)//组装病人主诉数据
	{
		String data="";
		for(int i=0;i<diaries.size();i++)
		{
			String [] diary=diaries.get(i);
			data=data+'"'+diary[0]+'"'+"\t"+'"'+diary[1]+'"'+"\r\n";
		}
		return data;
	}
	
	public static String makeCommitExaminationResults(String userId,ArrayList<String[]> diaries)//组装问卷调查结果数据
	{
		String data="";
		if(diaries.size()>0)
		{
			 data="<CommitSurvey>";
			for(int i=0;i<diaries.size();i++)
			{
				String [] diary=diaries.get(i);
				String time="";
				time=time+diary[0].substring(0,4)+diary[0].substring(5,7)+diary[0].substring(8,10)+diary[0].substring(11,13)+diary[0].substring(14,16)+
				diary[0].substring(17,19);
				
				data=data+"<Survey PatientId="+'"'+time+'"'
					 +"TypeId="+'"'+diary[1]+'"'
					 +"BeginTime="+'"'+diary[2]+'"'
					 +"EndTime="+'"'+diary[3]+'"'
					 +"TotalScore="+'"'+diary[4]+'"'+">"
					 
					 +"<Topic TopicId="+'"'+diary[5]+'"'
					 +"GradeType="+'"'+diary[6]+'"'
					 +"GradeVal="+'"'+diary[7]+'"'+"/>"
				
					 +"<Topic TopicId="+'"'+diary[8]+'"'
					 +"GradeType="+'"'+diary[9]+'"'
					 +"GradeVal="+'"'+diary[10]+'"'+"/>"
					 +"</Survey>";
				
			}
			data=data+"</CommitSurvey>";
		}
			return data;
	}
	
	public static String makeCommitUFR(String userId,ArrayList<String[]> diaries)//组装尿流率数据
	{
		Log.i("mylog","begin makeCommitUFR.......");
		String data="";
		if(diaries.size()>0)
		{
					 Log.i("mylog","diaries.size:"+diaries.size());
					 data="<CommitUFR>";
					for(int i=0;i<diaries.size();i++)
					{
						String [] diary=diaries.get(i);
//						String time="";
//						time=time+diary[0].substring(0,4)+diary[0].substring(5,7)+diary[0].substring(8,10)+diary[0].substring(11,13)+diary[0].substring(14,16)+
//						diary[0].substring(17,19);
						
						data=data+"<Record StartTime="+'"'+diary[0]+'"'+" "
							 +"Duration="+'"'+diary[1]+'"'+" "
							 +"VaryVal="+'"'+diary[2]+'"'+" "
							 +"MeanFlow="+'"'+diary[3]+'"'+" "
							 +"Q90="+'"'+diary[4]+'"'+" "
							 +"PeakFlow="+'"'+diary[5]+'"'+" "
							 +"_2SecFlow="+'"'+diary[6]+'"'+" "
							 +"FlowTime="+'"'+diary[7]+'"'+" "
							 +"VoidingTime="+'"'+diary[8]+'"'+" "
							 +"T90="+'"'+diary[9]+'"'+" "
							 +"TimeToPeak="+'"'+diary[10]+'"'+" "
							 +"VoidVolume="+'"'+diary[11]+'"'+" "
							 +"AmountUnit="+'"'+diary[12]+'"'+" "
							 +"Proportion="+'"'+diary[13]+'"'+">"
							 
							 +"<IntervalTime  BeginPos="+'"'+diary[14]+'"'+" "
							 +"EndPos="+'"'+diary[15]+'"'+"/>"
							 
							 +"</Record>";
						
					}
					data=data+"</CommitUFR>";
		}
		else
			Log.i("mylog","ufr datas null!!!");
		Log.i("mylog","data:"+data);
		return data;
	}
	
	public static String makeCommitUrinaryUrgency(String userId,ArrayList<String[]> diaries)//组装尿急数据
	{
		String data="";
		if(diaries.size()>0)
		{
		 data="<CommitUrinaryUrgency>";
		for(int i=0;i<diaries.size();i++)
		{
			String [] diary=diaries.get(i);
			data=data+"<Record Operator="+'"'+diary[0]+'"'
				 +"UniqueId="+'"'+diary[1]+'"'
				 +"Datetime="+'"'+diary[2]+'"'+"/>"				 
				 +"</Record>";
			
		}
		data=data+"</CommitUrinaryUrgency >";
		}
		return data;
	}


}
