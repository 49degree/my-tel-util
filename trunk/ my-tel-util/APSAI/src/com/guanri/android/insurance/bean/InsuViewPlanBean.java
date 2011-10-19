package com.guanri.android.insurance.bean;

import java.util.ArrayList;
import java.util.List;

import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAdditional;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAttr;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanCOCode;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanChan;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDefine;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanDutyAmount;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanFeeRate;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanPromis;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanSpecPromis;

/**
 * 
 * @author Administrator
 *
 */
public class InsuViewPlanBean {
	/**
	 * 1．	第一段落：{编辑模板定义表}
	 */
	private List<InsuPlanMode> insuPlanModeList = null;
	/**
	 * 2．	第二段落：{编辑内容定义表}
	 */
	private List<InsuPlanContent> insuPlanContentList = null;
	
	public InsuViewPlanBean(){
		insuPlanModeList = new ArrayList<InsuPlanMode>();//1．	第一段落：
		insuPlanContentList = new ArrayList<InsuPlanContent>();//2．	第二段落：
	}
	

	public List<InsuPlanMode> getInsuPlanModeList() {
		return insuPlanModeList;
	}

	public void setInsuPlanModeList(List<InsuPlanMode> insuPlanModeList) {
		this.insuPlanModeList = insuPlanModeList;
	}

	public List<InsuPlanContent> getInsuPlanContentList() {
		return insuPlanContentList;
	}

