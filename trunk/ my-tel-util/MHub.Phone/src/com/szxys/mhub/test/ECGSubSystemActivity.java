package com.szxys.mhub.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.bizmanager.IBusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.TestData;

/**
 * 利用心电采集器测试系统的工具类
 * 
 * @author 黄仕龙
 * 
 */
public class ECGSubSystemActivity extends Activity {
	private Button btClearTV;
	private Button btECGStart;
	private Button btECGStop;
	private TextView tvECGData;

	private String strECGData;
	private final IBusinessManager bizManager = BusinessManager
			.getIBusinessManager();

	final Handler cwjHandler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		@Override
		public void run() {
			updateUI();
		}
	};

	private void updateUI() {
		tvECGData.setText(strECGData);
		// tvECGData.postInvalidate();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testsubsystem);

		btClearTV = (Button) findViewById(R.id.ClearTV);
		btECGStart = (Button) findViewById(R.id.ECGStart);
		btECGStop = (Button) findViewById(R.id.ECGStop);
		tvECGData = (TextView) findViewById(R.id.textViewECGData);

		btECGStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(Platform.PFLOG_INFO, "Start ECG");
				ECGSSCallBack subSysCallBack = new ECGSSCallBack();
				bizManager.startSubSystem(TestData.testZhangSanUserID,
						Platform.SUBBIZ_ECG, subSysCallBack);
			}
		});
		btECGStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(Platform.PFLOG_INFO, "Stop ECG");
				bizManager.stopSubSystem(10, Platform.SUBBIZ_ECG);

			}
		});
	}

	class ECGSSCallBack implements ISubSystemCallBack {

		@Override
		public long onReceived(int mainCmd, int subCmd, byte[] data, int length) {
			try {
				// strECGData = byteToHexString(data);
				strECGData = "onReceived";
				Log.i(Platform.PFLOG_INFO, strECGData);
				cwjHandler.post(mUpdateResults);
			} catch (Exception e) {
				Log.i(Platform.PFLOG_ERROR,
						"ECGSSCallBack::onReceived() error!");
			}
			return 0;
		}

		@Override
		public long onReceived(int mainCmd, int subCmd, Object obj) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer returnValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
			returnValue.append(hex.toUpperCase() + " ");
		}

		return "[" + returnValue.toString() + "]";
	}
}
