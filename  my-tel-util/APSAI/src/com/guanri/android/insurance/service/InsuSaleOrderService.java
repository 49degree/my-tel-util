package com.guanri.android.insurance.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAdditional;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDefine;
import com.guanri.android.insurance.bean.InsuranceBean;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandHandler.UpCommandHandlerListener;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.printer.PrinterEngine;
import com.guanri.android.insurance.printer.PrinterSelectUtils;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TimeUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 保单销售
 */
public class InsuSaleOrderService 
{
	public static Logger logger = Logger.getLogger(InsuPlanManagerService.class);// 日志对象;
	
	public DBOperator dbOperater;
    public PrinterEngine printerEngine;
    private Context context;
	private CommandControl commandControl = null;

   /**
   @roseuid 4DF8330F0271
    */
   public InsuSaleOrderService(Context context) {
		this.commandControl = CommandControl.getInstance();
		this.context = context;
		dbOperater = DBOperator.getInstance();
	}
   
   /**
   @roseuid 4DF81F020157
    */
   public void structView() 
   {
    
   }
   
   /**
    * 想服务器提交保单销售记录
   @roseuid 4DF81F2500BB
    */
   public void submit(final Handler handler,final ProgressDialog btDialog,final SaleOrderBean saleOrderBean,
		   final HashMap<Integer, String> inputValueMap,final InsuranceBean insuranceBean) {
	   btDialog.show();
		try {
			 // 计算备注字段长度,分装备注字段
			String remarkstr = saleOrderBean.getRemark();
			int reamarkbyteLength = 0;
			byte[] reamarkbyte = null;
			if (remarkstr != null&&remarkstr.length() > 0) {
				reamarkbyteLength = TypeConversion.stringToAscii(remarkstr).length;
				reamarkbyte = new byte[reamarkbyteLength];
					String[] remarkstrs = remarkstr.split(";");
					int index = 0;
					for (int i = 0; i < remarkstrs.length; i++) {
						try {
							//String str = remarkstrs[i];
							byte[] temp = TypeConversion.stringToAscii(remarkstrs[i]); 
							System.arraycopy(temp, 0,reamarkbyte, index, temp.length);
							index = index + temp.length;
							if(++index<reamarkbyteLength){
								reamarkbyte[index] = 0x0d;
							}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			
			// 构造命令体
			final byte[] body = new byte[354+reamarkbyteLength];
				
			logger.debug("CommandConstant.COMFIG_COM_PWD: " +CommandConstant.COMFIG_COM_PWD);
			//1		分公司代码	BranchID	ASC	3	
			if(CommandConstant.COMFIG_BRANCH_ID.length()>0){
				System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_BRANCH_ID), 0, body, 0, CommandConstant.COMFIG_BRANCH_ID.length());
			}
			//2		终端ID	PosID	ASC	8	
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, body, 3, 8);
			//3		终端校验码	ComPSW	ASC	8	
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body,11, 8);
			//4		操作员工号	Operator ID	ASC	6	
			System.arraycopy(TypeConversion.stringToAscii(saleOrderBean.getOperator_id()), 0,body, 19, 6);
			//5		操作员姓名	Operator Name	ASC	10	
			String userName = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserName();
			byte[] operatornamebyte = TypeConversion.stringToAscii(userName); 
			System.arraycopy(operatornamebyte, 0,body, 25, operatornamebyte.length);
			//6		批次号	BatchNo	HEX	4	
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getCheck_id()), 0,body, 35, 4);
			//7		销售日期	SaleTime	ASC	19	
			byte[] Operate_timebyte = TypeConversion.stringToAscii(saleOrderBean.getOperate_time());
			System.arraycopy(Operate_timebyte, 0,body, 39, Operate_timebyte.length);
			//8		业务方案代码	CardCode	ASC	8
			System.arraycopy(TypeConversion.stringToAscii(saleOrderBean.getCardCode()), 0,body, 58, 8);
			//9		业务方案序号	PlanNo	HEX	2
			System.arraycopy(TypeConversion.intToBytes(new Integer(saleOrderBean.getPlanNo())), 0,body, 66, 2);
			//10		组合序号		HEX	1	选择的组合序号，0表示未选择。泰康特有字段，其他保险公司都无此字段。
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsuAssembled()), 0,body, 68, 1);
			//11		单证号码	BillNo	ASC	20	不足后补0x00，下同
			byte[] BillNotype = TypeConversion.stringToAscii(saleOrderBean.getBillNo()); 
			System.arraycopy(BillNotype, 0,body, 69, BillNotype.length);
			//12		保险起期		ASC	19
			String dateTime = null;
			//判断起期时间是否存在
			if(saleOrderBean.getInsu_begin_time()==null){
				dateTime = saleOrderBean.getInsu_begin_date()+" 00:00:01";
			}else{
				dateTime = saleOrderBean.getInsu_begin_date()+" "+saleOrderBean.getInsu_begin_time();
			}
			byte[] Insu_begin_datebyte = TypeConversion.stringToAscii(dateTime);
			System.arraycopy(Insu_begin_datebyte, 0,body, 89, Insu_begin_datebyte.length);
			
			//13		保险终期		ASC	19	
			//判断终期时间是否存在
			if(saleOrderBean.getInsu_end_time()==null){
				dateTime = saleOrderBean.getInsu_end_date()+" 23:59:59";
			}else{
				dateTime = saleOrderBean.getInsu_end_date()+" "+saleOrderBean.getInsu_end_time();
			}
			byte[] Insu_end_datebyte = TypeConversion.stringToAscii(dateTime);
			System.arraycopy(Insu_end_datebyte, 0,body, 108, Insu_end_datebyte.length);

			//14		保险份数		HEX	4	投保份数或保额，当传送保险份数时，固定为1，传送保额时，单位为元。
			System.arraycopy(TypeConversion.intToBytes(1), 0,body, 127, 4);
			//15		总保费	Premium	HEX	4	单位为分，如100.00元则表示为十进制10000，即0x2710，排列为0x10，0x27，0x00，0x00
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsured_amount()), 0,body, 131, 4);
			//System.arraycopy(TypeConversion.intToBytes(300), 0,body, 131, 4);
			//16		投保人姓名		ASC	30	最多30字节
			if(saleOrderBean.getPlyh_name()!=null){
				byte[] Plyh_namebyte = TypeConversion.stringToAscii(saleOrderBean.getPlyh_name());
				System.arraycopy(Plyh_namebyte, 0,body, 135, Plyh_namebyte.length);
			}

			//17		投保人性别 HEX	1	1=男 2=女；没有录入性别时送0x00，下同
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getPlyh_sex()), 0,body, 165, 1);
			//18		投保人生日		ASC	10	格式为YYYY-MM-DD，没有录入生日时所有字节送0x00
			if(saleOrderBean.getPlyh_Brithday()!=null){
				byte[] Plyh_Brithdaybyte = TypeConversion.stringToAscii(saleOrderBean.getPlyh_Brithday());
				System.arraycopy(Plyh_Brithdaybyte, 0,body, 166, Plyh_Brithdaybyte.length);
			}
			//19		投保人证件类型 		HEX	1	没有录入证件类型时送0x00，下同。
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getPlyh_Card_type()), 0,body, 176, 1);
			//20		投保人证件号码		ASC	18
			if(saleOrderBean.getPlyh_Card_no() != null){
				byte[] Plyh_Card_nobyte = TypeConversion.stringToAscii(saleOrderBean.getPlyh_Card_no());
				System.arraycopy(Plyh_Card_nobyte, 0,body, 177, Plyh_Card_nobyte.length);
			}
			//21		被保人姓名		ASC	30	最多30字节
			if(saleOrderBean.getInsured_name()!=null){
				byte[] Insured_namebyte = TypeConversion.stringToAscii(saleOrderBean.getInsured_name());
				System.arraycopy(Insured_namebyte, 0,body, 195, Insured_namebyte.length);
			}
			//22		被保人性别		HEX	1	1=男 2=女
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsured_Sex()), 0,body, 225, 1);
			//			23		被保人生日		ASC	10	格式为YYYY-MM-DD，
			if(saleOrderBean.getInsured_brithday()!=null){
				byte[] Insured_brithdaybyte = TypeConversion.stringToAscii(saleOrderBean.getInsured_brithday());
				System.arraycopy(Insured_brithdaybyte, 0,body, 226, Insured_brithdaybyte.length);
			}
			//			24		被保人证件类型		HEX	1	
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsured_card_type()), 0,body, 236, 1);
			//			25		被保人证件号码		ASC	18
			if(saleOrderBean.getInsured_card_No()!=null){
				byte[]  Insured_card_Nobyte= TypeConversion.stringToAscii(saleOrderBean.getInsured_card_No());
				System.arraycopy(Insured_card_Nobyte, 0,body, 237, Insured_card_Nobyte.length);
			}
			//			26		投保人和被保人关系		HEX	1	没有录入投保人和被保人关系时送0x00。
			System.arraycopy(TypeConversion.intToBytes(saleOrderBean.getInsured_relation()), 0,body, 255, 1);
			//			27		受益人姓名		ASC	30	最多30字节
			if(saleOrderBean.getBeneficlary_name()!=null){
				byte[] Beneficlary_namebyte = TypeConversion.stringToAscii(saleOrderBean.getBeneficlary_name());
				System.arraycopy(Beneficlary_namebyte, 0,body, 256, Beneficlary_namebyte.length);
			}
			//			28		学校		ASC	20
			if(saleOrderBean.getSchool()!=null){
				byte[] Schoolbyte = TypeConversion.stringToAscii(saleOrderBean.getSchool());
				System.arraycopy(Schoolbyte, 0,body, 286, Schoolbyte.length);
			}
			//			29		班级		ASC	16
			if(saleOrderBean.getSchoolClass()!=null){
				byte[] SchoolClassbyte = TypeConversion.stringToAscii(saleOrderBean.getSchoolClass());
				System.arraycopy(SchoolClassbyte, 0,body, 306, SchoolClassbyte.length);
			}
			//			30		航班号/客运班次		ASC	10
			if(saleOrderBean.getTrainnumber()!=null){
				byte[] Trainnumberbyte = TypeConversion.stringToAscii(saleOrderBean.getTrainnumber());
				System.arraycopy(Trainnumberbyte, 0,body, 322, Trainnumberbyte.length);
			}
			//			31		客票/门票号码/卡号等		ASC	20	
			if(saleOrderBean.getTrainticket()!=null){
				byte[] Trainticketbyte =  TypeConversion.stringToAscii(saleOrderBean.getTrainticket());
				System.arraycopy(Trainticketbyte, 0,body, 332, Trainticketbyte.length);
			}
			//			32		其它要素集合字节数		HEX	2	
			if(reamarkbyte!=null){
				System.arraycopy(TypeConversion.intToBytes(reamarkbyte.length), 0,body, 352, 2);
				//				33		其它要素集合		ASC	变长最大500	用来上报其它输入信息，格式为：提示符＋输入信息，可以有多个其它信息，用0x0d分隔，
				System.arraycopy(reamarkbyte, 0,body, 354, reamarkbyte.length);
			}
			// 构造上行命令对象
			UpCommandBean upCommandBean = new UpCommandBean();
			upCommandBean.setCommandCode(CommandConstant.CMD_SALE2);
			upCommandBean.setBody(body);
		
			final UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);

			//在当前线程中执行命令 并在回调函数中处理返回结果:创建命令回调函数
			UpCommandHandler upCommandHandler = new UpCommandHandler(new UpCommandHandlerListener(context){
				//处理具体下行命令
				public void handlerOthorMsg(int what,Object object){ 
					DownCommandParse downCommandParse = (DownCommandParse)object;
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
					if(downCommandBean.getAnswerCode().equals("0")){//销售成功
						byte[] data = downCommandBean.getBody();
						try {
							logger.debug("单证号码:"+ TypeConversion.asciiToString(data, 3, 20));
							logger.debug("保单号码"+ TypeConversion.asciiToString(data, 23, 20));
							logger.debug("投保单号:"+ TypeConversion.asciiToString(data, 43, 20));
							logger.debug("保险起期:"+ TypeConversion.asciiToString(data, 63, 19));
							logger.debug("保险终期:"+ TypeConversion.asciiToString(data, 82, 19));
							logger.debug("效验码:"+ TypeConversion.bytesToInt(data,101));
							String Insu_No = TypeConversion.asciiToString(data, 23, 20);
							saleOrderBean.setInsu_No(Insu_No);
							saleOrderBean.setProposalForm_No(TypeConversion.asciiToString(data, 43, 20));
							// 保存销售数据
							saleOrderBean.setCheck_state(false);
							save(saleOrderBean);
							btDialog.dismiss();
//							billNo.getText().toString();
//							String newBillNo = addBillNo(billNo.getText().toString());
//							billNo.setText(newBillNo);
							DialogUtils.showMessageAlertDlg(context,StringUtils.getStringFromValue(R.string.apsai_common_advise),"销售成功",
									new OnAlertDlgSureBtn() {
								        public void OnSureBtn() {
								        	//打印出单数据，首先检查蓝牙打印机的情况
											PrinterSelectUtils.printCheckResult(context, new PrinterSelectUtils.OnPrintConnectedListen(){
											public void onPrintConnected(){
									        	// 打印
									        	PrinterEngine printerEngine = new PrinterEngine(null,insuranceBean,inputValueMap,saleOrderBean);
									        	try {
									        		printerEngine.printInsuOrder();
									        	} catch (Exception e) {
									        		e.printStackTrace();
									        	}
											}});
								        }
								    });
							//返回成功消息\
							Message msg = handler.obtainMessage(0);
							handler.sendMessage(msg);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
					}else{//销售失败
						btDialog.dismiss();
						DialogUtils.showMessageAlertDlg(context, StringUtils.getStringFromValue(R.string.apsai_common_warning),
								downCommandBean.getAnswerMsg(), null);
						//返回失败消息
						Message msg = handler.obtainMessage(1);
						handler.sendMessage(msg);
					}
				}
				
				 //外部环境失败处理 
				public void handlerInfo(){
					btDialog.dismiss();
					
				}
			});
			
			commandControl.sendUpCommandInThread(upCommandParse, upCommandHandler);
			

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
   }
   
   /**
   @roseuid 4DF81F3B003E
    */
   public void print() 
   {
    
   }
   
   /**
    * 保存数据到数据库
   @roseuid 4DF81F470109
    */
   public long save(SaleOrderBean saleOrderBean) {
	   return dbOperater.insert(DBBean.TB_SALE_ORDER, saleOrderBean);
   }
   
   /**
   @return SaleOrderBean
   @roseuid 4DF822DD01E4
    */
   public SaleOrderBean getOrderList() 
   {
    return null;
   }
   
   /**
    * 获得新保单号
    * @param BillNo
    * @return
    */
   public String addBillNo(String BillNo){
		String newBillNo = "";
		int j = 0;
		for(int i = BillNo.length()-1; i >=0 ; i--){
			 String item = String.valueOf(BillNo.charAt(i));
			 if(item.matches("[0-8]")){
				   j = Integer.valueOf(item)+1;
				   newBillNo = String.valueOf(j) + newBillNo  ;
				   break;
			 }else if(item.matches("[9]")){
				 newBillNo = 0 + newBillNo;
				
			 }else{
				break; 
			 }		 
		}
		int newlengt = newBillNo.length();
		return BillNo.substring(0, BillNo.length()-newlengt)+newBillNo;   
   }
   
   /**
    * 检查数据合法性
    * @param insuPlanBean
    * @param saleOrderBean
    * @return
    */
   public boolean checkSaleOrderBean(InsuPlanBean insuPlanBean,SaleOrderBean saleOrderBean){
	   List<InsuPlanDefine> insuPlanDefinelist =  insuPlanBean.getInsuPlanDefineList();
	   List<InsuPlanAdditional> insuPlanAdditionalList = insuPlanBean.getInsuPlanAdditionalList();
	   
	   StringBuffer msg = new StringBuffer();
	   // 检查性别
	   InsuPlanDefine insuPlanDefine = insuPlanDefinelist.get(0);
	   if(!insuPlanDefine.sex.equals("")){
		   if(!insuPlanDefine.sex.equals(saleOrderBean.getInsured_Sex())){
			   msg.append(StringUtils.getStringFromValue(R.string.apsai_insu_sale_check_sex_error)).append("\n");
		   }
	   }
	   //检查年龄
	   int age = TimeUtils.bithdayToAge(saleOrderBean.getInsured_brithday());
	   logger.debug("被保人年龄:"+age);
	   if((!insuPlanDefine.begage.equals(""))&&(!insuPlanDefine.endage.equals(""))){
		   int begage = Integer.valueOf(insuPlanDefine.begage); 
		   int endage = Integer.valueOf(insuPlanDefine.endage);
		   if(!((begage<=age)&&(endage > age))){
			   msg.append(StringUtils.getStringFromValue(R.string.apsai_insu_sale_check_age_error)).append("\n");
		   }
	   }
	   
	   // 检查工作类型
	   if((!insuPlanDefine.profession.equals(""))&&(saleOrderBean.Insured_WorkNo.equals(""))){
		   int profession = Integer.valueOf(insuPlanDefine.profession);
		   String professionstr = Integer.toBinaryString(profession);
		   int WorkNo = Integer.valueOf(saleOrderBean.Insured_WorkNo); 
		   if(professionstr.substring(WorkNo-1, WorkNo).equals("1")){
			   msg.append(StringUtils.getStringFromValue(R.string.apsai_insu_sale_check_work_type_error)).append("\n");
		   }
	   }

	   // 检查保险起期是否合法
	   if(insuPlanAdditionalList.size()>1){
		   try {
			   InsuPlanAdditional insuPlanAdditional = insuPlanAdditionalList.get(0);
			   int begday =  Integer.valueOf(insuPlanAdditional.begday);
			   Calendar calendar = Calendar.getInstance();
			   calendar.add(calendar.DATE,begday);
			   long begdaydate = calendar.getTimeInMillis();
			   int endday = Integer.valueOf(insuPlanAdditional.endday);
			   calendar = Calendar.getInstance();
			   calendar.add(calendar.DATE,endday);
			   long enddaydate = calendar.getTimeInMillis();
			   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   Date Insu_begin_date = simpleDateFormat.parse(saleOrderBean.getInsu_begin_date()+
					   " "+saleOrderBean.getInsu_begin_time());
			   
			   if((Insu_begin_date.getTime()<=begdaydate)||(Insu_begin_date.getTime()>=enddaydate)){
				   msg.append(StringUtils.getStringFromValue(R.string.apsai_insu_sale_check_begday_error)).append("\n");
			   }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	   }
	   
	   if(!msg.toString().equals("")){
		   DialogUtils.showMessageAlertDlg(context, 
				   StringUtils.getStringFromValue(R.string.apsai_common_warning),
				   msg.toString(), null);
		   
		   //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		   return false;
	   }else{
		   return true;
	   }
	   
   }
}
