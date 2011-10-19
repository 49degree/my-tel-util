package com.guanri.android.insurance.service;

import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.guanri.android.insurance.bean.OperatorRecordBean;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;
import com.guanri.android.lib.log.Logger;

public class OperatorRecordService {
	public static Logger logger = Logger.getLogger(OperatorRecordService.class);//日志对象
	public DBOperator dBOperator = null;
	private Context context ;
	/**
	 * @roseuid 4DF8330D0177
	 */
	public OperatorRecordService(Context context) {
		this.context = context;
		dBOperator = DBOperator.getInstance();
	}
	
	/**
	 * 根据工号ID查询员工对象
	 * @param operatorId
	 * @return
	 */
	public OperatorRecordBean queryOperatorInfo(String operatorId){
		HashMap<String,String> params = new HashMap<String,String>(1); 
		params.put("Operator_id=", operatorId);
		List<Object> operatorRecordList = dBOperator.queryBeanList(DBBean.TB_OPERATOR_RECORD, params);
		if(operatorRecordList!=null&&operatorRecordList.size()>0){
			return (OperatorRecordBean)operatorRecordList.get(0);
		}else{
			return null;
		}
	}
	
	
	/**
	 * 记录员工ID和姓名
	 * @param operatorId
	 * @return
	 */
	public void recordOperatorInfo(String operatorId,String operatorName){
		if(queryOperatorInfo(operatorId)==null){
			OperatorRecordBean operatorRecord = new OperatorRecordBean();
			operatorRecord.setOperator_id(operatorId);
			operatorRecord.setOperator_name(operatorName);
			dBOperator.insert(DBBean.TB_OPERATOR_RECORD, operatorRecord);
		}
	}
}
