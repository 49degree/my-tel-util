package com.yang.android.tel;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyTelServices extends Service {
	private static final String TAG = "MyService";
	MediaPlayer player;
	
	public MyServiceBinder binder = new MyServiceBinder();
	public class MyServiceBinder extends Binder {
        public MyTelServices getServices() {
            return MyTelServices.this; 
        }
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return binder;
	}
	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		player = MediaPlayer.create(this,R.raw.online);//运行例子是，需要替换音乐的名称
		player.setLooping(false); // Set looping

	}
	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		player.stop();
	}
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		player.start();
	}
	
	public void play() {
		Toast.makeText(this, "My music play", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.start();
	}
	
	public void stop() {
		Toast.makeText(this, "My music stop", Toast.LENGTH_LONG).show();
		Log.d(TAG, "play");
		player.stop();
	}
}


