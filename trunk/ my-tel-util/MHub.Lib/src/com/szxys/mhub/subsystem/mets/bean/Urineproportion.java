package com.szxys.mhub.subsystem.mets.bean;

public class Urineproportion {
	//Entity class of TABLE tb_UrineProportion
	private int c_Id;// INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	private String c_DateTime;// TIMESTAMP NOT NULL DEFAULT '', 
	private float c_Proportion;// FLOAT, 
	private int c_IsMatch=0;// BOOL NOT NULL DEFAULT 0
	
	public void setC_Id(int c_Id) {
		this.c_Id = c_Id;
	}
	public int getC_Id() {
		return c_Id;
	}
	public void setC_DateTime(String c_DateTime) {
		this.c_DateTime = c_DateTime;
	}
	public String getC_DateTime() {
		return c_DateTime;
	}
	public void setC_Proportion(float c_Proportion) {
		this.c_Proportion = c_Proportion;
	}
	public float getC_Proportion() {
		return c_Proportion;
	}
	public void setC_IsMatch(int c_IsMatch) {
		this.c_IsMatch = c_IsMatch;
	}
	public int getC_IsMatch() {
		return c_IsMatch;
	}
}
