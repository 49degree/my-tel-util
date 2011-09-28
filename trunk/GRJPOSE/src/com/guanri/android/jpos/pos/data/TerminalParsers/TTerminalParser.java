package com.guanri.android.jpos.pos.data.TerminalParsers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.guanri.android.exception.PacketException;
import com.guanri.android.jpos.iso.JposPackageFather;
import com.guanri.android.jpos.network.CommandControl;
import com.guanri.android.jpos.pad.ServerDownDataParse;
import com.guanri.android.jpos.pad.ServerUpDataParse;
import com.guanri.android.jpos.pos.data.Common;
import com.guanri.android.jpos.pos.data.TStream;
import com.guanri.android.jpos.pos.data.Fields.TFieldList.TResult_LoadFromBytes;
import com.guanri.android.jpos.pos.data.Fields.TFieldList.TResult_SaveToBytes;
import com.guanri.android.jpos.pos.data.TerminalLinks.TTerminalLink;
import com.guanri.android.jpos.pos.data.TerminalMessages.TEncryptMAC_Recv;
import com.guanri.android.jpos.pos.data.TerminalMessages.TEncryptMAC_Send;
import com.guanri.android.jpos.pos.data.TerminalMessages.THandshake_Response;
import com.guanri.android.jpos.pos.data.TerminalMessages.TTransaction;
import com.guanri.android.jpos.pos.data.TerminalMessages.TWorkingStatus;
import com.guanri.android.lib.log.LogInfo;
import com.guanri.android.lib.log.Logger;

public class TTerminalParser {
	public static String LOG_INFO = "";
	protected TTerminalLink FTerminalLink;
	protected int FIdent = 0;
	protected int FLastSerialNumber = 0; // 最后一次的流水号
	protected String FLastMerchantID = null; // 最后一次的商户号
	protected String FLastTerminalID = null; // 最后一次的终端号
	protected String FLastUserID = null; // 最后一次的操作员ID
	protected String FLastReferenceNumber = null; // 最后一次的POS中心流水号

	final Logger logger = new Logger(TTerminalParser.class);

	/*
	protected enum TTermState {
		Offline, Online
	};

	
	protected TTermState TermState;
//*/
	
	public void SetTerminalLink(TTerminalLink Value) {
		FTerminalLink = Value;
	}

	protected int GetNewIdent() { // 获取新的终端识别码
		Random rnd = new Random();
		FIdent = rnd.nextInt(65535);
		return FIdent;
	}

	protected String GetNowDateTime() { // YYYYMMDDhhmmss
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddkkmmss");
		return df.format(date);
	}

	public TTerminalParser() {
		// GetNewIdent();
		//TermState = TTermState.Offline;
	}

	private static final byte ws_WillConnect = 1;  //
	private static final byte ws_ErrorIdent = 3;   //

	private static final byte ws_ErrorConnect = 4; //
	private static final byte ws_NoOrderNumber = 11; //
	private static final byte ws_ErrorBuild = 11; 
	
	private static final byte ws_ErrorRecv = 10;  //引发冲正
	

	protected void UpdateWorkingStatus(byte AStatus) { // 检测工作状态
		TWorkingStatus WorkingStatus = new TWorkingStatus();
		WorkingStatus.Status().SetAsInteger(AStatus & 0xFF);
		TStream Stream = new TStream(null);
		WorkingStatus.SaveToBytes(Stream);
		FTerminalLink.SendPackage(Stream.Bytes);
		PutLog("更新工作状态" + AStatus);
	}

	protected boolean CheckWorkingStatus(byte AStatus) {// 检测回送的工作状态
		TWorkingStatus WorkingStatus = new TWorkingStatus();
		byte[] Bytes = FTerminalLink.RecvPackage();
		if (Common.Length(Bytes) <= 0)
			return false;
		TStream Stream = new TStream(Bytes);
		if (WorkingStatus.LoadFormBytes(Stream) != TResult_LoadFromBytes.rfll_NoError) {
			//PutLog("没有收到回送的工作状态");
			return false;
		}
			
		return WorkingStatus.Status().GetAsInteger() == (AStatus & 0xFF);
	}

