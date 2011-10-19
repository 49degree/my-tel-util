package com.szxys.mhub.base.btdevice;

import java.io.IOException;
import java.util.UUID;

import javax.crypto.NullCipher;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.szxys.mhub.base.btprotocol.AbstractBthDeviceProtocol;
import com.szxys.mhub.base.btprotocol.BthLog;
import com.szxys.mhub.base.btprotocol.IProtocolInformHandler;

/**
 * concrete BthLogic bluetooth device
 * 
 * @author Administrator
 * 
 */
class BluetoothDeviceImpl implements IBluetoothDevice
{
	/**
	 * parameters to see {@link BthDeviceFactory#createBthDevice}
	 */
	public BluetoothDeviceImpl(BthTransportAttribute aBthAttribute,
			IBthDeviceEventHandler aEventHandler) throws Exception
	{
		if ( aBthAttribute == null || aEventHandler == null )
		{
			BthLog.d(TAG, "parameter is null");
			throw new Exception("parameter is null");
		}
		if ( aBthAttribute.fAppContext == null )
		{
			BthLog.d(TAG, "must set Context");
			throw new Exception("must set Context");
		}
		
		BthDeviceInfo btDeviceInfo = new BthDeviceInfo();
		btDeviceInfo.fMacAddress = new String(aBthAttribute.fMacAddress.toUpperCase());
		btDeviceInfo.fPairingCode = new String(aBthAttribute.fPairingCode);
		btDeviceInfo.fProtocolType = aBthAttribute.fProtocolType;
		btDeviceInfo.fPassiveMode = aBthAttribute.fIsPassiveMode;
		btDeviceInfo.fChannelNumbers = aBthAttribute.fNumOfChannels;
		if ( checkParameters(btDeviceInfo) == false )
		{
			btDeviceInfo = null;

			BthLog.d(TAG, "invalide parameters to create IBluetoothDevice");
			throw new Exception("invalide parameters to create IBluetoothDevice");
		}

		// save BthLogical bluetooth device information
		btDeviceInfo.fStatus = BthCommStatus.STATUS_DISCONNECTION;
		fBtDeviceInfo = btDeviceInfo;
		
		fBthManagerEventHandler = aEventHandler;
		
		//save transport attribute
		fTransportAttribute = new BthTransportAttribute();
		fTransportAttribute.set(aBthAttribute);		
//		//MAC address change to upper character
//		fTransportAttribute.fMacAddress.toUpperCase();
		aBthAttribute = null;
		
		fIsSending = false;

//		//listen bluetooth state changes
//		IntentFilter filter = new IntentFilter(BthDeviceStateChangeReceiver.ADAPTER_STATE_CHANGED);
//		fBthStateChangeReceiver = new BthDeviceStateChangeReceiver();
//		fTransportAttribute.fAppContext.registerReceiver(fBthStateChangeReceiver,filter);
//		filter = new IntentFilter(BthDeviceStateChangeReceiver.BOND_STATE_CHANGED);
//		fTransportAttribute.fAppContext.registerReceiver(fBthStateChangeReceiver,filter);
		
	}

//	@Override
//	public boolean setPairingCode(String aPairingCode)
//	{
//		if ( checkPairingCode(aPairingCode) )
//		{
//			fBtDeviceInfo.fPairingCode = aPairingCode;
//			return true;
//		}
//
//		return false;
//	}
	
//	@Override
//	public void setApplicationId(byte aAppId)
//	{
//		fBtDeviceInfo.fAppId = aAppId;
//	}

	@Override
	public synchronized void run()
	{
		BthLog.d(TAG,"try to connect : "+fTransportAttribute.fMacAddress);
		if ( canRun() == false )
		{			
			BthLog.e(TAG,"ran already");
			return;
		}

		// set the status to connecting
		setStatus(BthCommStatus.STATUS_CONNECTING);
		BthLog.e(TAG,"start : STATUS_CONNECTING");

		// check pairing code
		if ( checkPairingCode(fBtDeviceInfo.fPairingCode) == false )
		{
			notifyRunStatus(false);
			// pairing code not set
			BthLog.i(TAG, "pairing code not set");			
			return;
		}

		// distinguish active/passive mode
		if ( fBtDeviceInfo.fPassiveMode == true )// passive mode
		{
			BthLog.d(TAG,"run passive mode");
			runPassiveMode();
		}
		else// active mode
		{
			runActiveMode(null);
		}
	}

