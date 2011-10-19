package com.szxys.mhub.ui.virtualui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.bizmanager.IBusinessManager;

/**
 * 虚拟子业务UI..测试时使用
 * 
 * @author 黄仕龙
 * 
 */

public class VirtualSystemActivity extends Activity {
	private final IBusinessManager bizManager = BusinessManager
			.getIBusinessManager();
	private Button btVirtualStart;

	public VirtualSystemActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.virtualsystem);

		btVirtualStart = (Button) findViewById(R.id.virtualStart);

		btVirtualStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// bizManager.startSubSystem(888888, subSystemID,
				// subSysCallBack);
			}
		});

	}
}
