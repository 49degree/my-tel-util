package com.xys.ecg.activity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xys.ecg.device.DeviceRation;
import com.xys.ecg.device.SocketServerTest;
import com.xys.ecg.device.ThreadReadWriterIOSocket;
import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.log.Logger;
import com.xys.ecg.network.SoapTool;
import com.xys.ecg.utils.TypeConversion;

public class Equip_Config extends Activity {
	public static final String TAG = "Equip_Config";  
    public   static  String START_ACTION =  "NotifyServiceStart" ; //��ʼ�㲨�¼�  
    public   static  String STOP_ACTION =  "NotifyServiceStop" ; //ֹͣ�㲨�¼�
    private ServerSocket serverSocket = null; //������SOCKET���� 
    public final int SERVER_PORT = 10086; //�������˿�  
    private ServiceBroadcastReceiver serviceBroadcastReceiver = null;
    MainHandler mhd = null;
	
	
	private EditText etBTAddress;
	private EditText etOutWebAddress;
	private EditText etCommision;
	private Button btOK;
	private Button btCancel;
	public static Logger logger = Logger.getLogger(Equip_Config.class);
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equip_config);
       
        etBTAddress = (EditText)findViewById(R.id.ET_BTAddress);
        etOutWebAddress= (EditText)findViewById(R.id.ET_OutWebAddress);
        etCommision= (EditText)findViewById(R.id.ET_Commision);
        btOK = (Button)findViewById(R.id.OK);
        btCancel = (Button)findViewById(R.id.Cancel);
        //��ʾ������ַ
        String BTAddress = null;
        String strCommision = null;
        String strOutWebAddress = null;
        String strInWebAddress = null;
        
        EcgXmlFileOperate XMLConfig =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
		try {
		
			BTAddress = XMLConfig.selectEcgXmlNode("CollectorBtAddr").getParentNodeAttributeValue();
			strCommision = XMLConfig.selectEcgXmlNode("ValidateCode").getParentNodeAttributeValue();//��Ȩ��
			strOutWebAddress = XMLConfig.selectEcgXmlNode("WebServiceAddrOfWan").getParentNodeAttributeValue();//����
			strInWebAddress = XMLConfig.selectEcgXmlNode("WebServiceAddrOfLan").getParentNodeAttributeValue();//����
			XMLConfig.close();
		} catch (Exception e) {
			logger.debug("Get BTAddress failed!");
		}
        etBTAddress.setText(BTAddress);
        etCommision.setText(strCommision);
        etOutWebAddress.setText(strOutWebAddress);
        //��ʾҵ����Ȩ���ַ
        
        
        btOK.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//�洢������ַ
				EcgXmlFileOperate xmlOperate =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
				try {
					xmlOperate.updateEcgXmlCurrentNode("CollectorBtAddr", etBTAddress.getText().toString());
					xmlOperate.updateEcgXmlCurrentNode("ValidateCode", etCommision.getText().toString());
					xmlOperate.updateEcgXmlCurrentNode("WebServiceAddrOfWan", etOutWebAddress.getText().toString());
					xmlOperate.updateEcgXmlCurrentNode("WebServiceAddrOfLan", etOutWebAddress.getText().toString());
					xmlOperate.close();
					
					SoapTool.initSoapInfo();//��������
					
				} catch (Exception e) {
					logger.debug("Write configFile failed ");
					e.printStackTrace();
				}
				finish();
			}
        	
        });
        
        btCancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
				
			}
        	
        }); 
	}
	
	
	
	/****************************************************************************************
	*����λͨ��WEB�˷����豸��أ���ʱ�ò���
	****************************************************************************************/
    /**
     * ��onStart�ж�̬ע��㲥,��ȻҲ������onCreate����ע��
     */
    @Override
    protected void onStart() {
    	super.onStart();
    	System.out.println("onStart"); 
    	
    	mhd = new MainHandler();
    	
        serviceBroadcastReceiver = new ServiceBroadcastReceiver(); // ����һ��BroadcastReceiver����  
        IntentFilter filter = new IntentFilter(); // ����һ��IntentFilter����  
        filter.addAction(SocketServerTest.START_ACTION); // ΪIntentFilter�������һ��Action
        filter.addAction(SocketServerTest.START_ACTION); // ΪIntentFilter�������һ��Action
        registerReceiver(serviceBroadcastReceiver, filter);//ע�����Receiver  
        System.out.println("registerReceiver Success"); 
    }
    
    
	@Override
	public void onDestroy(){
		super.onDestroy();
		unregisterReceiver(serviceBroadcastReceiver);//ȡ���¼�  
		try { 
	        if(serverSocket!=null){
	        	serverSocket.close();
	        	serverSocket = null;
	        }
        } catch (IOException e1) { 
            e1.printStackTrace();  
        }
		System.out.println("unregisterReceiver Success");  
	}
	
	/**
	 * �����㲥
	 * @author Administrator
	 *
	 */
	public   class  ServiceBroadcastReceiver  extends  BroadcastReceiver {   
	    @Override    
	    public   void  onReceive(Context context, Intent intent) {   
	        Log.d(TAG, Thread.currentThread().getName() + "---->" + "ServiceBroadcastReceiver onReceive" );   
	        String action = intent.getAction();   
	        try { 
		        if  (SocketServerTest.START_ACTION.equalsIgnoreCase(action)) {
		            
	            	if(serverSocket == null){
		                Log.d(TAG, Thread.currentThread().getName() + "---->"   + " Receiver new serverSocket");  
		                serverSocket = new ServerSocket(SERVER_PORT);   
			            //����Socket�����߳�
			            new Thread() {  
			                public void run() {
			                	Looper.prepare();
			                    doListen();  
			                };  
			            }.start();
	            	}


	        } else   if  (SocketServerTest.STOP_ACTION.equalsIgnoreCase(action)) {  
	            Log.d(TAG, Thread.currentThread().getName() + "---->"    
	                    + "ServiceBroadcastReceiver onReceive stop end" );
	            if(serverSocket!=null){
	            	serverSocket.close();
	            	serverSocket = null;
	            }
	        }  
            } catch (IOException e1) {  
                Log.v(TAG, Thread.currentThread().getName()   + "---->" + "new serverSocket error");  
                e1.printStackTrace();  
            }
 
	    }   
	} 
	
	/**
	 * ��ȡ����
	 */
    private void doListen() {  
        Log.d(TAG, Thread.currentThread().getName() + "---->"   + " doListen() START");   
        try {
        	while(true){
                Socket client = serverSocket.accept();  
                new Thread(new ThreadReadWriterIOSocket(this, client,mhd)).start();  
        	}

        } catch (IOException e1) {
            e1.printStackTrace();  
        }  
    } 
    
    public class MainHandler extends Handler{
		public MainHandler() {
			super();
		}
		
		public void handleMessage(Message msg) {
			DeviceRation deviceRation = new DeviceRation((byte[])msg.obj);
			//�洢������ַ
			EcgXmlFileOperate xmlOperate =  new EcgXmlFileOperate("Device",ECGApplication.getInstance());
			try {
				String collectorBtAddr = new String(TypeConversion.bytesToChars(deviceRation.getSzCollectorBtAddr()));
				String szValidateCode = new String(TypeConversion.bytesToChars(deviceRation.getSzValidateCode()));
				String szWebServiceAddrOfWan = new String(TypeConversion.bytesToChars(deviceRation.getSzWebServiceAddrOfLan()));
				
		        etBTAddress.setText(collectorBtAddr);
		        etCommision.setText(szValidateCode);
		        etOutWebAddress.setText(szWebServiceAddrOfWan);
				
//				xmlOperate.updateEcgXmlCurrentNode("CollectorBtAddr", szWebServiceAddrOfWan);
//				xmlOperate.updateEcgXmlCurrentNode("ValidateCode", szValidateCode);
//				xmlOperate.updateEcgXmlCurrentNode("WebServiceAddrOfWan", szWebServiceAddrOfWan);
//				xmlOperate.updateEcgXmlCurrentNode("WebServiceAddrOfLan", szWebServiceAddrOfWan);
				SoapTool.initSoapInfo();//��������
			} catch (Exception e) {
				logger.debug("Write configFile failed ");
				e.printStackTrace();
			}finally{
				xmlOperate.close();
			}
    	}
    }
}
