package com.guanri.android.jpos.pad;

import java.util.TreeMap;

import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;

/**
 * 返回数据到POS端
 * @author Administrator
 *
 */
public class ServerDownDataParse {
	public byte[] returnData = null;
	protected JposUnPackageFather jposUnPackage = null;
	public ServerDownDataParse(byte[] returnData){
		this.returnData = returnData;
		//解析数据
		try{
			jposUnPackage = ServerDataHandlerFactory.geServerDataHandler().receiveServerData(returnData);
			jposUnPackage.unPacketed();
		}catch(Exception e){
			//返回失败信息
		}
	}
	/**
	 * 获取返回数据的MAB
	 * @return
	 */
	public byte[] getMab(){
		if(jposUnPackage!=null&&jposUnPackage.getMReturnMap()!=null){
			JposPackage99Bill jposPackage99Bill = new JposPackage99Bill(jposUnPackage.getMReturnMap(),jposUnPackage.getMMessageType());
			return jposPackage99Bill.packagMacBlock();
		}else{
			return null;
		}
	}
	/**
	 * 获取返回数据的MAC
	 * @return
	 */
	public byte[] getMac(){
		if(jposUnPackage!=null&&jposUnPackage.getMReturnMap()!=null&&jposUnPackage.getMReturnMap().containsKey("64")){
			return (byte[])(jposUnPackage.getMReturnMap().get("64"));
		}else{
			return null;
		}
	}
	
	/**
	 * 保存数据
	 * @return
	 */
	public boolean saveOrder(){
		return true;
	}
}
