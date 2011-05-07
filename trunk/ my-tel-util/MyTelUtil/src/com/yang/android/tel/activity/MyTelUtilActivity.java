package com.yang.android.tel.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.android.tel.R;
import com.yang.android.tel.db.DBBean;
import com.yang.android.tel.db.DBOperator;
import com.yang.android.tel.receiver.RefuseReceiver;
import com.yang.android.tel.service.MyTelServices;
import com.yang.android.tel.ui.DataGridView;
import com.yang.android.tel.ui.DataGridViewAdapter;
import com.yang.android.tel.utils.SharedPreferencesUtils;

public class MyTelUtilActivity extends Activity implements OnClickListener{
	public static String TAG = "MyTelUtilActivity";
	public static String inAction = "android.intent.action.PHONE_STATE";
	public static String inAction2 = "NEW_OUTGOING_CALL";
	public static String SMS_RECEIVE = "android.provider.Telephony.SMS_RECEIVED";
	
	
	private final Handler mHandler = new Handler(); 
	private RefuseReceiver refuseReceiver = new RefuseReceiver();//广播接收器
	MyTelServices myTelServices = null;//电话状态监听服务
	private Button btnDial = null; // 拨电话按钮
	private Button btnCancel = null;// 挂电话按钮
	private TextView buttonExit = null;//退出按钮
	private LinearLayout mLinPic = null;
	private LinearLayout mLinCount = null;
	private Button mLinPicBtn = null;
	private Button mLinCountBtn = null;
	private Button mDeleteBtn = null;
	private Button mAddBtn = null;
	private TextView socketBtn = null;
	
	
	private CheckBox restart_call = null;//自动重拨
	private CheckBox check_num = null;//过滤号码
	private LinearLayout check_type_layout = null;
	private CheckBox shutdown_call = null;//自动挂断
	private CheckBox check_call = null;//弹出提示框
	private CheckBox refuse_msg = null;//短信
	
	
	
	
	private DataGridView urineCountList = null;
	private List<String[]> telList = null;
	public static Map<String,String> telMap = new HashMap<String,String>();
	ArrayList<ArrayList<View>> mDataViews = null;

	//数据库操作对象
	DBOperator dbOperator = null;
	SQLiteDatabase sqlDb = null;
	
	private String choiceId = null;//被选中电话号码
	private int urineRecordListX = 0;//表格滚动条的 横轴的位置
	private int urineRecordListY = 0;//表格滚动条的 纵轴的位置
	
	private boolean restart_call_state = false;
	private boolean check_num_state = false;
	private boolean shutdown_call_state = false;//自动挂断
	private boolean check_call_state = false;//弹出提示框
	private boolean refuse_msg_state = false;//弹出提示框
	
	
	
