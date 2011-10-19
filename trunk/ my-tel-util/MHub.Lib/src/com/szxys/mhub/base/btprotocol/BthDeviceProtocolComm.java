package com.szxys.mhub.base.btprotocol;

import com.szxys.mhub.base.btdevice.BthTransportAttribute;

import android.bluetooth.BluetoothSocket;

class BthDeviceProtocolComm extends AbstractBthDeviceProtocol
{
	public BthDeviceProtocolComm(BthTransportAttribute aBthTransAttribute,IProtocolInformHandler aInformHandler,
			BluetoothSocket aBtSocket) throws Exception
	{		
		if ( aBthTransAttribute.fNumOfChannels == 0 )
		{
			fCommPolicy = new CommPolicyOfRawMode(aBthTransAttribute,aInformHandler,aBtSocket);
		}
		else
		{
			fCommPolicy = new CommPolicyOfCommonMode(aBthTransAttribute,aInformHandler,aBtSocket);
		}
	}

	@Override
	public boolean run()
	{
		return fCommPolicy.run();
	}

	@Override
	public synchronized boolean postData(byte[] aSendData,int aChannel)
	{
		return fCommPolicy.postData(aSendData,aChannel);
	}

	@Override
	public void shutDown()
	{
		fCommPolicy.shutDown();
	}
	
	private CommPolicy fCommPolicy;
}