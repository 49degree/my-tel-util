/**
 * 
 */
package com.szxys.mhub.base.btdevice;

/**
 * @author Administrator
 * 通知当前蓝牙物理设备的工作状态的回调接口
 */
public interface IBthDeviceEventHandler
{
	/**
	 * 在通信过程中有错误发生
	 * @param aError 错误类型
	 */
	void onError(int aError);
	
	/**
	 * 接收到数据
	 * @param aReceivedData 接收到的原始数据
	 * @param aChannel 数据所使用的通道号
	 */
	void onReceived(byte[] aReceivedData, int aChannel);
	
	/**
	 * 是否可以与外部蓝牙设备进行正常的通信
	 * @param aSuccess true表示可以进行通信
	 */
	void onRun(boolean aSuccess);
}
