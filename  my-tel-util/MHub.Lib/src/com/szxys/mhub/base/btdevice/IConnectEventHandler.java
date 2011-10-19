package com.szxys.mhub.base.btdevice;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * notify the outer bluetooth device has tried to connect this
 * @author Administrator
 *
 */
interface IConnectEventHandler
{
	/**
	 * 
	 * @param aSocket the socket of communicating with outer device
	 */
	void onConnect(BluetoothSocket aSocket);

	/** 
	 * @param aOuterBtDevice represents the outer bluetooth device
	 */
	void onConnect(BluetoothDevice aOuterBtDevice);
}


/**
 * assistant class, manages all the monitors
 * @author Administrator
 *
 */
class IConnectEventHandlerArray
{
	/**
	 * constructor, construct a container to manage all the monitors
	 * @param aCapacity initialized size
	 */
	public IConnectEventHandlerArray(int aCapacity)
	{
		//check validate
		if (aCapacity <= 0)
			aCapacity = 3;
		fEventArrayList = new ArrayList<IConnectEventHandler>(aCapacity);
		fMacArrayList = new ArrayList<String>(aCapacity);
	}

	/**
	 * add one monitor with the specific MAC address
	 * one monitor can monitor more than one outer device
	 * @param aMacAddress the MAC address of the monitored outer device
	 * @param aHandler the handler to receive notify
	 */
	public synchronized void addEventHandler(String aMacAddress,IConnectEventHandler aHandler)
	{
		fEventArrayList.add(aHandler);
		fMacArrayList.add(aMacAddress);
	}

	/**
	 * remove one MAC address according to  the specific handler
	 * @param aMacAddress will be removed MAC address
	 * @param aHandler the specific handler being attached with MAC address
	 */
	public synchronized void removeEventHandler(String aMacAddress,
			IConnectEventHandler aHandler)
	{
		int num = fEventArrayList.size();
		for (int i = 0; i < num; i++)
		{
			if (aMacAddress.equals(fMacArrayList.get(i)) == true)
			{
				for (int j = 0; j < num; j++)
				{
					if (aHandler == fEventArrayList.get(j) )
					{
						fEventArrayList.remove(j);
						fMacArrayList.remove(i);
						return;
					}
				}
			}
		}
	}

	/**
	 * remove all MAC address by the specific handler
	 * @param aHandler the specific handler will be removed
	 */
	public synchronized void removeEventHandler(IConnectEventHandler aHandler)
	{
		int index = 0;
		while (index < fEventArrayList.size())
		{
			if (aHandler == fEventArrayList.get(index) )
			{
				fEventArrayList.remove(index);
				fMacArrayList.remove(index);
				continue;
			}
			index++;
		}
	}

	/**
	 * notify the monitors outer Bluetooth device has been connected to this
	 * @param aSocket the connected socket
	 */
	public synchronized void inform(BluetoothSocket aSocket)
	{
		if (aSocket != null)
		{
			//get the MAC address of the remote device
			String mac = aSocket.getRemoteDevice().getAddress();
			int num = fMacArrayList.size();
			for (int i = 0; i < num; i++)
			{
				if (mac.equals(fMacArrayList.get(i)))//match
				{
					fEventArrayList.get(i).onConnect(aSocket);
				}
			}
		}
	}
	
	/**
	 * notify the monitors outer Bluetooth device required for pairing
	 * @param aOuterBtDevice represents the outer bluetooth device
	 */
	public synchronized void inform(BluetoothDevice aOuterBtDevice)
	{
		String macAddress = aOuterBtDevice.getAddress();
		int num = fMacArrayList.size();
		for (int i = 0; i < num; i++)
		{
			if (macAddress.equals(fMacArrayList.get(i)))
			{
				fEventArrayList.get(i).onConnect(aOuterBtDevice);
			}
		}
	}
	
	/**
	 * remove all the monitors
	 */
	public synchronized void removeAll()
	{
		//ArrayList.clear() will set each element to null
		fEventArrayList.clear();
		fMacArrayList.clear();
	}
	
	/**
	 * container of the event handlers
	 */
	private ArrayList<IConnectEventHandler> fEventArrayList;
	
	/**
	 * container of the MAC addresses
	 */
	private ArrayList<String> fMacArrayList;
}
