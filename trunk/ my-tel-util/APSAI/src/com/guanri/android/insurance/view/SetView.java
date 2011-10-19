package com.guanri.android.insurance.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanContent;
import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanMode;
import com.guanri.android.insurance.bean.SaleOrderBean;
import com.guanri.android.insurance.common.SharedPreferencesUtils;
import com.guanri.android.insurance.service.InsuSaleOrderService;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.utils.IdcardUtils;
import com.guanri.android.lib.utils.StringUtils;

public class SetView {
	private Map<Integer, String> printmap;
	private static final int PLYH = 0;
	private static final int INSURED = 1;
	private static InsuViewPlanBean insuViewPlanBean;
	private static InsuPlanBean insuPlanBean ;

	public static final Integer BTNOK = 10000001;
	public static final Integer BTNCANCEL = 10000002;
	
	private Context context = null;
	// 提交到服务时使用
	private Map<String, String> AttributeValues;
	// 打印的时候使用
	private Map<Integer, String> ViewIdValues;
	// 保存255多对应控件的ID和控件类型
	private Map<Integer,String> OtherView;
	
	private String editname = null;
	private String editcode = null;
	private int i;
	private List<InsuPlanContent> insuPlanContentList;
	
	private LinearLayout mLayout;
	
	private LinearLayout sLayout;
	// TextView 布局文件属性
	private LinearLayout.LayoutParams txtParams;
	// EditText 布局文件属性
	private LinearLayout.LayoutParams edtParams;

	private InsuSaleOrderService insuSaleOrderService = null;

	private SaleOrderBean saleOrderBean;
	
	// 定义控件
	// 1. 单证号
	private EditText edt_No = null;
	// 2. 保险起期(格式固定为YYYY-MM-DD)
	private Button btn_beginDate = null;
	// 3. 保险起期时间(格式固定为HH[MM[SS]])
	private Button btn_beginTime = null;
	// 4. 保险止期(格式固定为YYYYMMDD)
	private Button btn_endDate = null;
	// 5. 保险止期时间(格式固定为HH[MM[SS]])
	private Button btn_endTime = null;
	// 6. 投保人
	private EditText edt_policyholderName = null;
	// 7. 投保人性别
	private Spinner spn_policyholderSex = null;
	// 8. 投保人生日
	private Button btn_policyholderBirthday = null;
	// 9. 投保人证件类型
	private Spinner spn_PHcredentialsType = null;
	// 10. 投保人证件号码
	private EditText edt_PHcredentialsNo = null;
	// 11. 被保人
	private EditText edt_insuredName = null;
	// 12. 被保人性别
	private Spinner spn_insuredSex = null;
	// 13. 被保人生日
	private Button btn_insuredBirthday = null;
	// 14. 被保人证件类型
	private Spinner spn_IScredentialsType = null;
	// 15. 被保人证件号码
	private EditText edt_IScredentialsNo = null;
	// 16. 投保人和被保人关系
	private EditText edt_nsuredrelation = null;
	// 17. 受益人
	private EditText edt_beneficiaryName = null;
	// 18. 保险保费（单位为元，可以输入两位小数）
	private EditText edt_insurermoney = null;
	// 19. 保险金额（单位为元）
	private EditText edt_insunredamount = null;
	// 20. 学校
	private EditText edt_school = null;
	// 21. 班级
	private EditText edt_schoolclass = null;
	// 22. 航班号/客运班次
	private EditText edt_trainnumber = null;
	// 23. 客票/门票号码，卡号等
	private EditText edt_trainticket = null;
	// 24. 保险期间:1-按年;2-按日;3-按天(暂时未使用)
	private EditText edt_InsurancePeriod = null;
	// 25. 保险期间值(暂时未使用)
	private EditText edt_Insurancevalues = null;
	// 26. 投保人职业序号(暂时未使用)
	private EditText edt_policyholderwrk = null;
	// 27. 被保人职业序号(暂时未使用)
	private EditText edt_insuredwork = null;
	
	// 49. 组合
	private Spinner spn_assembled = null;
	
	// 50. 联系电话
	private EditText edt_phone = null;
	// 51. 保险费率（单位为百分之几）
	private EditText edt_insurancerate = null;
	// 52. 中标合同价（单位为元）
	private EditText edt_contractprice = null;
	// 53. 建筑面积（单位为mm）
	private EditText edt_area = null;
	// 54. 付款方式：包含有现金、转账、刷卡
	private Spinner spn_paymenttype = null;
	// 55. 开户银行
	private EditText edt_bankName = null;
	// 56. 银行账号
	private EditText edt_bankNo = null;
	// 57. 银行电话
	private EditText edt_bankPhone = null;
	// 58. 贷款合同编号
	private EditText edt_contractNo = null;
	// 59. 贷款金额（单位为元）
	private EditText edt_contractMoney = null;
	// 60. 汽车类型(暂时未使用)
	private EditText edt_carType = null;
	// 61. 核定座位数(暂时未使用)
	private EditText edt_carSeat = null;
	// 62. 被保人有社保标志
	private CheckBox chb_socialsecurityState = null;
	// 63. 贷款起期
	private Button btn_loansBegin = null;
	// 64. 贷款止期
	private Button btn_loansEnd = null;

