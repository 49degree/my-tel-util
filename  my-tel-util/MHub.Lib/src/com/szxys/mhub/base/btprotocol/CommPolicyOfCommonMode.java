package com.szxys.mhub.base.btprotocol;

import java.io.IOException;
import java.io.InputStream;

import com.szxys.mhub.R;
import com.szxys.mhub.base.btdevice.BthTransportAttribute;

import android.bluetooth.BluetoothSocket;
import android.media.MediaPlayer;

class CommPolicyOfCommonMode implements CommPolicy
{
	public CommPolicyOfCommonMode(BthTransportAttribute aBthTransAttribute,IProtocolInformHandler aInformHandler,
			BluetoothSocket aBtSocket) throws Exception
	{
		// check parameters' validate
		if ( aBthTransAttribute == null )
		{
			BthLog.e(TAG, "at least one parameter is invalid");
			throw new Exception("at least one parameter is invalid");
		}
		if ( aInformHandler == null || aBtSocket == null || aBthTransAttribute.fNumOfChannels < 1
				|| aBthTransAttribute.fNumOfChannels > 256 )
		{
			BthLog.e(TAG, "at least one parameter is invalid");
			throw new Exception("at least one parameter is invalid");
		}

		fAttributes = new CommAttributes();
		fAttributes.fInformHandler = aInformHandler;
		fAttributes.fBtSocket = aBtSocket;
		fAttributes.fInputStream = aBtSocket.getInputStream();
		fAttributes.fOutputStream = aBtSocket.getOutputStream();
		fAttributes.fTransportAttribute = aBthTransAttribute;
		fAttributes.initChannels();

		setRunning(false);
		fIsActiveDisconnection = false;
	}

	@Override
	public synchronized boolean run()
	{
		if ( isRunning() == true )
			return true;
		
		initialize();	
		
		setRunning(true);
		
		return true;
	}
	@Override
	public synchronized boolean postData(byte[] aSendData, int aChannel)
	{		
		if ( isRunning() == false )
			return false;
		// check if data is validate and keeps them
		if ( aSendData == null )
			return true;
		if ( aSendData.length == 0 )
			return false;

		if ( fAttributes.getChannel(aChannel) == null )
		{
			BthLog.e(TAG, "channel out of range");
			return false;
		}

		// make a data packet
		// MUST NOT pack the raw data, because this protocol used
		// BthDataSlicableOutputStream
		// it will call back this protocol to pack raw data
		// send
		return realSend(fBthDataOutputStream,aSendData,aChannel
						,CommPacket.PACKET_DATA);
	}
	@Override
	public synchronized void shutDown()
	{
		if ( isRunning() == false )
			return;
		
		//send a disconnection packet
		processActiveDisconnection();
		
		unInitialize();	
	}
	
