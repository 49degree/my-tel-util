package com.szxys.mhub.base.btdevice;



/**
 * a structure, representative a outer bluetooth device's information
 * @author Administrator
 *
 */
class BthDeviceInfo
{
	//initializing
	public BthDeviceInfo()
	{
		fMacAddress = null;
		fProtocolType = ProtocolType.BTH_COMM;
		fPassiveMode = false;
		fChannelNumbers = 1;
		fStatus = BthCommStatus.STATUS_DISCONNECTION;
		fPairingCode = null;
		fAppId = 0;
	}
	/**
	 * MAC address of outer device
	 */
	public String fMacAddress;
	/**
	 * type of communication protocol
	 */
	public int fProtocolType;
	/**
	 * if it is a passive mode, true means it's a passive mode
	 */
	public boolean fPassiveMode;
	/**
	 * channel numbers of using
	 */
	public int fChannelNumbers;
	/**
	 * current status
	 */
	public int fStatus;
//	public final static int STATUS_DISCONNECT = 1;
//	public final static int STATUS_CONNECT = 2;
//	public final static int STATUS_IN_ERROR = 3;
//	public final static int STATUS_SENDING = 4;
//	public final static int STATUS_IDLE = 5;
	/**
	 * pairing code, used before building the communication
	 */
	public String fPairingCode;
	
	public byte fAppId;
	
	public boolean identical(BthDeviceInfo aBtDeviceInfo)
	{
		if ( aBtDeviceInfo == null )
			return false;
		
		//same object
		if ( this.hashCode() == aBtDeviceInfo.hashCode() )
			return true;
		
		//other situation
		if ( fMacAddress.equals(aBtDeviceInfo.fMacAddress) )
		{
			return true;
		}
		
		return false;
	}
}
