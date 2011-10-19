package com.szxys.mhub.base.btprotocol;

/**
 * a call back interface to pack raw data to a packet
 * @author Administrator
 *
 */
interface IBthDataSlicableProtocol
{
	/**
	 * get a packet
	 * @param aRawData raw data
	 * @param aSlicerStatus slice status
	 * @return a packed packet
	 */
	byte[] getPacket(byte[] aRawData,byte aSlicerStatus);
	
	//packet type: low 4 bit of FLAG
	public final static byte SLICER_NONE = 0x00;
	public final static byte SLICER_START = 0x01;
	public final static byte SLICER_MIDDLE = 0x02;
	public final static byte SLICER_END = 0x04;
}
