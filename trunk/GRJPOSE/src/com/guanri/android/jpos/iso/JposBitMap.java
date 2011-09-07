package com.guanri.android.jpos.iso;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.TreeMap;

import com.guanri.android.jpos.constant.JposConstant.MessageTypeDefineUnionpay;
import com.guanri.android.jpos.iso.unionpay.JposMessageTypeUnionPay;
import com.guanri.android.jpos.iso.unionpay.JposPackageUnionPay;
import com.guanri.android.jpos.network.CommandControl;
import com.guanri.android.lib.utils.TypeConversion;

/**
 * 位图对象
 * @author Administrator
 *
 */
public class JposBitMap {
    private BitSet bitmapBase = new BitSet(64);// 基本位图 
    private BitSet bitmapExtend;   // 扩展位图 
    private boolean extendFlag   =   false;
    
    public void setBitmapBase(int position){
    	bitmapBase.set(position);
    }

    public boolean getBitmapBaseValue(int position){
    	return bitmapBase.get(position);
    }
    
    public void addBitmapExtend(){
       bitmapExtend = new BitSet(64);;   // 扩展位图 
       extendFlag   =   true;
    }

	public boolean getBitmapExtendValue(int position) {
		return bitmapExtend.get(position);
	}

	public void setBitmapExtend(int position) {
		bitmapExtend.set(position);
	}

	public boolean isExtendFlag() {
		return extendFlag;
	}
	
	public byte[] getBitmapBaseByte(){
		return null;
	}
	
	public byte[] getBitmapExtendByte(){
		return null;
	}
	
	public byte[] parseBitmapBase(){
		byte[] bitMap = new byte[64];
		byte[] bitMapValue = new byte[8];
		for(int i=0;i<64;i++){
			if(bitmapBase.get(i)){
				//System.out.println(i);
				bitMap[i]=0x01;}
		}
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				bitMapValue[i]=(byte)(bitMapValue[i]|bitMap[i*8+j]<<7-j);
				//System.out.println(i+":"+j+":"+bitMapValue[i]);
			}
		}
		return bitMapValue;
	}
	
	
	
	public List<Integer> getBitmapBase(){
		
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < bitmapBase.size(); i++) {
			if (bitmapBase.get(i)){
				result.add(i);
			}
		}
		return result;
	}
	
	
	
	
	
	public static void main(String[] args){
		JposBitMap jposBitMap = new JposBitMap();
		jposBitMap.bitmapBase.set(5);
		jposBitMap.bitmapBase.set(7);
		jposBitMap.bitmapBase.set(9);
		jposBitMap.bitmapBase.set(15);
		
		System.out.println(TypeConversion.byte2hex(jposBitMap.parseBitmapBase()));
		
		//System.out.println(TypeConversion.byte2hex(unionpayRequestLongin()));
	}
	
	
	/**
	 * 银联签到方法
	 * @return 返回签到报文
	 */
	public static byte[] unionpayRequestLongin(){
		//构造签到所需各域
		TreeMap<Integer,Object> sendMap = new TreeMap<Integer,Object>();
		// 域11 流水号
		sendMap.put(11, "000001");
		// 域41 终端代码
		sendMap.put(41, "00000001");
		// 域42 商户代码
		sendMap.put(42, "000000000000001");
		// 域60 自定义域     60.1 交易类型码 00  60.2 批次号  000001 网络管理信息码 001
		sendMap.put(60, "00000001001");
		// 域63 自定义域  63.1 操作员代码
		sendMap.put(63, "001");
		
		JposMessageTypeUnionPay messageType = new JposMessageTypeUnionPay();
		messageType.setID("6");
		messageType.setServerAddress("0000");
		messageType.setAddress("0000");
		messageType.setAppType("60");
		messageType.setSoftVer("22");
		messageType.setPosstate("0");
		messageType.setDisposal("0");
		messageType.setPreserving("000000");
		//设置消息头类型
		//messageType.setMessageType(MessageTypeDefineUnionpay.REQUEST_POS_CHECK_IN);
		
		JposPackageUnionPay jposPackageUnionPay = new JposPackageUnionPay(sendMap,messageType);
	 
		return jposPackageUnionPay.packaged();
		
	}
	
}
