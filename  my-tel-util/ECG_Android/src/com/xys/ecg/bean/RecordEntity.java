package com.xys.ecg.bean;

public class RecordEntity {
    private int recordID;      //��¼ID
	private int userID;        //�û�ID
	private long startTime;    //��ʼʱ��
	private String filePath;   //�ļ�·��
	private int mode;          //��������ģʽ
	private int state;         //�Ƿ�ͬ����־
	private int uploaded;      //�ϴ���δ�ϴ�״̬
	

	public RecordEntity(){
		
	}
	public RecordEntity(int userID ,long startTime ,String filePath ,int mode ,int state)
	{
		this.userID = userID;
		this.startTime = startTime;
		this.filePath = filePath;
		this.mode = mode;
		this.state = state;
	}
	
	public int getRecordID()
	{
		return this.recordID;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	
	public void setUserID(int userID)
	{
		this.userID = userID;
	}
	
	public long getStartTime()
	{
		return this.startTime;
	}
	
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	public int getMode()
	{
		return this.mode;
	}
	
	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	public int getState()
	{
		return this.state;
	}
	
	public void setState(int state)
	{
		this.state = state;
	}
	
	public int getUploaded() {
		return uploaded;
	}
	
	public void setUploaded(int uploaded) {
		this.uploaded = uploaded;
	}
}
