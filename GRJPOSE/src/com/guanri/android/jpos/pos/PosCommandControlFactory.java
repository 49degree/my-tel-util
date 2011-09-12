package com.guanri.android.jpos.pos;

/**
 * 串口通信控制
 * 为单例模式
 * @author Administrator
 *
 */
public class PosCommandControlFactory {
	/**
	 * 根据需要获取串口或者FSK通讯控制实例
	 * @return
	 */
	public static PosCommandControlImp getPosCommandControl(){
		//FSKPortPosCommandControl.getInstance();//返回FSK通信的实例
		
		return PosCommandControlSerialPort.getInstance();//返回串口通信的实例
	}
}
