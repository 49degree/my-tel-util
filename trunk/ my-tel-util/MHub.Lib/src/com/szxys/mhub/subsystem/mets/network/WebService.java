package com.szxys.mhub.subsystem.mets.network;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Getupgotobed;
import com.szxys.mhub.subsystem.mets.dao.DrinkUrineDao;
import com.szxys.mhub.subsystem.mets.dao.GetupSleepDao;
import com.szxys.mhub.subsystem.mets.dao.SysConfigDao;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.MetsData;
import com.szxys.mhub.subsystem.mets.db.MetsDbHelper;
import com.szxys.mhub.subsystem.mets.db.DoctorAdviceInfo;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.GotobedGetup;
import com.szxys.mhub.subsystem.mets.db.MetsData;
import com.szxys.mhub.subsystem.mets.db.MetsDbHelper;
import com.szxys.mhub.subsystem.mets.db.SysConfig;
import com.szxys.mhub.subsystem.mets.db.UFR_Record;
import com.szxys.mhub.subsystem.mets.db.UrineRecord;
import com.szxys.mhub.subsystem.mets.db.MetsDbHelper;
import com.szxys.mhub.subsystem.mets.network.MakeCommitDataPack;

/**
 * Created by shiwen.chai.
 * User: Administrator
 * Date: 11-3-24
 * Time: 下午4:17
 */
public class WebService {
	public static final String ReqWebSvcCode_QuerySurvey = "12,1";					//询问服务器是否启用了问卷调查
	public static final String ReqWebSvcCode_DownSurveyTemplate = "12,2";				//下载问卷模板
	public static final String ReqWebSvcCode_CommitSurvey = "12,3";					//提交问卷调查结果
	public static final String ReqWebSvcCode_CommitUFR = "12,4"	;					//提交尿流率数据
	public static final String ReqWebSvcCode_CommitEmictionRecord = "12,5";       // 【提交排尿信息】
	public static final String ReqWebSvcCode_CommitDrinkRecord = "12,6";          // 【提交饮水量信息】
	public static final String ReqWebSvcCode_CommitGotoBedGetUpTime = "12,7";			//提交睡觉和起床时间
	public static final String ReqWebSvcCode_CommitUrinaryUrgency_16 = "12,8";		//提交尿急信息
	
    private String webServiceAddr = "";
    //private String userAccount = "";    
    String userAccount = "";

    private int timeout = 1000;

    private volatile static WebService inst;

    private WebService() {}

    public static WebService instance() {
        if (inst != null) return inst;
        synchronized (WebService.class) {
            inst = new WebService();
           // String patientID=SysConfigDao.getPatientID();
           // if(patientID!=null && patientID.trim().length()>0) {
            //	inst.setUserAccount(patientID);
            //	return inst;
            //}            
            return inst;
        }
    } 
    /**
     * set connect timeout, millisecond
     * @param timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * set webserivce url
     * @param webServiceAddr
     */
    synchronized public void setWebServiceAddr(String webServiceAddr) {
        this.webServiceAddr = webServiceAddr;
    }

    /**
     * set user account
     * @param userAccount
     */
    synchronized public void setUserAccount(int userId) {
    	String userAccount = String.valueOf(userId);
        if (this.userAccount == userAccount) return;
	    this.userAccount = userAccount;
    }

