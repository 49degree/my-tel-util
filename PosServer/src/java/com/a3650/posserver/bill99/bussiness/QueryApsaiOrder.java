package com.a3650.posserver.bill99.bussiness;

import java.util.Date;

import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.dao.impl.ApsaiOrderDao;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.init.InitContext;
import com.a3650.posserver.core.service.impl.ApsaiOrderBuss;
import com.a3650.posserver.core.service.impl.ApsaiOrderPayInfoBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.Utils;

public class QueryApsaiOrder extends Operator{

	public QueryApsaiOrder(Bussness bussness) {
		super(bussness);
		// TODO Auto-generated constructor stub
		super.needBitData.set(3);
		super.needBitData.set(11);
		super.needBitData.set(22);
		super.needBitData.set(24);
		super.needBitData.set(25);
		super.needBitData.set(41);
		super.needBitData.set(42);
		super.needBitData.set(49);
		super.needBitData.set(60);
		super.needBitData.set(64);
	}

	@Override
	protected void operate() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = bussness.getReferenceNumber((String)sendMap.get(41));
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(37, checkIn.getReferenceNumber());// 检索参考号(Retrieval  Reference Number)
		

		
		// 获取保单ID
		String data60 = (String)sendMap.get(60);

		ApsaiOrderDao apsaiOrderDao = new ApsaiOrderBuss().getBaseDao();
		ApsaiOrder apsaiOrder = apsaiOrderDao.get(data60.trim());
		//判断保单是否已经支付
		ApsaiOrderPayInfoBuss apsaiOrderPayInfoBuss = new ApsaiOrderPayInfoBuss();
		if(apsaiOrder==null){
			returnMap.put(39, "95");
			returnMap.put(44, "无("+data60+")保单信息");
			//InitContext.isMultPay()判断是否可以重复支付，测试情况下可以
		}else if(!InitContext.isMultPay()&&apsaiOrderPayInfoBuss.checkIsPay(data60)){////判断保单是否已经支付
			returnMap.put(39, "95");
			returnMap.put(44, "保单("+data60+")已经支付");
		}else{
			returnMap.put(4, String.valueOf(apsaiOrder.getApsaiAmount()));
			
			StringBuffer orderInfo = new StringBuffer();
			orderInfo.append(apsaiOrder.getApsaiId()).append("|");
			orderInfo.append(apsaiOrder.getCustomName()).append("|");
			orderInfo.append(apsaiOrder.getCustomAddr()).append("|");
			orderInfo.append(apsaiOrder.getCustomLink()).append("|");
			orderInfo.append(apsaiOrder.getApsaiAmount()/100f);
			
			returnMap.put(57, orderInfo.toString());
			
			returnMap.put(39, "00");
		}
		checkInDao.saveOrUpdate(checkIn);
	}

}
