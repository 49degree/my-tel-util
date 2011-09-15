package com.guanri.android.jpos.pad.bill99;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.constant.JposConstant;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
import com.guanri.android.lib.log.Logger;

/**
 * 解析报文
 * @author Administrator
 *
 */
public class ServerDataUnPackage99Bill {
	final static Logger logger = new Logger(ServerDataUnPackage99Bill.class);

	static TTransaction tTransaction = new TTransaction();
	/**
	 * 解析签到返回报文
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageLogin(TTransaction rtTransaction,
			TreeMap<Integer, Object> getMap, JposMessageType messageType){

		if (getMap.containsKey(39)) {
			String str = JposConstant.result((String) getMap.get(39));

			logger.debug("响应结果" + str + "\n");
			Date date = new Date();
			tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
			tTransaction.Date().SetAsString((String) getMap.get(13));
			tTransaction.Time().SetAsString((String) getMap.get(12));
			tTransaction.TransCode().SetAsInteger(1);
			tTransaction.SerialNumber()
					.SetAsString((String) getMap.get(11));
			if (((String) getMap.get(39)).equals("00"))
				tTransaction.ProcessList.Response().SetAsString(
						(String) getMap.get(39) + "签到成功");
			else
				tTransaction.ProcessList.Response().SetAsString(
						(String) getMap.get(39) + str);
			// 检查6域 是否有数据
			if (getMap.containsKey(46)) {
				ArrayList<JposSelfFieldLeaf> datalist = (ArrayList<JposSelfFieldLeaf>) getMap.get(46);
				for (int j = 0; j < datalist.size(); j++) {
					JposSelfFieldLeaf jposSelfFieldLeaf = (JposSelfFieldLeaf) datalist
							.get(j);
					if (jposSelfFieldLeaf.getTag().equals("0024")) {
						// 商户名称
						logger.debug("商户名称" + jposSelfFieldLeaf.getValue()
								+ "\n");
						tTransaction.ProcessList.MerchantName()
								.SetAsString(jposSelfFieldLeaf.getValue());
					}
				}
			}
		}
		return tTransaction;
	}
	
	/**
	 * 解析余额查询报文
	 * @param rtTransaction  发送报文数据
	 * @param getMap 返回结果
	 * @param messageType  报文头
	 * @return
	 */
	public static TTransaction UnPackageQuery(TTransaction rtTransaction,
			TreeMap<Integer, Object> getMap, JposMessageType messageType){

		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(100);
		
		TreeMap<String,AdditionalAmounts> amountData = (TreeMap<String,AdditionalAmounts>)getMap.get(54);
		if(amountData.containsKey("02")){
			AdditionalAmounts am = amountData.get("02");
			if(((String) getMap.get(39)).equals("00")) {
				double dou = Long.valueOf(am.getAmount().trim()) / 100.0;
				DecimalFormat df1 = new DecimalFormat("###0.00"); 
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + "可用余额为:" + df1.format(dou));
			}
			else
				tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		}
		
