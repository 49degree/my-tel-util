package com.guanri.android.insurance.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAdditional;
import com.guanri.android.insurance.bean.InsuPlanBean.InsuPlanAttr;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanContent;
import com.guanri.android.insurance.bean.InsuranceBean;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnDatePickDlgBtn;
import com.guanri.android.insurance.common.DialogUtils.OnTimePickDlgBtn;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.service.InsuSaleOrderService;
import com.guanri.android.lib.components.ZoomLinearLayout;
import com.guanri.android.lib.components.ZoomLinearLayout.OnResizeListener;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.IdcardUtils;
import com.guanri.android.lib.utils.StringUtils;
import com.guanri.android.lib.utils.TimeUtils;

public class InsuViewByModeFile extends LinearLayout{
	public static Logger logger = Logger.getLogger(InsuViewByModeFile.class);// 日志对象;
	
	public final static int CONTENT_INIT_ID = 10000;//控件ID开始值
	public final static HashSet<String> spinnerAttributeSet = new HashSet<String>();
	public final static String TIME_VALUE = "----";
	
	private Context context = null;
	private InsuranceBean insuranceBean;
	private InsuViewPlanBean insuViewPlanBean;//视图模板文件对象
	private InsuPlanBean insuPlanBean ;//业务方案文件模板对象
	private List<InsuPlanContent> insuPlanContentList;//界面输入项定义列表
	private HashMap<String,Integer> attributeTimes = null;
	//viewIdInsuPlanContentMap保存选择视图的对应的值 ，还可以保持视图创建的顺序，判断视图是否操作
	private LinkedHashMap<String,InsuPlanContent> viewIdInsuPlanContentMap = new LinkedHashMap<String,InsuPlanContent>();
	
	private String editname = null;//编辑名称
	private String editcode = null;//编辑代码
	private String cardCode = null;//8		业务方案代码	CardCode	ASC	8
	private String planNo = null;//9		业务方案序号	PlanNo	HEX	2
	
	private LinearLayout.LayoutParams txtParams;// TextView 布局文件属性
	private LinearLayout.LayoutParams edtParams;// EditText 布局文件属性
	private ZoomLinearLayout scrollView = null;//界面可滚动
	
	private InsuSaleOrderService insuSaleOrderService = null;

	private OnResizeHandler onResizeHandler;
	
	private EditText dzh  = null; 

	private EditText tbr = null;
	private Button tbrsr = null;
	private Spinner tbrsex = null;
	private Spinner tbrPaperType = null;
	private Button tbrPaperNum = null;
	
	private Button bbrsr = null;
	private Spinner bbrsex = null;
	private Spinner bbrPaperType = null;
	private EditText bbrPaperNum = null;
	
	private DBOperator dbOperator = null;
	
	static{
		spinnerAttributeSet.add("7");//7.	投保人性别	
		spinnerAttributeSet.add("9"); //9.	投保人证件类型
		spinnerAttributeSet.add("12");//12.	被保人性别
		spinnerAttributeSet.add("14");//14.	被保人证件类型
		spinnerAttributeSet.add("49");// 49. 组合
		
		spinnerAttributeSet.add("54");// 54. 付款方式：包含有现金、转账、刷卡
		spinnerAttributeSet.add("255");// 255. 其他 nsuPlanContent.InputEnAble.equals("128")) 是否选择项
	}


	public InsuViewByModeFile(Context context){
        super(context);
        this.context = context;
        dbOperator = DBOperator.getInstance();
	}
	
