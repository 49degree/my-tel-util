/**
 * 
 */
package com.szxys.mhub.base.btprotocol;

/**
 * @author Administrator
 * notify the communication status from {@link AbstractBthDeviceProtocol}
 *  to {@link IBluetoothDevice}
 */
public interface IProtocolInformHandler
{
	/**
	 * received data from outer bluetooth device
	 * @param aReceivedData received data
	 * @param aChannel which channel the data used
	 */
	public abstract void onDataReceived(byte[] aReceivedData,int aChannel);
	
	/**
	 * 
	 * @param aStatus
	 */
	public abstract void inform(int aStatus);
	//status
	public final static int E_RUN_OK = 1;
	public final static int E_RUN_FAILED = 2;
	public final static int E_RECEIVE_ERROR = 3;
	public final static int E_DISCONNECTION = 4;
	
}