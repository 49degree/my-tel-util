package com.guanri.android.insurance.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.InitBussIdActivity;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.service.InsuOrderOperateService;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;

public class InsuUselessDialog extends Dialog implements
			android.view.View.OnClickListener {
	public static Logger logger = Logger.getLogger(InitBussIdActivity.class);//日志对象

	private Button mOkBtn = null;
	private Button mCancelBtn = null;

	private EditText EdtNo = null;
	private EditText EdtInsuNumber = null;

	private String operaterNo;
	private String operaterName;
	
	private InsuOrderOperateService insuOrderOperateDAO;
	private Context context;
	
	public InsuUselessDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		insuOrderOperateDAO = new InsuOrderOperateService(context);
		
	}
	
	
	public void displayDlg() {
		
		operaterNo = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserId();
		operaterName = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserName();
		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.insu_useless_dialog);// 设置对话框的布局

		mOkBtn = (Button) findViewById(R.id.setting_in);
		mCancelBtn = (Button) findViewById(R.id.setting_out);

		EdtNo = (EditText) findViewById(R.id.edt_no);
		EdtInsuNumber = (EditText) findViewById(R.id.edt_insuno);
		EdtNo.setText("");
		EdtNo.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		show();// 显示对话框
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setting_in) {

			final ProgressDialog btDialog = new ProgressDialog(context);
			btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise)); // title     
			btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_common_server_loading));//进度是否是不确定的，这只和创建进度条有关
			btDialog.show();
			
			String strNo = EdtNo.getText().toString();
			String strInsuNum = EdtInsuNumber.getText().toString();
			if((strNo.length()>0) && (strInsuNum.length()>0)){
				Handler messageHandler = new Handler(){
					public void handleMessage(Message msg) {
						switch(msg.what){
						case 0:
							//正常
							btDialog.dismiss();
							Msgdialog msgdialog = new Msgdialog(context);
							msgdialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));
							msgdialog.setMsgstr(StringUtils.getStringFromValue(R.string.apsai_insu_useless_data_success));
							msgdialog.setImageid(R.drawable.dialog_success);
							msgdialog.displayDlg();
							dismiss();
							break;
						case -1:
							// 失败
							btDialog.dismiss();
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_warning));
							builder.setMessage((String)msg.obj);
							builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),null);
							builder.create().show();
							break;
						default :
							btDialog.setMessage((String)msg.obj);
						}
					}
				};
				String BatchNo =
					SharedPreferencesUtils.getConfigString(
						SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.CHECK_ID);
				if(BatchNo==""){
					BatchNo = "1";
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
							SharedPreferencesUtils.CHECK_ID, "1");
				}
				logger.debug("当前批号"+BatchNo);
				insuOrderOperateDAO.uselessOrder(operaterNo,operaterName,
						strNo,Integer.valueOf(strInsuNum),Integer.valueOf(BatchNo),messageHandler,btDialog);
			}else{
				btDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_warning));
				builder.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_insuno_number_error));
				builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),null);
				builder.create().show();
			}
		} else if (v.getId() == R.id.setting_out) {
			dismiss();
		}
	}

	
}