	@Override
	public synchronized void shutDown()
	{		
		//fTransportAttribute.fAppContext.unregisterReceiver(fBthStateChangeReceiver);
		//stop running first
		if ( getStatus() != BthCommStatus.STATUS_DISCONNECTION )
		{
//			//test
//			Vibrator vibrator = ( Vibrator )fTransportAttribute.fAppContext.getSystemService (Service.VIBRATOR_SERVICE);
//			if ( vibrator != null )
//			{
//				vibrator.vibrate( new long[]{100,10,100,1000},2); 
//			}		
			
			BthLog.i(TAG,"shut down: first step is to stop running");
			//passive mode
			if ( fBtDeviceInfo.fPassiveMode == true )
			{
				if ( fPassiveModeListener != null )
				{
					fPassiveModeListener.removeEventHandler(fPassiveConnectEventHandler);
					BthLog.i(TAG,"shut down: stop running OK");
				}
			}
		
			setStatus(BthCommStatus.STATUS_DISCONNECTION);
			
			// stop protocol
			if ( fBtProtocol != null )
			{
				fBtProtocol.shutDown();
				fBtProtocol = null;
			}	
			
			//must do this, otherwise will make resources leak
			fTransportAttribute.fAppContext = null;	
		}
		else
		{
			BthLog.e(TAG,"shut down already");
		}
	}

	@Override
	public synchronized boolean postData(byte[] aSendData, int aChannel)
	{
		fIsSending = true;
		boolean sendOk = false;
		if ( aSendData != null && aSendData.length > 0 )
		{
			sendOk = fBtProtocol.postData(aSendData,aChannel);
		}
		fIsSending = false;
		
		return sendOk;
	}

	@Override
	public boolean identical(IBluetoothDevice aBtDevice)
	{
		if ( aBtDevice == null )
			return false;
		
		//same object
		if ( this.hashCode() == aBtDevice.hashCode() )
			return true;
		
		//other situation
		if ( fBtDeviceInfo.fMacAddress.equals(aBtDevice.getMac()) )
		{
			return true;
		}
		
		return false;
	}

	@Override
	public String getMac()
	{
		return fBtDeviceInfo.fMacAddress;
	}

	@Override
	public int getProtocolType()
	{
		return fBtDeviceInfo.fProtocolType;
	}

	@Override
	public boolean getPassiveMode()
	{
		return fBtDeviceInfo.fPassiveMode;
	}

	@Override
	public int getNumOfChannels()
	{
		return fBtDeviceInfo.fChannelNumbers;
	}

	@Override
	public synchronized int getStatus()
	{
		BthLog.e(TAG,"get status :" + Integer.valueOf(fBtDeviceInfo.fStatus));
		return fBtDeviceInfo.fStatus;
	}
	
	private BluetoothSocket createSocket() throws Exception
	{		
		// may throws exception
		UUID uuid = UUID.fromString(UUID_STRING);

		// get adapter
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if ( adapter == null )
		{
			BthLog.e(TAG,"not support Bluetooth device");
			throw new Exception("not support Bluetooth device");
		}
		
		//whether the bluetooth device has been opened, if not open it
		if ( adapter.isEnabled() == false )
		{
			if ( adapter.enable() == false )
			{
				BthLog.e(TAG,"can't enable Bluetooth device");
				throw new Exception("can't enable Bluetooth device");
			}
			
			//wait 10 seconds
			int maxWait = 0;
			while ( (adapter.getState() != BluetoothAdapter.STATE_ON) && (maxWait++ < 100) )
			{
				try
				{
					Thread.sleep(100);
				}
				catch (Exception exception)
				{					
				}				
			}
			if ( adapter.getState() != BluetoothAdapter.STATE_ON )
			{
				throw new Exception("can not open bluetooth device");
			}
			BthLog.i(TAG,"open bluetooth uses "+maxWait*10+" millionseconds");
		}
		
		// get remote device
		BluetoothDevice btDevice = adapter.getRemoteDevice(fBtDeviceInfo.fMacAddress);
		if ( btDevice == null )
		{
			BthLog.e(TAG,"can't get remote device from given MAC : " + fBtDeviceInfo.fMacAddress);
			throw new Exception("can't get remote device from given MAC : " + fBtDeviceInfo.fMacAddress);
		}
		
		//pairing
		if ( makeBonded(btDevice,fBtDeviceInfo.fPairingCode) == false )
		{
			throw new Exception("can't setPin or createBond");
		}				
		// may throws exception
		BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
		//Note: You should always ensure that the device is not performing device discovery when you call connect(). 
		//If discovery is in progress, then the connection attempt will be significantly slowed and is more likely to fail.
		adapter.cancelDiscovery();
		
		return btSocket;
	}
	
