package com.guanri.android.insurance.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

import com.guanri.android.insurance.bean.OperateLogBean;
import com.guanri.android.insurance.command.CommandControl;
import com.guanri.android.insurance.db.DBBean;
import com.guanri.android.insurance.db.DBOperator;

/**
 * 操作日志管理类
 * @author 
 *
 */
public class OperateLogService {
	public DBOperator dboperater = null;
	private Context context = null;
	private CommandControl commandControl = null;
	
	public OperateLogService(Context context){
			this.commandControl = CommandControl.getInstance();
			this.context = context;
			dboperater = DBOperator.getInstance();
	}
	
	/**
	 * 增加操作日志
	 * @param operateLogBean 操作日志Bean
	 * @return
	 */
	public boolean addLog(OperateLogBean operateLogBean){
		
		if(dboperater.insert(DBBean.TB_OPERATE_LOG, operateLogBean) >-1 ){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 查询
	 * @return
	 */
	public List<Object> getLogList(Map<String,String> params){
		List<Object> operatelogBeanlist = null;
		operatelogBeanlist = dboperater.queryBeanList(DBBean.TB_OPERATE_LOG, params);
//		for (int i = 0; i < objectlist.size(); i++) {
//			operatelogBeanlist.add((OperateLogBean)objectlist.get(i));
//		}
		
		return operatelogBeanlist;
	}
	
	/**
	 * 查询
	 * @return
	 */
	public OperateLogBean viewLog(int logId){
		List<Object> objectlist = new ArrayList<Object>();
		
		Map<String, String> params = new HashMap<String,String>();
		params.put("Log_id=", ""+logId);
		
		objectlist = dboperater.queryBeanList(DBBean.TB_OPERATE_LOG, params);
		return (OperateLogBean)objectlist.get(0) ;
	}
	
	/**
	 * 逻辑删除日志
	 * @return
	 */
	public int deleteLog(int logId){
		long insertRow = 0 ;
		Map<String, String> params = new HashMap<String,String>();
		params.put("Log_id=", ""+logId);
		
		ContentValues cv =new ContentValues();
        cv.put("Is_del", "1");
        
        insertRow = dboperater.update(DBBean.TB_OPERATE_LOG, cv, params);
        
		return (int) insertRow;
	}
	
	/**
	 * 获得日志总行数
	 * @return
	 */
	public int getLogRowNum(){
		Map<String, String> params = new HashMap<String,String>();
		params.put("Is_del=", "0");
		return dboperater.queryRowNum(DBBean.TB_OPERATE_LOG, params);
	}
	
}
