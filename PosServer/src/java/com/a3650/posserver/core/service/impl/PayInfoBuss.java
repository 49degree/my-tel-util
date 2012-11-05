package com.a3650.posserver.core.service.impl;

import java.util.List;

import com.a3650.posserver.core.bean.PayInfo;
import com.a3650.posserver.core.dao.impl.PayInfoDao;
import com.a3650.posserver.core.service.BaseBussProxy;

public class PayInfoBuss extends BaseBussProxy<PayInfo, Long,PayInfoDao>{
	
	/**
	 * 查询是否存在支付记录
	 * @param orderId
	 * @return
	 */
	public PayInfo queryPayInfo(String referenceNum){
		PayInfo payInfo = new PayInfo();
		payInfo.setReferenceNum(referenceNum);
		List<PayInfo> payInfos = dao.findEqualByEntity(payInfo, new String[]{"referenceNum"});
		if(payInfos!=null&&payInfos.size()>0)
			return payInfos.get(0);
		else
			return null;
	}
	
	/**
	 * 查询是否存在支付记录
	 * @param orderId
	 * @return
	 */	
	public PayInfo queryPayInfo(String batchNum,String posOrderId){
		PayInfo payInfo = new PayInfo();
		payInfo.setBatchNumber(batchNum);
		payInfo.setPosOrderId(posOrderId);
		List<PayInfo> payInfos = dao.findEqualByEntity(payInfo, new String[]{"posOrderId","batchNumber"});
		if(payInfos!=null&&payInfos.size()>0)
			return payInfos.get(0);
		else
			return null;
	}

	

}
