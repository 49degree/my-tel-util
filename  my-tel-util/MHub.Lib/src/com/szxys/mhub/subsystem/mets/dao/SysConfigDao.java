package com.szxys.mhub.subsystem.mets.dao;

import android.content.ContentValues;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Sysconfig;
import com.szxys.mhub.subsystem.mets.db.SysConfig;

public class SysConfigDao {

	public static Sysconfig getSysconfigObj() {
		return SysConfig.getSysConfigObj(MhubApplication.getInstance());
	}
	public static String getPatientID() {
		String patientID="";
		Sysconfig objConfig=SysConfig.getSysConfigObj(MhubApplication.getInstance());
		if (objConfig!=null) {
			patientID=objConfig.getC_PatientNo();
		}
		return patientID;
	}
	public long saveSysconfigInfo(Sysconfig objSysconfig) {
		long result=-1;
		if (objSysconfig!=null) {
			Sysconfig objSysconfigExisit=SysConfig.getSysConfigObj(MhubApplication.getInstance());
			if (objSysconfigExisit!=null) {
				ContentValues values=new ContentValues();
				//values.put(key, value);
				values.put("c_Hospital", objSysconfig.getC_Hospital());
				values.put("c_DoctorsNo", objSysconfig.getC_DoctorsNo());
				values.put("c_DoctorsName", objSysconfig.getC_DoctorsName());
				values.put("c_PatientNo", objSysconfig.getC_PatientNo());
				values.put("c_PatientName", objSysconfig.getC_PatientName());
				values.put("c_DevType", objSysconfig.getC_DevType());
				values.put("c_MobileId", objSysconfig.getC_MobileId());
				values.put("c_MobileName", objSysconfig.getC_MobileName());
				values.put("c_CollectorId", objSysconfig.getC_CollectorId());
				values.put("c_CollectorName", objSysconfig.getC_CollectorName());
				values.put("c_RationDateTime", objSysconfig.getC_RationDateTime());
				values.put("c_RationGuDt", objSysconfig.getC_RationGuDt());
				values.put("c_RecyDateTime", objSysconfig.getC_RecyDateTime());
				values.put("c_CollectDtInterval", objSysconfig.getC_CollectDtInterval());
				values.put("c_MaxDuration", objSysconfig.getC_MaxDuration());
				values.put("c_NoDataTime", objSysconfig.getC_NoDataTime());
				values.put("c_SendDtInterval", objSysconfig.getC_SendDtInterval());
				values.put("c_MeasuringCupWeight", objSysconfig.getC_MeasuringCupWeight());
				values.put("c_Version", objSysconfig.getC_Version());
				values.put("c_Copyright", objSysconfig.getC_Copyright());
				values.put("c_WebServiceUrl", objSysconfig.getC_WebServiceUrl());
				values.put("c_IsRecycling", objSysconfig.getC_IsRecycling());
				values.put("c_IsRegister", objSysconfig.getC_IsRegister());
				values.put("c_AutoCloseBt", objSysconfig.getC_AutoCloseBt());
				values.put("c_HaveSpecificGravity", objSysconfig.getC_HaveSpecificGravity());
				values.put("c_RebootTimer", objSysconfig.getC_RebootTimer());
				values.put("c_TimeInitialLead", objSysconfig.getC_TimeInitialLead());
				values.put("c_GetUpAlarm", objSysconfig.getC_GetUpAlarm());
				values.put("c_GotoBedAlarm", objSysconfig.getC_GotoBedAlarm());
				values.put("c_GprsGuid", objSysconfig.getC_GprsGuid());
				values.put("c_LastNetCommTime", objSysconfig.getC_LastNetCommTime());
				result=SysConfig.Update(MhubApplication.getInstance(), values, null, null);
			}else {
				result=SysConfig.Insert(MhubApplication.getInstance(), objSysconfig);
			}
		}
		return result;
	}
	/**
	 * 更新系统配置信息Sysconfig
	 * 
	 * @param ContentValues values
	 * @return long >0更新成功
	 */
	public int updateSysconfigInfo(ContentValues values) {
		int result=-1;
		result=SysConfig.Update(MhubApplication.getInstance(), values, null, null);
		return result;
	}
}
