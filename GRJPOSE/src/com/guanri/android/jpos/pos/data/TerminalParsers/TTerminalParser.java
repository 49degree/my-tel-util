package com.guanri.android.jpos.pos.data.TerminalParsers;

import com.guanri.android.exception.CommandParseException;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.iso.JposSelfFieldLeaf;
import com.guanri.android.jpos.iso.JposUnPackageFather;
import com.guanri.android.jpos.network.CommandControl;
import com.guanri.android.jpos.network.CryptionControl;
import com.guanri.android.jpos.pad.ServerDownDataParse;
import com.guanri.android.jpos.pad.ServerUpDataParse;
import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.Stream;
import com.guanri.android.jpos.pos.data.Fields.TField;
import com.guanri.android.jpos.pos.data.Fields.TFieldList.TResult_LoadFromBytes;
import com.guanri.android.jpos.pos.data.Fields.TFieldList.TResult_SaveToBytes;
//import com.guanri.android.jpos.pos.data.Fields.TFieldList.TResult_SaveToBytes;
import com.guanri.android.jpos.pos.data.TerminalLinks.TTerminalLink;
import com.guanri.android.jpos.pos.data.TerminalMessages.*;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.TypeConversion;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TreeMap;

public class TTerminalParser {
	public static String LOG_INFO = "";
	protected TTerminalLink FTerminalLink;
	protected int FIdent = 0;
	protected int FLastSerialNumber = 0;  //最后一次的流水号
	protected String FLastMerchantID = null;  //最后一次的商户号
	protected String FLastTerminalID = null;  //最后一次的终端号
	protected String FLastUserID = null;  //最后一次的操作员ID
	
	final Logger logger = new Logger(TTerminalParser.class);
	
	protected enum TTermState {
		Offline, Online
	};
	
	protected TTermState TermState;
	
	public void SetTerminalLink(TTerminalLink Value) {
		FTerminalLink = Value;
	}
	protected int GetNewIdent() {   //获取新的终端识别码
		Random rnd = new Random();
		FIdent = rnd.nextInt(65535);
		return FIdent;
	}
	
	protected String GetNowDateTime(){   // YYYYMMDDhhmmss
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddkkmmss");
		return df.format(date);
	}
	
	public TTerminalParser() {
		//GetNewIdent();
		TermState = TTermState.Offline;
	}
	
	private static final byte ws_WillConnect = 1;
	private static final byte ws_ErrorIndet = 3;
	
	private static final byte ws_ErrorConnect = 4;
	private static final byte ws_ErrorRecv = 10;
	private static final byte ws_ErrorBuild = 4;
	
	protected void UpdateWorkingStatus(byte AStatus) {    //检测工作状态
		TWorkingStatus WorkingStatus = new TWorkingStatus();
		WorkingStatus.Status().SetAsInteger(AStatus & 0xFF);
		Stream.SetBytes(null);
		WorkingStatus.SaveToBytes();
		FTerminalLink.SendPackage(Stream.Bytes);
	}
	protected boolean CheckWorkingStatus(byte AStatus)  {//检测回送的工作状态
		TWorkingStatus WorkingStatus = new TWorkingStatus();
		byte[] Bytes = FTerminalLink.RecvPackage();
		if (Common.Length(Bytes) <= 0) return false;
		Stream.SetBytes(Bytes);
		if (WorkingStatus.LoadFormBytes() != TResult_LoadFromBytes.rfll_NoError) return false;
		return WorkingStatus.Status().GetAsInteger() == (AStatus & 0xFF);
	}
	
