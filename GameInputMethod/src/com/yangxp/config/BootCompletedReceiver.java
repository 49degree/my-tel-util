package com.yangxp.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yangxp.config.db.DBBean;

public class BootCompletedReceiver extends BroadcastReceiver  {  
  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        Log.i("MainActivity", "系统启动完毕");  
        initDataBase(context);
    }  
    
	private void initDataBase(Context context){
		File file = new File("/data/data/"+context.getPackageName()+"/databases/"+DBBean.DB_SETTING);
		if(file.exists())
			return;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = context.getAssets().open(DBBean.DB_SETTING);
		    out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len=in.read(buffer))>0){
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try{
				file.delete();
			}catch(Exception e1){
				
			}
			return;
		}finally{
			if(in!=null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if(out!=null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
} 
