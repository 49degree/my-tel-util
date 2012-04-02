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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.custom.utils.DialogUtils;
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
	TextView textView9 = null;
	LinearLayout linearLayout1 = null;
	LinearLayout linearLayout3 = null;
	LinearLayout linearLayout4 = null;
	LinearLayout linearLayout5 = null;
	LinearLayout linearLayout6 = null;
	LinearLayout linearLayout7 = null;
	LinearLayout linearLayout8 = null;
	
	Button Button1= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏标题栏        
        setContentView(R.layout.main);
        


        
        progress = new ProgressDialog(this);
    	progress.setTitle("请稍候");
    	progress.setMessage( "正在打开....");
    	progress.setCancelable(true);
    	progress.setOnCancelListener(new OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				if(customUtils!=null)
					customUtils.stop();
				//progress.cancel();
			}
		});
    	progress.show();
    	
    	logger.error("开始查询");
    	LoadResources.clearInstalledFoldInfo(this);
        Intent query = new Intent();//发送查询已经安装广播
        query.setAction("com.custom.update.InitDataFoldReceiver");
        sendBroadcast(query);
        new Thread(){
        	public void run(){
        		try{
        			sleep(3000);
                    LoadResources.queryInstalledFoldInfo(Update.this);
                    MainApplication.getInstance().startNetWorkListen(handler);//监听网络状况
        		}catch(Exception e){}
        	}
        }.start();
        
        
        btn = (Button)this.findViewById(R.id.update_btn);
        btn2 = (Button)this.findViewById(R.id.update_btn2);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        
        linearLayout1 = (LinearLayout)this.findViewById(R.id.LinearLayout1);
        linearLayout3 = (LinearLayout)this.findViewById(R.id.LinearLayout3);
        linearLayout4 = (LinearLayout)this.findViewById(R.id.LinearLayout4);
        linearLayout5 = (LinearLayout)this.findViewById(R.id.LinearLayout5); 
        linearLayout6 = (LinearLayout)this.findViewById(R.id.LinearLayout6); 
        linearLayout7 = (LinearLayout)this.findViewById(R.id.LinearLayout7); 
        linearLayout8 = (LinearLayout)this.findViewById(R.id.LinearLayout8); 
        textView1 = (TextView)this.findViewById(R.id.TextView1);
        textView2 = (TextView)this.findViewById(R.id.TextView2);
        textView3 = (TextView)this.findViewById(R.id.TextView3);
        textView4 = (TextView)this.findViewById(R.id.TextView4);
        textView5 = (TextView)this.findViewById(R.id.TextView5);
        textView6 = (TextView)this.findViewById(R.id.TextView6);
        textView8 = (TextView)this.findViewById(R.id.TextView8);
        textView9 = (TextView)this.findViewById(R.id.TextView9);
        Button1 = (Button)this.findViewById(R.id.Button1);
        Button1.setOnClickListener(this);
        
        

    }
    @Override
    public void onPause(){
    	MainApplication.getInstance().stopNetWorkListen();
    	super.onPause();
    }
    @Override
    public void onRestart(){
    	super.onRestart();
    	MainApplication.getInstance().startNetWorkListen(handler);//监听网络状况
    }
    Thread downThread = null;
    boolean downThreadStop = false;
    @Override
    public void onClick(View v){
    	if(v.getId()==R.id.update_btn){
        	progress.setMessage( "正在连接....");
    		if(!progress.isShowing())
    			progress.show();
    		
    		downThread = new Thread(){
            	public void run(){
                	customUtils = new CustomUtils(Update.this);
                	customUtils.queryInfo();
                	logger.error("JSONObject installed = customUtils.queryInfo();");
                	try{
                	   	Iterator it = LoadResources.updateInstalledInfo.keySet().iterator();
                    	logger.error("createNoInstalledfolds");
                    	while(!downThreadStop&&it.hasNext()){
                    		JSONObject install = LoadResources.updateInstalledInfo.get(it.next());
                        	if(install!=null){
            					logger.error(install.getString(Constant.updateId));
                        		handler.sendMessage(handler.obtainMessage(5));
                        	    customUtils.downFile(install, handler);
                        	    try{
                        	    	wait();
                        	    }catch(Exception e){
                        	    	
                        	    }
                        	}
                    	}
    	    			if(progress.isShowing())
    	    				progress.dismiss();
            		}catch(Exception e){
            			e.printStackTrace();
            		}
       
            	}
            };
            downThread.start();
    	}else if(v.getId()==R.id.update_btn2){
			Intent mIntent = new Intent("/");
			ComponentName comp = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			mIntent.setComponent(comp);
			mIntent.setAction("android.intent.action.VIEW");
			startActivity(mIntent);
    	}else if(v.getId()==R.id.Button1){
			Intent mIntent = new Intent(this,Help.class);
			startActivity(mIntent);
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
    			try{
        			msgObject = (JSONObject)msg.obj;
    				progress.setMessage("正在解压文件");
        			if(!progress.isShowing())
        				progress.show();
        			final String filePath = msgObject.getString(Constant.filePath);
        			new Thread(){
        				public void run(){
        					try{
	        					new ToGetFile().downFileFromzip(filePath);
	        					msgObject.put(Constant.fileUnziped, "true");
	        					LoadResources.updateInstalledInfo(msgObject);
	        					LoadResources.addInstalledInfo(msgObject);
	        					LoadResources.queryInstalledFoldInfo(Update.this);
	        					createNoInstalledfolds();
	        					createInstalledfolds();
	        					if(downThread!=null&&downThread.isAlive())
	        						downThread.interrupt();
        					}catch(Exception e){
        						downThreadStop = true;
	        					if(downThread!=null&&downThread.isAlive())
	        						downThread.interrupt();
        						DialogUtils.showMessageAlertDlg(Update.this,"提示", "解压文件异常", null,null);
        						if(new File(filePath).exists()){
        							new File(filePath).delete();
        						}
        						try {
									LoadResources.updateInstalledInfo.remove(msgObject.getString(Constant.updateId));
								} catch (JSONException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
        					}

        				}
        			}.start();
    			}catch(Exception e){
    				e.printStackTrace();
    				
					downThreadStop = true;
					if(downThread!=null&&downThread.isAlive())
						downThread.interrupt();
    				
    				progress.dismiss();
    				DialogUtils.showMessageAlertDlg(Update.this,"提示", "解压文件异常", null,null);
    			}
    			
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
    		case HandlerWhat.NETWORK_CONNECT_RESULE://网络连接状态结果通知
    			updateUi((Boolean)msg.obj);
    			
    			break;
    		case 7://更新已经安装情况
    			textView3.setText((String)msg.obj);
    			break; 
    		case 8://更新未安装情况
    			textView4.setText((String)msg.obj);
    			if(progress.isShowing())
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
    
    public void updateUi(boolean networkState){
		progress.setMessage( "正在查询资源....");
		if(!progress.isShowing())
			progress.show();
		if(networkState){
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
		    linearLayout6.setBackgroundColor(0xFFCDCD00);
			linearLayout8.setVisibility(View.VISIBLE);
			linearLayout7.setVisibility(View.GONE);
			

			
		    
		}else{
			linearLayout1.setVisibility(View.GONE);
			linearLayout4.setVisibility(View.VISIBLE);
			linearLayout3.setVisibility(View.GONE);
			linearLayout5.setVisibility(View.GONE);
			linearLayout6.setBackgroundColor(0xFFFAFAD2);
			linearLayout7.setVisibility(View.VISIBLE);
			linearLayout8.setVisibility(View.GONE);
			textView1.setText("上次更新时间:"+LoadResources.lastModifyTime+"您的设备已经有"+LoadResources.noModifyTime+"天没有更新了");
			WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE); 
			WifiInfo wifiinfo = wifi_service.getConnectionInfo();
			textView8.setText("设备系列号:"+wifiinfo.getMacAddress());
			try{
				byte[] buffer = LoadResources.loadFile(Update.this,"down.txt",0);
				textView9.setText(new String(buffer,"GBK"));
			}catch(Exception e){}
			
		
		}
		textView5.setText("本机已有内容:");
		
		
		
		createInstalledfolds();
    }
    
    public void createInstalledfolds(){
		try{
			LoadResources.queryInstalledFoldInfo(Update.this);
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
    public int createNoInstalledfolds(){
    	int count = 0;
    	StringBuffer temp = new StringBuffer();
    	Iterator it = LoadResources.noInstalledfolds.keySet().iterator();
    	int allNum = 0;
    	logger.error("createNoInstalledfolds");
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