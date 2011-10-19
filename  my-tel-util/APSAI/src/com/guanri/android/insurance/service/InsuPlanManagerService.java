package com.guanri.android.insurance.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.DownCommandBean;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPlanRecordBean;
import com.guanri.android.insurance.bean.UpCommandBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.command.CommandParseException;
import com.guanri.android.insurance.command.DownCommandParse;
import com.guanri.android.insurance.command.UpCommandHandler;
import com.guanri.android.insurance.command.UpCommandHandler.UpCommandHandlerListener;
import com.guanri.android.insurance.command.UpCommandParse;
import com.guanri.android.insurance.common.CommandConstant;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.insuplan.PareFileToObject;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TimeUtils;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 保险业务方案管理
 */
public class InsuPlanManagerService {
	public static Logger logger = Logger.getLogger(InsuPlanManagerService.class);// 日志对象;
	
	public DBOperator dbOperater;
	private Context context;
	private CommandControl commandControl = null;

	private String operator_id = null;
	private String filePath = null;


	/**
	 * @roseuid 4DF8330E035B
	 */
	public InsuPlanManagerService(Context context) {
		this.commandControl = CommandControl.getInstance();
		this.context = context;
		dbOperater = DBOperator.getInstance();
		operator_id = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserId();
		filePath = PareFileToObject.INSU_PLAN_FILE_DIR;
	}



	/**
	 * 从服务器下载方案列表
	 * 
	 * @return List<InsuPlanRecordBean>
	 */
	public void downInsuPlanList(final ProgressDialog btDialog) {
		btDialog.show();
		// 构造命令体
		final byte[] body = new byte[26];
		try {
			/*
			 * 分公司代码 ASC 3 终端ID ASC 8 终端校验码 ASC 8 管理员工号 ASC 6 当前需要下载的包序号 HEX 1
			 * 首次，此值为0x01
			 */
			logger.debug("CommandConstant.COMFIG_COM_PWD: " +CommandConstant.COMFIG_COM_PWD);
			//System.arraycopy(TypeConversion.stringToAscii("001"), 0, body, 0,3);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, body, 3, 8);
			System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body,11, 8);
			System.arraycopy(TypeConversion.stringToAscii(operator_id), 0,body, 19, 6);
			body[25] = 0X01;
			// System.arraycopy(0x01, 0, body, 25, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 构造上行命令对象
		UpCommandBean upCommandBean = new UpCommandBean();
		upCommandBean.setCommandCode(CommandConstant.CMD_CONTROLFILE_LIST);
		upCommandBean.setBody(body);
		final UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);

