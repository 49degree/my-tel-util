package com.guanri.android.jpos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.bean.AdditionalAmounts;
import com.guanri.android.jpos.bean.PosMessageBean;
import com.guanri.android.jpos.common.CommandConstant;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.bill99.JposMessageType99Bill;
import com.guanri.android.jpos.iso.bill99.JposPackage99Bill;
import com.guanri.android.jpos.iso.bill99.JposUnPackage99Bill;
import com.guanri.android.jpos.network.CommandControl;
import com.guanri.android.jpos.network.CryptionControl;
import com.guanri.android.jpos.pad.ServerUpDataParse;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
	EditText log;
	Button btn_query,btn_login,btn_sale;
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
		
		log = (EditText)findViewById(R.id.edt_log);
		btn_query.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_sale.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_query:
		try{
			PosMessageBean msgBean = new PosMessageBean();
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
				byte[] reData = CommandControl.getInstance().sendUpCommand(serverParseData);
				logger.debug("请求数据++++++++++++++++++:"+TypeConversion.byte2hex(reData));
				JposUnPackage99Bill bill = new JposUnPackage99Bill(reData);
				bill.unPacketed();
				
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
		} catch (CommandParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PacketException e) {
			e.printStackTrace();
		}
			break;
		case R.id.btn_login:
			try{
				
				PosMessageBean msgBean = new PosMessageBean();
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
					byte[] reData = CommandControl.getInstance().sendUpCommand(serverParseData);
					logger.debug("请求数据++++++++++++++++++:"+TypeConversion.byte2hex(reData));
					JposUnPackage99Bill bill = new JposUnPackage99Bill(reData);
					bill.unPacketed();
					
					TreeMap<Integer, Object>  getMap = bill.getMReturnMap();
					if(getMap.containsKey(39)){
						String str =TypeConversion.result((String)getMap.get(39));
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
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PacketException e) {
				e.printStackTrace();
			}

			break;
		case R.id.btn_sale:
			try{
				
				PosMessageBean msgBean = new PosMessageBean();
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
					byte[] reData = CommandControl.getInstance().sendUpCommand(serverParseData);
					
					if(reData != null){
						logger.debug("请求数据++++++++++++++++++:"+TypeConversion.byte2hex(reData));
					JposUnPackage99Bill bill = new JposUnPackage99Bill(reData);
					bill.unPacketed();
					
					TreeMap<Integer, Object>  getMap = bill.getMReturnMap();
					if(getMap.containsKey(39)){
						String str =TypeConversion.result((String)getMap.get(39));
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
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CommandParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PacketException e) {
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}
	
	
	
	
}
