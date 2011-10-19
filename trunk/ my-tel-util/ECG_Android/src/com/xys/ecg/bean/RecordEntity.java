package com.xys.ecg.bean;

public class RecordEntity {
    private int recordID;      //记录ID
	private int userID;        //用户ID
	private long startTime;    //开始时间
	private String filePath;   //文件路径
	private int mode;          //导联或触摸模式
	private int state;         //是否同步标志
	private int uploaded;      //上传或未上传状态
	

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
