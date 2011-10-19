package com.guanri.android.insurance.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.guanri.android.lib.log.Logger;

public class UserInfoBean {
	public static Logger logger = Logger.getLogger(UserInfoBean.class);//日志对象
	
	private byte[] PSW	        =null;//   ASC	8	终端后续的通信均采用这个动态密钥加密
	private String curTime	    =null;//   ASC	19	表示服务器当前的时间 YYYY-MM-DD hh:mm:ss以下同
	private byte isOK		  =0;//    HEX	1	0：表示可用；1：表示禁止使用
	private byte userLevel	  =0;//    	HEX	1	0x00=管理员；0x01=一般操作员
	private String userName		  =null;//    ASC	10	返回该工号在后台注册的姓名
	private String firstIP		  =null;//    HEX	4	如0x32 0x64 0x02 0xC8表示IP地址为50.100.2.200
	private short firstPort		=0;//      HEX	2	服务器的端口，小端模式，如0xB3 0x15表示值0x15B3，表示端口号5555
	private String firstUseTime		  =null;//    ASC	19	YYYY-MM-DD HH:MM:SS终端在该时间后，将此组IP、端口作为A组IP、主端口使用，如果没有新的A组IP等信息，则全部填0x00
	private String secondIP		  =null;//    HEX	4	
	private short secondPort	  =0;//    	HEX	2	
	private String secondUseTime		  =null;//    ASC	19	定于参考A组
	private String newSoftVer		=null;//     当前系统中最新软件版本 ASC	20	
	private byte mustUpdate		=0;//      HEX	1	0：不需要升级1：必须进行升级操作2：功能改善，建议性升级
	private byte newErrorMsg	=0;//      	HEX	1	0：表示没有需要更新的信息其它表示需要更新的错误信息版本
	private String SMSID		    =null;// 后台短信接入号码 ASC	12	
	private String MSG		      =null;//ASC	160	终端收到此消息，如果有内容，则必须显示出来提示用户
	private String[][] insuPlanRecord	  =null;//可以销售的业务方案列表ASC	100	只针对操作员有效，每个操作员同时最多可以10个业务方案，采用CARDCODE(8字节)＋PLANNO（2字节）的方式下载
	private String branchName		=null;//     分公司名称(签单机构) ASC	30	
	private String branchAddr		=null;//       分公司地址(保险分公司地址)ASC	40	
	private String stationName	=null;//机构名称ASC	30	目前只有平安\泰康\百年有该字段 ASC	16	目前只有泰康有该字段
	private String stationTel	=null;//分公司联系电话 ASC	16	目前只有泰康有该字段
	
	private String userId = null;
	

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public byte[] getPSW() {
		return PSW;
	}
	public void setPSW(byte[] PSW) {
		this.PSW = PSW;
	}
	public String getCurTime() {
		return curTime;
	}
	public void setCurTime(String curTime) {
		this.curTime = curTime;
	}
	public byte getIsOK() {
		return isOK;
	}
	public void setIsOK(byte isOK) {
		this.isOK = isOK;
	}
	public byte getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(byte userLevel) {
		this.userLevel = userLevel;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFirstIP() {
		return firstIP;
	}
	public void setFirstIP(String firstIP) {
		this.firstIP = firstIP;
	}
	public short getFirstPort() {
		return firstPort;
	}
	public void setFirstPort(short firstPort) {
		this.firstPort = firstPort;
	}
	public String getFirstUseTime() {
		return firstUseTime;
	}
	public void setFirstUseTime(String firstUseTime) {
		this.firstUseTime = firstUseTime;
	}
	public String getSecondIP() {
		return secondIP;
	}
	public void setSecondIP(String secondIP) {
		this.secondIP = secondIP;
	}
	public short getSecondPort() {
		return secondPort;
	}
	public void setSecondPort(short secondPort) {
		this.secondPort = secondPort;
	}
	public String getSecondUseTime() {
		return secondUseTime;
	}
	public void setSecondUseTime(String secondUseTime) {
		this.secondUseTime = secondUseTime;
	}
	public String getNewSoftVer() {
		return newSoftVer;
	}
	public void setNewSoftVer(String newSoftVer) {
		newSoftVer = newSoftVer;
	}
	public byte getMustUpdate() {
		return mustUpdate;
	}
	public void setMustUpdate(byte mustUpdate) {
		this.mustUpdate = mustUpdate;
	}
	public byte getNewErrorMsg() {
		return newErrorMsg;
	}
	public void setNewErrorMsg(byte newErrorMsg) {
		this.newErrorMsg = newErrorMsg;
	}
	public String getSMSID() {
		return SMSID;
	}
	public void setSMSID(String SMSID) {
		this.SMSID = SMSID;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String MSG) {
		this.MSG = MSG;
	}
	public String[][] getInsuPlanRecord() {
		return insuPlanRecord;
	}
	public void setInsuPlanRecord(String[][] insuPlanRecord) {
		this.insuPlanRecord = insuPlanRecord;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBranchAddr() {
		return branchAddr;
	}
	public void setBranchAddr(String branchAddr) {
		this.branchAddr = branchAddr;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getStationTel() {
		return stationTel;
	}
	public void setStationTel(String stationTel) {
		this.stationTel = stationTel;
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		Field[] fields = UserInfoBean.class.getDeclaredFields();
		
		StringBuffer methodName = new StringBuffer("get");
		//methodName.append(f.getName());
		Method getMethod = null;
		for(Field f:fields){
			if(Modifier.isStatic(f.getModifiers()))
				continue;
			try{
				methodName = new StringBuffer("get");
				methodName.append(f.getName().substring(0, 1).toUpperCase()).append(f.getName().substring(1));

				getMethod = UserInfoBean.class.getMethod(methodName.toString());
				logger.debug((methodName+":"+String.valueOf(getMethod.invoke(this))));
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return str.toString();
	}
	
}