    /**
     * 关闭游标
     */
public static void closeCur(Cursor cursor)
{
	if (cursor!=null && !cursor.isClosed()) 
		cursor.close();

}

/**
 * 上传数据成功后修改排尿记录的c_IsUpload标志
 */
private static void updateIsUpload_urines(Context context)
{

	DrinkAndUrine du=new DrinkAndUrine();
	ContentValues values=new ContentValues();

	values.put("c_IsUpload", 1);	
	du.Update(context, values, "c_IsUpload=0 and c_Type=1", null);
}

/**
 * 上传数据成功后修改饮水记录的c_IsUpload标志
 */
private static void updateIsUpload_waters(Context context)
{

	DrinkAndUrine du=new DrinkAndUrine();
	ContentValues values=new ContentValues();
	values.put("c_IsUpload", 1);	
	du.Update(context, values, "c_IsUpload=0 and c_Type=0", null);
}


/**
 * 上传数据成功后修改尿流率表记录的c_IsUpload标志
 */
private static void updateIsUpload_ufrs(Context context)
{

	ContentValues values=new ContentValues();
	values.put("c_IsUpload", 1);	
	new UrineRecord().Update(context, values, "c_IsUpload=0", null);
}

/**
 * 上传数据成功后修改起床睡觉时间表记录的c_IsUpload标志
 */

private static void updateIsUpload_updown(Context context)
{

	ContentValues values=new ContentValues();
	values.put("c_IsUpload", 1);	
	new GotobedGetup().Update(context, values, "c_IsUpload=0", null);
}


 
/**
 * 上传成功后，更改数据库中对应表记录标志
 */
private static void updateIsUpload(Context context,String flag)
{
if( flag == WebService.ReqWebSvcCode_CommitGotoBedGetUpTime)
{
	updateIsUpload_updown(context);
	return;
}
	
if( flag == WebService.ReqWebSvcCode_CommitEmictionRecord) // 【提交排尿信息】
{
	updateIsUpload_updown(context);
	return;
}
if( flag == WebService.ReqWebSvcCode_CommitDrinkRecord)
{
	updateIsUpload_updown(context);
	return;
}
if( flag == WebService.ReqWebSvcCode_CommitUFR)
{
	updateIsUpload_updown(context);
	return;
}
if( flag == WebService.ReqWebSvcCode_CommitSurvey)//提交问卷调查结果
{
	updateIsUpload_updown(context);
	return;
}
if( flag == WebService.ReqWebSvcCode_CommitUrinaryUrgency_16)//提交尿急信息
{
	updateIsUpload_updown(context);
	return;
}

}

/**
 * 上传尿流率信息信息
 */
public static String  strUfrsUnupload(Context context)
{
ArrayList<String[]> ufrecords=new ArrayList<String[]>();
String[] columns={"c_DateTime","c_Duration","c_MeanFlow","c_Q90","c_PeakFlow","c_2SecFlow","c_FlowTime",
		"c_VoidingTime","c_T90","c_TimeToPeak","c_VoidVolume","c_Units","c_StartPos","c_EndPos","c_Id"}; 
// VaryVal=? tb_UFR_Record c_orgData  Proportion=? 另外计算

Cursor cursor=new UrineRecord().Select(context, columns, "c_IsUpload=0", null, null, null, null);	
//Log.d("mylog", "CommmonInfer count="+cursor.getCount());
if (cursor!=null && cursor.getCount()>0) {
	
	DrinkAndUrine objDrinkAndUrine=new DrinkAndUrine();
	UFR_Record objUfr_Record=new UFR_Record();
	for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()){
		String[] urineRecords=new String[16];
		urineRecords[0]=cursor.getString(0);
		urineRecords[1]=cursor.getString(1);
		urineRecords[2]=objUfr_Record.getOrgDataByUrineId(context, cursor.getString(14));//VaryVal=?
		urineRecords[3]=cursor.getString(2);
		urineRecords[4]=cursor.getString(3);
		urineRecords[5]=cursor.getString(4);
		urineRecords[6]=cursor.getString(5);
		urineRecords[7]=cursor.getString(6);
		urineRecords[8]=cursor.getString(7);
		urineRecords[9]=cursor.getString(8);
		urineRecords[10]=cursor.getString(9);
		urineRecords[11]=cursor.getString(10);
		urineRecords[12]=cursor.getString(11);
		//urineRecords[13]=objDrinkAndUrine.getProportionByUrineId(context, cursor.getString(14));//Proportion=?
		urineRecords[13]= "1";
		urineRecords[14]=cursor.getString(12);
		urineRecords[15]=cursor.getString(13);
		
		ufrecords.add(urineRecords);
	}			
}
closeCur(cursor);
//Log.i("mylog","ufrecords:"+ufrecords.size());
for(int i=0;i<ufrecords.size();i++)
{
	String str="record:";
	for(int j=0;j<ufrecords.get(i).length;j++)
		str=str+ufrecords.get(i)[j]+"\t"+"~~";
	System.out.println(str);
}
	
// Log.i("mylog","ufrecords:"+ufrecords.size());
 String uploadUrines=MakeCommitDataPack.makeCommitUFR(inst.userAccount, ufrecords);
 System.out.println("uploadUfr:"+uploadUrines);
 return uploadUrines;

}


/**
 * 上传起床睡觉信息信息
 */
