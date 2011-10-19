package com.xys.ecg.device;

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

import com.xys.ecg.activity.R;

public class SocketServerTest extends Activity {
	
	public static final String TAG = "SocketServerTest";  
    public   static  String START_ACTION =  "NotifyServiceStart" ;   
    public   static  String STOP_ACTION =  "NotifyServiceStop" ; 
    private ServerSocket serverSocket = null;  
    final int SERVER_PORT = 10086;  
    
    MainHandler mhd = null;
    
	ServiceBroadcastReceiver serviceBroadcastReceiver = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mhd = new MainHandler();
    }
    
    //��onStart�ж�̬ע��㲥,��ȻҲ������onCreate����ע��
    @Override
    protected void onStart() {
    	super.onStart();
    	System.out.println("onStart"); 
    	
        serviceBroadcastReceiver = new ServiceBroadcastReceiver(); // ����һ��BroadcastReceiver����  
        IntentFilter filter = new IntentFilter(); // ����һ��IntentFilter����  
        filter.addAction(SocketServerTest.START_ACTION); // ΪIntentFilter�������һ��Action
        filter.addAction(SocketServerTest.START_ACTION); // ΪIntentFilter�������һ��Action
        SocketServerTest.this.registerReceiver(serviceBroadcastReceiver, filter);//ע�����Receiver  
        System.out.println("registerReceiver Success"); 
    }
    
    
	@Override
	public void onDestroy(){
		super.onDestroy();
		SocketServerTest.this.unregisterReceiver(serviceBroadcastReceiver);  
		System.out.println("unregisterReceiver Success");  
	}
	
	public   class  ServiceBroadcastReceiver  extends  BroadcastReceiver {   
	    @Override    
	    public   void  onReceive(Context context, Intent intent) {   
	        Log.d(TAG, Thread.currentThread().getName() + "---->" + "ServiceBroadcastReceiver onReceive" );   
	        String action = intent.getAction();   
	        if  (SocketServerTest.START_ACTION.equalsIgnoreCase(action)) {
	            try { 
	            	if(serverSocket == null){
		                Log.d(TAG, Thread.currentThread().getName() + "---->"   + " Receiver new serverSocket");  
		                serverSocket = new ServerSocket(SERVER_PORT);   
	            	}
	            } catch (IOException e1) {  
	                Log.v(TAG, Thread.currentThread().getName()   + "---->" + "new serverSocket error");  
	                e1.printStackTrace();  
	            }
	            //����Socket�����߳�
	            new Thread() {  
	                public void run() {
	                	Looper.prepare();
	                    doListen();  
	                };  
	            }.start();
	        } else   if  (SocketServerTest.STOP_ACTION.equalsIgnoreCase(action)) {  
	            Log.d(TAG, Thread.currentThread().getName() + "---->"    
	                    + "ServiceBroadcastReceiver onReceive stop end" );
	            serverSocket = null;
	        }   
	    }   
	} 
	
	/**
	 * ��ȡ����
	 */
    private void doListen() {  
        Log.d(TAG, Thread.currentThread().getName() + "---->"   + " doListen() START");   
        try {   
            Socket client = serverSocket.accept();  
            new Thread(new ThreadReadWriterIOSocket(this, client,mhd)).start();  
        } catch (IOException e1) {
            e1.printStackTrace();  
        }  
    } 
    
    public class MainHandler extends Handler{
		public MainHandler() {
			super();
		}
		
		public void handleMessage(Message msg) {
//    	    EditText m_editAddParam1 = (EditText)findViewById(R.id.edit_add_param1);
//    	    EditText m_editAddParam2 = (EditText)findViewById(R.id.edit_add_param2);
//    	    m_editAddParam1.setText((String)msg.obj);
    	}
    }
    

}