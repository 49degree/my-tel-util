/**
 * 
 */
package com.szxys.mhub.base.btprotocol;


import com.szxys.mhub.base.btdevice.BthTransportAttribute;
import com.szxys.mhub.base.btdevice.IBthDeviceEventHandler;
import com.szxys.mhub.base.btdevice.ProtocolType;

import android.bluetooth.BluetoothSocket;

/**
 * @author Administrator
 * abstract bluetooth protocol, only defines basic functions
 */
public abstract class AbstractBthDeviceProtocol
{
	/**
	 * create a concrete protocol according the parameters
	 * @param aBthTransAttribute to see {@link BthTransportAttribute}
	 * @param aInformHandler a handler to receive callback events, must not be null, to see {@link IBthDeviceEventHandler}
	 * @param aBtSocket the connected socket to communicate with outer bluetooth device 
	 * @return a concrete {@link AbstractBthDeviceProtocol} object if all parameters are valid, null otherwise
	 */
	public static AbstractBthDeviceProtocol createConcreteProtocol(BthTransportAttribute aBthTransAttribute,IProtocolInformHandler aInformHandler,BluetoothSocket aBtSocket)
	{
		AbstractBthDeviceProtocol absBtPro = null;
		if ( aBthTransAttribute.fProtocolType == ProtocolType.BTH_COMM )
		{
			try
			{
				absBtPro = new BthDeviceProtocolComm(aBthTransAttribute,aInformHandler,aBtSocket); 
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
			return absBtPro;
		}
		
		
		return null;
	}
//	/**
//	 * constructor
//	 * @param aCommEventHandler receive status of communicating notifications, to see {@link IProtocolInformHandler}
//	 * @param aBtSocket the socket to communicate with outer device
//	 */
//	public AbstractBthDeviceProtocol(byte aAppId,IProtocolInformHandler aInformHandler,BluetoothSocket aBtSocket,int aNumOfChannles)
//	{		
//	}
	
	/**
	 * initialize all the works before communication
	 */
	public abstract boolean run();
	
	/**
	 * send raw data
	 * @param aSSID the type of subsystem
	 * @param aSendData raw data
	 * @param aChannel channel to used
	 * @return
	 */
	public abstract boolean postData(byte[] aSendData,int aChannel);
	
	/**
	 * stop communicate, and release all the resources
	 */
	public abstract void shutDown();
}
