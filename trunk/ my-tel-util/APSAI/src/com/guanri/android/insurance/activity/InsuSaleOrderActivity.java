package com.guanri.android.insurance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.bean.InsuPlanBean;
import com.guanri.android.insurance.bean.InsuPrintBean;
import com.guanri.android.insurance.bean.InsuViewPlanBean;
import com.guanri.android.insurance.bean.InsuranceBean;
import com.guanri.android.insurance.common.DialogUtils;
import com.guanri.android.insurance.common.DialogUtils.OnAlertDlgSureBtn;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.insurance.insuplan.PareFileToObject;
import com.guanri.android.insurance.view.InsuViewByModeFile;
import com.guanri.android.lib.log.Logger;

/**
 * 单证管理界面
 * @author Administrator
 *
 */
public class InsuSaleOrderActivity  extends ApsaiActivity {
	public static Logger logger = Logger.getLogger(InsuSaleOrderActivity.class);// 日志对象;

	private String CardCode = null;
	private String Planno = null;
	private String EditName = null;
	private String PrtName = null;
	private String InsuName = null;
	
	TextView spsai_define_title_bar_text = null;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		CardCode = intent.getStringExtra("CardCode");
		Planno = intent.getStringExtra("Planno");
		EditName = intent.getStringExtra("EditName");
		PrtName = intent.getStringExtra("PrtName");
		InsuName = intent.getStringExtra("InsuName");
		//构造业务方案配置对象
		logger.debug(EditName+":"+CardCode+":"+PrtName);
		InsuViewPlanBean insuViewPlanBean = PareFileToObject.pareInsuViewPlanBean(EditName);
		InsuPlanBean insuPlanBean = PareFileToObject.pareInsuPlanBean(CardCode+".txt");
		InsuPrintBean insuPrintBean = PareFileToObject.pareInsuPrintBean(PrtName);
		if(insuPrintBean==null){
			DialogUtils.showMessageAlertDlg(this, "失败提示", "解析打印模板文件失败！", new OnAlertDlgSureBtn(){
				public void OnSureBtn(){
					finish();
				}
			});
		}
		
		
		InsuranceBean insuranceBean = new InsuranceBean();
		insuranceBean.setInsuPlanBean(insuPlanBean);
		insuranceBean.setInsuPrintBean(insuPrintBean);
		insuranceBean.setInsuViewPlanBean(insuViewPlanBean);
		
		//setTitle(InsuName);
		
		InsuViewByModeFile createViewByModeFile = new InsuViewByModeFile(this);
		createViewByModeFile.setModeInfo(insuranceBean);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(createViewByModeFile);	
	    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.apsai_define_title_bar);   
	    spsai_define_title_bar_text = (TextView)this.findViewById(R.id.spsai_define_title_bar_text);
	    spsai_define_title_bar_text.setText(InsuName);
	    
	}
   
}
