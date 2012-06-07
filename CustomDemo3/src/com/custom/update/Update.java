package com.custom.update;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.network.HttpRequest;
import com.custom.update.Constant.DirType;
import com.custom.update.CustomUtils.FileInfo;
import com.custom.utils.CryptionControl;
import com.custom.utils.DialogUtils;
import com.custom.utils.HandlerWhat;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.MainApplication;
import com.custom.utils.TypeConversion;

public class Update extends Activity implements OnClickListener{
	private static final Logger logger = Logger.getLogger(Update.class);
    /** Called when the activity is first created. */
    protected ProgressDialog progress = null; 
    CustomUtils customUtils = null;
	Button btn = null;
	Button btn2 = null;
	ImageButton btn_back = null;
	TextView textView1 = null;
	TextView textView2 = null;
	TextView textView3 = null;
	TextView textView4 = null;
	TextView textView5 = null;
	TextView textView6 = null;
	TextView textView8 = null;
	TextView textView9 = null;
	TextView textView10 = null;
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
        
        registMac();
        if(!checkMacResult){
        	return;
        }
        	
        
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
    	progress.setCanceledOnTouchOutside(false);
    	
    	
    	logger.error("开始查询");
    	LoadResources.clearInstalledFoldInfo(this);
        Intent query = new Intent();//发送查询已经安装广播
        query.setAction("com.custom.update.InitDataFoldReceiver");
        sendBroadcast(query);
        new Thread(){
        	public void run(){
        		try{
        			sleep(3000);
        			LoadResources.loadUpdateInstalledInfo();
                    LoadResources.queryInstalledFoldFileInfo(Update.this);
                    MainApplication.getInstance().startNetWorkListen(handler);//监听网络状况
        		}catch(Exception e){}
        	}
        }.start();
        
        
        btn = (Button)this.findViewById(R.id.update_btn);
        btn2 = (Button)this.findViewById(R.id.update_btn2);
        btn_back = (ImageButton)this.findViewById(R.id.update_back);
        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        
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
        textView10 = (TextView)this.findViewById(R.id.TextView10);
        Button1 = (Button)this.findViewById(R.id.Button1);
        Button1.setOnClickListener(this);
        
        

    }
    
    
	boolean checkMacResult = false;
	/**
	 * 验证
	 * @return
	 */
	private void registMac(){
		int times = 0;
		WifiManager wifi_service = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
		wifi_service.setWifiEnabled(true);

		try{
			while(wifi_service.getWifiState()!=wifi_service.WIFI_STATE_ENABLED&&times++<100)
				Thread.sleep(50);
			WifiInfo wifiinfo = wifi_service.getConnectionInfo();
			
			final String filePath = Constant.getSdPath()+File.separator+Constant.root_fold+File.separator+Constant.check_mac_info_file;
			byte[] buf = LoadResources.loadFile(this, Constant.root_fold+File.separator+Constant.check_mac_info_file, DirType.sd,false);
			final String macStr = wifiinfo.getMacAddress();
			if(buf!=null){
				String s = new String(buf,"GBK");
				byte[] macBuffer = TypeConversion.stringToAscii(macStr);
				byte[] temp = new byte[macBuffer.length%8==0?macBuffer.length:(macBuffer.length/8+1)*8];
				System.arraycopy(macBuffer, 0, temp, 0, macBuffer.length);
				
				String encodeMac = TypeConversion.byte2hex(CryptionControl.getInstance().encryptoECB(temp, CryptionControl.rootKey));
				if(s.equals(encodeMac))
					checkMacResult = true;
			}
			
			if(!checkMacResult){
				
    			AlertDialog.Builder alertDialog = new AlertDialog.Builder(Update.this).setTitle("提示");
    			alertDialog.setMessage("请点击确定联网获得正版授权认证") ;
    			alertDialog.setPositiveButton("确定", 
		        new DialogInterface.OnClickListener(){
	                    public void onClick(DialogInterface dialoginterface, int i){ 
	                    	progress = ProgressDialog.show(Update.this, "请稍候", "正在加载资源....");
	                    	
	            			HashMap<String, String> params = new HashMap<String, String>();
	            			HttpRequest httpRequest = new HttpRequest(Constant.check_mac_url+"&mac="+macStr,
	            					params, Update.this);
	            			JSONObject retJson = httpRequest.getResponsJSON(false);
	            			logger.error("retJson:"+retJson);
	            			// 解析数据
	            			try{
	            				if(retJson.getBoolean("result")){
	            					checkMacResult = true;
	            					LoadResources.writeFile(filePath,retJson.getString("mac"));
	            					Toast.makeText(Update.this, "验证成功", Toast.LENGTH_LONG).show();
	            					
	            					Intent intent = new Intent();
	            					intent.setClass(Update.this.getApplicationContext(),Update.class);
	            					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            					Update.this.startActivity(intent);
	            					finish();
	            					
	            				}else{
	            					if(progress!=null){
	            						progress.dismiss();
	            					}
	            					Toast.makeText(Update.this, "验证失败", Toast.LENGTH_LONG).show();
	            					((Activity)Update.this).finish();
	            					
	            				}
	            			}catch(Exception e){
	            				
	            				if(progress!=null){
	            					progress.dismiss();
	            				}
	            				Toast.makeText(Update.this, "验证失败", Toast.LENGTH_LONG).show();
	            				((Activity)Update.this).finish();
	            				
	            			}
                        } 
                });

    			alertDialog.show();
			}
		}catch(Exception e){
			if(progress!=null){
				progress.dismiss();
			}
			((Activity)Update.this).finish();
			e.printStackTrace();
		}
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
                			String unZipflag = null;
                			try{
                				unZipflag = install.getString(Constant.fileUnziped);
                			}catch(Exception e){}
                			
                        	if(install!=null&&unZipflag==null){
            					//logger.error(install.getString(Constant.updateId));
                        		handler.sendMessage(handler.obtainMessage(5));
                        	    customUtils.downFile(install, handler);
                        	    try{
                        	    	synchronized (this) {
                        	    		wait();
									}
                        	    }catch(Exception e){
                        	    	e.printStackTrace();
                        	    }
                        	    
                        	}
                    	}
    	    			if(progress.isShowing())
    	    				progress.dismiss();

            		}catch(Exception e){
            			e.printStackTrace();
            		}
            		handler.sendMessage(handler.obtainMessage(HandlerWhat.NETWORK_CONNECT_RESULE, new Boolean(true)));
            		logger.error("下载线程结束");
            	}
            };
            downThread.setDaemon(true);
            downThread.start();
    	}else if(v.getId()==R.id.update_btn2){
    		startApp(this,Constant.wifi_manager);
    		
//    		if(Build.VERSION.SDK_INT>13){
//    			startApp(this,Constant.wifi_manager);
//    		}else{
//    			Intent mIntent = new Intent("/");
//    			ComponentName comp = new ComponentName("com.android.settings",
//    					"com.android.settings.WirelessSettings");
//    			mIntent.setComponent(comp);
//    			mIntent.setAction("android.intent.action.VIEW");
//    			startActivity(mIntent);
//    		}
			
			
			
    	}else if(v.getId()==R.id.Button1){
			Intent mIntent = new Intent(this,Help.class);
			startActivity(mIntent);
    	}else if(v.getId()==R.id.update_back){
    		finish();
    	}

    }
    
    boolean hasNotify = false;
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
    				
    				FileInfo fileInfo = (FileInfo)msg.obj;
        			//logger.error("download:"+downedL);
    				progress.setMessage("已经下载"+fileInfo.downLength+"(bytes),共"+fileInfo.allLength+"(bytes)");
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
        					Looper.prepare();
        					try{
        						ToGetFile toGetFile = new ToGetFile();
        						toGetFile.downFileFromzip(filePath);
        						toGetFile.delteDownFile(filePath);
	        					msgObject.put(Constant.fileUnziped, "true");
	        					LoadResources.updateInstalledInfo(msgObject,true);
	        					if(downThread!=null&&downThread.isAlive()){
	        						synchronized (downThread) {
	        							downThread.notify();
									}
	        					}
	        						
        					}catch(Exception e){
        						e.printStackTrace();
        						downThreadStop = true;
	        					if(downThread!=null&&downThread.isAlive()){
	        						synchronized (downThread) {
	        							downThread.notify();
									}
	        					}
        						DialogUtils.showMessageAlertDlg(Update.this,"提示", "解压文件异常:"+e.getMessage(), null,null);
        					}
        					logger.error("解压线程结束");
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
    			progress.setMessage("连接下载文件");
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
    	logger.error("updateUi");
		progress.setMessage( "正在查询资源....");
		
		initFile();//以下为加压SD卡内容
		copyFile();//
		
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
		    logger.error("allNum:"+allNum);
		    if(allNum<1){
		    	linearLayout3.setVisibility(View.GONE);
		    	linearLayout5.setVisibility(View.GONE);
				if(!hasNotify){
					DialogUtils.showMessageAlertDlg(Update.this,"提示", "已经更新到最新版本，无需更新", null,null);
					hasNotify = true;
				}
		    	
		    }else{
		    	linearLayout5.setVisibility(View.VISIBLE);
		    }
		    linearLayout6.setBackgroundColor(0xFFCDCD00);
			linearLayout8.setVisibility(View.VISIBLE);
			linearLayout7.setVisibility(View.GONE);
			textView10.setText("已联网");
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
			wifi_service.setWifiEnabled(true);
			WifiInfo wifiinfo = wifi_service.getConnectionInfo();
			textView8.setText("设备系列号:"+wifiinfo.getMacAddress());
			try{
				byte[] buffer = LoadResources.loadFile(Update.this,"down.txt",0);
				textView9.setText(new String(buffer,"GBK"));
			}catch(Exception e){}
			textView10.setText("未联网");
		}
		textView5.setText("本机已有内容:");
		createInstalledfolds();
    }
    
    public void createInstalledfolds(){
		try{
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
    
    
    /**
     * 以下COPY SD卡内容
     */
    private void copyFile(){
    	if(Constant.getExtSdPath()==null||"".equals(Constant.getExtSdPath())){
    		return;
    	}
		HashMap<String,String> btnInfo = new HashMap<String,String> ();
		try{
			byte[] buf = LoadResources.loadFile(this, Constant.root_fold+File.separator+Constant.copy_file_info_file, DirType.sd,false);
			if(buf!=null){
				BufferedReader fin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
				String line = fin.readLine();
				
				while(line!=null){
					btnInfo.put(line,line);
					line = fin.readLine();
				}
			}
		}catch(Exception e){
			
		}
        //查询扩展SD卡中是否有需要解压的资源
		File sdfile = LoadResources.getFileByType(Constant.copy_file_fold,DirType.extSd);
		String[] lists = getFileNames(sdfile.listFiles());
		ZipToFile zipToFile = new ZipToFile();//.upZipFile(filePath,false,"custom");
		if(lists!=null){
			for(int i = 0;i<lists.length;i++){
				if(!btnInfo.containsKey(lists[i])){
					try{
						zipToFile.upZipFile(Constant.getExtSdPath()+
								File.separator+Constant.copy_file_fold+
								File.separator+lists[i],true,"..");
						
						btnInfo.put(lists[i], lists[i]);
					}catch(Exception e){
						
					}

				}
			}
			String filePath = Constant.getSdPath()+File.separator+Constant.root_fold+File.separator+Constant.copy_file_info_file;
    		//保存文件
    		modifyInitedFile(btnInfo,filePath);
		}
    }
    
    /**
     * 以下为加压SD卡内容
     */
    private void initFile(){
    	if(Constant.getExtSdPath()==null||"".equals(Constant.getExtSdPath())){
    		return;
    	}
    	FilenameFilter fl = new FilenameFilter() {//过滤文件名称
			@Override
			public boolean accept(File arg0, String arg1) {
				//logger.error("accept(File arg0, String arg1):"+arg1);
				if(arg1.indexOf(".")<0)
					return false;
				return "ZIP".equals(arg1.substring(arg1.indexOf(".")+1).toUpperCase());
			}
		};
		
		HashMap<String,String> btnInfo = new HashMap<String,String> ();
		try{
			byte[] buf = LoadResources.loadFile(this, Constant.root_fold+File.separator+Constant.inited_file_info_file, DirType.sd,false);
			if(buf!=null){
				BufferedReader fin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
				String line = fin.readLine();
				
				while(line!=null){
					btnInfo.put(line,line);
					line = fin.readLine();
				}
			}
		}catch(Exception e){
			
		}

        //查询扩展SD卡中是否有需要解压的资源
		File sdfile = LoadResources.getFileByType(Constant.inited_file_fold,DirType.extSd);
		String[] lists = getFileNames(sdfile.listFiles(fl));
		ToGetFile toGetFile = new ToGetFile();
		if(lists!=null){
			for(int i = 0;i<lists.length;i++){
				if(!btnInfo.containsKey(lists[i])){
					toGetFile.downFileFromzip(Constant.getExtSdPath()+
							File.separator+Constant.inited_file_fold+
							File.separator+lists[i]);
					
					btnInfo.put(lists[i], lists[i]);
				}
			}
			String filePath = Constant.getSdPath()+File.separator+Constant.root_fold+File.separator+Constant.inited_file_info_file;
    		//保存文件
    		modifyInitedFile(btnInfo,filePath);
		}
		
		
    }
    
	/**
	 * 获取文件名称
	 * @param files
	 * @return
	 */
	private String[] getFileNames(File[] files){
		String[] lists = null;
		if(files!=null&&files.length>0){
			
			lists = new String[files.length];
			for(int i=0;i<lists.length;i++){
				lists[i] = files[i].getName();
			}
			
		}
		return lists;
	}
	
	public void modifyInitedFile(HashMap<String,String> btnInfo,String filePath){
		try{
			//String filePath = Constant.getSdPath()+File.separator+Constant.inited_file_fold+File.separator+Constant.inited_file_info_file;
			
			//清空文件
			RandomAccessFile   raf   =   new   RandomAccessFile(filePath,   "rw"); 
			raf.setLength(0); 
			raf.close(); 
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			Iterator it = btnInfo.keySet().iterator();

			while(it.hasNext()){
				String key = (String)it.next();
				key = key+"\n"; 
				fos.write(key.getBytes());
			}
			fos.getChannel().force(true);
			fos.flush();
			fos.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	

	/**
	 * 启动应用
	 * @param context
	 * @param packageName
	 */
	public static  void startApp(Context context, String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);

			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				context.startActivity(intent);
			}
		} catch (Exception e) {

		}
	}
}