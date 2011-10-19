package com.guanri.android.insurance.activity.dialog;

import java.io.UnsupportedEncodingException;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.service.UserManagerService;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

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

public class UpdateUserDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button mOkBtn = null;
	private Button mCancelBtn = null;
	
	private EditText EdtUserNo = null;
	private EditText EdtUserOldPWD = null;
	private EditText EdtUserNewPWD = null;
	

	private UserManagerService userManagerDAO;
	private Context context;
	
	public UpdateUserDialog(Context context,UserManagerService userManagerDAO) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.userManagerDAO = userManagerDAO;
	}
	
	public void displayDlg() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.user_manager_update_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		EdtUserNo = (EditText) findViewById(R.id.edt_userno);
		EdtUserOldPWD = (EditText)findViewById(R.id.edt_useroldpwd);
		EdtUserNewPWD =(EditText)findViewById(R.id.edt_usernewpwd);
		
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setting_in) {

			String strUserNo = EdtUserNo.getText().toString();
			try {	
				byte[]  byteUserNo = TypeConversion.stringToAscii(strUserNo);
				if(byteUserNo.length>10){
					strUserNo = new String(byteUserNo,0,6,"GBK");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strUserOldPWD = EdtUserOldPWD.getText().toString();
			String strUserNewPWD = EdtUserNewPWD.getText().toString();
			
			final ProgressDialog btDialog = new ProgressDialog(context);
			btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise)); // title     
			btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_user_manager_loading));//进度是否是不确定的，这只和创建进度条有关
			btDialog.show();
			
			Handler messageHandler = new Handler(){
				public void handleMessage(Message msg) {
					switch(msg.what){
					case 0:
						// 保存成功
						Msgdialog msgdialog = new Msgdialog(context);
						msgdialog.setTitle(R.string.apsai_common_advise);
						msgdialog.setImageid(R.drawable.dialog_success);
						msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_insu_manager_data_success));
						msgdialog.displayDlg();
						
						EdtUserNo.setText("");
						EdtUserOldPWD.setText("");
						EdtUserNewPWD.setText("");
						break;
					case -1:
						btDialog.dismiss();
						// 失败
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_warning));
						builder.setMessage((String)msg.obj);
						builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										// dosome thing
									}
						});
						builder.create().show();
						break;
					default :
						btDialog.setMessage((String)msg.obj);
					}
				}
			};
			userManagerDAO.updateUser(strUserNo, strUserOldPWD,strUserNewPWD,messageHandler);	
			

		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}
	
}
