package com.skyeyes.storemonitor.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skyeyes.base.activity.BaseActivity;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.base.util.StringUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.service.DevicesService;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private TopTitleView topTitleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_settings_page);
		((Button) findViewById(R.id.store_login_bt)).setOnClickListener(this);
		topTitleView = (TopTitleView) findViewById(R.id.setting_topView);

		String userName = PreferenceUtil.getConfigString(
				PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_name);
		String userPsd = PreferenceUtil.getConfigString(
				PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_psd);
		String ip = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG,
				PreferenceUtil.sysconfig_server_ip);
		String port = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG,
				PreferenceUtil.sysconfig_server_port);
		if (!StringUtil.isNull(userName)) {
			((TextView) findViewById(R.id.store_login_name_et))
					.setText(userName);
			((TextView) findViewById(R.id.store_login_psd_et)).setText(userPsd);
			((TextView) findViewById(R.id.store_server_id_et)).setText(ip);
			((TextView) findViewById(R.id.store_server_port_et)).setText(port);
		}
		topTitleView
				.setOnMenuButtonClickListener(new OnClickListenerCallback() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						((HomeActivity) getParent()).backToPreView();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((HomeActivity) getParent()).backToPreView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		PreferenceUtil
				.setSingleConfigInfo(
						PreferenceUtil.ACCOUNT_IFNO,
						PreferenceUtil.account_login_name,
						StringUtil
								.getTextViewValue((TextView) findViewById(R.id.store_login_name_et)));

		PreferenceUtil
				.setSingleConfigInfo(
						PreferenceUtil.ACCOUNT_IFNO,
						PreferenceUtil.account_login_psd,
						StringUtil
								.getTextViewValue((TextView) findViewById(R.id.store_login_psd_et)));

		PreferenceUtil
				.setSingleConfigInfo(
						PreferenceUtil.SYSCONFIG,
						PreferenceUtil.sysconfig_server_ip,
						StringUtil
								.getTextViewValue((TextView) findViewById(R.id.store_server_id_et)));

		PreferenceUtil
				.setSingleConfigInfo(
						PreferenceUtil.SYSCONFIG,
						PreferenceUtil.sysconfig_server_port,
						StringUtil
								.getTextViewValue((TextView) findViewById(R.id.store_server_port_et)));

		Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();

		try {
			DevicesService.getInstance().mQueryDeviceList
					.queryEquitListNoLogin();
		} catch (Exception e) {

		}

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 取消的只是当前Context的Notification
		mNotificationManager.cancel(DevicesService.ERROR_NOTIFICATION_ID);

	}
}
