package com.szxys.mhub.base.btdevice;


import com.szxys.mhub.base.btprotocol.BthLog;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * receive the system broadcast that the outer bluetooth device requires 
 * to connect to the phone
 * @author Administrator
 *
 */
class BthPairingRequest extends BroadcastReceiver
{	
	/**
	 * singleton mode	
	 * @return the single object
	 */
	public static BthPairingRequest getInstance()
	{
		return fInstance;
	}
	
	/**
	 * add one monitor with the specific MAC address
	 * one monitor can monitor more than one outer device
	 * @param aMacAddress the MAC address of the monitored outer device
	 * @param aHandler the handler to receive notify
	 */
	public void addHandler(String aMacAddress,IConnectEventHandler aHandler)
	{
		fHandlerArray.addEventHandler(aMacAddress,aHandler);
	}
	/**
	 * remove all MAC address by the specific handler
	 * @param aHandler the specific handler will be removed
	 */
	public void removeHandler(IConnectEventHandler aHandler)
	{
		fHandlerArray.removeEventHandler(aHandler);
	}
	
	/**
	 * remove all the monitors
	 */
	public void removeAll()
	{
		fHandlerArray.removeAll();
	}
	
	/**
	 * when received system broadcast, system call this function
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//abortBroadcast();
		
		BthLog.i("BthPairingRequest","onReceive");
		String action = intent.getAction();
		String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
		//String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
		//String EXTRA_PASSKEY = "android.bluetooth.device.extra.PASSKEY";
		//int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
		//int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
		if (action.equals(ACTION_PAIRING_REQUEST))
		{
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if ( device != null )
			{
				BthLog.i("BthPairingRequest","address : "+device.getAddress());
				clientConnected(device);
			}
		}
	}
	
	
//	private synchronized void clientConnected(String aMacAddress)
//	{
//		fHandlerArray.inform(aMacAddress);
//	}	
	private synchronized void clientConnected(BluetoothDevice aOuterBtDevice)
	{
		fHandlerArray.inform(aOuterBtDevice);
	}
	private BthPairingRequest()
	{
		fHandlerArray = new IConnectEventHandlerArray(5);
	}
	
	/**
	 * container of monitors
	 */
	private IConnectEventHandlerArray fHandlerArray;
	
	/**
	 * the single object
	 */
	private static BthPairingRequest fInstance = new BthPairingRequest();

}
