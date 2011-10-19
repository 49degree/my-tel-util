package com.szxys.mhub.ui.virtualui;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.szxys.mhub.R;
import com.szxys.mhub.bizmanager.BusinessManager;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.subsystem.virtual.Ctrl_Com_Code;
import com.szxys.mhub.subsystem.virtual.OutgoingMessage;


import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ComplainActivity extends Activity {

	private EditText ContentEditText;
	private TextView inputsizeTextView;
	private Button   sendButton;
	private Spinner spinner;
	private Bundle  bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pfphone_complain);
	    bundle = getIntent().getExtras();
		spinner=(Spinner) findViewById(R.id.subSystemID);
		ArrayAdapter<CharSequence> subSystemID=ArrayAdapter.createFromResource(this, R.array.SubSystemID, android.R.layout.simple_spinner_item);
		subSystemID.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(subSystemID);
		ContentEditText =(EditText)findViewById(R.id.EditContext);
		inputsizeTextView =(TextView)findViewById(R.id.inputSize);
		sendButton =(Button)findViewById(R.id.sendMessage);
		sendButton.setOnClickListener(new Button.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String time = sdf.format(date);
				Object param[] = new Object[1];		
				OutgoingMessage complain = new OutgoingMessage();
				complain.setAppId((int)spinner.getSelectedItemId());
				complain.setContent(ContentEditText.getText().toString());
				complain.setDoctorId(bundle.getString("DocID"));
				complain.setDoctorName(bundle.getString("DocName"));
				complain.setIsSend(1);
				complain.setPatientId(bundle.getString("PKID"));
				complain.setPatientName(bundle.getString("PKName"));
				complain.setSourceMsgId("0");
				complain.setPatientId("6");
				complain.setTime(time);
				param[0] = complain;
				BusinessManager.getIBusinessManager().control(0, Platform.SUBBIZ_VIRTUAL, Ctrl_Com_Code.SAVE_COMPLAIN_TODB, param, null);		
			}
			
		});
		ContentEditText.addTextChangedListener(mTextWatcher);
	}
	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart ;
		private int editEnd ;
		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
		int arg3) {
		temp = s;
		if(temp.length() ==0)
		{
			sendButton.setEnabled(false);
		}
		else {
			sendButton.setEnabled(true);
		}
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2,
		int arg3) {
			inputsizeTextView.setText(s.length()+"/120");
			if(temp.length() ==0)
			{
				sendButton.setEnabled(false);
			}
			else {
				sendButton.setEnabled(true);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		editStart = ContentEditText.getSelectionStart();
		editEnd = ContentEditText.getSelectionEnd();
		if(temp.length() ==0)
		{
			sendButton.setEnabled(false);
		}
		else {
			sendButton.setEnabled(true);
		}
		if (temp.length() > 120) {
			Toast.makeText(ComplainActivity.this,"你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
			s.delete(editStart-1, editEnd);
			int tempSelection = editStart;
			ContentEditText.setText(s.length()+"/120");
			ContentEditText.setSelection(tempSelection);
			}
		  }
		};
		
		
	
}


    	

