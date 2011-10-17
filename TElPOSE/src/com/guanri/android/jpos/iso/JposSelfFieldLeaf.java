package com.guanri.android.jpos.iso;

import com.guanri.android.jpos.constant.JposConstant.DataEncodeType;

/**
 * 自定义域数据项的定义
 * @author Administrator
 *
 */
public class JposSelfFieldLeaf {
	private String tag = null;//标识，用于TLV数据定义
	private int lengthType = 0;//0表示定长，1表示不定长
	private DataEncodeType encodeType = null;//数据编码类型
	
	private int maxLength = 0;//lengthType = 1时表示最大长度，lengthType = 0表示长度
	private String value = null;//数据
	private String memo = null;//备注，不作为上传的信息
	

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getLengthType() {
		return lengthType;
	}
	public void setLengthType(int lengthType) {
		this.lengthType = lengthType;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public DataEncodeType getEncodeType() {
		return encodeType;
	}
	public void setEncodeType(DataEncodeType encodeType) {
		this.encodeType = encodeType;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
