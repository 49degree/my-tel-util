package com.guanri.android.insurance.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.activity.dialog.AddUserDialog;
import com.guanri.android.insurance.activity.dialog.DelUserDialog;
import com.guanri.android.insurance.activity.dialog.UpdateUserDialog;
import com.guanri.android.insurance.service.UserManagerService;
import com.guanri.android.lib.utils.StringUtils;


/**
 * 用户管理界面
 * @author wuxiang
 *
 */
public class UserManagerActivity extends ApsaiActivity{

	private UserManagerService userManagerDAO = null;
	
	private ImageButton addUserBtn = null;
	private ImageButton delUserBtn = null;
	private ImageButton updateUserBtn = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitle(StringUtils.getStringFromValue(R.string.apsai_user_manager));
		setContentView(R.layout.user_info_manager);
		
		userManagerDAO = new UserManagerService();
		
		addUserBtn = (ImageButton) findViewById(R.id.imgbtn_adduser);
		delUserBtn = (ImageButton) findViewById(R.id.imgbtn_deluser);
		updateUserBtn = (ImageButton) findViewById(R.id.imgbtn_updateuser);

		addUserBtn.setOnClickListener(this);
		delUserBtn.setOnClickListener(this);
		updateUserBtn.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgbtn_adduser:
			new AddUserDialog(UserManagerActivity.this,userManagerDAO).displayDlg();
			break;
		case R.id.imgbtn_deluser:
			new DelUserDialog(UserManagerActivity.this,userManagerDAO).displayDlg();
			break;
		case R.id.imgbtn_updateuser:
			new UpdateUserDialog(UserManagerActivity.this,userManagerDAO).displayDlg();
			break;
		default:
			break;
		}
	}
	

}
