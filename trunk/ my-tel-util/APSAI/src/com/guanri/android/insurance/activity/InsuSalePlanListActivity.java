package com.guanri.android.insurance.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.guanri.android.insurance.R;
import com.guanri.android.insurance.activity.base.ApsaiActivity;
import com.guanri.android.insurance.printer.PrinterSelectUtils;
import com.guanri.android.insurance.service.InsuOrderOperateService;
import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.StringUtils;

/**
 * 可销售保单方案列表
 * @author Administrator
 *
 */
public class InsuSalePlanListActivity extends ApsaiActivity{
   public InsuOrderOperateService insuOrderOperateDAO;
   public static Logger logger = Logger.getLogger(InsuSalePlanListActivity.class);// 日志对象;
   
   private ListView insuinfo_list = null;

   @Override
   public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    
		super.onCreate(savedInstanceState);
		setTitle(StringUtils.getStringFromValue(R.string.apsai_systeminfo_insulist));
		setContentView(R.layout.sale_plan_list_manager); 
		insuOrderOperateDAO = new InsuOrderOperateService(this);
		insuinfo_list = (ListView) findViewById(R.id.insuinfo_list);
		queryInsuList();
		
	}
   
   private void queryInsuList() {
	// TODO Auto-generated method stub
		String[] queryParam = {"CardCode","Planno","Name"};
		List<Map<String,String>> insuMap = insuOrderOperateDAO.queryInsuPlanList(queryParam);
       int[] to = new int[] {R.id.apsai_insu_manager_code, R.id.apsai_insu_manager_planno, R.id.apsai_insu_manager_name};
       SimpleAdapter recordAdapter = new SimpleAdapter(this, insuMap, R.layout.sale_plan_list_manager_item,queryParam, to);
		insuinfo_list.setAdapter(recordAdapter);
		insuinfo_list.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(final AdapterView<?> arg0, View arg1,final int arg2, long arg3){
				
//				PrinterSelectUtils.printCheckResult(InsuSalePlanListActivity.this, new PrinterSelectUtils.OnPrintConnectedListen(){
//					public void onPrintConnected(){
//
//					}
//				});
				
				//开始销售保单
			    String CardCode = ((Map<String,String>)arg0.getItemAtPosition(arg2)).get("CardCode");
			    String Planno = ((Map<String,String>)arg0.getItemAtPosition(arg2)).get("Planno");
				String EditName = insuOrderOperateDAO.getInsuEditName(CardCode, Planno);
				String PrtName = insuOrderOperateDAO.getInsuPrtName(CardCode, Planno);
				String InsuName = insuOrderOperateDAO.getInsuName(CardCode, Planno);
				
				Intent insuIntent = new Intent(InsuSalePlanListActivity.this, InsuSaleOrderActivity.class);
				
				insuIntent.putExtra("CardCode", CardCode);
				insuIntent.putExtra("Planno", Planno);
				insuIntent.putExtra("EditName", EditName);
				insuIntent.putExtra("PrtName", PrtName);
				insuIntent.putExtra("InsuName", InsuName);
				startActivity(insuIntent);

			}
		});
   }

   @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	  // insuOrderOperateDAO.dbOperater.release();
		super.onDestroy();
	}
}
