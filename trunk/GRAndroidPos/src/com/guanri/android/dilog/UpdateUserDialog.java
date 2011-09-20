package com.guanri.android.dilog;

import java.io.UnsupportedEncodingException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.R;
import com.guanri.android.jpos.common.SharedPreferencesUtils;
import com.guanri.android.lib.utils.DialogUtils;
import com.guanri.android.lib.utils.TypeConversion;

public class UpdateUserDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button mOkBtn = null;
	private Button mCancelBtn = null;
	
	
	private EditText EdtUserOldPWD = null;
	private EditText EdtUserNewPWD = null;
	private EditText EdtUserNewPWD2 = null;
	
	private Context context;
	
	public UpdateUserDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.user_manager_update_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		
		EdtUserOldPWD = (EditText)findViewById(R.id.edt_useroldpwd);
		EdtUserNewPWD =(EditText)findViewById(R.id.edt_usernewpwd);
		EdtUserNewPWD2 = (EditText) findViewById(R.id.edt_usernewpwd2);
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setting_in) {
			String strUserOldPWD = EdtUserOldPWD.getText().toString();
			String strUserNewPWD = EdtUserNewPWD.getText().toString();
			String strUserNewPWD2 = EdtUserNewPWD2.getText().toString();
			String savePwd = SharedPreferencesUtils.getConfigString(
					SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.POS_PWD);
			//判断2次新密码是否相同
			if(strUserOldPWD==null||strUserNewPWD==null||strUserNewPWD2==null){
				DialogUtils.showMessageAlertDlg(context, "提示", "新旧密码不能为空", null);
			}else if(!strUserNewPWD.equals(strUserNewPWD2)){
				DialogUtils.showMessageAlertDlg(context, "提示", "2次输入的新密码不一致", null);
			}else if(!TypeConversion.byte2hex(strUserOldPWD.getBytes()).equals(savePwd)){//比较输入的旧密码是否正确
				DialogUtils.showMessageAlertDlg(context, "提示", "输入旧密码不正确", null);
			}else{//修改密码
				SharedPreferencesUtils.setConfigString(
						SharedPreferencesUtils.COMFIG_INFO, 
						SharedPreferencesUtils.POS_PWD,
						TypeConversion.byte2hex(strUserNewPWD.getBytes()));
				DialogUtils.showMessageAlertDlg(context, "提示", "密码修改成功", null);
			}
			
			

		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}
	
}