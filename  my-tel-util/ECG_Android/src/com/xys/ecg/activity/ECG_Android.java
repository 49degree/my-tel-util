package com.xys.ecg.activity;
import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.szxys.CurveChart;

import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.bluetooth.*;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.dataproc.EcgShapeLine;
import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.log.Logger;
import com.xys.ecg.network.NetworkTool;
import com.xys.ecg.sqlite.DoctorAdviceDB;
import com.xys.ecg.sqlite.RecordDB;

public class ECG_Android extends Activity {
	public static Logger logger = Logger.getLogger(ECG_Android.class);
	//private final int INVISIBLE = 0x00000004;
	private boolean isStart = false; //是否点击了开始按钮
	LinearLayout layDraw = null;
	LinearLayout layTemp = null;
	private ImageButton btTips;
	private EditText etTips;
	private ImageButton btRecord;
	private ImageButton btAdvice;
	private ImageButton btConfig;
	private ImageButton btStart;
	private Button btEcg;
	private Button btAcc;
	private Button btHr;
	private ImageView ivMonitor;//采集图标
	private ImageView ivBT;  //蓝牙
	private ImageView ivBatt;//电量
	private boolean isConnected = false;
	private ArrayList<String> listInfo = new ArrayList<String>(5);
	private CurveChart[] curveChar = new CurveChart[5];//创建五个绘图对象
	private MainEventHandler mainHandler = null;
	private EcgShapeLine[] ecgShapeLine = new EcgShapeLine[5];
	private Activity thisActivity = null;
	private DrawStatus drawstatus = DrawStatus.ECG;//绘图状态
	private final int DRAW_ECG = 0;
	private final int DRAW_ACC_X = 1;
	private final int DRAW_ACC_Y = 2;
	private final int DRAW_ACC_Z = 3;
	private final int DRAW_HR = 4;
	public static  RecordDB gWriteRecord;//
	private enum DrawStatus {
		ACC,
		ECG,
		HR,
	}
	private Button imgView_msg;
	private TextView  msg_count;
	private  BluetoothControl bluetoothControl =  BluetoothControl.getInstance();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ECGApplication.getInstance().setScreenW2H(this);//获取屏幕尺寸
			
		imgView_msg = (Button) findViewById(R.id.ImgView_msg);	
		btRecord = (ImageButton) findViewById(R.id.Btn_record);
		btAdvice = (ImageButton) findViewById(R.id.Btn_advice);
		btConfig = (ImageButton) findViewById(R.id.Btn_config);
		btStart = (ImageButton) findViewById(R.id.Btn_start);
		btEcg = (Button) findViewById(R.id.ImgBtn_ecg);
		btAcc = (Button) findViewById(R.id.ImgBtn_acc);
		btHr = (Button) findViewById(R.id.ImgBtn_hr);
		btTips = (ImageButton)findViewById(R.id.ImgBtn_tips);
		etTips = (EditText)findViewById(R.id.Edit_tips);
		layDraw = (LinearLayout) findViewById(R.id.Layout_draw);
		ivMonitor = (ImageView)findViewById(R.id.ImgView_monitoring);
		ivBT = (ImageView)findViewById(R.id.ImgView_devconn);
		ivBatt = (ImageView)findViewById(R.id.ImgView_batthigh);
		
		curveChar[0] = (CurveChart)findViewById(R.id.DRAW_ECG);// 创建绘图对象
	//	curveChar[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
		curveChar[1] = (CurveChart)findViewById(R.id.DRAW_ACC_X);// 创建绘图对象
		curveChar[2] = (CurveChart)findViewById(R.id.DRAW_ACC_Y);
		curveChar[3] = (CurveChart)findViewById(R.id.DRAW_ACC_Z);
		curveChar[4] = (CurveChart)findViewById(R.id.DRAW_HR);// 创建绘图对象
		thisActivity = this;
	    gWriteRecord = new RecordDB(ECG_Android.this);
		
