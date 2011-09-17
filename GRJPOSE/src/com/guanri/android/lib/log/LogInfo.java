package com.guanri.android.lib.log;

public class LogInfo {

	public static LogInfo instance = new LogInfo();
    public StringBuffer pad_to_server = new StringBuffer();
    public StringBuffer pad_to_pos = new StringBuffer();
    public StringBuffer pos_to_pad = new StringBuffer();
    public StringBuffer server_to_pad = new StringBuffer();
    
    public void append(String params,String value){
		if ("pad_to_pos".equals(params)) {
			pad_to_pos.append(value);
		} else if ("pad_to_server".equals(params)) {
			pad_to_server.append(value);
		} else if ("pos_to_pad".equals(params)) {
			pos_to_pad.append(value);
		} else if ("server_to_pad".equals(params)) {
			server_to_pad.append(value);
		}
    }
    /**
     * 其他操作
     * @param params
     * @return
     */
	public String operate(String params) {
		if ("pad_to_pos".equals(params)) {
			pad_to_pos=new StringBuffer(pad_to_pos.length()<1000?pad_to_pos:pad_to_pos.substring(pad_to_pos.length()-1000));
			return pad_to_pos.toString();
		} else if ("pad_to_server".equals(params)) {
			pad_to_server=new StringBuffer(pad_to_server.length()<1000?pad_to_server:pad_to_server.substring(pad_to_server.length()-1000));
			return pad_to_server.toString();
		} else if ("pos_to_pad".equals(params)) {
			pos_to_pad=new StringBuffer(pos_to_pad.length()<1000?pos_to_pad:pos_to_pad.substring(pos_to_pad.length()-1000));
			return pos_to_pad.toString();
		} else if ("server_to_pad".equals(params)) {
			server_to_pad=new StringBuffer(server_to_pad.length()<1000?server_to_pad:server_to_pad.substring(server_to_pad.length()-1000));
			return server_to_pad.toString();
		}
		return "";
	}
}