	public InsuViewByModeFile(Context context, AttributeSet attr){
        super(context, attr);
        this.context = context;
        dbOperator = DBOperator.getInstance();
	}

	
	public void setModeInfo(InsuranceBean insuranceBean) {
		
		this.insuranceBean = insuranceBean;
		this.insuViewPlanBean = insuranceBean.getInsuViewPlanBean();
		this.insuPlanBean = insuranceBean.getInsuPlanBean();
		
		// 获得协议编号，协议名称
		editname = insuViewPlanBean.getInsuPlanModeList().get(0).cardname;
		editcode = insuViewPlanBean.getInsuPlanModeList().get(0).editcode;		// 获取协议各控件
		cardCode = insuPlanBean.getInsuPlanAttrList().get(0).cardcode;//8		业务方案代码	CardCode	ASC	8
		planNo = insuPlanBean.getInsuPlanAttrList().get(0).planno;//9		业务方案序号	PlanNo	HEX	2
		insuPlanContentList = insuViewPlanBean.getInsuPlanContentList();
		attributeTimes = new HashMap<String,Integer>(insuPlanContentList.size());//记录同一属性编号出现的次数
		insuSaleOrderService = new InsuSaleOrderService(context);		
		
		
		// 设置主界面布局
		this.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		this.setOrientation(LinearLayout.VERTICAL);
		scrollView = new ZoomLinearLayout(this.context);
		scrollView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		//scrollView.setScrollBarStyle(LinearLayout.VERTICAL);
		this.addView(scrollView);

		// 各控件的位置布局
		txtParams = new LinearLayout.LayoutParams(100,LinearLayout.LayoutParams.WRAP_CONTENT);
		txtParams.setMargins(10, 0, 0, 0);
		txtParams.weight = 3;
		txtParams.gravity = Gravity.CENTER_VERTICAL;
		
		edtParams = new LinearLayout.LayoutParams(300,60);
		edtParams.setMargins(0, 0, 10, 0);
		edtParams.weight = 1;
		edtParams.gravity = Gravity.CENTER_VERTICAL;
		initView();
		
		onResizeHandler = new OnResizeHandler();
		
		scrollView.setOnResizeListener(new OnResizeListener() {
			//处理重新设置ZoomLinearLayout控件大小
			@Override
			public void onResize(int w, int h, int oldw, int oldh) {
				if(scrollView.isFirstCreate){
					scrollView.firstCreateHeight = h;
					scrollView.isFirstCreate = false;
		    	} else {
		    		ViewGroup.LayoutParams layoutParams = scrollView.child.getLayoutParams();
		    		int keyboardHeight = 0;
		    		if(h < oldh){
		    			keyboardHeight = oldh - h;
		    			layoutParams.height = layoutParams.height + keyboardHeight;
		    		} else {
		    			keyboardHeight = h - oldh;
		    			layoutParams.height = layoutParams.height - keyboardHeight;
		    		}
		    		Message message = new Message();
		    		message.obj = layoutParams;
		    		onResizeHandler.sendMessage(message);
		    	}
			}
		});
	}
	
