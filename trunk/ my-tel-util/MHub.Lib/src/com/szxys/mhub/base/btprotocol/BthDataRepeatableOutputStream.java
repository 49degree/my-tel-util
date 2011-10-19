package com.szxys.mhub.base.btprotocol;

class BthDataRepeatableOutputStream implements IBthDataOutputStream
{
	public BthDataRepeatableOutputStream(IBthDataOutputStream aBthOutputStream,int aReapteTimes)
	{
		fBthOutputStream = aBthOutputStream;
		fReapteTimes = aReapteTimes;		
	}
	@Override
	public boolean send(byte[] aPacketData)
	{	
		int sendTimes = fReapteTimes;
		while ( sendTimes-- > 0 )
		{			
			if ( fBthOutputStream.send(aPacketData) == true )
			{
				return true;
			}			
		}		
		return false;
	}
	
	@Override
	public void releaseResource()
	{
		fBthOutputStream.releaseResource();
		fBthOutputStream = null;
	}
	
	private IBthDataOutputStream fBthOutputStream;
	private int fReapteTimes;
}
