package com.z4mod.z4root2;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

public class Phase2 extends Activity {
	Handler handler = null;
	TextView detailtext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.main);
		detailtext = (TextView) findViewById(R.id.detailtext);
		saystuff("正在安装Snake,请稍候..........");
		Log.i("Phase2", "onCreate");
		
		SharedPreferences preferences = Phase2.this.getSharedPreferences(Phase1.CONFIG_XML, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Phase1.CAN_ROOT, "true");
        editor.commit();
		
		dotemproot();
		//dopermroot();
		
	}
	
	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				detailtext.setText(stuff);
			}
		});
	}

	OutputStream out = null;
	InputStream in = null;
	public void dotemproot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();
		Log.i("Phase2", "Starting");

		final int[] processId = new int[1];
//		final FileDescriptor fd = Exec.createSubprocess("/data/data/com.z4mod.z4root2/files/sh", "-", null, processId);
//		Log.i("Phase2", "Got processid: " + processId[0]);
		Process localProcess = null;
		try{
			localProcess = Runtime.getRuntime().exec("sh");

		    out = localProcess.getOutputStream();
			in = localProcess.getInputStream();
		}catch(Exception e){
			e.printStackTrace();
		}


		
		new Thread(){
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						//saystuff(str);
						Log.i("Phase2", str);
						if (str.contains("finished checked")) {

							saystuff("完成Snake安装..........");;
							break;
						}
					} catch (Exception ex) {						
					}
				}
				wl.release();
			}
		}.start();


		try {
			write(out, "id");
			try {
				Phase1.SaveIncludedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				Phase1.SaveIncludedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				Phase1.SaveIncludedFileIntoFilesFolder(R.raw.snake, "snake.apk", getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			write(out, "chmod 777 " + getFilesDir() + "/busybox");
			write(out, getFilesDir() + "/busybox killall gingerbreak");
			write(out, getFilesDir() + "/busybox killall gingerbreak");
			write(out, getFilesDir() + "/busybox rm "+getFilesDir()+"/temproot.ext");
			write(out, getFilesDir() + "/busybox rm -rf "+getFilesDir()+"/bin");
			write(out, getFilesDir() + "/busybox cp -rp /system/bin "+getFilesDir());
			write(out, getFilesDir() + "/busybox dd if=/dev/zero of="+getFilesDir()+"/temproot.ext bs=1M count=15");
			write(out, getFilesDir() + "/busybox mknod /dev/loop9 b 7 9");
			write(out, getFilesDir() + "/busybox losetup /dev/loop9 "+getFilesDir()+"/temproot.ext");
			write(out, getFilesDir() + "/busybox mkfs.ext2 /dev/loop9");
			write(out, getFilesDir() + "/busybox mount -t ext2 /dev/loop9 /system/bin");
			write(out, getFilesDir() + "/busybox cp -rp "+getFilesDir()+"/bin/* /system/bin/");
			write(out, getFilesDir() + "/busybox cp "+getFilesDir()+"/su /system/bin");
			write(out, getFilesDir() + "/busybox cp "+getFilesDir()+"/busybox /system/bin");
			write(out, getFilesDir() + "/busybox chown 0 /system/bin/su");
			write(out, getFilesDir() + "/busybox chown 0 /system/bin/busybox");
			write(out, getFilesDir() + "/busybox chmod 4755 /system/bin/su");
			write(out, getFilesDir() + "/busybox chmod 755 /system/bin/busybox");
			write(out, "pm install "+getFilesDir()+"/snake.apk");
			write(out, "checkvar=checked");
			write(out, "echo finished $checkvar");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	public void dopermroot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();
		Log.i("dopermroot", "Starting");

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("dopermroot", "Got processid: " + processId[0]);

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
						Log.i("dopermroot", str);
					} catch (Exception ex) {
						
					}
				}
				wl.release();
			}
		});

		try {
			String command = "id\n";
			out.write(command.getBytes());
			out.flush();
			try {
				Phase1.SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				Phase1.SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				Phase1.SaveIncludedFileIntoFilesFolder(R.raw.snake, "snake.apk", getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			command = "chmod 777 " + getFilesDir() + "/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox mount -o remount,rw /system\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp "+getFilesDir()+"/su /system/bin/\n";
			out.write(command.getBytes());
			out.flush();
			
//			command = getFilesDir() + "/busybox cp "+getFilesDir()+"/SuperUser.apk /system/app\n";
//			out.write(command.getBytes());
//			out.flush();
			
			command = getFilesDir() + "/busybox cp "+getFilesDir()+"/busybox /system/bin/\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/busybox\nchmod 755 /system/bin/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox chmod 6755 /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();
			
//			command = "chown root.root /system/app/SuperUser.apk\nchmod 755 /system/app/SuperUser.apk\n";
//			out.write(command.getBytes());
//			out.flush();
			
			write(out, "pm install "+getFilesDir()+"/snake.apk");
			write(out, "checkvar=checked");
			write(out, "echo finished $checkvar");
			
//			command = "rm "+getFilesDir()+"/busybox\n";
//			out.write(command.getBytes());
//			out.flush();
//			command = "rm "+getFilesDir()+"/su\n";
//			out.write(command.getBytes());
//			out.flush();
//			command = "rm "+getFilesDir()+"/SuperUser.apk\n";
//			out.write(command.getBytes());
//			out.flush();
//			command = "rm "+getFilesDir()+"/rageagainstthecage\n";
//			out.write(command.getBytes());
//			out.flush();
//			command = "echo \"reboot now!\"\n";
//			saystuff("Rebooting...");
//			out.write(command.getBytes());
//			out.flush();		
//			Thread.sleep(3000);
//			command = "sync\nsync\n";
//			out.write(command.getBytes());
//			out.flush();
//			command = "reboot\n";
//			out.write(command.getBytes());
//			out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	
	public void write(OutputStream out, String command) throws IOException {
		command += "\n";
		out.write(command.getBytes());
		out.flush();
	}
}
