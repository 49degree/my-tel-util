package com.custom;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.custom.network.HttpRequest;

public class CustomDemo extends Activity {
	final static String TAG = "CustomDemo";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("cmdid", "");
        params.put("imei", "");
        params.put("imsi", "");
        params.put("iccid", "");
        params.put("mobile", "");
        params.put("ctime", "20111001132050");
        params.put("osver", "");
        params.put("cver", "");
        params.put("uid", "");
        
        HttpRequest HttpRequest = new HttpRequest(Constant.QUERY_URL,params,this);
        String retStr = HttpRequest.getResponsString(false);
        Log.i(TAG, "==================="+retStr);
        
        
    }
}