	public SetView(InsuViewPlanBean myInsuViewPlanBean,InsuPlanBean myinsuPlanBean, Context mycontext) {
		this.insuViewPlanBean = myInsuViewPlanBean;
		this.context = mycontext;
		this.insuPlanBean = myinsuPlanBean;
		
		insuSaleOrderService = new InsuSaleOrderService(context);
		
		AttributeValues = new HashMap<String, String>();
		// 打印的时候使用
		ViewIdValues = new HashMap<Integer, String>();
		OtherView = new HashMap<Integer,String>();
		
		// 各控件的位置布局
		txtParams = new LinearLayout.LayoutParams(100,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		txtParams.setMargins(10, 0, 0, 0);
		txtParams.weight = 3;
		
		edtParams = new LinearLayout.LayoutParams(300,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		edtParams.weight = 1;
		edtParams.setMargins(10, 0, 20, 0);

	}

	/**
	 * 根据读取到的协议文件信息解析得出布局文件
	 * 
	 * @return
	 */
	public View ObjectSetView() {
		// 设置滚动条
		ScrollView scrollView = new ScrollView(context);
		scrollView.setLayoutParams(new ScrollView.LayoutParams(
				ScrollView.LayoutParams.FILL_PARENT,
				ScrollView.LayoutParams.FILL_PARENT));
		scrollView.setScrollBarStyle(LinearLayout.VERTICAL);
		// 设置主布局
		LinearLayout mLayout = new LinearLayout(context);
		mLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		mLayout.setOrientation(LinearLayout.VERTICAL);

		// 获得协议编号，协议名称
		List<InsuPlanMode> insuPlanModeList = insuViewPlanBean
				.getInsuPlanModeList();
		editname = insuPlanModeList.get(0).cardname;
		editcode = insuPlanModeList.get(0).editcode;
		
		
		// 获取协议各控件
		insuPlanContentList = insuViewPlanBean.getInsuPlanContentList();
		for (i = 0; i < insuPlanContentList.size(); i++) {
			// 空录入项 跳过
			if (insuPlanContentList.get(i).Attribute.equals("0"))
				continue;

			// 子线性布局文件
			sLayout = new LinearLayout(context);
			sLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			sLayout.setOrientation(LinearLayout.HORIZONTAL);
			
			

			LinearLayout hitLayout = new LinearLayout(context);
			hitLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
			hitLayout.setFocusable(true);
			hitLayout.setFocusableInTouchMode(true);
			sLayout.addView(hitLayout);

			if (insuPlanContentList.get(i).TSChar != null) {
				TextView tv = new TextView(context);
				tv.setText(insuPlanContentList.get(i).TSChar);
				tv.setLayoutParams(txtParams);
				tv.setTextSize(18);
				sLayout.addView(tv);
			}

			switch (Integer.valueOf(insuPlanContentList.get(i).Attribute)) {
			case 1: {
				edt_No = new EditText(context);
				edt_No.setId(i);
				edt_No.setLayoutParams(edtParams);
				edt_No.requestFocus();
				edt_No.setText("10154014211210073001");
				sLayout.addView(edt_No);
			}
				break;
			case 2: {
				btn_beginDate = new Button(context);
				btn_beginDate.setId(i);
				btn_beginDate.setLayoutParams(edtParams);
				//android:gravity
				btn_beginDate.setText(getDateStr());
				btn_beginDate.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_beginDate.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_beginDate.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_beginDate.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() +"-"+ monthstr +"-"+ daystr;
				    				btn_beginDate.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_beginDate);
			}
				break;
			case 3: {
				btn_beginTime = new Button(context);
				btn_beginTime.setId(i);
				btn_beginTime.setLayoutParams(edtParams);
				btn_beginTime.setText(getTimeStr());
				btn_beginTime.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int currentHour = Integer.valueOf(btn_beginTime.getText().toString().substring(0, 2));
							int currentMinute = Integer.valueOf(btn_beginTime.getText().toString().substring(3, 5));
							
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着时间");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_time_dialog, null);
							final TimePicker tmp_begintime;
							tmp_begintime = (TimePicker)view.findViewById(R.id.btp_time);
							tmp_begintime.setIs24HourView(true);
							tmp_begintime.setCurrentHour(currentHour);
							tmp_begintime.setCurrentMinute(currentMinute); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				String timestr;
				    				if(tmp_begintime.getCurrentMinute()>9){
				    					timestr = tmp_begintime.getCurrentHour() + ":" + tmp_begintime.getCurrentMinute();
				    				}
				    				else{
				    					timestr = tmp_begintime.getCurrentHour() + ":0" + tmp_begintime.getCurrentMinute();
				    				}
				    				
				    				btn_beginTime.setText(timestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_beginTime);
			}
				break;
			case 4: {
				btn_endDate = new Button(context);
				btn_endDate.setId(i);
				btn_endDate.setLayoutParams(edtParams);
				btn_endDate.setText(getDateStr());
				btn_endDate.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_endDate.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_endDate.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_endDate.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() + "-" + monthstr + "-" + daystr;
				    				btn_endDate.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_endDate);	
			}
				break;
			case 5: {
				final Button btn_endTime = new Button(context);
				btn_endTime.setId(i);
				btn_endTime.setLayoutParams(edtParams);
				btn_endTime.setText(getTimeStr());
				btn_endTime.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int currentHour = Integer.valueOf(btn_endTime.getText().toString().substring(0, 2));
							int currentMinute = Integer.valueOf(btn_endTime.getText().toString().substring(3, 5));
							
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着时间");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_time_dialog, null);
							final TimePicker tmp_begintime;
							tmp_begintime = (TimePicker)view.findViewById(R.id.btp_time);
							tmp_begintime.setIs24HourView(true);
							tmp_begintime.setCurrentHour(currentHour);
							tmp_begintime.setCurrentMinute(currentMinute); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				String timestr;
				    				if(tmp_begintime.getCurrentMinute()>9){
				    					timestr = tmp_begintime.getCurrentHour() + ":" + tmp_begintime.getCurrentMinute();
				    				}
				    				else{
				    					timestr = tmp_begintime.getCurrentHour() + ":0" + tmp_begintime.getCurrentMinute();
				    				}
				    				
				    				btn_endTime.setText(timestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_endTime);
				
			}
				break;
			case 6: {
				edt_policyholderName = new EditText(context);
				edt_policyholderName.setLayoutParams(edtParams);
				edt_policyholderName.setId(i);
				edt_policyholderName
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_policyholderName);
			}
				break;
			case 7: {
				spn_policyholderSex = new Spinner(context);
				spn_policyholderSex.setLayoutParams(edtParams);
				String HelpChar = insuPlanContentList.get(i).HelpChar;
				spn_policyholderSex.setAdapter(GetAdapter(HelpChar));
				spn_policyholderSex.setId(i);
				if (insuPlanContentList.get(i).FillType != null) {
					int position = Integer.valueOf(
							insuPlanContentList.get(i).FillType).intValue();
					spn_policyholderSex.setSelection(position);
				}
				sLayout.addView(spn_policyholderSex);
			}
				break;
			case 8: {
				btn_policyholderBirthday = new Button(context);
				btn_policyholderBirthday.setId(i);
				btn_policyholderBirthday.setLayoutParams(edtParams);
				btn_policyholderBirthday.setText(getDateStr());
				btn_policyholderBirthday.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_policyholderBirthday.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_policyholderBirthday.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_policyholderBirthday.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							Calendar c =Calendar.getInstance();
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String yearstr,daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() +"-"+ monthstr +"-"+ daystr;
				    				btn_policyholderBirthday.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_policyholderBirthday);	
			}
				break;
			case 9: {
				spn_PHcredentialsType = new Spinner(context);
				spn_PHcredentialsType.setLayoutParams(edtParams);
				String HelpChar = insuPlanContentList.get(i).HelpChar;
				spn_PHcredentialsType.setAdapter(GetAdapter(HelpChar));
				spn_PHcredentialsType.setId(i);
				if (insuPlanContentList.get(i).FillType != null) {
					int position = Integer.valueOf(
							insuPlanContentList.get(i).FillType).intValue();
					spn_PHcredentialsType.setSelection(position);
				}
				sLayout.addView(spn_PHcredentialsType);
			}
				break;
			case 10: {
				edt_PHcredentialsNo = new EditText(context);
				edt_PHcredentialsNo.setLayoutParams(edtParams);
				edt_PHcredentialsNo.setId(i);
				edt_PHcredentialsNo
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_PHcredentialsNo);
			}
				break;
			case 11: {
				edt_insuredName = new EditText(context);
				edt_insuredName.setLayoutParams(edtParams);
				edt_insuredName.setId(i);
				edt_insuredName.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_insuredName);
			}
				break;
			case 12: {
				spn_insuredSex = new Spinner(context);
				spn_insuredSex.setLayoutParams(edtParams);
				String HelpChar = insuPlanContentList.get(i).HelpChar;
				spn_insuredSex.setAdapter(GetAdapter(HelpChar));
				spn_insuredSex.setId(i);
				if (insuPlanContentList.get(i).FillType.equals("")) {
					int position = Integer.valueOf(
							insuPlanContentList.get(i).FillType).intValue();
					spn_insuredSex.setSelection(position);
				}
				sLayout.addView(spn_insuredSex);
			}
				break;
			case 13: {
				btn_insuredBirthday = new Button(context);
				btn_insuredBirthday.setId(i);
				btn_insuredBirthday.setLayoutParams(edtParams);
				btn_insuredBirthday.setText(getDateStr());
				btn_insuredBirthday.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_insuredBirthday.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_insuredBirthday.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_insuredBirthday.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							Calendar c =Calendar.getInstance();
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String yearstr,daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() +"-"+ monthstr +"-"+ daystr;
				    				btn_insuredBirthday.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_insuredBirthday);	
			}
				break;
			case 14: {
				spn_IScredentialsType = new Spinner(context);
				spn_IScredentialsType.setLayoutParams(edtParams);
				String HelpChar = insuPlanContentList.get(i).HelpChar;
				spn_IScredentialsType.setAdapter(GetAdapter(HelpChar));
				spn_IScredentialsType.setId(i);
				if (insuPlanContentList.get(i).FillType.equals("")) {
					int position = Integer.valueOf(
							insuPlanContentList.get(i).FillType).intValue();
					spn_IScredentialsType.setSelection(position);
				}
				sLayout.addView(spn_IScredentialsType);
			}
				break;
			case 15: {
				edt_IScredentialsNo = new EditText(context);
				edt_IScredentialsNo.setLayoutParams(edtParams);
				edt_IScredentialsNo.setId(i);
				edt_IScredentialsNo
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_IScredentialsNo);
			}
				break;
			case 16: {
				edt_nsuredrelation = new EditText(context);
				edt_nsuredrelation.setLayoutParams(edtParams);
				edt_nsuredrelation.setId(i);
				edt_nsuredrelation
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_nsuredrelation);
			}
				break;
			// 受益人
			case 17: {
				edt_beneficiaryName = new EditText(context);
				edt_beneficiaryName.setLayoutParams(edtParams);
				edt_beneficiaryName.setId(i);
				edt_beneficiaryName
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_beneficiaryName);
			}
				break;
			// 18. 保险保费（单位为元，可以输入两位小数）
			case 18: {
				edt_insurermoney = new EditText(context);
				edt_insurermoney.setLayoutParams(edtParams);
				edt_insurermoney.setId(i);
				edt_insurermoney.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_insurermoney);
			}
				break;
			// 19. 保险金额（单位为元）
			case 19: {
				edt_insunredamount = new EditText(context);
				edt_insunredamount.setLayoutParams(edtParams);
				edt_insunredamount.setId(i);
				edt_insunredamount
						.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_insunredamount);

			}
				break;
			// 20. 学校
			case 20: {
				edt_school = new EditText(context);
				edt_school.setLayoutParams(edtParams);
				edt_school.setId(i);
				edt_school.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_school);
			}
				break;
			// 21. 班级
			case 21: {
				edt_schoolclass = new EditText(context);
				edt_schoolclass.setLayoutParams(edtParams);
				edt_schoolclass.setId(i);
				edt_schoolclass.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_schoolclass);
			}
				break;
			// 22. 航班号/客运班次
			case 22: {
				edt_trainnumber = new EditText(context);
				edt_trainnumber.setLayoutParams(edtParams);
				edt_trainnumber.setId(i);
				edt_trainnumber.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_trainnumber);
			}
				break;
			// 23. 客票/门票号码，卡号等
			case 23: {
				edt_trainticket = new EditText(context);
				edt_trainticket.setLayoutParams(edtParams);
				edt_trainticket.setId(i);
				edt_trainticket.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_trainticket);
			}
				break;
			// 49. 组合	
			case 49:{
				spn_assembled = new Spinner(context);
				spn_assembled.setLayoutParams(edtParams);
				// 通过 业务方案文件构造 列表 ***=***; ***=***;
				String HelpChar = getAssembled(insuPlanBean);
				spn_assembled.setAdapter(GetAdapter(HelpChar));
				spn_assembled.setId(i);
				
				sLayout.addView(spn_assembled);
				
				
			}
				break;
			// 50. 联系电话
			case 50: {
				edt_phone = new EditText(context);
				edt_phone.setLayoutParams(edtParams);
				edt_phone.setId(i);
				edt_phone.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_phone);
			}
				break;
			// 51. 保险费率（单位为百分之几）
			case 51: {
				edt_insurancerate = new EditText(context);
				edt_insurancerate.setLayoutParams(edtParams);
				edt_insurancerate.setId(i);
				edt_insurancerate.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_insurancerate);
			}
				break;
			// 52. 中标合同价（单位为元）
			case 52: {
				edt_contractprice = new EditText(context);
				edt_contractprice.setLayoutParams(edtParams);
				edt_contractprice.setId(i);
				edt_contractprice.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_contractprice);
			}
				break;
			// 53. 建筑面积（单位为mm）
			case 53: {
				edt_area = new EditText(context);
				edt_area.setLayoutParams(edtParams);
				edt_area.setId(i);
				edt_area.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_area);
			}
				break;
			// 54. 付款方式：包含有现金、转账、刷卡
			case 54: {

			}
				break;
			// 55. 开户银行
			case 55: {
				edt_bankName = new EditText(context);
				edt_bankName.setLayoutParams(edtParams);
				edt_bankName.setId(i);
				edt_bankName.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_bankName);
			}
				break;
			// 56. 银行账号
			case 56: {
				edt_bankNo = new EditText(context);
				edt_bankNo.setLayoutParams(edtParams);
				edt_bankNo.setId(i);
				edt_bankNo.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_bankNo);
			}
				break;
			// 57. 银行电话
			case 57: {
				edt_bankPhone = new EditText(context);
				edt_bankPhone.setLayoutParams(edtParams);
				edt_bankPhone.setId(i);
				edt_bankPhone.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_bankPhone);
			}
				break;
			// 58. 贷款合同编号
			case 58: {
				edt_contractNo = new EditText(context);
				edt_contractNo.setLayoutParams(edtParams);
				edt_contractNo.setId(i);
				edt_contractNo.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_contractNo);
			}
				break;
			// 59. 贷款金额（单位为元）
			case 59: {
				edt_contractMoney = new EditText(context);
				edt_contractMoney.setLayoutParams(edtParams);
				edt_contractMoney.setId(i);
				edt_contractMoney.setText(insuPlanContentList.get(i).FirstFill);
				sLayout.addView(edt_contractMoney);
			}
				break;
			// 62. 被保人有社保标志
			case 62: {
				chb_socialsecurityState = new CheckBox(context);
				chb_socialsecurityState.setLayoutParams(edtParams);
				chb_socialsecurityState.setId(i);
				sLayout.addView(chb_socialsecurityState);
			}
				break;
			// 63. 贷款起期
			case 63: {
				btn_loansBegin = new Button(context);
				btn_loansBegin.setId(i);
				btn_loansBegin.setLayoutParams(edtParams);
				btn_loansBegin.setText(getDateStr());
				btn_loansBegin.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_loansBegin.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_loansBegin.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_loansBegin.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							Calendar c =Calendar.getInstance();
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String yearstr,daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() +"-"+ monthstr +"-"+ daystr;
				    				btn_loansBegin.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_loansBegin);	
				