	/**
	 * 获取services绑定对象
	 */
    private ServiceConnection sc = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
        	myTelServices = ((MyTelServices.MyServiceBinder)binder).getServices();
        }
        public void onServiceDisconnected(ComponentName name) {
        	myTelServices.onDestroy();
        	myTelServices = null;
        }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbOperator = new DBOperator(this,"RefuseTelDB.db",null,1);
		sqlDb = dbOperator.getReadableDatabase();
		
		mLinPic = (LinearLayout)findViewById(R.id.mets_urine_rate_pic_layout);
		mLinCount = (LinearLayout)findViewById(R.id.mets_urine_rate_count_layout);
		mLinPicBtn = (Button)findViewById(R.id.mets_urine_rate_pic_btn);
		mLinCountBtn = (Button)findViewById(R.id.mets_urine_rate_count_btn);
		socketBtn = (TextView)findViewById(R.id.button_socket);
		socketBtn.setOnClickListener(this);
		
		
		btnDial = (Button) findViewById(R.id.button_call); // 拨电话按钮
		btnCancel = (Button) findViewById(R.id.button_cancel); // 挂电话按钮
		buttonExit = (TextView) findViewById(R.id.button_exit); //退出按钮
		mDeleteBtn = (Button) findViewById(R.id.button_delete); // 删除记录按钮
		mAddBtn = (Button) findViewById(R.id.button_add); // 添加记录按钮
		
		restart_call = (CheckBox)findViewById(R.id.restart_call);//自动重拨
		check_num = (CheckBox)findViewById(R.id.check_num);//过滤号码
		check_type_layout = (LinearLayout)findViewById(R.id.check_type_layout);
		shutdown_call = (CheckBox)findViewById(R.id.shutdown_call);//自动挂断
		check_call = (CheckBox)findViewById(R.id.check_call);//弹出提示框
		
		refuse_msg = (CheckBox)findViewById(R.id.refuse_msg);//短信

		urineCountList = (DataGridView)findViewById(R.id.mets_urine_count_list_view);//获取记录表格对象
		
		mLinPicBtn.setOnClickListener(this);
		mLinCountBtn.setOnClickListener(this);
		btnDial.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		buttonExit.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);//删除记录
		mAddBtn.setOnClickListener(this);//添加记录
		restart_call.setOnClickListener(this);//自动重拨
		check_num.setOnClickListener(this);//过滤号码
		shutdown_call.setOnClickListener(this);//自动挂断
		check_call.setOnClickListener(this);//弹出提示框
		refuse_msg.setOnClickListener(this);
	
    	//动态注册两个，说明我可以”接收“2个这样Action的动态广播       
    	IntentFilter intentFilter= new IntentFilter();      
    	intentFilter.addAction(inAction2);
    	intentFilter.addAction(inAction); 
    	intentFilter.addAction(SMS_RECEIVE);   
    	intentFilter.setPriority(1000);
    	registerReceiver(refuseReceiver,intentFilter); 
    	//绑定服务
		bindService(new Intent(MyTelUtilActivity.this, MyTelServices.class), sc, Service.BIND_AUTO_CREATE);
		
		initCofigUI();//初始化配置界面
		selectPicBtn();//初始化电话界面
	    getMdata();//获取数据
		initGrid();

	}

	/**
	 * 单击事件处理器
	 */
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.button_call://启动Dialer程序Button变量
			Log.d(TAG, "onClick btnDial:");
			EditText view = (EditText) findViewById(R.id.text_tel_num); //获取的号码
			String phoneNum = view.getText().toString();
			if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNum)) {//方法用来检验输入的串是否是有效的号码
				if(myTelServices!=null){
					myTelServices.startCall(phoneNum);
				}
			} else {
				if(myTelServices!=null){
					myTelServices.endCall(); 
				}
				Toast.makeText(MyTelUtilActivity.this, "号码不正确，请重新输入",
						Toast.LENGTH_LONG).show();// 无效的号码，提示用户输入错误
			}
			break;		
		case R.id.button_cancel:// 取消Dialer程序Button变量
			Log.d(TAG, "onClick btnCancel:");
			if(myTelServices!=null){
				myTelServices.endCall();
			}
			break;			
		case R.id.button_exit://返回按钮事件
			exitMethod();
			break;
		case R.id.mets_urine_rate_pic_btn://打电话相关
			selectPicBtn();
			break;		
		case R.id.mets_urine_rate_count_btn://过滤界面
			selectedCountBtn();
			break;	
		case R.id.button_delete:// 删除记录按钮
			deleteTelnum();
			break;		
		case R.id.button_add: // 添加记录按钮
			addTelnum();
			break;
		case R.id.restart_call://自动重拨
			MyTelServices.isReCall = ((CheckBox)v).isChecked();
			SharedPreferencesUtils.setConfigBoolean(this, "restart_call", ((CheckBox)v).isChecked());
			break;	
		case R.id.refuse_msg://自动重拨
			RefuseReceiver.refuseMessage = ((CheckBox)v).isChecked();
			SharedPreferencesUtils.setConfigBoolean(this, "refuse_msg_state", ((CheckBox)v).isChecked());
			break;		
			
		case R.id.check_num://过滤号码
			RefuseReceiver.isCheckTel = ((CheckBox)v).isChecked();
			if(RefuseReceiver.isCheckTel){
				check_type_layout.setVisibility(View.VISIBLE);
				if(shutdown_call_state==check_call_state){
					
					shutdown_call_state = false;
					check_call_state = true;
					RefuseReceiver.shoutDown = false;
					check_call.setChecked(true);
					SharedPreferencesUtils.setConfigBoolean(this, "check_call", true);
					SharedPreferencesUtils.setConfigBoolean(this, "shutdown_call", false);
				}
			}else{
				check_type_layout.setVisibility(View.INVISIBLE);
			}
			SharedPreferencesUtils.setConfigBoolean(this, "check_num", ((CheckBox)v).isChecked());
			break;	
		case R.id.shutdown_call://自动挂断
			if(((CheckBox)v).isChecked()){
				check_call.setChecked(false);
				RefuseReceiver.shoutDown = true;
				SharedPreferencesUtils.setConfigBoolean(this, "check_call", false);
			}
			SharedPreferencesUtils.setConfigBoolean(this, "shutdown_call", ((CheckBox)v).isChecked());
			break;		
		case R.id.check_call: //弹出提示框
			if(((CheckBox)v).isChecked()){
				shutdown_call.setChecked(false);
				RefuseReceiver.shoutDown = false;
				SharedPreferencesUtils.setConfigBoolean(this, "shutdown_call", false);
			}
			SharedPreferencesUtils.setConfigBoolean(this, "check_call", ((CheckBox)v).isChecked());
			break;
		case R.id.button_socket: 
			this.addSocket();
			break;
		default :
			break;
		}
	}
	
	/**
	 * 初始化配置界面
	 */
	protected void initCofigUI(){
		//获取配置信息
		restart_call_state = SharedPreferencesUtils.getConfigBoolean(this, "restart_call");
		check_num_state = SharedPreferencesUtils.getConfigBoolean(this, "check_num");
		shutdown_call_state = SharedPreferencesUtils.getConfigBoolean(this, "shutdown_call");//自动挂断
		check_call_state = SharedPreferencesUtils.getConfigBoolean(this, "check_call_state");//弹出提示框
		refuse_msg_state = SharedPreferencesUtils.getConfigBoolean(this, "refuse_msg_state");//弹出提示框
		
		if(check_num_state){
			check_type_layout.setVisibility(View.VISIBLE);
			if(shutdown_call_state==check_call_state){
				check_call_state = true;
				shutdown_call_state = false;
				SharedPreferencesUtils.setConfigBoolean(this, "check_call", true);
				SharedPreferencesUtils.setConfigBoolean(this, "shutdown_call", false);
			}
		}else{
			check_type_layout.setVisibility(View.INVISIBLE);
		}
		restart_call.setChecked(restart_call_state);
		check_num.setChecked(check_num_state);
		shutdown_call.setChecked(shutdown_call_state);
		check_call.setChecked(check_call_state);
		refuse_msg.setChecked(refuse_msg_state);
		
		MyTelServices.isReCall=restart_call_state;
		RefuseReceiver.isCheckTel = check_num_state;
		RefuseReceiver.shoutDown = shutdown_call_state;
		RefuseReceiver.refuseMessage = refuse_msg_state;
	}
	
	/**
	 * 选择电话界面
	 */
	protected void selectPicBtn(){
		mLinPic.setVisibility(View.VISIBLE);
		mLinCount.setVisibility(View.INVISIBLE);
		mLinPicBtn.setSelected(true);
		mLinCountBtn.setSelected(false);
	}
	
	/**
	 * 选择记录界面
	 */
	private void selectedCountBtn(){
		mLinPic.setVisibility(View.INVISIBLE);
		mLinCount.setVisibility(View.VISIBLE);
		mLinPicBtn.setSelected(false);
		mLinCountBtn.setSelected(true);
	}	
	
	
	/**
	 * 选择电话界面
	 */
	protected void deleteTelnum(){
		if(choiceId!=null){
			sqlDb.delete("RefuseTelNum", 
					DBBean.RefuseTel.REFUSE_CALL+"='1' and "+DBBean.RefuseTel.REFUSE_TEL_NUM+"=?", new String[]{String.valueOf(choiceId)});
		    getMdata();//获取数据
			initGrid();
		}else{
			
		}
	}

	/**
	 * 选择电话界面
	 */
	protected void addTelnum(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.add_tel_num,null);
		builder.setTitle("添加过滤记录");
		builder.setView(newLayout);
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {  
						EditText nameText = (EditText) newLayout.findViewById(R.id.add_tel_name_text);
		                EditText telText = (EditText) newLayout.findViewById(R.id.add_tel_num_text);
						
		                if(nameText==null||telText.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "电话号码不能为空", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                //判断是否已经存在
						Map<String, String> params = new HashMap<String, String>(1);
						params.put(DBBean.RefuseTel.REFUSE_TEL_NUM, telText.getText().toString());
						params.put(DBBean.RefuseTel.REFUSE_CALL, "1");
						try{
							List<String[]> telList = DBOperator.queryList(DBBean.needInitTables.get("RefuseTel"), sqlDb, 
									new String[]{DBBean.RefuseTel.REFUSE_PEOPLE_NAME,DBBean.RefuseTel.REFUSE_TEL_NUM}, params);
							if(telList.size()>0){
			                	Toast.makeText(getApplicationContext(), "已经存在该号码", Toast.LENGTH_LONG).show();
			                	return ;
							}
							
						}catch(Exception e){
							
						}
						ContentValues values = new ContentValues();
						values.put(DBBean.RefuseTel.REFUSE_PEOPLE_NAME, nameText.getText().toString());
						values.put(DBBean.RefuseTel.REFUSE_TEL_NUM, telText.getText().toString());
						values.put(DBBean.RefuseTel.REFUSE_CALL, "1");
						try{
							DBOperator.insertIntoTable(DBBean.needInitTables.get("RefuseTel"), sqlDb, values);
						    getMdata();//获取数据
							initGrid();
						}catch(Exception e){
							
						}
					}
				});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
		
	}
	
	

	/**
	 * 选择电话界面
	 */
	protected void addSocket(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.add_socket,null);
		builder.setTitle("添加Socket地址");
		builder.setView(newLayout);
		
		final EditText ip = (EditText) newLayout.findViewById(R.id.add_ip);
        final EditText port = (EditText) newLayout.findViewById(R.id.add_port);
        
        String ipStr = SharedPreferencesUtils.getConfigString(this, "socket_ip");
        String portStr = SharedPreferencesUtils.getConfigString(this, "socket_port");
        
        ip.setText(ipStr.equals("")?"223.223.5.145":ipStr);
        port.setText(portStr.equals("")?"5555":portStr);
        
        
		
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {  

						
		                if(ip==null||ip.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "IP不能为空", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                if(port==null||port.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "port不能为空", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                SharedPreferencesUtils.setConfigString(MyTelUtilActivity.this, "socket_ip", ip.getText().toString());
		                SharedPreferencesUtils.setConfigString(MyTelUtilActivity.this, "socket_port", port.getText().toString());
					}
				});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
		
	}
	
	/**
	 * 查询数据
	 * @return
	 */
	private void getMdata(){
		try{
			Map<String, String> params = new HashMap<String, String>(1);
			params.put(DBBean.RefuseTel.REFUSE_CALL, "1");
			telList = DBOperator.queryList(DBBean.needInitTables.get("RefuseTel"), sqlDb, 
					new String[]{DBBean.RefuseTel.REFUSE_PEOPLE_NAME,DBBean.RefuseTel.REFUSE_TEL_NUM}, params);
			for(String[] telinfo:telList){
				telMap.put(telinfo[1], telinfo[0]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 根据数据构造表格及数据
	 */
	public void initGrid(){
        DataGridViewAdapter simpleAdapter = new DataGridViewAdapter();//表格控件数据适配器
        //为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
        String[][] mColumnHeaders = new String[][]{
        		{"","40"},
        		{"姓名","90"},
        		{"号码","110"},
//        		{"屏蔽电话","110"},
//        		{"屏蔽信息","110"}
        };
        mDataViews = new ArrayList<ArrayList<View>>(20);
        ArrayList<View> itemView = null;
		int length = telList.size();
		for(int i=0;i<length;i++){ 
			itemView = new ArrayList<View>();
			String[] info = telList.get(i);
			Log.i(TAG,info[0]+":"+choiceId+":"+info[1]);
			//单选款单击事件（选中） 
			RadioButton rBtn = new RadioButton(this);//构造单选框
			rBtn.setTag(info[1]);
			if(choiceId!=null&&info[1].equals(choiceId)){//判断是否被选中
				rBtn.setChecked(true);
			}
			rBtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					if(choiceId==null||!choiceId.equals((String)v.getTag())){
						choiceId = (String)v.getTag();
						if(urineCountList.mTableContainer!=null){
							urineRecordListX = urineCountList.mTableContainer.getScrollX();
							urineRecordListY = urineCountList.mTableContainer.getScrollY();
						}
						initGrid();
					}
				}
			});
			itemView.add(rBtn);
			
			//增加记录详细信息
			TextView headerDate = new TextView(this);//类型
			headerDate.setText(info[0]);
			TextView headerType = new TextView(this);//类型
			headerType.setText(info[1]);
			headerDate.setGravity(Gravity.CENTER);
			headerType.setGravity(Gravity.CENTER);
			
			itemView.add(headerDate);
			itemView.add(headerType);
			mDataViews.add(itemView);
		}
        
        simpleAdapter.setMColumnHeaders(mColumnHeaders);//配置表头 
        simpleAdapter.setMData(mDataViews);
        urineCountList.setMPageDataAdapter(simpleAdapter);//设置表格数据
        urineCountList.buildDatagrid();//构建表格
        
        //控制表格滚动位置
        mHandler.post(new Runnable() {  
            public void run() { 
                if(urineCountList.mTableContainer!=null){
                	urineCountList.mTableContainer.scrollTo(urineRecordListX, urineRecordListY);
                }  
            }  
          });
	}	

	private void exitMethod(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(myTelServices!=null){
							myTelServices.endCall();
						}
						stopService(new Intent(MyTelUtilActivity.this, MyTelServices.class));
						unbindService(sc);
						unregisterReceiver(refuseReceiver);    
						finish();// 退出程序
					}
				});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}
	// 监听键盘事件
	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exitMethod();
		}
		return false;
	}


	public static boolean isIntentAvailable(Context context, Intent intent ) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	

    //在onStart中动态注册广播,当然也可以在onCreate里面注册   
    @Override    
    protected void onStart() {        
    	// TODO Auto-generated method stub        
    	super.onStart();
    	
		//获取数据库对象
    	if(sqlDb==null){
    		dbOperator = new DBOperator(this,"RefuseTelDB.db",null,1);
    	}
    	if(dbOperator==null){
    		sqlDb = dbOperator.getReadableDatabase();
    	}
    }
    
    //在onStop中取消注册广播,如果在onCreate里面注册，那么在onDestroy里面取消注册。    
    @Override    
    protected void onDestroy() {        
    	// TODO Auto-generated method stub        
    	super.onDestroy();
    	if(sqlDb!=null){
    		sqlDb.close();
    		sqlDb = null;
    	}
    	if(dbOperator!=null){
    		dbOperator.close();
    		dbOperator = null;
    	}
    }  	
}