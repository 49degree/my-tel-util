package com.szxys.mhub.subsystem.mets.bean;

public class Urinerecord {
	//Entity class of TABLE tb_UrineRecord
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, \
	private int c_Units=0;// SMALLINT NOT NULL DEFAULT 0, 
	private String c_DateTime;// TIMESTAMP NOT NULL DEFAULT '', 
	private int c_Duration=0;// SMALLINT NOT NULL DEFAULT 0, 
	private float c_MeanFlow;// FLOAT, \
	private float c_Q90;// FLOAT, 
	private float c_PeakFlow;// FLOAT, 
	private float c_2SecFlow;// FLOAT, 
	private float c_VoidingTime;// FLOAT, 
	private float c_FlowTime;// FLOAT, 
	private float c_T90;// FLOAT, 
	private float c_TimeToPeak;// FLOAT, \
	private float c_VoidVolume;// FLOAT, 
	private int c_StartPos;// INTEGER, 
	private int c_EndPos;// INTEGER, 
	private int c_IsUpload=0;// BOOL NOT NULL DEFAULT 0
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_Units(int c_Units) {
		this.c_Units = c_Units;
	}
	public int getC_Units() {
		return c_Units;
	}
	public void setC_DateTime(String c_DateTime) {
		this.c_DateTime = c_DateTime;
	}
	public String getC_DateTime() {
		return c_DateTime;
	}
	public void setC_Duration(int c_Duration) {
		this.c_Duration = c_Duration;
	}
	public int getC_Duration() {
		return c_Duration;
	}
	public void setC_MeanFlow(float c_MeanFlow) {
		this.c_MeanFlow = c_MeanFlow;
	}
	public float getC_MeanFlow() {
		return c_MeanFlow;
	}
	public void setC_Q90(float c_Q90) {
		this.c_Q90 = c_Q90;
	}
	public float getC_Q90() {
		return c_Q90;
	}
	public void setC_PeakFlow(float c_PeakFlow) {
		this.c_PeakFlow = c_PeakFlow;
	}
	public float getC_PeakFlow() {
		return c_PeakFlow;
	}
	public void setC_2SecFlow(float c_2SecFlow) {
		this.c_2SecFlow = c_2SecFlow;
	}
	public float getC_2SecFlow() {
		return c_2SecFlow;
	}
	public void setC_VoidingTime(float c_VoidingTime) {
		this.c_VoidingTime = c_VoidingTime;
	}
	public float getC_VoidingTime() {
		return c_VoidingTime;
	}
	public void setC_FlowTime(float c_FlowTime) {
		this.c_FlowTime = c_FlowTime;
	}
	public float getC_FlowTime() {
		return c_FlowTime;
	}
	public void setC_T90(float c_T90) {
		this.c_T90 = c_T90;
	}
	public float getC_T90() {
		return c_T90;
	}
	public void setC_TimeToPeak(float c_TimeToPeak) {
		this.c_TimeToPeak = c_TimeToPeak;
	}
	public float getC_TimeToPeak() {
		return c_TimeToPeak;
	}
	public void setC_VoidVolume(float c_VoidVolume) {
		this.c_VoidVolume = c_VoidVolume;
	}
	public float getC_VoidVolume() {
		return c_VoidVolume;
	}
	public void setC_StartPos(int c_StartPos) {
		this.c_StartPos = c_StartPos;
	}
	public int getC_StartPos() {
		return c_StartPos;
	}
	public void setC_EndPos(int c_EndPos) {
		this.c_EndPos = c_EndPos;
	}
	public int getC_EndPos() {
		return c_EndPos;
	}
	public void setC_IsUpload(int c_IsUpload) {
		this.c_IsUpload = c_IsUpload;
	}
	public int getC_IsUpload() {
		return c_IsUpload;
	}
}
