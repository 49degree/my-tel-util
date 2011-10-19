package com.guanri.android.insurance.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.SaleOrderBackBean;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.bean.SaleOrderUselessBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.CommandParseException;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler.UpCommandHandlerListener;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 单证管理
 * 
 * @author Administrator
 * 
 */
public class InsuOrderOperateService {
	public static Logger logger = Logger.getLogger(InsuPlanManagerService.class);// 日志对象;
	private String operator_id;

	public DBOperator dbOperater;
	private Context context;
	private CommandControl commandControl = null;

	/**
	 * @roseuid 4DF8330F033C
	 */
	public InsuOrderOperateService(Context context) {
		this.commandControl = CommandControl.getInstance();
		this.context = context;
		dbOperater = DBOperator.getInstance();
		operator_id = "";
	}

	/**
	 * 退单
	 * 
	 * @param userNo
	 *            操作员工号
	 * @param userName
	 *            操作员姓名
	 * @param BillNo
	 *            单证号
	 * @param PolicyNo
	 *            保单号
	 * @param insuNo
	 *            投保单号
	 * @param batchNo
	 *            批次号
	 * @return
	 */
	public boolean backOrder(final String userNo, final String userName, final String BillNo, final String PolicyNo, final String insuNo,
			final int batchNo, final Handler messageHandler, final ProgressDialog btDialog) {
		btDialog.show();
		try {
			byte[] body = new byte[99];
			/**
			 * 1 分公司代码 ASC 3 2 终端ID ASC 8 3 终端校验码 ASC 8 4 管理员工号 ASC 6 5
			 * 待增加的操作员工号 ASC 6 6 待增加的操作员姓名 ASC 10 7 待增加的操作员初始密码 ASC 6
			 */
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, body, 3, 8);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body, 11, 8);
			byte[] byteuserNo = TypeConversion.stringToAscii(userNo);
			System.arraycopy(byteuserNo, 0, body, 19, byteuserNo.length);
			byte[] byteuserName = TypeConversion.stringToAscii(userName);
			System.arraycopy(byteuserName, 0, body, 25, byteuserName.length);
			byte[] byteBillNo = TypeConversion.stringToAscii(BillNo);
			System.arraycopy(byteBillNo, 0, body, 35, byteBillNo.length);
			byte[] bytePolicyNo = TypeConversion.stringToAscii(PolicyNo);
			System.arraycopy(bytePolicyNo, 0, body, 55, bytePolicyNo.length);
			byte[] byteinsuNo = TypeConversion.stringToAscii(insuNo);
			System.arraycopy(byteinsuNo, 0, body, 75, byteinsuNo.length);

			System.arraycopy(TypeConversion.intToBytes(batchNo), 0, body, 95, 4);
			// 构造上行命令对象
			UpCommandBean upCommandBean = new UpCommandBean();
			upCommandBean.setCommandCode(CommandConstant.CMD_REFUND);
			upCommandBean.setBody(body);

