package com.xys.ecg.device;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.xys.ecg.utils.BeanRefUtil;
import com.xys.ecg.utils.TypeConversion;

public class DeviceRation_bak {
	short   iAppAllLen;                  
	short   iAppNeedLen;
	char[]    szAppTypeId = new char[50];			 // 业务类型
	short   iPatientPKAllLen;
	short   iPatientPKNeedLen;
	char[]    szPatientPkId = new char[50];			 // 病人ID
	short   iPatientAllLen;
	short   iPatientNeedLen;
	char[]    szPatientName = new char[110];			 // 病人姓名
	short   iDoctorAllLen;
	short   iDoctorNeedLen;
	char[]    szDoctorName = new char[50];			 // 医生姓名
	short   iMobileIdAllLen;
	short   iMobileIdNeedLen;
	char[]	szMobileId = new char[50];				 // e+医终端编号
	short   iMobileBtAllLen;
	short   iMobileBtNeedLen;
	char[]	szMobileBtAddr = new char[50];			 // e+医终端蓝牙地址
	short   iForceAllLen;
	short   iForceNeedLen;
	int     iForce;                      //是否强制发放 
	short   iCheckAllLen;
	short   iCheckNeedLen;
	int     iCheck;                      // 是否检验蓝牙地址
	short   iCollectorCountAllLen;
	short   iCollectorCountNeedLen;
	int     iCollectorCount;             // 采集器个数
	short   iCollectorIdAllLen;
	short   iCollectorIdNeedLen;
	char[]	szCollectorId = new char[500];			 // 采集器编号
	short   iCollectorTypeAllLen;
	short   iCollectorTypeNeedLen;
	char[]	szCollectorType = new char[500];		 // 采集器类型
	short   iCollectorBtAllLen;
	short   iCollectorBtNeedLen;
	char[]    szCollectorBtAddr = new char[500];		 // 采集器蓝牙地址
	short   iNetConnAllLen;
	short   iNetConnNeedLen;
	char[]	szNetConnName = new char[70];			 // 网络连接名称
	short   iNetApnAllLen;
	short   iNetApnNeedLen;
	char[]	szNetApnName = new char[70];			 // 接入点名称
	short   iNetUserAllLen;
	short   iNetUserNeedLen;
	char[]    szNetUserName = new char[70];			 // 网络连接用户名
	short   iNetPSAllLen;
	short   iNetPSNeedLen;
	char[]    szNetPassword = new char[70];           // 网络连接密码
	short   iSetParamAllLen;
	short   iSetParamNeedLen;
	char[]    szSetParam = new char[200];             // 配置参数（多个参数，用：号分开）
	short   iWebWAllLen;
	short   iWebWNeedLen;
	char[]    szWebServiceAddrOfWan = new char[100];  // 业务外网WebService地址
	short   iWebLAllLen;
	short   iWebLNeedLen;
	char[]	szWebServiceAddrOfLan = new char[100];  // 业务内网WebService地址
	short   iVCodeAllLen;
	short   iVCodeNeedLen;
	char[]	szValidateCode = new char[100];          // 访问业务WebService时的验证码
	
	
	
	public short getiAppAllLen() {
		return iAppAllLen;
	}

	public void setiAppAllLen(short iAppAllLen) {
		this.iAppAllLen = iAppAllLen;
	}

	public short getiAppNeedLen() {
		return iAppNeedLen;
	}

	public void setiAppNeedLen(short iAppNeedLen) {
		this.iAppNeedLen = iAppNeedLen;
	}

	public char[] getSzAppTypeId() {
		return szAppTypeId;
	}

	public void setSzAppTypeId(char[] szAppTypeId) {
		this.szAppTypeId = szAppTypeId;
	}

	public short getiPatientPKAllLen() {
		return iPatientPKAllLen;
	}

	public void setiPatientPKAllLen(short iPatientPKAllLen) {
		this.iPatientPKAllLen = iPatientPKAllLen;
	}

	public short getiPatientPKNeedLen() {
		return iPatientPKNeedLen;
	}

	public void setiPatientPKNeedLen(short iPatientPKNeedLen) {
		this.iPatientPKNeedLen = iPatientPKNeedLen;
	}

	public char[] getSzPatientPkId() {
		return szPatientPkId;
	}

	public void setSzPatientPkId(char[] szPatientPkId) {
		this.szPatientPkId = szPatientPkId;
	}

	public short getiPatientAllLen() {
		return iPatientAllLen;
	}