	public void ParseRequest() {
		if (FTerminalLink == null) return;
		
		byte[] Bytes = null;
		
		Bytes = FTerminalLink.RecvPackage();
		if(Common.Length(Bytes) < 3) return;
		byte MsgType = Bytes[0];
		
		if (MsgType == 0) {  //数据传输
			byte CmdID = Bytes[2];
			switch (CmdID) {
			case 6:
				System.out.println("握手, 时间同步");
				
				THandshake_Response Handshake_Response = new THandshake_Response();
				
				Handshake_Response.DateTime().SetAsString(GetNowDateTime());
				Handshake_Response.SerialNumber().SetAsInteger(FLastSerialNumber);
				Handshake_Response.Ident().SetAsInteger(GetNewIdent());
				
				Stream.SetBytes(null);
				Handshake_Response.SaveToBytes();
				FTerminalLink.SendPackage(Stream.Bytes);			
				
				break;
			}
			return;
		}
		
		if (MsgType == 1) {
			System.out.println("交易报文");
			TTransaction Transaction = new TTransaction();
			
			
			Transaction.ClearProcess(); //清空流程
			
			Stream.SetBytes(Bytes);
			if (Transaction.LoadFormBytes() != TResult_LoadFromBytes.rfll_NoError) //报文格式错误
				return; 
			
			if (! Transaction.CheckMAC()){ //MAC签名错误
				System.out.println("MAC签名错误");
				return; 
			}	
			
			if (Transaction.Ident().GetAsInteger() != FIdent) {   //识别码不匹配
				System.out.println("识别码不匹配");
				UpdateWorkingStatus(ws_ErrorIdent);
				return; 
			}
			
			int TransCode = Transaction.TransCode().GetAsInteger();
			switch (TransCode) {
			case 1:
			case 100:
			case 200:	
				break;
			default:
				System.out.println("不能识别的交易代码: " + TransCode);
				return; 
			}
			
			if (! Transaction.LoadProcess()){ //导入流程错误
				System.out.println("导入流程错误");
				return; 
			}	
			
	
			
			if (Transaction.TransCode().GetAsInteger() == 1) {   //签到
				FLastMerchantID = Transaction.ProcessList.MerchantID().GetAsString();
				FLastTerminalID = Transaction.ProcessList.TerminalID().GetAsString();
				FLastUserID = Transaction.ProcessList.UserID().GetAsString();
			} 
			else {
				Transaction.ProcessList.MerchantID().SetAsString(FLastMerchantID);
				Transaction.ProcessList.TerminalID().SetAsString(FLastTerminalID);
				Transaction.ProcessList.UserID().SetAsString(FLastUserID);
			}
			
			FLastSerialNumber = Transaction.SerialNumber().GetAsInteger();
			
			
				
			System.out.println("[请求]流水号: " + Transaction.SerialNumber().GetAsString());
			System.out.println("[请求]交易代码: " + Transaction.TransCode().GetAsInteger());
			System.out.println("[请求]年:" + Transaction.Year().GetAsString());
			System.out.println("[请求]日期:" + Transaction.Date().GetAsString());
			System.out.println("[请求]时间:" + Transaction.Time().GetAsString());
			System.out.println("[请求]2磁道数据: " + Transaction.ProcessList.GetTrack2Data());
			System.out.println("[请求]3磁道数据: " + Transaction.ProcessList.GetTrack3Data());
			System.out.println("[请求]卡号: " + Transaction.ProcessList.GetPAN());
			
			System.out.println("[请求]商户号: " + Transaction.ProcessList.MerchantID().GetAsString());
			System.out.println("[请求]终端号: " + Transaction.ProcessList.TerminalID().GetAsString());
			System.out.println("[请求]操作员ID: " + Transaction.ProcessList.UserID().GetAsString());
			
			UpdateWorkingStatus(ws_WillConnect);
			if (!CheckWorkingStatus(ws_WillConnect)) {
				System.out.println("更新工作状态错误");
				return; 
			}
			
			
			//******************************************************
			
			ServerUpDataParse serverParseData = null;
			try {
				serverParseData = new ServerUpDataParse(Transaction);
				byte[] mab = serverParseData.getMab();//构造MAC BLOCK
				//获取数据包对象
				JposPackageFather jpos = serverParseData.getJposPackage();
				//构造MAK BLOCK
				String makSource = (String)(jpos.getSendMapValue(11))+(String)(jpos.getSendMapValue(13))+
						(String)(jpos.getSendMapValue(12))+(String)(jpos.getSendMapValue(41));
				//获取MAC
				byte[] mac = CryptionControl.getInstance().getMac(mab,makSource);
				jpos.setMac(mac);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("构建数据包错误");
				UpdateWorkingStatus(ws_ErrorBuild);
				return;
			}

			try {
				CommandControl.getInstance().connect(10000, 20000);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("连接后台错误");
				UpdateWorkingStatus(ws_ErrorConnect);
				return;
			}
			
			
			try {
				
				ServerDownDataParse reData = CommandControl.getInstance().sendUpCommand(serverParseData);//发送数据
				
				Transaction = reData.getTTransaction();//取返回POS的对象

				Transaction.Ident().SetAsInteger(FIdent);
				
				System.out.println("[响应]流水号: " + Transaction.SerialNumber().GetAsString());
				System.out.println("[响应]交易代码: " + Transaction.TransCode().GetAsInteger());
				System.out.println("[响应]年:" + Transaction.Year().GetAsString());
				System.out.println("[响应]日期:" + Transaction.Date().GetAsString());
				System.out.println("[响应]时间:" + Transaction.Time().GetAsString());
				System.out.println("[响应]应答: " + Transaction.ProcessList.Response().GetAsString());
				System.out.println("[响应]商户名称: " + Transaction.ProcessList.MerchantName().GetAsString());			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("接收错误");
				UpdateWorkingStatus(ws_ErrorRecv);
				return;
			}
			
			
			//******************************************************
			
			
			
			/*
			Transaction.ClearProcess(); //清空流程
			switch (Transaction.TransCode().GetAsInteger()) {
			case 1:
				Transaction.ProcessList.Response().SetAsString("00签到成功" );
				Transaction.ProcessList.MerchantName().SetAsString("太平洋公司");
				break;
			case 100:
				Transaction.ProcessList.Response().SetAsString("00余额为"+888666.55);
				break;
			default:
				Transaction.ProcessList.Response().SetAsString("00交易成功" );
				break;
			}
			*/
			
			Transaction.SaveProcess();
			Transaction.SaveMAC();
			Stream.SetBytes(null);
			if (Transaction.SaveToBytes() != TResult_SaveToBytes.rfls_NoError) {
				System.out.println("保存响应数据错误!");
				return;
			};	
			
			System.out.println("已发送响应数据.");
			FTerminalLink.SendPackage(Stream.Bytes);
			
			
		}
	}

}
