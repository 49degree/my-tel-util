package com.custom.update;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Update extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	Button btn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn = (Button)this.findViewById(R.id.update_btn);
        
    }
    
    protected ProgressDialog progress = null; 
    @Override
    public void onClick(View v){
    	progress = ProgressDialog.show(this, "请稍候", "正在连接....");
    	CustomUtils customUtils = new CustomUtils(this);
    	JSONObject installed = customUtils.queryInfo();
    	if(installed!=null){
    		try{
    			customUtils.downFile(installed, handler);
    		}catch(Exception e){
    			
    		}
    		
    	}
    }
    
    
    private Handler handler = new Handler(){
    	int times = 0;
    	@Override
    	public void handleMessage(Message msg){
    		int what = msg.what;
    		switch (what){
    		case 1:
    			progress.cancel();
    			Toast.makeText(Update.this, "存储空间不足", Toast.LENGTH_LONG);
    			break;//没有存储空间
    		case 2:
    			times++;
    			if(times%5==0){
        			int downedL = msg.arg1; 
        			int length = msg.arg2;
    				progress.setMessage("已经下载"+downedL+"(bytes),共"+length+"(bytes)");
    			}
    			break;//报告下载进度
    		case 3:
    			progress.cancel();
    			Toast.makeText(Update.this, "下载成功", Toast.LENGTH_LONG);
    			break;//报告下载完成
    		case 4:
    			progress.cancel();
    			Toast.makeText(Update.this, "连接异常", Toast.LENGTH_LONG);
    			break;//连接异常
    		default:
    			break;
    		}
    	}
    };
}