public static String strUpDown()
{
ArrayList<String[]> updowns=new ArrayList<String[]>();
ArrayList<Getupgotobed> upgotobed = GetupSleepDao.getUploadTimes();  //return ArrayList<Getupgotobed>
if ( !upgotobed.isEmpty() ) 
{
	for(int i=0; i < upgotobed.size() ; i++ ) 
	{
		String[] updown={upgotobed.get(i).getC_Date(),upgotobed.get(i).getC_GotoBedTime(),upgotobed.get(i).getC_GetUpTime() };
		updowns.add(updown);
		
	}
}

String strUpdowns=MakeCommitDataPack.makeCommitDownAndUp(inst.userAccount, updowns);
return strUpdowns;
}

/**
 * 上传饮水信息
 */
public static String strWartersUnupload()
{
ArrayList<String[]> waters=new ArrayList<String[]> ();
ArrayList<Drinkandurine> updrinkandurine = DrinkUrineDao.getUploadDrinkInfo();  
if ( !updrinkandurine.isEmpty() ) 
{
	for(int i=0; i < updrinkandurine.size() ; i++ ) 
	{
		String[] updown={ String.valueOf(updrinkandurine.get(i).getC_Units()),updrinkandurine.get(i).getC_DateTime(),String.valueOf(updrinkandurine.get(i).getC_Quantity()),String.valueOf(updrinkandurine.get(i).getC_Proportion()) };
		waters.add(updown);
		
	}
}

 String uploadWaters=MakeCommitDataPack.makeCommitUrineOrDrink(inst.userAccount,waters);
 
 return uploadWaters;
}


/**
 * 上传排尿信息
 */
public static String  strUrinesUnupload()
{

	ArrayList<String[]> urines=new ArrayList<String[]> ();
	ArrayList<Drinkandurine> updrinkandurine = DrinkUrineDao.getUploadUrineInfo();  
	if ( !updrinkandurine.isEmpty() ) 
	{
		for(int i=0; i < updrinkandurine.size() ; i++ ) 
		{
			String[] updown={ String.valueOf(updrinkandurine.get(i).getC_Units()),updrinkandurine.get(i).getC_DateTime(),String.valueOf(updrinkandurine.get(i).getC_Quantity()),String.valueOf(updrinkandurine.get(i).getC_Proportion()) };
			urines.add(updown);
			
		}
	}

	 String uploadUrines=MakeCommitDataPack.makeCommitUrineOrDrink(inst.userAccount,urines);
	 
	 return uploadUrines;	

}

/**
 * 上传尿急信息
 */
public static String  strEmergenturinesUnupload()
{

	ArrayList<String[]> urines=new ArrayList<String[]> ();	

	ArrayList<Drinkandurine> updrinkandurine = DrinkUrineDao.getUploadEmergenturines();  
	if ( !updrinkandurine.isEmpty() ) 
	{
		for(int i=0; i < updrinkandurine.size() ; i++ ) 
		{
			String[] updown={ String.valueOf(updrinkandurine.get(i).getC_Units()),updrinkandurine.get(i).getC_DateTime(),String.valueOf(updrinkandurine.get(i).getC_Quantity()),String.valueOf(updrinkandurine.get(i).getC_Proportion()) };
			urines.add(updown);
			
		}
	}

	 String uploadUrines=MakeCommitDataPack.makeCommitUrineOrDrink(inst.userAccount,urines);
	 
	 return uploadUrines;	

}

/**
 * 上传尿失禁信息
 */
public static String  strLossurinesUnupload(Context context)
{

	ArrayList<String[]> urines=new ArrayList<String[]> ();
	
	ArrayList<Drinkandurine> updrinkandurine = DrinkUrineDao.getUploadLossurines();  
	if ( !updrinkandurine.isEmpty() ) 
	{
		for(int i=0; i < updrinkandurine.size() ; i++ ) 
		{
			String[] updown={ String.valueOf(updrinkandurine.get(i).getC_Units()),updrinkandurine.get(i).getC_DateTime(),String.valueOf(updrinkandurine.get(i).getC_Quantity()),String.valueOf(updrinkandurine.get(i).getC_Proportion()) };
			urines.add(updown);
			
		}
	}

	 String uploadUrines=MakeCommitDataPack.makeCommitUrineOrDrink(inst.userAccount,urines);
	 
	 return uploadUrines;	

}



}
