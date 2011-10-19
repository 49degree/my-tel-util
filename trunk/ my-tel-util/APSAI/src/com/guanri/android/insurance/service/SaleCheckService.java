package com.guanri.android.insurance.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.widget.AdapterView.OnItemClickListener;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.OperateLogBean;
import com.guanri.android.insurance.bean.OperatorRecordBean;
import com.guanri.android.insurance.bean.SaleCheckBean;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.CommandParseException;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgCancelBtn;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.printer.BluetoothPrinter;
import com.guanri.android.insurance.printer.PrinterEngine;
import com.guanri.android.insurance.printer.PrinterSelectUtils;
import com.guanri.android.lib.bluetooth.BluetoothPool;
import com.guanri.android.lib.context.HandlerWhat;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TimeUtils;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

/**
 * 对账管理
 * 
 * @author Administrator
 * 
 */
public class SaleCheckService {
	public static Logger logger = Logger.getLogger(SaleCheckService.class);//日志对象
	public static String moneyUnit = "("+StringUtils.getStringFromValue(R.string.apsai_common_money_unit_text)+")";
	public static String orderUnit = "("+StringUtils.getStringFromValue(R.string.apsai_common_order_unit_text)+")";
	
	private BluetoothPrinter mBluetoothPrinter;//打印对象
	private final static Class<BluetoothPrinter> printerClass = BluetoothPrinter.class;
	private OperateLogService operateLog;//日志操作对象
	public DBOperator dbOperator;//数据库对象
	private CommandControl commandControl;//命令操作对象
	private Context context ;//android上下文
	/**
	 * @roseuid 4DF8331001C5
	 */
	public SaleCheckService(Context context) {
		this.commandControl = CommandControl.getInstance();
		this.context = context;
		dbOperator = DBOperator.getInstance();
		operateLog = new OperateLogService(this.context);
	}

