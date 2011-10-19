package com.szxys.mhub.ui.mets.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.mets.activity.ConfigInfoActivity;
import com.szxys.mhub.ui.mets.activity.DrinkUrineActivity;
import com.szxys.mhub.ui.mets.activity.GetupSleepActivity;
import com.szxys.mhub.ui.mets.activity.UrineRecordActivity;

public class SystemManagementVerifyActivity extends Activity implements OnClickListener{
	private EditText verify_code;
	private Button verify_ok;
	private Button verify_cancel;
	
	private boolean isCodeRight = false;
   
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mets_system_management_verify);
        
        verify_code = (EditText) this.findViewById(R.id.mets_system_management_verification_et);
        verify_ok = (Button) this.findViewById(R.id.mets_system_management_ok);
        verify_cancel = (Button) this.findViewById(R.id.mets_system_management_cancel);
        
        verify_ok.setOnClickListener(this);
        verify_cancel.setOnClickListener(this);
        
    }
    
    /**
     * 事件处理函数
     */
    @Override
    public void onClick(View v){
    	int buttonId = v.getId();
    	Intent intent = null;
		switch (buttonId) {
		case R.id.mets_system_management_ok:
			
			String code_text = verify_code.getText().toString();
			
			//-----对验证码进行验证,成功的话isCodeRight = true ，否则=false--------- 
			
			isCodeRight = true;
			//-----对验证码进行验证---------
			if(isCodeRight == false) {
				Toast.makeText(SystemManagementVerifyActivity.this, "验证码错误，请重新输入", Toast.LENGTH_LONG).show();
			} else {
				intent = new Intent(this, SystemManagementMainActivity.class);
	   			startActivity(intent);
			}
			break;
			
		case R.id.mets_system_management_cancel:
   			finish();
			break;
		default:
			break;
		}
    }
}