package com.szxys.mhub.subsystem.virtual;
/**
 * 消息实体类
 * 
 * @author 苏佩
 * 
 */
public class MhubMessage {
	
	private String doctorId;		//医生ID
	private String doctorName;		//医生姓名
	private String patientId;		//病人ID
	private String patientName;		//病人姓名
	private String content;			//信息内容
	private String time;			//接收/发送时间
	private String sourceMsgId;		//SourceMsgId
	private int    appId;			//业务类型码
	private int    mhubmsgId;       //消息ID
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getDoctorName() {
		return doctorName;
	}
	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSourceMsgId() {
		return sourceMsgId;
	}
	public void setSourceMsgId(String sourceMsgId) {
		this.sourceMsgId = sourceMsgId;
	}
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public void setMhubmsgId(int mhubmsgId) {
		this.mhubmsgId = mhubmsgId;
	}
	public int getMhubmsgId() {
		return mhubmsgId;
	}
	
	

}
