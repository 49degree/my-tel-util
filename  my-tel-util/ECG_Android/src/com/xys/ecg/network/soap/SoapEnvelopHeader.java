package com.xys.ecg.network.soap;

/**
 * SOAP协议定义
 * @author Administrator
 *
 */
public class SoapEnvelopHeader {
	//soap协议版本
	public static final int VER10 = 100;
	public static final int VER11 = 110;
	public static final int VER12 = 120;

	public int mVersion;//协议版本
	public String mEnv;//env协议
	public String mEnc;//enc协议
	public String mXsi;//xsi协议
	public String mXsd;//xsd协议
	
	public SoapEnvelopHeader(int version)
	{
		setVersion(version);
	}
	public void setVersion(int version)
	{
		this.mVersion = version;
	    if (version == 100) {
	    	mXsi = "http://www.w3.org/1999/XMLSchema-instance";
	    	mXsd = "http://www.w3.org/1999/XMLSchema";
	    } else {
	    	mXsi = "http://www.w3.org/2001/XMLSchema-instance";
	    	mXsd = "http://www.w3.org/2001/XMLSchema";
	    }
	    if (version < 120) {
	    	mEnc = "http://schemas.xmlsoap.org/soap/encoding/";
	    	mEnv = "http://schemas.xmlsoap.org/soap/envelope/";
	    } else {
	    	mEnc = "http://www.w3.org/2001/12/soap-encoding";
	    	mEnv = "http://www.w3.org/2001/12/soap-envelope";
	    }
	}
}
