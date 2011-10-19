package com.szxys.mhub.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.szxys.mhub.R;

public class Main extends Activity {
	Button btExitSystem;
	Context appContext = null;
	Activity thisActivity = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btExitSystem = (Button) findViewById(R.id.btExit);
		thisActivity = this;
		appContext = this.getApplicationContext();

		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						thisActivity.getApplicationContext(),
						PlatformGuardService.class);
				thisActivity.getApplicationContext().stopService(intent);

				thisActivity.finish();
			}
		};
		btExitSystem.setOnClickListener(l);

		Window window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// 这句就是设置窗口里崆件的透明度的．０.０全透明．１.０不透明．
		wl.alpha = 1.0f;
		window.setAttributes(wl);

		if (true == PlatformGuardService.m_bInitView) {
			PlatformGuardService.m_bInitView = false;

			appContext.startService(new Intent(appContext,
					PlatformGuardService.class));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("com.szxys.mhub.app.Main",
				"onStart()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("com.szxys.mhub.app.Main",
				"onRestart()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("com.szxys.mhub.app.Main",
				"onResume()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("com.szxys.mhub.app.Main",
				"onPause()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("com.szxys.mhub.app.Main",
				"onStop()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("com.szxys.mhub.app.Main",
				"onDestroy()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Intent intent = new Intent(thisActivity.getApplicationContext(),
				PlatformGuardService.class);
		thisActivity.getApplicationContext().stopService(intent);

	}
}