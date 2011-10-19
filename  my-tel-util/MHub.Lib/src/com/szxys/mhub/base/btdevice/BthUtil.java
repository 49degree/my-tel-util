package com.szxys.mhub.base.btdevice;

import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;

public class BthUtil
{
	/**
	 * comes from platform/packages/apps/Settings.git
	 * \Settings\src\com\android\settings\bluetooth\CachedBluetoothDevice.java
	 * 
	 * @param btDevice
	 * @return
	 */
	static public boolean createBond(BluetoothDevice aBtDevice)
	{
		Boolean returnValue = false;
		try
		{
			Method createBondMethod = aBtDevice.getClass().getMethod("createBond");
			returnValue = (Boolean) createBondMethod.invoke(aBtDevice);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		System.out.println("createBond :" + returnValue);
		return returnValue;
	}

	/**
	 * comes from 
	 * platform/packages/apps/Settings.git
	 * \Settings\src\com\android\settings\bluetooth\CachedBluetoothDevice.java
	 * @param btDevice
	 * @return
	 */
	static public boolean removeBond(BluetoothDevice aBtDevice)
	{
		Boolean returnValue = false;
		try
		{
			Method removeBondMethod = aBtDevice.getClass().getMethod("removeBond");
			returnValue = (Boolean) removeBondMethod.invoke(aBtDevice);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
		}
		System.out.println("removeBond :" + returnValue);
		return returnValue;
	}

	static public boolean setPin(BluetoothDevice aBtDevice,String aPairingCode)
	{
		Boolean returnValue = false;
		try
		{
			Method setPinMethod = aBtDevice.getClass().getDeclaredMethod("setPin",
					new Class[] { byte[].class });
			returnValue = (Boolean) setPinMethod.invoke(aBtDevice,
					new Object[] { aPairingCode.getBytes() });			
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		System.out.println("setPin :" + returnValue);
		return returnValue;
	}
}
