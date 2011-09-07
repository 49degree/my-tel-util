package com.guanri.android.jpos;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.pos.PosCommandControlFactory;
import com.guanri.android.jpos.pos.PosCommandControlImp;
import com.guanri.android.jpos.pos.PosCommandControlImp.SendDataResultListener;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

public class Main extends Activity {
	Logger logger = Logger.getLogger(Main.class);
	
	PosCommandControlImp posCommandControlImp = null;
	
	EditText info = null;
	EditText read_info = null;  
	Button send = null;
	Button open = null;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main); 
        info = (EditText)this.findViewById(R.id.info);
        read_info = (EditText)this.findViewById(R.id.read_info);
        send = (Button)this.findViewById(R.id.send); 
        
        open = (Button)this.findViewById(R.id.open); 
        final byte[] msg = {0X55,0X55,0X55,0X55,0X55,0X08,0X13,0X00,0X02,0X01,0X01,0X00,0X00,0X30,0X30,0X31,
    			0X30,0X00,0X30,0X02,(byte)0X90,(byte)0XBE,0X00,0X00,(byte)0XB4,0X08};
        
        info.setText(TypeConversion.byte2hex(msg));
        
        open.setOnClickListener(new android.view.View.OnClickListener(){
        	public void onClick(View v){
        		if(posCommandControlImp==null){
        			try{
        				posCommandControlImp = PosCommandControlFactory.getPosCommandControl();
        				//send.setClickable(true);
        				open.setText("关闭串口");
        			}catch(SecurityException se){
        				displayError("打开通讯端口失败");
        			}
        			
        		}else{
        			posCommandControlImp.portClose();
        			posCommandControlImp = null;
        			open.setText("打开串口");
        			//send.setClickable(false);
        		}
        	}
        }); 
        
        
        logger.error("开始了");
        send.setOnClickListener(new android.view.View.OnClickListener(){
        	public void onClick(View v){
        		if(posCommandControlImp!=null){
            		try {
            			logger.error("开始发送数据："+TypeConversion.byte2hex(msg));
            			posCommandControlImp.sendData(msg,new SendDataResultListener(){
            				public void onSendDataResult(byte[] returnData){ 
        						updateUI.sendMessage(updateUI.obtainMessage(1, TypeConversion.byte2hex(returnData)));
            				}
            			},1000);
            			logger.error("发送数据结束");
                      }catch(IOException ex){
                    	  displayError("发送数据异常，请重新连接端口");
                    	  logger.error("发送数据出现异常2"+ex.getMessage());
                  		if(posCommandControlImp!=null){
                			posCommandControlImp.portClose();//关闭端口
                			posCommandControlImp = null;
                			open.setText("打开串口");
                		}
                      }
        		}else{
        			displayError("请先打开端口");
        		}
        		

        	}
        });
		
	}
	
	

	
	
    /**
     * 回调更新界面
     */
    public Handler updateUI = new Handler(){
        public void handleMessage(Message msg) {
        	if(msg.what==1&&read_info!=null){
        		read_info.setText((String)msg.obj);
        	}else if(msg.what==2){
        		
        	}if(msg.what==3){
        		
        	}
        }
    };

	private void displayError(int resourceId) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(resourceId);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Main.this.finish();
			}
		});
		b.show();
	}

	private void displayError(String msg) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Error");
		b.setMessage(msg);
		b.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		b.show();
	}
	
	@Override
	protected void onDestroy() {
		if(posCommandControlImp!=null){
			posCommandControlImp.portClose();//关闭端口
			posCommandControlImp = null;
		}
		
		super.onDestroy();
	}
}