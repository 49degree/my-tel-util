package com.szxys.mhub.subsystem.mets.bean;

public class Drinkandurine {
	//Entity class of TABLE tb_InAndOut
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private int c_Units=0;// SMALLINT NOT NULL DEFAULT 0, \
	private String c_DateTime;// TIMESTAMP NOT NULL DEFAULT '', 
	private float c_Quantity=0;// FLOAT NOT NULL DEFAULT 0, 
	private int c_Type=0;// SMALLINT NOT NULL DEFAULT 0, [0 drink(饮水) 1 urine(排尿) 2 urinary urgency(尿急) 3 urine loss(尿失禁)]
	private int c_IsUpload=0;// BOOL NOT NULL DEFAULT 0, \
	private float c_Proportion;// FLOAT, 
	private int c_UfrId=0;// INTEGER DEFAULT 0, 
	private int c_CollectType=0;// SMALLINT NOT NULL DEFAULT 0, \采集类型: 1采集器，2病人输入，3医生输入
	private String c_UniqueId;// TEXT UNIQUE NOT NULL, 
	private int c_Status=0;// SMALLINT NOT NULL DEFAULT 0
	
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
	public void setC_Quantity(float c_Quantity) {
		this.c_Quantity = c_Quantity;
	}
	public float getC_Quantity() {
		return c_Quantity;
	}
	public void setC_Type(int c_Type) {
		this.c_Type = c_Type;
	}
	public int getC_Type() {
		return c_Type;
	}
	public void setC_IsUpload(int c_IsUpload) {
		this.c_IsUpload = c_IsUpload;
	}
	public int getC_IsUpload() {
		return c_IsUpload;
	}
	public void setC_Proportion(float c_Proportion) {
		this.c_Proportion = c_Proportion;
	}
	public float getC_Proportion() {
		return c_Proportion;
	}
	public void setC_UfrId(int c_UfrId) {
		this.c_UfrId = c_UfrId;
	}
	public int getC_UfrId() {
		return c_UfrId;
	}
	public void setC_CollectType(int c_CollectType) {
		this.c_CollectType = c_CollectType;
	}
	public int getC_CollectType() {
		return c_CollectType;
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
