package com.guanri.android.insurance.printer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.os.Handler;

import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDutyAmount;
import com.guanri.android.insurance.bean.InsuPrintBean;
import com.guanri.android.insurance.bean.InsuPrintBean.PrintDefine;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanContent;
import com.guanri.android.insurance.bean.InsuranceBean;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.lib.bluetooth.BluetoothPool;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.utils.TypeConversion;
import com.guanri.android.lib.utils.Utils;

/**
 * 打印驱动类
 * 
 * @author Administrator
 * 
 */
public class PrinterEngine {

	private InsuranceBean insuranceBean = null;// 保险方案，封装了表现方案相关的对象，包括方案，打印指令方案，界面方案
	private Map<Integer, String> inputValueMap = null;// 重保险销售界面输入的相关值，其中key为界面方案配置文件中的列表顺序
	
	private InsuPlanBean insuPlanBean = null;//具体业务方案对象
	private InsuPrintBean insuPrintBean = null;//具体打印方案对象
	private InsuViewPlanBean insuViewPlanBean = null;//具体视图方案对象
	
	private SaleOrderBean saleOrderBean = null;
	
	private BluetoothPrinter mBluetoothPrinter = null;
	private Handler mainEventHandler = null;// 用户界面消息对象
	private final static Class<BluetoothPrinter> printerClass = BluetoothPrinter.class;
	
	public final static int PRINT_PAGE_WDITH = 240;


