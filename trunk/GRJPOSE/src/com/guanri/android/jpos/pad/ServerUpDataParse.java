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
	public TTransaction tTransaction =null;
	protected byte[] mac = null;
	protected JposPackageFather jposPackage = null;
	private ServerDataHandlerImp serverDataHandler = null; 
	public ServerUpDataParse(TTransaction tTransaction) throws PacketException{
		this.tTransaction = tTransaction;
		serverDataHandler = ServerDataHandlerFactory.geServerDataHandler();
		jposPackage = serverDataHandler.receivePosData(tTransaction);
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
	
	public byte[] getBeSendData() throws PacketException{
		//设置消息类型
		byte[] dataBuffer = jposPackage.packaged();
		if(dataBuffer == null){
			throw new PacketException("data is error");
		}
		return dataBuffer;
	}
	
	public JposPackageFather getJposPackage(){
		return jposPackage; 
	}
}