			final UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);

			// 在当前线程中执行命令 并在回调函数中处理返回结果:创建命令回调函数
			UpCommandHandler upCommandHandler = new UpCommandHandler(new UpCommandHandlerListener(context) {
				// 处理具体下行命令
				public void handlerOthorMsg(int what, Object object) {
					DownCommandParse downCommandParse = (DownCommandParse) object;
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
					if (downCommandBean.getAnswerCode().equals("0")) {
						// 成功
						try {
							byte[] data = downCommandBean.getBody();
							logger.debug("退单成功");
							logger.debug("单证号码:" + TypeConversion.asciiToString(data, 3, 20));
							logger.debug("保单号码" + TypeConversion.asciiToString(data, 23, 20));
							logger.debug("投保单号:" + TypeConversion.asciiToString(data, 43, 20));
							logger.debug("金额:" + TypeConversion.bytesToInt(data, 63));
							messageHandler.sendMessage(messageHandler.obtainMessage(0));

							int back_sum = TypeConversion.bytesToInt(data, 63);
							SaleOrderBackBean saleOrderBackBean = new SaleOrderBackBean();
							saleOrderBackBean.setPaper_number(BillNo);
							saleOrderBackBean.setInsu_no(PolicyNo);
							saleOrderBackBean.setCheck_id(batchNo);
							saleOrderBackBean.setProposalForm_No(insuNo);
							saleOrderBackBean.setOperator_id(userNo);
							Date date = new Date();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							String Operatetime = df.format(date);
							saleOrderBackBean.setOperate_time(Operatetime);
							saleOrderBackBean.setBack_sum(back_sum);
							saleOrderBackBean.setCheck_state(false);
							saveBackOrder(saleOrderBackBean);

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						// 失败
						btDialog.dismiss();
						DialogUtils.showMessageAlertDlg(context, StringUtils.getStringFromValue(R.string.apsai_common_warning),
								downCommandBean.getAnswerMsg(), null);
						// 返回失败消息
						Message msg = messageHandler.obtainMessage(1);
						messageHandler.sendMessage(msg);
					}
				}

				// 外部环境失败处理
				public void handlerInfo() {
					btDialog.dismiss();

				}
			});

			commandControl.sendUpCommandInThread(upCommandParse, upCommandHandler);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return true;

	}

	/**
	 * 废单
	 * 
	 * @roseuid wuxiang
	 */
	public boolean uselessOrder(final String userNo, final String userName, final String beginBillNo, final int number, final int batchNo,
			final Handler messageHandler, final ProgressDialog btDialog) {
		btDialog.show();
		try {
			byte[] body = new byte[61];
			/**
			 * 1 分公司代码 ASC 3 2 终端ID ASC 8 3 终端校验码 ASC 8 4 管理员工号 ASC 6 5
			 * 待增加的操作员工号 ASC 6 6 待增加的操作员姓名 ASC 10 7 待增加的操作员初始密码 ASC 6
			 */
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID), 0, body, 3, 8);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body, 11, 8);
			byte[] byteuserNo = TypeConversion.stringToAscii(userNo);
			System.arraycopy(byteuserNo, 0, body, 19, byteuserNo.length);
			byte[] byteuserName = TypeConversion.stringToAscii(userName);
			System.arraycopy(byteuserName, 0, body, 25, byteuserName.length);
			byte[] bytebeginBillNo = TypeConversion.stringToAscii(beginBillNo);
			System.arraycopy(bytebeginBillNo, 0, body, 35, bytebeginBillNo.length);
			System.arraycopy(TypeConversion.intToBytes(number), 0, body, 55, 2);
			System.arraycopy(TypeConversion.intToBytes(batchNo), 0, body, 57, 4);
			// 构造上行命令对象
			UpCommandBean upCommandBean = new UpCommandBean();
			upCommandBean.setCommandCode(CommandConstant.CMD_DESTROY);
			upCommandBean.setBody(body);

			final UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);

			// 在当前线程中执行命令 并在回调函数中处理返回结果:创建命令回调函数
			UpCommandHandler upCommandHandler = new UpCommandHandler(new UpCommandHandlerListener(context) {
				// 处理具体下行命令
				public void handlerOthorMsg(int what, Object object) {
					DownCommandParse downCommandParse = (DownCommandParse) object;
					DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
					if (downCommandBean.getAnswerCode().equals("0")) {
						// 成功
						try {
							byte[] data = downCommandBean.getBody();
							logger.debug("废单成功");
							logger.debug("作废单证开始号码:" + TypeConversion.asciiToString(data, 3, 20));
							logger.debug("作废单证数量:" + TypeConversion.asciiToString(data, 23, 2));
							logger.debug("作废成功否(0：表示作废成功；1：表示作废失败):" + TypeConversion.bytesToInt(data, 25));

							messageHandler.sendMessage(messageHandler.obtainMessage(0));
							// 保存记录
							SaleOrderUselessBean saleOrderUselessBean = new SaleOrderUselessBean();
							saleOrderUselessBean.setPaper_no(beginBillNo);
							saleOrderUselessBean.setNumber(number);
							saleOrderUselessBean.setOperator_id(userNo);
							saleOrderUselessBean.setCheck_id(batchNo);
							Date date = new Date();
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							String Operatetime = df.format(date);
							saleOrderUselessBean.setOperate_time(Operatetime);
							saleOrderUselessBean.setCheck_state(false);
							saveSselessOrder(saleOrderUselessBean);

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					} else {
						// 失败
						btDialog.dismiss();
						DialogUtils.showMessageAlertDlg(context, StringUtils.getStringFromValue(R.string.apsai_common_warning),
								downCommandBean.getAnswerMsg(), null);
						// 返回失败消息
						Message msg = messageHandler.obtainMessage(1);
						messageHandler.sendMessage(msg);
					}
				}

				// 外部环境失败处理
				public void handlerInfo() {
					btDialog.dismiss();

				}
			});

			commandControl.sendUpCommandInThread(upCommandParse, upCommandHandler);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return true;
	}

	/**
	 * 更新单据状态
	 * 
	 * @param BillNo
	 *            单据号
	 * @param state
	 *            状态
	 */
	public void updateOrderSQL(String BillNo, ContentValues values) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("BillNo=", BillNo);
		dbOperater.update(DBBean.TB_SALE_ORDER, values, params);
	}

	/**
	 * 获得可销售的保单列表
	 * 
	 * @param queryParam
	 * @return
	 */
	public List<Map<String, String>> queryInsuPlanList(String[] queryParam) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("useable=", "1");
		List<Map<String, String>> insunList = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD, queryParam, params);
		return insunList;
	}

	/**
	 * 返回编辑模板文件名
	 * 
	 * @param CardCode
	 * @param Planno
	 * @return
	 */
	public String getInsuEditName(String CardCode, String Planno) {
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "InsuEdit_name" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD, returnColumn, params);
		if (listmap.size() > 0) {
			return listmap.get(0).get("InsuEdit_name");
		} else
			return null;
	}

	/**
	 * 返回打印模板文件夹名
	 * 
	 * @param CardCode
	 * @param Planno
	 * @return
	 */
	public String getInsuPrtName(String CardCode, String Planno) {
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "InsuPrt_name" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD, returnColumn, params);
		if (listmap.size() > 0) {
			return listmap.get(0).get("InsuPrt_name");
		} else
			return null;
	}

	/**
	 * 获得完整的业务方案文件名
	 * 
	 * @param CardCode
	 * @param Planno
	 * @return
	 */
	public String getInsuName(String CardCode, String Planno) {
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "Insu_name" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD, returnColumn, params);
		if (listmap.size() > 0) {
			return listmap.get(0).get("Insu_name");
		} else
			return null;
	}

	/**
	 * 保单查询
	 * 
	 * @param BillNo
	 * @return
	 */
	public SaleOrderBean getSaleOrderBean(String BillNo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("BillNo=", BillNo);
		List<Object> objectlist = dbOperater.queryBeanList(DBBean.TB_INSU_PLAN_RECORD, params);
		if (objectlist.size() > 0) {
			return (SaleOrderBean) objectlist.get(0);
		} else {
			return null;
		}

	}

	/**
	 * 保存退单记录
	 * 
	 * @param saleOrderBackBean
	 * @return
	 */
	public long saveBackOrder(SaleOrderBackBean saleOrderBackBean) {
		return dbOperater.insert(DBBean.TB_SALE_ORDER_BACK, saleOrderBackBean);
	}

	public long saveSselessOrder(SaleOrderUselessBean saleOrderUselessBean) {
		return dbOperater.insert(DBBean.TB_SALE_ORDER_USELESS, saleOrderUselessBean);
	}
}
