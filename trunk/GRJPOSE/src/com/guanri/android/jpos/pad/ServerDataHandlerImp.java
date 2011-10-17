package com.guanri.android.jpos.pad;

import java.util.TreeMap;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.iso.JposMessageType;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;

/**
 * 从POS接收数据
 * @author Administrator
 *
 */
public interface ServerDataHandlerImp { 
	/**
	 * 收到POS上送服务器数据 
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	public JposPackageFather receivePosData(TTransaction posMessageBean) throws PacketException;
	/**
	 * 收到POS上送服务器数据 
	 * 进行相应的协议解析 构造传送到服务器的数据
	 * @param posMessageBean 从POS机获取的数据
	 */
	public JposUnPackageFather receiveServerData(byte[] receiveData) throws PacketException;
	
	
	/**
	 * 构造封包对象
	 */
	public JposPackageFather createJposPackage(TreeMap<Integer, Object> sendMap, JposMessageType messageType);
	
	/**
	 * 构造返回POS的数据
	 */
	public TTransaction createBackPosObject(TTransaction transaction,TreeMap<Integer, Object> sendMap, JposMessageType messageType);
	
}
