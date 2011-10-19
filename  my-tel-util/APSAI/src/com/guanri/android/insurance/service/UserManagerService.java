package com.guanri.android.insurance.service;

import java.io.UnsupportedEncodingException;

import android.os.Handler;
import android.os.Looper;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.CommandParseException;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.insuplan.PareFileToObject;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 用户管理DAO
 * 
 * @author Administrator
 * 
 */
public class UserManagerService {
	public static Logger logger = Logger.getLogger(InsuPlanManagerService.class);// 日志对象;

	private CommandControl commandControl = null;

	private String operator_id = null;

	public UserManagerService() {
		this.commandControl = CommandControl.getInstance();

		operator_id = SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.LOGINUSER);
	}

	/**
	 * 添加用户
	 * 
	 * @param userNo
	 * @param userName
	 * @param userPwd
	 */

	public boolean addUser(final String userNo, final String userName, final String userPwd, final Handler messageHandler) {

		new Thread() {
			public void run() {
				Looper.prepare();
				DownCommandParse downCommandParse = null;
				try {
					logger.debug("getFilesDir:" + operator_id);
					byte[] body = new byte[47];
					/**
					 * 1 分公司代码 ASC 3 2 终端ID ASC 8 3 终端校验码 ASC 8 4 管理员工号 ASC 6 5
					 * 待增加的操作员工号 ASC 6 6 待增加的操作员姓名 ASC 10 7 待增加的操作员初始密码 ASC 6
					 */
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, body, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body, 11, 8);
					System.arraycopy(TypeConversion.stringToAscii(operator_id), 0, body, 19, 6);

					byte[] userNoBuff = TypeConversion.stringToAscii(userNo);
					System.arraycopy(userNoBuff, 0, body, 25, userNoBuff.length);

					byte[] userNameBuff = TypeConversion.stringToAscii(userName);
					System.arraycopy(userNameBuff, 0, body, 31, userNameBuff.length);

					byte[] userPwdBuff = TypeConversion.stringToAscii(userNo);
					System.arraycopy(userPwdBuff, 0, body, 41, userPwdBuff.length);

					UpCommandBean upCommandBean = new UpCommandBean();
					upCommandBean.setCommandCode(CommandConstant.CMD_ADD_OPE);
					upCommandBean.setBody(body);
					// 构造上行命令对象
					UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
					downCommandParse = commandControl.sendUpCommand(upCommandParse);

					upCommandBean = upCommandParse.getUpCommandBean();
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();

					if (downCommandBean.getAnswerCode().equals("0")) {
						// 用户添加成功
						try {
							byte[] data = downCommandBean.getBody();
							logger.debug("终端ID:" + TypeConversion.asciiToString(data, 3, 8));
							logger.debug("增加成功的操作员工号" + TypeConversion.asciiToString(data, 11, 6));
							logger.debug("增加成功的操作员姓名:" + TypeConversion.asciiToString(data, 17, 10));
							logger.debug("增加成功的操作员初始密码:" + TypeConversion.asciiToString(data, 27, 6));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						// 用户添加失败
						String exceptionstr = "";
						if (downCommandBean.getAnswerMsg() != null) {
							exceptionstr = downCommandBean.getAnswerMsg();
						}
						if (downCommandBean.getMark() != null) {
							exceptionstr = exceptionstr + downCommandBean.getMark();
						}
						throw new CommandParseException(exceptionstr);

					}

					messageHandler.sendMessage(messageHandler.obtainMessage(0));
					// btDialog.dismiss();
				} catch (CommandParseException cmde) {
					String exceptionstr = "";
					exceptionstr = cmde.getMessage();
					messageHandler.sendMessage(messageHandler.obtainMessage(-1, exceptionstr));
				} catch (Exception io) {
					io.printStackTrace();
					// btDialog.dismiss();
					logger.error(io.getMessage());
					messageHandler.sendMessage(messageHandler.obtainMessage(-1,
							StringUtils.getStringFromValue(R.string.apsai_common_server_link_error)));
				}

			}
		}.start();

		return true;

	}

	/**
	 * 删除用户
	 * 
	 * @param userNo
	 */
	public boolean delUser(final String userNo, final String userName, final Handler messageHandler) {

		new Thread() {
			public void run() {
				Looper.prepare();
				DownCommandParse downCommandParse = null;
				try {
					logger.debug("getFilesDir:" + operator_id);
					byte[] body = new byte[41];
					/**
					 * 1 分公司代码 ASC 3 2 终端ID ASC 8 3 终端校验码 ASC 8 4 管理员工号 ASC 6 5
					 * 待删除的操作员工号 ASC 6 6 待删除的操作员姓名 ASC 10
					 */
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, body, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body, 11, 8);
					System.arraycopy(TypeConversion.stringToAscii(operator_id), 0, body, 19, 6);

					byte[] userNoBuff = TypeConversion.stringToAscii(userNo);
					System.arraycopy(userNoBuff, 0, body, 25, userNoBuff.length);
					byte[] userNameBuff = TypeConversion.stringToAscii(userName);

					System.arraycopy(userNameBuff, 0, body, 31, userNameBuff.length);

					UpCommandBean upCommandBean = new UpCommandBean();
					upCommandBean.setCommandCode(CommandConstant.CMD_DEL_OPE);
					upCommandBean.setBody(body);
					// 构造上行命令对象
					UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
					downCommandParse = commandControl.sendUpCommand(upCommandParse);

					upCommandBean = upCommandParse.getUpCommandBean();
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();

					if (downCommandBean.getAnswerCode().equals("0")) {
						// 用户添加成功
						try {
							byte[] data = downCommandBean.getBody();
							logger.debug("终端ID:" + TypeConversion.asciiToString(data, 3, 8));
							logger.debug("删除成功的操作员工号" + TypeConversion.asciiToString(data, 11, 6));
							logger.debug("删除成功的操作员姓名:" + TypeConversion.asciiToString(data, 17, 10));

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						// 失败
						String exceptionstr = "";
						if (downCommandBean.getAnswerMsg() != null) {
							exceptionstr = downCommandBean.getAnswerMsg();
						}
						if (downCommandBean.getMark() != null) {
							exceptionstr = exceptionstr + downCommandBean.getMark();
						}
						throw new CommandParseException(exceptionstr);
					}

					messageHandler.sendMessage(messageHandler.obtainMessage(0));
					// btDialog.dismiss();
				} catch (CommandParseException cmde) {
					String exceptionstr = cmde.getMessage();
					messageHandler.sendMessage(messageHandler.obtainMessage(-1, exceptionstr));
				} catch (Exception io) {
					io.printStackTrace();
					// btDialog.dismiss();
					logger.error(io.getMessage());
					messageHandler.sendMessage(messageHandler.obtainMessage(-1,
							StringUtils.getStringFromValue(R.string.apsai_common_server_link_error)));
				}
			}
		}.start();

		return true;
	}

	/**
	 * 修改用户密码
	 * 
	 * @param userNo
	 * @param userPwd
	 */
	public boolean updateUser(final String userNo, final String userOldPwd, final String userNewPwd, final Handler messageHandler) {
		new Thread() {
			public void run() {
				Looper.prepare();
				DownCommandParse downCommandParse = null;
				try {
					logger.debug("getFilesDir:" + operator_id);
					byte[] body = new byte[44];
					/**
					 * 1 分公司代码 ASC 3 
					 * 2 终端ID ASC 8 
					 * 3 终端校验码 ASC 8 
					 * 4 管理员工号 ASC 6 
					 * 5 待删除的操作员工号 ASC 6 
					 * 6 待删除的操作员姓名 ASC 10
					 */
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, body, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body, 11, 8);
					System.arraycopy(TypeConversion.stringToAscii(operator_id), 0, body, 19, 6);
					byte[] userNoBuff = TypeConversion.stringToAscii(userNo);
					System.arraycopy(userNoBuff, 0, body, 25, userNoBuff.length);
					byte[] userOldPwdBuff = TypeConversion.stringToAscii(userNo);
					System.arraycopy(userOldPwdBuff, 0, body, 31, userOldPwdBuff.length);
					byte[] userNewPwdBuff = TypeConversion.stringToAscii(userNewPwd);
					System.arraycopy(userNewPwdBuff, 0, body, 37, userNewPwdBuff.length);
					UpCommandBean upCommandBean = new UpCommandBean();
					upCommandBean.setCommandCode(CommandConstant.CMD_DEL_OPE);
					upCommandBean.setBody(body);
					// 构造上行命令对象
					UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					upCommandBean = upCommandParse.getUpCommandBean();
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
					if (downCommandBean.getAnswerCode().equals("0")) {
						// 用户添加成功
						try {
							byte[] data = downCommandBean.getBody();
							logger.debug("终端ID:" + TypeConversion.asciiToString(data, 3, 8));
							logger.debug("删除成功的操作员工号" + TypeConversion.asciiToString(data, 11, 6));
							logger.debug("删除成功的操作员姓名:" + TypeConversion.asciiToString(data, 17, 10));

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						// 失败
						String exceptionstr = "";
						if (downCommandBean.getAnswerMsg() != null) {
							exceptionstr = downCommandBean.getAnswerMsg();
						}
						if (downCommandBean.getMark() != null) {
							exceptionstr = exceptionstr + downCommandBean.getMark();
						}
						throw new CommandParseException(exceptionstr);
					}

					messageHandler.sendMessage(messageHandler.obtainMessage(0));
					// btDialog.dismiss();
				} catch (CommandParseException cmde) {
					String exceptionstr = "";
					exceptionstr = cmde.getMessage();
					messageHandler.sendMessage(messageHandler.obtainMessage(-1, exceptionstr));
				} catch (Exception io) {
					io.printStackTrace();
					// btDialog.dismiss();
					logger.error(io.getMessage());
					messageHandler.sendMessage(messageHandler.obtainMessage(-1,
							StringUtils.getStringFromValue(R.string.apsai_common_server_link_error)));
				}
			}
		}.start();

		return true;
	}

}
