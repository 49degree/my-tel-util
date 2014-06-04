package com.skyeyes.base.activity;

import com.skyeyes.base.util.ViewUtils;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class BaseActivity extends Activity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getSimpleName(), "onKeyDown.........");

		return false;
	}
}
