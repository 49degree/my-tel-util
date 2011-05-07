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
	private RefuseReceiver refuseReceiver = new RefuseReceiver();//�㲥������
	MyTelServices myTelServices = null;//�绰״̬��������
	private Button btnDial = null; // ���绰��ť
	private Button btnCancel = null;// �ҵ绰��ť
	private TextView buttonExit = null;//�˳���ť
	private LinearLayout mLinPic = null;
	private LinearLayout mLinCount = null;
	private Button mLinPicBtn = null;
	private Button mLinCountBtn = null;
	private Button mDeleteBtn = null;
	private Button mAddBtn = null;
	private TextView socketBtn = null;
	
	
	private CheckBox restart_call = null;//�Զ��ز�
	private CheckBox check_num = null;//���˺���
	private LinearLayout check_type_layout = null;
	private CheckBox shutdown_call = null;//�Զ��Ҷ�
	private CheckBox check_call = null;//������ʾ��
	private CheckBox refuse_msg = null;//����
	
	
	
	
	private DataGridView urineCountList = null;
	private List<String[]> telList = null;
	public static Map<String,String> telMap = new HashMap<String,String>();
	ArrayList<ArrayList<View>> mDataViews = null;

	//���ݿ��������
	DBOperator dbOperator = null;
	SQLiteDatabase sqlDb = null;
	
	private String choiceId = null;//��ѡ�е绰����
	private int urineRecordListX = 0;//���������� �����λ��
	private int urineRecordListY = 0;//���������� �����λ��
	
	private boolean restart_call_state = false;
	private boolean check_num_state = false;
	private boolean shutdown_call_state = false;//�Զ��Ҷ�
	private boolean check_call_state = false;//������ʾ��
	private boolean refuse_msg_state = false;//������ʾ��
	
	
	
	/**
	 * ��ȡservices�󶨶���
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
		
		
		btnDial = (Button) findViewById(R.id.button_call); // ���绰��ť
		btnCancel = (Button) findViewById(R.id.button_cancel); // �ҵ绰��ť
		buttonExit = (TextView) findViewById(R.id.button_exit); //�˳���ť
		mDeleteBtn = (Button) findViewById(R.id.button_delete); // ɾ����¼��ť
		mAddBtn = (Button) findViewById(R.id.button_add); // ��Ӽ�¼��ť
		
		restart_call = (CheckBox)findViewById(R.id.restart_call);//�Զ��ز�
		check_num = (CheckBox)findViewById(R.id.check_num);//���˺���
		check_type_layout = (LinearLayout)findViewById(R.id.check_type_layout);
		shutdown_call = (CheckBox)findViewById(R.id.shutdown_call);//�Զ��Ҷ�
		check_call = (CheckBox)findViewById(R.id.check_call);//������ʾ��
		
		refuse_msg = (CheckBox)findViewById(R.id.refuse_msg);//����

		urineCountList = (DataGridView)findViewById(R.id.mets_urine_count_list_view);//��ȡ��¼������
		
		mLinPicBtn.setOnClickListener(this);
		mLinCountBtn.setOnClickListener(this);
		btnDial.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		buttonExit.setOnClickListener(this);
		mDeleteBtn.setOnClickListener(this);//ɾ����¼
		mAddBtn.setOnClickListener(this);//��Ӽ�¼
		restart_call.setOnClickListener(this);//�Զ��ز�
		check_num.setOnClickListener(this);//���˺���
		shutdown_call.setOnClickListener(this);//�Զ��Ҷ�
		check_call.setOnClickListener(this);//������ʾ��
		refuse_msg.setOnClickListener(this);
	
    	//��̬ע��������˵���ҿ��ԡ����ա�2������Action�Ķ�̬�㲥       
    	IntentFilter intentFilter= new IntentFilter();      
    	intentFilter.addAction(inAction2);
    	intentFilter.addAction(inAction); 
    	intentFilter.addAction(SMS_RECEIVE);   
    	intentFilter.setPriority(1000);
    	registerReceiver(refuseReceiver,intentFilter); 
    	//�󶨷���
		bindService(new Intent(MyTelUtilActivity.this, MyTelServices.class), sc, Service.BIND_AUTO_CREATE);
		
		initCofigUI();//��ʼ�����ý���
		selectPicBtn();//��ʼ���绰����
	    getMdata();//��ȡ����
		initGrid();

	}

	/**
	 * �����¼�������
	 */
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.button_call://����Dialer����Button����
			Log.d(TAG, "onClick btnDial:");
			EditText view = (EditText) findViewById(R.id.text_tel_num); //��ȡ�ĺ���
			String phoneNum = view.getText().toString();
			if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNum)) {//����������������Ĵ��Ƿ�����Ч�ĺ���
				if(myTelServices!=null){
					myTelServices.startCall(phoneNum);
				}
			} else {
				if(myTelServices!=null){
					myTelServices.endCall(); 
				}
				Toast.makeText(MyTelUtilActivity.this, "���벻��ȷ������������",
						Toast.LENGTH_LONG).show();// ��Ч�ĺ��룬��ʾ�û��������
			}
			break;		
		case R.id.button_cancel:// ȡ��Dialer����Button����
			Log.d(TAG, "onClick btnCancel:");
			if(myTelServices!=null){
				myTelServices.endCall();
			}
			break;			
		case R.id.button_exit://���ذ�ť�¼�
			exitMethod();
			break;
		case R.id.mets_urine_rate_pic_btn://��绰���
			selectPicBtn();
			break;		
		case R.id.mets_urine_rate_count_btn://���˽���
			selectedCountBtn();
			break;	
		case R.id.button_delete:// ɾ����¼��ť
			deleteTelnum();
			break;		
		case R.id.button_add: // ��Ӽ�¼��ť
			addTelnum();
			break;
		case R.id.restart_call://�Զ��ز�
			MyTelServices.isReCall = ((CheckBox)v).isChecked();
			SharedPreferencesUtils.setConfigBoolean(this, "restart_call", ((CheckBox)v).isChecked());
			break;	
		case R.id.refuse_msg://�Զ��ز�
			RefuseReceiver.refuseMessage = ((CheckBox)v).isChecked();
			SharedPreferencesUtils.setConfigBoolean(this, "refuse_msg_state", ((CheckBox)v).isChecked());
			break;		
			
		case R.id.check_num://���˺���
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
		case R.id.shutdown_call://�Զ��Ҷ�
			if(((CheckBox)v).isChecked()){
				check_call.setChecked(false);
				RefuseReceiver.shoutDown = true;
				SharedPreferencesUtils.setConfigBoolean(this, "check_call", false);
			}
			SharedPreferencesUtils.setConfigBoolean(this, "shutdown_call", ((CheckBox)v).isChecked());
			break;		
		case R.id.check_call: //������ʾ��
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
	 * ��ʼ�����ý���
	 */
	protected void initCofigUI(){
		//��ȡ������Ϣ
		restart_call_state = SharedPreferencesUtils.getConfigBoolean(this, "restart_call");
		check_num_state = SharedPreferencesUtils.getConfigBoolean(this, "check_num");
		shutdown_call_state = SharedPreferencesUtils.getConfigBoolean(this, "shutdown_call");//�Զ��Ҷ�
		check_call_state = SharedPreferencesUtils.getConfigBoolean(this, "check_call_state");//������ʾ��
		refuse_msg_state = SharedPreferencesUtils.getConfigBoolean(this, "refuse_msg_state");//������ʾ��
		
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
	 * ѡ��绰����
	 */
	protected void selectPicBtn(){
		mLinPic.setVisibility(View.VISIBLE);
		mLinCount.setVisibility(View.INVISIBLE);
		mLinPicBtn.setSelected(true);
		mLinCountBtn.setSelected(false);
	}
	
	/**
	 * ѡ���¼����
	 */
	private void selectedCountBtn(){
		mLinPic.setVisibility(View.INVISIBLE);
		mLinCount.setVisibility(View.VISIBLE);
		mLinPicBtn.setSelected(false);
		mLinCountBtn.setSelected(true);
	}	
	
	
	/**
	 * ѡ��绰����
	 */
	protected void deleteTelnum(){
		if(choiceId!=null){
			sqlDb.delete("RefuseTelNum", 
					DBBean.RefuseTel.REFUSE_CALL+"='1' and "+DBBean.RefuseTel.REFUSE_TEL_NUM+"=?", new String[]{String.valueOf(choiceId)});
		    getMdata();//��ȡ����
			initGrid();
		}else{
			
		}
	}

	/**
	 * ѡ��绰����
	 */
	protected void addTelnum(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.add_tel_num,null);
		builder.setTitle("��ӹ��˼�¼");
		builder.setView(newLayout);
		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {  
						EditText nameText = (EditText) newLayout.findViewById(R.id.add_tel_name_text);
		                EditText telText = (EditText) newLayout.findViewById(R.id.add_tel_num_text);
						
		                if(nameText==null||telText.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "�绰���벻��Ϊ��", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                //�ж��Ƿ��Ѿ�����
						Map<String, String> params = new HashMap<String, String>(1);
						params.put(DBBean.RefuseTel.REFUSE_TEL_NUM, telText.getText().toString());
						params.put(DBBean.RefuseTel.REFUSE_CALL, "1");
						try{
							List<String[]> telList = DBOperator.queryList(DBBean.needInitTables.get("RefuseTel"), sqlDb, 
									new String[]{DBBean.RefuseTel.REFUSE_PEOPLE_NAME,DBBean.RefuseTel.REFUSE_TEL_NUM}, params);
							if(telList.size()>0){
			                	Toast.makeText(getApplicationContext(), "�Ѿ����ڸú���", Toast.LENGTH_LONG).show();
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
						    getMdata();//��ȡ����
							initGrid();
						}catch(Exception e){
							
						}
					}
				});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
		
	}
	
	

	/**
	 * ѡ��绰����
	 */
	protected void addSocket(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final LinearLayout newLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.add_socket,null);
		builder.setTitle("���Socket��ַ");
		builder.setView(newLayout);
		
		final EditText ip = (EditText) newLayout.findViewById(R.id.add_ip);
        final EditText port = (EditText) newLayout.findViewById(R.id.add_port);
        
        String ipStr = SharedPreferencesUtils.getConfigString(this, "socket_ip");
        String portStr = SharedPreferencesUtils.getConfigString(this, "socket_port");
        
        ip.setText(ipStr.equals("")?"223.223.5.145":ipStr);
        port.setText(portStr.equals("")?"5555":portStr);
        
        
		
		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {  

						
		                if(ip==null||ip.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "IP����Ϊ��", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                if(port==null||port.getText().toString().trim().equals("")){
		                	Toast.makeText(getApplicationContext(), "port����Ϊ��", Toast.LENGTH_LONG).show();
		                	return ;
		                }
		                SharedPreferencesUtils.setConfigString(MyTelUtilActivity.this, "socket_ip", ip.getText().toString());
		                SharedPreferencesUtils.setConfigString(MyTelUtilActivity.this, "socket_port", port.getText().toString());
					}
				});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
		
	}
	
	/**
	 * ��ѯ����
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
	 * �������ݹ���������
	 */
	public void initGrid(){
        DataGridViewAdapter simpleAdapter = new DataGridViewAdapter();//���ؼ�����������
        //Ϊһ����ά���� �ڶ�ά����Ϊ2,�ֱ��ʾ�е����ƺ��еĿ��
        String[][] mColumnHeaders = new String[][]{
        		{"","40"},
        		{"����","90"},
        		{"����","110"},
//        		{"���ε绰","110"},
//        		{"������Ϣ","110"}
        };
        mDataViews = new ArrayList<ArrayList<View>>(20);
        ArrayList<View> itemView = null;
		int length = telList.size();
		for(int i=0;i<length;i++){ 
			itemView = new ArrayList<View>();
			String[] info = telList.get(i);
			Log.i(TAG,info[0]+":"+choiceId+":"+info[1]);
			//��ѡ����¼���ѡ�У� 
			RadioButton rBtn = new RadioButton(this);//���쵥ѡ��
			rBtn.setTag(info[1]);
			if(choiceId!=null&&info[1].equals(choiceId)){//�ж��Ƿ�ѡ��
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
			
			//���Ӽ�¼��ϸ��Ϣ
			TextView headerDate = new TextView(this);//����
			headerDate.setText(info[0]);
			TextView headerType = new TextView(this);//����
			headerType.setText(info[1]);
			headerDate.setGravity(Gravity.CENTER);
			headerType.setGravity(Gravity.CENTER);
			
			itemView.add(headerDate);
			itemView.add(headerType);
			mDataViews.add(itemView);
		}
        
        simpleAdapter.setMColumnHeaders(mColumnHeaders);//���ñ�ͷ 
        simpleAdapter.setMData(mDataViews);
        urineCountList.setMPageDataAdapter(simpleAdapter);//���ñ������
        urineCountList.buildDatagrid();//�������
        
        //���Ʊ�����λ��
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
		builder.setMessage("ȷ��Ҫ�˳���?");
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(myTelServices!=null){
							myTelServices.endCall();
						}
						stopService(new Intent(MyTelUtilActivity.this, MyTelServices.class));
						unbindService(sc);
						unregisterReceiver(refuseReceiver);    
						finish();// �˳�����
					}
				});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}
	// ���������¼�
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
	

    //��onStart�ж�̬ע��㲥,��ȻҲ������onCreate����ע��   
    @Override    
    protected void onStart() {        
    	// TODO Auto-generated method stub        
    	super.onStart();
    	
		//��ȡ���ݿ����
    	if(sqlDb==null){
    		dbOperator = new DBOperator(this,"RefuseTelDB.db",null,1);
    	}
    	if(dbOperator==null){
    		sqlDb = dbOperator.getReadableDatabase();
    	}
    }
    
    //��onStop��ȡ��ע��㲥,�����onCreate����ע�ᣬ��ô��onDestroy����ȡ��ע�ᡣ    
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