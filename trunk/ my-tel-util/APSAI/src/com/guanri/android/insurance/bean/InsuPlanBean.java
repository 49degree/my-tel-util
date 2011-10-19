package com.guanri.android.insurance.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * 业务方案实体
 * Insu 保险（insurance）缩写Ins
 * @author 杨雪平
 *
 */
public class InsuPlanBean {
	private List<InsuPlanAttr> insuPlanAttrList = null;//1．	第一段落：{定额保单属性定义表}
	private List<InsuPlanChan> insuPlanChanList = null;//2．	第二段落：{定额保单销售渠道定义表}
	private List<InsuPlanDutyAmount> insuPlanDutyAmountList = null;//3．	第三段落：{定额保单责任保额定义表}
	private List<InsuPlanDefine> insuPlanDefineList = null;//4．	第四段落：{定额保限定定义表}
	private List<InsuPlanAdditional> insuPlanAdditionalList = null;//5．	第五段落：{保障计划附加信息}
	private List<InsuPlanPromis> insuPlanPromisList = null;//6．	第六段落： { 卡单可约定项目表 }
	private List<InsuPlanCOCode> insuPlanCOCodeList = null;//7．	第七段落：｛保险公司代码｝
	private List<InsuPlanSpecPromis> insuPlanSpecPromisList = null;//8．	第八段落：｛特别约定定义｝
	private List<InsuPlanFeeRate> insuPlanFeeRateList = null;//9．	第九段落：｛定义保费分级累进表｝
	
	public InsuPlanBean(){
		insuPlanAttrList = new ArrayList<InsuPlanAttr>();//1．	第一段落：{定额保单属性定义表}
		insuPlanChanList = new ArrayList<InsuPlanChan>();//2．	第二段落：{定额保单销售渠道定义表}
		insuPlanDutyAmountList = new ArrayList<InsuPlanDutyAmount>();//3．	第三段落：{定额保单责任保额定义表}
		insuPlanDefineList = new ArrayList<InsuPlanDefine>();//4．	第四段落：{定额保限定定义表}
		insuPlanAdditionalList = new ArrayList<InsuPlanAdditional>();//5．	第五段落：{保障计划附加信息}
		insuPlanPromisList = new ArrayList<InsuPlanPromis>();//6．	第六段落： { 卡单可约定项目表 }
		insuPlanCOCodeList = new ArrayList<InsuPlanCOCode>();//7．	第七段落：｛保险公司代码｝
		insuPlanSpecPromisList = new ArrayList<InsuPlanSpecPromis>();//8．	第八段落：｛特别约定定义｝
		insuPlanFeeRateList = new ArrayList<InsuPlanFeeRate>();//9．	第九段落：｛定义保费分级累进表｝	}
	}

	
	public List<InsuPlanAttr> getInsuPlanAttrList() {
		return insuPlanAttrList;
	}

	public void setInsuPlanAttrList(List<InsuPlanAttr> insuPlanAttrList) {
		this.insuPlanAttrList = insuPlanAttrList;
	}

	public List<InsuPlanChan> getInsuPlanChanList() {
		return insuPlanChanList;
	}

	public void setInsuPlanChanList(List<InsuPlanChan> insuPlanChanList) {
		this.insuPlanChanList = insuPlanChanList;
	}

	public List<InsuPlanDutyAmount> getInsuPlanDutyAmountList() {
		return insuPlanDutyAmountList;
	}

	public void setInsuPlanDutyAmountList(
			List<InsuPlanDutyAmount> insuPlanDutyAmountList) {
		this.insuPlanDutyAmountList = insuPlanDutyAmountList;
	}

	public List<InsuPlanDefine> getInsuPlanDefineList() {
		return insuPlanDefineList;
	}

	public void setInsuPlanDefineList(List<InsuPlanDefine> insuPlanDefineList) {
		this.insuPlanDefineList = insuPlanDefineList;
	}

	public List<InsuPlanAdditional> getInsuPlanAdditionalList() {
		return insuPlanAdditionalList;
	}

