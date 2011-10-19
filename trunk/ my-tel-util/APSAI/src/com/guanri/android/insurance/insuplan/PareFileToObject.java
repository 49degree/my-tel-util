package com.guanri.android.insurance.insuplan;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAdditional;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAttr;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanCOCode;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanChan;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDefine;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDutyAmount;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanFeeRate;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanPromis;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanSpecPromis;
import com.guanri.android.insurance.bean.InsuPrintBean;
import com.guanri.android.insurance.bean.InsuPrintBean.PrintDefine;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanContent;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanMode;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.FileReader;

/**
 * 映射业务方案配置文件为相应类的对象
 * @author 杨雪平
 *
 */
public class PareFileToObject{
	static Logger logger = Logger.getLogger(PareFileToObject.class);
	//public static String INSU_PLAN_FILE_DIR = MainApplication.getInstance().getFilesDir()+"/isuaplan";//业务方案配置文件目录
	public static String INSU_PLAN_FILE_DIR = MainApplication.getInstance().getFilesDir()+File.separator+"isuaplan"+File.separator;//业务方案配置文件目录

	public final static String TAB_SPLIT_FLAG = "\\\t";//TAB分割
	public final static String BLANK_SPLIT_FLAG = " ";//空格分割
	public final static String LINE_SPLIT_FLAG = "\\|";//|分割
	public final static String COMMA_SPLIT_FLAG = ",";//,分割
	public final static String END_FLAG = "===";//结束符合
	
	
	/**
	 * 解析业务定义文件
	 * @param filePath
	 * @return
	 */
	public static InsuPlanBean pareInsuPlanBean(String fileName){
		File file = new File(INSU_PLAN_FILE_DIR,fileName);
		
		InsuConfigFileReader configReader = new InsuConfigFileReader(file,PareFileToObject.LINE_SPLIT_FLAG);
		List<List<Map<String,Object>>> groupList = configReader.getFileGroupList();
		
		InsuPlanBean insuPlanBean = new InsuPlanBean();
		//获取 第一段落：{定额保单属性定义表}的值
		List<Map<String,Object>> group = groupList.get(0);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanAttrList().add((InsuPlanAttr)reflectField(InsuPlanAttr.class,map));
		}
		//第二段落：{定额保单销售渠道定义表}的值
		group = groupList.get(1);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanChanList().add((InsuPlanChan)reflectField(InsuPlanChan.class,map));
		}
		//获取 第三段落：{定额保单责任保额定义表}的值
		group = groupList.get(2);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanDutyAmountList().add((InsuPlanDutyAmount)reflectField(InsuPlanDutyAmount.class,map));
		}
		//获取 第四段落：{定额保限定定义表}的值
		group = groupList.get(3);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanDefineList().add((InsuPlanDefine)reflectField(InsuPlanDefine.class,map));
		}
		//获取 	第五段落：{保障计划附加信息}的值
		group = groupList.get(4);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanAdditionalList().add((InsuPlanAdditional)reflectField(InsuPlanAdditional.class,map));
		}
		//获取 第六段落： { 卡单可约定项目表 }的值
		group = groupList.get(5);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanPromisList().add((InsuPlanPromis)reflectField(InsuPlanPromis.class,map));
		}
		//获取 	第七段落：｛保险公司代码｝的值
		group = groupList.get(6);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanCOCodeList().add((InsuPlanCOCode)reflectField(InsuPlanCOCode.class,map));
		}
		//获取 	第八段落：｛特别约定定义｝的值
		group = groupList.get(7);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanSpecPromisList().add((InsuPlanSpecPromis)reflectField(InsuPlanSpecPromis.class,map));
		}
		//获取 	第九段落：｛定义保费分级累进表｝的值
		group = groupList.get(8);
		for(Map<String,Object> map:group){
			insuPlanBean.getInsuPlanFeeRateList().add((InsuPlanFeeRate)reflectField(InsuPlanFeeRate.class,map));
		}
		return insuPlanBean;
	}
	
	/**
	 * 解析编辑模板文件将各属性文件  保存 InsuViewPlanBean对象
	 * @param filePath 文件路径
	 * @return
	 */
	public static InsuViewPlanBean pareInsuViewPlanBean(String fileName){
		File file = new File(INSU_PLAN_FILE_DIR,fileName);
		InsuConfigFileReader configReader = new InsuConfigFileReader(file,PareFileToObject.TAB_SPLIT_FLAG);
		
		List<List<Map<String,Object>>> groupList = configReader.getFileGroupList();
		
		InsuViewPlanBean insuViewPlanBean = new InsuViewPlanBean();
		
		//1．	第一段落：{编辑模板定义表}
		List<Map<String,Object>> group = groupList.get(0);
		for(Map<String,Object> map:group){
			insuViewPlanBean.getInsuPlanModeList().add((InsuPlanMode)reflectField(InsuPlanMode.class,map));
		}
		
		//2．	第二段落：{编辑内容定义表} 
		group = groupList.get(1);
		for(Map<String,Object> map:group){
			insuViewPlanBean.getInsuPlanContentList().add((InsuPlanContent)reflectField(InsuPlanContent.class,map));
		}
		
		return insuViewPlanBean;
	}
	
	/**
	 * 解析打印模板文件将各属性文件  保存 InsuPrintBean对象
	 * @param filePath 文件路径
	 * @return
	 */
	public static InsuPrintBean pareInsuPrintBean(String fileName){
		File file = new File(INSU_PLAN_FILE_DIR,fileName);
		InsuConfigFileReader configReader = new InsuConfigFileReader(file,PareFileToObject.TAB_SPLIT_FLAG);
		
		List<Map<String,Object>> group = configReader.getGroup(0);
		
		InsuPrintBean insuPrintBean = new InsuPrintBean();
		//第一段落：{打印模板定义表}
		for(Map<String,Object> map:group){
			insuPrintBean.setPrintDefine((PrintDefine)reflectField(PrintDefine.class,map));
		}
		if(insuPrintBean.getPrintDefine()==null){
			return null;
		}
		/*
		 * 第二部分：打印控制部分
		 * 打印指令部分（printComanndList）的位置对应走纸命令部分的key,
		 * 如果在走纸命令(paperStepMap)中没有对应的KEY，则表示该打印指令前无需走纸
		 */
		String[] paperStep = null;
		int linesOfComannd = 0;//当前为第几条命令
		FileReader fileReader = null;
		String lineString = null;
		try{
			//获取文件读取流对象
			fileReader = new FileReader(file);
			while((lineString = fileReader.readLine()) != null){
				if(lineString.indexOf(PareFileToObject.END_FLAG)>-1)//已经读到第一段落：{打印模板定义表}结束位置
					break;
			}
			while((lineString = fileReader.readLine()) != null){
				if(lineString.indexOf(PareFileToObject.END_FLAG)>-1)//已经读到第二部分：打印控制部分结束位置
					break;
				//判断是走纸命令还是输出指令
				if(lineString.indexOf(PareFileToObject.COMMA_SPLIT_FLAG)>-1){//输出命令
					insuPrintBean.addPrintComanndMap(lineString.split(PareFileToObject.COMMA_SPLIT_FLAG));
					linesOfComannd++;
				}else{
					paperStep = lineString.trim().split(PareFileToObject.BLANK_SPLIT_FLAG);
					insuPrintBean.addPaperStepMap(linesOfComannd, new Integer(paperStep[2].trim()));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{//释放资源
			if(fileReader!=null){
				fileReader.release();
				fileReader = null;
			}

		}
		
		return insuPrintBean;
	}	
	
	
	/**
	 * 由MAP转换成对象
	 * @param clazz
	 * @param map
	 * @return
	 */
	public static Object reflectField(Class clazz,Map<String,Object> map){
		String key = null;
		try{
			//创建蓝牙设备对象
			Constructor devConstructor = clazz.getConstructor();
			Object o = devConstructor.newInstance();
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()){
				key = it.next();
				Field mapField = clazz.getField(key);
				mapField.set(o, map.get(key));
			}
			return o;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}
