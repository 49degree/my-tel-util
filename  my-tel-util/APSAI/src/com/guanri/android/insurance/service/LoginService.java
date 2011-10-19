package com.guanri.android.insurance.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.MainActivity;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.OperateLogBean;
import com.guanri.android.insurance.bean.OperatorRecordBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.CryptionControl;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandHandler.UpCommandHandlerListener;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TimeUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 初始化化
 * 
 * @author Administrator
 * 
 */
public class LoginService {
	public static Logger logger = Logger.getLogger(LoginService.class);//日志对象
	public DBOperator dBOperator = null;
	private CommandControl commandControl;
	private Context context ;
	/**
	 * @roseuid 4DF8330D0177
	 */
	public LoginService(Context context) {
		this.commandControl = CommandControl.getInstance();
		this.context = context;
		dBOperator = DBOperator.getInstance();
	}

	/**
	 * @return boolean
	 * @roseuid 4DF732B2038A
	 */
	public boolean checkBussId() {
		return true;
	}

	/**
	 * @return boolean
	 * @roseuid 4DF732F8007D
	 */
	public boolean initBussId() {
		return true;
	}

	/**
	 * 验证用户密码
	 * @return boolean
	 * @roseuid 4DF7331C0280
	 */
	public boolean checkOperator(final String userId, String userPsd) {

		final ProgressDialog btDialog = new ProgressDialog(context);
		btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_init_login)); // title     
		btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_init_login_loading));//进度是否是不确定的，这只和创建进度条有关
		btDialog.show();

		//构造命令体
		byte[] body = new byte[102];
		try{
			/*
			分公司代码	BranchID	ASC	3	表示本终端所属的分公司的代码
			终端ID	PosID	ASC	8	
			终端校验码	ComPSW	ASC	8	
			SIM卡卡号	SIMCode	ASC	12	
			用户工号	UserID	ASC	6	
			用户密码	UserPassword	ASC	6	
			软件版本号	SoftVer	ASC	20	表示本终端当前使用的软件版本号
			硬件版本号	HardVer	ASC	20	表示本终端当前所使用的硬件版本号
			终端的类型	PosType	ASC	10	表示本终端的机器类型
			错误信息版本(预留)	MsgVer	HEX	1	当前错误信息版本
			*/
			String strPostId = SharedPreferencesUtils.getConfigString(
						SharedPreferencesUtils.COMFIG_INFO,
						SharedPreferencesUtils.POS_ID);			
			String strPostCheck = SharedPreferencesUtils.getConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.COMPSW);
			String strSimCard = SharedPreferencesUtils.getConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.SIMCODE);
			if(CommandConstant.COMFIG_BRANCH_ID.length()>0){
				System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_BRANCH_ID), 0, body, 0, 3);
			}
			System.arraycopy(TypeConversion.stringToAscii(strPostId), 0, body, 3, strPostId.length());
			System.arraycopy(TypeConversion.stringToAscii(strPostCheck), 0, body, 11, strPostCheck.length());
			System.arraycopy(TypeConversion.stringToAscii(strSimCard), 0, body, 19, strSimCard.length());
			System.arraycopy(TypeConversion.stringToAscii(userId), 0, body, 31, userId.length());
			System.arraycopy(TypeConversion.stringToAscii(userPsd), 0, body, 37, userPsd.length());
			System.arraycopy(TypeConversion.stringToAscii("V1.2"), 0, body, 43, 4);
			System.arraycopy(TypeConversion.stringToAscii("V1.2"), 0, body, 63, 4);

		
		}catch(Exception e){
			e.printStackTrace();
		}
		//构造上行命令对象
		UpCommandBean upCommandBean = new UpCommandBean();
		upCommandBean.setCommandCode(CommandConstant.CMD_LOGIN);
		upCommandBean.setBody(body);
		UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);

		//创建命令回调函数
		UpCommandHandler upCommandHandler = new UpCommandHandler(new UpCommandHandlerListener(context){
			//处理具体下行命令
			public void handlerOthorMsg(int what,Object object){ 
				btDialog.dismiss();
				DownCommandParse downCommandParse = (DownCommandParse)object;
				DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
				logger.debug("AnswerCode11:"+downCommandBean.getAnswerCode());
				logger.debug("CommandCode11:"+downCommandBean.getCommandCode());
				logger.debug("AnswerMsg11:"+downCommandBean.getAnswerMsg());
				if(downCommandBean.getAnswerCode().equals("0")){//登陆成功
					logInfo(userId,StringUtils.getStringFromValue(R.string.apsai_init_login_succ));//记录日志
					//解析数据
					try{
						SharedPreferencesUtils.setConfigString(
								SharedPreferencesUtils.COMFIG_INFO,
								SharedPreferencesUtils.LOGINUSER,userId);
						UserInfoBean userInfo = parseUserInfo(downCommandBean.getBody());
						userInfo.setUserId(userId);
						MainApplication.getInstance().setUserInfo(userInfo);//parseUserInfo(downCommandBean.getBody());
						//获取员工姓名
						OperatorRecordService operatorRecordService = new OperatorRecordService(context);
						operatorRecordService.recordOperatorInfo(userInfo.getUserId(),userInfo.getUserName());
						//operatorRecordService.dBOperator.release();//释放数据库连接
						
						//获取动态密钥
						byte[] dynamicKey = new byte[8];
						System.arraycopy(downCommandBean.getBody(), 0, dynamicKey, 0, dynamicKey.length);
						CryptionControl.getInstance().setDynamicKey(dynamicKey);
						((Activity)context).finish();
						// 保存登录用户名
						SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
								SharedPreferencesUtils.USERNO, userId);
						//打开主界面
						Intent mainIntent = new Intent(context, MainActivity.class);
						context.startActivity(mainIntent);
					
					}catch(Exception e){
						e.printStackTrace();
						//解析数据异常
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setMessage(downCommandBean.getAnswerMsg());
						builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_init_login_failure));
						builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//dosome thing
							}
						});
					}
				}else{
					logInfo(userId,StringUtils.getStringFromValue(R.string.apsai_init_login_failure)+":"+downCommandBean.getAnswerMsg());//记录日志
					//登录失败
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(downCommandBean.getAnswerMsg());
					builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_init_login_failure));
					builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//dosome thing
							//System.exit(0);
						}
					});
					builder.create().show();
				}
			}
			
			 //外部环境失败处理 
			public void handlerInfo(){
				if(btDialog!=null&&btDialog.isShowing()){
					btDialog.dismiss();
				}
				
				logInfo(userId,StringUtils.getStringFromValue(R.string.apsai_init_login_failure)+":"+
						StringUtils.getStringFromValue(R.string.apsai_common_cmd_error));
			}
			


		});

		return commandControl.sendUpCommandInThread(upCommandParse,upCommandHandler);
		
	}
	
	/**
	 * 解析登陆返回的用户数据
	 * @param body
	 * @return
	 */
	private UserInfoBean parseUserInfo(byte[] body) throws UnsupportedEncodingException{
//		private String PSW	        =null;//   ASC	8	终端后续的通信均采用这个动态密钥加密
//		private String curTime	    =null;//   ASC	19	表示服务器当前的时间 YYYY-MM-DD hh:mm:ss以下同
//		private byte isOK		  =0;//    HEX	1	0：表示可用；1：表示禁止使用
//		private byte userLevel	  =0;//    	HEX	1	0x00=管理员；0x01=一般操作员
//		private String userName		  =null;//    ASC	10	返回该工号在后台注册的姓名
//		private String firstIP		  =null;//    HEX	4	如0x32 0x64 0x02 0xC8表示IP地址为50.100.2.200
//		private short firstPort		=0;//      HEX	2	服务器的端口，小端模式，如0xB3 0x15表示值0x15B3，表示端口号5555
//		private String firstUseTime		  =null;//    ASC	19	YYYY-MM-DD HH:MM:SS终端在该时间后，将此组IP、端口作为A组IP、主端口使用，如果没有新的A组IP等信息，则全部填0x00
//		private String secondIP		  =null;//    HEX	4	
//		private short secondPort	  =0;//    	HEX	2	
//		private String secondUseTime		  =null;//    ASC	19	定于参考A组

//      private String NewSoftVer		=null;//     当前系统中最新软件版本 ASC	20	
//		private byte mustUpdate		=0;//      HEX	1	0：不需要升级1：必须进行升级操作2：功能改善，建议性升级
//		private byte newErrorMsg	=0;//      	HEX	1	0：表示没有需要更新的信息其它表示需要更新的错误信息版本
//		private String SMSID		    =null;// 后台短信接入号码 ASC	12	
//		private String MSG		      =null;//ASC	160	终端收到此消息，如果有内容，则必须显示出来提示用户
//		private String[][] insuPlanRecord	  =null;//可以销售的业务方案列表ASC	100	只针对操作员有效，每个操作员同时最多可以10个业务方案，采用CARDCODE(8字节)＋PLANNO（2字节）的方式下载
//		private String branchName		=null;//     分公司名称(签单机构) ASC	30	
//		private String branchAddr		=null;//       分公司地址(保险分公司地址)ASC	40	
//		private String stationName	=null;//机构名称ASC	30	目前只有平安\泰康\百年有该字段 ASC	16	目前只有泰康有该字段
//		private String stationTel	=null;//分公司联系电话 ASC	16	目前只有泰康有该字段
		UserInfoBean userInfoBean = new UserInfoBean();
		userInfoBean.setPSW(TypeConversion.getSomeBytesFromSource(body,0,8));
		userInfoBean.setCurTime(TypeConversion.asciiToString(body,8,19));
		userInfoBean.setIsOK(body[27]);
		userInfoBean.setUserLevel(body[28]);
		userInfoBean.setUserName(TypeConversion.asciiToString(body,29,10));
		userInfoBean.setFirstIP(TypeConversion.bytesToSingleNum(body, 39, 1)
				+"."+TypeConversion.bytesToSingleNum(body,40,1)+"."
				+TypeConversion.bytesToSingleNum(body,41,1)+"."
				+TypeConversion.bytesToSingleNum(body,42,1));
		userInfoBean.setFirstPort(TypeConversion.bytesToShort(body, 43));
		userInfoBean.setFirstUseTime(TypeConversion.asciiToString(body, 45,19));
		
		userInfoBean.setSecondIP(TypeConversion.bytesToSingleNum(body, 64, 1)
				+"."+TypeConversion.bytesToSingleNum(body,65,1)+"."
				+TypeConversion.bytesToSingleNum(body,66,1)+"."
				+TypeConversion.bytesToSingleNum(body,67,1));
		
		userInfoBean.setSecondPort(TypeConversion.bytesToShort(body, 68));
		userInfoBean.setSecondUseTime(TypeConversion.asciiToString(body, 70,19));
		
		userInfoBean.setNewSoftVer(TypeConversion.asciiToString(body, 89,20));
		
		userInfoBean.setMustUpdate(body[109]);
		userInfoBean.setNewErrorMsg(body[110]);
		
		
		
		userInfoBean.setSMSID(TypeConversion.asciiToString(body, 111,12));
		userInfoBean.setMSG(TypeConversion.asciiToString(body, 123,160));
		
//		private String[][] insuPlanRecord	  =null;//可以销售的业务方案列表ASC	100	只针对操作员有效，每个操作员同时最多可以10个业务方案，采用CARDCODE(8字节)＋PLANNO（2字节）的方式下载
//		private String branchName		=null;//     分公司名称(签单机构) ASC	30	
//		private String branchAddr		=null;//       分公司地址(保险分公司地址)ASC	40	
//		private String stationName	=null;//机构名称ASC	30	目前只有平安\泰康\百年有该字段 ASC	16	目前只有泰康有该字段
//		private String stationTel	=null;//分公司联系电话 ASC	16	目前只有泰康有该字段
		String[][] insuPlanRecord = new String[10][2];
		for(int i=0;i<10;i++){
			if(TypeConversion.bytesToLong(body, 283+(i*10))!=0){
				insuPlanRecord[i][0] = TypeConversion.asciiToString(body,283+(i*10),8);
				insuPlanRecord[i][1] = String.valueOf(TypeConversion.bytesToShort(body,291+(i*10)));
			}
		}
		userInfoBean.setBranchName(TypeConversion.asciiToString(body, 383, 30));
		userInfoBean.setBranchAddr(TypeConversion.asciiToString(body, 413, 40));
		userInfoBean.toString();
		
		return userInfoBean;
		
		
	}
	
	/**
	 * 记录日志
	 * @param userId
	 * @param logMemo
	 */
	private void logInfo(String userId,String logMemo){
		OperateLogBean operateLog = new OperateLogBean();
		operateLog.setOperator_name(StringUtils.getStringFromValue(R.string.apsai_init_login));
		operateLog.setOperator_id(userId);
		operateLog.setOperate_time(TimeUtils.getTimeString(new Date()));
		operateLog.setOperate_memo(logMemo);
		
		
		dBOperator.insert(DBBean.TB_OPERATE_LOG, operateLog);
		
	}
}