	public void setInsuPlanAdditionalList(
			List<InsuPlanAdditional> insuPlanAdditionalList) {
		this.insuPlanAdditionalList = insuPlanAdditionalList;
	}

	public List<InsuPlanPromis> getInsuPlanPromisList() {
		return insuPlanPromisList;
	}

	public void setInsuPlanPromisList(List<InsuPlanPromis> insuPlanPromisList) {
		this.insuPlanPromisList = insuPlanPromisList;
	}

	public List<InsuPlanCOCode> getInsuPlanCOCodeList() {
		return insuPlanCOCodeList;
	}

	public void setInsuPlanCOCodeList(List<InsuPlanCOCode> insuPlanCOCodeList) {
		this.insuPlanCOCodeList = insuPlanCOCodeList;
	}

	public List<InsuPlanSpecPromis> getInsuPlanSpecPromisList() {
		return insuPlanSpecPromisList;
	}

	public void setInsuPlanSpecPromisList(
			List<InsuPlanSpecPromis> insuPlanSpecPromisList) {
		this.insuPlanSpecPromisList = insuPlanSpecPromisList;
	}

	public List<InsuPlanFeeRate> getInsuPlanFeeRateList() {
		return insuPlanFeeRateList;
	}

	public void setInsuPlanFeeRateList(List<InsuPlanFeeRate> insuPlanFeeRateList) {
		this.insuPlanFeeRateList = insuPlanFeeRateList;
	}






	/**
	 * 第一段落：{定额保单属性定义表}
	 * String primary key (cardcode,classcode,planno)
	 * “cardcode”、“planno”定义一个业务方案 
	 * “editcode”、“prntcode”定义业务使用的编辑模板和打印模板
	 * “feeamt”决定该险种的保险费，这个数值需要打印在保单上，具体计算方式为“feeamt”对应的所有内容项
	 * “headocno”对应的内容表示本业务的单证号头标识（最多10个字符），用于决定输入的单证号是否合法    
	 */
	public static class InsuPlanAttr{
		public String cardcode		=null ;//char(8),			--{定额保单号码},                                         
		public String cardname		=null ;//char(100),		--{单证名称},                                             
		public String appl_no		=null ;//char(13),		--{申请单编号},                                           
		public String classcode	=null ;//char(10)	,		--{险种代码},                                             
		public String classname 	=null ;//char(100),		--{险种名称},                                             
		public String appf	      =null ;//	char(1)	,		--{主附险，1-主险；2-附加险},                             
		public String begdate		=null ;//char(10)	,		--{开办日期},                                             
		public String enddate		=null ;//char(10)	,		--{停办日期},                                             
		public String cardtype		=null ;//char(1),			--{1.具名撕票式 2.不具名撕票式 3.二联式 4.三联式 5.四联式 
		public String risktype		=null ;//char(1),			--{定义类别: 1.份数定制 2.保额定值 3. 档次定制},          
		public String feetype		=null ;//char(1),			--{保费类型: 1.固定缴费 2.按职业确定保费 3.保费手工录入 4.按建筑面积 5.按造价 6.按保额 7.按年费率；8.按月费率；9.按日费; 10.按保险期限}
		              
		public String feeamtlist		=null;//decimal(10,2),	--{保费金额}                                              
		public String pieces		=null;//decimal(5,2),	--{份数}                                                    
		public String dcdm	    =null;//  	char(2),			--{档次}                                                
		public String timetype =null;//    char(1),         --{保险期间选择,1:保险期限(按年) 2: 保险期限(按月) 3: 保险期限(按日;4:保险期限不固定;5-最长保险期限（按年）; 6-最长保险期限（按月）; 7-最长保险期限（按日）;}
		public String num			=null;//int,				--{相应的数值}   
		public String timelist			=null;//int,				--{相应的数值} 
		
