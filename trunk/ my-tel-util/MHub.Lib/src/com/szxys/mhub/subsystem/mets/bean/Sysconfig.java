package com.szxys.mhub.subsystem.mets.bean;

public class Sysconfig {
	//Entity class of TABLE tb_SystemConfig
	private String c_Hospital;// TEXT, 
	private String c_DoctorsNo;// TEXT, 
	private String c_DoctorsName;// TEXT 
	private String c_PatientNo;// TEXT NOT NULL, 
	private String c_PatientName;// TEXT, 
	private int c_DevType;// SMALLINT NOT NULL, 
	private String c_MobileId;// TEXT NOT NULL, 
	private String c_MobileName;// TEXT, 
	private String c_CollectorId;// TEXT NOT NULL
	private String c_CollectorName;// TEXT, 
	private String c_RationDateTime;// TIMESTAMP DEFAULT '', 
	private String c_RationGuDt;// TIMESTAMP DEFAULT '', 
	private String c_RecyDateTime;// TIMESTAMP DEFAULT '' 
	private int c_CollectDtInterval;// SMALLINT NOT NULL , 
	private int c_MaxDuration=160;// SMALLINT DEFAULT 160, 
	private int c_NoDataTime=600;// SMALLINT DEFAULT 600 
	private int c_SendDtInterval;// SMALLINT NOT NULL, 
	private int c_MeasuringCupWeight;// SMALLINT NOT NULL, 
	private String c_Version;// TEXT, 
	private String c_Copyright;// TEXT, 
	private String c_WebServiceUrl;// TEXT NOT NULL, 
	private int c_IsRecycling;// BOOL NOT NULL, 
	private int c_IsRegister;// BOOL NOT NULL, 
	private int c_AutoCloseBt;// BOOL NOT NULL, 
	private int c_HaveSpecificGravity;// BOOL NOT NULL, 
	private int c_RebootTimer=-1;// SMALLINT DEFAULT -1 , 
	private int c_TimeInitialLead=0;// SMALLINT DEFAULT 0, 
	private String c_GetUpAlarm;// TIMESTAMP DEFAULT '', 
	private String c_GotoBedAlarm;// TIMESTAMP DEFAULT '', 
	private String c_GprsGuid;// TEXT , 
	private String c_LastNetCommTime;// TIMESTAMP
	