	public void setiPatientAllLen(short iPatientAllLen) {
		this.iPatientAllLen = iPatientAllLen;
	}

	public short getiPatientNeedLen() {
		return iPatientNeedLen;
	}

	public void setiPatientNeedLen(short iPatientNeedLen) {
		this.iPatientNeedLen = iPatientNeedLen;
	}

	public char[] getSzPatientName() {
		return szPatientName;
	}

	public void setSzPatientName(char[] szPatientName) {
		this.szPatientName = szPatientName;
	}

	public short getiDoctorAllLen() {
		return iDoctorAllLen;
	}

	public void setiDoctorAllLen(short iDoctorAllLen) {
		this.iDoctorAllLen = iDoctorAllLen;
	}

	public short getiDoctorNeedLen() {
		return iDoctorNeedLen;
	}

	public void setiDoctorNeedLen(short iDoctorNeedLen) {
		this.iDoctorNeedLen = iDoctorNeedLen;
	}

	public char[] getSzDoctorName() {
		return szDoctorName;
	}

	public void setSzDoctorName(char[] szDoctorName) {
		this.szDoctorName = szDoctorName;
	}

	public short getiMobileIdAllLen() {
		return iMobileIdAllLen;
	}

	public void setiMobileIdAllLen(short iMobileIdAllLen) {
		this.iMobileIdAllLen = iMobileIdAllLen;
	}

	public short getiMobileIdNeedLen() {
		return iMobileIdNeedLen;
	}

	public void setiMobileIdNeedLen(short iMobileIdNeedLen) {
		this.iMobileIdNeedLen = iMobileIdNeedLen;
	}

	public char[] getSzMobileId() {
		return szMobileId;
	}

	public void setSzMobileId(char[] szMobileId) {
		this.szMobileId = szMobileId;
	}

	public short getiMobileBtAllLen() {
		return iMobileBtAllLen;
	}

	public void setiMobileBtAllLen(short iMobileBtAllLen) {
		this.iMobileBtAllLen = iMobileBtAllLen;
	}

	public short getiMobileBtNeedLen() {
		return iMobileBtNeedLen;
	}

	public void setiMobileBtNeedLen(short iMobileBtNeedLen) {
		this.iMobileBtNeedLen = iMobileBtNeedLen;
	}

	public char[] getSzMobileBtAddr() {
		return szMobileBtAddr;
	}

	public void setSzMobileBtAddr(char[] szMobileBtAddr) {
		this.szMobileBtAddr = szMobileBtAddr;
	}

	public short getiForceAllLen() {
		return iForceAllLen;
	}

	public void setiForceAllLen(short iForceAllLen) {
		this.iForceAllLen = iForceAllLen;
	}

	public short getiForceNeedLen() {
		return iForceNeedLen;
	}

	public void setiForceNeedLen(short iForceNeedLen) {
		this.iForceNeedLen = iForceNeedLen;
	}

	public int getiForce() {
		return iForce;
	}

	public void setiForce(int iForce) {
		this.iForce = iForce;
	}

	public short getiCheckAllLen() {
		return iCheckAllLen;
	}

	public void setiCheckAllLen(short iCheckAllLen) {
		this.iCheckAllLen = iCheckAllLen;
	}

	public short getiCheckNeedLen() {
		return iCheckNeedLen;
	}

	public void setiCheckNeedLen(short iCheckNeedLen) {
		this.iCheckNeedLen = iCheckNeedLen;
	}

	public int getiCheck() {
		return iCheck;
	}

	public void setiCheck(int iCheck) {
		this.iCheck = iCheck;
	}

	public short getiCollectorCountAllLen() {
		return iCollectorCountAllLen;
	}

	public void setiCollectorCountAllLen(short iCollectorCountAllLen) {
		this.iCollectorCountAllLen = iCollectorCountAllLen;
	}

	public short getiCollectorCountNeedLen() {
		return iCollectorCountNeedLen;
	}

	public void setiCollectorCountNeedLen(short iCollectorCountNeedLen) {
		this.iCollectorCountNeedLen = iCollectorCountNeedLen;
	}

	public int getiCollectorCount() {
		return iCollectorCount;
	}

	public void setiCollectorCount(int iCollectorCount) {
		this.iCollectorCount = iCollectorCount;
	}

	public short getiCollectorIdAllLen() {
		return iCollectorIdAllLen;
	}

	public void setiCollectorIdAllLen(short iCollectorIdAllLen) {
		this.iCollectorIdAllLen = iCollectorIdAllLen;
	}

