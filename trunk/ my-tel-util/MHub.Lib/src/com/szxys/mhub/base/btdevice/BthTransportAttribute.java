package com.szxys.mhub.base.btdevice;

import android.content.Context;


/**
 * a structure, include all the attributes of logical bluetooth device 
 * and protocol policies
 * @author Administrator
 *
 */
public class BthTransportAttribute
{
	//used to create a logical bluetooth device 	
	/**
	 * the MAC address of the outer buletooth device.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public String fMacAddress;
	
	/**
	 * the pairing code of the outer bluetooth device.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public String fPairingCode;
	
	/**
	 * the protocol type, to see {@link ProtocolType}.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public int fProtocolType;
	
	/**
	 * false if the phone connect to the outer bluetooth device 
	 * otherwise false.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public boolean fIsPassiveMode;
	
	/**
	 * how many channels the outer bluetooth device used.
	 * if it's value is "0", then it means we use raw mode;
	 * if it's value is "greater than 0", then it means we use common mode;
	 * otherwise the value is invalid.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public byte fNumOfChannels;
	
	/**
	 * the type of outer bluetooth device.
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public byte fDeviceType;
	
	/**
	 * the application's context
	 * {@linkplain NOTE MUST SET this parameter, because the default value is no sense.}
	 */
	public Context fAppContext;
	
	//protocol policy	
	/**
	 * the max time to wait the ACK after sent the data to a outer bluetooth device.
	 * {@linkplain NOTE the default value is useful,unit in milliseconds}
	 */
	public int fWaitAckTime;
	
	/**
	 * the size of a slicing.
	 * {@linkplain NOTE the default value is valid,unit in byte}
	 */
	public int fSlicingSize;
	
	/**
	 * the max resent times after the first send command failed.
	 * {@linkplain NOTE the default value is valid}
	 */
	public int fResentTimes;
	
	/**
	 * the frequency of heart beat.
	 * {@linkplain NOTE the default value is valid,unit in milliseconds}
	 */
	public int fHeartBeatFrequency;
	
	/**
	 * set default values.
	 * {@linkplain NOTE but some are invalid}
	 */
	public BthTransportAttribute()
	{
		//the following default values are no sense
		fMacAddress = null;
		fPairingCode = null;
		fProtocolType = -1;
		fIsPassiveMode = false;
		fNumOfChannels = -1;
		fDeviceType = -1;
		fAppContext = null;
		
		//the following default values are very useful
		fWaitAckTime = 3000;
		fSlicingSize = 5 * 1024;
		fResentTimes = 3;
		fHeartBeatFrequency = 10 * 1000;
	}
	
	public void set(final BthTransportAttribute attribute)
	{
		//logical attributes
		fMacAddress = new String(attribute.fMacAddress);
		fPairingCode = new String(attribute.fPairingCode);
		fProtocolType = attribute.fProtocolType;
		fIsPassiveMode = attribute.fIsPassiveMode;
		fNumOfChannels = attribute.fNumOfChannels;
		fDeviceType = attribute.fDeviceType;
		fAppContext = attribute.fAppContext;
		
		//policy
		fWaitAckTime = attribute.fWaitAckTime;
		fSlicingSize = attribute.fSlicingSize;
		fResentTimes = attribute.fResentTimes;
		fHeartBeatFrequency = attribute.fHeartBeatFrequency;
	}
	
	public boolean identical(final BthTransportAttribute attribute)
	{
		if ( attribute == null )
			return false;
		
		if ( attribute.fMacAddress == null )
			return false;
		
		if ( attribute.fMacAddress.equals(fMacAddress) == false )
			return false;
		
		return true;
	}
}