//				dtp_loansBegin = new DatePicker(context);
//				Calendar c = null;
//				c = Calendar.getInstance();
//				dtp_loansBegin.setId(i);
//				dtp_loansBegin.setLayoutParams(dateParams);
//				dtp_loansBegin.init(c.get(Calendar.YEAR),
//						c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),
//						null);
//				sLayout.addView(dtp_loansBegin);
			}
				break;
			// 64. 贷款止期
			case 64: {
				btn_loansEnd = new Button(context);
				btn_loansEnd.setId(i);
				btn_loansEnd.setLayoutParams(edtParams);
				btn_loansEnd.setText(getDateStr());
				btn_loansEnd.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							int year = Integer.valueOf(btn_loansEnd.getText().toString().substring(0, 4));
							int month = Integer.valueOf(btn_loansEnd.getText().toString().substring(5, 7)) -1;
							int day = Integer.valueOf(btn_loansEnd.getText().toString().substring(8, 10));
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							builder.setTitle("选着日期");
							LayoutInflater inflater = LayoutInflater.from(context);
							View view = inflater.inflate(R.layout.sale_date_dialog, null);
							final DatePicker dtp_date;
							dtp_date = (DatePicker)view.findViewById(R.id.btp_date);
							Calendar c =Calendar.getInstance();
							dtp_date.init(year,month,day,null); 
							builder.setNeutralButton("确认", new DialogInterface.OnClickListener(){
				    			public void onClick(DialogInterface dialog, int which){
				    				//
				    				int monthint = dtp_date.getMonth() +1 ;
				    				String yearstr,daystr, monthstr; 
				    				if(monthint>9){
				    					monthstr = "" + monthint;
				    				}else{
				    					monthstr = "0" + monthint;
				    				}
				    				if(dtp_date.getDayOfMonth()>9){
				    					daystr = dtp_date.getDayOfMonth() +"";
				    				}
				    				else{
				    					daystr = "0" + dtp_date.getDayOfMonth();
				    				}
				    				String datestr = dtp_date.getYear() +"-"+ monthstr +"-"+ daystr;
				    				btn_loansEnd.setText(datestr);    				
				    			}
				    		});
							builder.setNegativeButton("取消", null);
							builder.setView(view);
							builder.create().show();
						}
					
				});
				sLayout.addView(btn_loansEnd);	
				
