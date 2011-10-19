package com.szxys.mhub.base.btprotocol;

import android.text.Html.TagHandler;

class CommPacket
{
	public CommPacket(IBthDataCheckSum aCheckSum)
	{
		fCheckSum = aCheckSum;
		fReplyPacket = new byte[HEANDER_LENGTH];
	}
	public int getHeadlerLength()
	{
		return HEANDER_LENGTH;
	}
	
	public synchronized byte[] makePacketWithData(CommChannel aGivenChannel,byte[] aRawData)
	{
		if ( aRawData == null )
		{
			return null;
		}
		
		int headerLength = getHeadlerLength();
		//not include the last byte which is the sum check
		int totalLength = headerLength + aRawData.length;
		byte[] packet = new byte[totalLength+1];
		
		//set header		
		//lead code
		packet[0] = 0x55;
		packet[1] = (byte)0xAA;
		//channel
		packet[2] = aGivenChannel.fChannelIndex;
		//flag
		packet[3] = (byte) (aGivenChannel.fPacketType|aGivenChannel.fSliceStatus);
		//application type
		packet[4] = (byte) aGivenChannel.fAppId;
		//packet serialize
		aGivenChannel.fCurrentSN++;
		packet[5] = aGivenChannel.fCurrentSN;
		//low byte of application's data
		short dataLen = (short) aRawData.length;
		packet[6] = ByteUtil.lowByte(dataLen);
		//high byte of application's data
		packet[7] = ByteUtil.highByte(dataLen);
		//reserve
		packet[8] = 0;
		//crc of packet header
		packet[9] = fCheckSum.crc(packet,0,headerLength-1);
		
		//set application's data
		//copy data
		System.arraycopy(aRawData,0,packet,headerLength,dataLen);
		//check sum data, the last index is the sum check
		packet[totalLength] = fCheckSum.SumCheck(packet,headerLength,totalLength);
				
		return packet;
	}
	
	public synchronized byte[] makeReplyPacket(byte aAppId,byte aChannel,byte aSN,byte aType)
	{			
		//set header		
		//lead code
		fReplyPacket[0] = 0x55;
		fReplyPacket[1] = (byte)0xAA;
		//channel
		fReplyPacket[2] = aChannel;
		//flag: no slicer
		fReplyPacket[3] = aType;
		//application type
		fReplyPacket[4] = aAppId;
		//packet serialize		
		fReplyPacket[5] = aSN;
		//low byte of application's data		
		fReplyPacket[6] = 0x0;
		//high byte of application's data
		fReplyPacket[7] = 0x0;
		//reserve
		fReplyPacket[8] = 0;
		//crc of packet header
		fReplyPacket[9] = fCheckSum.crc(fReplyPacket,0,HEANDER_LENGTH-1);
			
		return fReplyPacket;
	}
	public synchronized byte[] makePacketWithoutData(CommChannel aGivenChannel)
	{	
		//set header		
		//lead code
		fReplyPacket[0] = 0x55;
		fReplyPacket[1] = (byte)0xAA;
		//channel
		fReplyPacket[2] = aGivenChannel.fChannelIndex;
		//flag
		fReplyPacket[3] = (byte)(aGivenChannel.fPacketType|aGivenChannel.fSliceStatus);
		//application type
		fReplyPacket[4] = (byte) aGivenChannel.fAppId;
		//packet serialize
		aGivenChannel.fCurrentSN++;
		fReplyPacket[5] = aGivenChannel.fCurrentSN;
		//low byte of application's data		
		fReplyPacket[6] = 0x0;
		//high byte of application's data
		fReplyPacket[7] = 0x0;
		//reserve
		fReplyPacket[8] = 0;
		//crc of packet header
		fReplyPacket[9] = fCheckSum.crc(fReplyPacket,0,HEANDER_LENGTH-1);
			
		return fReplyPacket;
	}
//	public synchronized byte[] makePacketWithoutData(CommChannel aGivenChannel)
//	{
//		int headerLength = getHeadlerLength();
//		byte[] packet = new byte[headerLength];				
//		//set header		
//		//lead code
//		packet[0] = 0x55;
//		packet[1] = (byte)0xAA;
//		//channel
//		packet[2] = aGivenChannel.fChannelIndex;
//		//flag
//		packet[3] = (byte)(aGivenChannel.fPacketType|aGivenChannel.fSliceStatus);
//		//application type
//		packet[4] = (byte) aGivenChannel.fAppId;
//		//packet serialize
//		aGivenChannel.fCurrentSN++;
//		packet[5] = aGivenChannel.fCurrentSN;
//		//low byte of application's data		
//		packet[6] = 0x0;
//		//high byte of application's data
//		packet[7] = 0x0;
//		//reserve
//		packet[8] = 0;
//		//crc of packet header
//		packet[9] = fCheckSum.crc(packet,0,headerLength-1);
//			
//		return packet;
//	}