	private synchronized void runActiveMode(BluetoothSocket aBtSocket)
	{
		BluetoothSocket btSocket = null;
		if ( aBtSocket == null )
		{//real active mode : create socket
		//fake active mode : outer device send pairing request, so this connect outer device
			try
			{
				btSocket = createSocket();
				btSocket.connect();
			}
			catch (Exception exception)
			{
				notifyRunStatus(false);				
				BthLog.e(TAG,"create socket error: ",exception);
				return;
			}
		}
		else
		{//fake active mode : outer device connected with this
			btSocket = aBtSocket;
		}
		
		//create protocol
		fBtProtocol = createProtocol(btSocket);
		if ( fBtProtocol == null )
		{
			notifyRunStatus(false);
			
			BthLog.e(TAG,"failed to create protocol");			
			return;
		}
		
		//run protocol
		if ( fBtProtocol.run() == false )
		{
			notifyRunStatus(false);
		}
				
		//notify 
		notifyRunStatus(true);
	}
	
	private synchronized void runPassiveMode()
	{
		//start listener
		if ( fPassiveModeListener == null )
		{
			fPassiveModeListener = BthPassiveModeListener.getInstance();
			if ( BthPassiveModeListener.isListening() == false )
			{
				if ( fPassiveModeListener.listen(fTransportAttribute.fAppContext) == false )
				{
					notifyRunStatus(false);
					BthLog.d(TAG,"passive mode lisenter running failed");
					return;
				}
				BthLog.d(TAG,"passive mode lisenter running succeed");
			}
		}
		
		//create and set handler
		if ( fPassiveConnectEventHandler == null )
		{
			fPassiveConnectEventHandler = new BthConnectEventHandler();
		}		
		try
		{
			fPassiveModeListener.addEventHandler(fBtDeviceInfo.fMacAddress,fPassiveConnectEventHandler);
		}
		catch (Exception exception) 
		{//should never goes here except out of memory
			exception.printStackTrace();
		}		
	}
	
	private synchronized void notifyRunStatus(boolean aSuccess)
	{
		if ( aSuccess == false )
		{
			setStatus(BthCommStatus.STATUS_DISCONNECTION);
		}
		else
		{
			setStatus(BthCommStatus.STATUS_COMMUNICATING);
		}
		fBthManagerEventHandler.onRun(aSuccess);		
	}	
	
	/** 
	 * @param aBtSocket can't be null
	 * @return null if failed
	 */
	private AbstractBthDeviceProtocol createProtocol(BluetoothSocket aBtSocket)
	{
		if ( aBtSocket == null )
			return null;
		
		if ( fProtocolInfromHandler == null )
		{
			fProtocolInfromHandler = new BthProtocolInformHandler();
		}	
		
		return AbstractBthDeviceProtocol.createConcreteProtocol(fTransportAttribute,fProtocolInfromHandler,aBtSocket);
	}

	private boolean checkParameters(BthDeviceInfo aBtDeviceInfo)
	{
		// check MAC address is valid
		if ( BluetoothAdapter.checkBluetoothAddress(aBtDeviceInfo.fMacAddress) == false )
			return false;

		// check channels
		if ( aBtDeviceInfo.fChannelNumbers < 1
				|| aBtDeviceInfo.fChannelNumbers > 255 )
			return false;
		
		//check pairing code
		if ( checkPairingCode(aBtDeviceInfo.fPairingCode) == false )
			return false;
		
		//TODO check the send data policy
		
		return true;
	}

	private boolean checkPairingCode(String aPairingCode)
	{
		//it's length can be zero
		if ( (aPairingCode != null) )
			return true;

		return false;
	}

	private synchronized void setStatus(int aStatus)
	{
		fBtDeviceInfo.fStatus = aStatus;
	}

	/**
	 * if it can run, return true. Otherwise return false but DO NOT change the current status
	 * @return true if it can run
	 */
	private synchronized boolean canRun()
	{
		// already connected
		if ( fBtDeviceInfo.fStatus == BthCommStatus.STATUS_COMMUNICATING )
		{
			fBthManagerEventHandler.onRun(false);
			return false;
		}
		// it has been called run() before, but not connected yet
		else if ( fBtDeviceInfo.fStatus == BthCommStatus.STATUS_CONNECTING )
		{
			fBthManagerEventHandler.onRun(false);
			return false;
		}
		// clear error status, reconnect again
		//else if ( fBtDeviceInfo.fStatus == BthCommStatus.STATUS_IN_ERROR )
		{
			// stop protocol
			// it has ran, but disconnected because of some errors
			if ( fBtProtocol != null )
			{
				fBtProtocol.shutDown();
				fBtProtocol = null;
				BthLog.e(TAG,"the buletooth device has ran, but disconnected because of some errors. reconnect again");
			}
		}

		return true;
	}

