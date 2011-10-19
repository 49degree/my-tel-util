package com.szxys.mhub.subsystem.virtual;

import java.util.ArrayList;
import java.util.List;

import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.IPlatFormInterface;

/**
 * 提供的异常信息的接口操作
 * @author Administrator
 *
 */
public class EMSRequestData {
	private  IPlatFormInterface pfInterface = null;
	private  ISubSystemCallBack ssCallBack = null;
	private ErrorMessageSqliteDB db=null;
	private static EMSRequestData mInstatce=null;
	public EMSRequestData(IPlatFormInterface pf, ISubSystemCallBack sscb)
	{
		pfInterface = pf;
		ssCallBack = sscb;
		db=new ErrorMessageSqliteDB();
	}
	/**
	 * 获取所有的异常信息
	 * @return
	 */
	public List<ErrorMessageEntity> searchErrorMessageList()
	{
		return db.findErrorMessage();
	}
	
	@SuppressWarnings("unchecked")
	public void delErrorMessageById(Object obj)
	{
		db.delErrorMessageByID((ArrayList<Integer>)obj);
	}
	public void saveErrorMessage(ErrorMessageEntity entity)
	{
		db.saveErrorInfo(entity);
	}
	
	

}