	public static void printData(byte[] data,int aStart,int length) {
		StringBuilder sb = new StringBuilder(length);
		for ( int i = aStart; i < aStart+length; i++ )
		{
			sb.append(Integer.toHexString(data[i] & 0xff));
			sb.append(" ");
		}
		BthLog.d(TAG,sb.toString());
	}
	/**
	 * @param aData to be checked, NOTE: aData.length - aStart >= HEANDER_LENGTH
	 * @param aStart where to be checked in the array
	 */
	public boolean isHeaderValidate(byte[] aData,int aStart)
	{		
		//not a complete header
		if ( aData.length - aStart < HEANDER_LENGTH )
		{
			printData(aData,aStart,10);
			BthLog.e(TAG,"data length < header length");
			return false;
		}
		
		//lead code low
		if ( (aData[aStart]&Packet.fLeadCodeLow) != Packet.fLeadCodeLow )
		{
			printData(aData,aStart,10);
			BthLog.e(TAG,"lead code low error");
			return false;
		}
		
		//lead code high
		if ( (aData[aStart+1]&Packet.fLeadCodeHigh) != Packet.fLeadCodeHigh )
		{
			printData(aData,aStart,10);
			BthLog.e(TAG,"lead code high error");
			return false;
		}
		//crc check, HEADER_LENGTH include the last byte which is the check sum
		byte crc = fCheckSum.crc(aData,aStart,aStart+HEANDER_LENGTH-1);
		if ( (crc&aData[aStart+HEANDER_LENGTH-1]) != crc )
		{
			printData(aData,aStart,10);
			BthLog.e(TAG,"crc check error");
			return false;
		}
		
		return true;
	}
	
	/**
	 * assume that you called {@link #isHeaderValidate} before calling this,
	 * so this function DO NOT CHECK the parameters and whether they are validate
	 */
	public Packet setHeaderInfo(byte[] aData,int aStart)
	{
		Packet packet = new Packet();
		
		//channel
		packet.fChannel = aData[aStart+Packet.CHANNEL];
		//flag
		packet.fFlag = aData[aStart+Packet.FLAG];
		//application's id
		packet.fAppId = aData[aStart+Packet.APP_ID];
		//SN
		packet.fSN = aData[aStart+Packet.SN];
		//data length low
		packet.fDataLenLow = aData[aStart+Packet.DATA_LEN_L];
		//data length high
		packet.fDatalenHigh = aData[aStart+Packet.DATA_LEN_H];
		//crc of header
		packet.fCrc = aData[aStart+Packet.CRC];
		//application's data's check sum
		packet.fAppCheck = aData[aStart+Packet.APP_CHECK];
		
		return packet;
	}
	
	/**
	 * 
	 * @param aData
	 * @param aStart
	 * @param aLength
	 */
	public byte[] getAppData(byte[] aData,int aStart,int aLength)
	{
		//check whether the applicaion's data is correct
		//the last byte is the check sum
		byte sumCheck = aData[aStart+aLength];
		byte calSum = fCheckSum.SumCheck(aData,aStart,aStart+aLength);
		//if ( (calSum&aPacket.fAppCheck)!=aPacket.fAppCheck )
		if ( ByteUtil.isEqual(calSum,sumCheck) == false )
		{
			BthLog.e(TAG,"sum check error");
			printData(aData,aStart,aStart+aLength+1);
			return null;
		}
		byte[] retVal = new byte[aLength];
		System.arraycopy(aData,aStart,retVal,0,aLength);
		return retVal;
	}
	
	public static byte getPacketType(Packet aPacket)
	{
		//higher 4 bit
		return (byte)(aPacket.fFlag&0xF0);
	}
	public static byte getSlicerStatus(Packet aPacket)
	{
		//lower 4 bit
		return (byte)(aPacket.fFlag&0x0F);
	}
	private static final String TAG = "bt.CommPacket";
	//packet type: high 4 bit of FLAG
	public final static byte PACKET_DATA = 0x10;
	public final static byte PACKET_ACK = 0x20;
	public final static byte PACKET_HEART_BEAT = 0x30;
	public final static byte PACKET_DISCONNECTION = 0x40;
	
	
	private final static int HEANDER_LENGTH = 10;
	private IBthDataCheckSum fCheckSum;
	
	private byte[] fReplyPacket;
}

/**
 * a structure of packet
 */
class Packet
{
	public final static byte fLeadCodeLow = 0x55;
	public final static byte fLeadCodeHigh = (byte) 0xAA;
	public byte fChannel;
	public byte fFlag;
	public byte fAppId;
	public byte fSN;
	public byte fDataLenLow;
	public byte fDatalenHigh;
	public final static byte fReserve = 0;
	public byte fCrc;
	public byte fAppBuf[];
	public byte fAppCheck;
	
	public static final int LEAD_CODE_L = 0;
	public static final int LEAD_CODE_H = 1;
	public static final int CHANNEL = 2;
	public static final int FLAG = 3;
	public static final int APP_ID = 4;
	public static final int SN = 5;
	public static final int DATA_LEN_L = 6;
	public static final int DATA_LEN_H = 7;
	public static final int RESERVE = 8;
	public static final int CRC = 9;
	public static final int DATA = 10;
	public static final int APP_CHECK = 11;
}

/**
 * a structure of parsing packet's status
 * @author Administrator
 *
 */
class PacketParser
{
	public Packet fPacket;
	public boolean fParsedHeader;
	public int fTotalLength;
	public int fDataLength;
	public byte fType;
}