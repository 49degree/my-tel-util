package com.guanri.android.jpos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.network.CommandControl;
import com.guanri.android.jpos.network.CryptionControl;
import com.guanri.android.jpos.pad.ServerDownDataParse;
import com.guanri.android.jpos.pad.ServerUpDataParse;
import com.guanri.android.jpos.pos.data.TerminalLinks.TAndroidCommTerminalLink;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
import com.guanri.android.jpos.pos.data.TerminalParsers.TTerminalParser;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

public class MainActivity extends Activity implements OnClickListener {
	
	EditText log;
	EditText log_info;
	Button btn_query,btn_login,btn_sale,btn_receive;
	final Logger logger = new Logger(MainActivity.class);
	StringBuffer result = new StringBuffer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.querymoney);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_sale = (Button)findViewById(R.id.btn_sale);
		btn_query = (Button)findViewById(R.id.btn_query);
		
		btn_receive = (Button)findViewById(R.id.btn_receive);
		
		log = (EditText)findViewById(R.id.edt_log);
		
		log_info = (EditText)findViewById(R.id.edt_log_info);
		btn_query.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_sale.setOnClickListener(this);
		btn_receive.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_query:
			try{
				TTransaction msgBean = new TTransaction();
				msgBean.TransCode().SetAsInteger(100);
				//构造数据发送对象
				ServerUpDataParse serverParseData = new ServerUpDataParse(msgBean);
				byte[] mab = serverParseData.getMab();//构造MAC BLOCK
				//获取数据包对象
				JposPackageFather jpos = serverParseData.getJposPackage();
				//构造MAK BLOCK
				String makSource = (String)(jpos.getSendMapValue(11))+(String)(jpos.getSendMapValue(13))+
						(String)(jpos.getSendMapValue(12))+(String)(jpos.getSendMapValue(41));
				//获取MAC
				byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
				jpos.setMac(mac);
				
				for(int i=0;i<1;i++){
					CommandControl.getInstance().connect(10000, 1000);
					ServerDownDataParse reData = CommandControl.getInstance().sendUpCommand(serverParseData);//发送数据
					logger.debug("收到数据为++++++++++++++++++:"+TypeConversion.byte2hex(reData.getReturnData()));
					TTransaction returnTransaction = reData.getTTransaction();//取返回POS的对象
					
					//以下为读取返回的数据
					JposUnPackageFather bill =reData.getJposUnPackage();
					
					TreeMap<Integer, Object>  getMap = bill.getMReturnMap();
					TreeMap<String,AdditionalAmounts> amountData = (TreeMap<String,AdditionalAmounts>)getMap.get(54);
					if(amountData.containsKey("02")){
						AdditionalAmounts am = amountData.get("02");
						logger.debug(Integer.parseInt(am.getAmount().trim())+":"+am.getAmountType()+":"+am.getBanlanceType());
						TreeMap<Integer, JposSelfFieldLeaf> tlvData = (TreeMap<Integer, JposSelfFieldLeaf>) getMap.get(61);
						JposSelfFieldLeaf jposSelfFieldLeaf = tlvData.get(5);
						String str = jposSelfFieldLeaf.getValue();
						log.setText(Integer.valueOf(am.getAmount().trim())/100 + "\n 发卡行简介:" + str);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btn_login:
			try{
				TTransaction msgBean = new TTransaction();
				msgBean.TransCode().SetAsInteger(1);
				//构造数据发送对象
				ServerUpDataParse serverParseData = new ServerUpDataParse(msgBean);
				byte[] mab = serverParseData.getMab();//构造MAC BLOCK
				//获取数据包对象
				JposPackageFather jpos = serverParseData.getJposPackage();
				//构造MAK BLOCK
				String makSource = (String)(jpos.getSendMapValue(11))+(String)(jpos.getSendMapValue(13))+
						(String)(jpos.getSendMapValue(12))+(String)(jpos.getSendMapValue(41));
				//获取MAC
				byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
				jpos.setMac(mac);
				
				for(int i=0;i<1;i++){
					CommandControl.getInstance().connect(10000, 1000);
					ServerDownDataParse reData = CommandControl.getInstance().sendUpCommand(serverParseData);//发送数据
					logger.debug("收到数据为++++++++++++++++++:"+TypeConversion.byte2hex(reData.getReturnData()));
					TTransaction returnTransaction = reData.getTTransaction();//取返回POS的对象
					
					//以下为读取返回的数据
					JposUnPackageFather bill =reData.getJposUnPackage();
					TreeMap<Integer, Object>  getMap = bill.getMReturnMap();
					if(getMap.containsKey(39)){
						String str =(String)getMap.get(39);
						logger.debug("响应成功:"+ str);
						result.append("响应结果" + str+ "\n");
						String timeStr = "时间" + (String)getMap.get(12) + "\n";
						String dateStr = "日期" + (String)getMap.get(13) + "\n";
						result.append(dateStr);
						result.append(timeStr);
						
					}
					ArrayList<JposSelfFieldLeaf> datalist = (ArrayList<JposSelfFieldLeaf>) getMap.get(46);
					for (int j = 0; j < datalist.size(); j++) {
						JposSelfFieldLeaf jposSelfFieldLeaf = (JposSelfFieldLeaf)datalist.get(j);
						if(jposSelfFieldLeaf.getTag().equals("0024")){
							//商户名称
							result.append("商户名称" + jposSelfFieldLeaf.getValue()+ "\n");
						}
						if(jposSelfFieldLeaf.getTag().equals("0025")){
							//商户电话1
							result.append("商户电话1" + jposSelfFieldLeaf.getValue()+ "\n");
						}
						if(jposSelfFieldLeaf.getTag().equals("0026")){
							result.append("商户电话2" + jposSelfFieldLeaf.getValue()+ "\n");
						}
						
					}
				}
				
				log.setText(result.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case R.id.btn_sale:
	try{
				
				TTransaction msgBean = new TTransaction();
				//构造数据发送对象
				ServerUpDataParse serverParseData = new ServerUpDataParse(msgBean);
				byte[] mab = serverParseData.getMab();//构造MAC BLOCK
				//获取数据包对象
				JposPackageFather jpos = serverParseData.getJposPackage();
				//构造MAK BLOCK
				String makSource = (String)(jpos.getSendMapValue(11))+(String)(jpos.getSendMapValue(13))+
						(String)(jpos.getSendMapValue(12))+(String)(jpos.getSendMapValue(41));
				//获取MAC
				byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
				jpos.setMac(mac);
				
				for(int i=0;i<1;i++){
					CommandControl.getInstance().connect(10000, 1000);
					ServerDownDataParse reData = CommandControl.getInstance().sendUpCommand(serverParseData);//发送数据
					logger.debug("收到数据为++++++++++++++++++:"+TypeConversion.byte2hex(reData.getReturnData()));
					TTransaction returnTransaction = reData.getTTransaction();//取返回POS的对象
					
					//以下为读取返回的数据
					JposUnPackageFather bill =reData.getJposUnPackage();
					TreeMap<Integer, Object>  getMap = bill.getMReturnMap();
					if(getMap.containsKey(39)){
						String str =(String)getMap.get(39);
						logger.debug("响应成功:"+ str);
						result.append("响应结果" + str+ "\n");
						String timeStr = "时间" + (String)getMap.get(12) + "\n";
						String dateStr = "日期" + (String)getMap.get(13) + "\n";
						result.append(dateStr);
						result.append(timeStr);
						
						String str1 = (String) getMap.get(38);
						logger.debug("授权码:"+ str1);
						result.append(str1 +"\n");
						
					}
				}
				
				log.setText(result.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			break;
		case R.id.btn_receive:
			if(!stopTask&&task!=null){
				stopTask = true;
				task = null;
				btn_receive.setText("打开接收数据");
				updateUI.sendMessage(updateUI.obtainMessage(1, "终端解析器 close..."));
			}else{
				stopTask = false;
				task = new Thread(){
					public void run(){
						TAndroidCommTerminalLink TerminalLink = new TAndroidCommTerminalLink();
						TerminalLink.CommName = "/dev/ttyUSB0";
						TerminalLink.ReadTimeout = 5000;
						try{
							TerminalLink.Connect();

							TTerminalParser TerminalParser = new TTerminalParser();
							TerminalParser.SetTerminalLink(TerminalLink);

							updateUI.sendMessage(updateUI.obtainMessage(1, "终端解析器正在运行..."));
							
							TTerminalParser.LOG_INFO =TTerminalParser.LOG_INFO+"\n is ok";
							
							while (!stopTask) {
								TerminalParser.ParseRequest();
							}
							showInfoTask.start();
						}catch(SecurityException se){
							updateUI.sendMessage(updateUI.obtainMessage(1, "open comm failed..."));
						}catch(Exception e){
							updateUI.sendMessage(updateUI.obtainMessage(1, "comm failed...:"+e.getMessage()));
						}

					}
				};
				task.start();
				btn_receive.setText("关闭接收数据");
			}
			

			break;
		default:
			break;
		}
	}
	
	private Thread task = null;
	public boolean stopTask = true;
	
    /**
     * 回调更新界面
     */
    public Handler updateUI = new Handler(){
        public void handleMessage(Message msg) {
        	if(msg.what==1&&log!=null){
        		log.setText((String)msg.obj);
        	}else if(msg.what==2){
        		log_info.setText((String)msg.obj);
        	}if(msg.what==3){
        		
        	}
        }
    };
    
    private Thread showInfoTask = new Thread(){
    	public void run(){
    		updateUI.sendMessage(updateUI.obtainMessage(1, "open comm failed..."));
			try{
				while (!stopTask) {
					if(TTerminalParser.LOG_INFO!=null&&TTerminalParser.LOG_INFO.length()>250){
						updateUI.sendMessage(updateUI.obtainMessage(2, TTerminalParser.LOG_INFO.substring(TTerminalParser.LOG_INFO.length()-250, 250)));
					}else{
						updateUI.sendMessage(updateUI.obtainMessage(2, TTerminalParser.LOG_INFO));
					}
					Thread.sleep(500);
				}
			}catch(Exception e){
				updateUI.sendMessage(updateUI.obtainMessage(2, "read log_info failed...:"+e.getMessage()));
			}
    	}
    };
	
}
