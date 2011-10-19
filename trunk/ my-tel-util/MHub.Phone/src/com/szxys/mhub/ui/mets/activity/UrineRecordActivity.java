package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.DataGridView;
import com.szxys.mhub.ui.mets.components.DataGridViewAdapter;

/**
 * 排尿记录
 * @author Administrator
 *
 */
public class UrineRecordActivity  extends Activity implements OnClickListener{
	public final static String PARAM_RECORD_ID = "recordId";//Intent中的参数名称，记录ID
//	public final static String ID_PARAM_TIME = "time";//Intent中的参数名称，记录的时间
//	public final static String ID_PARAM_VALUE = "value";//Intent中的参数名称，记录的值
	
	public final static int sleep = 2;

	int choiceId = -1;//被选中记录ID
	int choiceIndex = -1;//被选中的行号
	int urineRecordListX = 0;//表格滚动条的 横轴的位置
	int urineRecordListY = 0;//表格滚动条的 纵轴的位置
	private final Handler mHandler = new Handler(); 
	
	private EditText dateEdit = null;//日期输入控件
	private DataGridView urineRecordList = null;//表格控件
	private Button modifyBtn = null;//修改按钮
	private Button deleteBtn = null;//删除按钮
	private Button returnBtn = null;//返回按钮
	private TextView urineCountLink = null;//排尿统计连接	
	
	ArrayList<Drinkandurine> drinkandurines = null;//数据列表
	