	private boolean makeBonded(BluetoothDevice aBtDevice,String aPairingCode)
	{
/*		int bondState = aBtDevice.getBondState();
		BthLog.e(TAG,Integer.valueOf(aBtDevice.getBondState()).toString());
		if ( bondState != BluetoothDevice.BOND_BONDED )
		{
			BthUtil.setPin(aBtDevice,aPairingCode);
			BthUtil.createBond(aBtDevice);
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException exception)
			{
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}
		BthUtil.createBond(aBtDevice);*/
		//return true;
//		//BOND_NONE = 10; BOND_BONDING = 11; BOND_BONDED = 12;
//		int bondState = aBtDevice.getBondState();
//		BthLog.e(TAG,Integer.valueOf(aBtDevice.getBondState()).toString());
//		if ( bondState != BluetoothDevice.BOND_BONDED )
//		{
//			if ( bondState != BluetoothDevice.BOND_BONDING )
//			{
//				BthLog.e(TAG,"need create bond");
//				BthUtil.createBond(aBtDevice);
//	//			//wait 10 seconds to let the system change state
//				try
//				{
//					Thread.sleep(10000);
//				}
//				catch (Exception exception)
//				{					
//				}	
//			}
//			else
//			{
//				BthLog.e(TAG,"needn't create bond,the status is BONDING");
//			}
//			BthUtil.setPin(aBtDevice,aPairingCode);
//			try
//			{
//				Thread.sleep(10000);
//			}
//			catch (Exception exception)
//			{					
//			}
//		}
//		else
//		{
//			BthLog.e(TAG,"bonded already");
//		}
		
		//BOND_NONE = 10; BOND_BONDING = 11; BOND_BONDED = 12;
		int bondState = aBtDevice.getBondState();
		BthLog.e(TAG,Integer.valueOf(aBtDevice.getBondState()).toString());
		if ( bondState != BluetoothDevice.BOND_BONDED )
		{
			if ( bondState == BluetoothDevice.BOND_NONE )
			{
				BthLog.e(TAG,"need create bond");
				BthUtil.createBond(aBtDevice);
//				//wait 10 seconds to let the system change state
//				try
//				{
//					Thread.sleep(10*1000);
//				}
//				catch (Exception exception)
//				{					
//				}
			}
			else
			{
				BthLog.e(TAG,"needn't create bond,the status is BONDING");
			}
			
			try
			{
				Thread.sleep(10*1000);
			}
			catch (Exception exception)
			{					
			}
			BthUtil.setPin(aBtDevice,aPairingCode);
			try
			{
				Thread.sleep(10*1000);
			}
			catch (Exception exception)
			{					
			}
		}
		else
		{
			BthLog.e(TAG,"bonded already");
		}
		
		return true;
	}
	/**
	 * SPP
	 */
	private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";

	/**
	 * attributes
	 */
	private BthDeviceInfo fBtDeviceInfo;
	
	private BthTransportAttribute fTransportAttribute;
	
	/**
	 * this notify the observer something happens
	 */
	private IBthDeviceEventHandler fBthManagerEventHandler;
	/**
	 * the protocol notify this something happens
	 */
	private IProtocolInformHandler fProtocolInfromHandler;

	/**
	 * the passive listener notify this that one outer bluetooth device
	 * try to connect or has been connected to this
	 */
	private IConnectEventHandler fPassiveConnectEventHandler;

	/**
	 * the passive listener object
	 */
	private BthPassiveModeListener fPassiveModeListener;

	/**
	 * the protocol object
	 */
	private AbstractBthDeviceProtocol fBtProtocol;

	private final static String TAG = "bt.BluetoothDeviceImpl";
	
	private boolean fIsSending;
	
	//listen bluetooth state changes
	//BroadcastReceiver fBthStateChangeReceiver;
	//inner class
	
	/**
	 * {@link AbstractBthDeviceProtocol} use this class to send information to
	 * {@link IBluetoothDevice}
	 */
	private class BthProtocolInformHandler implements IProtocolInformHandler
	{

		@Override
		public void onDataReceived(byte[] aReceivedData, int aChannel)
		{
			fBthManagerEventHandler.onReceived(aReceivedData,aChannel);
		}

