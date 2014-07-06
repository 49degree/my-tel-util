package com.yangxp.wapsad;

import android.app.Activity;
import android.os.Bundle;

import cn.waps.AppConnect;

import com.yangxp.wapsad.extend.AppDetail;

public class IndexActivity extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(HeartReceiver.adInfo!=null)
			AppDetail.getInstanct().showAdDetail(IndexActivity.this,HeartReceiver.adInfo);
	}
}
