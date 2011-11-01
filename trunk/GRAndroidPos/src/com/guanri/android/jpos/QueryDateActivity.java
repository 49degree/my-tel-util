package com.guanri.android.jpos;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.guanri.android.jpos.bean.SaleDataLogBean;
import com.guanri.android.jpos.db.DBBean;
import com.guanri.android.jpos.db.DBOperator;
import com.guanri.android.lib.log.Logger;

public class QueryDateActivity extends Activity {
	private EditText edtdb;
	Logger logger = new Logger(QueryDateActivity.class);
	DBOperator dboperator = DBOperator.getInstance();
	StringBuffer strbuf = new StringBuffer();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jpos_dbcontent);
		querydata();
		edtdb = (EditText) findViewById(R.id.db_edt);
		edtdb.setText(strbuf.toString());
	}
	
	
	public void querydata(){
		List<Object> datalist =dboperator.queryBeanList(DBBean.TB_SALE_RECORD, null);
		if((datalist!= null)&&(datalist.size()>0)){
		for (int i = 0; i < datalist.size(); i++) {
			SaleDataLogBean saledata= (SaleDataLogBean)datalist.get(i);
			strbuf.append("主账号:"+saledata.CardNo);
			strbuf.append("流水号:"+saledata.PosNo);
			strbuf.append("日期:"+saledata.DataStr);
			strbuf.append("时间:"+saledata.TimeStr);
			strbuf.append("金额:"+saledata.TransactionMoney);
			strbuf.append("订单号:"+saledata.OrderNo);
			strbuf.append("参考号:"+saledata.SearchNo);
			strbuf.append("授权号:"+saledata.AuthorizationNo);
			strbuf.append("批次号:"+saledata.BatchNo);	
			strbuf.append("PosMAC:"+saledata.PosMac);
			strbuf.append("状态:"+ saledata.TransactionState);
			strbuf.append("\n");
		}
		}
		else{
			strbuf.append("未找到数据");
			logger.debug("查询数据为空------------------------------------");
		}
	}
}
