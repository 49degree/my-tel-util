package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposUnPackageFather;

/**
 * 返回数据到POS端
 * @author Administrator
 *
 */
public class ServerDownDataParse {
	private byte[] returnData = null;
	private JposUnPackageFather jposUnPackage = null;
	public ServerDownDataParse(byte[] returnData) {
		this.returnData = returnData;
		//解析数据
		try{
			jposUnPackage = ServerDataHandlerFactory.geServerDataHandler().receiveServerData(returnData);
			jposUnPackage.unPacketed();
		}catch(Exception e){
			//返回失败信息
			e.printStackTrace();
		}
	}
	/**
	 * 获取返回数据的MAB
	 * @return
	 */
	public byte[] getMab(){
		if(jposUnPackage!=null&&jposUnPackage.getMReturnMap()!=null){
			JposPackageFather jposPackage = ServerDataHandlerFactory.geServerDataHandler().createJposPackage(jposUnPackage.getMReturnMap(),jposUnPackage.getMMessageType());
			return jposPackage.packagMacBlock();
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
	
	
	public byte[] getReturnData() {
		return returnData;
	}
	public void setReturnData(byte[] returnData) {
		this.returnData = returnData;
	}
	public JposUnPackageFather getJposUnPackage() {
		return jposUnPackage;
	}
	public void setJposUnPackage(JposUnPackageFather jposUnPackage) {
		this.jposUnPackage = jposUnPackage;
	}
	
	
}
