package com.guanri.android.insurance.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.InitBussIdActivity;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandHandler.UpCommandHandlerListener;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

public class InitializeBusinessIdService {

	public static Logger logger = Logger.getLogger(LoginService.class);//日志对象
	private CommandControl commandControl = null;
	private Context context = null;

	
	public InitializeBusinessIdService(Context ctx)
	{
		commandControl = CommandControl.getInstance();
		context = ctx;
	}
	
	
	/**
	 * 保存返回的数据到配置文件
	 * @param body
	 */
	private void saveMsgBodyToConfig(byte[] body) {
		//

		/*
		 * 分公司代码 BranchID ASC 3 表示本终端所属的分公司的代码 终端校验码 ComPSW ASC 8
		 * 终端存储该校验码，以后的通信上报此校验码，与POSID一起验证终端合法性 管理员工号(预留) ManID ASC 6
		 * 终端注册成功后，需要在显示屏上显示该号码，以便管理员参考校对 管理员姓名(预留) ManName ASC 10
		 * 表示对应的管理员的姓名，终端处理同“3-管理员工号” 站点名称 StationName ASC 16 表示本终端所处的站点的名称
		 * 后台短信接入号码 SMSID ASC 12 后台短信的接入号码，通过这个后台号码发送的信息，终端会解析显示出来。
		 */

		String strBranchID = null;
		String strPostCheck = null;
		String strStationName = null;
		String strSMSID = null;
		try {

			strBranchID = TypeConversion.asciiToString(body, 0, 3); // 分公司代码
			strPostCheck = TypeConversion.asciiToString(body, 3, 8); // 终端校验码
			
			//strStationName = TypeConversion.asciiToString(body, 27, 16);// 本终端所处的站点的名称
			
			strSMSID = TypeConversion.asciiToString(body, 43, 12);// 后台短信的接入号码
			
			/*
			SharedPreferencesUtils.setConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.BRANCHID, strBranchID);
			
			SharedPreferencesUtils.setConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.COMPSW, strPostCheck);
			
			
			SharedPreferencesUtils.setConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.STATIONAME, strStationName);
			
			
			SharedPreferencesUtils.setConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.SMSID, strSMSID);
			*/
			
			SharedPreferencesUtils.setConfigString(
					SharedPreferencesUtils.COMFIG_INFO,
					SharedPreferencesUtils.COMPSW, strPostCheck); 
			
			//重新加载终端信息配置文件 
			CommandConstant.COMFIG_POS_ID = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.POS_ID);//终端ID
			CommandConstant.COMFIG_COM_PWD = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.COMPSW);//终端校验码
			CommandConstant.COMFIG_SIM_CODE = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.SIMCODE);//SIM卡号码
			CommandConstant.COMFIG_BRANCH_ID = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO,SharedPreferencesUtils.BRANCHID);//分公司代码

	        
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 终端业务ID初始化
	 * @param postID
	 * @param postCheck
	 * @param simcard
	 * @return
	 */
	public boolean initializeBusinessID(String postID, String postCheck, String simcard,final Dialog mainDialog)
	{
		
		final ProgressDialog btDialog = new ProgressDialog(context);
		btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_init_register)); // title     
		btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_initialzing));//进度是否是不确定的，这只和创建进度条有关
		btDialog.show();

		//构造命令体
		byte[] body = new byte[68];
		try{
			/*
				终端ID	PosID	ASC	8	
				终端初始化注册码	InitPSW	ASC	8	终端初始化的时候后台分配的注册码
				SIM卡号码	SIMCode	ASC	12	表示终端安装的SIM卡的号码
				当前软件版本号	CurSoftVer	ASC	20	终端必须把软件版本发到后台
				当前硬件版本	CurHardVer	ASC	20	当前硬件版本号
			*/

			System.arraycopy(TypeConversion.stringToAscii(postID), 0, body, 0, postID.length());
			System.arraycopy(TypeConversion.stringToAscii(postCheck), 0, body, 8, postCheck.length());
			System.arraycopy(TypeConversion.stringToAscii(simcard), 0, body, 16, simcard.length());
			System.arraycopy(TypeConversion.stringToAscii("V1.2"), 0, body, 28, 4);
			System.arraycopy(TypeConversion.stringToAscii("V1.2"), 0, body, 48, 4);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//构造上行命令对象
		UpCommandBean upCommandBean = new UpCommandBean();
		upCommandBean.setCommandCode(CommandConstant.CMD_INIT);
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
				
				
				if(downCommandBean.getAnswerCode().equals("0")){//初始化业务ID成功
					//初始化批次号 从1开始
					SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
							SharedPreferencesUtils.CHECK_ID, "1");
					//保存返回的数据到配置文件
					saveMsgBodyToConfig(downCommandBean.getBody());
					//初始化业务ID成功
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_init_succ_msg));
					builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_init_succ));
					builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(mainDialog!=null) mainDialog.dismiss();
							//进入登录界面
							((InitBussIdActivity)context).entryLoginActivity();
						}
					});
					builder.create().show();
				}else{
					//初始化业务ID失败
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(downCommandBean.getAnswerMsg());
					builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_init_failure));
					builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					builder.create().show();
					
				}
			}
			
			 //外部环境失败处理 
			public void handlerInfo(){
				btDialog.dismiss();
			}

		});
		
		
		return commandControl.sendUpCommandInThread(upCommandParse,upCommandHandler);

	}
	
	
}