//				dtp_loansEnd = new DatePicker(context);
//				Calendar c = null;
//				c = Calendar.getInstance();
//				dtp_loansEnd.setId(i);
//				dtp_loansEnd.setLayoutParams(dateParams);
//				dtp_loansEnd.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
//						c.get(Calendar.DAY_OF_MONTH), null);
//				sLayout.addView(dtp_loansEnd);
			}
				break;
			case 255: {
				// 是否问选择项
				if (insuPlanContentList.get(i).InputEnAble.equals("128")) {
					// 设置可选属性
					Spinner spinner = new Spinner(context);
					spinner.setLayoutParams(edtParams);
					String HelpChar = insuPlanContentList.get(i).HelpChar;
					spinner.setAdapter(GetAdapter(HelpChar));
					spinner.setId(i);
					if (insuPlanContentList.get(i).FillType != null) {
						int position = Integer.valueOf(
								insuPlanContentList.get(i).FillType).intValue();
						spinner.setSelection(position);
					}
					OtherView.put(i, "Spinner");
					sLayout.addView(spinner);
				}

				// 文本框
				else {
					EditText editText = new EditText(context);
					editText.setLayoutParams(edtParams);
					editText.setId(i);
					editText.setText(insuPlanContentList.get(i).MaxInputMust);
					// /
					if (!insuPlanContentList.get(i).SecretFlag.equals("0")) {
						editText.setPressed(true);
					}
					// 
					if (insuPlanContentList.get(i).FirstFill != "") {
						// editText.setText(insuPlanContentList.get(i).FirstFill);
					}
					OtherView.put(i, "EditText");
					sLayout.addView(editText);
				}
			}
				break;
			default:
				break;

			}

			mLayout.addView(sLayout);
		}

		SetButtonView(mLayout);

		//
		// Button btn_canel = new Button(context);
		//
		// btn_canel.setText("取消");
		// btn_canel.setLayoutParams(btnParams);
		// btn_canel.setId(100002);
		// scrollView.addView(btn_canel);

		scrollView.addView(mLayout);
		return scrollView;

	}

	/**
	 * 根据字符串获得下拉列表框的内容
	 * 
	 * @param params
	 * @return
	 */
	private ArrayAdapter<String> GetAdapter(String params) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String tempstr = params;
		if (tempstr != "" && tempstr.length() > 0) {
			String[][] param = StringUtils.splitString(params, ";", "=");
			for (int i = 0; i < param.length; i++) {

				adapter.add(param[i][1]);
			}
		}
		return adapter;
	}

	private String getAssembled(InsuPlanBean insuPlanBean){
		String result = "";
		String feeamtlist = insuPlanBean.getInsuPlanAttrList().get(0).feeamtlist;
		String timelist = insuPlanBean.getInsuPlanAttrList().get(0).timelist;
		int timetype = new Integer(insuPlanBean.getInsuPlanAttrList().get(0).timetype);
		String timetypestr = "";
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
		int i = timelist.indexOf(",");
		int k = feeamtlist.indexOf(",");
		int j = 1;
		while (i>0) {
			result =  result +j +"="+ timelist.substring(0, i) + timetypestr + "-" + 
				feeamtlist.substring(0, k) +"元;" ;
			timelist = timelist.substring(i+1);
			feeamtlist = feeamtlist.substring(k+1);
			j++;
			i = timelist.indexOf(",");
			k = feeamtlist.indexOf(",");
		}
		result = result + j +"="+ timelist + timetypestr + "----" + feeamtlist +"元";
		return result;
	}
	/**
	 * 添加按钮
	 * 
	 * @param view
	 */
	private void SetButtonView(LinearLayout view) {
		LinearLayout btnLayout = new LinearLayout(context);
		btnLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		btnLayout.setOrientation(LinearLayout.HORIZONTAL);
		btnLayout.setGravity(Gravity.CENTER);
		
		
		
		Button btn_ok = new Button(context);
		LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
				200, LinearLayout.LayoutParams.WRAP_CONTENT);
		btnParams.setMargins(10, 0, 0, 0);
		
		btn_ok.setText("提交");
		btn_ok.setId(BTNOK);
		btn_ok.setLayoutParams(btnParams);
		btn_ok.setOnClickListener(new OkOnClickListener());
		;
		btnLayout.addView(btn_ok);

		Button btn_cancel = new Button(context);
		btn_cancel.setText("清空");
		btn_cancel.setId(BTNCANCEL);
		btn_cancel.setLayoutParams(btnParams);
		btnLayout.addView(btn_cancel);

		view.addView(btnLayout);
	}

	class OkOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str = "";
			GetViewValues();
			//等待提示框
			final ProgressDialog btDialog = new ProgressDialog(context);
			btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_common_advise)); // title     
			btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_common_server_loading));//进度是否是不确定的，这只和创建进度条有关
			
