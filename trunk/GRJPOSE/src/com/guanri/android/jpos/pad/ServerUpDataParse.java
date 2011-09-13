package com.guanri.android.jpos.pad;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
/**
 * 用于POS数据交换
 * @author Administrator
 *
 */
public class ServerUpDataParse {
	protected TTransaction posMessageBean=null;
	protected byte[] mac = null;
	protected JposPackageFather jposPackage = null;
	
	public ServerUpDataParse(TTransaction posMessageBean) throws PacketException{
		jposPackage = ServerDataHandlerFactory.geServerDataHandler().receivePosData(posMessageBean);
		if(jposPackage==null){
			throw new PacketException("data is error");
		}
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
