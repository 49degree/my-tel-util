package com.szxys.mhub.subsystem.mets.bean;

public class Getupgotobed {
	//Entity class of TABLE tb_GotoBedGetUp
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \
	private String c_Date;// TIMESTAMP NOT NULL DEFAULT '', 
	private String c_GetUpTime;// TIMESTAMP DEFAULT '', 
	private String c_GotoBedTime;// TIMESTAMP DEFAULT '', 
	private int c_IsUpload=0;// BOOL NOT NULL DEFAULT 0, \表示数据是否已经上传
	private String c_UniqueId;// TEXT UNIQUE NOT NULL, 
	private int c_Status;// SMALLINT NOT NULL DEFAULT 0 表示数据状态，1 新增，2 修改，3 删除
	// isupload = 1 && status = 1 时不上传
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_Date(String c_Date) {
		this.c_Date = c_Date;
	}
	public String getC_Date() {
		return c_Date;
	}
	public void setC_GetUpTime(String c_GetUpTime) {
		this.c_GetUpTime = c_GetUpTime;
	}
	public String getC_GetUpTime() {
		return c_GetUpTime;
	}
	public void setC_GotoBedTime(String c_GotoBedTime) {
		this.c_GotoBedTime = c_GotoBedTime;
	}
	public String getC_GotoBedTime() {
		return c_GotoBedTime;
	}
	public void setC_IsUpload(int c_IsUpload) {
		this.c_IsUpload = c_IsUpload;
	}
	public int getC_IsUpload() {
		return c_IsUpload;
	}
	public void setC_UniqueId(String c_UniqueId) {
		this.c_UniqueId = c_UniqueId;
	}
	public String getC_UniqueId() {
		return c_UniqueId;
	}
	public void setC_Status(int c_Status) {
		this.c_Status = c_Status;
	}
	public int getC_Status() {
		return c_Status;
	}
}
