package com.guanri.android.jpos.pad.bill99;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.common.SharedPreferencesUtils;
import com.guanri.android.jpos.constant.JposConstant;
import com.guanri.android.jpos.db.DBBean;
import com.guanri.android.jpos.db.DBOperator;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 解析报文
 * 
 * @author Administrator
 * 
 */
public class ServerDataUnPackage99Bill {
	final static Logger logger = new Logger(ServerDataUnPackage99Bill.class);

	public static DBOperator bdOperator = DBOperator.getInstance();

	/**
	 * 解析签到返回报文
	 * 
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageLogin(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		if (getMap.containsKey(39)) {
			String str = JposConstant.result((String) getMap.get(39));

			logger.debug("响应结果" + str + "\n");
			Date date = new Date();
			tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
			tTransaction.Date().SetAsString((String) getMap.get(13));
			tTransaction.Time().SetAsString((String) getMap.get(12));
			tTransaction.TransCode().SetAsInteger(1);
			tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
			if (((String) getMap.get(39)).equals("00")) {
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + "签到成功");
				// 保存交易批次号
				if (getMap.containsKey(61)) {
					TreeMap<Integer,JposSelfFieldLeaf> datalist = (TreeMap<Integer,JposSelfFieldLeaf>) getMap.get(61);
					if (datalist.size() > 0) {
						JposSelfFieldLeaf jposf = datalist.get(1);
						SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
								SharedPreferencesUtils.POSBATCHNO, jposf.getValue());
						logger.debug("返回的批次号："+jposf.getValue());

					}
				}
				// 保存终端号
				if(getMap.containsKey(41)){
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO,
							SharedPreferencesUtils.POSID,(String)getMap.get(41));
				}
				// 保存商户号
				if(getMap.containsKey(42)){
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO,
							SharedPreferencesUtils.POSMERCHANT,(String)getMap.get(42));
				}
				
				// 检查6域 是否有数据
				if (getMap.containsKey(46)) {
					ArrayList<JposSelfFieldLeaf> datalist = (ArrayList<JposSelfFieldLeaf>) getMap.get(46);
					for (int j = 0; j < datalist.size(); j++) {
						JposSelfFieldLeaf jposSelfFieldLeaf = (JposSelfFieldLeaf) datalist.get(j);
						if (jposSelfFieldLeaf.getTag().equals("0024")) {
							// 商户名称
							logger.debug("商户名称" + jposSelfFieldLeaf.getValue() + "\n");
							tTransaction.ProcessList.MerchantName().SetAsString(jposSelfFieldLeaf.getValue());
							// 保存商户名称
							SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO,
									SharedPreferencesUtils.POSMERCHANTNAME,jposSelfFieldLeaf.getValue());
						}
					}
				}
			} else
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + str);

		}
		return tTransaction;
	}

	/**
	 * 解析余额查询报文
	 * 
	 * @param rtTransaction
	 *            发送报文数据
	 * @param getMap
	 *            返回结果
	 * @param messageType
	 *            报文头
	 * @return
	 */
	public static TTransaction UnPackageQuery(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(100);

		TreeMap<String, AdditionalAmounts> amountData = (TreeMap<String, AdditionalAmounts>) getMap.get(54);
		if (amountData.containsKey("02")) {
			AdditionalAmounts am = amountData.get("02");
			if (((String) getMap.get(39)).equals("00")) {
				double dou = Long.valueOf(am.getAmount().trim()) / 100.0;
				DecimalFormat df1 = new DecimalFormat("###0.00");
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + "可用余额为:" + df1.format(dou));
			} else
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}

