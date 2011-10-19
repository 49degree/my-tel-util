package com.szxys.mhub.base.btprotocol;

class ByteUtil
{
	public static byte lowByte(short value)
	{		
		return (byte) (value & 0x00FF);
	}
	public static byte highByte(short value)
	{		
		return (byte) ((value >> 8) & 0x00FF);
	}
	public static short getUnsignedByte(byte[] buffer,int start)
	{
		assert(start >= 0 && start < buffer.length);
		return (short) (buffer[start] & 0xFF);
	}
//	public static int getUnsignedShortViaLittleEndianBytes(byte[] buffer,int start)
//	{
//		assert(start >= 0 && start < buffer.length-1);
//		return ((buffer[start+1]<<8) | buffer[start]) & 0xFFFF;
//	}
//	public static int getUnsignedShortViaBigEndianBytes(byte[] buffer,int start)
//	{
//		assert(start >= 0 && start < buffer.length-1);
//		return ((buffer[start]<<8) | buffer[start+1]) & 0xFFFF;
//	}
	public static int shortFromLittleEndianBytes(byte[] buffer,int start)
	{
		assert(start >= 0 && start < buffer.length-1);
		int value = 0;
		value |= buffer[start];
		value |= (buffer[start+1] << 8);		
		
		return value;
	}
	public static int shortFromBigEndianBytes(byte[] buffer,int start)
	{
		assert(start >= 0 && start < buffer.length-1);
		int value = 0;
		value |= buffer[start+1];
		value |= (buffer[start] << 8);		
		
		return value;
	}
	
	public static boolean isEqual(byte aNum1,byte aNum2)
	{
		return ((byte)(aNum1^aNum2) == 0) ? true : false;
	}
	
//	public static byte[] intToBytes(int num) {
//
//		byte[] b = new byte[4];
//		for (int i = 0; i < 4; i++) {
//			b[i] = (byte) (num >>> (i * 8));
//		}
//		return b; 
//	}

	public static void intToLittleEndianBytes(byte[] buffer,int start,int data)
	{
		assert(start >= 0 && start < buffer.length-3);
		buffer[start] = (byte) (data & 0xFF);
		buffer[start+1] = (byte) ((data >> 8) & 0xFF); 
		buffer[start+2] = (byte) ((data >> 16) & 0xFF);
		buffer[start+3] = (byte) ((data >> 24) & 0xFF); 
	}
	public static int intFromLittleEndianBytes(byte[] buffer,int start)
	{
		assert(start >= 0 && start < buffer.length-3);
		int value = 0;
		value |= buffer[start];
		value |= (buffer[start+1] << 8);
		value |= (buffer[start+2] << 16);
		value |= (buffer[start+3] << 24);
		
		return value;
	}
}
