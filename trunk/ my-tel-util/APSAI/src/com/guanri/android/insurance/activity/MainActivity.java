package com.guanri.android.insurance.activity;


import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.activity.dialog.InsuBackDialog;
import com.guanri.android.insurance.activity.dialog.InsuUselessDialog;
import com.guanri.android.insurance.bean.SaleCheckBean;
import com.guanri.android.insurance.bean.UserInfoBean;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgCancelBtn;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.common.NetWorkBlthStateHandler;
import com.guanri.android.insurance.service.SaleCheckService;
import com.guanri.android.lib.context.MainApplication;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 主 界面管理
 * @author Administrator
 *
 */
public class MainActivity extends ApsaiActivity{
	
	ImageButton logManagerBtn = null;//日志管理
	ImageButton operatorManagerBtn = null;//操作员管理
	
	ImageButton insuBatchListBtn = null; //保单批次列表
	ImageButton checkManagerBtn = null;//对账管理

	ImageButton insuplanManagerBtn = null;//业务方案管理
	ImageButton softUpdatebtn = null;//升级
	
	ImageButton sysConfigBtn = null;//系统配置
	ImageButton versionInfoBtn = null;//软件版本

	ImageButton insuBackBtn = null; //退单
	ImageButton insuSaleBtn = null; //出单 
	ImageButton insuUselessBtn = null; //废单
	ImageButton insuQueryBtn = null; //单证查询
	
	SaleCheckService saleCheckService = null;
	byte userLevel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//获取用户类型
		getWindow().setFormat(PixelFormat.RGBA_8888);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		userLevel = ((UserInfoBean)MainApplication.getInstance().getUserInfo()).getUserLevel();//HEX	1	0x00=管理员；0x01=一般操作员
		
		
		if(userLevel==0x00){  //0x00=管理员 
			this.setContentView(R.layout.apsai_manager_main);
			
			logManagerBtn = (ImageButton)this.findViewById(R.id.log_manager_btn);//日志管理
			operatorManagerBtn = (ImageButton)this.findViewById(R.id.operator_manager_btn);//操作员管理
			insuplanManagerBtn = (ImageButton)this.findViewById(R.id.insuplan_manager_btn);//业务方案管理
			
			logManagerBtn.setOnClickListener(this);
			operatorManagerBtn.setOnClickListener(this);
			insuplanManagerBtn.setOnClickListener(this);
			
		}else{//一般操作员 
			this.setContentView(R.layout.apsai_operator_main);
			
			insuBackBtn = (ImageButton)this.findViewById(R.id.insu_back_btn);
			insuSaleBtn = (ImageButton)this.findViewById(R.id.insu_sale_btn);
			insuUselessBtn = (ImageButton)this.findViewById(R.id.insu_useless_btn);
			insuQueryBtn = (ImageButton)this.findViewById(R.id.insu_query_btn);
			
			insuBackBtn.setOnClickListener(this);
			insuSaleBtn.setOnClickListener(this);
			insuUselessBtn.setOnClickListener(this);
			insuQueryBtn.setOnClickListener(this);

			//强制进行对账
			saleCheckService = new SaleCheckService(this);
			SaleCheckBean saleCheckBean  = saleCheckService.queryNoCheckedOrder();//查询是否有需要进行对账的记录
			if(saleCheckBean!=null&&saleCheckBean.getOperator_id()!=null&&
					!"".equals(saleCheckBean.getOperator_id())){//存在需要对账的记录
				Intent saleCheckIntent = new Intent(this, SaleCheckActivity.class);
				saleCheckIntent.putExtra(SaleCheckActivity.MUST_CHECK_FLAS, true);
				this.startActivityForResult(saleCheckIntent,SaleCheckActivity.MAIN_ACTIVITY);
			}
		}
		

		checkManagerBtn = (ImageButton)this.findViewById(R.id.check_manager_btn);//对账管理
		softUpdatebtn = (ImageButton)this.findViewById(R.id.soft_updatebtn);//升级
		sysConfigBtn = (ImageButton)this.findViewById(R.id.sys_config_btn);//系统配置
		versionInfoBtn = (ImageButton)this.findViewById(R.id.version_info_btn);//软件版本
		insuBatchListBtn = (ImageButton)this.findViewById(R.id.insubatch_manager_btn);//保单批次管理
		checkManagerBtn = (ImageButton)this.findViewById(R.id.check_manager_btn);//对账
		

