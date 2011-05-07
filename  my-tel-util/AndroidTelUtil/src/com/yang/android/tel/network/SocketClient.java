package com.yang.android.tel.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.yang.android.tel.log.Logger;
import com.yang.android.tel.service.MyTelServices;

public class SocketClient{
	public Logger logger = Logger.getLogger(SocketClient.class);
	String ip = null;
	String port = null;
	public SocketClient(String ip,String port){
		this.ip = ip;
		this.port = port;
	}
	
	
	boolean result = true;
	public boolean sendMessage(final String value) {
		
		new Thread(){
			public void run(){
				BufferedReader in = null;
				Socket socket = null;
				PrintWriter out = null;
				try {
					logger.error(ip + ":" + port + ":beging=========================");
					socket = new Socket(ip, Integer.parseInt(port));
					socket.setSoTimeout(30*1000);
					out = new PrintWriter(socket.getOutputStream(), true);
					out.println(value);
					out.flush();
					out.close();
					logger.error("end=========================");
				} catch (Exception e) {
					e.printStackTrace();
					result = false;
				} finally {
					try {
						socket.close();
					} catch (Exception e) {
					}
				}
			}
		}.start();

		return result;
	}
	

	

}
