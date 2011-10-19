package com.xys.ecg.network.soap;

import java.util.HashMap;
import java.util.Map;


public class SoapEnvelopData {
	public String strNameSpace = null;
	public String strMethodName = null;
	public String outParameterName = null;
	public Map<String,String> inParameters = null;
	public SoapEnvelopData(String nameSpace,String methodName,String outParameterName){
		this.inParameters = new HashMap<String,String>();
		this.strNameSpace = nameSpace;
		this.strMethodName = methodName;
		this.outParameterName = outParameterName;
	}
}
