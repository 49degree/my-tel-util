package com.guanri.android.insurance.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.InsuPlanManagerActivity.ListViewAdapter.MyOnClickListener;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.bean.InsuPlanRecordBean;
import com.guanri.android.insurance.service.InsuPlanManagerService;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 业务方案管理界面
 * 
 * @author Administrator
 * 
 */
public class InsuPlanManagerActivity extends ApsaiActivity {
	public static Logger logger = Logger.getLogger(InsuPlanManagerActivity.class);// 日志对象;
	InsuPlanManagerService insuPlanManagerDAO = null;
	private ListView insuinfo_list = null;
	
	private List<InsuPlanRecordBean> insuinfolist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitle(StringUtils.getStringFromValue(R.string.apsai_systeminfo_insulist));
		setContentView(R.layout.sys_insuinfo_manager);
		insuPlanManagerDAO = new InsuPlanManagerService(this);
		
		insuinfo_list = (ListView) findViewById(R.id.insuinfo_list);
		queryInsuList();
	}

	/**
	 * 构建列表
	 */
	private void queryInsuList() {
		insuinfolist = null;
		insuPlanManagerDAO = null;
		insuPlanManagerDAO = new InsuPlanManagerService(this);
		insuinfolist = new ArrayList<InsuPlanRecordBean>();
		insuinfolist = insuPlanManagerDAO.queryInsuPlanList();
		insuinfo_list.setAdapter(new ListViewAdapter(InsuPlanManagerActivity.this,
				insuinfolist));
//		String[] queryParam = {"CardCode","Planno","Name","File_downloaded","useable","Operator_id","Operate_time"};
//		List<Map<String,String>> insuMap = insuPlanManagerDAO.queryInsuPlanList(queryParam);
//		for(Map<String,String> insu:insuMap){
//			for(String a:insu.values()){
//				logger.debug(a);
//			}
//			
//			if(insu.containsKey("useable")&&insu.get("useable").equals("1")){
//				insu.put("useable", "激活");
//			}else{
//				insu.put("useable", "禁用");
//			}
//			if(insu.containsKey("File_downloaded")&&insu.get("File_downloaded").equals("1")){
//				insu.put("File_downloaded", "已下载");
//			}else{
//				insu.put("File_downloaded", "未下载");
//			}
//		}
//		
//        int[] to = new int[] {R.id.apsai_insu_manager_code, R.id.apsai_insu_manager_planno, R.id.apsai_insu_manager_name, 
//        		R.id.apsai_insu_manager_file_state};
//        SimpleAdapter recordAdapter = new SimpleAdapter(this, insuMap, R.layout.sys_insuinfo_item,queryParam, to);
//       
//
//        
//        
//		insuinfo_list.setAdapter(recordAdapter);
//		
////        for(int position = 0;position<insuMap.size();position++){
////        	logger.debug("arg0");
////        	View v = recordAdapter.getView(position, null, insuinfo_list);
////        	Button bt = (Button)v.findViewById(R.id.apsai_insu_manager_filedown_btn);
////        	bt.setOnClickListener(new OnClickListener(){
////        		public void onClick(View v){
////        			logger.debug("arg0");
////        		}
////        	});
////        	
////        }
//		
//		insuinfo_list.setOnItemClickListener(new OnItemClickListener(){
//			public void onItemClick(final AdapterView<?> arg0, View arg1,final int arg2, long arg3){
//				logger.debug("arg0");
//				Map<String,String> insu = (Map<String,String>)arg0.getItemAtPosition(arg2);
//				
//				final ProgressDialog btDialog = new ProgressDialog(InsuPlanManagerActivity.this);
//				btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadloading)); // title     
//				btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadinsuloading));//进度是否是不确定的，这只和创建进度条有关
//				btDialog.show();
//				
//				Handler messageHandler = new Handler(){
//					public void handleMessage(Message msg) {
//						switch(msg.what){
//						case 0:
//							btDialog.dismiss();
//							break;
//						case -1:
//							btDialog.dismiss();
//							// 失败
//							AlertDialog.Builder builder = new AlertDialog.Builder(InsuPlanManagerActivity.this);
//							builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_error));
//							builder.setMessage((String)msg.obj);
//							builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),
//									new DialogInterface.OnClickListener() {
//										public void onClick(DialogInterface dialog,int which) {
//											// dosome thing
//										}
//							});
//							builder.create().show();
//							break;
//						default :
//							btDialog.setMessage((String)msg.obj);
//						}
//					}
//				};
//				insuPlanManagerDAO.downInsuFile(insu.get("CardCode"), insu.get("Planno"),messageHandler);
//				
//				
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.apsai_insu_manager_updatelist);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		//等待提示框
		final ProgressDialog btDialog = new ProgressDialog(this);
		btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_updatelist)); // title     
		btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadinsulistloading));//进度是否是不确定的，这只和创建进度条有关
		
		btDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
			public void onDismiss(DialogInterface dialog){
				queryInsuList();
			}
		});
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			// 重新下载服务列表
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(StringUtils
					.getStringFromValue(R.string.apsai_insu_manager_updatelist_warning));
			builder.setTitle(StringUtils
					.getStringFromValue(R.string.apsai_common_advise));
			builder.setPositiveButton(
					StringUtils.getStringFromValue(R.string.apsai_common_sure),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							insuPlanManagerDAO.downInsuPlanList(btDialog);
						}
					});
			builder.setNegativeButton(StringUtils.getStringFromValue(R.string.apsai_common_cancall), null);
			builder.create().show();
		}
		return true;
	}

	
	class ListViewAdapter extends BaseAdapter {
		LayoutInflater inflater;
		List<InsuPlanRecordBean> insuinfos;

		public ListViewAdapter(Context context,
				List<InsuPlanRecordBean> insuinfos) {
			// TODO Auto-generated constructor stub
			inflater = LayoutInflater.from(context);
			this.insuinfos = insuinfos;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return insuinfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return insuinfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 将设置的XML文件转型为View
			View view = inflater.inflate(R.layout.sys_insuinfo_manager_item, null);
			// 出去XML文件定义的控件
			TextView tv_name = (TextView) view.findViewById(R.id.tv_insu_name);
			TextView tv_filestate = (TextView)view.findViewById(R.id.tv_insu_filestate);
			ImageView imv = (ImageView) view.findViewById(R.id.img_insu_state);
			ToggleButton tgbtn = (ToggleButton) view
					.findViewById(R.id.tglbtn_insu_state);	
			tgbtn.setOnClickListener(new MyOnClickListener(position));
			Button btn_filedown = (Button)view.findViewById(R.id.btn_filedown);
			btn_filedown.setOnClickListener(new MyOnClickListener(position));
			// 为控件进行赋值
			
			tv_filestate.setText(insuinfos.get(position).getCardCode() +" - "+ insuinfos.get(position).getPlanno() );
			tv_name.setText(insuinfos.get(position).getName());
			if (insuinfos.get(position).getFile_downloaded()) {
				imv.setImageResource(R.drawable.sys_insu_state_down);
				tv_filestate.setText(insuinfos.get(position).getCardCode()+"_"+ insuinfos.get(position).getPlanno()+"-"+ StringUtils.getStringFromValue(R.string.apsai_systeminfo_insusdownstate));
			} else {
				imv.setImageResource(R.drawable.sys_insu_state_undown);
				tv_filestate.setText(insuinfos.get(position).getCardCode() +"_"+ insuinfos.get(position).getPlanno()+"-"+ StringUtils.getStringFromValue(R.string.apsai_systeminfo_insusdownunstate));
			}
			tgbtn.setChecked(insuinfos.get(position).getUseable());
			tgbtn.setEnabled(insuinfos.get(position).getFile_downloaded());
			return view;
		}
		
		class MyOnClickListener implements OnClickListener{
			private int position;
			public MyOnClickListener(int position){
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.tglbtn_insu_state:{
					insuPlanManagerDAO.changePlanState(insuinfos.get(position).getCardCode(),insuinfos.get(position).getPlanno());
						
				}
					break;
				case R.id.btn_filedown:{
					//开始下载
					
					final ProgressDialog btDialog = new ProgressDialog(InsuPlanManagerActivity.this);
					btDialog.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadloading)); // title     
					btDialog.setMessage(StringUtils.getStringFromValue(R.string.apsai_insu_manager_downloadinsuloading));//进度是否是不确定的，这只和创建进度条有关
					btDialog.show();
					
					Handler messageHandler = new Handler(){
						public void handleMessage(Message msg) {
							switch(msg.what){
							case 0:
								btDialog.dismiss();
								ContentValues cv = new ContentValues();
								cv.put("File_downloaded", true);
								insuPlanManagerDAO.updateInsuInfo(insuinfolist.get(position).getCardCode(),insuinfos.get(position).getPlanno(), cv);
								queryInsuList();
								break;
							case -1:
								btDialog.dismiss();
								// 失败
								AlertDialog.Builder builder = new AlertDialog.Builder(InsuPlanManagerActivity.this);
								builder.setTitle(StringUtils.getStringFromValue(R.string.apsai_insu_manager_error));
								builder.setMessage((String)msg.obj);
								builder.setPositiveButton(StringUtils.getStringFromValue(R.string.apsai_common_sure),
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,int which) {
												// dosome thing
											}
								});
								builder.create().show();
								break;
							default :
								btDialog.setMessage((String)msg.obj);
							}
						}
					};
					insuPlanManagerDAO.downInsuFile(insuinfolist.get(position).getCardCode(), insuinfolist.get(position).getPlanno(),messageHandler);
						
				}
				default:
					break;
				}
				
			}
		}
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//insuPlanManagerDAO.dbOperater.release();

	}
}
