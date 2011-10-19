package com.szxys.mhub.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.szxys.mhub.common.Logcat;
import com.szxys.mhub.ui.main.LoadingActivity;

/**
 * 平台系统守护服务，当程序被关闭后，本服务器会被 Andoird系统自动启动。
 * 
 * @author 黄仕龙
 */
public class PlatformGuardService extends Service {

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public PlatformGuardService getService() {
			return PlatformGuardService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public static boolean m_bInitView = true;

	@Override
	public void onCreate() {
		Logcat.start(this.getApplicationContext());
		Logcat.setLogGrade(Logcat.GRADE_VERBOSE);
		Logcat.i("PlatformGuardService",
				"onCreate()..............................................");

		if (true == m_bInitView) {
			m_bInitView = false;
			Intent intent = new Intent(this.getApplicationContext(),
					LoadingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

			Log.i("PlatformGuardService",
					"onCreate()  startActivity..............................................");
		}
	}

	@Override
	public void onStart(Intent intent, int nInput) {
		super.onStart(intent, nInput);
		Log.i("PlatformGuardService",
				"onStart()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	public void onDestroy() {
		Log.i("PlatformGuardService",
				"onDestroy()..............................................");
		// Intent intent = new Intent(this.getApplicationContext(),
		// PlatRPCService.class);
		// startService(intent)
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	// 启动本服务
	public static void init() {
		if (true == PlatformGuardService.m_bInitView) {
			PlatformGuardService.m_bInitView = false;

			MhubApplication
					.getInstance()
					.getApplicationContext()
					.startService(
							new Intent(MhubApplication.getInstance()
									.getApplicationContext(),
									PlatformGuardService.class));
		}
	}

	// 终止本服务
	public static void release() {
	}
}
