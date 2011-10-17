package com.guanri.android.jpos.pos;


import com.guanri.android.lib.log.Logger;

/**
 * 串口通信控制
 * 为单例模式
 * @author Administrator
 *
 */
public class PosCommandControlFSKPort extends PosCommandControlImp{
	static Logger logger = Logger.getLogger(PosCommandControlFSKPort.class);
	
	private static PosCommandControlImp instance = new PosCommandControlFSKPort();
	/**
	 * 获得控制对象
	 * @return
	 */
	public static PosCommandControlImp getInstance(){
		return instance;
	}
	/**
	 * 构造函数
	 */
	private PosCommandControlFSKPort(){
	}
	
	/**
	 * 发送数据到串口设备，同一时间只能有一个线程调用
	 * @param data
	 */
	public synchronized void sendData(byte[] data,SendDataResultListener sendDataResultListener,long waitTime){
	}
	

	
	/**
	 * 关闭端口
	 */
	public void portClose(){
	}
}
