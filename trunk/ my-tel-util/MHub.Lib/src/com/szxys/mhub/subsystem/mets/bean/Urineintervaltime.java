package com.szxys.mhub.subsystem.mets.bean;

public class Urineintervaltime {
	//Entity class of TABLE tb_UrineIntervalTime
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	private int c_UrineId;// INTEGER NOT NULL
	private float c_BeginPos;// FLOAT, 
	private float c_EndPos;// FLOAT
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_UrineId(int c_UrineId) {
		this.c_UrineId = c_UrineId;
	}
	public int getC_UrineId() {
		return c_UrineId;
	}
	public void setC_BeginPos(float c_BeginPos) {
		this.c_BeginPos = c_BeginPos;
	}
	public float getC_BeginPos() {
		return c_BeginPos;
	}
	public void setC_EndPos(float c_EndPos) {
		this.c_EndPos = c_EndPos;
	}
	public float getC_EndPos() {
		return c_EndPos;
	}
}
