/**
 * 
 */
package com.szxys.mhub.base.btdevice;
/**
 * create a concrete logic bluetooth device
 * @author Administrator
 */
public abstract class BthDeviceFactory
{	
	/**
	 * create a concrete logic bluetooth device
	 * @param aBthAttribute to see {@link BthTransportAttribute}
	 * @param aEventHandler a handler to receive callback events, must not be null, to see {@link IBthDeviceEventHandler}
	 * @return a {@link IBluetoothDevice} object if all parameters are valid, null otherwise
	 */
	public static IBluetoothDevice createBthDevice(BthTransportAttribute aBthAttribute,IBthDeviceEventHandler aEventHandler)
	{
		IBluetoothDevice ibt = null;
		try
		{
			ibt = new BluetoothDeviceImpl(aBthAttribute,aEventHandler);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		return ibt;
	}
}