		public String saleattr	=null;//	char(6),			--                                                        
		public String regdate	=null;//	date,			--{定义日期}                                                  
		public String regname	=null;//	char(20),		--{定义人}                                                  
		public String prelmt		=null;//char(1),			--{是否有责任限制 1：有 0：没有}                            
		public String operno		=null;//char(8),			--                                                          
		public String utime		=null;//char(16),		--{本记录最后更新时间},                                       
		public String planno		=null;//char(2),			--{计划号}                                                  
		public String verdate	=null;//	date,			--{审核日期}                                                  
		public String verno		=null;//char(8),			--{审核人}                                                  
		public String is_issue	=null;//	char(1),			--{0：no  1：yes}                                         
		public String headocno	=null;//	char(16); 		--单证号前N位                                             
		public String prntype	=null;//					--打印类型，用来定义险种                                        
		public String prntno		=null;//				--打印模板，用来定义险种方案                                      
		public String editcode	=null;//	char(8)			--编辑模板代码，用来定义险种的录入界面                      
		public String prntcode	=null;//	char(8)			--打印模板代码，用来定义险种的打印格式                      
		public String commtype	=null;//	char(1)			--险种通信方式 0-实时联机，1-非实时脱机	                    
	}
	//2．	第二段落：{定额保单销售渠道定义表}
	public static class InsuPlanChan{
		public String cardcode = null;//		char(8),			--{定额保单号码},
		public String saleattr = null;//		char(2),			--{销售渠道}, 
		public String primary  = null;// key (cardcode,saleattr)	
	}

	/**
	 * 第三段落：{定额保单责任保额定义表}
	 * 本段落表示该业务对应的具体保险条款；
	 * “responame”表示保险条款名称
	 * “classcode”表示条款的类别代码
	 * “clmcode”表示具体的条款代码
	 * “pre”表示该条款的最高赔付金额
	 * 其中
	 * 对于某些险种，保单上没有预印保险条款，所以BD-200在保单上打印了保险条款名称“responame”与赔付金额“pre”；有几个条款就打印几个，本业务文件共5个条款。
	 * 对于某些险种，保险条款已经预印在保单上，所以只需打印赔付金额“pre”。
	 * @author Administrator
	 */
	public static class InsuPlanDutyAmount{
		public String cardcode	= null;//	char(8),			--{定额保单号码}, 
		public String classcode	= null;//	char(10),		--{险种代码}, 
		public String prelist			  = null;// decimal(12,2)	--{保险金额}, 
		public String clmcode		= null;//char(10)			--{责任代码}, 
		public String typeno		= null;//char(10)			--{责任子码}, 
		public String planno		= null;//char(2),			--{计划号}
		public String responame	= null;//	char(100)		--{责任名称}
		public String pretype   = null;//   char(1),	--{保额类型: 0.保额固定 1.按年龄 2.按职业确定保额 3.保额手工录入 4.按建筑面积 5.按造价 6.按保额 7.按年费率；8.按月费率；9.按日费}
		public String preamt  	= null;//	decimal(12,2),	--{额度}
		public String min_pre   = null;//   decimal(12,2)	--{保险金额最小值，为0表示不限制。该变量只在需要录入保险金额时有效，对于主险，要是录入值小于该值，不允许出单；对于附加险，要是按照录入值计算出来的保额小于该值，也允许出单，但必须以该值为准。},
		public String max_pre   = null;//   decimal(12,2)	--{保险金额最大值，为0表示不限制。该变量只在需要录入保险金额时有效，对于主险，要是录入值大于该值，不允许出单；对于附加险，要是按照录入值计算出来的保额大于该值，也允许出单，但必须以该值为准。},

	}
	
