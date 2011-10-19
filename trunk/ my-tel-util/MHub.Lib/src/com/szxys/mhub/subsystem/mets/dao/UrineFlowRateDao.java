package com.szxys.mhub.subsystem.mets.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.os.SystemClock;
import android.util.Log;

import com.szxys.mhub.app.MhubApplication;
import com.szxys.mhub.subsystem.mets.bean.Drinkandurine;
import com.szxys.mhub.subsystem.mets.bean.Sysconfig;
import com.szxys.mhub.subsystem.mets.bean.UFRrecord;
import com.szxys.mhub.subsystem.mets.bean.Urineflowrate;
import com.szxys.mhub.subsystem.mets.bean.Urineintervaltime;
import com.szxys.mhub.subsystem.mets.bean.Urinerecord;
import com.szxys.mhub.subsystem.mets.bluetooth.IBtComm;
import com.szxys.mhub.subsystem.mets.c.FlowrateAlgorithm;
import com.szxys.mhub.subsystem.mets.c.FlowrateAlgorithm.FlowrateParam;
import com.szxys.mhub.subsystem.mets.db.DrinkAndUrine;
import com.szxys.mhub.subsystem.mets.db.SysConfig;
import com.szxys.mhub.subsystem.mets.db.UFR_Record;
import com.szxys.mhub.subsystem.mets.db.UrineIntervalTime;
import com.szxys.mhub.subsystem.mets.db.UrineRecord;

public class UrineFlowRateDao implements IBtComm {

	@Override
	public boolean load() {
		// when the time to communicate with the collector, this method is
		// invoke to obtain the data
		Sysconfig objSysconfig = SysConfigDao.getSysconfigObj();
		boolean hasLoadedData = false;
		if (objSysconfig != null) {
			IBtComm.param.timerTime = new Date();// TimeUtils.convertToDate("yyyy-MM-dd HH:mm:ss");
			IBtComm.param.waitTime = 600;
			IBtComm.param.mode = 1;// 完成后变成0
			IBtComm.param.intervalUnit = 0;
			IBtComm.param.timerInterval = (byte)objSysconfig.getC_SendDtInterval();
			IBtComm.param.maxDuration = (short)objSysconfig.getC_MaxDuration();
			IBtComm.param.voiceSwitch = 1;
			IBtComm.param.voiceType = 2052;
			IBtComm.param.cupWeight = (short)objSysconfig.getC_MeasuringCupWeight();
			hasLoadedData = true;
		}
		return hasLoadedData;
	}

	@Override
	public boolean save() {
		// when the connunication is completed, this method is invode to save
		// the data
		boolean isSaved = false;
		//Sysconfig objSysconfig = new Sysconfig();
		ContentValues values = new ContentValues();

//		values.put("c_Hospital", objSysconfig.getC_Hospital());
//		values.put("c_MobileId", objSysconfig.getC_MobileId());
//		values.put("c_MobileName", objSysconfig.getC_MobileName());
//		values.put("c_CollectorId", objSysconfig.getC_CollectorId());
//		values.put("c_CollectorName", objSysconfig.getC_CollectorName());
//		values.put("c_PatientNo", objSysconfig.getC_PatientNo());
//		values.put("c_PatientName", objSysconfig.getC_PatientName());
//		values.put("c_DoctorsNo", objSysconfig.getC_DoctorsNo());
//		values.put("c_DoctorsName", objSysconfig.getC_DoctorsName());
		values.put("c_CollectDtInterval", param.timerInterval);
		values.put("c_MeasuringCupWeight", param.cupWeight);
//		values.put("c_TimeInitialLead", objSysconfig.getC_TimeInitialLead());
//		values.put("c_RebootTimer", objSysconfig.getC_RebootTimer());
//		values.put("c_GetUpAlarm", objSysconfig.getC_GetUpAlarm());
//		values.put("c_GotoBedAlarm", objSysconfig.getC_GotoBedAlarm());
//		values.put("c_HaveSpecificGravity", objSysconfig.getC_HaveSpecificGravity());
		values.put("c_MaxDuration", param.maxDuration);
		values.put("c_NoDataTime", 600);
		values.put("c_IsRecycling", 0);
//		values.put("c_RationDateTime", new SimpleDateFormat("yyyy-MM-dd").format(param.timerTime));// yy-mm-dd hh:mm:ss
//		values.put("c_RationGuDt", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		int result = SysConfig.Update(MhubApplication.getInstance(), values, null, null);
		if (result > 0) {
			isSaved = true;
		}
		return isSaved;
	}

