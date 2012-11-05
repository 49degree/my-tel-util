package com.a3650.posserver.core.datapackage;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 位图对象
 * @author Administrator
 *
 */
public class DataBitMap {
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
	
	

}