	/**
	 * 4．	第四段落：{定额保限定定义表}
     *primary  key (cardcode,comp,sex,begage,minpieces)                                                                                
     *“begage”表示允许的被保人的起始年龄，本业务文件对应内容为6表示6?
     *“endage”表示允许的被保人的结束年龄，本业务文件为18岁止         
     *“maxpieces”表示最大限购份数，某些险种的保单上需要打印这个内容  
     **/
	public static class InsuPlanDefine{
		public String cardcode		= null;//char(8),			--{定额保单号码}, 
		public String 	comp			= null;//char(5),			--{限制销售区域代码，全部为空}
		public String compname		= null;//char(40)			--{限制销售区域名称，全部时为空}
		public String 	sex			  = null;//char(1),			-- 
		public String 	begage		= null;//int,				--{起始年龄}
		public String 	endage		= null;//int,				--{终止年龄}
		public String 	minpieces	= null;//decimal(5,2),	--{限制承保份数}
		public String 	maxpieces	= null;//decimal(5,2),   
		public String maxreprn		= null;//int,				--{限制重复打印次数}
		public String profession  = null;// int,             --{职业,每种职业占1个Bit,为0时表示不限制,为1表示限制,如为11(B1011),则表示第一,二,四种职业限制购买本方案,为0表示所有职业都可以购买(不限制职业)}
		public String 	movedate	= null;//	date, 
		public String 	planno		= null;//char(2)			--default '0',{计划号}
	}
	
	 /**
	  * 第五段落：{保障计划附加信息}
      *primary key (cardcode,planno)                                                                                                                                            
      *“begday”用于计算输入的保险起期的起始有效日期，以当天的日期为基点，当天日期＋begday的日期即保险起期的有效起始日期。                          
      *注意：如果“begday”的内容为负数即表示以当天日期为基点的前X天                                                                                 
      *	“endday” 用于计算输入的保险起期的终止有效日期，以当天的日期为基点，当天日期＋endday的日期即保险起期的有效终止日期。                        
      *例如：本业务文件“begday”内容为0，“endday”内容为14，表示保险起期的合法日期范围为“（当天＋0） ～ （当天＋14）”；注意，两个数值负数有效！  
	  */
	public static class InsuPlanAdditional{
		public String cardcode	= null;//	char(8), 		--保障计划代码                                              
		public String planno		= null;// int,				--保障计划序号                                                
		public String issign		= null;// char(1), 		--是否具名                                                    昶?
		public String cardtype	= null;//	char(1),			--单据类型                                                
		public String activedays= null;//	int,				--激活式激活有效期间                                        
		public String isunion		= null;// char(1),			--是否共保                                                  
		public String unionrate	= null;//	decimal(7,3),	--共保比例                                                
		public String begday		= null;//int,				--保险起期自签单后 起始天数                                   
		public String endday		= null;//int,				--保险起期自签单后 截至天数                                   
		public String effect    = null;// char(1),       --立即生效，0-不立即生效，1-要是保险起期为当天，则立即生效
		public String iscont		= null;//char(1),			--是否自动续保                                              
		public String proc			= null;//char(5),			--适用流程   
	}
	
	/**
	 * 第六段落： { 卡单可约定项目表 }
	 *primary key (cardcode,classcode,code) 
     *该段落内容具体描述各保险条款的赔付比例，太保目前仅学平险有段落内容。
     *?	“classcode”表示条款的类别代码，请参考第三段落的定义
     *?	“code”表示顺序号
     *?	“dbvalue”表示百分比
     *太保的学平险保单需要打印“住院医疗保险责任”的百分比，“classcode”为“EA3713”，所以保单上要打印“EA3713”的百分比，本业务文件中，该内容按顺序号为“50、60、70、80、90”                           
	 */
	public static class InsuPlanPromis{
		public String cardcode	= null;//	char(8), 		--{保障计划代码}                                   
		public String classcode	= null;//	char(10),			--{险种代码}                                     
		public String code      = null;//  	char(6),			--{项目代码}                                   
		public String dbvalue		= null;//decimal(12,4),		--{数字型约定值}                               
		public String chvalue		= null;//char(120),			--{字符型约定值}                                 
		public String attr      = null;//    char(1),	--{属性 1-必须约定 2-限制值 3-不准录入 4-多条值  5}
		public String type			= null;//char(1),			-- type to stype                                                                                  
		public String planno		= null;//char(2),			--{计划号}   
	}
	
