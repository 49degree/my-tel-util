package com.a3650.posserver.core.service.impl;

import java.util.Date;
import java.util.List;

import com.a3650.posserver.core.bean.ApsaiOrder;
import com.a3650.posserver.core.bean.ApsaiOrderPayInfo;
import com.a3650.posserver.core.bean.PayInfo;
import com.a3650.posserver.core.dao.BaseDao;
import com.a3650.posserver.core.dao.impl.ApsaiOrderPayInfoDao;
import com.a3650.posserver.core.init.InitContext;
import com.a3650.posserver.core.service.BaseBussProxy;
import com.a3650.posserver.core.utils.Utils;

public class ApsaiOrderPayInfoBuss extends BaseBussProxy<ApsaiOrderPayInfo, Long,ApsaiOrderPayInfoDao>{
	

	
	/**
	 * 查询是否已经支付
	 * @param orderId
	 * @return
	 */
	public boolean checkIsPay(String orderId){
		boolean result = false;
		ApsaiOrderPayInfo apsaiOrderPayInfo = new ApsaiOrderPayInfo();
		apsaiOrderPayInfo.setApsaiId(orderId);
		apsaiOrderPayInfo.setPayResult(ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.suss.value());
		List<ApsaiOrderPayInfo> apsaiOrderPayInfos = dao.findEqualByEntity(apsaiOrderPayInfo, new String[]{"apsaiId","payResult"});
		
		if(apsaiOrderPayInfos==null)
			apsaiOrderPayInfo.setPayResult(ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.faild.value());
		apsaiOrderPayInfos = dao.findEqualByEntity(apsaiOrderPayInfo, new String[]{"apsaiId","payResult"});
		if(apsaiOrderPayInfos==null||apsaiOrderPayInfos.size()<1)
			result = false;
		else
			result = true;
		
		return result;
	}
	
	/**
	 * 查询是否已经支付
	 * @param orderId
	 * @return
	 */
	public boolean savePayInfo(ApsaiOrder orderBean,PayInfo payInfo){
		//核实是否已经保存
		ApsaiOrderPayInfo apsaiOrderPayInfo = new ApsaiOrderPayInfo();
		apsaiOrderPayInfo.setApsaiId(orderBean.getApsaiId());
		apsaiOrderPayInfo.setPayResult(ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.suss.value());
		
		List<ApsaiOrderPayInfo> apsaiOrderPayInfos = dao.findEqualByEntity(apsaiOrderPayInfo, new String[]{"apsaiId","payResult"});
		
		//InitContext.isMultPay()判断是否可以重复支付，测试情况下可以
		if(InitContext.isMultPay()||apsaiOrderPayInfos==null||apsaiOrderPayInfos.size()<1){
			
			BaseDao handlerDao = this.getHandlerDao(); 
			
			//PayInfoDao payInfoDao = new PayInfoBuss().getBaseDao();
			handlerDao.save(payInfo);

			apsaiOrderPayInfo = new ApsaiOrderPayInfo();
			apsaiOrderPayInfo.setPayId(payInfo.getPayId());
			apsaiOrderPayInfo.setPayAmount(orderBean.getApsaiAmount());
			apsaiOrderPayInfo.setApsaiId(orderBean.getApsaiId());
			
			apsaiOrderPayInfo.setPayResult(payInfo.getPayState()==PayInfo.PayInfoState.insure.value()?
					ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.suss.value():
						ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.faild.value());
			
			apsaiOrderPayInfo.setPayTime(Utils.getTimeString(new Date(), Utils.timeFormat));
			handlerDao.save(apsaiOrderPayInfo);

			try{
				handlerDao.getSession().getTransaction().commit();
			}catch(Exception e){
				e.printStackTrace();
				handlerDao.getSession().getTransaction().rollback();
				return false;
			}
			
			
		}else{
			return false;
		}
		return true;
	}
	

	
	/**
	 * 交易回执
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean inSurePayInfo(String referenceNum){
		PayInfoBuss payInfoBuss = new PayInfoBuss();
		
		PayInfo payInfo = payInfoBuss.queryPayInfo(referenceNum);
		if(payInfo==null)
			return false;
		BaseDao handlerDao = this.getHandlerDao(); 
		
		payInfo.setPayState(PayInfo.PayInfoState.insure.value());
		payInfo.setInsureTime(Utils.getTimeString(new Date(), Utils.timeFormat));
		handlerDao.saveOrUpdate(payInfo);

		//更新保单支付记录
		ApsaiOrderPayInfo apsaiOrderPayInfo = new ApsaiOrderPayInfo();
		apsaiOrderPayInfo.setPayId(payInfo.getPayId());
		List<ApsaiOrderPayInfo> apsaiOrderPayInfos = dao.findEqualByEntity(apsaiOrderPayInfo, new String[]{"payId"});
		
		if(apsaiOrderPayInfos!=null&&apsaiOrderPayInfos.size()>0){
			apsaiOrderPayInfo = apsaiOrderPayInfos.get(0);
			apsaiOrderPayInfo.setPayResult(ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.suss.value());
			handlerDao.saveOrUpdate(apsaiOrderPayInfo);

			
			
			try{
				handlerDao.getSession().getTransaction().commit();
			}catch(Exception e){
				handlerDao.getSession().getTransaction().rollback();
				return false;
			}
			return true;
		}else{
			try{
				handlerDao.getSession().getTransaction().rollback();
			}catch(Exception e){}
			return false;
		}
	}
	
	

	/**
	 * 冲正
	 * @param orderId
	 * @return
	 */
	public boolean backPayInfo(String batchNum,String posOrderId){
		PayInfoBuss payInfoBuss = new PayInfoBuss();
		PayInfo payInfo = payInfoBuss.queryPayInfo(batchNum,posOrderId);
		
		if(payInfo==null)
			return false;
		
		BaseDao handlerDao = this.getHandlerDao(); 
		
		payInfo.setPayState(PayInfo.PayInfoState.back.value());
		payInfo.setInsureTime(Utils.getTimeString(new Date(), Utils.timeFormat));
		handlerDao.saveOrUpdate(payInfo);
		
		//更新保单支付记录
		ApsaiOrderPayInfo apsaiOrderPayInfo = new ApsaiOrderPayInfo();
		apsaiOrderPayInfo.setPayId(payInfo.getPayId());
		List<ApsaiOrderPayInfo> apsaiOrderPayInfos = dao.findEqualByEntity(apsaiOrderPayInfo, new String[]{"payId"});
		
		if(apsaiOrderPayInfos!=null&&apsaiOrderPayInfos.size()>0){
			apsaiOrderPayInfo = apsaiOrderPayInfos.get(0);
			apsaiOrderPayInfo.setPayResult(ApsaiOrderPayInfo.ApsaiOrderPayInfoResult.back.value());
			handlerDao.saveOrUpdate(apsaiOrderPayInfo);
			try{
				handlerDao.getSession().getTransaction().commit();
			}catch(Exception e){
				handlerDao.getSession().getTransaction().rollback();
				return false;
			}
			return true;
		}else{
			try{
				handlerDao.getSession().getTransaction().rollback();
			}catch(Exception e){}
			return false;
		}
	}
	
}
