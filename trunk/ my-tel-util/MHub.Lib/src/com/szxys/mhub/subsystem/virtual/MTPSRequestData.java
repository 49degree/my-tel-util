package com.szxys.mhub.subsystem.virtual;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.szxys.mhub.base.communication.webservice.WebUtils;
import com.szxys.mhub.base.manager.Consts;
import com.szxys.mhub.bizinterfaces.ISubSystemCallBack;
import com.szxys.mhub.interfaces.IPlatFormInterface;
import com.szxys.mhub.interfaces.Platform;
import com.szxys.mhub.interfaces.RequestIdentifying;

/**
 * 监护参数：数据请求包
 * 
 * @author 张丹
 * 
 */
public class MTPSRequestData {
	private final static MTPSDB db= new MTPSDB();
	private static  MTPSRequestData mtps=null;
	/*private IPlatFormInterface pfInterface = null;
	private ISubSystemCallBack ssCallBack = null;*/
	//MTPSSubSystemCallBack mtpsSSCallBack = null;
/*	public MTPSRequestData(IPlatFormInterface pf, ISubSystemCallBack sscb) {
		pfInterface = pf;
		ssCallBack = sscb;
		//mtpsSSCallBack = new MTPSSubSystemCallBack(ssCallBack);

		db = new MTPSDB();
		*//**
		 * 因为监护参数表是有默认数据的、所以在这写了这个增加的方法。 用户ID我在这是随便插入的。插入默认数据后、这段是应删除的。
		 *//*
		for (int i = 10; i <=23; i++) {
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date da=new Date();
			String time=df.format(da);
			MTPSEntity entity = new MTPSEntity();
			entity.userID = i;
			entity.appID = i;
			entity.downTime =time;
			entity.isChange =0;
			db.save(entity);
		}

	}*/
	private MTPSRequestData()
	{
		
	}
	static public MTPSRequestData getInstance()
	{
		if(mtps==null)
		{
			mtps=new MTPSRequestData();
		}
		return mtps;
	}
	

	/**
	 * 检查指定子业务是否存在需要更新的监护参数
	 * 
	 * @param subSystemID
	 * @return
	 */
	public boolean CheckChange(int subSystemID) {
		return db.cachMTPSByAppID(subSystemID);
	}

	/**
	 * 发送数据请求
	 * 
	 * @return
	 */
	public  void MointoringParametersRequest(IPlatFormInterface pf,int nUserID) {
		List<MTPSEntity> parameters = db.cachMTPSEntity(0);
		if (parameters.size() > 0) {
			for(int i=0;i<parameters.size();i++)
			{
			RequestIdentifying reqIdentifying = new RequestIdentifying();
			reqIdentifying.userID = nUserID;
			reqIdentifying.subSystemID = Platform.SUBBIZ_VIRTUAL;
			byte[] bDownTime = restoreDate(parameters.get(0).downTime);
			byte[] bUserId = WebUtils.toLH(reqIdentifying.userID);
			byte[] sendData = new byte[4 + bDownTime.length];
			// bUserId复制给sendData从0开始复制,放置的起始位置是前面4位.复制的长度是bUserId的length
			System.arraycopy(bUserId, 0, sendData, 0, bUserId.length);
			System.arraycopy(bDownTime, 0, sendData, bUserId.length,
					bDownTime.length);
			pf.send(reqIdentifying, 0, Platform.NETDATA_REALTIME,
					Consts.CMD_WEB_GET_MONITORING_PARAMETERS, 0, sendData,sendData.length);
		    	  }
		}
	}
	/**
	 * 各业务子系统下载数据成功后要调用的方法
	 * @param IsChange
	 * @param appID
	 * @param downTime
	 */
	public void UpdateDownTimeByAppID(int IsChange, int appID, String downTime)
	{
		db.UpdateDownTimeByAppID(IsChange, appID, downTime);
	}
      /**
  	 * 形如yyyy-MM-dd HH:mm:ss的格式时间串转换成/Date(1304403691652+0800)/
  	 */
  	public static byte[] restoreDate(String src) {
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		Date date = null;
		try {
			date = sdf.parse(src);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return WebUtils.longReverseOrder(date.getTime()/1000);		
  	}
	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}
}