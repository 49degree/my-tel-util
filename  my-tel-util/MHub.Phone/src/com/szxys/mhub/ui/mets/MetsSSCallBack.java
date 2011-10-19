package com.szxys.mhub.ui.mets;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;

import com.szxys.mhub.R;
import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.subsystem.mets.business.MetsConstant;

/**
 * 更新界面数回调函数
 * @author Administrator
 *
 */
public class MetsSSCallBack implements ISubSystemCallBack {
	Context context = null;
	public final Handler metsHandler = new Handler();
	public MetsSSCallBack() {
		context = MhubApplication.getInstance();
	}

	/**
	 * @param lMainCmd
	 * @param lSubCmd
	 * @param data
	 * @return long
	 */
	@Override
	public long onReceived(int lMainCmd, int lSubCmd, byte[] data, int length) {
		metsHandler.post(new UIRunnable(lMainCmd,lSubCmd,data,length));
		return 0;
	}
	
	@Override
	public long onReceived(int mainCmd, int subCmd, Object obj){
		return 0;
	}

    public class UIRunnable implements Runnable{
    	int lMainCmd; int lSubCmd;byte[] data; int length;
    	public UIRunnable(int lMainCmd, int lSubCmd, byte[] data, int length){
    		this.lMainCmd = lMainCmd; this.lSubCmd= lSubCmd;this.data= data; this.length = length;
    	}
    	public void run(){
    		try {
    			if(lMainCmd ==MetsConstant.MAIN_CMD_TEST&&lSubCmd == MetsConstant.SUB_CMD_TEST){
    				Activity activity = null;//MhubApplication.getInstance().getActivity("MetsMain");
    				if(activity!=null){
//        				Button mhub_test_Button = (Button)activity.findViewById(R.id.mets_test_button);
//        				mhub_test_Button.setText(new String(data));
    				}

    			}
    		} catch (Exception e) {
    		}
    	}
    }
}