	public short getiCollectorIdNeedLen() {
		return iCollectorIdNeedLen;
	}

	public void setiCollectorIdNeedLen(short iCollectorIdNeedLen) {
		this.iCollectorIdNeedLen = iCollectorIdNeedLen;
	}

	public char[] getSzCollectorId() {
		return szCollectorId;
	}

	public void setSzCollectorId(char[] szCollectorId) {
		this.szCollectorId = szCollectorId;
	}

	public short getiCollectorTypeAllLen() {
		return iCollectorTypeAllLen;
	}

	public void setiCollectorTypeAllLen(short iCollectorTypeAllLen) {
		this.iCollectorTypeAllLen = iCollectorTypeAllLen;
	}

	public short getiCollectorTypeNeedLen() {
		return iCollectorTypeNeedLen;
	}

	public void setiCollectorTypeNeedLen(short iCollectorTypeNeedLen) {
		this.iCollectorTypeNeedLen = iCollectorTypeNeedLen;
	}

	public char[] getSzCollectorType() {
		return szCollectorType;
	}

	public void setSzCollectorType(char[] szCollectorType) {
		this.szCollectorType = szCollectorType;
	}

	public short getiCollectorBtAllLen() {
		return iCollectorBtAllLen;
	}

	public void setiCollectorBtAllLen(short iCollectorBtAllLen) {
		this.iCollectorBtAllLen = iCollectorBtAllLen;
	}

	public short getiCollectorBtNeedLen() {
		return iCollectorBtNeedLen;
	}

	public void setiCollectorBtNeedLen(short iCollectorBtNeedLen) {
		this.iCollectorBtNeedLen = iCollectorBtNeedLen;
	}

	public char[] getSzCollectorBtAddr() {
		return szCollectorBtAddr;
	}

	public void setSzCollectorBtAddr(char[] szCollectorBtAddr) {
		this.szCollectorBtAddr = szCollectorBtAddr;
	}

	public short getiNetConnAllLen() {
		return iNetConnAllLen;
	}

	public void setiNetConnAllLen(short iNetConnAllLen) {
		this.iNetConnAllLen = iNetConnAllLen;
	}

	public short getiNetConnNeedLen() {
		return iNetConnNeedLen;
	}

	public void setiNetConnNeedLen(short iNetConnNeedLen) {
		this.iNetConnNeedLen = iNetConnNeedLen;
	}

	public char[] getSzNetConnName() {
		return szNetConnName;
	}

	public void setSzNetConnName(char[] szNetConnName) {
		this.szNetConnName = szNetConnName;
	}

	public short getiNetApnAllLen() {
		return iNetApnAllLen;
	}

	public void setiNetApnAllLen(short iNetApnAllLen) {
		this.iNetApnAllLen = iNetApnAllLen;
	}

	public short getiNetApnNeedLen() {
		return iNetApnNeedLen;
	}

	public void setiNetApnNeedLen(short iNetApnNeedLen) {
		this.iNetApnNeedLen = iNetApnNeedLen;
	}

	public char[] getSzNetApnName() {
		return szNetApnName;
	}

	public void setSzNetApnName(char[] szNetApnName) {
		this.szNetApnName = szNetApnName;
	}

	public short getiNetUserAllLen() {
		return iNetUserAllLen;
	}

	public void setiNetUserAllLen(short iNetUserAllLen) {
		this.iNetUserAllLen = iNetUserAllLen;
	}

	public short getiNetUserNeedLen() {
		return iNetUserNeedLen;
	}

	public void setiNetUserNeedLen(short iNetUserNeedLen) {
		this.iNetUserNeedLen = iNetUserNeedLen;
	}

	public char[] getSzNetUserName() {
		return szNetUserName;
	}

	public void setSzNetUserName(char[] szNetUserName) {
		this.szNetUserName = szNetUserName;
	}

	public short getiNetPSAllLen() {
		return iNetPSAllLen;
	}

	public void setiNetPSAllLen(short iNetPSAllLen) {
		this.iNetPSAllLen = iNetPSAllLen;
	}

	public short getiNetPSNeedLen() {
		return iNetPSNeedLen;
	}

	public void setiNetPSNeedLen(short iNetPSNeedLen) {
		this.iNetPSNeedLen = iNetPSNeedLen;
	}

	public char[] getSzNetPassword() {
		return szNetPassword;
	}

	public void setSzNetPassword(char[] szNetPassword) {
		this.szNetPassword = szNetPassword;
	}

	public short getiSetParamAllLen() {
		return iSetParamAllLen;
	}

