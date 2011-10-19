package com.szxys.mhub.base.btprotocol;

/**
 * a struct
 * @author Administrator
 *
 */
class CommChannel
{
	public CommChannel()
	{
		fAppId = 1;
		fChannelIndex = 0;
		fCurrentSN = 0;
	}
	/**
	 * the application's Id
	 */
	public int fAppId;
	/**
	 * channel number
	 */
	public byte fChannelIndex;
	
	/**
	 * package number, used circularly,
	 * for example {@code fCurrentSN++}
	 */
	public byte fCurrentSN;
	
	public byte fPacketType;
	
	public byte fSliceStatus;
	
	public boolean identical(CommChannel aAnother)
	{
		if ( aAnother == null )
			return false;
		if ( (fAppId == aAnother.fAppId)
			&&(fChannelIndex == aAnother.fChannelIndex)
			&&(fCurrentSN == aAnother.fCurrentSN))
		{
			return true;
		}
		
		return false;
	}
	public void set(CommChannel aAnother)
	{
		fAppId = aAnother.fAppId;
		fChannelIndex = aAnother.fChannelIndex;
		fCurrentSN = aAnother.fCurrentSN;
		fPacketType = aAnother.fPacketType;
		fSliceStatus = aAnother.fSliceStatus;
	}
}