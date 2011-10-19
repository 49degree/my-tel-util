package com.xys.ecg.bean;

public class DoctorAdviceEntity {
     
	private int adviceID;   //����ID
	private int userID;     //�û�ID
	private String publishTime;   //����ʱ��
	private String arriveTime;    //�´�ʱ��
	private String doctorName;    //ҽ������
	private String content;       //����
	private int readFlag;         //��ȡ��־
	
	public DoctorAdviceEntity()
	{
		
	}
	
	public DoctorAdviceEntity(int userID, String publishTime, String arriveTime, String doctorName, String content, int readFlag)
	{
		this.userID = userID;
		this.publishTime = publishTime;
		this.arriveTime = arriveTime;
		this.doctorName = doctorName;
		this.content = content;
		this.readFlag = readFlag;
	}
	public void setAdviceID(int adviceID)
	{
		this.adviceID = adviceID;
	}
	public int getAdviceID()
	{
		return this.adviceID;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	public String getPublishTime()
	{
		return this.publishTime;
	}
	
	public void setPublishTime(String publishTime)
	{
		this.publishTime = publishTime;
	}
	
	public String getArriveTime()
	{
		return this.arriveTime;
	}
	
	public void setArriveTime(String arriveTime)
	{
		this.arriveTime = arriveTime;
	}
	
	public String getDoctorName()
	{
		return this.doctorName;
	}
	
	public void setDoctorName(String doctorName)
	{
		this.doctorName = doctorName;
	}
	
	public String getContent()
	{
		return this.content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
	
	public int getReadFlag()
	{
		return this.readFlag;
	}
	
	public void setReadFalg(int readFlag)
	{
		this.readFlag = readFlag;
	}
	
	
}