	/**
	 * 根据对账ID查询对账信息
	 * 
	 * @return
	 */
	public SaleCheckBean queryCheckedOrder(String checkId) {
		HashMap<String,String> params = new HashMap<String,String>(1); 
		params.put("Check_id=", checkId);
		List<Object> list = dbOperator.queryBeanList(DBBean.TB_SALE_CHECK, params);
		if(list!=null&&list.size()>0){
			return (SaleCheckBean)list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 查询是否有未对账单，并返回待对账信息
	 * 
	 * @return
	 */
	public SaleCheckBean queryNoCheckedOrder() {
		SaleCheckBean saleCheckBean = new SaleCheckBean(); 
		//获取当前的对账ID
		String checkId = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.CHECK_ID);
		if("".equals(checkId)){
			checkId = "1";
		}
		saleCheckBean.setCheck_id(Integer.parseInt(checkId));
		//checkId = "0";
		//查询当前未对账销售记录
		String[] returnColumn = {"Operator_id","count(1)","sum(Insured_amount)"};
		HashMap<String,String> params = new HashMap<String,String>(1); 
		params.put("Check_id=", checkId);
		params.put("Check_state=", String.valueOf(CommandConstant.CHECK_STATE_NO));
		List<Map<String,String>> orderList = dbOperator.queryMapList(DBBean.TB_SALE_ORDER, returnColumn, params);
		if(orderList.size()>0){
			Map<String,String> order = orderList.get(0);
			String operatorId = order.get(returnColumn[0]);
			if(operatorId!=null&&!"".equals(operatorId)){
				saleCheckBean.setOperator_id(operatorId);
				try{
					saleCheckBean.setOrder_count(Integer.parseInt(order.get(returnColumn[1])));
				}catch(Exception e){
					saleCheckBean.setOrder_count(0);
				}
				try{
					saleCheckBean.setOrder_sum(Integer.parseInt(order.get(returnColumn[2])));
				}catch(Exception e){
					saleCheckBean.setOrder_sum(0);
				}
			}			
		}
		//查询当前未对账退单记录
		returnColumn[2] = "sum(Back_sum)";
		orderList = dbOperator.queryMapList(DBBean.TB_SALE_ORDER_BACK, returnColumn, params);
		if(orderList.size()>0){
			Map<String,String> order = orderList.get(0);
			String operatorId = order.get("Operator_id");
			if(operatorId!=null&&!"".equals(operatorId)){
				saleCheckBean.setOperator_id(operatorId);
				try{
					saleCheckBean.setOrder_back_count(Integer.parseInt(order.get(returnColumn[1])));
				}catch(Exception e){
					saleCheckBean.setOrder_back_count(0);
				}
				try{
					saleCheckBean.setOrder_back_sum(Integer.parseInt(order.get(returnColumn[2])));
				}catch(Exception e){
					saleCheckBean.setOrder_back_sum(0);
				}
			}			
		}
		
		//查询当前未对账废单记录
		returnColumn[2] = "sum(number)";
		orderList = dbOperator.queryMapList(DBBean.TB_SALE_ORDER_USELESS, returnColumn, params);
		if(orderList.size()>0){
			Map<String,String> order = orderList.get(0);
			String operatorId = order.get("Operator_id");
			if(operatorId!=null&&!"".equals(operatorId)){
				saleCheckBean.setOperator_id(operatorId);
				try{
					saleCheckBean.setOrder_useless_count(Integer.parseInt(order.get(returnColumn[2])));
				}catch(Exception e){
					saleCheckBean.setOrder_useless_count(0);
				}
			}			
		}
		saleCheckBean.setCheck_time(TimeUtils.getTimeString(new Date()));
		return saleCheckBean;
	}

	/**
	 * 对账成功，记录对账记录并修改批次号
	 * 
	 * @return boolean
	 * @roseuid 4DF80B0101E4
	 */
	public boolean saveSaleCheck(SaleCheckBean saleCheckBean) {
		
		long id = dbOperator.insert(DBBean.TB_SALE_CHECK, saleCheckBean);
		SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
				SharedPreferencesUtils.CHECK_ID,String.valueOf(saleCheckBean.getCheck_id()+1));//修改批次号
		//修改记录中的对账状态
		ContentValues contentValues = new ContentValues();//修改值
		contentValues.put("Check_state", CommandConstant.CHECK_STATE_YES);
		HashMap<String,String> params = new HashMap<String,String>(1); //条件
		params.put("Check_id=", String.valueOf(saleCheckBean.getCheck_id()));
		if(saleCheckBean.getOrder_count()>0){
			dbOperator.update(DBBean.TB_SALE_ORDER, contentValues, params);
		}
		
		if(saleCheckBean.getOrder_back_count()>0){
			dbOperator.update(DBBean.TB_SALE_ORDER_BACK, contentValues, params);
		}
		
		if(saleCheckBean.getOrder_useless_count()>0){
			dbOperator.update(DBBean.TB_SALE_ORDER_USELESS, contentValues, params);
		}
		
		return true;
	}


