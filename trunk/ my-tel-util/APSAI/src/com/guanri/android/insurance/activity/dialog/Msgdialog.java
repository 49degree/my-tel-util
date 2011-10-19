package com.guanri.android.insurance.activity.dialog;

import com.guanri.android.insurance.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Msgdialog extends Dialog implements android.view.View.OnClickListener {
	private String title = null;
	private int imageid = 0;
	private String msgstr = null;
	
	private Button mOkBtn = null;
	private TextView tv_titile = null;
	private TextView tv_content = null;
	private ImageView imgview = null;
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getImageid() {
		return imageid;
	}


	public void setImageid(int imageid) {
		this.imageid = imageid;
	}


	public String getMsgstr() {
		return msgstr;
	}


	public void setMsgstr(String msgstr) {
		this.msgstr = msgstr;
	}


	public Msgdialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void displayDlg(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		setContentView(R.layout.msg_dialog);// 设置对话框的布局
		mOkBtn = (Button) findViewById(R.id.msgdialog_btn);
		tv_titile = (TextView)findViewById(R.id.msgdialog_title);
		tv_content = (TextView) findViewById(R.id.msgdialog_content);
		imgview = (ImageView)findViewById(R.id.msgdialog_img);
		
		tv_titile.setText(title);
		tv_content.setText(msgstr);
		if(imageid != 0){
			imgview.setVisibility(View.VISIBLE);
			imgview.setImageResource(imageid);
		}
		
		mOkBtn.setOnClickListener(this);
		show();// 显示对话框
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
	} 
	
}