	@Override
	public void resultVoidingDiary(ArrayList<VoidingDiary> diarys) {
		// save voiding diary data
		if (diarys != null && diarys.size() > 0) {
			Drinkandurine objUrine = null;
			for (int i = 0; i < diarys.size(); i++) {
				objUrine = new Drinkandurine();
				float quantity = diarys.get(i).quantity;
				objUrine.setC_Type(1);
				objUrine.setC_DateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(diarys.get(i).date));
				objUrine.setC_Quantity(quantity);
				objUrine.setC_UniqueId(new SimpleDateFormat("yyyyMMddHHmmss").format(diarys.get(i).date)+SystemClock.uptimeMillis());
				long result = DrinkAndUrine.Insert(MhubApplication.getInstance(), objUrine);
				if (result > 0) {
					Log.d("UrineFlowrateDao", "Insert Quantity=" + quantity);
				} else {
					Log.d("UrineFlowrateDao", "Failed insert Quantity="	+ quantity);
				}
			}
		}

	}

	@Override
	public void resultUfoFlow(ArrayList<UfoFlow> ufoFlows) {
		// save ufoflow data
		Urineintervaltime objInterval = new Urineintervaltime();
		UFRrecord objUFR = new UFRrecord();// c_OrgData c_QuantityData
											// c_RateData
		Urinerecord objUrinerecord = new Urinerecord(); // c_Q90 c_2SecFlow
														// c_PeakFlow
														// c_VoidingTime
														// c_FlowTime
		if (ufoFlows != null && ufoFlows.size() > 0) {
			FlowrateParam param = null;
			for (int i = 0; i < ufoFlows.size(); i++) {
				param = new FlowrateParam();
				float[] orgVolume = ufoFlows.get(i).orgVolume;
				int fwcFlag = FlowrateAlgorithm.FWCFLAG_SMOOTH|FlowrateAlgorithm.FWCFLAG_FILTER;
				int smoothDegree = 1;
				Date ufoDate = ufoFlows.get(i).date;
				String dtFlowRate = "";
				float urineQuantity = 0;
				if (!UrineRecord.isExistDatetime(MhubApplication.getInstance(),	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ufoDate))) {
					if (FlowrateAlgorithm.FlowRateCalc(orgVolume, fwcFlag, smoothDegree, param)) {
						// 记录尿流信息
						//dtFlowRate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(param.date);
						urineQuantity = param.fMicturateVolume;
						objUrinerecord.setC_DateTime(dtFlowRate); // 开始时间
						objUrinerecord.setC_Duration(160); // 采集时长【设备发放时传入】外面接口
						objUrinerecord.setC_MeanFlow(param.fAvgFlowrate); // 平均尿流率
						// model_urineFlowRate.c_Q90=0; //90%流量的平均尿流率
						objUrinerecord.setC_PeakFlow(param.fMaxFlowrate); // 最大尿流率
						objUrinerecord.setC_2SecFlow(param.f2SecFlowrate);// FLOAT,
																			// //
																			// 2秒时的尿流率
						objUrinerecord.setC_VoidingTime(param.fMicturateDuration); // 排尿时间
						objUrinerecord.setC_FlowTime(param.fFlowDuration); // 尿流时间
						// model_urineFlowRate.c_T90=0; //90%流量的时间
						objUrinerecord.setC_TimeToPeak(param.fMaxFlowrateTime); // 达到最大尿流率的时间
						objUrinerecord.setC_VoidVolume(urineQuantity); // 该次排尿的总量
						objUrinerecord.setC_StartPos(param.nMicturateStartPos);
						objUrinerecord.setC_EndPos(param.nMicturateEndPos);
						long rowOfUrineRecord = UrineRecord.Insert(MhubApplication.getInstance(), objUrinerecord);
						Log.d("UrineFlowrateDao", "Generated UfrId="+rowOfUrineRecord);
						if (rowOfUrineRecord > 0) {
							// 尿流率起始点位置
							int[] urineIntervalTimes = param.intermission;
							int pairCount = param.intermission.length / 2;
							for (int x = 0; x < pairCount; x++) {
								objInterval.setC_BeginPos(urineIntervalTimes[i * 2] * 0.1f);
								objInterval.setC_EndPos(urineIntervalTimes[i * 2 + 1] * 0.1f);
								objInterval.setC_UrineId((int) rowOfUrineRecord);
								long result_urineInterval = UrineIntervalTime.Insert(MhubApplication.getInstance(),	objInterval);
								Log.d("UrineFlowrateDao","Insert urineInterval="	+ result_urineInterval);
							}
							// 记录排尿
							Drinkandurine objInAndOut = new Drinkandurine();
							objInAndOut.setC_DateTime(dtFlowRate);
							objInAndOut.setC_Quantity(urineQuantity);
							objInAndOut.setC_Type(1);
							objInAndOut.setC_UfrId((int) rowOfUrineRecord);
							//objInAndOut.setC_UniqueId(new SimpleDateFormat("yyyyMMddHHmmss").format(param.date)	+ SystemClock.uptimeMillis());
							long result_UrineQuantity = DrinkAndUrine.Insert(MhubApplication.getInstance(), objInAndOut);
							Log.d("UrineFlowrateDao", "Insert UrineCid=" + result_UrineQuantity + " Quantity=" + urineQuantity);
							// 尿流率起始点位置
							float[] ufrVolume = param.volumeData;
							float[] ufrFlowrate = param.flowrateData;
							String orgDataString = "";
							String volumnString = "";
							String ufrString = "";
							for (int j = 0; j < orgVolume.length; j++) {
								orgDataString += orgVolume[j] + ",";
							}
							for (int a = 0; a < ufrVolume.length; a++) {
								volumnString += ufrVolume[a] + ",";
							// }
							// for (int y = 0; y < ufrFlowrate.length; y++)
							// {
								ufrString += ufrFlowrate[a] + ",";
							}
							objUFR.setC_OrgData(orgDataString.substring(0,orgVolume.length - 1));
							objUFR.setC_FinalData(orgDataString.substring(0,orgVolume.length - 1));
							objUFR.setC_QuantityData(volumnString.substring(0,orgVolume.length - 1));
							objUFR.setC_RateData(ufrString.substring(0,ufrFlowrate.length - 1));
							objUFR.setC_UrineId((int) rowOfUrineRecord);
							long result_ufr = UFR_Record.Insert(MhubApplication.getInstance(), objUFR);
							Log.d("UrineFlowrateDao", "Insert UFR_Record Cid=" + result_ufr);
						}
					}
				}

			}
		}

	}

	@Override
	public void communicateResult(boolean succeed) {
		// TODO Auto-generated method stub

	}
	
	//尿流率 【此处仅负责提供对象集合而不进行字串组合】
	/* WebService请求码：ReqWebSvcCode_CommitUFR
	 * 请求包数据格式：
	 * <CommitUFR  PatientId="" >
	 *    <Record StartTime=”” Duration=”” VaryVal=”” MeanFlow=”” Q90=”” PeakFlow=”” 2SecFlow=”” FlowTime=”” StreamTime=”” T90=”” TimeToPeak=”” VoidVolume=”” AmountUnit=”” Proportion=””>
	 *        <IntervalTime BeginPos=”” EndPos=””/>
	 *    </Record>
	 * </CommitUFR>
	 */
	public static ArrayList<Urineflowrate> getUploadUFR() {
		ArrayList<Urineflowrate> UFR_list=new ArrayList<Urineflowrate>();
		ArrayList<Urinerecord> listOfUrinerecord=UrineRecord.getUploadUrinerecords(MhubApplication.getInstance());
		if (listOfUrinerecord!=null && !listOfUrinerecord.isEmpty()) {
			//String patientId=SysConfigDao.getPatientID(); //病人ID
			Urineflowrate objUFR=null;
			Urinerecord objUrinerecord=null;
			for (int i = 0; i < listOfUrinerecord.size(); i++) {
				objUrinerecord=listOfUrinerecord.get(i);
				if (objUrinerecord!=null) {
					objUFR=new Urineflowrate();
					objUFR.setStartTime(objUrinerecord.getC_DateTime());
					objUFR.setDuration(objUrinerecord.getC_Duration());
					int ufrId=objUrinerecord.getC_Id();
					if (ufrId>0) {
						UFRrecord objUfRrecord=UFR_Record.getUFRbyUfrId(MhubApplication.getInstance(), String.valueOf(ufrId));
						if (objUfRrecord!=null) {
							objUFR.setVaryVal(objUfRrecord.getC_OrgData()); //每秒的尿量
						}						
					}					
					objUFR.setMeanFlow(objUrinerecord.getC_MeanFlow());
					objUFR.setQ90(objUrinerecord.getC_Q90());
					objUFR.setPeakFlow(objUrinerecord.getC_PeakFlow());
					objUFR.setTwoSecondFlow(objUrinerecord.getC_2SecFlow());
					objUFR.setFlowTime(objUrinerecord.getC_FlowTime());
					objUFR.setStreamTime(objUrinerecord.getC_VoidingTime());
					objUFR.setT90(objUrinerecord.getC_T90());
					objUFR.setTimeToPeak(objUrinerecord.getC_TimeToPeak());
					objUFR.setVoidVolume(objUrinerecord.getC_VoidVolume());
					objUFR.setAmountUnit(String.valueOf(objUrinerecord.getC_Units()));
//					objUFR.setProportion(proportion); //尿比重
					objUFR.setBeginPos(objUrinerecord.getC_StartPos());
					objUFR.setEndPos(objUrinerecord.getC_EndPos());
				}				
				
				UFR_list.add(objUFR);
			}
			
		}
		return UFR_list;
	}

}
