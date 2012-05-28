package com.custom.update;

import java.io.File;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.utils.DialogUtils;
import com.custom.utils.HandlerWhat;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.MainApplication;

public class Help extends Activity {
	private static final Logger logger = Logger.getLogger(Help.class);
    /** Called when the activity is first created. */
	TextView textView9 = null;
	
	ImageView ImageView1= null;
	ImageButton btn_back = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏        
        setContentView(R.layout.help);
        textView9 = (TextView)this.findViewById(R.id.TextView9);
		try{
			byte[] buffer = LoadResources.loadFile(Help.this,"help.txt",0);
			textView9.setText(new String(buffer,"GBK"));
		}catch(Exception e){}
		
		btn_back = (ImageButton)this.findViewById(R.id.update_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				finish();
			}
		});

    }

}