		return tTransaction;
	}

	/**
	 * 解析销售报文
	 * 
	 * @param rtTransaction
	 *            原报文信息
	 * @param getMap
	 *            收到数据
	 * @param messageType
	 * @return
	 */

	public static TTransaction UnPackageSale(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		String AuthorizeCode = "";
		if (((String) getMap.get(39)).equals("00")) {
			double dou = Long.valueOf((String) getMap.get(4)) / 100.0;
			DecimalFormat df1 = new DecimalFormat("###0.00");
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + "消费金额为:" + df1.format(dou));
		} else
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		// 授权码
		if (getMap.containsKey(38)) {
			AuthorizeCode = (String) getMap.get(38);
			// tTransaction.
		}
		
		//tanzijie
		if (getMap.containsKey(37)) {
			tTransaction.BufferList.ReferenceNumber().SetAsString((String) getMap.get(37));
			// tTransaction.
		}
		
		
		String strr = (String) getMap.get(44);
		logger.debug("返回结果" + strr + "\n");
		TreeMap<Integer, JposSelfFieldLeaf> datalist = (TreeMap<Integer, JposSelfFieldLeaf>) getMap.get(61);
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = datalist.get(4);
		logger.debug(leaf.getValue() + "\n");
		leaf = datalist.get(5);
		logger.debug(leaf.getValue() + "\n");
		return tTransaction;
	}

	/**
	 * 解析订单查询
	 * 
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPagckageQueryOQS(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		StringBuffer result = new StringBuffer();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11 POS 流水号
		if (getMap.containsKey(11))
			tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		if (getMap.get(39).equals("00")) {
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			String timeStr = "时间" + (String) getMap.get(12) + "\n";
			String dateStr = "日期" + (String) getMap.get(13) + "\n";
			result.append(dateStr);
			result.append(timeStr);
			// 获得域4 保单金额
			String money = (String) getMap.get(4);
			tTransaction.ProcessList.ReturnSaleAmount().SetAsInt64(Long.valueOf(money));
			// 响应信息
			if (getMap.containsKey(57)){
				tTransaction.ProcessList.Response().SetAsString("00" + getMap.get(57));
				tTransaction.ProcessList.ReturnDisplayMessage().SetAsString((String)getMap.get(57));
			}
			else
				tTransaction.ProcessList.Response().SetAsString("00");
			// 域60 订单编号
			if (getMap.containsKey(60))
				tTransaction.ProcessList.ReturnOrderNumber().SetAsString((String) getMap.get(60));

			logger.debug(result.toString());
		} else {
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}
		return tTransaction;
	}

	/**
	 * 查询后消费
	 * 
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageQueryOQSSale(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		StringBuffer result = new StringBuffer();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11 POS 流水号
		if (getMap.containsKey(11))
			tTransaction.SerialNumber().SetAsInteger((Integer) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		// 判断是否相应成功
		if (getMap.get(39).equals("00")) {
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			String timeStr = "时间" + (String) getMap.get(12) + "\n";
			String dateStr = "日期" + (String) getMap.get(13) + "\n";
			result.append(dateStr);
			result.append(timeStr);
			// 获得域4 保单金额
			tTransaction.ProcessList.SaleAmount().SetAsString((String) getMap.get(4));
			// 响应信息
			if (getMap.containsKey(57))
				tTransaction.ProcessList.Response().SetAsString("00" + getMap.get(57));
			else
				tTransaction.ProcessList.Response().SetAsString("00");
			// 域60 订单编号
			if (getMap.containsKey(60))
				tTransaction.ProcessList.ReturnOrderNumber().SetAsString((String) getMap.get(60));
			// 域37 参考号
			String ReferenceStr = "";
			//if (getMap.containsKey(37))
				ReferenceStr = (String) getMap.get(37);
				logger.debug("服务下发参考号:"+(String) getMap.get(37));
				tTransaction.BufferList.ReferenceNumber().SetAsString(ReferenceStr);
			// 域38 授权码
			String authorizeStr = "";
			if (getMap.containsKey(38))
				authorizeStr = (String) getMap.get(38);
			// 更新 数据, --------------------------------------------------
			// 更新 单据状态为1 更新服务器下发的 参考号,授权码
			Map<String,String> params = new HashMap<String,String>();
			params.put("TransactionState=", "1");
			params.put("SearchNo=", ReferenceStr);
			params.put("AuthorizationNo=", authorizeStr);
			upDataState(rtTransaction.SerialNumber().GetAsString(),
					TypeConversion.byte2hex(rtTransaction.MAC().GetData()),params);
			logger.debug(result.toString());
		} else {
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}
		return tTransaction;
	}

	/**
	 * 交易回执解析
	 * 
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageSaleReceipt(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		StringBuffer result = new StringBuffer();
		tTransaction.TransCode().SetAsInteger(7);
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11 POS 流水号
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));

		// 判断是否相应成功
		if (getMap.get(39).equals("00")) {
			// 更新 数据, 代码待完善
			// ------------------------------------------------------------
			// bdOperator.onUpgrade();
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			// 响应信息
			tTransaction.ProcessList.Response().SetAsString("00" + (String) getMap.get(49));
			
			// 更新数据库状态
			//-------------------------------------------------------
			Map<String,String> params = new HashMap<String,String>();
			params.put("TransactionState=", "2");
			upDataState(rtTransaction.SerialNumber().GetAsString(),
						rtTransaction.MAC().GetAsString(),params);
			
			logger.debug(result.toString());
		} else {
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}

		return tTransaction;
	}

	/**
	 * 冲正相应
	 * 
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageReversal(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		tTransaction.TransCode().SetAsInteger(4);
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11 POS 流水号
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		// 响应信息
		tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		
		return tTransaction;
	}

	/**
	 * POS结算
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageCheck(TTransaction rtTransaction, TreeMap<Integer, Object> getMap, JposMessageType messageType) {
		TTransaction tTransaction = new TTransaction();
		StringBuffer result = new StringBuffer();
		tTransaction.TransCode().SetAsInteger(6);
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11 POS 流水号
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));

		// 判断是否相应成功
		if (getMap.get(39).equals("00")) {
			// 更新 数据, 代码待完善
			// ------------------------------------------------------------
			// bdOperator.onUpgrade();
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			// 响应信息
			tTransaction.ProcessList.Response().SetAsString("00" + "批结算成功");
			if (getMap.containsKey(61)) {
				TreeMap<Integer,JposSelfFieldLeaf> datalist = (TreeMap<Integer,JposSelfFieldLeaf>) getMap.get(61);
				if (datalist.containsKey(1)) {
					JposSelfFieldLeaf jposf = datalist.get(1);
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
							SharedPreferencesUtils.POSBATCHNO, jposf.getValue());
					logger.debug("新批次号:" + jposf.getValue());
				}
			}
			logger.debug(result.toString());
		} else {
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}

		return tTransaction;
	}
	
	public static void upDataState(String SerialNumber,String MAC,Map<String,String> params){
		// 更新数据库标识
		Map<String,String> values = new HashMap<String,String>();
		// 流水号
		values.put("PosNo=", SerialNumber);
		// MAC值
		values.put("PosMac=", MAC);
		//values.put("PosMac=", rtTransaction.MAC().GetAsString());
		//values.put("CardNo=", rtTransaction.ProcessList.GetPAN());
		bdOperator.update(DBBean.TB_SALE_RECORD, values, params);
	}
}