	public void setInsuPlanContentList(List<InsuPlanContent> insuPlanContentList) {
		this.insuPlanContentList = insuPlanContentList;
	}

	
	/**
	 * 描述业务模板代码和名称
	 * @author Administrator
	 */
	public static class InsuPlanMode{
		//char(8),			--{编辑模板代码},
		public String editcode		= null ;
		//char(100),		--{编辑模板名称},	//注: 国寿模板文件中项名
		public String cardname		= null ;
		
		
		@Override
		public String toString() {
			return "InsuPlanMode [editcode=" + editcode + ", editname="
					+ cardname + "]";
		} 
		
		
	}
	/**
	 * 描述业务模板各属性
	 * @author Administrator
	 */
	public static class InsuPlanContent{
		public String editcode;
		/**
		 * 提示字符
		 */
		public String TSChar;
		/**
		 * 编辑区起始列
		 */
		public String BegCol;
		/**
		 * 编辑区结束列
		 */
		public String EndCol;
		/**
		 * 最大舒服字符个数
		 */
		public String MaxInputCnt;
		/**
		 * 输入类型 1数字 2=T9拼音输入法 4=五笔输入法 6=汉字输入法 16=英文大写输入法 32=英文小写输入法 48=英文输入法
		 * 128=特殊输入，项选择 要是允许多个输入法， 则数值为允许的所有输入法的累加和， 为0表示所有输入法都不可用}
		 */
		public String InputEnAble;
		/**
		 * 该编辑的初始默认输入法， }, 0=数字 1=T9拼音输入法 2=五笔输入法 3=保留 4=英文大写输入法 5=英文小写输入法 6=保留
		 * 7=特殊输入，项选择
		 */
		public String DefaultType;
		/**
		 * 该编辑的保密标志(用于输入密码,显示*)
		 * 
		 */
		public String SecretFlag;
		/**
		 * 是否显示输入法区域
		 * 
		 */
		public String ShowInputArea;
		/**
		 * 输入是否可以为空
		 */
		public String NullEnable;
		/**
		 * 是否必须输入最大字符个数
		 */
		public String MaxInputMust;
		/**
		 * 是否在底层就判断输入字符个数标志 注：该字段暂时不需要在后台设置，统一设置为1。
		 */
		public String JudgeCntAtonce;
		/**
		 * 是否在开始输入的时候清空填充的内容
		 */
		public String ClearFill;
		/**
		 * 初始填充内容
		 */
		public String FirstFill;
		/**
		 * 选择输入时的帮助内容，最多显示一屏
		 */
		public String HelpChar;
		/**
		 * 字段属性： 
		 *  0. 空录入项（为了录入界面格式对齐而增加的）
		 *  1. 单证号 2. 保险起期(格式固定为YYYYMMDD) 
		 *  3.* 保险起期时间(格式固定为HH[MM[SS]]) 
		 *  4. 保险止期(格式固定为YYYYMMDD) 
		 *  5. 保险止期时间(格式固定为HH[MM[SS]])
		 *  6. 投保人 
		 *  7. 投保人性别 
		 *  8. 投保人生日 
		 *  9. 投保人证件类型 
		 *  10. 投保人证件号码
		 *  11. 被保人 
		 *  12. 被保人性别
		 *  13.	* 被保人生日 
		 *  14. 被保人证件类型 
		 *  15. 被保人证件号码 
		 *  16. 投保人和被保人关系 
		 *  17. 受益人
		 *  18.	* 保险保费（单位为元，可以输入两位小数） 
		 *  19. 保险金额（单位为元） 
		 *  20. 学校
		 *  21. 班级 
		 *  22. 航班号/客运班次 
		 *  23.	* 客票/门票号码，卡号等 
		 *  24. 保险期间:1-按年;2-按日;3-按天(暂时未使用) 
		 *  25. 保险期间值(暂时未使用) 
		 *  26.	* 投保人职业序号(暂时未使用) 
		 *  27. 被保人职业序号(暂时未使用)	 
		 *  50. 联系电话 
		 *  51. 保险费率（单位为百分之几） 
		 *  52. 中标合同价（单位为元） 
		 *  53. 建筑面积（单位为mm） 
		 *  54. 付款方式：包含有现金、转账、刷卡 
		 *  55. 开户银行 
		 *  56. 银行账号 
		 *  57. 银行电话 
		 *  58. 贷款合同编号 
		 *  59. 贷款金额（单位为元） 
		 *  60. 汽车类型(暂时未使用) 
		 *  61. 核定座位数(暂时未使用) 
		 *  62. 被保人有社保标志 
		 *  63. 贷款起期 
		 *  64. 贷款止期
		 * 属性为50－200的字段（包括1-49属性编辑项中有多个编辑项时），发送到后台是采用其它字段，采用的格式为“0xff+属性编码+录入内容+
		 * 分隔符0xOd”，后台收到该数据段后需要把属性分析。
		 * 
		 * 
		 * 200.日期(格式固定为YYYYMMDD) 201.时间(格式固定为HH[MM[SS]])
		 * 
		 * 255 其它。（所有其它字段，录入后不做逻辑判断）
		 * 属性200编号后的所有字段，通信发送时的发送格式为：“提示字符:录入内容+分隔符0x0d”，后台收到该数据包全部保存在一个内容框内
		 * ，查询时显示出来即可。
		 * 注：对于被保人有多个的现象，每个被保人都采用相同的属性，在通信时，只有第一被保人采用固定地方发送，其余共同被保人采用“提示字符
		 * :录入内容+分隔符0x0d”的格式发送。收益人也一样。
		 */
		public String Attribute;
		/**
		 * 填充方式，255-当前值自动累加后填充到下一单。 其它N－把上一单的前N个字节填充到本单
		 */
		public String FillType;
		@Override
		public String toString() {
			return "InsuPlanContent [TSChar="
					+ TSChar + ", BegCol=" + BegCol + ", EndCol=" + EndCol
					+ ", MaxInputCnt=" + MaxInputCnt + ", InputEnAble="
					+ InputEnAble + ", DefaultType=" + DefaultType
					+ ", SecretFlag=" + SecretFlag + ", ShowInputArea="
					+ ShowInputArea + ", NullEnable=" + NullEnable
					+ ", MaxInputMust=" + MaxInputMust + ", JudgeCntAtonce="
					+ JudgeCntAtonce + ", ClearFill=" + ClearFill
					+ ", FirstFill=" + FirstFill + ", HelpChar=" + HelpChar
					+ ", Attribute=" + Attribute + ", FillType=" + FillType
					+ "]";
		}
		
		
	}
}
