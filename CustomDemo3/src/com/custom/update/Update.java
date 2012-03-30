package com.custom.update;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.custom.utils.DialogUtils;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.DialogUtils.OnAlertDlgSureBtn;

public class Update extends Activity implements OnClickListener{
	private static final Logger logger = Logger.getLogger(Update.class);
    /** Called when the activity is first created. */
	Button btn = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn = (Button)this.findViewById(R.id.update_btn);
        btn.setOnClickListener(this);
        
        Intent query = new Intent();
        query.setAction("com.custom.update.InitDataFoldReceiver");
        this.sendBroadcast(query);
        logger.error("开始查询");
        new Thread(){
        	public void run(){
        		try{
        			int count = 0;
        			while(count++<10){
        				//logger.error("开始查询:"+count);
        				//查询
        				HashMap<String,String> folds = LoadResources.queryDownedFold(Update.this);
        				Iterator it = folds.keySet().iterator();
        				while(it.hasNext()){
        					String name = (String)it.next();
        					String value = folds.get(name);
        					//logger.error("name:"+name+":value:"+value);
        				}
        				Thread.sleep(500);
        			}
        		}catch(Exception e){
        			
        		}
        	}
        }.start();
    }
    
    protected ProgressDialog progress = null; 
    CustomUtils customUtils = null;
    @Override
    public void onClick(View v){
    	
    	progress = new ProgressDialog(this);
    	progress.setTitle("请稍候");
    	progress.setMessage( "正在连接....");
    	//progress.setIndeterminate(indeterminate);
    	progress.setCancelable(true);
    	progress.show();
    	progress.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				customUtils.stop();
				//progress.cancel();
			}
		});
        new Thread(){
        	public void run(){
            	customUtils = new CustomUtils(Update.this);
            	JSONObject installed = customUtils.queryInfo();

            	logger.error("JSONObject installed = customUtils.queryInfo();");
            	try{
                 	
                	if(installed!=null){
            			try{
            				if(!installed.getBoolean(Constant.success)){
                         		handler.sendMessage(handler.obtainMessage(6,installed.getString("msg")));
                         		return;
            				}
            			}catch(Exception e){
            			}
    					logger.error(installed.getString(Constant.updateId));
                		handler.sendMessage(handler.obtainMessage(5));
                	    customUtils.downFile(installed, handler);
                	}else{
                		handler.sendMessage(handler.obtainMessage(6,"无升级资源"));
                	}
        		}catch(Exception e){
        			e.printStackTrace();
        		}
   
        	}
        }.start();
    }
    
    
    private Handler handler = new Handler(){
    	int times = 0;
    	JSONObject msgObject = null;
    	@Override
    	public void handleMessage(Message msg){
    		int what = msg.what;
    		switch (what){
    		case 1:
    			progress.cancel();
    			DialogUtils.showMessageAlertDlg(Update.this,"提示", "存储空间不足", null,null);
    			break;//没有存储空间
    		case 2:
    			times++;
    			if(times%5==0){
    				
        			int downedL = msg.arg1; 
        			int length = msg.arg2;
        			logger.error("download:"+downedL);
    				progress.setMessage("已经下载"+downedL+"(bytes),共"+length+"(bytes)");
    			}
    			break;//报告下载进度
    		case 3:
    			progress.dismiss();
    			msgObject = (JSONObject)msg.obj;
    			DialogUtils.showMessageAlertDlg(Update.this,"提示", "下载成功", new OnAlertDlgSureBtn(){
    				public void OnSureBtn(){
    					progress.setMessage("正在解压文件");
    					progress.show();
    	    			try{
    	        			final String filePath = msgObject.getString(Constant.filePath);
    	        			new Thread(){
    	        				public void run(){
    	        					try{
        	        					new ToGetFile().downFileFromzip(filePath);
        	        					msgObject.put(Constant.fileUnziped, "true");
        	        					LoadResources.updateInstalledInfo(msgObject);
    	        					}catch(Exception e){
    	        						DialogUtils.showMessageAlertDlg(Update.this,"提示", "解压文件异常", null,null);
    	        						if(new File(filePath).exists()){
    	        							new File(filePath).delete();
    	        						}
    	        						try {
											LoadResources.installedInfo.remove(msgObject.getString(Constant.updateId));
										} catch (JSONException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
    	        					}
    	        					progress.cancel();
    	        				}
    	        			}.start();
    	    			}catch(Exception e){
    	    				e.printStackTrace();
    	    				progress.cancel();
    	    				DialogUtils.showMessageAlertDlg(Update.this,"提示", "解压文件异常", null,null);
    	    			}
    				}
    			},null);
    			break;//报告下载完成
    		case 4:
    			progress.cancel();
    			DialogUtils.showMessageAlertDlg(Update.this,"提示", "连接异常", null,null);
    			break;//连接异常
    		case 5:
    			progress.setMessage("开始下载程序");
    			break;//没有存储空间    		
    		case 6:
    			progress.cancel();
    			DialogUtils.showMessageAlertDlg(Update.this,"提示",(String)msg.obj, null,null);
    			break;//连接异常
    		default:
    			break;
    		}
    	}
    };
}