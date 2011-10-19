package com.guanri.android.insurance.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.activity.base.ApsaiPageNumPannelView;
import com.guanri.android.insurance.activity.base.ApsaiPageNumPannelView.ApsaiPageNumOperator;
import com.guanri.android.insurance.activity.dialog.LogQueryDialog;
import com.guanri.android.insurance.activity.dialog.LogQueryDialog.QueryInterface;
import com.guanri.android.insurance.bean.OperateLogBean;
import com.guanri.android.insurance.service.OperateLogService;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 操作日志管理界面
 * @author Administrator
 *
 */
public class OperateLogActivity extends ApsaiActivity implements OnCheckedChangeListener{
	public static Logger logger = Logger.getLogger(OperateLogActivity.class);// 日志对象;
	
	private ListView loginfo_list = null;
	private CheckBox allSelectBtn = null;
	private ApsaiPageNumPannelView apsaiPageNumPannel = null;
	
	private OperateLogService operateLogService = null;
	
	private List<Object> loginfolist;
	private List<String> selectitem;
	public Map<String,String> params = null;
	
	ListViewAdapter listViewAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(StringUtils.getStringFromValue(R.string.apsai_log_manager));
		setContentView(R.layout.log_info_manager);
		
		loginfo_list = (ListView) findViewById(R.id.loginfo_list);
		allSelectBtn = (CheckBox)findViewById(R.id.allSelect_btn);
		allSelectBtn.setOnCheckedChangeListener(this);
		operateLogService = new OperateLogService(this);
		params = new HashMap<String,String>();
		params.put("ORDERBY","Operate_time DESC");
		params.put("is_del=", "0");
		//queryInsuList(false,params);
		
		
		apsaiPageNumPannel = (ApsaiPageNumPannelView)this.findViewById(R.id.log_page_num_pannel);
		apsaiPageNumPannel.setApsaiPageNumOperator(apsaiPageNumOperator);
		
		
	}

	/**
	 * 根据数据构造表格
	 */
	public void initArrayList(){

		loginfo_list.setHorizontalScrollBarEnabled(true);//设置可滚动
		loginfo_list.setDividerHeight(0);//设置LIST没有分割线
		LayoutInflater mInflater = LayoutInflater.from(this);
	}
	

	private ApsaiPageNumOperator apsaiPageNumOperator = new ApsaiPageNumOperator(){
		/**
		 * 查询总记录数
		 */
		public int queryAllRows(){
			return operateLogService.getLogRowNum();
		}
		/**
		 * 设置表格数据 
		 * 	pageInfo[0]//第几页
		 *	pageInfo[1]//每页行数
		 *	pageInfo[2]//总页数
		 *	pageInfo[3]//总行数
		 */
		@Override
		public void initListAdapter(int[] pageInfo){
			
			//分页查询条件--》limit 10,100
			params.put("LIMIT",(pageInfo[0]-1)*pageInfo[1]+","+String.valueOf(pageInfo[1]));
			selectitem = new ArrayList<String>();
			loginfolist = operateLogService.getLogList(params);
			
			listViewAdapter = new ListViewAdapter(OperateLogActivity.this,
					loginfolist,false);
			
			loginfo_list.setAdapter(listViewAdapter);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.apsai_log_operate_late).setIcon(R.drawable.apsai_late);
		menu.add(0, 2, 1, R.string.apsai_base_del).setIcon(R.drawable.apsai_del);
		menu.add(0, 3, 1, R.string.apsai_base_query).setIcon(R.drawable.apsai_query);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:			
			//当日日志查询
			Date date = new Date();
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd ");
			String Operatetime = df.format(date)+"00:00:00";
			params.put("Operate_time>=",Operatetime);
			apsaiPageNumPannel.initApsaiPageNumOperator();
			break;
		case 2:
			// 删除
			if((selectitem != null)&&(selectitem.size()>0)){
				for (int i = 0; i < selectitem.size(); i++) {
					operateLogService.deleteLog(new Integer(selectitem.get(i)));
				}
				//apsaiPageNumPannel.initApsaiPageNumOperator();
				apsaiPageNumPannel.initListAdapter();
				
			}
			break;
		case 3:
			
			LogQueryDialog logQueryDialog = new LogQueryDialog(OperateLogActivity.this,new QueryInterface(){
				public void query(Map<String,String> queryparams){
					//params.put("Operator_id=", "800001");
					params.putAll(queryparams);
					apsaiPageNumPannel.initApsaiPageNumOperator();
				}
			});
			logQueryDialog.displayDlg();
			break;
		default:
			break;
		}
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	class ListViewAdapter extends BaseAdapter {
		LayoutInflater inflater;
		List<Object> loginfos;
		boolean allselect;

		public ListViewAdapter(Context context,
				List<Object> loginfos,boolean allselect) {
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(context);
			this.loginfos = loginfos;
			this.allselect = allselect;
			
			selectitem = new ArrayList<String>();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return loginfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return loginfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
//		public View getView( int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			OperateLogBean operateLogBean = (OperateLogBean)loginfos.get(position);
//			
//			// 将设置的XML文件转型为View
//			View view = inflater.inflate(R.layout.log_info_manager_item, null);
//			// 出去XML文件定义的控件
//			TextView tv_name = (TextView) view.findViewById(R.id.log_name_edt);
//			TextView tv_no = (TextView) view.findViewById(R.id.log_no_edt);
//			TextView tv_date = (TextView) view.findViewById(R.id.log_date_edt);
//			TextView tv_content = (TextView) view.findViewById(R.id.log_cont_edt);
//			CheckBox chbox = (CheckBox) view
//					.findViewById(R.id.log_select_chb);	
//			chbox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(position));
//			// 为控件进行赋值
//			chbox.setChecked(allselect);
//			tv_name.setText(operateLogBean.getOperator_name());
//			tv_no.setText(operateLogBean.getOperator_id());
//			tv_date.setText(operateLogBean.getOperate_time());
//			tv_content.setText("描述: "+operateLogBean.getOperate_memo());
//			if(allselect){
//				selectitem.add(""+operateLogBean.getLog_id());
//			}
//			return view;
//		}
		public View getView(int position, View convertView, ViewGroup parent) {

			Log.d("MyAdapter", "Position:" + position + "---" + String.valueOf(System.currentTimeMillis()));
			ViewHolder holder;
			if (convertView == null) {
				OperateLogBean operateLogBean = (OperateLogBean)loginfos.get(position);
				//final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.log_info_manager_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.log_name_edt);
				holder.tv_no = (TextView) convertView.findViewById(R.id.log_no_edt);
				holder.tv_date = (TextView) convertView.findViewById(R.id.log_date_edt);
				holder.tv_content = (TextView) convertView.findViewById(R.id.log_cont_edt);
				holder.chbox = (CheckBox) convertView
						.findViewById(R.id.log_select_chb);	
				holder.chbox.setOnCheckedChangeListener(new MyOnCheckedChangeListener(position));
				// 为控件进行赋值
				holder.chbox.setChecked(allselect);
				holder.tv_name.setText(operateLogBean.getOperator_name());
				holder.tv_no.setText(operateLogBean.getOperator_id());
				holder.tv_date.setText(operateLogBean.getOperate_time());
				holder.tv_content.setText("描述: "+operateLogBean.getOperate_memo());
				if(allselect){
					selectitem.add(""+operateLogBean.getLog_id());
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}

		class ViewHolder {
			TextView tv_name ;
			TextView tv_no  ;
			TextView tv_date ;
			TextView tv_content ;
			CheckBox chbox;
		}
		
		
		class MyOnCheckedChangeListener implements OnCheckedChangeListener{
			private int position;
			public MyOnCheckedChangeListener(int position){
				this.position = position;
			}
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					selectitem.add(""+((OperateLogBean)loginfolist.get(position)).getLog_id());
				}else{
					selectitem.remove(""+((OperateLogBean)loginfolist.get(position)).getLog_id());
				}
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
		listViewAdapter = new ListViewAdapter(OperateLogActivity.this,
				loginfolist,isChecked);
		loginfo_list.setAdapter(listViewAdapter);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
