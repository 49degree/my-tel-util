package com.szxys.mhub.base.btdevice;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.szxys.mhub.base.btprotocol.BthLog;

public class BthServerListener
{
	public static BthServerListener getInstance()
	{
		return instance;
	}
	
	public synchronized boolean isStarted()
	{
		return serverSocket != null;
	}
	public synchronized boolean startServer()
	{
		if ( serverSocket != null )
			return true;
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if ( adapter == null )
			return false;
		
		BluetoothServerSocket socket = null;
		try
		{
			socket = adapter.listenUsingRfcommWithServiceRecord(getClass().getName(),UUID.fromString(UUID_STRING));
			serverRunner = new ServerRunner(socket);
			Thread thread = new Thread(serverRunner,TAG);
			thread.start();
			serverSocket = socket;
		}
		catch (Exception exception)
		{
			if ( serverRunner != null )
			{
				serverRunner.stopRuning();
				serverRunner = null;
			}
			exception.printStackTrace();
		}
		
		return serverSocket != null ? true : false;
	}
	public synchronized void stopServer()
	{
		if ( serverRunner != null )
		{
			serverRunner.stopRuning();
		}
		serverRunner = null;
		serverSocket = null;
	}
	
	public void addHandler(String macAddress,IConnectEventHandler handler) throws Exception
	{
		handlerArray.addEventHandler(macAddress,handler);
	}
	public void removeHandler(IConnectEventHandler handler)
	{
		handlerArray.removeEventHandler(handler);
	}
	
	private void clientConnected(BluetoothSocket btSocket)
	{
		handlerArray.inform(btSocket);
	}
	private void serverRunnerExit(boolean aNormalExit)
	{
		if ( aNormalExit )
		{
			BthLog.i(TAG,"exit thread normally");
		}
		else
		{
			BthLog.e(TAG,"exit thread abnormally");	
			
			serverRunner = null;
			if ( serverSocket != null )
			{
				try
				{
					serverSocket.close();
				}
				catch (IOException exception)
				{					
				}
			}
			serverSocket = null;
		}		
	}
	
	//SPP
	private static final String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
	private BluetoothServerSocket serverSocket;
	private static BthServerListener instance = new BthServerListener();
	private ServerRunner serverRunner;
		
	private IConnectEventHandlerArray handlerArray;
	
	private final static String TAG = "bt.BthServerListener";
	
	private BthServerListener()
	{
		handlerArray = new IConnectEventHandlerArray(5);
	}
	
	//inner class
	private class ServerRunner implements Runnable
	{
		private BluetoothServerSocket serverSocket;
		private boolean stopRunning;
		public ServerRunner(BluetoothServerSocket serverSocket) throws Exception
		{
			if ( serverSocket == null )
				throw new NullPointerException("server socket is null pointer");
			
			this.serverSocket = serverSocket;
			stopRunning = false;
		}
		public synchronized void stopRuning()
		{
			if ( stopRunning == false )
			{
				stopRunning = true;
				try
				{
					serverSocket.close();
				}
				catch (IOException exception)
				{
					exception.printStackTrace();
				}
				serverSocket = null;
			}
		}
		@Override
		public void run()
		{
			boolean normalExit = false;
			BluetoothSocket btSocket = null;
			while ( true )
			{				
				try
				{
					btSocket = serverSocket.accept();
					if ( btSocket != null )
					{
						BthServerListener.this.clientConnected(btSocket);
					}
				}
				catch (Exception exception)
				{
					if ( stopRunning == false )
					{
						//abnormal situation
						exception.printStackTrace();
						normalExit = false;
						break;
					}
					else
					{
						//stop
						normalExit = true;
						break;
					}
				}				
			}
			BthServerListener.this.serverRunnerExit(normalExit);
		}		
	}	
}