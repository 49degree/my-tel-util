/*
 * @(#)UserChooseActivity.java	1.00 11/05/06
 *
 * Copyright (c) 2011-2013  New Element Inc. 
 * 9/10f, Building 2, Financial Base, No.6 Keyuan Road, 
 * Nanshan District, Shenzhen 518057
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * New Element Medical Equipment Technology Development CO., Ltd 
 * ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with New Element.
 */
package com.szxys.mhub.ui.main;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.LightUser;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.VirtualSubSystem;

/**
 * 选择用户页面（目前已经集成到主页面）
 * 
 * @author xak
 * @version 1.0
 */
public class UserChooseActivity extends Activity {

	private List<LightUser> mUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_user_choose);
		ListView lstUser = (ListView) findViewById(R.id.lv_user);
		getUserNames();
		int userNum = mUsers.size();
		String[] userName = new String[userNum];
		if (userNum == 1) {
			startMainActivity(0);
		} else {
			for (int i = 0; i < userNum; i++) {
				userName[i] = mUsers.get(i).Name;
			}
			lstUser.setAdapter(new ArrayAdapter<String>(this,
					R.layout.pfphone_user_list_item, userName));
			lstUser.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					startMainActivity(position);
				}
			});
		}

	}

	/**
	 * 获取系统到已存在的用户 modify by hsl
	 * 
	 * @return 返回系统中已存在的用户
	 */
	private void getUserNames() {
		// 实际使用的代码
		Object[] obj = new Object[1];
		BusinessManager.getIBusinessManager().control(
				VirtualSubSystem.VSS_USERID, Platform.SUBBIZ_VIRTUAL,
				VirtualSubSystem.CTRL_GetLightUserS, null, obj);
		if (null != obj[0]) {
			List<LightUser> list = (List<LightUser>) obj[0];
			mUsers = list;
		}

		// 测试使用的代码
		// mUsers = new ArrayList<LightUser>();
		// LightUser a = new LightUser(1, "张三", "testId", null);
		// mUsers.add(a);
		// a = new LightUser(2, "李四", "testId", null);
		// mUsers.add(a);
		// a = new LightUser(3, "王五", "testId", null);
		// mUsers.add(a);

	}

	/*
	 * 设置当前选择的用户名 add by hsl
	 */
	private void setCurrentUserID(int userID) {
		Object[] obj = new Object[1];
		obj[0] = userID;
		BusinessManager.getIBusinessManager().control(0,
				Platform.SUBBIZ_VIRTUAL, VirtualSubSystem.CTRL_SetRunningUser,
				obj, null);
	}

	/**
	 * 启动主页面
	 * 
	 * @param position
	 */
	private void startMainActivity(int position) {
		Intent intent = new Intent();
		intent.setClass(this, MHubMainActivity.class);
		intent.putExtra("userid", mUsers.get(position).ID);
		intent.putExtra("username", mUsers.get(position).Name);

		startActivity(intent);
		this.finish();
	}
}
