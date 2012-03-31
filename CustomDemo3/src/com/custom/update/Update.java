package com.custom.update;

import java.io.File;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.utils.DialogUtils;
import com.custom.utils.DialogUtils.OnAlertDlgSureBtn;
import com.custom.utils.HandlerWhat;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.MainApplication;

public class Update extends Activity implements OnClickListener{
	private static final Logger logger = Logger.getLogger(Update.class);
    /** Called when the activity is first created. */
    protected ProgressDialog progress = null; 
    CustomUtils customUtils = null;
	Button btn = null;
	Button btn2 = null;
	TextView textView1 = null;
	TextView textView2 = null;
	TextView textView3 = null;
	TextView textView4 = null;
	TextView textView5 = null;
	TextView textView6 = null;
	TextView textView8 = null;
	LinearLayout linearLayout1 = null;
	LinearLayout linearLayout3 = null;
	LinearLayout linearLayout4 = null;
	LinearLayout linearLayout5 = null;
	LinearLayout linearLayout6 = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LoadResources.queryDownedFold(Update.this);
        progress = new ProgressDialog(this);
    	progress.setTitle("请稍候");
    	progress.setMessage( "正在连接....");
    	progress.setCancelable(true);
    	progress.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				customUtils.stop();
				//progress.cancel();
			}
		});
        
        MainApplication.getInstance().startNetWorkListen(handler);//监听网络状况
        LoadResources.initInstalledInfo();
        btn = (Button)this.findViewById(R.id.update_btn);
        btn2 = (Button)this.findViewById(R.id.update_btn2);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        
        linearLayout1 = (LinearLayout)this.findViewById(R.id.LinearLayout1);
        linearLayout3 = (LinearLayout)this.findViewById(R.id.LinearLayout3);
        linearLayout4 = (LinearLayout)this.findViewById(R.id.LinearLayout4);
        linearLayout5 = (LinearLayout)this.findViewById(R.id.LinearLayout5); 
        linearLayout6 = (LinearLayout)this.findViewById(R.id.LinearLayout6); 
        textView1 = (TextView)this.findViewById(R.id.TextView1);
        textView2 = (TextView)this.findViewById(R.id.TextView2);
        textView3 = (TextView)this.findViewById(R.id.TextView3);
        textView4 = (TextView)this.findViewById(R.id.TextView4);
        textView5 = (TextView)this.findViewById(R.id.TextView5);
        textView6 = (TextView)this.findViewById(R.id.TextView6);
        textView8 = (TextView)this.findViewById(R.id.TextView8);
        
        

    }
    

    @Override
    public void onClick(View v){
    	if(v.getId()==R.id.update_btn){
        	progress.setMessage( "正在连接....");
    		if(!progress.isShowing())
    			progress.show();
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
    	}else if(v.getId()==R.id.update_btn2){
			Intent mIntent = new Intent("/");
			ComponentName comp = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			mIntent.setComponent(comp);
			mIntent.setAction("android.intent.action.VIEW");
			startActivity(mIntent);
			System.exit(0);//推出程序
    	}

    }
    
    
    private Handler handler = new Handler(){
    	int times = 0;
    	JSONObject msgObject = null;
    	@Override
    	public void handleMessage(Message msg){
    		int what = msg.what;
    		switch (what){
    		case 1:
    			if(progress.isShowing())
    				progress.dismiss();
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
    			if(progress.isShowing())
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
        	        					LoadResources.initInstalledInfo();
        	        					createNoInstalledfolds();
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
    			if(progress.isShowing())
    				progress.dismiss();
    			DialogUtils.showMessageAlertDlg(Update.this,"提示", "连接异常", null,null);
    			break;//连接异常
    		case 5:
    			progress.setMessage("开始下载程序");
    			break;//没有存储空间    		
    		case 6:
    			if(progress.isShowing())
    				progress.dismiss();
    			DialogUtils.showMessageAlertDlg(Update.this,"提示",(String)msg.obj, null,null);
    			break;//连接异常
    		case HandlerWhat.NETWORK_CONNECT_RESULE://网络结果通知
    			progress.setMessage( "正在查询资源....");
    			if(!progress.isShowing())
    				progress.show();
    			if((Boolean)msg.obj){
                	customUtils = new CustomUtils(Update.this);
                	customUtils.queryInfo();//联网查询
    				
    				textView1.setText("联网内容更新");
    				linearLayout1.setVisibility(View.VISIBLE);
    				linearLayout4.setVisibility(View.GONE);
    				linearLayout3.setVisibility(View.VISIBLE);
    			    textView2.setText("最近更新："+LoadResources.lastModifyTime);
    			    int allNum = createNoInstalledfolds();
    			    if(allNum<1){
    			    	linearLayout3.setVisibility(View.GONE);
    			    	linearLayout5.setVisibility(View.GONE);
    			    	DialogUtils.showMessageAlertDlg(Update.this,"提示", "已经更新到最新版本，无需更新", null,null);
    			    }else{
    			    	linearLayout5.setVisibility(View.VISIBLE);
    			    }
    			    linearLayout6.setBackgroundColor(0xFFADEAEA);
    			    
    			}else{
    				linearLayout1.setVisibility(View.GONE);
    				linearLayout4.setVisibility(View.VISIBLE);
    				linearLayout3.setVisibility(View.GONE);
    				linearLayout5.setVisibility(View.GONE);
    				linearLayout6.setBackgroundColor(0xFFFAFAD2);
    				textView1.setText("上次更新时间:"+LoadResources.lastModifyTime+"您的设备已经有"+LoadResources.noModifyTime+"天没有更新了");
    			
    			}
    			textView5.setText("本机已有内容:");
    			createInstalledfolds();
    			
    			break;
    		case 7://更新已经安装情况
    			textView3.setText((String)msg.obj);

    			break; 
    		case 8://更新未安装情况
    			textView4.setText((String)msg.obj);
    			progress.dismiss();
    			break;  
    		case 9://更新未安装情况
    			textView6.setText((String)msg.obj);
    			break; 
    		case 10://更新未安装情况
    			if(progress.isShowing())
    				progress.dismiss();
    			break;     			

    		default:
    			break;
    		}
    	}
    };
    
    public void createInstalledfolds(){
    	logger.error("开始查询");
        Intent query = new Intent();
        query.setAction("com.custom.update.InitDataFoldReceiver");
        this.sendBroadcast(query);
        new Thread(){
        	public void run(){
        		try{
        			Thread.sleep(5000);
        			
        	    	StringBuffer temp = new StringBuffer();
        	    	Iterator it = LoadResources.installedfolds.keySet().iterator();
        	    	int count = 0;
        	    	temp.append("          ");
        	    	while(it.hasNext()){
        	    		String name = (String)it.next();
        	    		int value = LoadResources.installedfolds.get(name);
        	    		logger.error("name:"+name+":value:"+value);
        	    		if(count>0&&++count%3==0){
        	    			temp.append("\n");
        	    			temp.append("          ");
        	    		}
        	    		temp.append(name).append(":").append(LoadResources.installedfolds.get(name)).append("          ");
        	    	}
        	    	handler.sendMessage(handler.obtainMessage(7, temp.toString())); 
        	    	handler.sendMessage(handler.obtainMessage(10));
        		}catch(Exception e){
        			
        		}
        	}
        }.start();
    }
    public int createNoInstalledfolds(){
    	int count = 0;
    	StringBuffer temp = new StringBuffer();
    	Iterator it = LoadResources.noInstalledfolds.keySet().iterator();
    	int allNum = 0;
    	while(it.hasNext()){
    		String name = (String)it.next();
    		temp.append("          ");
    		if(count>0&&count++%3==0){
    			temp.append("\n");
    			temp.append("          ");
    		}
    		temp.append(name).append(":").append(LoadResources.noInstalledfolds.get(name)).append("          ");
    		allNum +=LoadResources.noInstalledfolds.get(name);
    	}

    	handler.sendMessage(handler.obtainMessage(8, temp.toString()));
    	String msg1 = "发现了"+allNum+"个新内容，保持及时更新，享受最佳服务！";
    	handler.sendMessage(handler.obtainMessage(9, msg1));
    	return allNum;
    }
}