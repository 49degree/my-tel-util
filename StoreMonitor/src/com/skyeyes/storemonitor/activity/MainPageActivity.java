package com.skyeyes.storemonitor.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.skyeyes.base.BaseSocketHandler;
import com.skyeyes.base.activity.BaseActivity;
import com.skyeyes.base.cmd.CommandControl.REQUST;
import com.skyeyes.base.cmd.bean.ReceiveCmdBean;
import com.skyeyes.base.cmd.bean.impl.ReceivLogin;
import com.skyeyes.base.cmd.bean.impl.ReceiveChannelPic;
import com.skyeyes.base.cmd.bean.impl.ReceiveDeviceRegisterInfo;
import com.skyeyes.base.cmd.bean.impl.ReceiveReadDeviceList;
import com.skyeyes.base.cmd.bean.impl.SendObjectParams;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAllManuByMouse;
import com.skyeyes.base.cmd.bean.impl.manucount.ReceiveAvgManuStayTimeByMouse;
import com.skyeyes.base.exception.CommandParseException;
import com.skyeyes.base.exception.NetworkException;
import com.skyeyes.base.h264.H264DecoderException;
import com.skyeyes.base.h264.JavaH264Decoder;
import com.skyeyes.base.h264.JavaH264Decoder.DecodeSuccCallback;
import com.skyeyes.base.network.impl.SkyeyeSocketClient;
import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.PreferenceUtil;
import com.skyeyes.base.util.StringUtil;
import com.skyeyes.base.view.TopTitleView;
import com.skyeyes.base.view.TopTitleView.OnClickListenerCallback;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.StoreMonitorApplication;
import com.skyeyes.storemonitor.activity.adapter.ChennalPicViewAdapter;
import com.skyeyes.storemonitor.activity.bean.ChennalPicBean;
import com.skyeyes.storemonitor.process.DeviceProcessInterface.DeviceReceiveCmdProcess;
import com.skyeyes.storemonitor.process.impl.CountManuCmdProcess;
import com.skyeyes.storemonitor.service.DevicesService;




public class MainPageActivity extends BaseActivity{
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
	String TAG = "MainPageActivity";
	public final static int SEND_QUERY_MANU_ID = 1;
	private boolean stopQueryManu = true;
	private boolean isInView = false;
	
	//TextView store_login_id_tv = null;
	Gallery gallery = null;
	Gallery history_gallery = null;
	private TopTitleView topTitleView;
	private LinearLayout vp_real_time_ll;
	private LinearLayout vp_history_ll;
	private LinearLayout no_login_notify_ll;
	private TextView count_all_manu_tv;
	private TextView count_avg_time_tv;
	private TextView login_notify_tv;
	private TextView video_history_query_time_iv;
	private TextView video_history_query_long_iv;
	
	//private ImageView video_history_play_iv;
	
