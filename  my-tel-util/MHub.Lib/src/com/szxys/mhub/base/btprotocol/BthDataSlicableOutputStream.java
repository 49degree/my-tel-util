package com.szxys.mhub.base.btprotocol;

class BthDataSlicableOutputStream implements IBthDataOutputStream
{
	BthDataSlicableOutputStream(IBthDataOutputStream aBthOutputStream,int aSlicerMaxLength,IBthDataSlicableProtocol aSlicableProtocol)
	{
		fBthOutputStream = aBthOutputStream;
		fSlicerMaxLength = aSlicerMaxLength;
		fSlicableProtocol = aSlicableProtocol;		
	}
	@Override
	public boolean send(byte[] aPacketData)
	{
		if ( aPacketData == null )
		{
			return sendNonData();
		}
		
		
		int left = aPacketData.length;
		int sent = 0;
		int length = 0;		
		byte[] temp = null;
		byte slicerStatus = 0;
		while ( left > 0 )
		{
			//get actual data length to be sent
			length = left > fSlicerMaxLength ? fSlicerMaxLength : left;
			
			//set slice status
			//the first loop
			if ( sent == 0 )
			{
				if ( left > fSlicerMaxLength )
					slicerStatus = IBthDataSlicableProtocol.SLICER_START;
				else
					slicerStatus = IBthDataSlicableProtocol.SLICER_NONE;
			}
			else//more than one loop
			{
				if ( left > fSlicerMaxLength )
					slicerStatus = IBthDataSlicableProtocol.SLICER_MIDDLE;
				else {
					slicerStatus = IBthDataSlicableProtocol.SLICER_END;
				}
			}
			
			//get packet
			temp = getSubPacket(aPacketData,sent,length,slicerStatus);
			if ( temp == null )
			{
				return false;
			}
			if ( fBthOutputStream.send(temp) == false )
				return false;
			//reset
			sent += length;
			left -= fSlicerMaxLength;
		}
		
		return true;
	}
	
	@Override
	public void releaseResource()
	{
		fBthOutputStream.releaseResource();
		fBthOutputStream = null;
	}
	
	private byte[] getSubPacket(byte[] aSrc,int aStart,int length,byte aSlicerStatus)
	{
		byte[] temp = new byte[length];
		System.arraycopy(aSrc,aStart,temp,0,length);

		return fSlicableProtocol.getPacket(temp,aSlicerStatus);
	}

	private boolean sendNonData()
	{
		byte[] packet = fSlicableProtocol.getPacket(null,IBthDataSlicableProtocol.SLICER_NONE);
		if ( packet == null )
			return false;
		
		return fBthOutputStream.send(packet);
	}
	private IBthDataOutputStream fBthOutputStream;
	private final int fSlicerMaxLength;
	private final IBthDataSlicableProtocol fSlicableProtocol;
}