	/**
	 * 总对账对账
	 * 
	 * @return SaleCheckBean
	 * @roseuid 4DF80B610290
	 */
	public boolean doAllSaleCheck(SaleCheckBean saleCheckBean,UpCommandHandler upCommandHandler) {

		
		
		//		1	分公司代码		ASC	3	
		//		2	终端ID		ASC	8	
		//		3	终端校验码		ASC	8	
		//		4	操作员工号		ASC	6	
		//		5	操作员姓名		ASC	10	
		//		6	结算日期		ASC	19	
		//		7	保单销售笔数		HEX	2	
		//		8	保费销售总和		HEX	4	
		//		9	废单张数		HEX	2	
		//		10	退单笔数		HEX	2	
		//		11	退单金额		HEX	4	
		//		12	批次号		HEX	4
		byte[] body = new byte[72];
		try{
			if(CommandConstant.COMFIG_BRANCH_ID.length()>0){
				System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_BRANCH_ID), 0, body, 0, CommandConstant.COMFIG_BRANCH_ID.length());
			}
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, body, 3, 8);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body,11, 8);
			System.arraycopy(TypeConversion.stringToAscii(saleCheckBean.getOperator_id()), 0, body,19, 6);
			//获取员工姓名
			OperatorRecordService operatorRecordService = new OperatorRecordService(this.context);
			OperatorRecordBean  operatorRecordBean  = operatorRecordService.queryOperatorInfo(saleCheckBean.getOperator_id());
			
			//logger.debug(operatorRecordBean.getOperator_name());
			//operatorRecordService.dBOperator.release();//释放数据库连接
			
			byte[] userName = TypeConversion.stringToAscii(operatorRecordBean.getOperator_name());
			System.arraycopy(userName, 0, body, 25, userName.length);
			
			System.arraycopy(TypeConversion.stringToAscii(saleCheckBean.getCheck_time()), 0,body, 35, saleCheckBean.getCheck_time().length());
			
			System.arraycopy(TypeConversion.shortToBytes((short)saleCheckBean.getOrder_count()), 0, body, 54, 2);
			System.arraycopy(TypeConversion.intToBytes(saleCheckBean.getOrder_sum()), 0, body, 56, 4);
			System.arraycopy(TypeConversion.shortToBytes((short)saleCheckBean.getOrder_useless_count()), 0, body, 60, 2);
			System.arraycopy(TypeConversion.shortToBytes((short)saleCheckBean.getOrder_back_count()), 0, body, 62, 2);
			System.arraycopy(TypeConversion.intToBytes(saleCheckBean.getOrder_back_sum()), 0, body, 64, 4);
			System.arraycopy(TypeConversion.intToBytes(saleCheckBean.getCheck_id()), 0, body, 68, 4);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		//构造上行命令对象
		UpCommandBean upCommandBean = new UpCommandBean();
		upCommandBean.setCommandCode(CommandConstant.CMD_RECORD_TOTAL);
		upCommandBean.setBody(body);
		UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
		return commandControl.sendUpCommandInThread(upCommandParse,upCommandHandler);
	}


	/**
	 * 明细对账
	 * 
	 * @roseuid 4DF80D780000
	 */
	public String doSaleRecordCheck(SaleCheckBean saleCheckBean) {
		//		1	分公司代码		ASC	3	
		//		2	终端ID		ASC	8	
		//		3	结算日期		ASC	19	
		//		4	发送快数		ASC	6	R0000n"(n为块号,累加)；
		//		5	记录数	REC_CNT	HEX	1	
		//		6	记录明细		ASC	REC_BYTES*REC_CNT	具体参见每块记录组成
		//		REC_BYTES为每条明细占的字节个数
		//		7	终端校验码		ASC	8	
		//		8	批次号		HEX	4	
		//		单证号（20ASC）+保单号（20ASC）＋投保单号（20ASC）＋金额(4HEX)+交易码(6ASC)
		//		REC_BYTE = 20+20+20+4+6 = 70
		//分多次发送，每次发送10条 共749字节
		int packetList = 10;//每包记录数
		int count = saleCheckBean.getOrder_count();//总记录数
		int uploadPacket = 0;//总块数
		int reSendTime = 0;//重复上传次数,可重复上传3次
		DownCommandParse downCommandParse = null;
		String returnMsg = null;//返回消息
		try{
			if(count>0){// 有未对账记录
				uploadPacket = count%packetList>0?(count/packetList+1):(count/packetList);
				byte[] bodyHeader = new byte[37];
				byte[] body = null;
				byte[] bodyFooter = new byte[12];
				try{
					//前部分数据
					if(CommandConstant.COMFIG_BRANCH_ID.length()>0){
						System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_BRANCH_ID), 0, bodyHeader, 0, CommandConstant.COMFIG_BRANCH_ID.length());
					}
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, bodyHeader, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(saleCheckBean.getCheck_time()), 0,bodyHeader, 11, saleCheckBean.getCheck_time().length());
					
					//后部分数据
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, bodyFooter,0, 8);
					System.arraycopy(TypeConversion.intToBytes(saleCheckBean.getCheck_id()), 0, bodyFooter, 8, 4);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//查询出单记录
				HashMap<String,String> params = new HashMap<String,String>(1); 
				params.put("Check_id=", String.valueOf(saleCheckBean.getCheck_id()));
				params.put("Check_state=", String.valueOf(CommandConstant.CHECK_STATE_NO));
				List<Object> orderList = dbOperator.queryBeanList(DBBean.TB_SALE_ORDER, params);
				
				for(int i=0;i<=uploadPacket;i++){
					try{
						//发送快数		ASC	6	R0000n"(n为块号,累加)；
						System.arraycopy(TypeConversion.stringToAscii(getPacketNum(i)), 0,bodyHeader, 30, 6);
						
						if(i==uploadPacket){
							bodyHeader[36] = 0x00;//记录数	REC_CNT	HEX	1
							body = new byte[55];
							System.arraycopy(bodyHeader, 0, body, 0, 37);//头部
							System.arraycopy(new byte[]{0x00,0x00,0x00,0x00,0x00,0x00}, 0, body, 37, 6);//头部
							System.arraycopy(bodyFooter, 0, body, 43, 12);//尾部
						}else{
							//分包进行上传
							int packageEndIndex = (i+1)*packetList;//计算当前包最后一条记录位置
							byte packetRecord = 10;
							if(packageEndIndex>count){
								packetRecord = (byte)(count-i*packetList);
								packageEndIndex = count;
							}
							bodyHeader[36] = packetRecord;//记录数	REC_CNT	HEX	1
							body = new byte[70*packetRecord+49];//命令体       头部+尾部+记录明细 ASC	REC_BYTES*REC_CNT	具体参见每块记录组成 REC_BYTES为每条明细占的字节个数
							
							System.arraycopy(bodyHeader, 0, body, 0, 37);//头部
							System.arraycopy(bodyFooter, 0, body, 70*packetRecord+37, 12);//尾部
							
							//构造明细块数据 ：单证号（20ASC）+保单号（20ASC）＋投保单号（20ASC）＋金额(4HEX)+交易码(6ASC暂时没有用到)
							for(int j=0;j<packageEndIndex;j++){
								SaleOrderBean saleOrderBean = (SaleOrderBean)orderList.get(j+i*packetList);
								byte[] logId = TypeConversion.stringToAscii(saleOrderBean.getBillNo());
								System.arraycopy(logId, 0, body, 37+i*70, logId.length);
								byte[] insuNo = TypeConversion.stringToAscii(saleOrderBean.getInsu_No());
								System.arraycopy(insuNo, 0, body, 57+i*70, insuNo.length);		
								byte[] proposalNo = TypeConversion.stringToAscii(saleOrderBean.getProposalForm_No());
								System.arraycopy(proposalNo, 0, body, 77+i*70, proposalNo.length);	
								System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsured_amount()), 0, body, 97+i*70, 4);
								System.arraycopy(TypeConversion.stringToAscii(CommandConstant.CMD_SALE2), 0, body, 101+i*70, 6);
							}
						}
						//上传数据包
						try{
							downCommandParse = sendSaleRecordCheckPacket(body,0);
							returnMsg = null;
						}catch(CommandParseException cmde){
							returnMsg = cmde.getMessage();
							reSendTime++;
						}catch(IOException ioe){
							returnMsg = ioe.getMessage();
							reSendTime++;
						}
						if(reSendTime>0&&reSendTime<4){
							i=-1;//从第一包数据重新上传
						}else if(reSendTime>=4){//重复上传3次后还未成功则停止上传
							break;
						}
						
						//-1相等于0xFF
						if(!"-1".equals(downCommandParse.getDownCommandBean().getAnswerCode())&&
								!"0".equals(downCommandParse.getDownCommandBean().getAnswerCode())){//如果上传反馈回来的结果为错误信息
							break;
						}else{
							logger.debug("分公司代码:"+TypeConversion.asciiToString(downCommandParse.getDownCommandBean().getBody(), 0, 3));
							logger.debug("终端ID:"+TypeConversion.asciiToString(downCommandParse.getDownCommandBean().getBody(), 3, 8));
							logger.debug("总笔数:"+TypeConversion.bytesToShort(downCommandParse.getDownCommandBean().getBody(), 11));
							logger.debug("接收块数:"+TypeConversion.asciiToString(downCommandParse.getDownCommandBean().getBody(), 12, 6));
							
						}
						
					}catch(UnsupportedEncodingException e){
						
					}				
				}
			}
			if(downCommandParse!=null&&
					!"0".equals(downCommandParse.getDownCommandBean().getAnswerCode())
					&&!"-1".equals(downCommandParse.getDownCommandBean().getAnswerCode())){
				DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
				returnMsg = downCommandBean.getAnswerMsg()+downCommandBean.getMark()==null?"":downCommandBean.getMark();
			}
		}catch(Exception e){
			returnMsg = StringUtils.getStringFromValue(R.string.apsai_sale_check_list_faire_text);
		}


		return returnMsg;
	}
	
	/**
	 * 上传数据
	 * @param body 数据包
	 * @param reUploadTime 记录同一包重复上传次数
	 * @return
	 */
	private DownCommandParse sendSaleRecordCheckPacket(byte[] body,int reUploadTime) throws CommandParseException,IOException{
		//构造上行命令对象
		UpCommandBean upCommandBean = new UpCommandBean();
		upCommandBean.setCommandCode(CommandConstant.CMD_RECORD_LIST);
		upCommandBean.setBody(body);
		UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
		DownCommandParse downCommandParse = null;
		try{
			downCommandParse = commandControl.sendUpCommand(upCommandParse);
		}catch(CommandParseException cmde){
			//传输明细对账过程中如出现错误（终端没有接收到后台应答重复3次），则对账明细需从头再次发起
			if(++reUploadTime<4){
				downCommandParse = sendSaleRecordCheckPacket(body,reUploadTime);
			}else{
				throw cmde;
			}
		}catch(IOException ioe){
			//传输明细对账过程中如出现错误（终端没有接收到后台应答重复3次），则对账明细需从头再次发起
			if(++reUploadTime<4){
				downCommandParse = sendSaleRecordCheckPacket(body,reUploadTime);
			}else{
				throw ioe;
			}
		}

		return downCommandParse;
	}
	
	
	/**
	 * 打印对账信息
	 * 可以打印对账信息（可选、也可以不打印，如果打印，要提示使用者可能要换纸），
	 * 要打印的信息： 终端业务ID、操作员ID、对账时间、批次号、售单比数、售单金额（到分）、
	 * 退单比数、退单金额、废单比数；打印后提示使用者是否打印详单
	 */
	private String printerAddr = null;
	private Handler connectBthHandler = null;//蓝牙连接结果处理对象
	private OnItemClickListener itemClickMethod = null;//选中蓝牙设备处理对象
	public void printCheckResult(final SaleCheckBean saleCheckBean) {
    	//打印出单数据，首先检查蓝牙打印机的情况
		PrinterSelectUtils.printCheckResult(context, new PrinterSelectUtils.OnPrintConnectedListen(){
		public void onPrintConnected(){
			mBluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance().getBluetoothFactory(
					SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.PRINTER_INFO,SharedPreferencesUtils.PRINTER_ADD),printerClass);
        	// 打印
			printCheckOrder(saleCheckBean);
		}});
	}


	
	/**
	 * 打印总对账信息
	 * @param saleCheckBean
	 */
	private void printCheckOrder(final SaleCheckBean saleCheckBean){
		String split3 = "             ";
		String split2 = "           ";
		String split1 = "         ";
		try{
			StringBuffer printBuffer = new StringBuffer();
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_insu_manager_business_id)
					+CommandConstant.COMFIG_POS_ID).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_user_manager_user_id)
					+split1+saleCheckBean.getOperator_id()).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_order_time)+
					split3+saleCheckBean.getCheck_time()).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_batch)+
					split3+saleCheckBean.getCheck_id()).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_orders)+
					split2+saleCheckBean.getOrder_count()+orderUnit).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_order_price)+
					split1+Utils.fengToYuan(saleCheckBean.getOrder_sum())+moneyUnit).append(System.getProperty("line.separator", "\n"));
			
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_back_orders)+
					split2+saleCheckBean.getOrder_back_count()+orderUnit).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_back_price)+
					split1+Utils.fengToYuan(saleCheckBean.getOrder_back_sum())+moneyUnit).append(System.getProperty("line.separator", "\n"));
			printBuffer.append(StringUtils.getStringFromValue(R.string.apsai_sale_check_list_usless_orders)+
					split2+saleCheckBean.getOrder_useless_count()+orderUnit).append(System.getProperty("line.separator", "\n"));
			
			mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(printBuffer.toString()));
			mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
			mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
			mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
			mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
			DialogUtils.showChoiceAlertDlg(context, StringUtils.getStringFromValue(R.string.apsai_sale_check_text),
					StringUtils.getStringFromValue(R.string.apsai_sale_check_print_list_text),new OnAlertDlgSureBtn(){
    			public void OnSureBtn(){
    				printInsuInfo(saleCheckBean);
    			}
    		});	
		}catch(UnsupportedEncodingException e){
			
		}
	}
	
	/**
	 * 打印详单信息
	 * 
	 * 需要打印具体的每个保单信息（不同险种产品分开打印）：单证号、保单号、金额
	 */
	public void printInsuInfo(SaleCheckBean saleCheckBean){
		//查询出单记录
		if(saleCheckBean.getOrder_count()>0){
			HashMap<String,String> params = new HashMap<String,String>(1); 
			params.put("Check_id=", String.valueOf(saleCheckBean.getCheck_id()));
			//params.put("Check_state=", String.valueOf(CommandConstant.CHECK_STATE_YES));
			//params.put("ORDERBY", "Insu_code asc,Log_id asc");
			List<Object> orderList = dbOperator.queryBeanList(DBBean.TB_SALE_ORDER, params);
			String insuCode = null;
			StringBuffer printBuffer = new StringBuffer();
			try{
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii("出单明细列表：单证号:保单号:金额"));
				for(Object o:orderList){
					SaleOrderBean order = (SaleOrderBean)o;
//					if(order.getCardCode().equals(insuCode)){
//						printBuffer.append(order.getCardCode()).append(":").append(order.getInsu_name()).append(System.getProperty("line.separator", "\n"));
//					}
					printBuffer.append(order.getBillNo()).append(":").append(order.getInsu_No()).append(":").append(Utils.fengToYuan(order.getInsured_amount())).append(SaleCheckService.moneyUnit).append(System.getProperty("line.separator", "\n"));
				}
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(printBuffer.toString()));
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
				mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
			}catch(UnsupportedEncodingException e){
				
			}
		}
	}
	
	
	/**
	 * 获取块编号
	 * @param i
	 * @return
	 */
	private String getPacketNum(int i){
		//String temp = "R0000"+(++i);
		StringBuffer tempBuffer = new StringBuffer("R");
		int zeroLength = 5-String.valueOf(i+1).length();
		for(int index=0;index<5-String.valueOf(i+1).length();index++){
			tempBuffer.append("0");
		}
		tempBuffer.append(++i);
		
		return tempBuffer.toString();
	}
	
	/**
	 * 记录日志
	 * @param userId
	 * @param logMemo
	 */
	public void logInfo(String logMemo){
		String userId = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserId();
		
		OperateLogBean operateLog = new OperateLogBean();
		operateLog.setOperator_name(StringUtils.getStringFromValue(R.string.apsai_init_login));
		operateLog.setOperator_id(userId);
		operateLog.setOperate_time(TimeUtils.getTimeString(new Date()));
		operateLog.setOperate_memo(logMemo);
		
		
		dbOperator.insert(DBBean.TB_OPERATE_LOG, operateLog);
		
	}
}
