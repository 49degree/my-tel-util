package com.szxys.mhub.ui.mets.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.szxys.mhub.R;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.utils.TimeUtils;
import com.szxys.mhub.ui.mets.components.DataGridView;
import com.szxys.mhub.ui.mets.components.DataGridViewAdapter;

public class UrineCountActivity extends Activity implements OnClickListener{
	private TextView urineRecordLink = null;//排尿记录连接
	private DataGridView urineCountList = null;//表格控件
	private Button returnBtn = null;//返回按钮
	private EditText fromDateEdit = null;//开始日期输入控件
	private EditText toDateEdit = null;//结束日期输入控件
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mets_urine_count);
		
		urineCountList = (DataGridView)findViewById(R.id.mets_urine_count_list_view);//获取排尿记录列表
		fromDateEdit = (EditText)this.findViewById(R.id.mets_urine_count_from_date_edit);//开始日期输入控件
		toDateEdit = (EditText)this.findViewById(R.id.mets_urine_count_to_date_edit);//结束日期输入控件
		urineRecordLink = (TextView)this.findViewById(R.id.mets_urine_record_link);//排尿记录连接
		returnBtn = (Button)this.findViewById(R.id.mets_urine_count_return_btn);//返回按钮
		
		returnBtn.setOnClickListener(this);//配置返回按钮事件
		urineRecordLink.setOnClickListener(this);//配置排尿记录链接
		
		//默认为最近一次治疗开始日期到结束日期
		String fromDate = fromDateEdit.getText().toString();
		String toDate = toDateEdit.getText().toString();
		if(fromDate==null||fromDate.equals("")){//如果开始日期为空，请查询最近一次治疗开始日期填入其中，
			fromDateEdit.setText("2011-05-01");//模拟数据
		}
		if(toDate==null||toDate.equals("")){//结束日期
			toDateEdit.setText(TimeUtils.getTimeString(new Date(),TimeUtils.formatDate2));
		}
		
		initGrid();
		
	}
	
	/**
	 * 根据日期查询数据
	 * @param datas 查询日期
	 * @return
	 */
	private ArrayList<ArrayList<View>> getMdata(){		
		
		String fromDate = fromDateEdit.getText().toString(); //"yyyy-MM-dd"
		String toDate = toDateEdit.getText().toString(); //"yyyy-MM-dd"

		//ArrayList<String> statisticsList=DrinkAndUrine.getMetsStatistics(UrineCountActivity.this, fromDate, toDate);
		// String="统计日#(白/夜/总)排尿次数#(白/夜/总)排尿量#夜尿指数#尿急#尿失禁"
		ArrayList<String> statisticsList=DrinkUrineDao.getUrineStatisticsInfo(fromDate, toDate);
		Log.d("UrineStatistics", "From "+fromDate+" to "+toDate);

		final int ROW_COUNT=statisticsList.size();
		ArrayList<ArrayList<View>> mData = new ArrayList<ArrayList<View>>(ROW_COUNT);
		ArrayList<View> itemView = null;
		
		if (statisticsList!=null && !statisticsList.isEmpty()) {
			for(int i=0;i<statisticsList.size();i++){
				String[] rowStrings=statisticsList.get(i).split("#");//rowStrings.length=6  yyyy/MM/dd # int/int/int # int/int/int # ?% # int # int # 
				itemView = new ArrayList<View>();//增加记录详细信息
				TextView headerDate = new TextView(this);//日期				
				headerDate.setText(rowStrings[0]);
				TextView headerType = new TextView(this);//尿尿次数(白天/夜晚/总次数)
				headerType.setText(rowStrings[1]);
				TextView headerTime = new TextView(this);//尿量(白天/夜晚/总量)
				headerTime.setText(rowStrings[2]);
				TextView headerIn = new TextView(this);//夜尿指数
				headerIn.setText(rowStrings[3]);
				TextView headerOut = new TextView(this);//尿急(总次数)
				headerOut.setText(rowStrings[4]);
				TextView headerRate = new TextView(this);//尿失禁(总次数)
				headerRate.setText(rowStrings[5]);
				
				itemView.add(headerDate);
				itemView.add(headerType);
				itemView.add(headerTime);
				itemView.add(headerIn);
				itemView.add(headerOut);
				itemView.add(headerRate);

				mData.add(itemView);
			}
		}
		//以下为模拟数据
//		for(int i=0;i<10;i++){ 
//			itemView = new ArrayList<View>();
//			//增加记录详细信息
//			Date now=new Date();
//			String dateString=new SimpleDateFormat("yyyy/MM/dd").format(now);
//			TextView headerDate = new TextView(this);//日期
////			headerDate.setText(TimeUtils.getTimeString(new Date(),TimeUtils.format1));
//			headerDate.setText(dateString);
//			TextView headerType = new TextView(this);//尿尿次数(白天/夜晚/总次数)
//			headerType.setText("5/3/8");
//			TextView headerTime = new TextView(this);//尿量(白天/夜晚/总量)
//			headerTime.setText("200/40/100");
//			TextView headerIn = new TextView(this);//夜尿指数
//			headerIn.setText("26%");
//			TextView headerOut = new TextView(this);//尿急(总次数)
//			headerOut.setText(""+new Random().nextInt(10));
//			TextView headerRate = new TextView(this);//尿失禁(总次数)
//			headerRate.setText(""+i);
//			
//			itemView.add(headerDate);
//			itemView.add(headerType);
//			itemView.add(headerTime);
//			itemView.add(headerIn);
//			itemView.add(headerOut);
//			itemView.add(headerRate);
//
//			mData.add(itemView);
//		}
		
		return mData;
	}
	
	
	
	/**
	 * 根据数据构造表格及数据
	 */
	public void initGrid(){
		ArrayList<ArrayList<View>> mDataViews = getMdata();//根据排尿日记数据获取表格VIEW
        DataGridViewAdapter simpleAdapter = new DataGridViewAdapter();//表格控件数据适配器
        //为一个二维数组 第二维长度为2,分别表示列的名称和列的宽度
        String[][] mColumnHeaders = new String[][]{
        		{getText(R.string.mets_urine_count_header_date).toString(),"100"},
        		{getText(R.string.mets_urine_count_header_times).toString(),"60"},
        		{getText(R.string.mets_urine_count_header_value).toString(),"60"},
        		{getText(R.string.mets_urine_count_header_night_rate).toString(),"60"},
        		{getText(R.string.mets_urine_count_header_urgency).toString(),"40"},
        		{getText(R.string.mets_urine_count_header_rincontinence).toString(),"40"}
        };
        simpleAdapter.setMColumnHeaders(mColumnHeaders);//配置表头 
        simpleAdapter.setMData(mDataViews);
        urineCountList.setMPageDataAdapter(simpleAdapter);//设置表格数据
        urineCountList.buildDatagrid();//构建表格


	}
	
	
	@Override
	public void onClick(View v){
		int buttonId = v.getId();
		switch (buttonId) {
		case R.id.mets_urine_count_return_btn://返回按钮事件
			finish();
			break;
		case R.id.mets_urine_record_link://排尿记录链接
			Intent countIntent = new Intent(UrineCountActivity.this,UrineRecordActivity.class);
			startActivity(countIntent);//打开修改页面
			break;			
		default :
			break;
		}
	}
}
