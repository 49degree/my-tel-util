package com.szxys.mhub.base.btdevice;

/**
 * @author Administrator
 * 抽象的逻辑蓝牙设备
 */
public interface IBluetoothDevice
{
//	/**
//	 * 设置蓝牙设备的配对码
//	 * @param aPairingCode 配对码，不能为null或者长度不能为零
//	 * @return true表示设置成功
//	 */
//	boolean setPairingCode(String aPairingCode);
//	
//	void setApplicationId(byte aAppId);
	/** 
	 * 做好发送和接收数据前的所有初始化工作，当这个函数返回后建议调用{@link #getStatus}
	 */
	void run();
	
	/** 
	 * 停止发送和接收数据，并释放所占用的所有资源
	 */
	void shutDown();
	
	/**
	 * 发送原始数据
	 * @param aSendData 发送的原始数据
	 * @param aChannel 使用的通道号
	 * @return true表示发送成功
	 */
	boolean postData(byte[] aSendData, int aChannel);
	
	/**
	 * 判断两个逻辑蓝牙设备是否代表同一个物理蓝牙设备
	 * @param aBtDevice 逻辑蓝牙设备
	 * @return true表示代表同一个物理蓝牙设备
	 */
	boolean identical(IBluetoothDevice aBtDevice);
	
	/**
	 * 
	 * @return 连接的外部蓝牙设备的MAC地址
	 */
	String getMac();
	
	/**
	 * 
	 * @return 使用的蓝牙协议类型
	 */
	int getProtocolType();
	
	/**
	 * 
	 * @return true表示外部蓝牙设备是被动模式的
	 */
	boolean getPassiveMode();
	
	/**
	 * 
	 * @return 使用的通道个数
	 */
	int getNumOfChannels();
	
	/**
	 * 
	 * @return 当前的通信状态
	 */
	int getStatus();
}
