package com.szxys.mhub.subsystem.mets.dao;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.UFRrecord;
import com.szxys.mhub.subsystem.mets.db.UFR_Record;

public class UFRecordDao {

	/**
	 * 根据id获得尿流率曲线数据(对象字段加工：c_QuantityData与c_RateData是由","分隔的String)
	 * @param String ufrId
	 * @return UFRrecord 实体类
	 */
	public static UFRrecord getUfRrecordById(String ufrId) {
		UFRrecord objUFR=null;
		objUFR=UFR_Record.getUFRbyId(MhubApplication.getInstance(), ufrId);
		return objUFR;
	}
}
