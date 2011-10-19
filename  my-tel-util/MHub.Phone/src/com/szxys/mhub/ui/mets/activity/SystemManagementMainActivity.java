package com.szxys.mhub.ui.mets.activity;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.szxys.mhub.R;
import com.szxys.mhub.ui.mets.activity.ConfigInfoActivity;
import com.szxys.mhub.ui.mets.activity.DrinkUrineActivity;
import com.szxys.mhub.ui.mets.activity.GetupSleepActivity;
import com.szxys.mhub.ui.mets.activity.UrineRecordActivity;
import com.szxys.mhub.ui.mets.components.PromptMessageActivity;

public class SystemManagementMainActivity extends Activity implements OnClickListener{
	
	private Button gatherNow;
	private Button sendNow;
	private Button cancelLogin;
   
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mets_system_management_main);
        
        gatherNow = (Button) this.findViewById(R.id.mets_gatherNow_button);
        sendNow = (Button) this.findViewById(R.id.mets_sendNow_button);
        cancelLogin = (Button) this.findViewById(R.id.mets_cancelLogin_button);
        
        gatherNow.setOnClickListener(this);
        sendNow.setOnClickListener(this);
        cancelLogin.setOnClickListener(this);
        
    }
    
    /**
     * 事件处理函数
     */
    @Override
    public void onClick(View v){
    	int buttonId = v.getId();
    	Intent intent = null;
		switch (buttonId) {
		case R.id.mets_gatherNow_button:
//   			intent = new Intent(this, GatherNowActivity.class);
//   			startActivity(intent);
   			intent = new Intent(this, PromptMessageActivity.class);
   			intent.putExtra(PromptMessageActivity.PROMPT_MESSAGE_TYPE, PromptMessageActivity.PROMPT_MESSAGE_GATHER_NOW);
   			startActivity(intent);
			break;
		case R.id.mets_sendNow_button:
   			intent = new Intent(this, SendNowActivity.class);
   			startActivity(intent);
			break;
		case R.id.mets_cancelLogin_button:
			intent = new Intent(this, PromptMessageActivity.class);
			intent.putExtra(PromptMessageActivity.PROMPT_MESSAGE_TYPE, PromptMessageActivity.PROMPT_MESSAGE_CANCEL_LOGIN);
			startActivity(intent);
			break;
		default:
			break;
		}
    }
}