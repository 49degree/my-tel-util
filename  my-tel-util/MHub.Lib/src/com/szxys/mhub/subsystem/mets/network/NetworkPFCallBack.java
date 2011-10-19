package com.szxys.mhub.subsystem.mets.network;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.szxys.mhub.interfaces.IPlatFormCallBack;

import com.szxys.mhub.interfaces.RequestIdentifying;
import com.szxys.mhub.subsystem.mets.business.MetsConstant;



/**
 * WEBSERVICE 上传，下载回调类wangjinchun
 * 
 * 子业务生成并设置给平台，由平台调用，把数据回调给子业务。与IPlatFormInterface相呼应
 * @author Administrator
 *
 */
public class NetworkPFCallBack implements IPlatFormCallBack {
	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStarted(RequestIdentifying reqIdentifying, long lErrorCode){
		return 0;
	}

	/**
	 * @param reqIdentifying
	 * @param lErrorCode
	 * @return long
	 */
	public long onStopped(RequestIdentifying reqIdentifying, long lErrorCode){
		return 0;
	}

	/**
	 * @param reqIdentifying
	 * @param sendIndex
	 * @param lErrorCode
	 * @return long
	 */
	public long onSent(RequestIdentifying reqIdentifying, int sendIndex,
			long lErrorCode){
		return 0;
	}
	/**
	 * @param lChannel
	 * @param mainCmd
	 * @param subCmd
	 * @param byRecvData
	 * @param length
	 * @return long
	 */
	public long onReceived(int lChannel, int mainCmd, int subCmd,
			byte[] byRecvData, int length){
		switch(mainCmd){
		case MetsConstant.CTRL_SEND_URINE:
		       try {
		    	   String recvData = new String(byRecvData, "UTF-8");
		    	   Log.i("mylog","back data:"+recvData);
		        } catch (UnsupportedEncodingException e) {
		            e.printStackTrace();
		            //throw new MetsException(RetCode.RET_CODE_UnknownReason, e.getMessage());
		        }			
			break;
		case MetsConstant.CTRL_SEND_WATER:

			break;
		default:
			break;
		}
		return 0;
	}
	
	@Override
	public long onMessage(int errorCode, int mainCmd, int subCmd,
			Object obj) {
		// TODO Auto-generated method stub
		return 0;
	}
}