		checkManagerBtn.setOnClickListener(this);
		softUpdatebtn.setOnClickListener(this);
		sysConfigBtn.setOnClickListener(this);
		versionInfoBtn.setOnClickListener(this);
		insuBatchListBtn.setOnClickListener(this);
		checkManagerBtn.setOnClickListener(this);
		
		MainApplication.getInstance().startNetWorkListen(new NetWorkBlthStateHandler());//开始监听网络状态
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int vId = v.getId();
		switch(vId){
		case R.id.log_manager_btn://日志管理
			Intent logIntent = new Intent(this, OperateLogActivity.class);
			startActivity(logIntent);
			break;
		case R.id.insubatch_manager_btn://对账记录管理
			Intent insubatchIntent = new Intent(this, SaleCheckedRecordActivity.class);
			startActivity(insubatchIntent);
			break;			
		case R.id.check_manager_btn://对账管理
			Intent saleCheckIntent = new Intent(this, SaleCheckActivity.class);
			startActivity(saleCheckIntent);
			break;			
		case R.id.operator_manager_btn://操作员管理
			Intent userIntent = new Intent(this, UserManagerActivity.class);
			startActivity(userIntent);
			break;
		case R.id.insuplan_manager_btn://业务方案管理
			Intent insuplanIntent = new Intent(this, InsuPlanManagerActivity.class);
			startActivity(insuplanIntent);
			break;
		case R.id.sys_config_btn://系统配置
			Intent sysIntent = new Intent(this, SystemConfigActivity.class);
			startActivity(sysIntent);
			break;
		case R.id.soft_updatebtn://升级
			DialogUtils.showErrorAlertDlg(this, "提示", "已经是最新版本！");
			break;
		case R.id.version_info_btn://软件版本
			Intent sysverIntent = new Intent(this, SystemVersionActivity.class);
			startActivity(sysverIntent);
			//DialogUtils.showErrorAlertDlg(this, "提示", "正在开发中....");
			break;
		case R.id.insu_back_btn:
			new InsuBackDialog(this).displayDlg();
			break;
		case R.id.insu_sale_btn:
			Intent insusaleIntent = new Intent(this, InsuSalePlanListActivity.class);
			startActivity(insusaleIntent);
			break;
		case R.id.insu_useless_btn:
			new InsuUselessDialog(this).displayDlg();
			break;
		case R.id.insu_query_btn: // 单证查询
			Intent insuqueryIntent = new Intent(this, InsuQueryActivity.class);
			startActivity(insuqueryIntent);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 这里的resultCode得到的就是Activity1.java中设的RESULT_OK=-1，data就是mIntent
		//modify按钮 启动的Activity 回调后
		if (SaleCheckActivity.MAIN_ACTIVITY == requestCode ) {// 
			if (RESULT_OK == resultCode) {
				exit();
			}
		}
	}
	
	public void exit(){
		MainApplication.getInstance().stopNetWorkListen();//停止监听网络情况
		MainApplication.getInstance().logout();
		finish();
	}
	
	/**
	 * 退出系统
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
    		DialogUtils.showChoiceAlertDlg(this,StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit), 
    				StringUtils.getStringFromValue(R.string.apsai_insu_manager_exit_msg),new OnAlertDlgSureBtn(){
    			public void OnSureBtn(){
    				if(userLevel==0x01){ //0x01=操作员
    					isToOrderCheck();
    				}else{
    					exit();
    				}
    			}
    		});
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
	
	/**
	 * 提示是否进行对账
	 */
	public void isToOrderCheck(){
		SaleCheckBean saleCheckBean  = saleCheckService.queryNoCheckedOrder();//查询是否有需要进行对账的记录
		if(saleCheckBean!=null&&saleCheckBean.getOperator_id()!=null&&
				!"".equals(saleCheckBean.getOperator_id())){//存在需要对账的记录
			//提示是否进行对账
			DialogUtils.showChoiceAlertDlg(this,StringUtils.getStringFromValue(R.string.apsai_sale_check_text), 
					StringUtils.getStringFromValue(R.string.apsai_sale_check_is_tocheck_text), 
					new OnAlertDlgSureBtn(){
				      public void OnSureBtn(){
						Intent saleCheckIntent = new Intent(MainActivity.this, SaleCheckActivity.class);
						startActivityForResult(saleCheckIntent,SaleCheckActivity.MAIN_ACTIVITY);
					}},new OnAlertDlgCancelBtn(){
						public void OnCancelBtn(){
						exit();
					}});
		}else{
			exit();
		}
		

	}
	
}

