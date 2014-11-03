package com.yangxp.ginput;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.widget.Toast;

public class GameInputManagerService extends Service{
	private InputManager mIm = null;
	private static GameInputManagerService instance = null;
	private int addDeviceId = -1;
	private String mCurrentInputMethodId = null; 
	private final static String GAME_INPUT_METHOD = "com.yangxp.ginput/.GameInputService";
	
	public static GameInputManagerService getInstance(){
		return instance;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		instance = this;
		if(mIm==null){
			mIm = (InputManager)getSystemService(Context.INPUT_SERVICE);
			final Context context = this;
			mIm.registerInputDeviceListener(new InputDeviceListener(){
				@Override
				public void onInputDeviceAdded(int arg0) {
					// TODO Auto-generated method stub
					Log.e(this.getClass().getSimpleName(), "onInputDeviceAdded:"+this.hashCode());
					InputDevice device = InputDevice.getDevice(arg0);
					synchronized (this) {
						if(device != null && (device.getSources()&InputDevice.SOURCE_CLASS_JOYSTICK)!=0 && addDeviceId==-1){
							addDeviceId = arg0;
							ContentResolver resolver = getContentResolver();
					         String currentInputMethodId = Settings.Secure.getString(resolver,Settings.Secure.DEFAULT_INPUT_METHOD);
					         Log.e(this.getClass().getSimpleName(), "currentInputMethodId:"+currentInputMethodId);
					         if(currentInputMethodId==null||!currentInputMethodId.equals(GAME_INPUT_METHOD)){
					        	 	//Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD, GAME_INPUT_METHOD);
									Intent intent =  new Intent(Settings.ACTION_SETTINGS);  
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						            startActivity(intent);
						            mCurrentInputMethodId = currentInputMethodId;
					         }

						}
					}

					Toast.makeText(context, arg0+":onInputDeviceAdded", Toast.LENGTH_SHORT).show();
					/*
		            final int[] devices = InputDevice.getDeviceIds();
		            
		            for (int i = 0; i < devices.length; i++) {
		                InputDevice device = InputDevice.getDevice(devices[i]);
		                Log.i(this.getClass().getSimpleName(), device.toString());
		                if (device != null
		                        && !device.isVirtual()) {
		                    final String inputDeviceDescriptor = device.getDescriptor();
		                    Log.e(this.getClass().getSimpleName(), "ID="+device.getId()+":JOYSTICK:"+(device.getSources()&InputDevice.SOURCE_CLASS_JOYSTICK)+":inputDeviceDescriptor="+inputDeviceDescriptor);
		                }
		            }
		            */
				}

				@Override
				public void onInputDeviceChanged(int arg0) {
					// TODO Auto-generated method stub
					Log.e(this.getClass().getSimpleName(), "onInputDeviceChanged");
					Toast.makeText(context, "onInputDeviceChanged", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onInputDeviceRemoved(int arg0) {
					// TODO Auto-generated method stub
					Log.e(this.getClass().getSimpleName(), "onInputDeviceRemoved:"+this.hashCode());
					
					synchronized (this) {
						if(addDeviceId == arg0){
							 addDeviceId = -1;
							 ContentResolver resolver = getContentResolver();
					         String currentInputMethodId = Settings.Secure.getString(resolver,Settings.Secure.DEFAULT_INPUT_METHOD);
					         Log.e(this.getClass().getSimpleName(), "currentInputMethodId:"+currentInputMethodId+":"+currentInputMethodId.equals(GAME_INPUT_METHOD));
					         if(currentInputMethodId!=null && currentInputMethodId.equals(GAME_INPUT_METHOD)){
									Intent intent =  new Intent(Settings.ACTION_SETTINGS);  
									
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						            startActivity(intent);
						            //Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD, mCurrentInputMethodId);
						            mCurrentInputMethodId = null;
					         }
					         
						}
					}
					 Toast.makeText(context, arg0+":onInputDeviceRemoved", Toast.LENGTH_SHORT).show();
					 
					/*
		            final int[] devices = InputDevice.getDeviceIds();
		            for (int i = 0; i < devices.length; i++) {
		                InputDevice device = InputDevice.getDevice(devices[i]);
		                Log.i(this.getClass().getSimpleName(), device.toString());
		                if (device != null
		                        && !device.isVirtual()) {
		                    final String inputDeviceDescriptor = device.getDescriptor();
		                    Log.e(this.getClass().getSimpleName(), "ID="+device.getId()+":JOYSTICK:"+(device.getSources()&InputDevice.SOURCE_CLASS_JOYSTICK)+":inputDeviceDescriptor="+inputDeviceDescriptor);
		                }
		            }
		            */
				}
				
			},null);
		}
	}
}
