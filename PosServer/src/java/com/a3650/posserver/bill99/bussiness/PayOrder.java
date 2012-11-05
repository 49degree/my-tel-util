package com.a3650.posserver.bill99.bussiness;

import java.util.Date;
import java.util.TreeMap;

import com.a3650.posserver.bill99.security.SecurityControlBill99;
import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.bean.PayInfo;
import com.a3650.posserver.core.bean.PosTerminalCheckIn;
import com.a3650.posserver.core.bussiness.Bussness;
import com.a3650.posserver.core.bussiness.Operator;
import com.a3650.posserver.core.dao.impl.ApsaiOrderDao;
import com.a3650.posserver.core.dao.impl.PosTerminalCheckInDao;
import com.a3650.posserver.core.datapackage.DataSelfFieldLeaf;
import com.a3650.posserver.core.init.InitContext;
import com.a3650.posserver.core.security.SecurityControl.SecurityControlException;
import com.a3650.posserver.core.service.impl.ApsaiOrderBuss;
import com.a3650.posserver.core.service.impl.ApsaiOrderPayInfoBuss;
import com.a3650.posserver.core.service.impl.PosTerminalCheckInBuss;
import com.a3650.posserver.core.utils.Utils;

public class PayOrder extends Operator{

	public PayOrder(Bussness bussness) {
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
		super.needBitData.set(35);
		super.needBitData.set(36);
		super.needBitData.set(41);
		super.needBitData.set(42);
		super.needBitData.set(49);
		super.needBitData.set(57);
		super.needBitData.set(60);
		super.needBitData.set(61);
		super.needBitData.set(64);

	}

	@Override
	protected void operate() {
		PosTerminalCheckInDao checkInDao = new PosTerminalCheckInBuss().getBaseDao();
		PosTerminalCheckIn checkIn = bussness.getReferenceNumber((String)sendMap.get(41));

		
		returnMap.put(12, Utils.getTimeString(new Date(), "HHmmss"));
		returnMap.put(13, Utils.getTimeString(new Date(), "MMdd"));
		returnMap.put(37, checkIn.getReferenceNumber());// 检索参考号(Retrieval
														// Reference Number)

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
		
		} else {
			//判断PIN码
			if(sendMap.containsKey(52)){
				//验证PIN码
				SecurityControlBill99 securityControl = null;
				try {
					securityControl = new SecurityControlBill99((String)sendMap.get(41));//构造安全控制对象
					if(!securityControl.checkPin(bussness.getDataInPackage())){
						returnMap.put(39, "99");
						returnMap.put(44, "PIN码错误");
						return;
					}
				} catch (SecurityControlException e) {
					e.printStackTrace();
					returnMap.put(39, "95");
					returnMap.put(44, "PIN码错误");
					return;
				}
			}
			
			if(sendMap.containsKey(61)){
				TreeMap<Integer,DataSelfFieldLeaf> data = (TreeMap<Integer,DataSelfFieldLeaf>)sendMap.get(61);
				data.get(1).setValue(checkIn.getBatchNumber());
				
				DataSelfFieldLeaf leaf = new DataSelfFieldLeaf();
				leaf.setTag("4");
				leaf.setValue("06");
				data.put(4, leaf);
				leaf = new DataSelfFieldLeaf();
				leaf.setTag("5");
				leaf.setValue("**银行    ");
				data.put(5, leaf);
				returnMap.put(61, data);
			}
			
			
			PayInfo payInfo = new PayInfo();
			payInfo.setPosId((String)sendMap.get(41));
			payInfo.setPosOrderId((String)sendMap.get(11));
			payInfo.setReferenceNum(checkIn.getReferenceNumber());
			payInfo.setAddTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			payInfo.setPayState(PayInfo.PayInfoState.noInsure.value());
			payInfo.setBatchNumber(checkIn.getBatchNumber());
			payInfo.setPayAmount(apsaiOrder.getApsaiAmount());
			//ba
			if(apsaiOrderPayInfoBuss.savePayInfo(apsaiOrder,payInfo)){
				returnMap.put(39, "00");
			}else{
				returnMap.put(39, "95");
				returnMap.put(44, "支付失败");
			}
		}
		checkInDao.saveOrUpdate(checkIn);
	}

}
