package com.a3650.posserver.core.service.impl;

import java.util.List;

import com.a3650.posserver.core.bean.CheckBatchInfo;
import com.a3650.posserver.core.bean.PayInfo;
import com.a3650.posserver.core.dao.impl.CheckBatchInfoDao;
import com.a3650.posserver.core.service.BaseBussProxy;

public class CheckBatchInfoBuss extends BaseBussProxy< CheckBatchInfo, Long, CheckBatchInfoDao>{
	/**
	 * 统计该批次的支付情况
	 * 0，正向支付笔数
	 * 2，逆向支付笔数
	 * 1，正向支付金额
	 * 3，逆向支付金额
	 * @param batchNumber
	 * @return
	 */
	public long[] getBatchInfo(String batchNumber){
		long[] batchInfo = new long[4];//统计未冲正的数据
		String hql = "select p.batchNumber,count(*),sum(p.payAmount) from PayInfo p where (p.payState=? or p.payState=?) and p.batchNumber=? group by p.batchNumber";
		List<Object[]> query = dao.queryHql( hql,new String[]{""+PayInfo.PayInfoState.noInsure.value(),""+PayInfo.PayInfoState.insure.value(),batchNumber});
		Object[] result = null;
		if(query!=null&&query.size()>0){
			result = query.get(0);
			batchInfo[0] = (Integer)result[1];
			batchInfo[1] = (Long)result[2];
		}

//		hql = "select p.batchNumber,count(*),sum(p.payAmount) from PayInfo p where p.payState=? and p.batchNumber=? group by p.batchNumber";
//		query = dao.queryHql( hql,new String[]{""+PayInfo.PayInfoState.back.value(),batchNumber});
//		if(query!=null&&query.size()>0){
//			result = query.get(0);
//			batchInfo[2] = (Integer)result[1];
//			batchInfo[3] = (Long)result[2];
//		}

		return batchInfo;
		
	}
}