	public void setiSetParamAllLen(short iSetParamAllLen) {
		this.iSetParamAllLen = iSetParamAllLen;
	}

	public short getiSetParamNeedLen() {
		return iSetParamNeedLen;
	}

	public void setiSetParamNeedLen(short iSetParamNeedLen) {
		this.iSetParamNeedLen = iSetParamNeedLen;
	}

	public char[] getSzSetParam() {
		return szSetParam;
	}

	public void setSzSetParam(char[] szSetParam) {
		this.szSetParam = szSetParam;
	}

	public short getiWebWAllLen() {
		return iWebWAllLen;
	}

	public void setiWebWAllLen(short iWebWAllLen) {
		this.iWebWAllLen = iWebWAllLen;
	}

	public short getiWebWNeedLen() {
		return iWebWNeedLen;
	}

	public void setiWebWNeedLen(short iWebWNeedLen) {
		this.iWebWNeedLen = iWebWNeedLen;
	}

	public char[] getSzWebServiceAddrOfWan() {
		return szWebServiceAddrOfWan;
	}

	public void setSzWebServiceAddrOfWan(char[] szWebServiceAddrOfWan) {
		this.szWebServiceAddrOfWan = szWebServiceAddrOfWan;
	}

	public short getiWebLAllLen() {
		return iWebLAllLen;
	}

	public void setiWebLAllLen(short iWebLAllLen) {
		this.iWebLAllLen = iWebLAllLen;
	}

	public short getiWebLNeedLen() {
		return iWebLNeedLen;
	}

	public void setiWebLNeedLen(short iWebLNeedLen) {
		this.iWebLNeedLen = iWebLNeedLen;
	}

	public char[] getSzWebServiceAddrOfLan() {
		return szWebServiceAddrOfLan;
	}

	public void setSzWebServiceAddrOfLan(char[] szWebServiceAddrOfLan) {
		this.szWebServiceAddrOfLan = szWebServiceAddrOfLan;
	}

	public short getiVCodeAllLen() {
		return iVCodeAllLen;
	}

	public void setiVCodeAllLen(short iVCodeAllLen) {
		this.iVCodeAllLen = iVCodeAllLen;
	}

	public short getiVCodeNeedLen() {
		return iVCodeNeedLen;
	}

	public void setiVCodeNeedLen(short iVCodeNeedLen) {
		this.iVCodeNeedLen = iVCodeNeedLen;
	}

	public char[] getSzValidateCode() {
		return szValidateCode;
	}

	public void setSzValidateCode(char[] szValidateCode) {
		this.szValidateCode = szValidateCode;
	}

	public DeviceRation_bak(){
	}
	
	public DeviceRation_bak(byte[] source){
		try{
		Field[] fs = getClass().getDeclaredFields();
		Method[] methods = getClass().getDeclaredMethods();  
		int indexId = 0;
		for(Field f:fs){
            String fieldType = f.getType().getSimpleName();
            String fieldSetName = BeanRefUtil.parSetName(f.getName());   
            if (!BeanRefUtil.checkSetMet(methods, fieldSetName)) {   
                continue;   
            } 
            System.out.println(fieldType);
            if(fieldType.equals("short")){
            	//给变量赋值
            	Method mth = this.getClass().getMethod(fieldSetName, f.getType());
            	mth.invoke(this, Short.valueOf(TypeConversion.bytesToShort(source, indexId)));
            	indexId+=2;
            }else if(fieldType.equals("char[]")){
            	//获取数组的长度
            	String fieldGetName = BeanRefUtil.parGetName(f.getName());
            	Method getMth = this.getClass().getMethod(fieldGetName, new Class[] {});
            	Object o = getMth.invoke(this, new Object[] {});
            	int length = Array.getLength(o);
            	
            	byte[] tempBytes  = new byte[length];
            	System.arraycopy(source, indexId, tempBytes, 0, length);
            	Method mth = this.getClass().getMethod(fieldSetName, f.getType());
            	mth.invoke(this, TypeConversion.getChars(tempBytes));
            	indexId +=length;
            }
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		new DeviceRation(null);
		
//		try{
//			//Field[] fs = Class.forName("DeviceRation").getFields();
//			//Field[] fields = f.getClass().getDeclaredFields();
//
//			Class newoneClass = Class.forName("DeviceRation");
//			Field[] fs = newoneClass.getDeclaredFields();
//			
//			
//			for(Field f:fs){
//				System.out.println(f.getName());
//			}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
		
	}
	
}
