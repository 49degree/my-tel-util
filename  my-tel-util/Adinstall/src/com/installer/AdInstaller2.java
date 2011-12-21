package com.installer;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

import com.z4mod.z4root2.R;


public class AdInstaller2 extends Activity {

	public static final String MODE_ROOT = "MODE_ROOT";
	TextView detailtext;
	private Handler handler= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		handler = new Handler();
		detailtext = (TextView) findViewById(R.id.detailtext);
		//detailtext.setText("正在安装Snake,请稍候11..........");
		saystuff("正在安装Snake,请稍候11..........");;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		handler.post(new Runnable(){
			public void run() {
				dotemproot();
			}
		});
		
		
	}

	WakeLock wl;
	
	
	
	public void dotemproot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();
		Log.i("dotemproot", "Starting");

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("dotemproot", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);


		handler.post(new Runnable(){
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						//saystuff(str);
						Log.i("dotemproot", str);
						if (str.contains("finished checked")) {
							saystuff("完成Snake安装..........");;
							break;
						}
					} catch (Exception ex) {						
					}
				}
				wl.release();
			}
		
		});

		try {
			write(out, "id");
			try {
				AdInstaller.SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				AdInstaller.SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				AdInstaller.SaveIncludedFileIntoFilesFolder(R.raw.snake, "snake.apk", getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			write(out, "chmod 777 " + getFilesDir() + "/busybox");
			write(out, getFilesDir() + "/busybox killall rageagainstthecage");
			write(out, getFilesDir() + "/busybox killall rageagainstthecage");
			write(out, "pm install "+getFilesDir()+"/snake.apk");
			write(out, "checkvar=checked");
			write(out, "echo finished $checkvar");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void write(FileOutputStream out, String command) throws IOException {
		command += "\n";
		out.write(command.getBytes());
		out.flush();
	}
	

	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				detailtext.setText(stuff);
			}
		});
	}

}
