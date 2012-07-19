package com.custom.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.MainApplication;

public class InitDataFoldReceiver extends BroadcastReceiver {  
	Logger logger = Logger.getLogger(InitDataFoldReceiver.class);
    @Override      
    public void onReceive(Context context, Intent intent) { 
    	logger.info(intent.getAction());
        int count = LoadResources.queryDownedFold(context,Constant.path);//LoadResources.queryDownedFold(context);
        FileOutputStream out = null;
        //更新数据
        try{
    		Context friendContext = MainApplication.getInstance().createPackageContext(
    				Constant.update_package,Context.CONTEXT_IGNORE_SECURITY);
    		out = friendContext.openFileOutput(Constant.installedFold, Context.MODE_APPEND);
    		logger.error("InitDataFoldReceiver 查询结果:"+count);
    		modifyInstalledFile(count);
        }catch(Exception e){
        	
        }finally{
        	try{
        		if(out!=null)
        			out.close();
            }catch(Exception e){
            	
            }
        	
        }
    }
    
    
	public void modifyInstalledFile(int count){
		try{
			String filePath = Constant.getSdPath()+File.separator+Constant.root_fold+File.separator+Constant.installedFold;
			FileOutputStream fos = new FileOutputStream(new File(filePath),true);
			fos.write(("="+Constant.foldName_ex+"="+count+"\n").getBytes("GBK"));
			fos.flush();
			fos.flush();
			fos.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
