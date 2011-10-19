package com.szxys.mhub.ui.mets.components;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.mets.main.MetsMainActivity;

public class PromptMessageActivity extends Activity  implements OnClickListener{

	private TextView content;
	private Button okButton;
	private Button sureButton;
	private Button cancelButton;
	
//	private int promptMessage_flag = 0; 
	public static final int PROMPT_MESSAGE_GETUP = 1;
	public static final int PROMPT_MESSAGE_SLEEP = 2;
	public static final int PROMPT_MESSAGE_URINE = 3;
	public static final int PROMPT_MESSAGE_GATHER_NOW = 4;
	public static final int PROMPT_MESSAGE_SEND_NOW = 5;    	//单按钮
	public static final int PROMPT_MESSAGE_QUESTIONNAIRE = 6;   //单按钮
	public static final int PROMPT_MESSAGE_QUESTIONNAIRE_SAVE = 8;   //
	public static final int PROMPT_MESSAGE_CANCEL_LOGIN = 7;
	
	public static final String  PROMPT_MESSAGE_TYPE = "promptMessageType";
	
	private Intent ok_intent = null;  //点击确定按钮后的动作
	private Intent cancel_intent = null; //点击取消按钮后的动作，一般直接finish
	
	private boolean isResult = false;  //是否是带返回参数的Activity
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.mets_promptmessage);
//		
//		content = (TextView) this.findViewById(R.id.mets_promptmessage_content);
//		okButton = (Button) this.findViewById(R.id.mets_promptmessage_ok);
//		sureButton = (Button) this.findViewById(R.id.mets_promptmessage_sure);
//		cancelButton = (Button) this.findViewById(R.id.mets_promptmessage_cancel);
//		
//		okButton.setOnClickListener(this);
//		cancelButton.setOnClickListener(this);
		
		init();
		
	}
	private void init() {
		
		int promptMessageType = getIntent().getExtras().getInt(PROMPT_MESSAGE_TYPE);
		
		switch(promptMessageType) {
		case PROMPT_MESSAGE_GETUP: //输入起床时间的提示框
			content.setText(getResources().getString(R.string.mets_promptmessage_getup_content));
			ok_intent = new Intent(PromptMessageActivity.this, MetsMainActivity.class);
			
			break;
			
		case PROMPT_MESSAGE_SLEEP: //输入睡觉时间的提示框
			content.setText(getResources().getString(R.string.mets_promptmessage_sleep_content));
			ok_intent = new Intent(PromptMessageActivity.this, MetsMainActivity.class);
			
			break;
			
		case PROMPT_MESSAGE_URINE: //输入尿量的提示框
			content.setText(getResources().getString(R.string.mets_promptmessage_urine_content));
			//启动尿量输入的Activity
//			ok_ok_intent = new Intent(PromptMessageActivity.this, Main.class);
			
			break; 
			
		case PROMPT_MESSAGE_GATHER_NOW: //确认是否打开采集器提示框
			content.setText(getResources().getString(R.string.mets_promptmessage_gatherNow_content));
			
			break;
			
		case PROMPT_MESSAGE_SEND_NOW: //发送数据
			content.setText(getResources().getString(R.string.mets_promptmessage_sendNow_content));
			
			okButton.setVisibility(View.INVISIBLE);
			cancelButton.setVisibility(View.INVISIBLE);
			sureButton.setVisibility(View.VISIBLE);
			
			break;
			
		case PROMPT_MESSAGE_QUESTIONNAIRE: //没有可用的问卷调查 
			content.setText(getResources().getString(R.string.mets_promptmessage_questionnaire_content));
			okButton.setVisibility(View.INVISIBLE);
			cancelButton.setVisibility(View.INVISIBLE);
			sureButton.setVisibility(View.VISIBLE);
			
			break;
		case PROMPT_MESSAGE_QUESTIONNAIRE_SAVE: //是否保存问卷调查
			content.setText(getResources().getString(R.string.mets_promptmessage_questionnaire_save_content));
			okButton.setVisibility(View.VISIBLE);
			cancelButton.setVisibility(View.VISIBLE);
			sureButton.setVisibility(View.INVISIBLE);
			
			ok_intent = new Intent();
			isResult = true;
//			ok_intent.putExtra("isSave", true);
			
			
			break;
			
		case PROMPT_MESSAGE_CANCEL_LOGIN: //注销登录
			content.setText(getResources().getString(R.string.mets_promptmessage_cancel_login_content));
			ok_intent = new Intent(PromptMessageActivity.this, MetsMainActivity.class);
			
			
			break;
			
		}
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
//		case R.id.mets_promptmessage_ok:
//			if(ok_intent == null) {
//				finish();
//			} else {
//				if(isResult == true) {
//					System.out.println("isResult =" + isResult);
//					setResult(RESULT_OK, ok_intent);
//					finish();
//				}else {
//					startActivity(ok_intent);
//				}
//			}
//			
//			break;
//			
//		case R.id.mets_promptmessage_cancel:
//			if(cancel_intent == null) {
//				System.out.println("cancel_intent == null");
//				this.finish();
//			} else {
//				startActivity(cancel_intent);
//			}
//			
//			break;
//			
//		default:
//			break;
		}
		
		
		
	}

	
}
