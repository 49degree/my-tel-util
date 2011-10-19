package com.szxys.mhub.base.btprotocol;

/**
 * these methods are NOT multi-thread safe for the sake of effectiveness,
 * the caller need to do this if they use this in concurrent circumstance
 * @author Administrator
 *
 */
interface IBthDataOutputStream
{
	/**
	 * send data
	 * @param aPacketData byte array data to be sent
	 * @return true means send successfully
	 */
	boolean send(byte[] aPacketData);
	
	/**
	 * release it's resources, tell GC it can recycle the memory
	 */
	void releaseResource();
}