	    //单击提示按钮
	    btTips.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
	    		try{
	        		String[] info = new String[listInfo.size()];
		    		 for(int i = 0; i < listInfo.size(); i++){
		    			 info[i] = i+1+ "、" +listInfo.get(i); 
		    		 }
		    	
		    		 new AlertDialog.Builder(ECG_Android.this).setTitle("提示").
		    		      setPositiveButton("清空", new DialogInterface.OnClickListener() {		
		    		    	  public void onClick(DialogInterface dialog, int which) {
		    		    	// TODO Auto-generated method stub
		    		    		listInfo.clear();
						 }
					}).setItems(info, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
					
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();  
	    			
	    		}catch(Exception e){
	    			
	    		}   		
	    	}
	    });
	    
	    
		// 当单击短信图标
		imgView_msg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Auto-generated method stub
				Intent inAdvice = new Intent(ECG_Android.this, Advice.class);
				startActivity(inAdvice);
			}
			
		});
		
		// 参数配置
		btConfig.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inConfig = new Intent(ECG_Android.this, Config.class);
				startActivity(inConfig);
				
			}
		});
		
		// 健康建议
		btAdvice.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inAdvice = new Intent(ECG_Android.this, Advice.class);
				startActivity(inAdvice);
			}
		});

		
		
		// 查看记录
		btRecord.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inRecord = new Intent(ECG_Android.this, Record.class);
				startActivity(inRecord);
			}
		});
		
		//ECG
		btEcg.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				drawstatus = DrawStatus.ECG;
				btEcg.setBackgroundDrawable(getResources().getDrawable(R.drawable.ecg_down));
				btAcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.acc));
				btHr.setBackgroundDrawable(getResources().getDrawable(R.drawable.hr));
			
				
				if(isStart){
					ecgShapeLine[0].setSuspendDraw(false);
					ecgShapeLine[1].setSuspendDraw(true);
					ecgShapeLine[2].setSuspendDraw(true);
					ecgShapeLine[3].setSuspendDraw(true);
					ecgShapeLine[4].setSuspendDraw(true);
					
					curveChar[1].setVisibility(View.GONE);  
					curveChar[2].setVisibility(View.GONE);
					curveChar[3].setVisibility(View.GONE);
					curveChar[4].setVisibility(View.GONE);
					curveChar[0].setVisibility(View.VISIBLE);

				}		
			}
		});
		//Acc
		btAcc.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				drawstatus = DrawStatus.ACC;
				btEcg.setBackgroundDrawable(getResources().getDrawable(R.drawable.ecg));
				btAcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.acc_down));
				btHr.setBackgroundDrawable(getResources().getDrawable(R.drawable.hr));
				
				if(isStart){
					ecgShapeLine[1].setSuspendDraw(false);
					ecgShapeLine[2].setSuspendDraw(false);
					ecgShapeLine[3].setSuspendDraw(false);
					ecgShapeLine[4].setSuspendDraw(true);
					ecgShapeLine[0].setSuspendDraw(true);
					
					curveChar[4].setVisibility(View.GONE);
					curveChar[0].setVisibility(View.GONE);
					curveChar[1].setVisibility(View.VISIBLE);
					curveChar[2].setVisibility(View.VISIBLE);
					curveChar[3].setVisibility(View.VISIBLE);
				

					//ecgShapeLine[0] = new EcgShapeLine(curveChar[1], DRAW_ACC);//开始绘制曲线图	
				}
				
						
			}
		});
		//心率
		btHr.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				drawstatus = DrawStatus.HR;
				btEcg.setBackgroundDrawable(getResources().getDrawable(R.drawable.ecg));
				btAcc.setBackgroundDrawable(getResources().getDrawable(R.drawable.acc));
				btHr.setBackgroundDrawable(getResources().getDrawable(R.drawable.hr_down));
				if(isStart){
					ecgShapeLine[4].setSuspendDraw(false);
					ecgShapeLine[0].setSuspendDraw(true);
					ecgShapeLine[1].setSuspendDraw(true);
					ecgShapeLine[2].setSuspendDraw(true);
					ecgShapeLine[3].setSuspendDraw(true);
					
					curveChar[1].setVisibility(View.GONE);
					curveChar[0].setVisibility(View.GONE);
					curveChar[2].setVisibility(View.GONE);
					curveChar[3].setVisibility(View.GONE);
					curveChar[4].setVisibility(View.VISIBLE);

	
				}	
				
			}
		});

		mainHandler = new MainEventHandler();//初始化主线程消息队列
		
		new Thread(innitThread).start();//验证网络，没有配置可用的网络则提示用户进行配置
		
		// 启动蓝牙控制线程
		BluetoothFactory.startBthThread(mainHandler);
		
		// 启动业务数据传输控制线程
		BluetoothControl.startControlThread();
		
		//BluetoothFactory.getInstance().connectBlueTooth(mainHandler);//通知蓝牙模块进行连接
		
		btStart.setOnClickListener(new OnClickListener() {
			
		
			public void onClick(View v) {				 
				if(isConnected){
				//if(true){
					bluetoothControl.startcomm();//开始采集
					ivMonitor.setBackgroundDrawable(getResources().getDrawable(R.drawable.monitoring));
					btStart.setVisibility(View.GONE);// 隐藏开始按钮	
					isStart = true;
					curveChar[1].setVisibility(View.GONE);  
					curveChar[2].setVisibility(View.GONE);
					curveChar[0].setVisibility(View.GONE);
					curveChar[3].setVisibility(View.GONE);
					curveChar[4].setVisibility(View.GONE);
					
					
					ecgShapeLine[0] = new EcgShapeLine(curveChar[0], DRAW_ECG);//开始绘制曲线图	
					ecgShapeLine[1] = new EcgShapeLine(curveChar[1], DRAW_ACC_X);//开始绘制曲线图
					ecgShapeLine[2] = new EcgShapeLine(curveChar[2], DRAW_ACC_Y);
					ecgShapeLine[3] = new EcgShapeLine(curveChar[3], DRAW_ACC_Z);
					ecgShapeLine[4] = new EcgShapeLine(curveChar[4], DRAW_HR);//开始绘制曲线图
					
					ecgShapeLine[0].setSuspendDraw(true);
					ecgShapeLine[1].setSuspendDraw(true);
					ecgShapeLine[2].setSuspendDraw(true);
					ecgShapeLine[3].setSuspendDraw(true);
					ecgShapeLine[4].setSuspendDraw(true);
					
					
					if(drawstatus.equals(DrawStatus.ECG)){
						curveChar[0].setVisibility(View.VISIBLE);
						ecgShapeLine[0].setSuspendDraw(false);
					}else if(drawstatus.equals(DrawStatus.ACC)){
						curveChar[1].setVisibility(View.VISIBLE);
						ecgShapeLine[1].setSuspendDraw(false);
						curveChar[2].setVisibility(View.VISIBLE);
						ecgShapeLine[2].setSuspendDraw(false);
						curveChar[3].setVisibility(View.VISIBLE);
						ecgShapeLine[3].setSuspendDraw(false);
					}else{
						curveChar[4].setVisibility(View.VISIBLE);
						ecgShapeLine[4].setSuspendDraw(false);
					}
					
					logger.debug("btStart");
					//BluetoothFactory.getInstance().beginGetEcgData();//通知蓝牙开始采集数据
					EcgBusiness.getInstance().startTask(mainHandler);//通知业务模块进行业务处理
					//etTips.setText("开始采集数据...");
					outputInfo("开始采集数据...");
				}else{
					//etTips.setText("蓝牙未连接，请检查设备！");
					outputInfo("蓝牙未连接，请检查设备！");
				}	
			}
		});
	}


	public class MainEventHandler extends Handler{
		public MainEventHandler() {
			super();
		}
		public void handleMessage(Message msg) {
			logger.debug("Analysis2shapeline"+msg.what);
			// 可以根据msg.what执行不同的处理，这里没有这么做
			switch (msg.what) {
			case HandlerWhat.Bluetooth2Main://通知蓝牙连接情况
				if((Boolean)msg.obj){
					//etTips.setText("已连接上蓝牙");
					outputInfo("已连接上蓝牙");
					isConnected = true;
					ivBT.setBackgroundDrawable(getResources().getDrawable(R.drawable.devconn));
					//判断是否点击了开始按钮，如果已经点击了，则自动开始采集数据。	
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(isStart){
						
						bluetoothControl.startcomm();//开始采集
						ivMonitor.setBackgroundDrawable(getResources().getDrawable(R.drawable.monitoring));
						curveChar[1].setVisibility(View.GONE);  
						curveChar[2].setVisibility(View.GONE);
						curveChar[0].setVisibility(View.GONE);
						curveChar[3].setVisibility(View.GONE);
						curveChar[4].setVisibility(View.GONE);
						
						
						ecgShapeLine[0] = new EcgShapeLine(curveChar[0], DRAW_ECG);//开始绘制曲线图	
						ecgShapeLine[1] = new EcgShapeLine(curveChar[1], DRAW_ACC_X);//开始绘制曲线图
						ecgShapeLine[2] = new EcgShapeLine(curveChar[2], DRAW_ACC_Y);
						ecgShapeLine[3] = new EcgShapeLine(curveChar[3], DRAW_ACC_Z);
						ecgShapeLine[4] = new EcgShapeLine(curveChar[4], DRAW_HR);//开始绘制曲线图
						
						ecgShapeLine[0].setSuspendDraw(true);
						ecgShapeLine[1].setSuspendDraw(true);
						ecgShapeLine[2].setSuspendDraw(true);
						ecgShapeLine[3].setSuspendDraw(true);
						ecgShapeLine[4].setSuspendDraw(true);
						
						
						if(drawstatus.equals(DrawStatus.ECG)){
							curveChar[0].setVisibility(View.VISIBLE);
							ecgShapeLine[0].setSuspendDraw(false);
						}else if(drawstatus.equals(DrawStatus.ACC)){
							curveChar[1].setVisibility(View.VISIBLE);
							ecgShapeLine[1].setSuspendDraw(false);
							curveChar[2].setVisibility(View.VISIBLE);
							ecgShapeLine[2].setSuspendDraw(false);
							curveChar[3].setVisibility(View.VISIBLE);
							ecgShapeLine[3].setSuspendDraw(false);
						}else{
							curveChar[4].setVisibility(View.VISIBLE);
							ecgShapeLine[4].setSuspendDraw(false);
						}
						EcgBusiness.getInstance().startTask(mainHandler);//通知业务模块进行业务处理
						
						
					}
					
					//TODO:通知业务
				}else{
					//etTips.setText("蓝牙断开");
					MediaPlayer play = MediaPlayer.create(ECGApplication.getInstance(), R.raw.disconnected);
					play.start();
					outputInfo("蓝牙断开...");
					ivBT.setBackgroundDrawable(getResources().getDrawable(R.drawable.devdisconn));
					ivMonitor.setBackgroundDrawable(getResources().getDrawable(R.drawable.unmonitoring));
					ivBatt.setBackgroundDrawable(getResources().getDrawable(R.drawable.battunknown));
					isConnected = false;
					if(isStart){
						
						ecgShapeLine[0].setSuspendDraw(true);
						ecgShapeLine[1].setSuspendDraw(true);
						ecgShapeLine[2].setSuspendDraw(true);
						ecgShapeLine[3].setSuspendDraw(true);
						ecgShapeLine[4].setSuspendDraw(true);
					}
					//TODO:通知业务
				}
				
				break;
			case HandlerWhat.Tread2Notify://通知消息提示框，如上传失败，保存数据失败
				//Toast.makeText(ECG_Android.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
				//etTips.setText((String)msg.obj);
				outputInfo((String)msg.obj);				
				//logger.info("Tread2Notify"+String.valueOf(msg.obj));
				break;					
			case HandlerWhat.Analysis2shapeline://分析线程通知绘图
				ecgShapeLine[0].pushData(msg.obj);//根据分析返回的数据进行绘图
				ecgShapeLine[1].pushData(msg.obj);//根据分析返回的数据进行绘图
				ecgShapeLine[2].pushData(msg.obj);//根据分析返回的数据进行绘图				
				ecgShapeLine[3].pushData(msg.obj);//根据分析返回的数据进行绘图
				ecgShapeLine[4].pushData(msg.obj);//根据分析返回的数据进行绘图
				logger.debug("Analysis2shapeline"+String.valueOf(msg.obj));
				break;
			case HandlerWhat.FileOperate2MainEmpty:
				ivBatt.setBackgroundDrawable(getResources().getDrawable(R.drawable.battempty));
				MediaPlayer play = MediaPlayer.create(ECGApplication.getInstance(), R.raw.lowbatt);
				play.start();
				outputInfo("电量低，请更换电池");
				break;
			case HandlerWhat.FileOperate2MainLow:
				ivBatt.setBackgroundDrawable(getResources().getDrawable(R.drawable.battlow));
				
				break;
			case HandlerWhat.FileOperate2MainNomal:
				ivBatt.setBackgroundDrawable(getResources().getDrawable(R.drawable.battnormal));
				break;
			case HandlerWhat.FileOperate2MainHight:
				ivBatt.setBackgroundDrawable(getResources().getDrawable(R.drawable.batthigh));
				break;
				
			default :
				break;
			}
		}
	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		
		DoctorAdviceDB adviceDB = new DoctorAdviceDB(ECG_Android.this);
		Cursor cursor = adviceDB.selectDoctorAdviceByReadFlag(0);
		TextView tvmsg_count  = (TextView)findViewById(R.id.Msg_count);
		tvmsg_count.setText("("+cursor.getCount()+")");
		cursor.close();
		adviceDB.close();
		
		logger.info("onStart");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		logger.info("onResume");
		if(isStart){
			if(drawstatus.equals(DrawStatus.ECG)){
				curveChar[0].setVisibility(View.VISIBLE);
				ecgShapeLine[0].setSuspendDraw(false);
			}else if(drawstatus.equals(DrawStatus.ACC)){
				curveChar[1].setVisibility(View.VISIBLE);
				ecgShapeLine[1].setSuspendDraw(false);
				curveChar[2].setVisibility(View.VISIBLE);
				ecgShapeLine[2].setSuspendDraw(false);
				curveChar[3].setVisibility(View.VISIBLE);
				ecgShapeLine[3].setSuspendDraw(false);
			}else{
				curveChar[4].setVisibility(View.VISIBLE);
				ecgShapeLine[4].setSuspendDraw(false);
			}
		}
	}
	@Override
	public void onRestart(){
		super.onRestart();
		logger.info("onRestart");
	}
	@Override
	public void onPause(){
		super.onPause();
		logger.info("onPause");
		if(isStart){
			ecgShapeLine[0].setSuspendDraw(true);
			ecgShapeLine[1].setSuspendDraw(true);
			ecgShapeLine[2].setSuspendDraw(true);
			ecgShapeLine[3].setSuspendDraw(true);
			ecgShapeLine[4].setSuspendDraw(true);
		}
		//EcgBusiness.getInstance().suspendTask();//通知业务模块进行业务处理
		
	}
	@Override
	public void onStop(){
		super.onStop();
		logger.info("onStop");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		logger.info("onDestroy");
	}

	// 监听键盘事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确定要退出吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							
							try{				
								EcgBusiness.getInstance().stopTask(mainHandler);//通知业务模块进行业务处理
								System.exit(0);//退出程序
							}catch(Exception e){
								e.printStackTrace();
							}
						}
			});
			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
			});
			builder.create().show();

		}
		return false;
	}
	
	
	/**
	 * 启动初始化网络
	 */
	final Runnable innitThread = new Runnable() {
		public void run(){
			try{
				if(!NetworkTool.checkInternet(thisActivity)){//判断网络状态
					
					NetworkTool.changeNetwork(mainHandler,thisActivity);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	private void outputInfo(String info){
		try{
			while(listInfo.size() >= 5){
				listInfo.remove(0);
			}
			listInfo.add(info);
			etTips.setText(info);
			
		}catch(Exception e){
			logger.debug("outputInfo() Error...");
		}
		
		
	}
}
