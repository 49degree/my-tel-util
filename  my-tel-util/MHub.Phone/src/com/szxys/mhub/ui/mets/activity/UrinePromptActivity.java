package com.szxys.mhub.ui.mets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.ui.base.MHubActivity;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class UrinePromptActivity  extends MHubActivity implements OnClickListener{
	public final static String PARAM_BUSSNESS_ID= "param_bussness_id";//表示排尿记录删除提示信息
	public final static int URINE_RECORD_DELETE = 1;//表示排尿记录删除提示信息
	public final static int MODIFY_GETUP_TIME = 2;//表示修改起床时间
	private Bundle extras= null;//获取入参
	private int bussnessId = -1;//功能ID
	//mets_urine_record_operate_modify
	Button record_sure_btn = null;
	Button record_cannel_btn = null;
	TextView prompt_title_text = null;//标题
	TextView prompt_message_text = null;//提示信息
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mets_urine_prompt);
		record_sure_btn = (Button)this.findViewById(R.id.mets_urine_record_sure_btn);
		record_cannel_btn = (Button)this.findViewById(R.id.mets_urine_record_cannel_btn);
		
		prompt_title_text = (TextView)this.findViewById(R.id.mets_urine_prompt_title_text);//标题
		prompt_message_text = (TextView)this.findViewById(R.id.mets_urine_prompt_message_text);//提示信息
		
		record_sure_btn.setSelected(false);
		record_cannel_btn.setSelected(true);
		record_sure_btn.setOnClickListener(this);
		record_cannel_btn.setOnClickListener(this);
		
		extras=getIntent().getExtras(); //获取打开参数
		if(extras==null)
			return ;
		initInfo();
		
	}
	
	private void initInfo(){
		bussnessId = extras.getInt(UrinePromptActivity.PARAM_BUSSNESS_ID);
		switch(bussnessId){
		case URINE_RECORD_DELETE://设置删除排尿记录确认相关信息
			prompt_title_text.setText(R.string.mets_delete_btn_text);
			prompt_message_text.setText(R.string.mets_urine_record_delete_text);
			break;
		default :
			break;
		}
		
	}
	
	@Override
	public void onRestart() {
		super.onRestart();

	}
		
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_record_sure_btn://确定
			record_sure_btn.setSelected(true);
			record_cannel_btn.setSelected(false);
			switch(bussnessId){
			case URINE_RECORD_DELETE://确认删除排尿记录
				//以下为删除排尿记录代码
				int urineRecordId = Integer.parseInt(extras.getString(UrineRecordCountActivity.PARAM_RECORD_ID));//获取排尿记录ID
				if (urineRecordId>0) {
					int row=DrinkAndUrine.deleteByCid(this, urineRecordId);
				}
				finish();
				//以下为返回信息到打开的Activity
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				intent.putExtras(bundle);
//				setResult(RESULT_OK, intent);
				break;
				
			default :
				break;
			}
			
			break;
		case R.id.mets_urine_record_cannel_btn://取消
			record_sure_btn.setSelected(false);
			record_cannel_btn.setSelected(true);
			finish();
			break;			
		default :
			break;
		}
	}
	
	/**获取当前页面名称(仅用于测试）*/
	public String getSysName(){
		return this.getString(R.string.mets_app_name);
	}

	
}