	protected boolean IsAllowTrans(TTransaction Transaction) {
		int TransCode = Transaction.TransCode().GetAsInteger();
		switch (TransCode) {
		case 1: // 签到
		case 100: // 余额查询
		case 200: // 消费
		case 600: // 订单查询
		case 601: // 订单付款
		case 7: // 交易回执
		case 6: // 批结算
		case 4: // 冲正	
		case 8: //手动交易回执	
			return true;
			// break;
		default:
			return false;
		}
	}

	protected boolean IsEncryptMACTrans(TTransaction Transaction) {
		int TransCode = Transaction.TransCode().GetAsInteger();
		switch (TransCode) {
		case 100:
		case 200:
		case 600:
		case 601:
		case 7:
		case 4: // 冲正	
		case 8: //手动交易回执		
			return true;
			// break;
		default:
			return false;
		}
	}

	protected boolean IsFillZeroTrans(TTransaction Transaction) {
		int TransCode = Transaction.TransCode().GetAsInteger();
		switch (TransCode) {
		case 600:
			return true;

		default:
			return false;
		}
	}

	protected byte[] EncryptMAC(TTransaction Transaction, byte[] MAB,
			boolean IsFill) {

		TEncryptMAC_Send MAC_Send = new TEncryptMAC_Send();
		TEncryptMAC_Recv MAC_Recv = new TEncryptMAC_Recv();

		MAC_Send.MAB().SetData(MAB); // MAB
		if (!IsFill) {
			MAC_Send.Year().SetAsString(Transaction.Year().GetAsString()); // Year
			MAC_Send.Date().SetAsString(Transaction.Date().GetAsString()); // Date
			MAC_Send.Time().SetAsString(Transaction.Time().GetAsString()); // Time
		} else {
			MAC_Send.Year().SetAsInteger(0);
			MAC_Send.Date().SetAsInteger(0);
			MAC_Send.Time().SetAsInteger(0);
		}

		TStream Stream = new TStream(null);
		MAC_Send.SaveToBytes(Stream);
		FTerminalLink.SendPackage(Stream.Bytes); // 发送
		
		PutLog("MAB = " + Common.ToHex(Stream.Bytes));
		
		byte[] Bytes = FTerminalLink.RecvPackage();

		Stream = new TStream(Bytes);
		if (MAC_Recv.LoadFormBytes(Stream) != TResult_LoadFromBytes.rfll_NoError) { // MAC回复出错
			// PutLog("MAC回复出错");
			return null;
		}
		return MAC_Recv.MAC().GetData();
	}

	
	public void ParseRequest() {
		if (FTerminalLink == null)
			return;

		byte[] Bytes = null;
		
		
		Bytes = FTerminalLink.RecvPackage();
		if (Common.Length(Bytes) < 3)
			return;
		byte MsgType = Bytes[0];

		if (MsgType == 0) { // 数据传输
			byte CmdID = Bytes[2];
			switch (CmdID) {
			case 6:
				PutLog("握手, 时间同步");

				THandshake_Response Handshake_Response = new THandshake_Response();

				Handshake_Response.DateTime().SetAsString(GetNowDateTime());
				Handshake_Response.SerialNumber().SetAsInteger(
						FLastSerialNumber);
				Handshake_Response.Ident().SetAsInteger(GetNewIdent());

				TStream Stream = new TStream(null);
				Handshake_Response.SaveToBytes(Stream);
				FTerminalLink.SendPackage(Stream.Bytes);
				break;
			}
			return;
		}

		if (MsgType == 1) {
			PutLog("交易报文");
			TTransaction Transaction = new TTransaction();

			Transaction.ClearProcess(); // 清空流程

			TStream Stream = new TStream(Bytes);
			// Stream.SetBytes(Bytes);
			if (Transaction.LoadFormBytes(Stream) != TResult_LoadFromBytes.rfll_NoError) // 报文格式错误
				return;

			if (!Transaction.CheckMAC()) { // MAC签名错误
				PutLog("MAC签名错误");
				return;
			}

			if (Transaction.Ident().GetAsInteger() != FIdent) { // 识别码不匹配
				PutLog("识别码不匹配");
				UpdateWorkingStatus(ws_ErrorIdent);
				return;
			}

			if (!IsAllowTrans(Transaction)) {
				PutLog("不能识别的交易代码: " + Transaction.TransCode().GetAsInteger());
				return;
			}

			if (!Transaction.LoadProcess()) { // 导入流程错误
				PutLog("导入流程错误");
				return;
			}

			if (Transaction.TransCode().GetAsInteger() == 1) { // 签到
				FLastMerchantID = Transaction.ProcessList.MerchantID()
						.GetAsString();
				FLastTerminalID = Transaction.ProcessList.TerminalID()
						.GetAsString();
				FLastUserID = Transaction.ProcessList.UserID().GetAsString();
			} else {
				Transaction.ProcessList.MerchantID().SetAsString(
						FLastMerchantID);
				Transaction.ProcessList.TerminalID().SetAsString(
						FLastTerminalID);
				Transaction.ProcessList.UserID().SetAsString(FLastUserID);
			}

			
			/*  测试用 */
			/*
			Transaction.BufferList.MsgTypeID().SetAsInteger(200);
			Transaction.BufferList.ProcessCode().SetAsInteger(0);
			Transaction.BufferList.PAN().SetAsString("5264102500120211");
			Transaction.BufferList.TraceAuditNumber().SetAsInteger(10);
			Transaction.BufferList.SaleAmount().SetAsInt64(100000);
			
			Transaction.BufferList.ReferenceNumber().SetAsString(
					FLastReferenceNumber);
			//*/
			
			PutLog_Request(Transaction);
			// ******************************************************

			byte[] MAC;
			ServerUpDataParse serverParseData = null;
			try {
				serverParseData = new ServerUpDataParse(Transaction);
				JposPackageFather jpos = serverParseData.getJposPackage();

				if (IsEncryptMACTrans(Transaction)) {
					// 计算
					PutLog("正在请求终端计算MAC.......");

					MAC = EncryptMAC(Transaction, serverParseData.getMab(),
							IsFillZeroTrans(Transaction));

					if (Common.Length(MAC) <= 0) { // MAC回复出错
						PutLog("MAC回复出错");
						return;
					}

					jpos.setMac(MAC);

				}

			} catch (Exception e) {
				e.printStackTrace();
				PutLog("构建数据包错误");
				UpdateWorkingStatus(ws_ErrorBuild);
				return;
			}

			UpdateWorkingStatus(ws_WillConnect);
			if (!CheckWorkingStatus(ws_WillConnect)) {
				PutLog("更新工作状态错误");
				return;
			}

			try {
				if (!CommandControl.getInstance().isConnect())
					CommandControl.getInstance().connect(20000, 60000); // 连接后台
			} catch (IOException e) {
				e.printStackTrace();
				if(e instanceof SocketTimeoutException){
					PutLog("连接超时");//连接超时
				}else if(e instanceof ConnectException){
					PutLog("服务器无法连接");//IP端口错误
				}else{
					PutLog("连接错误");//网络或者其他错误
				}
				
				UpdateWorkingStatus(ws_ErrorConnect);
				return;
			
			} catch (Exception e) {
				e.printStackTrace();
				PutLog("连接后台错误");
				UpdateWorkingStatus(ws_ErrorConnect);
				return;
			}

			try {

				ServerDownDataParse reData = CommandControl.getInstance()
						.sendUpCommand(serverParseData);// 发送数据

				CommandControl.getInstance().closeConnect(); // 关闭连接

				Transaction = reData.getTTransaction();// 取返回POS的对象

				if (Transaction == null) {
					PutLog("返回对象错误, Transaction为空");
					return;
				}

				Transaction.Ident().SetAsInteger(FIdent);
				PutLog_Response(Transaction);
				
				/*
				if (!Transaction.BufferList.ReferenceNumber().GetIsEmpty())
					FLastReferenceNumber = Transaction.BufferList.ReferenceNumber().GetAsString();
				//*/
				
				if (IsEncryptMACTrans(Transaction)) {
					// 计算
					PutLog("正在请求终端计算MAC.......");

					MAC = EncryptMAC(Transaction, reData.getMab(), false);

					if (Common.Length(MAC) <= 0) { // MAC回复出错
						PutLog("MAC回复出错");
						return;
					}

					if (!Common.IsSameBytes(MAC, reData.getMac())) { // MAC校验出错
						PutLog("MAC校验出错");
						return;
					}
					PutLog("MAC校验正确.");
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(e instanceof SocketTimeoutException){
					PutLog("连接超时");//连接超时
				}else if(e instanceof ConnectException){
					PutLog("服务器无法连接");//IP端口错误
				}else{
					PutLog("连接错误");//网络或者其他错误
				}
				
				UpdateWorkingStatus(ws_ErrorRecv);
				return;
			} catch (PacketException e) {
				PutLog("数据错误");
				UpdateWorkingStatus(ws_ErrorRecv);
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				PutLog("接收错误");
				UpdateWorkingStatus(ws_ErrorRecv);
				return;
			}

			// ******************************************************

			Transaction.SaveProcess();
			Transaction.SaveMAC();
			Stream = new TStream(null);
			if (Transaction.SaveToBytes(Stream) != TResult_SaveToBytes.rfls_NoError) {
				PutLog("保存响应数据错误!");
				return;
			}
			;

			
			FTerminalLink.SendPackage(Stream.Bytes);
			PutLog("已发送响应数据.");
		}
	}

	public void PutLog(String s) {
		System.out.println(s);

		LogInfo.instance.pos_to_pad.append(s + "\n");
	}

	public void PutLog_Request(TTransaction Transaction) {
		PutLog("[请求]流水号: " + Transaction.SerialNumber().GetAsString());
		PutLog("[请求]交易代码: " + Transaction.TransCode().GetAsInteger());
		PutLog("[请求]年:" + Transaction.Year().GetAsString());
		PutLog("[请求]日期:" + Transaction.Date().GetAsString());
		PutLog("[请求]时间:" + Transaction.Time().GetAsString());
		PutLog("[请求]2磁道数据: " + Transaction.ProcessList.GetTrack2Data());
		PutLog("[请求]3磁道数据: " + Transaction.ProcessList.GetTrack3Data());
		PutLog("[请求]卡号: " + Transaction.ProcessList.GetPAN());
		PutLog("[请求]票据号: " + Transaction.ProcessList.BillNumber().GetAsString());
		
		PutLog("[请求]商户号: " + Transaction.ProcessList.MerchantID().GetAsString());
		PutLog("[请求]终端号: " + Transaction.ProcessList.TerminalID().GetAsString());
		PutLog("[请求]操作员ID: " + Transaction.ProcessList.UserID().GetAsString());

		PutLog("[请求]回送金额: "
				+ Transaction.ProcessList.ReturnSaleAmount().GetAsInt64());

		PutLog("[请求]上次参考号: "
				+ Transaction.BufferList.ReferenceNumber().GetAsString());
		
		PutLog("[请求]原始流水号: "
				+ Transaction.ProcessList.OriginalSerialNumber().GetAsString());
		
		PutLog("[请求]原始MAC值: "
				+ Common.ToHex(Transaction.ProcessList.OriginalMAC().GetData()));
	}

	public void PutLog_Response(TTransaction Transaction) {
		PutLog("[响应]流水号: " + Transaction.SerialNumber().GetAsString());
		PutLog("[响应]交易代码: " + Transaction.TransCode().GetAsInteger());
		PutLog("[响应]年:" + Transaction.Year().GetAsString());
		PutLog("[响应]日期:" + Transaction.Date().GetAsString());
		PutLog("[响应]时间:" + Transaction.Time().GetAsString());
		PutLog("[响应]应答: " + Transaction.ProcessList.Response().GetAsString());
		PutLog("[响应]商户名称: "
				+ Transaction.ProcessList.MerchantName().GetAsString());

		PutLog("[响应]参考号: "
				+ Transaction.BufferList.ReferenceNumber().GetAsString());
	}
}
