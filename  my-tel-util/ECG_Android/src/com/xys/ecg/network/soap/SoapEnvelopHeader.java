package com.xys.ecg.network.soap;

/**
 * SOAPЭ�鶨��
 * @author Administrator
 *
 */
public class SoapEnvelopHeader {
	//soapЭ��汾
	public static final int VER10 = 100;
	public static final int VER11 = 110;
	public static final int VER12 = 120;

	public int mVersion;//Э��汾
	public String mEnv;//envЭ��
	public String mEnc;//encЭ��
	public String mXsi;//xsiЭ��
	public String mXsd;//xsdЭ��
	
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