//			insuSaleOrderService.submit(btDialog, saleOrderBean);
//			for(Map.Entry<String, String> entry : AttributeValues.entrySet())
//			{
//				 str =  str + "\n" +entry.getKey()+": "+entry.getValue();
//			}
//
//			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}
	}

	private SaleOrderBean GetViewValues() {
		String userno = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserId();
		Date date = new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String Operatetime = df.format(date);
		String otherStr = "";
		saleOrderBean = new SaleOrderBean(); 
		
		saleOrderBean.setOperator_id(userno);
		saleOrderBean.setOperate_time(Operatetime);
		saleOrderBean.setCardCode(insuPlanBean.getInsuPlanAttrList().get(0).cardcode);
		saleOrderBean.setPlanNo(insuPlanBean.getInsuPlanAttrList().get(0).planno);
		
		if (edt_No != null) {
			EditTexttoMap(edt_No, "1");
			saleOrderBean.setBillNo(edt_No.getText().toString());
		}
		// (格式固定为YYYYMMDD)
		if (btn_beginDate != null) {
			DateButtontoMap(btn_beginDate, "2");
			saleOrderBean.setInsu_begin_date(btn_beginDate.getText().toString());
		}
		// 3. 保险起期时间(格式固定为HH[MM[SS]])
		if (btn_beginTime != null) {
			TimeButtontoMap(btn_beginTime, "3");
			saleOrderBean.setInsu_begin_time(btn_beginTime.getText().toString());
		}
		// 4. 保险止期(格式固定为YYYYMMDD)
		if (btn_endDate != null) {
			DateButtontoMap(btn_endDate, "4");
			saleOrderBean.setInsu_end_date(btn_endDate.getText().toString());
			
		}
		// 5. 保险止期时间(格式固定为HH[MM[SS]])
		if (btn_endTime != null) {
			TimeButtontoMap(btn_endTime, "5");
			saleOrderBean.setInsu_end_time(btn_endTime.getText().toString());
		}
		// 6
		if (edt_policyholderName != null) {
			EditTexttoMap(edt_policyholderName, "6");
			saleOrderBean.setPlyh_name(edt_policyholderName.getText().toString());
		}
		// 7
		if (spn_policyholderSex != null) {
			SpinnertoMap(spn_policyholderSex, "7");
			saleOrderBean.setPlyh_sex(new Integer(findIdByValues(spn_policyholderSex.getId(),spn_policyholderSex.getSelectedItem().toString())));
		}
		// 8. 投保人生日
		if (btn_policyholderBirthday != null) {
			DateButtontoMap(btn_policyholderBirthday, "8");
			saleOrderBean.setPlyh_Brithday(btn_policyholderBirthday.getText().toString());
		}
		//9
		if (spn_PHcredentialsType != null) {
			SpinnertoMap(spn_PHcredentialsType, "9");
			saleOrderBean.setPlyh_Card_type(new Integer(findIdByValues(spn_PHcredentialsType.getId(),spn_PHcredentialsType.getSelectedItem().toString())));
		}
		// 10
		if (edt_PHcredentialsNo != null) {
			EditTexttoMap(edt_PHcredentialsNo, "10");
			saleOrderBean.setPlyh_Card_no(edt_PHcredentialsNo.getText().toString());
		}
		// 11. 被保人
		if (edt_insuredName != null) {
			EditTexttoMap(edt_insuredName, "11");
			saleOrderBean.setInsured_name(edt_insuredName.getText().toString());
		}
		// 12. 被保人性别
		if (spn_insuredSex != null) {
			SpinnertoMap(spn_insuredSex, "12");
			saleOrderBean.setInsured_Sex(Integer.parseInt(findIdByValues(spn_insuredSex.getId(),spn_insuredSex.getSelectedItem().toString())));
		}
		// 13. 被保人生日
		if (btn_insuredBirthday != null) {
			DateButtontoMap(btn_insuredBirthday,"13");
			saleOrderBean.setInsured_brithday(btn_insuredBirthday.getText().toString());
		}
		// 14. 被保人证件类型
		if (spn_IScredentialsType != null) {
			SpinnertoMap(spn_IScredentialsType, "14");
			String str = findIdByValues(spn_IScredentialsType.getId(),spn_IScredentialsType.getSelectedItem().toString());
			saleOrderBean.setInsured_card_type(Integer.parseInt(str));
		}
		// 15. 被保人证件号码
		if (edt_IScredentialsNo != null) {
			EditTexttoMap(edt_IScredentialsNo, "15");
			saleOrderBean.setInsured_card_No(edt_IScredentialsNo.getText().toString());
		}
		// 16. 投保人和被保人关系
		if (edt_nsuredrelation != null) {
			EditTexttoMap(edt_nsuredrelation,"16");
			saleOrderBean.setInsured_relation(Integer.parseInt(findIdByValues(edt_nsuredrelation.getId(),edt_nsuredrelation.getText().toString())));
		}
		// 17. 受益人
		if (edt_beneficiaryName != null) {
			EditTexttoMap(edt_beneficiaryName, "17");
			saleOrderBean.setBeneficlary_name(edt_beneficiaryName.getText().toString());
		}
		// 18. 保险保费（单位为元，可以输入两位小数）
		if (edt_insurermoney != null) {
			EditTexttoMap(edt_insurermoney, "18");
			saleOrderBean.setInsured_money((Integer.parseInt(edt_insurermoney.getText().toString()))*100);
		}
		// 19. 保险金额（单位为元）
		if (edt_insunredamount != null) {
			EditTexttoMap(edt_insunredamount, "19");
			saleOrderBean.setInsured_amount((Integer.parseInt(edt_insunredamount.getText().toString()))*100);
		}
		// 20. 学校
		if (edt_school != null) {
			EditTexttoMap(edt_school, "20");
			saleOrderBean.setSchool(edt_school.getText().toString());
		}
		// 21. 班级
		if (edt_schoolclass != null) {
			EditTexttoMap(edt_schoolclass, "21");
			saleOrderBean.setSchoolClass(edt_schoolclass.getText().toString());
		}
		// 22. 航班号/客运班次
		if (edt_trainnumber != null) {
			EditTexttoMap(edt_trainnumber, "22");
			saleOrderBean.setTrainnumber(edt_trainnumber.getText().toString());
		}
		// 23. 客票/门票号码，卡号等
		if (edt_trainticket != null) {
			EditTexttoMap(edt_trainticket, "23");
			saleOrderBean.setTrainticket(edt_trainticket.getText().toString());
		}
		// 24. 保险期间:1-按年){2-按日){3-按天(暂时未使用)
		if (edt_InsurancePeriod != null) {
			EditTexttoMap(edt_InsurancePeriod, "24");
			saleOrderBean.setInsu_period(edt_InsurancePeriod.getText().toString()); 
		}
		// 25. 保险期间值(暂时未使用)
		if (edt_Insurancevalues != null) {
			EditTexttoMap(edt_Insurancevalues, "25");
			saleOrderBean.setInsu_values(edt_Insurancevalues.getText().toString());
		}
		// 26. 投保人职业序号(暂时未使用)
		if (edt_policyholderwrk != null) {
			EditTexttoMap(edt_policyholderwrk, "26");
			saleOrderBean.setPlyh_WorkNo(edt_policyholderwrk.getText().toString());
		}
		// 27. 被保人职业序号(暂时未使用)
		if (edt_insuredwork != null) {
			EditTexttoMap(edt_insuredwork, "27");
			saleOrderBean.setInsured_WorkNo(edt_insuredwork.getText().toString());
		}
		// 49. 组合 需要特殊处理
		if(spn_assembled != null){
			SpinnertoMap(spn_assembled, "49");
			
			String str = getAssembled(insuPlanBean);
			
			Integer i = -1,j=0;
			String resultstr = "";
			if(str != ""){
			if ((i = str.indexOf(spn_assembled.getSelectedItem().toString())) != -1) {
				String tempstr = str.substring(0, i);
				if ((j = tempstr.lastIndexOf(";")) > -1) {
					resultstr = tempstr.substring(j + 1, tempstr.length());
					} else {
						resultstr = tempstr;
					}
					resultstr = resultstr.substring(0, resultstr.length() - 1);
				}
			}
			
			AttributeValues.put("49", resultstr);

			ViewIdValues.put(spn_assembled.getId(), resultstr);
			
			saleOrderBean.setInsuAssembled(Integer.parseInt(resultstr));
		}
		// 50. 联系电话
		if (edt_phone != null) {
			EditTexttoMap(edt_phone, "50");
			saleOrderBean.setPhone(edt_phone.getText().toString());
			otherStr = otherStr + "联系电话"+edt_phone.getText().toString() + ";";
		}
		
		// 51. 保险费率（单位为百分之几）
		if (edt_insurancerate != null) {
			EditTexttoMap(edt_insurancerate, "51");
			otherStr = otherStr + "保险费率"+edt_insurancerate.getText().toString() + ";";
		}
		// 52. 中标合同价（单位为元）
		if (edt_contractprice != null) {
			EditTexttoMap(edt_contractprice, "52");
			otherStr = otherStr + "中标合同价"+edt_contractprice.getText().toString() + ";";
		}
		// 53. 建筑面积（单位为mm）
		if (edt_area != null) {
			EditTexttoMap(edt_area, "53");
			otherStr = otherStr + "建筑面积"+edt_area.getText().toString() + ";";
		}
		// 54. 付款方式：包含有现金、转账、刷卡
		if (spn_paymenttype != null) {
			SpinnertoMap(spn_paymenttype, "54");
			otherStr = otherStr + "付款方式"+spn_paymenttype.getSelectedItem().toString() + ";";
		}
		// 55. 开户银行
		if (edt_bankName != null) {
			EditTexttoMap(edt_bankName, "55");
			otherStr = otherStr + "开户银行"+edt_bankName.getText().toString() + ";";
		}
		// 56. 银行账号
		if (edt_bankNo != null) {
			EditTexttoMap(edt_bankNo, "56");
			otherStr = otherStr + "银行账号"+edt_bankNo.getText().toString() + ";";
		}
		// 57. 银行电话
		if (edt_bankPhone != null) {
			EditTexttoMap(edt_bankPhone, "57");
			otherStr = otherStr + "银行电话"+edt_bankPhone.getText().toString() + ";";
		}
		// 58. 贷款合同编号
		if (edt_contractNo != null) {
			EditTexttoMap(edt_contractNo, "58");
			otherStr = otherStr + "贷款合同编号"+edt_contractNo.getText().toString() + ";";
		}
		// 59. 贷款金额（单位为元）
		if (edt_contractMoney != null) {
			EditTexttoMap(edt_contractMoney, "59");
			otherStr = otherStr + "贷款金额"+edt_contractMoney.getText().toString() + ";";
		}
		// 60. 汽车类型(暂时未使用)
		if (edt_carType != null) {
			EditTexttoMap(edt_carType, "60");
			otherStr = otherStr + "汽车类型"+edt_carType.getText().toString() + ";";
		}
		// 61. 核定座位数(暂时未使用)
		if (edt_carSeat != null) {
			EditTexttoMap(edt_carSeat,"61");
			otherStr = otherStr + "核定座位数"+edt_carSeat.getText().toString() + ";";
		}
		// 62. 被保人有社保标志
		if (chb_socialsecurityState != null) {
			CheckBoxtoMap(chb_socialsecurityState,"62");
			otherStr = otherStr + "被保人有社保标志"+chb_socialsecurityState.getSelectionStart() + ";";
		}
		// 63. 贷款起期
		if (btn_loansBegin != null) {
			DateButtontoMap(btn_loansBegin, "63");
			otherStr = otherStr + "贷款起期"+btn_loansBegin.getText().toString() + ";";
		}
		// 64.货款止起
		if (btn_loansEnd != null) {
			DateButtontoMap(btn_loansEnd, "64");
			otherStr = otherStr + "开户银行"+btn_loansEnd.getText().toString() + ";";
		}
		//取Attribute为 255 控件的值
		String tempstr="";
		for(Map.Entry<Integer, String> entry : OtherView.entrySet()){
			if(entry.getValue().equals("EditText")){
				EditText tempet;
				tempet = (EditText)((Activity)context).findViewById(entry.getKey().intValue());
				tempstr = tempstr + insuPlanContentList.get(entry.getKey().intValue()).TSChar + tempet.getText().toString() +";";
				
				ViewIdValues.put(entry.getKey(), tempet.getText().toString());
			}else if(entry.getValue().equals("Spinner")){
				Spinner tempSpn ;
				tempSpn = (Spinner)((Activity)context).findViewById(entry.getKey().intValue());
				String spn_str = findIdByValues(entry.getKey().intValue(),tempSpn.getSelectedItem().toString());
				tempstr = tempstr + insuPlanContentList.get(entry.getKey().intValue()).TSChar + spn_str +";";
				ViewIdValues.put(entry.getKey(), spn_str);
			}
					
		}
		//Toast.makeText(context, "otherstr:" + otherstr, Toast.LENGTH_LONG).show();
		AttributeValues.put("255", tempstr);
		otherStr = otherStr + tempstr;
		saleOrderBean.setRemark(otherStr);
		//设置批次号
		String BatchNo = SharedPreferencesUtils.getConfigString(
				SharedPreferencesUtils.COMFIG_INFO, SharedPreferencesUtils.CHECK_ID);
		
		saleOrderBean.setCheck_id(Integer.parseInt("1"));
		
		return saleOrderBean;
	}

	/**
	 * 通过选中的字符串查找 字符串对应的ID
	 * 在  1="男";2="女" 中查找 "男"---->1
	 * @param ID
	 * @param string
	 * @return
	 */
	private String findIdByValues(Integer Id, String string) {
		// TODO Auto-generated method stub
		Integer i = -1,j=0;
		String str = insuPlanContentList.get(Id).HelpChar;
		String resultstr = "";
		if(str != ""){
		if ((i = str.indexOf(string)) != -1) {
			String tempstr = str.substring(0, i);
			if ((j = tempstr.lastIndexOf(";")) > -1) {
				resultstr = tempstr.substring(j + 1, tempstr.length());
				} else {
					resultstr = tempstr;
				}
				resultstr = resultstr.substring(0, resultstr.length() - 1);
			}
		}
		return resultstr;
	}

	private boolean CheckValues() {

		return false;
	}
	/**
	 * 将EdtiText控件的值保存到Map
	 * @param et 控件名称
	 * @param Attribute 
	 */
	private void EditTexttoMap(EditText et,String Attribute){
		AttributeValues.put(Attribute, et.getText().toString());
		ViewIdValues.put(et.getId(), et.getText().toString());
	}
	/**
	 * 将Button 控件的值保存到Map
	 * @param btn
	 * @param Attribute
	 */
	private void DateButtontoMap(Button btn,String Attribute){
		String datestr = btn.getText().toString();
		
		AttributeValues.put(Attribute, datestr);
		ViewIdValues.put(btn.getId(), datestr);
	}
	/**
	 * 将Button对象所选择的值保存到Map
	 * @param btn
	 * @param Attribute
	 */
	private void TimeButtontoMap(Button btn,String Attribute){
		String hour = btn.getText().toString().substring(0, 2);
		String minute = btn.getText().toString().substring(3, 5);  
		String timestr = hour + "["+ minute + "[00]]";
		AttributeValues.put(Attribute, timestr);
		ViewIdValues.put(btn.getId(), timestr);
	}
	/**
	 * 将Spinner对象的所选中的值保存到Map
	 * @param spn
	 * @param Attribute
	 */
	private void SpinnertoMap(Spinner spn,String Attribute){
		String spn_str = findIdByValues(spn.getId(),spn.getSelectedItem().toString());
		AttributeValues.put(Attribute, spn_str);
		ViewIdValues.put(spn.getId(), spn_str);
	}
	/**
	 * 将checkBox对象所选中的值保存到Map
	 * @param chb
	 * @param Attribute
	 */
	private void CheckBoxtoMap(CheckBox chb,String Attribute){
		
	}
	/**
	 * 获得当前日期字符串 格式: YYYYMMDD
	 * @return
	 */
	private String getDateStr(){
		final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); //获取当前年份
        int mMonth = c.get(Calendar.MONTH);//获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
		String datestr, monthstr, daystr;
		if(mMonth>9){
			monthstr = "" + (mMonth + 1);
		}else{
			monthstr = "0" + (mMonth + 1);
		}
		if(mDay>9){
			daystr = "" + mDay ;
		}
		else{
			daystr = "0" + mDay;
		}
		datestr = mYear +"-"+ monthstr +"-"+ daystr;
		
		return datestr;
	}
	/**
	 * 获得当前时间字符串 格式:  HH:MM
	 * @return
	 */
	private String getTimeStr(){
		final Calendar c = Calendar.getInstance();
		int mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        int mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数 
        
        return mHour + ":" + mMinute ;
	}
	
	/**
	 * 通过身份证设置 出生日期和性别
	 * @param typeid  0 表示投保人  1 表示被保人
	 */
	private void setValuseByIdCard(int typeid){
		String IdCard = "";
		String brithday = "";
		String Sex = "";
		if(typeid == 0){
			IdCard = edt_PHcredentialsNo.getText().toString();
			brithday = IdcardUtils.getBirthByIdCard(IdCard);
			Sex = IdcardUtils.getGenderByIdCard(IdCard);
			if(btn_policyholderBirthday != null){
				btn_policyholderBirthday.setText(brithday);
			}
			if(spn_policyholderSex != null){
				spn_policyholderSex.setSelection(0);
				if(!spn_policyholderSex.getSelectedItem().toString().equals(Sex)){
					spn_policyholderSex.setSelection(1);
				}
			}
		}else if(typeid == 1){
			IdCard = edt_IScredentialsNo.getText().toString();
			brithday = IdcardUtils.getBirthByIdCard(IdCard);
			Sex = IdcardUtils.getGenderByIdCard(IdCard);
			if(btn_insuredBirthday!= null){
				btn_insuredBirthday.setText(brithday);
			}
			if(spn_insuredSex != null){
				spn_insuredSex.setSelection(0);
				if(!spn_insuredSex.getSelectedItem().toString().equals(Sex)){
					spn_insuredSex.setSelection(1);
				}
			}
		}
	}
	
	/**
	 * 检查年龄是否合法
	 * @param age 待检查年龄
	 * @param minage 最小年龄
	 * @param maxage 最大年龄
	 * @return
	 */
	private boolean checkinsuredAge(int age, int minage,int maxage){
		if(minage ==0 && maxage == 0) 
			return true;
		if((age >= minage)&&(age <= maxage)){
			return true;
		}
		else{
			return false;
		}
		
	}
	/**
	 * 检查性别是否合法
	 * @param sex 待检查的性别
	 * @param checksex 只允许的性别
	 * @return
	 */
	private boolean checkinsuredsex(String sex,int checksex){
		if(checksex ==0)
			return true;
		else if(checksex ==1 && sex.equals("男"))
			return true;
		else if(checksex ==2 && sex.equals("女"))
			return true;
		else
			return false;
	}
	
}