package com.a3650.posserver.bill99.bussiness;

import java.util.Date;
import java.util.TreeMap;

import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.service.impl.ApsaiOrderPayInfoBuss;
import com.a3650.posserver.core.service.impl.PayInfoBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.Utils;

public class BackPayInfo extends Operator{

	public BackPayInfo(Bussness bussness) {
		super(bussness);
		// TODO Auto-generated constructor stub
		super.needBitData.set(2);
		super.needBitData.set(3);
		super.needBitData.set(4);
		super.needBitData.set(11);
		super.needBitData.set(12);
		super.needBitData.set(13);
		super.needBitData.set(22);
		super.needBitData.set(24);
		super.needBitData.set(25);
		super.needBitData.set(41);
		super.needBitData.set(42);
		super.needBitData.set(49);
		super.needBitData.set(61);
		super.needBitData.set(62);
		super.needBitData.set(64);
	}

	@Override
	protected void operate() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = bussness.getReferenceNumber((String)sendMap.get(41));

		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(37, checkIn.getReferenceNumber());
		TreeMap<Integer,DataSelfFieldLeaf> data62 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(62);
		String posOrderId = data62.get(2).getValue();//系统跟踪号
		
		TreeMap<Integer,DataSelfFieldLeaf> data61 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(61);
		String batchNumber = data61.get(1).getValue();//批次号
		
		PayInfoBuss payInfoBuss = new PayInfoBuss();
		
		if(payInfoBuss.queryPayInfo(batchNumber,posOrderId)==null){
			returnMap.put(39, "95");
			returnMap.put(44, "无支付记录");
		}else{
			ApsaiOrderPayInfoBuss apsaiOrderPayInfoBuss = new ApsaiOrderPayInfoBuss();
			if(!apsaiOrderPayInfoBuss.backPayInfo(batchNumber,posOrderId)){
				returnMap.put(39, "95");
				returnMap.put(44, "交易失败");
			}else{
				returnMap.put(39, "00");
				returnMap.put(44, "交易成功");
			}
		}
		checkInDao.saveOrUpdate(checkIn);
	}

}