	private String DayTimeString="白天";
	private String NightString="夜晚";
	private String EmergentUrine="尿急";
	private String LossUrine="(失)"; //尿失禁
	private String success_DelDrinkAndUrine="删除成功";
	private String failed_DelDrinkAndUrine="删除失败";
	private String[] thedaytimes=new String[]{"",""};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mets_urine_record);
		urineRecordList = (DataGridView)findViewById(R.id.mets_urine_record_list_view);//获取排尿记录列表
		dateEdit = (EditText)this.findViewById(R.id.mets_urine_record_date_edit);//日期输入控件
		urineCountLink = (TextView)this.findViewById(R.id.mets_urine_count_link);//日期输入控件
		
		modifyBtn = (Button)this.findViewById(R.id.mets_urine_record_modify_btn);
		deleteBtn = (Button)this.findViewById(R.id.mets_urine_record_delete_btn);
		returnBtn = (Button)this.findViewById(R.id.mets_urine_record_return_btn);
		
		
		modifyBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		urineCountLink.setOnClickListener(this);
		
		DayTimeString=getResources().getString(R.string.mets_time_atday_text);//"白天";
		NightString=getResources().getString(R.string.mets_time_atnight_text);//"夜晚";
		EmergentUrine=getResources().getString(R.string.mets_urine_emergent);//"尿急";
		LossUrine=getResources().getString(R.string.mets_urine_loss);//"(失)"; //尿失禁
		success_DelDrinkAndUrine=getResources().getString(R.string.mets_delete_ok);//"删除成功";
		failed_DelDrinkAndUrine=getResources().getString(R.string.mets_delete_failed);//"删除失败";
		String date = dateEdit.getText().toString();//获取输入框中的时间
		// 如果没有则获取当前时间
		if (date==null||date.equals("")) {
			date = TimeUtils.getTimeString(new Date(),TimeUtils.formatDate2);
			dateEdit.setText(date);
		}
		thedaytimes=GotobedGetup.getGetUpGotobedTimes(UrineRecordActivity.this,date);
		drinkandurines = getDrinkandurinesByTime(UrineRecordActivity.this, dateEdit.getText().toString());//根据日期查询排尿日记数据
		initGrid();//绘制表格
	}
	
	
	@Override
	public void onRestart() {
		super.onRestart();
		drinkandurines = getDrinkandurinesByTime(UrineRecordActivity.this,dateEdit.getText().toString());//根据日期查询排尿日记数据
		initGrid();//绘制表格
	}
		
	
	/**
	 * 根据日期查询数据
	 * @param 查询日期 dateString="yyyy-MM-dd"
	 * @return ArrayList<Drinkandurine> 实体类集合
	 */
	private ArrayList<Drinkandurine> getDrinkandurinesByTime(Context context,String dateString){
		ArrayList<Drinkandurine> drinkandurines = new ArrayList<Drinkandurine>();
		//drinkandurines=DrinkAndUrine.Select(context, null, null, null, null, " c_Id desc");
		drinkandurines=DrinkUrineDao.getUrineList(dateString);
		
		//以下为模拟构造数据
		for(int i=0;i<12;i++){
			Drinkandurine drinkandurine = new Drinkandurine();
			drinkandurine.setC_Id(i);
			drinkandurine.setC_Type(1);
			drinkandurine.setC_CollectType(2);
			drinkandurine.setC_Quantity(150.0f);
			drinkandurine.setC_Proportion(240f);
			drinkandurine.setC_DateTime("2011-05-05");
			drinkandurine.setC_UfrId(2);
			drinkandurines.add(drinkandurine);
			
		}
		return drinkandurines;
	}
	
	
	
	/**
	 * 根据数据构造表格及数据
	 */
	public void initGrid(){
		ArrayList<ArrayList<View>> mDataViews = getMdata(drinkandurines);//根据排尿日记数据获取表格VIEW
        DataGridViewAdapter simpleAdapter = new DataGridViewAdapter();//表格控件数据适配器
        //为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
        String[][] mColumnHeaders = new String[][]{
        		{getText(R.string.mets_urine_record_header_choice).toString(),"60"},
        		{getText(R.string.mets_urine_record_header_type).toString(),"80"},
        		{getText(R.string.mets_urine_record_header_time).toString(),"80"},
        		{getText(R.string.mets_urine_record_header_in).toString(),"80"},
        		{getText(R.string.mets_urine_record_header_out).toString(),"80"},
        		{getText(R.string.mets_urine_record_header_rate).toString(),"80"}
        };
        simpleAdapter.setMColumnHeaders(mColumnHeaders);//配置表头 
        simpleAdapter.setMData(mDataViews);
        urineRecordList.setMPageDataAdapter(simpleAdapter);//设置表格数据
        urineRecordList.buildDatagrid();//构建表格
        //控制表格滚动位置
        mHandler.post(new Runnable() {  
            @Override  
            public void run() {  
                if(urineRecordList.mTableContainer!=null){
                	urineRecordList.mTableContainer.scrollTo(urineRecordListX, urineRecordListY);
                }  
            }  
          });
	}

	private ArrayList<ArrayList<View>> getMdata(ArrayList<Drinkandurine> datas){
		ArrayList<ArrayList<View>> mData = new ArrayList<ArrayList<View>>(20);
		int datasLength = datas.size();
		Drinkandurine drinkandurine = null;
		
		
		for(int i=0;i<datasLength;i++){ 
			drinkandurine = datas.get(i);
			ArrayList<View> itemView = new ArrayList<View>();
			RadioButton rBtn = new RadioButton(this);//构造单选框
			TableRow.LayoutParams mp = new TableRow.LayoutParams();
			mp.leftMargin = 15;
			mp.height = 25;
		    mp.width = TableRow.LayoutParams.WRAP_CONTENT;
		    rBtn.setLayoutParams(mp);
			rBtn.setButtonDrawable(R.drawable.mets_checkbox);
			rBtn.setId(drinkandurine.getC_Id());
			if(drinkandurine.getC_Id() == choiceId){//判断是否被选中
				rBtn.setChecked(true);
			}
			//单选款单击事件（选中） 
			final int indexI = i;
			rBtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					if(choiceId!=v.getId()){
						choiceId = v.getId();
						choiceIndex = indexI;
						if(urineRecordList.mTableContainer!=null){
							urineRecordListX = urineRecordList.mTableContainer.getScrollX();
							urineRecordListY = urineRecordList.mTableContainer.getScrollY();
						}
						initGrid();
					}
				}
			});
			itemView.add(rBtn);
			
			//增加记录详细信息
			TextView headerType = new TextView(this);//类型
			headerType.setGravity(Gravity.CENTER);
			String dt_cDateTime=drinkandurine.getC_DateTime().trim();			
			TextView headerTime = new TextView(this);//时间
			headerTime.setGravity(Gravity.CENTER);
			if (dt_cDateTime.length()>10) {
				Date recordDate=TimeUtils.convertToDate(dt_cDateTime);
				headerType.setText(getCategoryString(dt_cDateTime));
				headerTime.setText(new SimpleDateFormat("HH:mm").format(recordDate));
			}
			TextView headerIn = new TextView(this);//入量
			headerIn.setGravity(Gravity.CENTER);
			TextView headerOut = new TextView(this);//尿量
			headerOut.setGravity(Gravity.CENTER);
			if (drinkandurine.getC_Type()==0) {
				headerIn.setText(String.valueOf(drinkandurine.getC_Quantity())+"ml");
				headerOut.setText("");
			}
			if (drinkandurine.getC_Type()==1) {
				headerIn.setText("");
				headerOut.setText(String.valueOf(drinkandurine.getC_Quantity())+"ml");
			}
			if (drinkandurine.getC_Type()==2) {
				headerIn.setText("");
				headerOut.setText(EmergentUrine);
			}
			if (drinkandurine.getC_Type()==3) {
				headerIn.setText("");
				headerOut.setText(drinkandurine.getC_Quantity()+LossUrine);
			}
			ImageView headerRate = new ImageView(this);//尿流率
			if (drinkandurine.getC_UfrId()>0) {
				headerRate.setImageResource(R.drawable.mets_u89);
			}
			
			final Drinkandurine drinkandurineFinal = drinkandurine;
			headerRate.setOnClickListener(new OnClickListener(){//查询尿流率信息
				public void onClick(View v){
					Intent rateIntent = new Intent(UrineRecordActivity.this,UrineRateActivity.class);
					//rateIntent.putExtra(UrineRecordActivity.PARAM_RECORD_ID, drinkandurineFinal.getC_Id());
					rateIntent.putExtra(UrineRecordActivity.PARAM_RECORD_ID, drinkandurineFinal.getC_UfrId()); //Edited by WXG @Thu May 11,2011
					startActivity(rateIntent);//打开尿流率页面
				}
			});
			
			itemView.add(headerType);
			itemView.add(headerTime);
			itemView.add(headerIn);
			itemView.add(headerOut);
			itemView.add(headerRate);

			mData.add(itemView);
		}
		return mData;
	}
	private String getCategoryString(String dtCompared) {
		String metsTypeString=DayTimeString;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date gotobed;
		if (thedaytimes[1].trim().length()>10) {
			try {
				gotobed = formatter.parse(thedaytimes[1].trim());
				Date compared=formatter.parse(dtCompared);
				Calendar calTmp=Calendar.getInstance();
				calTmp.setTime(gotobed);
				calTmp.add(Calendar.MINUTE, 10);//睡觉后10min
				if (compared.after(calTmp.getTime())) {
					metsTypeString=NightString;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}//没有睡觉时间 (算白天)
		return metsTypeString;
	}
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_record_modify_btn://修改按钮事件
			if(drinkandurines!=null&&choiceIndex>-1){//记录不为空，且有被选中的行
				Drinkandurine drinkandurine = drinkandurines.get(choiceIndex);
				//根据Drinkandurine对象转入不同的页面修改
				//若选择的是饮水量信息，点修改按钮后，进入饮水量修改页面；
				//若选择的是尿急信息，点修改按钮后，进入尿急修改页面；
				//若选择的是尿失禁信息，点修改按钮后，进入尿失禁修改页面；
				//若选择的是尿量信息，点修改按钮后，进入尿量量修改页面；
				Intent modifyIntent = new Intent(UrineRecordActivity.this,ModifyDrinkUrineActivity.class);
				modifyIntent.putExtra(PARAM_RECORD_ID, drinkandurine.getC_Id());//Intent中的参数名称，记录ID
				
				if(drinkandurine.getC_Type()==0){//饮水量
					//modifyIntent.putExtra(Main.MainStatic.BusinessIdString,Main.MainStatic.MODIFY_DRINK);
				}else if(drinkandurine.getC_Type()==1){//尿量
					//如果是尿量信息，则判断是否手工输入数据
					//modifyIntent.putExtra(Main.MainStatic.BusinessIdString,Main.MainStatic.MODIFY_URINE);
				}else if(drinkandurine.getC_Type()==2){//尿急
					//modifyIntent.putExtra(Main.MainStatic.BusinessIdString,Main.MainStatic.MODIFY_URINARY_URGENCY);
				}else if(drinkandurine.getC_Type()==3){//尿失禁
					//modifyIntent.putExtra(Main.MainStatic.BusinessIdString,Main.MainStatic.MODIFY_URINARY_INCONTINENCE);
				}
				startActivity(modifyIntent);//打开修改页面
			}
			
			
			break;
		case R.id.mets_urine_record_delete_btn://删除按钮事件
			if(choiceId>=0){
				deleteCorfirm();
			}else{
				Toast.makeText(getApplicationContext(), getText(R.string.mets_no_checked_line).toString(), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.mets_urine_record_return_btn://返回按钮事件
			finish();
			break;
		case R.id.mets_urine_count_link://排尿统计链接
			Intent countIntent = new Intent(UrineRecordActivity.this,UrineCountActivity.class);
			startActivity(countIntent);//打开修改页面
			break;			
		default :
			break;
		}
	}
	
	
	/**
	 * 删除确认
	 * @return
	 */
	public void deleteCorfirm() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(R.string.mets_urine_record_delete_text);
		
		builder.setTitle(R.string.mets_promptmessage_toptitle);
		builder.setPositiveButton(R.string.mets_promptmessage_ok,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						deleteById(choiceId);
						mHandler.post(new Runnable(){
							public void run(){
								drinkandurines = getDrinkandurinesByTime(UrineRecordActivity.this,dateEdit.getText().toString());//根据日期查询排尿日记数据
								initGrid();//绘制表格
							}
						});
					}
				});
		builder.setNegativeButton(R.string.mets_promptmessage_negative,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
	
	/**
	 * 删除指定的行
	 * @param choiceId
	 */
	public void deleteById(int choiceId){
		if (choiceId>0) {
			int row=DrinkAndUrine.deleteByCid(UrineRecordActivity.this, choiceId);
			if (row>0) {
				Toast.makeText(UrineRecordActivity.this, success_DelDrinkAndUrine, Toast.LENGTH_LONG).show();
			}
		}else {
			Toast.makeText(UrineRecordActivity.this, failed_DelDrinkAndUrine, Toast.LENGTH_LONG).show();
			return;
		}
	}
}