		//在当前线程中执行命令 并在回调函数中处理返回结果:创建命令回调函数
		UpCommandHandler upCommandHandler = new UpCommandHandler(new UpCommandHandlerListener(context){
			//处理具体下行命令
			public void handlerOthorMsg(int what,Object object){ 
				DownCommandParse downCommandParse = (DownCommandParse)object;
				DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
				if(downCommandBean.getAnswerCode().equals("0")){//解析业务方案
					if(downCommandBean.getBody()[11]>0){//存在业务方案
						parseInsuPlanRecord(downCommandBean.getBody());
						if(downCommandBean.getBody()[11]>downCommandBean.getBody()[12]){
							body[25]++;
							commandControl.sendUpCommandInThread(upCommandParse, new UpCommandHandler(this));
						}else{
							btDialog.dismiss();
						}
					}else{//没有业务方案
						btDialog.dismiss();
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_list_null));
						builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_updatelist));
						builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// dosome thing
									}
						});
						builder.create().show();
					}

				}else{//查询失败
					btDialog.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(downCommandBean.getAnswerMsg());
					builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_updatelist));
					builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// dosome thing
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
		commandControl.sendUpCommandInThread(upCommandParse, upCommandHandler);
	}

	
	/**
	 * 解析方案列表下载 下行命令体，并构造方案列表对象插入数据库
	 * @param body
	 * @throws UnsupportedEncodingException
	 */
	private void parseInsuPlanRecord(byte[] body){
		try{
			//根据本包业务方案个数计算本包业务方案列表占用空间，body[13]本包业务方案个数
			byte[] insuListdata = new byte[20 * body[13]];
			logger.debug("终端ID:"+ TypeConversion.asciiToString(body, 3, 8));
			logger.debug("业务方案总包数:" + body[11]);
			logger.debug("当前包序号:" + body[12]);
			logger.debug("本包业务方案个数:" + body[13]);
			System.arraycopy(body, 14,insuListdata, 0, insuListdata.length);
			for (int i = 0; i < body[13]; i++) {
				// 构建对象
				String cardCode = TypeConversion.asciiToString(insuListdata, i * 20, 8);
				String planno = String.valueOf(TypeConversion.bytesToShort(insuListdata,i * 20 + 8));
				String name = TypeConversion.asciiToString(insuListdata, i * 20 + 10, 10);
				String operate_time = TimeUtils.getTimeString(new Date());
				
				logger.debug("业务方案编号:" + cardCode);
				logger.debug("Planno:" + planno);
				logger.debug("Name:" + name);
				
				//查询数据库中是否存在该方案信息
				HashMap<String,String> params = new HashMap<String,String>(2);
				params.put("CardCode=", cardCode);
				params.put("Planno=", planno);
				int rowNum = dbOperater.queryRowNum(DBBean.TB_INSU_PLAN_RECORD, params);
				//如果数据库中已经存在，则不在增加
				if(rowNum>0){
					continue;
				}else{
					//插入数据库
					InsuPlanRecordBean insuPlanRecordBean = new InsuPlanRecordBean();
					insuPlanRecordBean.setName(name);
					insuPlanRecordBean.setCardCode(cardCode);
					insuPlanRecordBean.setFile_downloaded(false);
					insuPlanRecordBean.setOperate_time(operate_time);
					insuPlanRecordBean.setOperator_id(operator_id);
					insuPlanRecordBean.setPlanno(planno);
					insuPlanRecordBean.setUseable(false);
					dbOperater.insert(DBBean.TB_INSU_PLAN_RECORD, insuPlanRecordBean);
					
				}
			}	
		}catch(UnsupportedEncodingException unex){
			unex.printStackTrace();
		}
	}
	

	/**
	 * 下载业务方案文件
	 * 
	 * @param code
	 */
	int insufilesize = 0;
	public boolean downInsuFile(final String cardCode,final String Planno,final Handler messageHandler) {
		insufilesize = 0;
		new Thread(){
			public void run(){
				Looper.prepare();
				DownCommandParse  downCommandParse = null;
				try{
					//构造下载业务方案文件上传命令数据包
					int planno = Integer.valueOf(Planno);
					String insuPlanFileName = cardCode+".txt";//业务方案文件名称
					
					deleteInsuFile(filePath,insuPlanFileName);//删除文件
					
					for(String d:MainApplication.getInstance().getFilesDir().list()){
						logger.debug("getFilesDir:"+d);
					}
					
					byte[] body = new byte[36];
					//分公司代码 ASC 3 终端ID ASC 8 终端校验码 ASC 8 业务方案代码 ASC 8 业务方案序号 HEX 2
					//管理员工号 ASC 6 当前需要下载的数据包序号 HEX 1 新下载此数据默认为1，从第一包开始下载
					// System.arraycopy(TypeConversion.stringToAscii("001"), 0, body, 0, 3);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, body, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body,11, 8);
					System.arraycopy(TypeConversion.stringToAscii(cardCode), 0, body,19, 8);
					System.arraycopy(TypeConversion.intToBytes(planno), 0, body, 27, 2);
					System.arraycopy(TypeConversion.stringToAscii(operator_id), 0,body, 29, 6);
					body[35] =  0x01;
					UpCommandBean upCommandBean = new UpCommandBean();
					upCommandBean.setCommandCode(CommandConstant.CMD_CONTROLFILE_DL);
					upCommandBean.setBody(body);
					// 构造上行命令对象
					UpCommandParse upCommandParse = new UpCommandParse(upCommandBean);
					//下载业务方案文件
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					parsePlanFileBuffer(upCommandParse,downCommandParse,insuPlanFileName);
					//读取业务方案文件，获得编辑模板文件和打印模板文件名称代码
					InsuPlanBean insuPlanBean = PareFileToObject.pareInsuPlanBean(insuPlanFileName);
					String insuEditFileName = insuPlanBean.getInsuPlanAttrList().get(0).editcode+".edt";
					String insuPrintFileName = insuPlanBean.getInsuPlanAttrList().get(0).prntcode+".prn";
					String insuName = insuPlanBean.getInsuPlanAttrList().get(0).cardname;
					//保存编辑模板和打印模板文件名
					saveInsuEditName(cardCode, Planno, insuEditFileName);
					saveInsuPrtName(cardCode, Planno, insuPrintFileName);
					saveInsuName(cardCode, Planno, insuName);
					
					deleteInsuFile(filePath,insuEditFileName);//删除文件
					deleteInsuFile(filePath,insuPrintFileName);//删除文件
					
					
					//分公司代码	BranchID	ASC	3	 终端ID	PosID	ASC	8	 终端校验码	ComPSW	ASC	8	
					//编辑模板代码		ASC	8	管理员工号		ASC	6	 当前需要下载的数据包序号		HEX	1	
					messageHandler.sendMessage(messageHandler.obtainMessage(R.string.apsai_insu_manager_downloadeditloading,
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadeditloading)));//通知主线程
					insufilesize = 0;//初始化已下载文件字节数
					body = new byte[34];
					//System.arraycopy(TypeConversion.stringToAscii("001"), 0, body, 0, 3);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_POS_ID),0, body, 3, 8);
					System.arraycopy(TypeConversion.stringToAscii(CommandConstant.COMFIG_COM_PWD), 0, body,11, 8);
					System.arraycopy(TypeConversion.stringToAscii(insuEditFileName), 0, body,19, 8);
					System.arraycopy(TypeConversion.stringToAscii(operator_id), 0,body, 27, 6);
					//下载编辑模板
					upCommandBean = new UpCommandBean();
					upCommandBean.setCommandCode(CommandConstant.CMD_EDITFILE_DL);
					upCommandBean.setBody(body);
					body[33] =  0x01;
					upCommandParse = new UpCommandParse(upCommandBean);
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					parseEditPrintFileBuffer(upCommandParse,downCommandParse,insuEditFileName);
					//下载打印模板
					//btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadprintloading));

					messageHandler.sendMessage(messageHandler.obtainMessage(R.string.apsai_insu_manager_downloadprintloading,
							StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadprintloading)));	//通知主线程
					insufilesize = 0;//初始化已下载文件字节数
					//分公司代码	BranchID	ASC	3	 终端ID	PosID	ASC	8	 终端校验码	ComPSW	ASC	8	
					//打印模板代码		ASC	8	管理员工号		ASC	6	 当前需要下载的数据包序号		HEX	1	
					upCommandBean.setCommandCode(CommandConstant.CMD_PRINTFILE_DL);
					System.arraycopy(TypeConversion.stringToAscii(insuPrintFileName), 0, body,19, 8);
					body[33] =  0x01;
					upCommandParse = new UpCommandParse(upCommandBean);
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					parseEditPrintFileBuffer(upCommandParse,downCommandParse,insuPrintFileName);
					
					messageHandler.sendMessage(messageHandler.obtainMessage(0));
					//btDialog.dismiss();
					
				} catch (IOException e) {
					e.printStackTrace();
					messageHandler.sendMessage(messageHandler.obtainMessage(-1,StringUtils.getStringFromValue(R.string.apsai_common_server_link_error)));
				}catch(CommandParseException ex){
					logger.error(ex.getMessage());
					messageHandler.sendMessage(messageHandler.obtainMessage(-1,ex.getMessage()));
				}catch(Exception io){
					io.printStackTrace();
					//btDialog.dismiss();
					messageHandler.sendMessage(messageHandler.obtainMessage(-1));
				}	
			}
		}.start();

		return true;
	}


	/**
	 * 解析下载模板文件数据
	 * @param upCommandParse
	 * @param downCommandParse
	 * @param fileName
	 * @throws IOException
	 * @throws CommandParseException
	 */
	private void parsePlanFileBuffer(UpCommandParse upCommandParse,
			DownCommandParse  downCommandParse,String fileName) throws IOException, CommandParseException{
		UpCommandBean upCommandBean = upCommandParse.getUpCommandBean();
		DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
		byte[] upBody = upCommandBean.getBody();
		if(downCommandBean.getAnswerCode().equals("0")){
			try{
				byte[] data = downCommandBean.getBody();
				logger.debug("终端ID:" + TypeConversion.asciiToString(data, 3, 8));
				logger.debug("业务方案代码"+ TypeConversion.asciiToString(data, 11, 8));
				logger.debug("模板总大小:" + TypeConversion.bytesToShort(data, 21));
				logger.debug("当前数据包序号:" + data[23]);
				logger.debug("本包数据长度:" + TypeConversion.bytesToShort(data, 24));
				insufilesize = insufilesize+ TypeConversion.bytesToShort(data, 24);
				byte[] datefile = new byte[TypeConversion.bytesToShort(data, 24)];
				System.arraycopy(data, 26, datefile, 0,TypeConversion.bytesToShort(data, 24));
				byteTofile(filePath,fileName,datefile);	
				//检查数据是否已经下载完成
				if(TypeConversion.bytesToShort(data, 21)>insufilesize){
					upBody[upBody.length-1]++;
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					parsePlanFileBuffer(upCommandParse,downCommandParse,fileName);	
				}
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}else{
			throw new CommandParseException(downCommandBean.getAnswerMsg());
		}
	}
	
	/**
	 * 解析下载模板文件数据
	 * @param upCommandParse
	 * @param downCommandParse
	 * @param fileName
	 * @throws IOException
	 * @throws CommandParseException
	 */
	private void parseEditPrintFileBuffer(UpCommandParse upCommandParse,
			DownCommandParse  downCommandParse,String fileName) throws IOException, CommandParseException{
		UpCommandBean upCommandBean = upCommandParse.getUpCommandBean();
		DownCommandBean downCommandBean = downCommandParse.getDownCommandBean();
		byte[] upBody = upCommandBean.getBody();
		if(downCommandBean.getAnswerCode().equals("0")){
			try{
				byte[] data = downCommandBean.getBody();
				logger.debug("终端ID:" + TypeConversion.asciiToString(data, 3, 8));
				logger.debug("业务方案代码"+ TypeConversion.asciiToString(data, 11, 8));
				logger.debug("模板总大小:" + TypeConversion.bytesToShort(data, 19));
				logger.debug("当前数据包序号:" + data[21]);
				logger.debug("本包数据长度:" + TypeConversion.bytesToShort(data, 22));
				insufilesize = insufilesize+ TypeConversion.bytesToShort(data, 22);
				byte[] datefile = new byte[TypeConversion.bytesToShort(data, 22)];
				System.arraycopy(data, 24, datefile, 0,TypeConversion.bytesToShort(data, 22));
				byteTofile(filePath,fileName,datefile);	
				//检查数据是否已经下载完成
				if(TypeConversion.bytesToShort(data, 19)>insufilesize){
					upBody[upBody.length-1]++;
					downCommandParse = commandControl.sendUpCommand(upCommandParse);
					parseEditPrintFileBuffer(upCommandParse,downCommandParse,fileName);	
				}
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		}else{
			throw new CommandParseException(downCommandBean.getAnswerMsg());
		}
	}	
	/**
	 * 改变激活状态
	 */
	public void changePlanState(String CardCode,String Planno) {
		boolean nowstate = getInsustate(CardCode,Planno);
		if (nowstate) {
			ContentValues cv = new ContentValues();
			cv.put("useable", false);
			updateInsuInfo(CardCode,Planno, cv);
		} else {
			ContentValues cv = new ContentValues();
			cv.put("useable", true);
			updateInsuInfo(CardCode,Planno, cv);
		}
	}

	/**
	 * 保存编辑模板文件名
	 * @param CardCode
	 * @param Planno
	 * @param InsuEditName
	 */
	public void saveInsuEditName(String CardCode,String Planno,String InsuEditName){
		ContentValues cv = new ContentValues();
		cv.put("InsuEdit_name", InsuEditName);
		updateInsuInfo(CardCode,Planno, cv);
	}
	
	/**
	 * 返回编辑模板文件名
	 * @param CardCode
	 * @param Planno
	 * @return
	 */
	public String getInsuEditName(String CardCode,String Planno){
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "InsuEdit_name" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD,returnColumn, params);
		if (listmap.size() > 0) {
			return listmap.get(0).get("InsuEdit_name");
		}else
			return null;
	}
	/**
	 * 保存打印模板文件名
	 * @param CardCode
	 * @param Planno
	 * @param InsuEditName
	 */

	public void saveInsuPrtName(String CardCode,String Planno,String InsuPrtName){
		ContentValues cv = new ContentValues();
		cv.put("InsuPrt_name", InsuPrtName);
		updateInsuInfo(CardCode,Planno, cv);
	}
	
	/**
	 * 保存完整的业务方案文件名
	 * @param CardCode
	 * @param Planno
	 * @param InsuName
	 */
	public void saveInsuName(String CardCode,String Planno,String InsuName){
		ContentValues cv = new ContentValues();
		cv.put("Insu_name", InsuName);
		updateInsuInfo(CardCode,Planno, cv);
	}
	
	/**
	 * 返回打印模板文件夹名
	 * @param CardCode
	 * @param Planno
	 * @return
	 */
	public String getInsuPrtName(String CardCode,String Planno){
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "InsuPrt_name" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD,returnColumn, params);
		if (listmap.size() > 0) {
			return listmap.get(0).get("InsuPrt_name");
		}else
			return null;
	}
	
	/**
	 * 查询表中方案列表
	 * 
	 * @return List<Map<String,String>>
	 * @roseuid 4DF81E80033C
	 */
	public List<Map<String,String>> queryInsuPlanList(String[] queryParam) {
		List<Map<String,String>> insunList = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD, queryParam, null);
		return insunList;
	}

	/**
	 * 查询表中方案列表
	 * 
	 * @return List<InsuPlanRecordBean>
	 * @roseuid 4DF81E80033C
	 */
	public List<InsuPlanRecordBean> queryInsuPlanList() {
		List<InsuPlanRecordBean> insunlist = new ArrayList<InsuPlanRecordBean>();
		List<Object> objlist = new ArrayList<Object>();
		objlist = dbOperater.queryBeanList(DBBean.TB_INSU_PLAN_RECORD, null);
		for (int i = 0; i < objlist.size(); i++) {
			InsuPlanRecordBean insuPlanRecordBean = (InsuPlanRecordBean) objlist
					.get(i);
			insunlist.add(insuPlanRecordBean);
		}
		
		return insunlist;
	}
	/**
	 * 跟新业务方案
	 * 
	 * @param Insu_id
	 *            更新条件
	 * @param cv
	 *            待更新的值
	 */
	public long updateInsuInfo(String CardCode,String Planno, ContentValues cv) {
		long insertRow = 0;
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		insertRow = dbOperater.update(DBBean.TB_INSU_PLAN_RECORD, cv, params);
		
		return insertRow;
	}

	/**
	 * 返回业务方案状态 是否激活
	 * 
	 * @param Insu_id
	 * @return
	 */
	public boolean getInsustate(String CardCode,String Planno) {
		boolean nowstate = false;
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "useable" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", CardCode);
		params.put("Planno=", Planno);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD,returnColumn, params);
		if (listmap.size() > 0) {
			if (listmap.get(0).get("useable").equals("0")) {
				nowstate = false;
			} else {
				nowstate = true;
			}
		}
		
		return nowstate;

	}

	/**
	 * 返回业务方案下载状态，
	 * 
	 * @param Insu_id
	 * @return
	 */
	public boolean getInsuDownloaded(String Insu_id) {
		boolean nowdownloadstate = false;
		List<Map<String, String>> listmap = null;
		String[] returnColumn = { "File_downloaded" };
		Map<String, String> params = new HashMap<String, String>();
		params.put("CardCode=", Insu_id);
		listmap = dbOperater.queryMapList(DBBean.TB_INSU_PLAN_RECORD,returnColumn, params);
		if (listmap.size() > 0) {
			if (listmap.get(0).get("File_downloaded").equals("0")) {
				nowdownloadstate = false;
			} else {
				nowdownloadstate = true;
			}
		}
		
		return nowdownloadstate;
	}



	/**
	 * 判断文件是否存在
	 * @param filepath
	 * @param fileName
	 * @return
	 */
	public boolean fileExists(String filepath, String fileName){
		File f = new File(filepath, fileName);
		return f.exists();
	}
	
	/**
	 * 删除配置文件
	 * @param filepath
	 * @param fileName
	 */
	private void deleteInsuFile(String filepath, String fileName){
		File f = new File(filepath, fileName);
		if (f.exists()) {// 检查fileName是否存在
			logger.debug("删除");
			f.delete();// 在当前目录下建立一个名为fileName的文件
		}
	}


	/**
	 * 创建目录
	 * @param filePath
	 * @return
	 */
	private String createDir(String filePath) {
		File fileDir = null; // 文件流变量
		boolean hasDir = false; // 标示文件流对象是否存在
		fileDir = new File(filePath); // 生成文件流对象
		hasDir = fileDir.exists(); // 判断文件流对象是否存在
		if (!hasDir) {
			String[] fileDirs = filePath.split("/");
			StringBuffer fileDirStr = new StringBuffer();
			for (int i = 0; i < fileDirs.length; i++) {
				fileDir = new File(fileDirStr.append("/").append(fileDirs[i])
						.toString());
				if (!fileDir.exists()) {
					hasDir = fileDir.mkdir();
				}
			}
			// hasDir = fileDir.mkdir();
		}
		// 判断是否成功
		if (!hasDir) {
			filePath = null;
		}
		return filePath;
	}

	/**
	 * 写入文件
	 * @param filepath
	 * @param fileName
	 * @param date
	 */
	private void byteTofile(String filepath, String fileName, byte[] date) {
		// 创建目录
		if (createDir(filepath) == null)
			return;
		try {
			// 判断文件是否存在
			File f = new File(filepath, fileName);
			if (!f.exists()) {// 检查fileName是否存在
				f.createNewFile();// 在当前目录下建立一个名为fileName的文件
			}
			// 定义一个类RandomAccessFile的对象，并实例化
			java.io.RandomAccessFile rf = new java.io.RandomAccessFile(filepath
					+ File.separator + fileName, "rw");
			rf.seek(rf.length());// 将指针移动到文件末尾

			rf.write(date);
			rf.close();// 关闭文件流
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