	//7．	第七段落：｛保险公司代码｝
	public static class InsuPlanCOCode{
		public String cardcode	= null;//	char(8), 		--保障计划代码      
		public String planno		= null;//char(2),			--{计划号}          
		public String comp			= null;//char(3),			--{分公司代码}      
	}
	
	/**
	 * 8．	第八段落：｛特别约定定义｝
	 * //在定义有特别约定时，在指定的位置打印。
	 * @author Administrator
	 *
	 */
	public static class InsuPlanSpecPromis{
		public String cardcode = null;//		char(8), 		--保障计划代?
		public String planno	 = null;//	char(2),			--{计划号}   
		public String specagr	 = null;//	char(300),		--{特别约定} 

	}	
	
	/**
	 *第九段落：｛定义保费分级累进表｝
     *primary key (cardcode,planno,classcode,preamt)
     *	保费计算为：
     *		risktype1=0
     *			Feeamt= feeamt0+保额*feeamt1*N/1000    (N= 输入值 - preamt0)
     *		risktype1=1
     *			Feeamt= feeamt0+保额×feeamt1/1000。
     *		risktype1=2
     *			Feeamt= feeamt0+ feeamt1。
     *	保额计算：
     *	要是保额录入，则对于主险（appf=1）浮动费率的保额为录入值，附加险（appf=2）的浮动费率的保额为录入值的比例，即录入值×授权文件中附加险保额定义值/授权文件中主险保额值。注：这种情况，授权文件中的主险保额一定只有唯一一项，提示保费计算失败。
     *	要是保额没有录入，则不管是主险还是附加险，所有保额从授权文件中读取。但要是保费计算和保额有关，该险种存在的保额数据不唯一，提示保费计算失败。
     *如果一个产品定义的是按保额浮动，但是在录入的模板中又不录入保额，则提示保费计算失败
     *对于feetype方式选择为按保额的险种，允许preamt0值不为0，可以选择的计算方式risktype1可以选择为1或2，当选择1时，保费为: Feeamt= feeamt0+（保额－preamt0）×feeamt1/1000。                                         
	 */
	public static class InsuPlanFeeRate{
		public String cardcode	= null;//	char(8), 		--保障计划代码                                                ?
		public String planno		= null;// char(2),			--{计划号}                                                     
		public String classcode	= null;//	char(10)	,		--{险种代码},                                                
		public String feetype		= null;//char(1),			--{保费类型: 1.固定缴费 2.按职业确定保费 3.保费手 工录入 4.按建筑面积 5.按造价 6.按保额 7.按年费率；8.按月费率；9.按日费；10.按保险期限}
		public String preamt  	= null;//	decimal(12,2),	--{总额度}                                                
		public String preamt0		= null;//decimal(12,2),	--{固定费率的额度}                                          
		public String feeamt0		= null;//decimal(10,2),	--{固定部分的保费}                                          
		public String risktype1 = null;//	char(1),			--{计算方式:0-按保额*feeamt1*N；(N= 输入值 - preamt0)1-按保额* feeamt1、2-固定}                                                            
		public String feeamt1 	= null;//	decimal(10,4),	--{费率（对应费率）/保费（对应固定）} 费率为千分之        
		public String feetype2	= null;//	char(1),			--{保费类型: 1.固定缴费 2.按职业确定保费 3.保费手工录入 4.按建筑面积 5.按造价 6.按保额 7.按年费率；8.按月费率；9.按日费；10-按保险期限}
		public String preamt2  	= null;//	decimal(12,2),	--{额度2}                                                 
		public String feetype3	= null;//	char(1),			--{保费类型: 1.固定缴费 2.按职业确定保费 3.保费手工录入 4.按建筑面积 5.按造价 6.按保额 7.按年费率；8.按月费率；9.按日费；10.按保险期限}
		public String preamt3  	= null;//	decimal(12,2),	--{额度3}
	}	
}
