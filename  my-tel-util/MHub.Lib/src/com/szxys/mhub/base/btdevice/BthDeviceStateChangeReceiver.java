package com.szxys.mhub.base.btdevice;

import com.szxys.mhub.base.btprotocol.BthLog;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class BthDeviceStateChangeReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		
		int value;
		BthLog.e(TAG,"action: "+action);
		if ( action.equals(ADAPTER_STATE_CHANGED) )
		{
			value = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
			BthLog.e(TAG,"new state : "+value);
		}
		else if ( action.equals(BOND_STATE_CHANGED) )
		{
			value = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
			BthLog.e(TAG,"new bond : "+value);
		}
		else
		{
			BthLog.e(TAG,"unknown action: "+action);
		}
	}

	public final static String ADAPTER_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
	public final static String BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";
	
	private final static String TAG = "bt.BthStateChange";
}
