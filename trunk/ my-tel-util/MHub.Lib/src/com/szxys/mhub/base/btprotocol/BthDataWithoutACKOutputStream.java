package com.szxys.mhub.base.btprotocol;

import java.io.OutputStream;

class BthDataWithoutACKOutputStream implements IBthDataOutputStream
{
	public BthDataWithoutACKOutputStream(OutputStream aOutputStream)
	{
		fOutputStream = aOutputStream;
	}
	@Override
	public boolean send(byte[] aPacketData)
	{
		try
		{
			fOutputStream.write(aPacketData);
			return true;
		}
		catch (Exception exception)
		{
			//BthLog.e("bt.withoutACKOutputStream","write with error");
			//exception.printStackTrace();
			BthLog.e("bt.withoutACKOutputStream","write with error",exception);
		}
		
		return false;
	}
	
	@Override
	public void releaseResource()
	{
		fOutputStream = null;		
	}
	private OutputStream fOutputStream;
}
