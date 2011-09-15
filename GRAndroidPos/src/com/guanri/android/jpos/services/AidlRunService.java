package com.guanri.android.jpos.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AidlRunService extends MainService{
    //定义个一个Tag标签   
    private static final String TAG = "AidlRunService"; 
    
    @Override  
    public IBinder onBind(Intent intent) {  
        Log.e(TAG, "start IBinder~~~");  
        return mBinder;  
    }
    
    private final GrPosService.Stub mBinder = new GrPosService.Stub(){ 
        /**
         * 打开POS
         * @return
         */
        public boolean startPos(){
        	Log.d(TAG, "startPos");
        	return true;
        }
        /**
         * 关闭POS
         * @return
         */
        public boolean stopPos(){
        	Log.d(TAG, "stopPos");
        	return true;
        }
        /**
         * 其他操作
         * @param params
         * @return
         */
        public String  operate(String params){
        	Log.d(TAG, "operate");
        	return "";
        }
    };
}