		return tTransaction;
	}
	
	/**
	 * 解析销售报文
	 * @param rtTransaction  原报文信息
	 * @param getMap 收到数据
	 * @param messageType
	 * @return
	 */
	
	public static TTransaction UnPackageSale(TTransaction rtTransaction,
			TreeMap<Integer, Object> getMap, JposMessageType messageType){
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		tTransaction.SerialNumber().SetAsString((String) getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		String AuthorizeCode = "";
		if(((String) getMap.get(39)).equals("00")){
			double dou = Long.valueOf((String)getMap.get(4)) / 100.0;
			DecimalFormat df1 = new DecimalFormat("###0.00"); 
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) +"消费金额为:" +df1.format(dou));
		}
		else 
			tTransaction.ProcessList.Response().SetAsString((String) getMap.get(39) + JposConstant.result((String) getMap.get(39)));
		// 授权码
		if(getMap.containsKey(38)){
			AuthorizeCode = (String) getMap.get(38);
			//tTransaction.
		}
		String strr = (String)getMap.get(44);
		logger.debug("返回结果"+strr + "\n");
		TreeMap<Integer,JposSelfFieldLeaf> datalist = (TreeMap<Integer,JposSelfFieldLeaf>) getMap.get(61);
		JposSelfFieldLeaf leaf = new JposSelfFieldLeaf();
		leaf = datalist.get(4);
		logger.debug(leaf.getValue() +"\n");
		leaf = datalist.get(5);
		logger.debug(leaf.getValue() +"\n");
		return tTransaction;
	}
	
	/**
	 * 解析订单查询
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPagckageQueryOQS(TTransaction rtTransaction,
			TreeMap<Integer, Object> getMap, JposMessageType messageType){
		StringBuffer result = new StringBuffer();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11  POS 流水号
		if(getMap.containsKey(11))
			tTransaction.SerialNumber().SetAsInteger((Integer)getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		if (getMap.get(39).equals("00")) {
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			String timeStr = "时间" + (String) getMap.get(12) + "\n";
			String dateStr = "日期" + (String) getMap.get(13) + "\n";
			result.append(dateStr);
			result.append(timeStr);
			// 获得域4  保单金额
			tTransaction.ProcessList.SaleAmount().SetAsString((String)getMap.get(4));
			// 响应信息
			if(getMap.containsKey(57))
				tTransaction.ProcessList.Response().SetAsString("00"+getMap.get(57));
			else 
				tTransaction.ProcessList.Response().SetAsString("00");
			//域60 订单编号
			if(getMap.containsKey(60))
				tTransaction.ProcessList.ReturnOrderNumber().SetAsString((String)getMap.get(60));
			
			logger.debug(result.toString());
		}else{
			tTransaction.ProcessList.Response().SetAsString((String)getMap.get(39)+JposConstant.result((String)getMap.get(39)));
		}
		return tTransaction;
	}
	
	/**
	 * 查询后消费
	 * @param rtTransaction
	 * @param getMap
	 * @param messageType
	 * @return
	 */
	public static TTransaction UnPackageQueryOQSSale(TTransaction rtTransaction,
			TreeMap<Integer, Object> getMap, JposMessageType messageType){
		StringBuffer result = new StringBuffer();
		Date date = new Date();
		tTransaction.Year().SetAsString((date.getYear() + 1900) + "");
		tTransaction.Date().SetAsString((String) getMap.get(13));
		tTransaction.Time().SetAsString((String) getMap.get(12));
		// 域11  POS 流水号
		if(getMap.containsKey(11))
			tTransaction.SerialNumber().SetAsInteger((Integer)getMap.get(11));
		tTransaction.TransCode().SetAsInteger(200);
		//判断是否相应成功
		if (getMap.get(39).equals("00")) {
			String str = JposConstant.result((String) getMap.get(39));
			logger.debug("响应成功:" + str);
			result.append("响应结果" + str + "\n");
			String timeStr = "时间" + (String) getMap.get(12) + "\n";
			String dateStr = "日期" + (String) getMap.get(13) + "\n";
			result.append(dateStr);
			result.append(timeStr);
			// 获得域4  保单金额
			tTransaction.ProcessList.SaleAmount().SetAsString((String)getMap.get(4));
			// 响应信息
			if(getMap.containsKey(57))
				tTransaction.ProcessList.Response().SetAsString("00"+getMap.get(57));
			else 
				tTransaction.ProcessList.Response().SetAsString("00");
			//域60 订单编号
			if(getMap.containsKey(60))
				tTransaction.ProcessList.ReturnOrderNumber().SetAsString((String)getMap.get(60));
			//域37  参考号
			String ReferenceStr = "";
			if(getMap.containsKey(37))
				ReferenceStr = (String)getMap.get(37);
			//域38   授权码
			String authorizeStr = "";
			if(getMap.containsKey(38))
				authorizeStr = (String)getMap.get(38);
			logger.debug(result.toString());
		}else{
			tTransaction.ProcessList.Response().SetAsString((String)getMap.get(39)+JposConstant.result((String)getMap.get(39)));
		}
		return tTransaction;
	}
}