	private void initialize()
	{
		fCommSlicable = new CommSlicable();
		// create data output stream
		// with ACK
		BthDataWithACKOutputStream bthWithAck = new BthDataWithACKOutputStream(
				fAttributes.fOutputStream, 3000);
		fSendAckHandler = bthWithAck.getAckEventHandler();
		//fBthDataOutputStreamWithAck = bthWithAck;		

		//without ACK
		fBthDataOutputStreamWithoutAck = bthWithAck.getWithoutACKOutputStream();
		
		// data will be sent repeated
		BthDataRepeatableOutputStream bthRepatable = new BthDataRepeatableOutputStream(
				bthWithAck, 3);
		//fBthDataOutputStreamReptable = bthRepatable;
		
		// data will be sliced
		BthDataSlicableOutputStream bthSlicer = new BthDataSlicableOutputStream(
				bthRepatable, 1024, fCommSlicable);

		// get the ultimate output stream
		// slice ---> repeat ---> send with ACK
		fBthDataOutputStream = bthSlicer;

		// create a packet
		fCommPacket = new CommPacket(new CommCheckSum());
		
		fChannelCurrentSent = null;		
//		fChannelLastRecvData = new CommChannel();
//		fChannelLastRecvData.fCurrentSN = 0x00;
		
		fParser = new PacketParser();
		fParser.fTotalLength = 0;
		
		// start a receiver thread to receive message from outer bluetooth
		// device
		fCommRecvRunner = new CommReceiverRunnable(fAttributes.fInputStream);
		new Thread(fCommRecvRunner,TAG+" : receiver").start();		
		
		//start heart beat policy
		if ( fAttributes.fTransportAttribute.fHeartBeatFrequency == 0 )
		{
			BthLog.e(TAG,"do not use heart beat policy");
			fHeartBeatPolicy = new ProcessorOfNopHeartBeat();
		}
		else
		{
			BthLog.e(TAG,"use heart beat policy");
			fHeartBeatPolicyHandler = new CommHeartBeatPolicyHandler();
			fHeartBeatPolicy = new ProcessorOfHeartBeat(10000,1000,fHeartBeatPolicyHandler);
		}
		
		try
		{
			fHeartBeatPolicy.start();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
	private synchronized void unInitialize()
	{
		if ( isRunning() == false )
			return;
		
		////for test
		//MediaPlayer mp = MediaPlayer.create(fAttributes.fTransportAttribute.fAppContext,R.raw.alarmbeep);//音频文件
        //mp.start();
        
		setRunning(false);
		//stop heart beat policy
		stopHeartBeatPolicy();
		
		//release output stream
		if ( fBthDataOutputStream != null )
		{
			fBthDataOutputStream.releaseResource();
			fBthDataOutputStream = null;
			//fBthDataOutputStreamReptable = null;
			//fBthDataOutputStreamWithAck = null;
			fBthDataOutputStreamWithoutAck = null;
		}
		//stop receive data
		if ( fCommRecvRunner != null )
		{
			fCommRecvRunner.cancel();
			fCommRecvRunner = null;
		}	
		
		//stop socket
		if ( fAttributes.fBtSocket != null )
		{
			try
			{
				fAttributes.fBtSocket.close();
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
			}			
		}
		
		//notify
		fAttributes.fInformHandler.inform(IProtocolInformHandler.E_DISCONNECTION);
	}
	
	private int getHeaderLength()
	{
		return fCommPacket.getHeadlerLength();
	}
	//packet type : data
	//so the fCurrentDataOfChannel is not null
	//used by call back
	private synchronized byte[] pack(byte[] aRawData, byte aSlicerStatus)
	{		
		fChannelCurrentSent.fSliceStatus = aSlicerStatus;
		if ( aRawData == null )
			return fCommPacket.makePacketWithoutData(fChannelCurrentSent);
		else
			return fCommPacket.makePacketWithData(fChannelCurrentSent,aRawData);
	}

	/**
	 * reply message without get a reply ACK
	 * @param aAppId
	 * @param aChannel
	 * @param aSN
	 * @param aType
	 * @return
	 */
	private synchronized boolean realReply(byte aAppId,byte aChannel,byte aSN,byte aType)
	{
		if ( isRunning() == false )
			return false;
		
		fHeartBeatPolicy.pause();
		
		//we don't use slice policy, so this time it
		//will not call back this class to pack data		
		byte[] data = fCommPacket.makeReplyPacket(aAppId,aChannel,aSN,aType);
		boolean result =  fBthDataOutputStreamWithoutAck.send(data);
		
		fHeartBeatPolicy.resume();
		
		if ( result == false )
		{
			BthLog.e(TAG,"reply ERROR !!!!");
		}
		
		return result;
	}
	
	/**
	 * send data
	 * @param aStream the output stream of sending data
	 * @param aRawData raw data
	 * @param aChannel channel index used, it from 0 to 255
	 * @param aPacketType data type, to see {@link CommPacket}
	 * @return true if sent out the data without any error, otherwise false
	 */
	private synchronized boolean realSend(IBthDataOutputStream aStream,byte[] aRawData,int aChannel,byte aPacketType/*,byte aSliceStatus*/)
	{
		if ( isRunning() == false )
			return false;
		
		fHeartBeatPolicy.pause();
		
		fChannelCurrentSent = fAttributes.getChannel(aChannel);
		fChannelCurrentSent.fPacketType = aPacketType;
		//class BthDataSlicableOutputStream will set the field "fSliceStatus"
		//fChannelCurrentSent.fSliceStatus = aSliceStatus;
				
		//class BthDataSlicableOutputStream will call back this class to pack data
		boolean result =  aStream.send(aRawData);
		
		fHeartBeatPolicy.resume();
		
		if ( result == false )
		{
			BthLog.e(TAG,"SEND DATA ERROR !!!!");
			unInitialize();
		}
		
		return result;
	}
	
	private void stopHeartBeatPolicy()
	{
		BthLog.i(TAG,"enter stopHeartBeatPolicy ...");
		if ( fHeartBeatPolicy.isStopped() == false )
		{
			try
			{
				fHeartBeatPolicy.stop();
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}			
		}
		BthLog.i(TAG,"leave stopHeartBeatPolicy ...");
	}
	
	//reply a ACK
	private synchronized boolean replyACK(Packet aPacket)
	{		
		boolean replyOk = false;
		replyOk = realReply(fParser.fPacket.fAppId
				,fParser.fPacket.fChannel
				,fParser.fPacket.fSN,CommPacket.PACKET_ACK);
		if ( replyOk == false )
		{
			BthLog.d(TAG,"reply data ACK failed");	
		}
		else
		{
			BthLog.d(TAG,"reply data ACK OK");		
		}
		
		return replyOk;
	}
	private synchronized void processActiveDisconnection()
	{		
		BthLog.e(TAG,"process Active Disconnection");
		
		fIsActiveDisconnection = true;
		
		realSend(fBthDataOutputStream,null,0
				,CommPacket.PACKET_DISCONNECTION);	
		
		fIsActiveDisconnection = false;
		////update status
		//unInitialize();
	}
	private synchronized void processPassiveDisconnection(Packet aPacket)
	{		
		BthLog.e(TAG,"process Passive Disconnection");
		//reply
		realReply(aPacket.fAppId,aPacket.fChannel,aPacket.fSN,CommPacket.PACKET_DISCONNECTION);		
		//update status
		unInitialize();
	}
	private synchronized void processActiveHeartBeatPolicy()
	{
		fHeartBeatPolicy.pause();
		
		boolean result = realSend(fBthDataOutputStream,null,0,CommPacket.PACKET_HEART_BEAT);
		if ( result == false )
		{
			BthLog.i(TAG,"send heart beat policy error");
			//processActiveDisconnection();
			return;
		}
		
		BthLog.i(TAG,"leave heart beat policy");
		fHeartBeatPolicy.resume();
	}
	private synchronized void processPassiveHeartBeatPolicy(Packet aPacket)	
	{
		//reply a ACK
		replyACK(aPacket);
	}
	
	private boolean isReplyed(CommChannel aChannel,Packet aPacket)
	{
		if ( (aChannel.fAppId == aPacket.fAppId)
				&& (aChannel.fChannelIndex == aPacket.fChannel)
				&& (aChannel.fCurrentSN == aPacket.fSN) )
		{
			return true;			
		}	
		
		return false;
	}
	private int processPacketData(byte[] aReadData,int aDataLength)
	{
		int startPos = 0;
		int headerLength = getHeaderLength();
		while ( aDataLength > 0 )
		{
			if ( fParser.fParsedHeader == false )
			{
				if ( aDataLength < headerLength )
				{
					break;
				}
				if ( fCommPacket.isHeaderValidate(aReadData,startPos) == false )
				{
					//parse next
					startPos += headerLength;
					aDataLength -= headerLength;
					BthLog.e(TAG,"parcket header invalid");
					continue;
				}
				fParser.fParsedHeader = true;
				//dereference
				fParser.fPacket = null;
				//reset the new reference
				fParser.fPacket = fCommPacket.setHeaderInfo(aReadData,startPos);
				
				//to check if only has header
				//get packet type
				fParser.fType = CommPacket.getPacketType(fParser.fPacket); 
				//check if there are application's data
				fParser.fDataLength = (fParser.fPacket.fDatalenHigh << 8) + fParser.fPacket.fDataLenLow; 
				if ( fParser.fDataLength == 0 )//no application's data
				{
					if ( CommPacket.PACKET_ACK == fParser.fType )
					{
						if ( isReplyed(fChannelCurrentSent,fParser.fPacket) )
						{
							BthLog.d(TAG,"get a ACK packet");
							fSendAckHandler.onGetAck();															
						}		
						else
						{
							BthLog.i(TAG,"error: get a repetable ACK packet");
						}
					}
					else if ( CommPacket.PACKET_DISCONNECTION == fParser.fType )
					{		
						if ( fIsActiveDisconnection == true )
						{
							fSendAckHandler.onGetAck();
							BthLog.d(TAG,"acitve disconnection get replyed disconnection packet");
						}
						else
						{
							//process disconnection
							processPassiveDisconnection(fParser.fPacket);	
							BthLog.d(TAG,"get a disconnection packet");
						}						
					}
					else if ( CommPacket.PACKET_HEART_BEAT == fParser.fType )
					{
						processPassiveHeartBeatPolicy(fParser.fPacket);
						BthLog.d(TAG,"get a hear beat packet");
					}
					else if ( CommPacket.PACKET_DATA == fParser.fType )
					{
						BthLog.e(TAG,"get a DATA packet type, but it's length IS ZERO");
					}
					else
					{
						BthLog.e(TAG,"unKnown packet type");
					}					
					//parse next header
					fParser.fParsedHeader = false;
					//parse next
					startPos += headerLength;
					aDataLength -= headerLength;
					continue;
				}
				
				//the packet has data: the 1 is the check sum
				fParser.fTotalLength = fParser.fDataLength + headerLength + 1;
			}					
			
			//process data
			//must be a application's data type
			if ( CommPacket.PACKET_DATA != fParser.fType )
			{
				BthLog.e(TAG,"get a NON-DATA packet type, but it's data length is NOT ZERO");
				//parse next header
				fParser.fParsedHeader = false;
				//parse next
				startPos += fParser.fTotalLength;
				aDataLength -= fParser.fTotalLength;
				continue;
			}
			if ( aDataLength >= fParser.fTotalLength )
			{
				if ( fChannelLastRecvData == null )
				{
					fChannelLastRecvData = new CommChannel();
					fChannelLastRecvData.fCurrentSN = (byte) (fParser.fPacket.fSN - 0x01);
				}
				
				if ( ByteUtil.isEqual(fChannelLastRecvData.fCurrentSN,fParser.fPacket.fSN) == true )
				{
					BthLog.i(TAG,"receive a repeat data packet");
				}
				else
				{
					fChannelLastRecvData.fCurrentSN = fParser.fPacket.fSN;
					//check data sum check
					byte[] recvData = fCommPacket.getAppData(aReadData,startPos+headerLength,fParser.fDataLength);
					if ( recvData == null )
					{
						BthLog.i(TAG,"received data's check sum is WRONG");
					}
					else
					{
						replyACK(fParser.fPacket);
						fAttributes.fInformHandler.onDataReceived(recvData,fParser.fPacket.fChannel);						
						BthLog.d(TAG,"receive a data packet");
					}								
				}				
				
				//parse next header
				fParser.fParsedHeader = false;
				//parse next
				startPos += fParser.fTotalLength;
				aDataLength -= fParser.fTotalLength;												
				continue;
			}
			else
			{
				break;
			}
		}
		
		//parse data
		if ( aDataLength > 0 )
		{
			System.arraycopy(aReadData,startPos,aReadData,0,aDataLength);
		}
		return aDataLength;
	}
	
	private synchronized void processReceiveError(boolean aIsCancelled)
	{
		if ( aIsCancelled == false )//receive error
		{
			//when receiver happens error, it will cause inner exception:
			//"Transport endpoint is not connected"
			//processActiveDisconnection();
			fAttributes.fInformHandler.inform(IProtocolInformHandler.E_RECEIVE_ERROR);
			BthLog.e(TAG,"receiver thread exit with exception");
		}
		else
		{
			BthLog.e(TAG,"receiver thread exit normally");
		}
	}
	private synchronized boolean isRunning()
	{
		return fIsRunning;
	}
	private synchronized void setRunning(boolean aIsRunning)
	{
		fIsRunning = aIsRunning;
	}
	
	private final static String TAG = "bt.BthProCommCommonMode";
	private CommAttributes fAttributes;
	private boolean fIsRunning;
	private boolean fIsActiveDisconnection;
	private CommPacket fCommPacket;
	
	private CommChannel fChannelCurrentSent;
	private CommChannel fChannelLastRecvData;
	
	private IAckEventHandler fSendAckHandler;
	private IBthDataOutputStream fBthDataOutputStreamWithoutAck;
	//private IBthDataOutputStream fBthDataOutputStreamWithAck;
	//private IBthDataOutputStream fBthDataOutputStreamReptable;
	private IBthDataOutputStream fBthDataOutputStream;
	
	private CommHeartBeatPolicyHandler fHeartBeatPolicyHandler;
	private CommSlicable fCommSlicable;
	private CommReceiverRunnable fCommRecvRunner;
	private IProcessorOfHeartBeat fHeartBeatPolicy;
	
	//current parsed packet
	private PacketParser fParser;	
	//inner class
	private class CommHeartBeatPolicyHandler implements IHeartBeatEventHandler
	{
		@Override
		public void inform(int aEventType)
		{
			if ( aEventType == IHeartBeatEventHandler.ETYPE_CAN_SEND_PACKET )
			{
				CommPolicyOfCommonMode.this.processActiveHeartBeatPolicy();
			}
		}		
	}
	/**
	 * a call back handler
	 */
	private class CommSlicable implements IBthDataSlicableProtocol
	{
		@Override
		public byte[] getPacket(byte[] aRawData, byte aSlicerStatus)
		{
			return CommPolicyOfCommonMode.this.pack(aRawData, aSlicerStatus);
		}	
	}
	
	private class CommReceiverRunnable implements Runnable
	{
		CommReceiverRunnable(InputStream aBtStream)
		{
			fCancelled = false;
			fBtSocketInputStream = aBtStream;			
		}

		@Override
		public void run()
		{
			int bufLength = 4096;
			byte[] readBuf = new byte[bufLength];
			int realRead = 0;
			int totalRead = 0;
			
			int headerLength = CommPolicyOfCommonMode.this.getHeaderLength();
			while ( fCancelled == false )
			{
				try
				{
					realRead = fBtSocketInputStream.read(readBuf,totalRead,bufLength-totalRead);
					totalRead += realRead;
					BthLog.d(CommPolicyOfCommonMode.TAG,"length before process : "+totalRead);
					if ( totalRead >= headerLength )
					{
						totalRead = CommPolicyOfCommonMode.this.processPacketData(readBuf,totalRead);
						BthLog.d(CommPolicyOfCommonMode.TAG,"length after process  : "+totalRead);
					}
				}
				catch (Exception exception)
				{
					if ( fCancelled == false )
					{						
						//exception.printStackTrace();
						BthLog.e(TAG,"receiver thread exception",exception);
					}
					CommPolicyOfCommonMode.this.processReceiveError(fCancelled);
					break;//exit the thread
				}
			}
		}
		
		public void cancel()
		{
			fCancelled = true;
		}
		// a sign : when stop the thread, set this to true
		private boolean fCancelled;
		// use the stream to read message
		private InputStream fBtSocketInputStream;
	}
}
