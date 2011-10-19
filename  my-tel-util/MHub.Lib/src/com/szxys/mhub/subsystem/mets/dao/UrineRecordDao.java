package com.szxys.mhub.subsystem.mets.dao;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Urinerecord;
import com.szxys.mhub.subsystem.mets.db.UrineRecord;

public class UrineRecordDao {

	/**
	 * 根据id取尿流率信息(最大尿流率,平均尿流率,排尿时间,尿流时间,2秒时尿流率,平均尿流率。。。。)
	 * @param String ufrid
	 * @return Urinerecord
	 */
	public static Urinerecord getUrinerecordByUfrId(String ufrid) {
		Urinerecord objUrinerecord=UrineRecord.getUrineRecordbyId(MhubApplication.getInstance(), ufrid);		
		return objUrinerecord;
	}
	
}
