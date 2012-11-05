package com.a3650.posserver.bill99.bussiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import com.a3650.posserver.core.bean.Company;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.service.impl.CompanyBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.Utils;

public class PosLogin extends Operator{

	public PosLogin(Bussness bussness) {
		super(bussness);
		// TODO Auto-generated constructor stub
		super.needBitData.set(3);
		super.needBitData.set(11);
//		super.needBitData.set(12);
//		super.needBitData.set(13);
		super.needBitData.set(24);
		super.needBitData.set(41);
		super.needBitData.set(42);
		super.needBitData.set(61);
	}

	@Override
	protected void operate() {
		// TODO Auto-generated method stub
		PosTerminalCheckInDao checkInDao  = new PosTerminalCheckInBuss().getBaseDao();
		
		//查询商户信息
		Company company =  new CompanyBuss().getBaseDao().get((String)sendMap.get(42));

		returnMap.put(39, "00");
		returnMap.put(44, "交易成功");
		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		
		List<DataSelfFieldLeaf> return46 = new ArrayList<DataSelfFieldLeaf>();
		
		DataSelfFieldLeaf jposSelfFieldLeaf = new DataSelfFieldLeaf();
		jposSelfFieldLeaf.setTag("0024");
		jposSelfFieldLeaf.setValue(company.getCompanyName());
		return46.add(jposSelfFieldLeaf);
		
		returnMap.put(46, return46);
		
		returnMap.put(48, " ");
		
		PosTerminalCheckIn checkIn = bussness.getReferenceNumber((String)sendMap.get(41));
		
		checkIn.setCheckInTime(Utils.getTimeString(new Date(), Utils.timeFormat));
		checkIn.setRootKeyId(0);
		
		TreeMap<Integer,DataSelfFieldLeaf> data61 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(61);
		data61.get(1).setValue(checkIn.getBatchNumber());
		returnMap.put(61, data61);
		
		//returnMap.put(37,checkIn.getReferenceNumber());//检索参考号(Retrieval Reference Number)	
		checkIn.setState('0');

		checkInDao.saveOrUpdate(checkIn);
	}

}