	/**
	 * 构造函数 初始化相关信息
	 * 
	 * @param handler
	 * @param insuranceBean
	 * @param inputValueMap
	 */
	public PrinterEngine(Handler handler, InsuranceBean insuranceBean,
			Map<Integer, String> inputValueMap,SaleOrderBean saleOrderBean) {
		this.insuranceBean = insuranceBean;
		this.insuPlanBean = insuranceBean.getInsuPlanBean();
		
		
		this.insuPrintBean = insuranceBean.getInsuPrintBean();
		this.insuViewPlanBean = insuranceBean.getInsuViewPlanBean();
		
		
		this.inputValueMap = inputValueMap;
		this.saleOrderBean = saleOrderBean;

		mBluetoothPrinter = (BluetoothPrinter) BluetoothPool.getInstance().getBluetoothFactory(
				SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.PRINTER_INFO,SharedPreferencesUtils.PRINTER_ADD),printerClass);
	}

	
	/*
	 * 
	 * 
	 * 打印标题栏
	 */
	private byte[] printTitle(String titleHead)
	{
		if(titleHead != null && titleHead.length() > 0)
		{
			titleHead = handleCenterModel(titleHead);
			byte title_buf[] = EncodingUtils.getBytes(titleHead, "GBK");			
			return title_buf;
		}else{
			return null;
		}
	}

	byte[] printBuffer = new byte[2048];
	int index = 0;
	public void inserBuffer(byte[] newBuffer){
		if(newBuffer==null)
			return ;
		if(printBuffer.length<=index+newBuffer.length){
			byte[] tempBuffer = new byte[index+newBuffer.length+1024];
			System.arraycopy(printBuffer, 0, tempBuffer, 0, printBuffer.length);
			printBuffer = tempBuffer;
		}
		System.arraycopy(newBuffer, 0, printBuffer, index, newBuffer.length);
		index +=newBuffer.length;
	}
	
	/**
	 * 打印保单信息
	 */
	public void printInsuOrder() throws Exception {
		index = 0;
		InsuPrintBean insuPrintBean = insuranceBean.getInsuPrintBean();// 打印命令对象

		List<String[]> comandList = insuPrintBean.getPrintComanndList();//打印指令列表
		Map<Integer, Integer> paperStepMap = insuPrintBean.getPaperStepMap();//走纸命令列表
		PrintDefine printHeadInfo = insuPrintBean.getPrintDefine();//打印模板信息
		String titleName = printHeadInfo.prntname;
		
		byte[] printData = this.printTitle(titleName);//打印标题栏
		inserBuffer(printData);
		
		/***************直接把输入框的内容打印上去**************************************/
		for(int step=0;step<8;step++){
			inserBuffer(TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
		}
		String orderInfo = pringInputInfo();
		inserBuffer(TypeConversion.stringToAscii(orderInfo));
		/***************直接把输入框的内容打印上去**************************************/
		
		//打印保单具体内容
		int length = comandList.size();//命令长度
		int paperStep = 0;
		int oneStepLength = 30;
		for (int i = 0; i < length; i++) {
			
			/***************从14行命令开始打印**************************************/
			if(i<13){
				continue;
			}
			/***************从14行命令开始打印**************************************/
			
			
			// 判断是否有走纸命令  暂时以15为一行，以后按照打印机的走纸单位做
			if (paperStepMap.containsKey(new Integer(i))) {
				paperStep = paperStepMap.get(new Integer(i));
				paperStep = paperStep%oneStepLength==0?paperStep/oneStepLength:paperStep/oneStepLength+1;
				for(int step=0;step<paperStep;step++){
					//mBluetoothPrinter.sendData(null, TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
					inserBuffer(TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
				}
			}
			
			String[] comandLine = comandList.get(i);// 打印命令如命令：27,,,,20,6,5  // 则comannd有7个元素
			if(comandLine != null&&comandLine.length>=5){
				// 字段属性打印
				prarseLinePrintFormat(comandLine);//打印指令格式设置 未实现
				printData = handleExtendCmdItem(5, comandLine);
				inserBuffer(printData);
			}
		}
		
		/***************走纸**************************************/
		for(int step=0;step<15;step++){
			inserBuffer(TypeConversion.stringToAscii(System.getProperty("line.separator", "\n")));
		}
		/***************走纸**************************************/
		if(index>0){
			printData = new byte[index];
			System.arraycopy(printBuffer, 0, printData, 0, index);
			mBluetoothPrinter.sendData(null, printData);
		}
	}


	/**
	 * 每行打印格式控制
	 */
	private void prarseLinePrintFormat(String[] cmdLine) throws Exception {
		String movePos = cmdLine[0];//偏移位置
		String maxCharSum = cmdLine[1];//最多打印字符数
		String alignStyle = cmdLine[2];//对齐方式
		String printModel = cmdLine[3];//倍宽、倍高打印等

	}

	// 处理字段属性
	private byte[] handleExtendCmdItem(int valueStartIndex,String[] cmdLine) throws Exception  {
		int printArrt = Integer.parseInt(cmdLine[valueStartIndex-1]);
		byte[] printBufffer = null;
		switch (printArrt) {
		case 0:// 直接打印,如需要打印“生日”则可以为： 0,生日
			if(cmdLine.length>valueStartIndex){
				printBufffer = TypeConversion.stringToAscii(cmdLine[valueStartIndex]);
			}
			break;
		case 1:// 编辑录入的内容
			if(cmdLine[valueStartIndex]==null||"".equals(cmdLine[valueStartIndex].trim()))
				break;
			int valueIndex = Integer.parseInt(cmdLine[valueStartIndex]);// 录入的字段编号
			int startIndex = 0;//开始打印位置为空或0表示打印从0字节开始打印
			int endIndex = 0;//结束打印位置为空或0表示打印 到最后一个字节
			if(cmdLine.length>6&&cmdLine[6]!=null&&!"".equals(cmdLine[6].trim()))
				startIndex = Integer.parseInt(cmdLine[6]);
			if(cmdLine.length>7&&cmdLine[7]!=null&&!"".equals(cmdLine[7].trim()))
				endIndex = Integer.parseInt(cmdLine[7]);
			String nunString = null;//录入内容为空时打印
			if(cmdLine.length>8){
				nunString = cmdLine[8];
			}
			//获取要打印的值
			String inputedStr = inputValueMap.get(valueIndex);
			if(inputedStr==null||"".equals(inputedStr)){
				inputedStr = nunString;
			}
			if(inputedStr!=null&&!"".equals(inputedStr)){
				byte[] tempBuffer = TypeConversion.stringToAscii(inputedStr);
				if(endIndex==0)
					endIndex=tempBuffer.length;
				if(endIndex<=startIndex)
					break;
				printBufffer = new byte[endIndex-startIndex];
				System.arraycopy(tempBuffer, startIndex, printBufffer, 0, endIndex-startIndex);
			}
			break;
		case 2:// 选择项
			break;
		case 3:// 提示符
			if(cmdLine.length>valueStartIndex){
				printBufffer = TypeConversion.stringToAscii(cmdLine[valueStartIndex]);
			}
			break;
		case 4:// 投保项（终端程序暂没实现）
			break;
		case 10:// 产品名称
//			产品、险种名称的打印
//			<打印项>[<序号>]
//			打印项
//			00---产品名称,从业务方案文件的”cardname”字段中读出。
//			01---产品代码,从业务方案文件的”cardcode”字段中读出。
//			02---产品计划号,从业务方案文件的”planno”字段中读出。
//			10---险种名称,从业务方案文件的”classname”字段中读出。
//			11---险种代码,从业务方案文件的”classcode”字段中读出。
//			序号
//			在定义的打印项有多项时的打印顺序
			if(cmdLine.length<=valueStartIndex||cmdLine[valueStartIndex]==null||"".equals(cmdLine[valueStartIndex]))
				break;
			
			String inputedItem = cmdLine[valueStartIndex];// 打印项
			String value = null;
			if (inputedItem.equalsIgnoreCase("00")){// 产品名称
				value = "产品名称:"+insuPlanBean.getInsuPlanAttrList().get(0).cardname;
			} else if (inputedItem.equalsIgnoreCase("01")){// 产品代码
				value = "产品代码:"+insuPlanBean.getInsuPlanAttrList().get(0).cardcode;
			} else if (inputedItem.equalsIgnoreCase("02")){// 产品计划号
				value = "产品计划号:"+insuPlanBean.getInsuPlanAttrList().get(0).planno;
			} else if (inputedItem.equalsIgnoreCase("10")){// 险种名称
				value = "险种名称:"+insuPlanBean.getInsuPlanAttrList().get(0).classname;
			} else if (inputedItem.equalsIgnoreCase("11")){// 险种代码
				value = "险种代码:"+insuPlanBean.getInsuPlanAttrList().get(0).classcode;
			}
			if(value!=null){
				printBufffer = TypeConversion.stringToAscii(value);
			}
			break;
		case 11:// 保险责任
//			保险责任的打印，保险责任从业务方案文件中读出，打印控制格式为：
//			<打印项>,<cClassCode>,<cClmcode>,<cTypeNo>,<序号>
//			打印项：
//			  0---打印责任名称
//			  1---用小数的方式打印责任金额
//			  2---用大写的方式打印责任金额
//			<cClassCode>,<cClmcode>,<cTypeNo>用来从方案文件中找到相应的责任定义项，要是为空，则查找时不比较该项，要是为*，
//			则统计所有符合条件的责任的保费和，如EA3611,*,*表示所有险种代码为EA3601的责任累加和。
//			如用小数打印险种cClassCode为任意，cClmcode为112，cTypeNo为00的保险责任，则可以为：11,1,,112,00
//			<序号> 表示符合<cClassCode>,<cClmcode>,<cTypeNo>条件的第N个项
			List<InsuPlanDutyAmount> insuPlanDutyAmountList = insuPlanBean.getInsuPlanDutyAmountList();
			if(insuPlanDutyAmountList==null||cmdLine.length<10||cmdLine[9]==null||"".equals(cmdLine[9])||Integer.parseInt(cmdLine[9])>=insuPlanDutyAmountList.size())
				break;
			if("0".equals(cmdLine[valueStartIndex]))
				printBufffer = TypeConversion.stringToAscii(insuPlanDutyAmountList.get(Integer.parseInt(cmdLine[9])).responame);
			else if("1".equals(cmdLine[valueStartIndex]))
				printBufffer = TypeConversion.stringToAscii(insuPlanDutyAmountList.get(Integer.parseInt(cmdLine[9])).preamt);
			else if("2".equals(cmdLine[valueStartIndex]))
				printBufffer = TypeConversion.stringToAscii(insuPlanDutyAmountList.get(Integer.parseInt(cmdLine[9])).preamt);
			
			break;
		case 12:// 保险费
//			保险费的打印，打印控制格式为：
//			<打印项>,
//			打印项：
//			  1---用小数的方式打印保险金额
//			  2---用大写的方式打印保险金额
//			如用小数打印保费，则可以为：12,1。
			float amount = Utils.fengToYuan(saleOrderBean.getInsured_amount());
			printBufffer = TypeConversion.stringToAscii(String.valueOf(amount));
			break;
		case 13:// 赔付比例
//			赔付的打印，保险责任从业务方案文件中读出，打印控制格式为：
//			<打印项>,<cClassCode>,<code>
//			打印项：
//			  1---用小数的方式打印赔付比例
//			  2---用大写的方式打印赔付比例
//			<cClassCode>,<code>用来从方案文件中找到相应的赔付比例定义项，要是为空，则查找时不比较该项。
//			如用小数打印险种cClassCode为EA3713，code为0001，cTypeNo为00的赔付比例，则可以为：13,1,EA3713,0001
			break;
		case 14:// 产品限制打印
			break;
		case 15:// 特别约定
			break;
		case 20:// 出票信息
//			打印出票点信息，打印控制格式为：
//			<打印项>[<时间打印项>]
//			打印项：
//			1-分公司代码
//			2-分公司名称
//			3-代理点代码
//			4-代理点名称
//			5-出票人
//			7-分公司地址
//			8-代理点地址
//			9-分公司联系电话
//			6-出票时间
//			时间打印项为：
//			0-世纪
//			1-年 
//			2-月 
//			3-日 
//			4-时 
//			5-分
//			6-秒

			
//			private String branchName		=null;//     分公司名称(签单机构) ASC	30	
//			private String branchAddr		=null;//       分公司地址(保险分公司地址)ASC	40	
//			private String stationName	=null;//机构名称ASC	30	目前只有平安\泰康\百年有该字段 ASC	16	目前只有泰康有该字段
//			private String stationTel	=null;//分公司联系电话 ASC	16	目前只有泰康有该字段
			StringBuffer companyInfo = new StringBuffer(); 
			UserInfoBean userInfo = (UserInfoBean)MainApplication.getInstance().getUserInfo();
			if(cmdLine.length<=valueStartIndex)
				break;
			if("1".equals(cmdLine[valueStartIndex])){
				companyInfo.append("");
			}else if("2".equals(cmdLine[valueStartIndex])){
				if(userInfo.getBranchName()!=null){
					companyInfo.append(userInfo.getBranchName()).append("  ");
				}
				
			}else if("4".equals(cmdLine[valueStartIndex])){
				if(userInfo.getBranchName()!=null){
					companyInfo.append(userInfo.getBranchName()).append("  ");
				}
			}else if("5".equals(cmdLine[valueStartIndex])){
				if(userInfo.getUserName()!=null){
					companyInfo.append(userInfo.getUserName()).append("  ");
				}
			}else if("6".equals(cmdLine[valueStartIndex])){
				//companyInfo.append("出票时间:");
			}else if("7".equals(cmdLine[valueStartIndex])){
				if(userInfo.getBranchAddr()!=null){
					companyInfo.append(userInfo.getBranchAddr()).append("  ");
				}
			}else if("8".equals(cmdLine[valueStartIndex])){
				if(userInfo.getBranchAddr()!=null){
					companyInfo.append(userInfo.getBranchAddr()).append("  ");
				}
			}else if("9".equals(cmdLine[valueStartIndex])){
				if(userInfo.getStationTel()!=null){
					companyInfo.append(userInfo.getStationTel()).append("  ");
				}
			}
			
			if(cmdLine.length>6){
				if(cmdLine.length<=valueStartIndex)
					break;
				if("0".equals(cmdLine[6])){
					//companyInfo.append("21");
				}else if("1".equals(cmdLine[6])){
					companyInfo.append(Calendar.getInstance().get(Calendar.YEAR));
				}else if("2".equals(cmdLine[6])){
					companyInfo.append(Calendar.getInstance().get(Calendar.MONTH)+1);
				}else if("3".equals(cmdLine[valueStartIndex])){
					companyInfo.append(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				}else if("4".equals(cmdLine[valueStartIndex])){
					companyInfo.append(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				}else if("5".equals(cmdLine[valueStartIndex])){
					companyInfo.append(Calendar.getInstance().get(Calendar.MINUTE));
				}else if("6".equals(cmdLine[valueStartIndex])){
					companyInfo.append(Calendar.getInstance().get(Calendar.SECOND));
				}
			}
			printBufffer = TypeConversion.stringToAscii(companyInfo.toString());
			break;
		case 21:// 保险起期
			//判断起期时间是否存在
			String beginDateTime = null;
			if(saleOrderBean.getInsu_begin_time()==null){
				beginDateTime = saleOrderBean.getInsu_begin_date()+" 00:00:01";
			}else{
				beginDateTime = saleOrderBean.getInsu_begin_date()+" "+saleOrderBean.getInsu_begin_time();
			}
			printBufffer = TypeConversion.stringToAscii(beginDateTime);
			break;
		case 22:// 保险止期
			String endDateTime = null;
			if(saleOrderBean.getInsu_end_time()==null){
				endDateTime = saleOrderBean.getInsu_end_date()+" 23:59:59";
			}else{
				endDateTime = saleOrderBean.getInsu_end_date()+" "+saleOrderBean.getInsu_end_time();
			}
			printBufffer = TypeConversion.stringToAscii(endDateTime);
			break;
		case 23:// 保险期限
//			保险期限的打印：
//			<打印格式><数据格式>
//			打印格式：
//			0-自动匹配
//			1-按年
//			2-按月
//			3-按日
//			数据格式:
//			1---用小数的方式打印(默认)
//			2---用大写的方式打印
			String timetypestr = null;
			int timeTye = Integer.parseInt(cmdLine[valueStartIndex]);
			
			switch (timeTye) {
			case 1:
				timetypestr="年";
				break;
			case 2:
				timetypestr="月";
				break;
			case 3:
				timetypestr="天";
				break;
			default :
				timetypestr = "";
			}
			int insuAssembled = saleOrderBean.getInsuAssembled();
			String timeList = insuPlanBean.getInsuPlanAttrList().get(0).timelist;
			if(insuAssembled>0&&timeList!=null){
				String[] time = timeList.split(",");
				if(insuAssembled<=time.length){
					printBufffer = TypeConversion.stringToAscii(time[insuAssembled-1]+"("+timetypestr+")");
				}
			}
			break;
		default:
			break;
		}
		return printBufffer;
	}
	
	
	/**
	 * 计算打印内容长度
	 */
	private int getPrintStrLen(String p) {
		int length = p.length();
		int strLen = 0;

		for (int i = 0; i < length; i++) {
			char c = p.charAt(i);
			if (c > 255) {
				strLen += 2;
			} else {
				strLen += 1;
			}
		}
		return strLen;
	}	
	
	
	/**
	 * 打印内容居中
	 */
	private String handleCenterModel(String txt)
	{
		int width = getPrintStrLen(txt);
		int center = (PRINT_PAGE_WDITH - width) / 2;
		String str = "";
		for (int i = 0; i < center; i++) {
			str += " ";
		}
		
		txt = str + txt;
		
		return txt;		
	}
	
	/**
	 * 临时打印方法
	 */
	private String pringInputInfo(){
		List<InsuPlanContent> insuPlanContentList = insuViewPlanBean.getInsuPlanContentList();
		int length = insuPlanContentList.size();
		int valueIndex = 1;
		StringBuffer printStr = new StringBuffer();
		for (int i = 0; i < length; i++) {
			InsuPlanContent nsuPlanContent = insuPlanContentList.get(i);
			String tSChar = nsuPlanContent.TSChar;
			// 空录入项 跳过,表示同下一行的定义共同构成一个输入选项，如果下一行没有标体，取该行的标题放到下一行
			if (nsuPlanContent.Attribute.equals("0")){
				nsuPlanContent = insuPlanContentList.get(++i);
				if(nsuPlanContent.TSChar!=null&&!"".equals(nsuPlanContent.TSChar)){
					tSChar = nsuPlanContent.TSChar;
				}
			}
			String value = inputValueMap.get(valueIndex++);
			
			if(nsuPlanContent.Attribute.equals("49")){//保险组合
				value = "7天---3元";
			}else if(nsuPlanContent.Attribute.equals("9")||nsuPlanContent.Attribute.equals("14")){//证件类型
				String str = "1=身份证;2=护照;3=军官证;4=驾照;5=户口本;6=学生证;7=工作证;8=社保号;0=其他";
				value = getValueMap(str,";","=").get(value);
			}else if(nsuPlanContent.Attribute.equals("7")||nsuPlanContent.Attribute.equals("12")){//7.	投保人性别	
				String str = "1=男;2=女";
				value = getValueMap(str,";","=").get(value);
			}
			printStr.append(tSChar);
			printStr.append(value).append("\n");
		}	
		
		return printStr.toString();
	}
	
	private HashMap<String,String> getValueMap(String str,String split1,String split2){
		HashMap<String,String>  valueMap = new HashMap<String,String>();
		if(str!=null){
			String[] values = str.split(split1);
			for(String value:values){
				String[] keyValue = value.split(split2);
				valueMap.put(keyValue[0], keyValue[1]);
			}
		}
		return valueMap;
	}		
}
