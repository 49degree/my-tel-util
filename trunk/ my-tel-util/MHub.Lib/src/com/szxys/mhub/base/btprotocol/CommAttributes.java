package com.szxys.mhub.base.btprotocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.szxys.mhub.base.btdevice.BthTransportAttribute;

import android.bluetooth.BluetoothSocket;

/**
 * a struct
 * @author Administrator
 *
 */
class CommAttributes
{
	IProtocolInformHandler fInformHandler;
	BluetoothSocket fBtSocket;
	InputStream fInputStream;
	OutputStream fOutputStream;
	
	/**
	 * transport attribute
	 */
	BthTransportAttribute fTransportAttribute;
	/**
	 * each channel information, to see {@link CommChannel}
	 */
	ArrayList<CommChannel> fChannelArray;
	
	synchronized void initChannels()
	{
		if ( fTransportAttribute.fNumOfChannels <= 0 )
			return;
		
		if ( fChannelArray == null )
		{
			fChannelArray = new ArrayList<CommChannel>(fTransportAttribute.fNumOfChannels);
			CommChannel channel = null;
			for ( byte i = 0; i < fTransportAttribute.fNumOfChannels; i++ )
			{
				channel = new CommChannel();
				channel.fChannelIndex = i;
				channel.fCurrentSN = (byte) 0xFF;//the first SN must be "0"
				//channel.fCurrentSN = 0;
				channel.fAppId = fTransportAttribute.fDeviceType;
				channel.fPacketType = CommPacket.PACKET_DATA;
				fChannelArray.add(channel);
			}
		}
	}
	
	CommChannel getChannel(int aChannel)
	{
		if ( aChannel < 0 || aChannel >= fTransportAttribute.fNumOfChannels )
			return null;
		
		return fChannelArray.get(aChannel);
	}
}