	public void setC_Hospital(String c_Hospital) {
		this.c_Hospital = c_Hospital;
	}
	public String getC_Hospital() {
		return c_Hospital;
	}
	public void setC_DoctorsNo(String c_DoctorsNo) {
		this.c_DoctorsNo = c_DoctorsNo;
	}
	public String getC_DoctorsNo() {
		return c_DoctorsNo;
	}
	public void setC_DoctorsName(String c_DoctorsName) {
		this.c_DoctorsName = c_DoctorsName;
	}
	public String getC_DoctorsName() {
		return c_DoctorsName;
	}
	public void setC_PatientNo(String c_PatientNo) {
		this.c_PatientNo = c_PatientNo;
	}
	public String getC_PatientNo() {
		return c_PatientNo;
	}
	public void setC_PatientName(String c_PatientName) {
		this.c_PatientName = c_PatientName;
	}
	public String getC_PatientName() {
		return c_PatientName;
	}
	public void setC_DevType(int c_DevType) {
		this.c_DevType = c_DevType;
	}
	public int getC_DevType() {
		return c_DevType;
	}
	public void setC_MobileId(String c_MobileId) {
		this.c_MobileId = c_MobileId;
	}
	public String getC_MobileId() {
		return c_MobileId;
	}
	public void setC_MobileName(String c_MobileName) {
		this.c_MobileName = c_MobileName;
	}
	public String getC_MobileName() {
		return c_MobileName;
	}
	public void setC_CollectorId(String c_CollectorId) {
		this.c_CollectorId = c_CollectorId;
	}
	public String getC_CollectorId() {
		return c_CollectorId;
	}
	public void setC_CollectorName(String c_CollectorName) {
		this.c_CollectorName = c_CollectorName;
	}
	public String getC_CollectorName() {
		return c_CollectorName;
	}
	public void setC_RationDateTime(String c_RationDateTime) {
		this.c_RationDateTime = c_RationDateTime;
	}
	public String getC_RationDateTime() {
		return c_RationDateTime;
	}
	public void setC_RationGuDt(String c_RationGuDt) {
		this.c_RationGuDt = c_RationGuDt;
	}
	public String getC_RationGuDt() {
		return c_RationGuDt;
	}
	public void setC_RecyDateTime(String c_RecyDateTime) {
		this.c_RecyDateTime = c_RecyDateTime;
	}
	public String getC_RecyDateTime() {
		return c_RecyDateTime;
	}
	public void setC_CollectDtInterval(int c_CollectDtInterval) {
		this.c_CollectDtInterval = c_CollectDtInterval;
	}
	public int getC_CollectDtInterval() {
		return c_CollectDtInterval;
	}
	public void setC_MaxDuration(int c_MaxDuration) {
		this.c_MaxDuration = c_MaxDuration;
	}
	public int getC_MaxDuration() {
		return c_MaxDuration;
	}
	public void setC_NoDataTime(int c_NoDataTime) {
		this.c_NoDataTime = c_NoDataTime;
	}
	public int getC_NoDataTime() {
		return c_NoDataTime;
	}
	public void setC_SendDtInterval(int c_SendDtInterval) {
		this.c_SendDtInterval = c_SendDtInterval;
	}
	public int getC_SendDtInterval() {
		return c_SendDtInterval;
	}
	public void setC_MeasuringCupWeight(int c_MeasuringCupWeight) {
		this.c_MeasuringCupWeight = c_MeasuringCupWeight;
	}
	public int getC_MeasuringCupWeight() {
		return c_MeasuringCupWeight;
	}
	public void setC_Version(String c_Version) {
		this.c_Version = c_Version;
	}
	public String getC_Version() {
		return c_Version;
	}
	public void setC_Copyright(String c_Copyright) {
		this.c_Copyright = c_Copyright;
	}
	public String getC_Copyright() {
		return c_Copyright;
	}
	public void setC_WebServiceUrl(String c_WebServiceUrl) {
		this.c_WebServiceUrl = c_WebServiceUrl;
	}
	public String getC_WebServiceUrl() {
		return c_WebServiceUrl;
	}
	public void setC_IsRecycling(int c_IsRecycling) {
		this.c_IsRecycling = c_IsRecycling;
	}
	public int getC_IsRecycling() {
		return c_IsRecycling;
	}
	public void setC_IsRegister(int c_IsRegister) {
		this.c_IsRegister = c_IsRegister;
	}
	public int getC_IsRegister() {
		return c_IsRegister;
	}
	public void setC_AutoCloseBt(int c_AutoCloseBt) {
		this.c_AutoCloseBt = c_AutoCloseBt;
	}
	public int getC_AutoCloseBt() {
		return c_AutoCloseBt;
	}
	public void setC_HaveSpecificGravity(int c_HaveSpecificGravity) {
		this.c_HaveSpecificGravity = c_HaveSpecificGravity;
	}
	public int getC_HaveSpecificGravity() {
		return c_HaveSpecificGravity;
	}
	public void setC_RebootTimer(int c_RebootTimer) {
		this.c_RebootTimer = c_RebootTimer;
	}
	public int getC_RebootTimer() {
		return c_RebootTimer;
	}
	public void setC_TimeInitialLead(int c_TimeInitialLead) {
		this.c_TimeInitialLead = c_TimeInitialLead;
	}
	public int getC_TimeInitialLead() {
		return c_TimeInitialLead;
	}
	public void setC_GetUpAlarm(String c_GetUpAlarm) {
		this.c_GetUpAlarm = c_GetUpAlarm;
	}
	public String getC_GetUpAlarm() {
		return c_GetUpAlarm;
	}
	public void setC_GotoBedAlarm(String c_GotoBedAlarm) {
		this.c_GotoBedAlarm = c_GotoBedAlarm;
	}
	public String getC_GotoBedAlarm() {
		return c_GotoBedAlarm;
	}
	public void setC_GprsGuid(String c_GprsGuid) {
		this.c_GprsGuid = c_GprsGuid;
	}
	public String getC_GprsGuid() {
		return c_GprsGuid;
	}
	public void setC_LastNetCommTime(String c_LastNetCommTime) {
		this.c_LastNetCommTime = c_LastNetCommTime;
	}
	public String getC_LastNetCommTime() {
		return c_LastNetCommTime;
	}
}
