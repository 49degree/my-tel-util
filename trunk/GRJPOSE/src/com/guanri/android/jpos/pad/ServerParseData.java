package com.guanri.android.jpos.pad;

import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefine99Bill;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.pad.ServerDataHandlerFactory;
/**
 * 用于POS数据交换
 * @author Administrator
 *
 */
public class ServerParseData {
	protected PosMessageBean posMessageBean=null;
	protected byte[] mac = null;
	protected JposPackageFather jposPackage = null;
	
	public ServerParseData(PosMessageBean posMessageBean){
		jposPackage = ServerDataHandlerFactory.geServerDataHandler().receivePosData(posMessageBean);
		
	}
	
	/**
	 * 获取构造MAC数据block
	 */
	public byte[] getMab(){
		//得到模拟数据的mac值
		byte[] mab = jposPackage.packagMacBlock();
		return mab;
	}
	
	/**
	 * 设置MAC值
	 * @param mac
	 * @return
	 */
	public boolean setMac(byte[] mac){
		return jposPackage.setMac(mac);
	}
	
	public byte[] getBeSendData(){
		//设置消息类型
		return jposPackage.packaged();
	}
	
	public JposPackageFather getJposPackage(){
		return jposPackage; 
	}
}