	//声明Handler用于处理 ZoomLinearLayout 弹出软键盘时重新设置size
	class OnResizeHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			//获取LayoutParam在UI线程当中修改 ZoomLinearLayout第一个子控件的大小
			ViewGroup.LayoutParams layoutParams = (android.view.ViewGroup.LayoutParams) msg.obj;
			scrollView.child.setLayoutParams(layoutParams);
		}
	}

	
	/**
	 * 构建界面
	 */
	private void initView(){
		this.setBackgroundColor(Color.WHITE);
		int contentNum = insuPlanContentList.size();
		int height = 0;
		// 设置主布局
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		
		LinearLayout mLayout = new LinearLayout(context);
		mLayout.setLayoutParams(mLayoutParams);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		//使背景获取焦点，焦点不要默认在输入框
		mLayout.setFocusable(true);
		mLayout.setFocusableInTouchMode(true);
		scrollView.addView(mLayout);
		
//		LinearLayout headerSpaceLayout = new LinearLayout(context);
//		
//		headerSpaceLayout.setLayoutParams(new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.FILL_PARENT,10));
//		mLayout.addView(headerSpaceLayout);
		
		for (int i = 0; i < contentNum; i++) {
			InsuPlanContent nsuPlanContent = insuPlanContentList.get(i);
			String tSChar = nsuPlanContent.TSChar;
			// 空录入项 跳过,表示同下一行的定义共同构成一个输入选项，如果下一行没有标体，取该行的标题放到下一行
			if (nsuPlanContent.Attribute.equals("0")){
				nsuPlanContent = insuPlanContentList.get(++i);
				if(nsuPlanContent.TSChar!=null&&!"".equals(nsuPlanContent.TSChar)){
					tSChar = nsuPlanContent.TSChar;
				}else{
					nsuPlanContent.TSChar = tSChar;
				}
			}
				
			// 子线性布局文件
			LinearLayout sLayout = new LinearLayout(context);
			sLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			sLayout.setOrientation(LinearLayout.HORIZONTAL);
			sLayout.setPadding(0, 5, 0, 5);
			mLayout.addView(sLayout);
			height += 11;
			// 划线
			LinearLayout sspaceLayout = new LinearLayout(context);
			sspaceLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,1));
			sspaceLayout.setBackgroundColor(R.color.dialog_line);
			mLayout.addView(sspaceLayout);
			
			//创建标题
			if (nsuPlanContent.TSChar != null) {
				TextView tv = new TextView(context);
				tv.setText(tSChar);
				tv.setLayoutParams(txtParams);
				tv.setTextSize(18);
				tv.setTextColor(Color.BLACK);
				sLayout.addView(tv);
			}
			
			switch (Integer.valueOf(nsuPlanContent.Attribute)) {
			//以下为简单输入框
			case 1: //1.	单证号
				//logger.error(nsuPlanContent.TSChar);
				 dzh = createEditView(nsuPlanContent);
				 dzh.setText(getNewBillNo());
				//dzh.setText("10154014211210073050");
				dzh.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				sLayout.addView(dzh);
				break;
			case 2:   //2.	保险起期(格式固定为YYYYMMDD)	
				Button insuBeginDate = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,new ButtonRelationOprator(){
					public void OnRelationOprato(Button button){//计算关联的保险止期
						try{
							int id = 4;
							id += CONTENT_INIT_ID;
							id = Integer.parseInt(String.valueOf(id)+1);
							if(viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
								Button insuEndDate = (Button)findViewById(id);
								String insuStartDateText = button.getText().toString();
								id = Integer.parseInt(String.valueOf(49+CONTENT_INIT_ID)+1);
								if(insuStartDateText!=null&&!"".equals(insuStartDateText)&&
										viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
									Spinner spinner = (Spinner)findViewById(id);
									int spinnerPosition = spinner.getSelectedItemPosition();
									Date endDate= parseEndDate(TimeUtils.getDateFromString(insuStartDateText, TimeUtils.formatDate),spinnerPosition);
									insuEndDate.setText(TimeUtils.getTimeString(endDate, TimeUtils.formatDate));
								}
							}
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				insuBeginDate.setText(TimeUtils.getTimeString(getinsuBeginDate(), TimeUtils.formatDate));
				
				sLayout.addView(insuBeginDate);
				break;
			case 3:   //3.	保险起期时间(格式固定为HH[MM[SS]])
				Button insuStartTime = createTimeButtonView(nsuPlanContent,TimeUtils.formatTime);
				insuStartTime.setText("00:00:01");
				sLayout.addView(insuStartTime);
				break;
			case 4:   //4.	保险止期(格式固定为YYYYMMDD)	
				Button insuEndDate = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null);
				try{
					int id = Integer.parseInt(String.valueOf(49+CONTENT_INIT_ID)+1);
					if(viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
						Spinner spinner = (Spinner)findViewById(id);
						int spinnerPosition = spinner.getSelectedItemPosition();
						Date endDate= parseEndDate(getinsuBeginDate(),spinnerPosition);
						insuEndDate.setText(TimeUtils.getTimeString(endDate, TimeUtils.formatDate));
					}else{
						insuEndDate.setText(TimeUtils.getTimeString(getinsuBeginDate(), TimeUtils.formatDate));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				sLayout.addView(insuEndDate);
				break;
			case 5:   //5.	保险止期时间(格式固定为HH[MM[SS]])
				Button insuEndTime = createTimeButtonView(nsuPlanContent,TimeUtils.formatTime);
				insuEndTime.setText("23:59:59");
				sLayout.addView(insuEndTime);
				break;				
			case 6:   //6.	投保人	
				tbr = createEditView(nsuPlanContent);
				tbr.addTextChangedListener(new TextViewChangeWatcher(tbr,11));
				sLayout.addView(tbr);
				break;
			case 7:   //7.	投保人性别	
				tbrsex = createSpinner(nsuPlanContent);
				tbrsex.setOnItemSelectedListener(new OnItemSelectedListener(){
					public void onNothingSelected(AdapterView<?> arg0){
					}
					public void onItemSelected(AdapterView<?>  arg0, View arg1, int arg2, long arg3){
						try{
							int id = Integer.parseInt(String.valueOf(12+CONTENT_INIT_ID)+1); //12.	被保人性别
							if(viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
								Spinner spinner = (Spinner)findViewById(id);
								spinner.setSelection(arg2);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				
				sLayout.addView(tbrsex);
				break;
			case 8:   //8.	投保人生日
				tbrsr = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null);
				tbrsr.addTextChangedListener(new TextViewChangeWatcher(tbrsr,13));//13.	被保人生日
				sLayout.addView(tbrsr);
				break;
			case 9:   //9.	投保人证件类型
				tbrPaperType = createSpinner(nsuPlanContent);
				tbrPaperType.setOnItemSelectedListener(new OnItemSelectedListener(){
					public void onNothingSelected(AdapterView<?> arg0){
					}
					public void onItemSelected(AdapterView<?>  arg0, View arg1, int arg2, long arg3){
						try{
							int id = Integer.parseInt(String.valueOf(14+CONTENT_INIT_ID)+1); //14.	被保人证件类型
							if(viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
								Spinner spinner = (Spinner)findViewById(id);
								spinner.setSelection(arg2);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				
				sLayout.addView(tbrPaperType);
				
				
				sLayout.addView(createSpinner(nsuPlanContent));
				break;
			case 10:  //10.	投保人证件号码	
				tbrPaperNum = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null);
				tbrPaperNum.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				tbrPaperNum.addTextChangedListener(new TextViewChangeWatcher(tbrPaperNum,15));//15.	被保人证件号码
				bbrPaperNum.setOnFocusChangeListener(new TbrCardTextOnFocusChangeListener());
				sLayout.addView(tbrPaperNum);
				break;
			case 11:  //11.	被保人
				EditText bbr = createEditView(nsuPlanContent);
				bbr.addTextChangedListener(new TextViewChangeWatcher(bbr,17));// 17受益人
				sLayout.addView(bbr);
				break;
			case 12:  //12.	被保人性别
				bbrsex = createSpinner(nsuPlanContent);
				sLayout.addView(bbrsex);
				break;
			case 13:  //13.	被保人生日
				bbrsr = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null);
				sLayout.addView(bbrsr);
				break;			
			case 14:  //14.	被保人证件类型
				bbrPaperType = createSpinner(nsuPlanContent);
				sLayout.addView(bbrPaperType);
				break;
			case 15:  //15.	被保人证件号码
				bbrPaperNum = createEditView(nsuPlanContent);
				bbrPaperNum.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				bbrPaperNum.setOnFocusChangeListener(new DbrCardTextOnFocusChangeListener());
				sLayout.addView(bbrPaperNum);
				break;			
			case 16:  //16.	投保人和被保人关系
				sLayout.addView(createEditView(nsuPlanContent));
				break;				
			case 17:  // 17受益人
				sLayout.addView(createEditView(nsuPlanContent));
				break;		
			case 18:  // 18. 保险保费（单位为元，可以输入两位小数）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 19:  // 19. 保险金额（单位为元）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 20:  // 20. 学校
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 21:  // 21. 班级
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 22: // 22. 航班号/客运班次
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 23:// 23. 客票/门票号码，卡号等
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 49:// 49. 组合
				String helpChar = getAssembled(insuPlanBean);
				Spinner zhSpinner = createSpinner(nsuPlanContent,helpChar);
				zhSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
					public void onNothingSelected(AdapterView<?> arg0){
					}
					public void onItemSelected(AdapterView<?>  arg0, View arg1, int arg2, long arg3){
						try{
							int id = 2;
							id += CONTENT_INIT_ID;
							id = Integer.parseInt(String.valueOf(id)+1);//开始日期视图ID
							if(viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
								Button insuStartDate = (Button)findViewById(id);
								String insuStartDateText = insuStartDate.getText().toString();
								id = Integer.parseInt(String.valueOf(4+CONTENT_INIT_ID)+1);//结束日期视图ID
								if(insuStartDateText!=null&&!"".equals(insuStartDateText)&&
										viewIdInsuPlanContentMap.containsKey(String.valueOf(id))){
									Button insuEndDate = (Button)findViewById(id);
									Date endDate= parseEndDate(TimeUtils.getDateFromString(insuStartDateText, TimeUtils.formatDate),arg2);
									insuEndDate.setText(TimeUtils.getTimeString(endDate, TimeUtils.formatDate));
								}
							}
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
				sLayout.addView(zhSpinner);
				break;
			case 50:// 50. 联系电话
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 51: // 51. 保险费率（单位为百分之几）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 52: // 52. 中标合同价（单位为元）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 53:// 53. 建筑面积（单位为mm）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			case 54: // 54. 付款方式：包含有现金、转账、刷卡
				sLayout.addView(createSpinner(nsuPlanContent));
				break;
			case 55:// 55. 开户银行
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 56: // 56. 银行账号
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 57:// 57. 银行电话
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 58:// 58. 贷款合同编号
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 59:// 59. 贷款金额（单位为元）
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 62:// 62. 被保人有社保标志
				sLayout.addView(createEditView(nsuPlanContent));
				break;
			
			case 63: // 63. 贷款起期
				sLayout.addView(createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null));
				break;
			case 64: // 64. 贷款止期
				sLayout.addView(createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null));
				break;
			case 255: // 255. 其他
				if (nsuPlanContent.InputEnAble.equals("128")) {// 是否问选择项
					// 设置可选属性
					sLayout.addView(createSpinner(nsuPlanContent));
				}else if(nsuPlanContent.TSChar.indexOf("日期")>-1){// 文本框
					Button insuDateInput = createDateButtonView(nsuPlanContent,TimeUtils.formatDate,null);
					insuDateInput.setText(TimeUtils.getTimeString(new Date(),TimeUtils.formatDate));
					sLayout.addView(insuDateInput);
				}else{
					sLayout.addView(createEditView(nsuPlanContent));
				}
				break;
			default:
				break;

			}
			height += edtParams.height;
		}
		
		LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		btnLayoutParams.setMargins(0, 30, 0, 15);
		
		height += 45;
		
		LinearLayout btnLayout = new LinearLayout(context);
		btnLayout.setLayoutParams(btnLayoutParams);
		btnLayout.setOrientation(LinearLayout.HORIZONTAL);
		btnLayout.setGravity(Gravity.CENTER);
		
		LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
				200, LinearLayout.LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(10, 0, 0, 0);//按钮属性
		
		Button btn_ok = new Button(context);
		btn_ok.setText("提交");
		btn_ok.setLayoutParams(btnParams);
		btn_ok.setBackgroundResource(R.drawable.login_button);
		btn_ok.setOnClickListener(new SubmitOnClickListener());
		btnLayout.addView(btn_ok);

		Button btn_cancel = new Button(context);
		btn_cancel.setText("清空");
		btn_cancel.setBackgroundResource(R.drawable.login_button);
		btn_cancel.setLayoutParams(btnParams);
		btnLayout.addView(btn_cancel);
		
		height += btn_ok.getBackground().getMinimumHeight();
		
		ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
		layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
		layoutParams.height = height;
		mLayout.setLayoutParams(layoutParams);

		mLayout.addView(btnLayout);

	}
	
	private String getNewBillNo() {
		String newBillNo = "";
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("cardCode=", cardCode);
		params.put("planNo=", planNo);
		params.put("ORDERBY","BillNo DESC");
		params.put("LIMIT","0,1");
		List<Object> objectlist = dbOperator.queryBeanList(DBBean.TB_SALE_ORDER, params);
		if(objectlist.size()>0){
			SaleOrderBean saleOrderBean = (SaleOrderBean)objectlist.get(0);
			newBillNo = insuSaleOrderService.addBillNo(saleOrderBean.BillNo);
		}
		return newBillNo;
	}

	/**
	 * 根据选择的保险组合和开始日期计算保险结束日期
	 * @param startDate
	 * @param choice
	 * @return
	 */
	private Date parseEndDate(Date startDate,int position){
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		String timeValue = null;
		if(feeamtsArray!=null&&feeamtsArray.length>0){
			timeValue = feeamtsArray[position][0];
			int timetype = Integer.parseInt((insuPlanBean.getInsuPlanAttrList().get(0).timetype));
			/**
			 * timetype   --{保险期间选择,
			 * 1:保险期限(按年) 
			 * 2: 保险期限(按月) 
			 * 3: 保险期限(按日;
			 * 4: 保险期限不固定;
			 * 5-最长保险期限（按年）; 
			 * 6-最长保险期限（按月）; 
			 * 7-最长保险期限（按日）;}
			 */
			switch (timetype) {
			case 1:
				c.set(Calendar.YEAR, c.get(Calendar.YEAR)+Integer.parseInt(timeValue));
				break;
			case 2:
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+Integer.parseInt(timeValue));
				break;
			case 3:
				c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+Integer.parseInt(timeValue));
				break;
			case 4:
				break;
			default:
				break;
			}
		}
		return c.getTime();
	}

	
	/**
	 * 保险组合情况构造
	 * @param insuPlanBean
	 * @return
	 */
	private String[][] feeamtsArray = null;
	
	private String getAssembled(InsuPlanBean insuPlanBean){
		HashMap<String,String> feeam = new HashMap<String,String>();
		List<InsuPlanAttr> insuPlanAttrList = insuPlanBean.getInsuPlanAttrList();
		//分割时间与保费，构造选项
		for(InsuPlanAttr insuPlanAttr:insuPlanAttrList){
			String feeamtlist = insuPlanAttr.feeamtlist;//保费列表
			String timelist = insuPlanAttr.timelist;//对应的时间列表
			String[] feeamts = feeamtlist.split(",");
			String[] times = timelist.split(",");
			for(int i=0;i<times.length;i++){
				String tempFee = "";
				if(feeam.containsKey(times[i])){//合并主险和附加险
					int oldFee = 0;
					int newFee = 0;
					try{
						oldFee = Integer.parseInt(feeam.get(times[i]));//获取原先已经存在的保费
						newFee = Integer.parseInt(i<feeamts.length?feeamts[i]:"");//新增加的保费
					}catch(Exception e){
					}
					tempFee = String.valueOf(oldFee+newFee);
				}else{
					tempFee = i<feeamts.length?feeamts[i]:"";
				}
				feeam.put(times[i], tempFee);
			}
		}

		int timetype = Integer.parseInt((insuPlanBean.getInsuPlanAttrList().get(0).timetype));
		String timetypestr = null;
		/**
		 * timetype   --{保险期间选择,
		 * 1:保险期限(按年) 
		 * 2: 保险期限(按月) 
		 * 3: 保险期限(按日;
		 * 4: 保险期限不固定;
		 * 5-最长保险期限（按年）; 
		 * 6-最长保险期限（按月）; 
		 * 7-最长保险期限（按日）;}
		 */
		switch (timetype) {
		case 1:
			timetypestr="年";
			break;
		case 2:
			timetypestr="月";
			break;
		case 3:
			timetypestr="天";
			break;
		case 4:
			timetypestr="期间不固定";
			break;
		default:
			break;
		}
		StringBuffer result = new StringBuffer();
		//按照主险的时间列表构造LIST,方便以后使用
		String timelist =  insuPlanAttrList.get(0).timelist;//对应的时间列表
		String[] timeArray = timelist.split(",");
		feeamtsArray = new String[timeArray.length][2];//timelist.split(",");
		
		for(int i=0;i<timeArray.length;i++){
			feeamtsArray[i][0] = timeArray[i];
			feeamtsArray[i][1] = feeam.get(feeamtsArray[i][0]);
			result.append(i+1).append("=").append(feeamtsArray[i][0]).append(timetypestr);
			result.append(TIME_VALUE).append(feeamtsArray[i][1]).append("元").append(";");
		}
		return result.toString();
	}

	/**
	 * 创建选择项
	 * @param nsuPlanContent
	 * @return
	 */
	
	private Spinner createSpinner(InsuPlanContent nsuPlanContent,String adptString){
		int viewId = createViewId(nsuPlanContent.Attribute);
		
		nsuPlanContent.HelpChar = adptString;
		viewIdInsuPlanContentMap.put(String.valueOf(viewId), nsuPlanContent);//保存选择视图的对应定义对象
		
		Spinner spn = new Spinner(context);
		spn.setLayoutParams(edtParams);
		spn.setBackgroundResource(R.drawable.insu_sale_spn_btn);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (adptString != "" && adptString.length() > 0) {
			String[][] param = StringUtils.splitString(adptString, ";", "=");
			for (int i = 0; i < param.length; i++) {
				adapter.add(param[i][1]);
			}
		}
		
		
		spn.setAdapter(adapter);
		spn.setId(viewId);
		return spn;
	}
	
	private Spinner createSpinner(InsuPlanContent nsuPlanContent){
		return createSpinner(nsuPlanContent,nsuPlanContent.HelpChar);
	}
	
	/**
	 * 构造日期输入框
	 * @param nsuPlanContent
	 * @return
	 */
	private Button createDateButtonView(InsuPlanContent nsuPlanContent,final String dateFormat,final ButtonRelationOprator relationOprator){
		int viewId = createViewId(nsuPlanContent.Attribute);
		
		viewIdInsuPlanContentMap.put(String.valueOf(viewId), nsuPlanContent);//保存选择视图的对应定义对象
		
		final Button dateBtn = new Button(context);
		dateBtn.setId(viewId);
		dateBtn.setLayoutParams(edtParams);
		dateBtn.setBackgroundResource(R.drawable.login_button);
		dateBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				DialogUtils.showChoiceDateDlg(context, dateBtn.getText().toString(), dateFormat, new OnDatePickDlgBtn(){
					public void OnPickBtn(DatePicker dtpDate){
						Calendar c  = Calendar.getInstance();
						
						c.set(Calendar.DAY_OF_MONTH, dtpDate.getDayOfMonth());
						c.set(Calendar.MONTH, dtpDate.getMonth());
						c.set(Calendar.YEAR, dtpDate.getYear());
						dateBtn.setText(new SimpleDateFormat(dateFormat).format(c.getTime()));
						if(relationOprator!=null){
							relationOprator.OnRelationOprato(dateBtn);	
						}
						
					}
				});
			}
			
		});
		return dateBtn;
	}
	/**
	 * 按钮关联操作
	 * @author Administrator
	 *
	 */
	interface ButtonRelationOprator{
		public void OnRelationOprato(Button button);
	}
	
	/**
	 * 构造时间输入框
	 * @param nsuPlanContent
	 * @return
	 */
	private Button createTimeButtonView(InsuPlanContent nsuPlanContent,final String timeFormat){
		int viewId = createViewId(nsuPlanContent.Attribute);
		viewIdInsuPlanContentMap.put(String.valueOf(viewId), nsuPlanContent);//保存选择视图的对应定义对象
		final Button timeBtn = new Button(context);
		timeBtn.setId(viewId);
		timeBtn.setLayoutParams(edtParams);
		timeBtn.setBackgroundResource(R.drawable.login_button);
		timeBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				
				// TODO Auto-generated method stub
				DialogUtils.showChoiceTimeDlg(context, timeBtn.getText().toString(), timeFormat, new OnTimePickDlgBtn(){
					public void OnPickBtn(TimePicker dtpTime){
						Calendar c  = Calendar.getInstance();
						
						c.set(Calendar.HOUR_OF_DAY, dtpTime.getCurrentHour());
						c.set(Calendar.MINUTE, dtpTime.getCurrentMinute());
						timeBtn.setText(new SimpleDateFormat(timeFormat).format(c.getTime()));
					}
				});
			}
			
		});
		return timeBtn;
	}	

	
	/**
	 * 构造输入框
	 * @param nsuPlanContent
	 * @return
	 */
	private EditText createEditView(InsuPlanContent insuPlanContent){
		int viewId = createViewId(insuPlanContent.Attribute);
		viewIdInsuPlanContentMap.put(String.valueOf(viewId), insuPlanContent);//保存选择视图的对应定义对象
		EditText editText = new EditText(context);
		editText.setLayoutParams(edtParams);
		editText.setId(viewId);
		editText.setMinLines(1);
		editText.setBackgroundResource(R.drawable.apsai_edittext_item);
		editText.setPadding(8, 0, 0, 0);
		//限制最大输入长度	
		int maxInputCnt = 0;
		try{
			maxInputCnt = Integer.parseInt(insuPlanContent.MaxInputCnt);
		}catch(Exception e){
			
		}
		if(maxInputCnt>0){
			editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxInputCnt)});  
		}
		//密码字段
		if("1".equals(insuPlanContent.SecretFlag)){
			
		}
		
		
		return editText;
	}
	
	
	/**
	 * 取当前attribute是第几次出现
	 * 判断是否已经存在相同的attribute
	 * @param attribute
	 * @return
	 */
	//KEY 表示VIEW ID,VALUE表示该VIEW 添加的顺序，便于在打印模板中使用
	private HashMap<String,Integer> viewOrderMap = new HashMap<String,Integer>();
	int createIndex = 1;
	private int createViewId(String attribute){
		int attributeTime = 0;
		int id = 0;
		if(attributeTimes.containsKey(attribute)){
			attributeTime = attributeTimes.get(attribute).intValue();
		}
		attributeTimes.put(attribute, ++attributeTime);
		
		try{
			id = Integer.parseInt(attribute);
			id += CONTENT_INIT_ID;
			id = Integer.parseInt(String.valueOf(id)+attributeTime);
		}catch(Exception e){
			e.printStackTrace();
		}
		viewOrderMap.put(String.valueOf(id), createIndex++);
		return id;
	}
	

	/**
	 * 提交按钮处理事件
	 * @author Administrator
	 *
	 */
	private class SubmitOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//等待提示框
			final ProgressDialog btDialog = new ProgressDialog(context);
			btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise));    
			btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_common_server_loading));
			btDialog.show();
			
			InsuViewParseValue insuViewParseValue = new InsuViewParseValue((Activity)context,viewIdInsuPlanContentMap,attributeTimes,viewOrderMap);
			SaleOrderBean saleOrderBean = insuViewParseValue.getSaleOrderBean();
			HashMap<Integer, String> inputValueMap = insuViewParseValue.getInputValueMap();
			//设置批次号
			String batchNo = SharedPreferencesUtils.getConfigString(
					SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.CHECK_ID);
			if("".equals(batchNo)){
				batchNo = "1";
			}
			saleOrderBean.setCheck_id(Integer.parseInt(batchNo));
			saleOrderBean.setOperate_time(TimeUtils.getTimeString(new Date()));//操作时间
			saleOrderBean.setSale_time(TimeUtils.getTimeString(new Date()));//操作时间
			saleOrderBean.setCardCode(cardCode);//8		业务方案代码	CardCode	ASC	8
			saleOrderBean.setPlanNo(planNo);//9		业务方案序号	PlanNo	HEX	2
			saleOrderBean.setOperator_id(((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserId());
			//计算保险费
			if(feeamtsArray!=null){//如果是按照组合缴费
				//getInsuAssembled的ID目前是从1开始计算，应该-1
				try{
					saleOrderBean.setInsured_amount(100*Integer.parseInt(feeamtsArray[saleOrderBean.getInsuAssembled()-1][1]));
				}catch(Exception e){
					e.printStackTrace();
					
				}
			}
			

			
			//检查输入项是否合法
			if(insuSaleOrderService.checkSaleOrderBean(insuPlanBean, saleOrderBean)){
				insuSaleOrderService.submit(messageHandler,btDialog,saleOrderBean,inputValueMap,insuranceBean);
			}else{
				if(btDialog!=null&&btDialog.isShowing()){
					btDialog.dismiss();
				}
			}
				 
		}
	}
	
	/**
	 * 处理提交消息
	 */
	Handler messageHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				updateView();
				break;
			}
		}
	};
	
	
	
	
	/**
	 * 销售成功以后跟新控件的值
	 */
	private void updateView() {
		// TODO Auto-generated method stub
		if(dzh!=null){
			String newBillNo = insuSaleOrderService.addBillNo(dzh.getText().toString());
			dzh.setText(newBillNo);	
		}
	}

	private class TbrCardTextOnFocusChangeListener implements OnFocusChangeListener{
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			//失去焦点 判断为输入完成
			if(!hasFocus){
				//证件类型为身份证时
				if(tbrPaperType.getSelectedItem().toString().equals("身份证")){
					//检查身份输入时候合法
					if((tbrPaperNum != null)&&(!tbrPaperNum.getText().toString().equals(""))) { 
					String CardNo = bbrPaperNum.getText().toString();
					if(IdcardUtils.validateCard(CardNo)){
						IdcardUtils.getAgeByIdCard(CardNo);
						if(tbrsr != null){
							tbrsr.setText(IdcardUtils.getBirthByIdCard(CardNo));
						}
						String sexstr = IdcardUtils.getGenderByIdCard(CardNo);
						if(tbrsex != null){
							for (int i = 0; i < tbrsex.getCount(); i++) {
								if(tbrsex.getAdapter().getItem(i).toString().equals(sexstr)){
									tbrsex.setSelection(i);
									break;
								}
							}
						}
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_warning));
						builder.setMessage(StringUtils.getStringFromValue(R.string.apsai_sale_order_card_error));
						builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),null);
						builder.create().show();
						
					}
					}
				}
				
			}
		}
	}
	
	private class DbrCardTextOnFocusChangeListener implements OnFocusChangeListener{
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			//失去焦点 判断为输入完成
			if(!hasFocus){
				//证件类型为身份证时
				if(bbrPaperType.getSelectedItem().toString().equals("身份证")){
					//检查身份输入时候合法
					if((bbrPaperNum != null)&&(!bbrPaperNum.getText().toString().equals(""))) { 
					String CardNo = bbrPaperNum.getText().toString();
					if(IdcardUtils.validateCard(CardNo)){
						IdcardUtils.getAgeByIdCard(CardNo);
						if(bbrsr != null){
							bbrsr.setText(IdcardUtils.getBirthByIdCard(CardNo));
						}
						String sexstr = IdcardUtils.getGenderByIdCard(CardNo);
						if(bbrsex != null){
							for (int i = 0; i < bbrsex.getCount(); i++) {
								if(bbrsex.getAdapter().getItem(i).toString().equals(sexstr)){
									bbrsex.setSelection(i);
									break;
								}
							}
						}
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_warning));
						builder.setMessage(StringUtils.getStringFromValue(R.string.apsai_sale_order_card_error));
						builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),null);
						builder.create().show();
					}
					}
				}
				
			}
		}
		
	}
	
	public Date getinsuBeginDate(){
		Date result = new Date();
		if(insuPlanBean.getInsuPlanAdditionalList().size()>0){
			InsuPlanAdditional insuPlanAdditional =	insuPlanBean.getInsuPlanAdditionalList().get(0);
			int begday = 0;
			if(insuPlanAdditional.begday.length()>0){
				begday = Integer.valueOf(insuPlanAdditional.begday);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.add(calendar.DATE,begday);
			result = calendar.getTime();
		}
		return result;
	}
	/**
	 * 表单被改动事件监听器
	 */
	private class TextViewChangeWatcher implements TextWatcher{
		TextView view = null;
		int attributeId = 0;
		String beforeStr = null;
		public TextViewChangeWatcher(TextView view,int attributeId){
			this.view = view;
			this.attributeId = attributeId;
		}
        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                int arg3) {
        	beforeStr = s.toString();
        	//curentPage.setText("");
        }
        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
        	String newStr = s.toString();
        	int relateViewId = Integer.parseInt(String.valueOf(attributeId+InsuViewByModeFile.CONTENT_INIT_ID)+1); 
        	
        	if(newStr!=null&&!newStr.equals(beforeStr)&&viewIdInsuPlanContentMap.containsKey(String.valueOf(relateViewId))){
        		//判断是否为整形数据
        		try{
        			((TextView)findViewById(relateViewId)).setText(newStr);
        		}catch(Exception e){
        			
        		}
        		
        	}
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
	}
}
