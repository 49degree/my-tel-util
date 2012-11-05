package com.a3650.posserver.bill99.bussiness;

import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.CheckBatchInfo;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.dao.impl.CheckBatchInfoDao;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.service.impl.CheckBatchInfoBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.Utils;

public class BatchPayInfo extends Operator{
	static Logger logger =  Logger.getLogger(BatchPayInfo.class);
	public BatchPayInfo(Bussness bussness) {
		super(bussness);
		// TODO Auto-generated constructor stub
		
		super.needBitData.set(11);
		super.needBitData.set(12);
		super.needBitData.set(13);
		super.needBitData.set(24);
		super.needBitData.set(41);
		super.needBitData.set(42);
		super.needBitData.set(49);
		super.needBitData.set(61);
		super.needBitData.set(63);
	}

	@Override
	protected void operate() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = bussness.getReferenceNumber((String)sendMap.get(41));

		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(37, checkIn.getReferenceNumber());
		

		
		@SuppressWarnings("unchecked")
		TreeMap<Integer,DataSelfFieldLeaf> data61 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(61);
		if(data61==null||!data61.containsKey(1)){
			returnMap.put(39, "95");
			returnMap.put(44, "批次号错误");
		}else{
			String batchNumber = data61.get(1).getValue();
			
			CheckBatchInfoBuss checkBatchInfoBuss = new CheckBatchInfoBuss();
			long[] batchInfo = checkBatchInfoBuss.getBatchInfo(batchNumber);//统计未冲正的数据
			logger.info(Arrays.toString(batchInfo));
			CheckBatchInfo.CheckBatchResult checkResult = CheckBatchInfo.CheckBatchResult.normal;
			@SuppressWarnings("unchecked")
			TreeMap<Integer,DataSelfFieldLeaf> data63 = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(63);
			//未进行判断
			logger.info(data63.get(1).getValue()+":"+data63.get(2).getValue()+":"+data63.get(3).getValue()+":"+data63.get(4).getValue());
			
			CheckBatchInfoDao checkBatchInfoDao = new CheckBatchInfoBuss().getBaseDao();
			CheckBatchInfo checkBatchInfo = new CheckBatchInfo();
			
			if(batchInfo[0] !=Integer.parseInt(data63.get(1).getValue())|| batchInfo[1]!=Long.parseLong(data63.get(2).getValue())){
				checkBatchInfo.setCheckResult(CheckBatchInfo.CheckBatchResult.less.value());
				returnMap.put(44, "对账不平");
			}else{
				checkBatchInfo.setCheckResult(CheckBatchInfo.CheckBatchResult.normal.value());
				returnMap.put(44, "交易成功");
			}
			
			
			checkBatchInfo.setBatchNumber(batchNumber);
			checkBatchInfo.setPayNum(Integer.parseInt(data63.get(1).getValue()));
			checkBatchInfo.setPayAmount(Long.parseLong(data63.get(2).getValue()));
			checkBatchInfo.setBackNum(Integer.parseInt(data63.get(3).getValue()));
			checkBatchInfo.setBackAmount(Long.parseLong(data63.get(4).getValue()));
			checkBatchInfo.setCheckTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			checkBatchInfoDao.save(checkBatchInfo);
			
			//更新批次号
			long newBatchNumber = Long.parseLong(checkIn.getBatchNumber())+1;
			String headerStr = "000000";
			checkIn.setBatchNumber(headerStr.substring(String.valueOf(newBatchNumber).length())+newBatchNumber);
			data61.get(1).setValue(checkIn.getBatchNumber());
			
			returnMap.put(61, data61);
			returnMap.put(39, "00");
		}
		

		checkInDao.saveOrUpdate(checkIn);
	}

}