	List<ChennalPicBean> chennalPicBeanlist=new ArrayList<ChennalPicBean>();
	ChennalPicViewAdapter historyAdapter;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_video_page);
		//store_login_id_tv = (TextView)findViewById(R.id.store_login_id_tv);
		vp_real_time_ll = (LinearLayout)findViewById(R.id.vp_real_time_ll);
		vp_history_ll = (LinearLayout)findViewById(R.id.vp_history_ll);
		no_login_notify_ll  = (LinearLayout)findViewById(R.id.no_login_notify_ll);
		
		topTitleView = (TopTitleView)findViewById(R.id.vp_topView);
		
		count_all_manu_tv = (TextView)findViewById(R.id.count_all_manu_tv);
		count_avg_time_tv = (TextView)findViewById(R.id.count_avg_time_tv);
		login_notify_tv = (TextView)findViewById(R.id.login_notify_tv);
		
		
		RelativeLayout video_history_query_long_rv = (RelativeLayout)findViewById(R.id.video_history_query_long_rv);
		RelativeLayout video_history_query_time_rv = (RelativeLayout)findViewById(R.id.video_history_query_time_rv);
		video_history_query_time_iv = (TextView)findViewById(R.id.video_history_query_time_tv);
		video_history_query_long_iv = (TextView)findViewById(R.id.video_history_query_long_tv);
		
				
		
		video_history_query_time_rv.setOnClickListener(new DateTimePick());
		video_history_query_long_rv.setOnClickListener(new NumberPick());
		
		/**
		video_history_play_iv = (ImageView)findViewById(R.id.video_history_play_iv);
		
		video_history_play_iv.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("MainPageActivity", "iv.setOnClickListener(new OnClickListener()================");
				Intent it = new Intent(MainPageActivity.this,VideoPlayActivity.class);
				it.putExtra("chennalId", (byte)0);
				it.putExtra("videoType", 1);
				try {
					it.putExtra("startTime", DateUtil.getTimeStringFormat(format.parse(StringUtil.getTextViewValue(video_history_query_time_iv)),DateUtil.TIME_FORMAT_YMDHMS));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				it.putExtra("videoLong", (short)(Short.parseShort(StringUtil.getTextViewValue(video_history_query_long_iv))*60));
				startActivity(it);
			}
		});
		*/
		
		vp_history_ll.setVisibility(View.GONE);
		vp_real_time_ll.setVisibility(View.GONE);
		no_login_notify_ll.setVisibility(View.VISIBLE);
		
		stopQueryManu = false;

		topTitleView.setOnRightButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				if(StoreMonitorApplication.getInstance().getReceivLogin()!=null){
					vp_real_time_ll.setVisibility(View.GONE);
					vp_history_ll.setVisibility(View.VISIBLE);
				}

				stopQueryManu = true;
				queryManuCountHandler.removeMessages(SEND_QUERY_MANU_ID);
			}
		});
		topTitleView.setOnLeftButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				stopQueryManu = false;
		    	if(isInView && 
		    			StoreMonitorApplication.getInstance().getReceivLogin()!=null){
		    		queryManuCountHandler.sendEmptyMessage(SEND_QUERY_MANU_ID);//统计人流
		    	}
				if(StoreMonitorApplication.getInstance().getReceivLogin()!=null){
					vp_history_ll.setVisibility(View.GONE);
					vp_real_time_ll.setVisibility(View.VISIBLE);
				}

			}
		});
		
		topTitleView.setOnMenuButtonClickListener(new OnClickListenerCallback() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				try{
					HomeActivity.getInstance().toggleMenu();
				}catch(Exception e){
					e.printStackTrace();
				}
				

			}
		});
		
		gallery = (Gallery) findViewById(R.id.chennal_pic_gallery);
		history_gallery = (Gallery) findViewById(R.id.history_chennal_pic_gallery);
		



	}
    
    
    public void onResume(){
    	super.onResume();
    	
    	if(StoreMonitorApplication.getInstance().getReceivLogin()==null){
    		if(DevicesService.getInstance()!=null){
    			stopService(new Intent(MainPageActivity.this,DevicesService.class));
    		}
    		String userName = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_name);
    		String userPsd = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_psd);
    		String ip = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG, PreferenceUtil.sysconfig_server_ip);
    		String port = PreferenceUtil.getConfigString(PreferenceUtil.SYSCONFIG, PreferenceUtil.sysconfig_server_port);
    		
    		if(StringUtil.isNull(userName)||
    				StringUtil.isNull(userPsd)||
    				StringUtil.isNull(ip)||
    				StringUtil.isNull(port)){
    			login_notify_tv.setText("用户数据不完整，请前往设置用户数据...");
    		}else{
        		SkyeyeSocketClient skyeyeSocketClient = null;
        		try {
        			skyeyeSocketClient = new SkyeyeSocketClient(new SocketHandlerImpl(), true);
        			skyeyeSocketClient.setServerAddr(ip, Integer.parseInt(port));
        		} catch (Exception e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
        		queryEquitListNoLogin(skyeyeSocketClient);//查询设备
        		
        		login_notify_tv.setText("正在登陆设备，请稍后...");
    		}
    	}
    	
    	Log.e(TAG,"onResume queryManuCountHandler.sendEmptyMessage(SEND_QUERY_MANU_ID)");
    	
    	if(!isInView && !stopQueryManu && StoreMonitorApplication.getInstance().getReceivLogin()!=null){
    		queryManuCountHandler.sendEmptyMessage(SEND_QUERY_MANU_ID);//统计人流
    	}
    	isInView = true;	
    }
    public void onStop(){
    	super.onStop();
    	isInView = false;
    	queryManuCountHandler.removeMessages(SEND_QUERY_MANU_ID);
    	
    }
    public void onDestroy(){
    	super.onDestroy();
    }
    
	// 查询设备列表
	public void queryEquitListNoLogin(
			SkyeyeSocketClient skyeyeSocketClient) {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		String userName = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_name);
		
		String userPsd = PreferenceUtil.getConfigString(PreferenceUtil.ACCOUNT_IFNO, PreferenceUtil.account_login_psd);

		Object[] params = new Object[] { userName, userPsd };
		//params = new Object[]{};
		try {

			sendObjectParams.setParams(REQUST.cmdUserEquitListNOLogin, params);

			Log.i(TAG,"testEquitListNoLogin入参数："+ sendObjectParams.toString());
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			skyeyeSocketClient.sendCmd(sendObjectParams);
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
    
	private class SocketHandlerImpl extends BaseSocketHandler {
		
		public SocketHandlerImpl(){
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceiveCmdEx(final ReceiveCmdBean receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i("MainPageActivity","解析报文成功:" + (receiveCmdBean!=null?receiveCmdBean.toString():"receiveCmdBean is null"));
			if (receiveCmdBean instanceof ReceiveReadDeviceList) {
				if(((ReceiveReadDeviceList) receiveCmdBean).getCommandHeader().resultCode == 0){
					final ReceiveReadDeviceList receiveReadDeviceList = ((ReceiveReadDeviceList) receiveCmdBean);
					PreferenceUtil.setSingleConfigInfo(PreferenceUtil.DEVICE_INFO,
							PreferenceUtil.device_count, receiveReadDeviceList.deviceCodeList.size());
					PreferenceUtil.setSingleConfigInfo(PreferenceUtil.DEVICE_INFO,
							PreferenceUtil.device_code_list,  receiveReadDeviceList.deviceListString);
					showToast(receiveReadDeviceList.deviceListString);	
					
					MainPageActivity.this.startService(new Intent(MainPageActivity.this,DevicesService.class));
					final LoginReceive loginReceive = new LoginReceive();
					final DeviceRegisterInfoReceive deviceRegisterInfoReceive = new DeviceRegisterInfoReceive();

					new Thread(){
						public void run(){
							while(true){
								if(DevicesService.getInstance() != null){
									if(DevicesService.getInstance().getCurrentDeviceCode() == null){
										DevicesService.getInstance().selectDevice(receiveReadDeviceList.deviceCodeList.get(0));
										DevicesService.getInstance().registerCmdProcess(ReceivLogin.class.getSimpleName(), loginReceive);
										DevicesService.getInstance().registerCmdProcess(ReceiveDeviceRegisterInfo.class.getSimpleName(),
												deviceRegisterInfoReceive);
									}
									break;
								}
								try {
									sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}.start();
					
					Log.i("MainPageActivity", "DevicesService.getInstance()==null:"+(DevicesService.getInstance()==null));
					
				}
			}else{
				Toast.makeText(MainPageActivity.this, "查询失败："+((ReceiveReadDeviceList) receiveCmdBean).getCommandHeader().errorInfo, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCmdExceptionEx(CommandParseException ex) {
			// TODO Auto-generated method stub
			Toast.makeText(MainPageActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSocketExceptionEx(NetworkException ex) {
			// TODO Auto-generated method stub
			Toast.makeText(MainPageActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onSocketClosedEx() {
			// TODO Auto-generated method stub
			//System.out.println("测试连接失败:onFailure");
			//Toast.makeText(MainPageActivity.this, "测试连接失败:onFailure", Toast.LENGTH_SHORT).show();

		}

	};
	
	private class LoginReceive extends DeviceReceiveCmdProcess<ReceivLogin>{

		@Override
		public void onProcess(ReceivLogin receiveCmdBean) {
			// TODO Auto-generated method stub
			if(receiveCmdBean.getCommandHeader().resultCode != 0){
				showToast(receiveCmdBean.getCommandHeader().errorInfo);
				StoreMonitorApplication.getInstance().setReceivLogin(null);

			}else{
				showToast("登陆成功...............");
		    	if(isInView && !stopQueryManu && 
		    			StoreMonitorApplication.getInstance().getReceivLogin()!=null){
		    		queryManuCountHandler.sendEmptyMessage(SEND_QUERY_MANU_ID);//统计人流
		    	}
		    	
		    	login_notify_tv.setText("正在查询设备通道信息，请稍后...");
			}
				
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	int chennalCount = 0;
	int getPicCount = 0;
	private class DeviceRegisterInfoReceive extends DeviceReceiveCmdProcess<ReceiveDeviceRegisterInfo>{

		@Override
		public void onProcess(ReceiveDeviceRegisterInfo receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i(TAG, receiveCmdBean.toString());
			//showToast("通道状态："+receiveCmdBean.toString());
			chennalCount = receiveCmdBean.videoChannelCount;
			if(chennalCount>0){
				//查询通道图片
				SendObjectParams sendObjectParams = new SendObjectParams();
				Object[] params = new Object[] {(byte)0x00};
				try {
					sendObjectParams.setParams(REQUST.cmdReqVideoChannelPic, params);
					System.out.println("getChannelPic入参数：" + sendObjectParams.toString());
					
					DevicesService.sendCmd(sendObjectParams, new ChannelPicReceive());
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				login_notify_tv.setText("正在获取设备通道图片，请稍后...");
			}
			
			Bitmap tempPic = BitmapFactory.decodeResource(MainPageActivity.this.getResources(), R.drawable.photo);
	        WindowManager windowManager = getWindowManager();
	        Display display = windowManager.getDefaultDisplay();
			float zoom = 1.0f*display.getWidth()/tempPic.getWidth();
			int imgHeight = (int)(tempPic.getHeight()*zoom);
			LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
					display.getWidth(),imgHeight);
			List<ChennalPicBean> historyPicBeanlist=new ArrayList<ChennalPicBean>();
			for(int i=0;i<chennalCount;i++){
	        	ChennalPicBean picBean=new ChennalPicBean();
	        	picBean.des = "通道"+(i+1);
	        	picBean.img = new BitmapDrawable(tempPic);
	        	picBean.ivLp = ivLp;
	        	picBean.chennalId = (byte)(i);
	        	historyPicBeanlist.add(picBean);
			}
			historyAdapter = new ChennalPicViewAdapter(MainPageActivity.this,historyPicBeanlist,1);
			
			historyAdapter.setHistoryInfo(StringUtil.getTextViewValue(video_history_query_time_iv), StringUtil.getTextViewValue(video_history_query_long_iv));
			history_gallery.setAdapter(historyAdapter);

		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
	}
	

	
	private Bitmap pic;
	private Bitmap blackPic;
	private class ChannelPicReceive extends DeviceReceiveCmdProcess<ReceiveChannelPic>{

		@Override
		public void onProcess(ReceiveChannelPic receiveCmdBean) {
			// TODO Auto-generated method stub
			Log.i("MainPageActivity", "ChannelPicReceive================");
			login_notify_tv.setText("正在解码通道图片，请稍后...");

		    try {
				JavaH264Decoder decoder = new JavaH264Decoder(new DecodeSuccCallback(){
					@Override
					public void onDecodeSucc(JavaH264Decoder decoder ,Bitmap bitmap) {
						// TODO Auto-generated method stub
						Log.i("DecoderCallback", "onDecodeSucc================");
						pic = Bitmap.createBitmap(bitmap);
						decoder.toStop();
					}
					
				});
				decoder.sendStream(receiveCmdBean.pic);
			} catch (H264DecoderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
		    if(pic == null){
		    	if(blackPic==null){
		    		blackPic = BitmapFactory.decodeResource(MainPageActivity.this.getResources(), R.drawable.photo);
		    	}
		    	pic = blackPic;
		    }
		    
			if(pic!=null){
		        WindowManager windowManager = getWindowManager();
		        Display display = windowManager.getDefaultDisplay();
				float zoom = 1.0f*display.getWidth()/pic.getWidth();
				int imgHeight = (int)(pic.getHeight()*zoom);
				LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(
						display.getWidth(),imgHeight);
	        	ChennalPicBean picBean=new ChennalPicBean();
	        	picBean.des = "通道"+(getPicCount+1);
	        	picBean.img = new BitmapDrawable(pic);
	        	picBean.ivLp = ivLp;
	        	picBean.chennalId = (byte)(getPicCount);
	            chennalPicBeanlist.add(picBean);
	            pic = null;
			}
			Log.i("MainPageActivity", "getPicCount================"+getPicCount);
			
			getPicCount++;
			if(getPicCount<chennalCount){
				//查询通道图片
				SendObjectParams sendObjectParams = new SendObjectParams();
				Object[] params = new Object[] {(byte)getPicCount};
				try {
					sendObjectParams.setParams(REQUST.cmdReqVideoChannelPic, params);
					System.out.println("getChannelPic入参数：" + sendObjectParams.toString());
					
					DevicesService.sendCmd(sendObjectParams, new ChannelPicReceive());
				} catch (CommandParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(chennalPicBeanlist.size()>0){
				Log.i("MainPageActivity", "chennalPicBeanlist================"+chennalPicBeanlist.size());
				if(gallery.getAdapter()==null){
					ChennalPicViewAdapter pageAdapter=new ChennalPicViewAdapter(MainPageActivity.this,chennalPicBeanlist,0);
					gallery.setAdapter(pageAdapter);
					
					vp_real_time_ll.setVisibility(View.VISIBLE);
					no_login_notify_ll.setVisibility(View.GONE);
				}else{
					((ChennalPicViewAdapter)gallery.getAdapter()).notifyDataSetChanged();
				}
			}
			
			if(getPicCount==chennalCount && chennalPicBeanlist.size()==0){
				login_notify_tv.setText("获取设备通道图片失败...");
			}
			
		}

		@Override
		public void onFailure(String errinfo) {
			// TODO Auto-generated method stub
			
		}
		
	}
	

	
	
	
	private void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	

	Handler queryManuCountHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case SEND_QUERY_MANU_ID:
					if(!isInView || stopQueryManu)
						break ;
					removeMessages(SEND_QUERY_MANU_ID);
					getManucountByMonth();
					break;
			}
		}
	};
	
	/**
	 * 按月统计人流
	 * @param dayTime 如：2014-05-01 00:00:00
	 */
	private void getManucountByMonth() {
		SendObjectParams sendObjectParams = new SendObjectParams();
		
		Object[] params = new Object[] {DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YM)+"-01 00:00:00"};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAllManuByMouse, params);
			CountAllManuByMouse mCountManuCmdProcess = new CountAllManuByMouse(REQUST.cmdReqAllManuByMouse,(String)params[0]);
			mCountManuCmdProcess.setTimeout(30*1000);
			
			DevicesService.sendCmd(sendObjectParams,mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 按月统计平均驻留时间
	 * @param dayTime 如：2014-05-01 00:00:00
	 */
	private void getManuAvgTimeByMonth() {
		SendObjectParams sendObjectParams = new SendObjectParams();
		Object[] params = new Object[] {DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YM)+"-01 00:00:00"};
		try {
			sendObjectParams.setParams(REQUST.cmdReqAvgManuStayTimeByMouse, params);
			CountAvgManuStayTimeByMouse mCountManuCmdProcess = new CountAvgManuStayTimeByMouse(REQUST.cmdReqAvgManuStayTimeByMouse,(String)params[0]);
			mCountManuCmdProcess.setTimeout(30*1000);
			
			DevicesService.sendCmd(sendObjectParams,mCountManuCmdProcess);
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getStringZero(int value,int len){
		int valueLen = String.valueOf(value).length();
		String temp = String.valueOf(value);
		if(valueLen<len){
			for(int i=len - valueLen;i>0;i--){
				temp = "0"+temp;
			}
		}
		return temp;
	}
	
	public void onResponsTimeout(){
		Log.e(TAG, "onResponsTimeout");
		if(!isInView || stopQueryManu)
			return ;
		queryManuCountHandler.sendEmptyMessageDelayed(SEND_QUERY_MANU_ID,20*1000);
	}
	
	/**
	 * 按月统计人流
	 * @author Administrator
	 *
	 */
	public class CountAvgManuStayTimeByMouse extends CountManuCmdProcess<ReceiveAvgManuStayTimeByMouse>{

		public CountAvgManuStayTimeByMouse(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}
		public void onProcess(ReceiveAvgManuStayTimeByMouse receiveCmdBean) {
			super.onProcess(receiveCmdBean);
			if(!isInView || stopQueryManu)
				return ;
			try{
				if(receiveCmdBean.getCommandHeader().cmdId == REQUST.cmdReqAvgManuStayTimeByMouse.cmdId()){
					if(count_avg_time_tv!=null){
						count_avg_time_tv.setText(getStringZero(receiveCmdBean.countManuResultBeans.get(0).avgTime,2));
					}
					queryManuCountHandler.sendEmptyMessageDelayed(SEND_QUERY_MANU_ID,60*1000);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void onResponsTimeout(){
			MainPageActivity.this.onResponsTimeout();
		}
	}

	/**
	 * 按月统计人流
	 * @author Administrator
	 *
	 */
	public class CountAllManuByMouse extends CountManuCmdProcess<ReceiveAllManuByMouse>{

		public CountAllManuByMouse(REQUST requst, String beginTime) {
			super(requst, beginTime);
			// TODO Auto-generated constructor stub
		}
		public void onProcess(ReceiveAllManuByMouse receiveCmdBean) {
			super.onProcess(receiveCmdBean);
			if(!isInView || stopQueryManu)
				return ;
			try{
				if(receiveCmdBean.getCommandHeader().cmdId == REQUST.cmdReqAllManuByMouse.cmdId()){
					if(count_all_manu_tv!=null){
						count_all_manu_tv.setText(getStringZero(receiveCmdBean.countManuResultBeans.get(0).inManu,4));
					}
					getManuAvgTimeByMonth();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void onResponsTimeout(){
			MainPageActivity.this.onResponsTimeout();
		}
	}
	
	
	
	private class DateTimePick implements OnClickListener {
		private DatePicker datePicker;
		private TimePicker timePicker;
		

		@Override
		public void onClick(View v) {
			final Calendar calendar = Calendar.getInstance();
			LayoutInflater inflaterDl = LayoutInflater
					.from(MainPageActivity.this);
			LinearLayout layout = (LinearLayout) inflaterDl.inflate(
					R.layout.date_time_pick, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainPageActivity.this);
			
			builder.setView(layout);
			
//			final Dialog dialog = builder.create();
//			dialog.show();
//			dialog.getWindow().setContentView(layout);

			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(historyAdapter!=null){
								historyAdapter.setHistoryInfo(format.format(calendar.getTime()), StringUtil.getTextViewValue(video_history_query_long_iv));
							}
							video_history_query_time_iv.setText(format.format(calendar.getTime()));
						}
					});
			// 设置一个NegativeButton
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

			datePicker = (DatePicker) layout.findViewById(R.id.dpPicker);
			timePicker = (TimePicker) layout.findViewById(R.id.tpPicker);

			datePicker.init(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					new OnDateChangedListener() {

						@Override
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// 获取一个日历对象，并初始化为当前选中的时间
							calendar.set(year, monthOfYear, dayOfMonth);
						}
					});
			timePicker.setIs24HourView(true);
			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						@Override
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
							calendar.set(Calendar.HOUR, hourOfDay);
							calendar.set(Calendar.MINUTE, minute);
						}
					});
			
			builder.show();

		}
	};
    
	private class NumberPick implements OnClickListener {
		@Override
		public void onClick(View v) {

			LayoutInflater inflaterDl = LayoutInflater
					.from(MainPageActivity.this);
			LinearLayout layout = (LinearLayout) inflaterDl.inflate(
					R.layout.number_pick, null);
			

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainPageActivity.this);
			builder.setView(layout);
			
//			final Dialog dialog = builder.create();
//			dialog.show();
//			dialog.getWindow().setContentView(layout);

			final NumberPicker np = (NumberPicker) layout.findViewById(R.id.numberPicker);
			np.setMaxValue(20);
			np.setMinValue(0);
			np.setFocusable(true);
			np.setFocusableInTouchMode(true);
			

			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							video_history_query_long_iv.setText(String.valueOf(np.getValue()));
							if(historyAdapter!=null){
								historyAdapter.setHistoryInfo( StringUtil.getTextViewValue(video_history_query_time_iv), String.valueOf(np.getValue()));
							}
						}
					});
			// 设置一个NegativeButton
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			
			builder.show();

		}
	};
}
