package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;

/**
 * 返回数据到POS端
 * @author Administrator
 *
 */
public class ServerDownDataParse {
	private byte[] returnData = null;
	private JposUnPackageFather jposUnPackage = null;
	private ServerDataHandlerImp serverDataHandler = null; 
	final private TTransaction rTransaction;
	public ServerDownDataParse(TTransaction transaction,byte[] returnData) {
		this.returnData = returnData;
		this.rTransaction = transaction;
		serverDataHandler = ServerDataHandlerFactory.geServerDataHandler();
		//解析数据
		try{
			jposUnPackage = serverDataHandler.receiveServerData(returnData);
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
			JposPackageFather jposPackage = serverDataHandler.createJposPackage(jposUnPackage.getMReturnMap(),jposUnPackage.getMMessageType());
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
		if(jposUnPackage!=null&&jposUnPackage.getMReturnMap()!=null&&jposUnPackage.getMReturnMap().containsKey(64)){
			return (byte[])(jposUnPackage.getMReturnMap().get(64));
		}else{
			return null;
		}
	}
	/**
	 * 返回POS需要的对象
	 * @return
	 */
	public TTransaction getTTransaction(){
		TTransaction tTransaction= serverDataHandler.createBackPosObject(rTransaction,
				jposUnPackage.getMReturnMap(), jposUnPackage.getMMessageType());
		return tTransaction;
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

