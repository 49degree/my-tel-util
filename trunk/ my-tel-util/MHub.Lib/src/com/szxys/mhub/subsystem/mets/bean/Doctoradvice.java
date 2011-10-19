package com.szxys.mhub.subsystem.mets.bean;

public class Doctoradvice {
	//Entity class of TABLE tb_DoctorAdviceInfo
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \
	private int c_Type; // SMALLINT NOT NULL, 
	private String c_InfoId;// TEXT NOT NULL DEFAULT '', 
	private String c_RecvDt;// TIMESTAMP NOT NULL DEFAULT '', 
	private String c_SendDt;// TIMESTAMP NOT NULL DEFAULT '', 
	private String c_DoctorName;// TEXT, c_Content TEXT, \
	private String c_Content; //c_Content TEXT, \
	private  int c_IsRead=0;// BOOL NOT NULL DEFAULT 0, 
	private  int c_IsReply=0;// BOOL NOT NULL DEFAULT 0
	private  int c_IsUpload;// BOOL NOT NULL DEFAULT 0, 
	private  String c_RemindTime;// TIMESTAMP, 
	private  String c_Interval;// TEXT, 
	private  String c_ExpireTime;// TIMESTAMP, 
	private  String c_NextRemindTime;// TIMESTAMP);
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_Type(int c_Type) {
		this.c_Type = c_Type;
	}
	public int getC_Type() {
		return c_Type;
	}
	public void setC_InfoId(String c_InfoId) {
		this.c_InfoId = c_InfoId;
	}
	public String getC_InfoId() {
		return c_InfoId;
	}
	public void setC_RecvDt(String c_RecvDt) {
		this.c_RecvDt = c_RecvDt;
	}
	public String getC_RecvDt() {
		return c_RecvDt;
	}
	public void setC_SendDt(String c_SendDt) {
		this.c_SendDt = c_SendDt;
	}
	public String getC_SendDt() {
		return c_SendDt;
	}
	public void setC_DoctorName(String c_DoctorName) {
		this.c_DoctorName = c_DoctorName;
	}
	public String getC_DoctorName() {
		return c_DoctorName;
	}
	public void setC_Content(String c_Content) {
		this.c_Content = c_Content;
	}
	public String getC_Content() {
		return c_Content;
	}
	public void setC_IsRead(int c_IsRead) {
		this.c_IsRead = c_IsRead;
	}
	public int getC_IsRead() {
		return c_IsRead;
	}
	public void setC_IsReply(int c_IsReply) {
		this.c_IsReply = c_IsReply;
	}
	public int getC_IsReply() {
		return c_IsReply;
	}
	public void setC_IsUpload(int c_IsUpload) {
		this.c_IsUpload = c_IsUpload;
	}
	public int getC_IsUpload() {
		return c_IsUpload;
	}
	public void setC_RemindTime(String c_RemindTime) {
		this.c_RemindTime = c_RemindTime;
	}
	public String getC_RemindTime() {
		return c_RemindTime;
	}
	public void setC_Interval(String c_Interval) {
		this.c_Interval = c_Interval;
	}
	public String getC_Interval() {
		return c_Interval;
	}
	public void setC_ExpireTime(String c_ExpireTime) {
		this.c_ExpireTime = c_ExpireTime;
	}
	public String getC_ExpireTime() {
		return c_ExpireTime;
	}
	public void setC_NextRemindTime(String c_NextRemindTime) {
		this.c_NextRemindTime = c_NextRemindTime;
	}
	public String getC_NextRemindTime() {
		return c_NextRemindTime;
	}
}
