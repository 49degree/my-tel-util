package com.guanri.android.dilog;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.common.SharedPreferencesUtils;
import com.guanri.android.jpos.constant.JposConstant;
import com.guanri.android.lib.utils.DialogUtils;
import com.guanri.android.lib.utils.TypeConversion;
import com.ihandy.xgx.R;

public class CheckPSWDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button mOkBtn = null;
	private Button mCancelBtn = null;
	
	
	private EditText EdtUserOldPWD = null;
	
	private Context context;
	
	public CheckPSWDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.jpos_user_check_pwd_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		
		EdtUserOldPWD = (EditText)findViewById(R.id.edt_useroldpwd);
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setting_in) {
			String strUserPWD = EdtUserOldPWD.getText().toString();
			String savePwd = SharedPreferencesUtils.getConfigString(
					SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.POS_PWD);
			//判断密码是否正确
			if(strUserPWD!=null&&(TypeConversion.byte2hex(strUserPWD.getBytes()).equals(savePwd)
					||strUserPWD.equals(JposConstant.SUPER_PWD))){//JposConstant.SUPER_PWD为超级密码
				new SystemSettingDialog(context).displayDlg();//打开修改服务器信息对话框
				dismiss();
			}else{
				DialogUtils.showMessageAlertDlg(context, "提示", "密码错误，请重新输入",null);
			}
			
		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}
	
}