		@Override
		public void inform(int aStatus)
		{
			switch ( aStatus )
			{
				case IProtocolInformHandler.E_RECEIVE_ERROR:
					BluetoothDeviceImpl.this.setStatus(BthCommStatus.STATUS_DISCONNECTION);
					fBthManagerEventHandler.onError(BthCommStatus.RECV_ERR);
					BthLog.e(TAG,"STATUS_DISCONNECTION");
					break;
				case IProtocolInformHandler.E_RUN_OK:
					BluetoothDeviceImpl.this.setStatus(BthCommStatus.STATUS_COMMUNICATING);
					fBthManagerEventHandler.onRun(true);
					BthLog.e(TAG,"STATUS_COMMUNICATING");
					break;
				case IProtocolInformHandler.E_RUN_FAILED:
					BluetoothDeviceImpl.this.setStatus(BthCommStatus.STATUS_DISCONNECTION);
					fBthManagerEventHandler.onRun(false);
					BthLog.e(TAG,"STATUS_DISCONNECTION");
					break;
				case IProtocolInformHandler.E_DISCONNECTION:
					BluetoothDeviceImpl.this.setStatus(BthCommStatus.STATUS_DISCONNECTION);
					if ( BluetoothDeviceImpl.this.fIsSending == false )//not called postData, do not inform caller
						fBthManagerEventHandler.onError(BthCommStatus.CONNECTION_ABORT);
					BthLog.e(TAG,"STATUS_DISCONNECTION");
					break;
				default:
					BthLog.e(TAG,"wrong status");
					break;
			}			
		}
		
	}

	private class BthConnectEventHandler implements IConnectEventHandler
	{
		@Override
		public void onConnect(BluetoothSocket aSocket)
		{
			//remove from the listener
			fPassiveModeListener.removeEventHandler(fPassiveConnectEventHandler);
			
			BthLog.i(BluetoothDeviceImpl.TAG,"passive mode get a socket, address: "
						+ aSocket.getRemoteDevice().getAddress());
			//outer device has connected this already
			BluetoothDeviceImpl.this.runActiveMode(aSocket);
		}
	
		@Override
		public void onConnect(BluetoothDevice aOuterBtDevice)
		{			
			BthLog.i(BluetoothDeviceImpl.TAG,"passive mode get a address direct: "+aOuterBtDevice.getAddress());
			
			//only make bond, after bonded the outer bluetooth device will require connect to this again next time,
			//but next time will get a BluetoothSocket object
			BluetoothDeviceImpl.this.makeBonded(aOuterBtDevice,fTransportAttribute.fPairingCode);
		}
//		@Override
//		public void onConnect(BluetoothSocket aSocket)
//		{
//			//remove from the listener
//			//fPassiveModeListener.removeEventHandler(fPassiveConnectEventHandler);
//			
//			BthLog.i(BluetoothDeviceImpl.TAG,"passive mode get a socket, address: "
//						+ aSocket.getRemoteDevice().getAddress());
//			//outer device has connected this already
//			//BluetoothDeviceImpl.this.runActiveMode(aSocket);
//			
////			try
////			{
////				aSocket.close();
////			}
////			catch (IOException exception)
////			{
////				BthLog.e(TAG,"onConnect",exception);
////			}
////			
//			BluetoothDeviceImpl.this.runActiveMode(aSocket);
//	/*		
//			try
//			{
//				BluetoothDevice btDevice = aSocket.getRemoteDevice();
//				//pairing
//				if ( makeBonded(btDevice,fBtDeviceInfo.fPairingCode) == false )
//				{
//					throw new Exception("can't setPin or createBond");
//				}				
//				UUID uuid = UUID.fromString(UUID_STRING);
//				// may throws exception
//				BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
//				//Note: You should always ensure that the device is not performing device discovery when you call connect(). 
//				//If discovery is in progress, then the connection attempt will be significantly slowed and is more likely to fail.
//				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
//				
//				runActiveMode(btSocket);
//			}
//			catch (Exception exception)
//			{
//				BthLog.e(TAG,"passive mode error",exception);
//			}
//			*/
//		}
//	
//		@Override
//		public void onConnect(BluetoothDevice aOuterBtDevice)
//		{			
//			BthLog.i(BluetoothDeviceImpl.TAG,"passive mode get a address direct: "+aOuterBtDevice.getAddress());
//			
//			//only make bond, after bonded the outer bluetooth device will require connect to this again next time,
//			//but next time will get a BluetoothSocket object
//			//BluetoothDeviceImpl.this.makeBonded(aOuterBtDevice,fTransportAttribute.fPairingCode);
//			BluetoothDeviceImpl.this.runActiveMode(null);
//		}
	}

}


