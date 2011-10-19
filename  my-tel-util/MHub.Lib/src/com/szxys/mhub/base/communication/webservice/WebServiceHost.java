package com.szxys.mhub.base.communication.webservice;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.szxys.mhub.base.communication.IWebHost;
import com.szxys.mhub.base.communication.IWebHostEventHandler;
import com.szxys.mhub.base.communication.WebDataCell;
import com.szxys.mhub.base.communication.WebHostEndPoint;
import com.szxys.mhub.interfaces.Platform;

/**
 * 	基于WebService与Web端通信的IWebHost接口的实现类
 * @author sujinyi
 * */


public class WebServiceHost implements IWebHost {

	/**
	 * 服务器终结点
	 * */
	WebHostEndPoint webHostEndPoint=null;
	
	/**
	 * 服务器通信管理模块的回调对象，由管理模块设置
	 * */
	IWebHostEventHandler webHostEventhandler=null;
	
	/**
	 * 接收线程运行状态标志
	 * */
	boolean runningFlag=false;
	
	/**构造方法
	*  @param remoteEndPoint :服务器终结点
	* */
	public WebServiceHost(WebHostEndPoint remoteEndPoint) {
		
		this.webHostEndPoint=remoteEndPoint;
		
	}

	/**
	 * 返回该对象的服务器终结点
	 * */
	public WebHostEndPoint getRemoteEndPoint() {
		
		return this.webHostEndPoint;
	}

	/**
	 * 异步接收Web端下发来的数据
	 * */
	public void run() {
		webHostEventhandler.onConnected(true);	
	}

	/**
	 * 返回对象的接收状态
	 * */
	public boolean isRunning() {
		
		return this.runningFlag;
	}

	/**
	 * 关闭连接
	 * */
	public void shutdown() {
		
		
	}

	/**
	 * 向Web端发送数据请求,并获得响应数据
	 * @param WebDataCell :数据包
	 * @return boolean    :执行是否成功
	 * */
	public boolean send(WebDataCell data) {
		
		String url=webHostEndPoint.Adress;
		boolean resultFlag=false;
		HttpPost httpRequest=new HttpPost(url);
		
		//组包
		byte []dataPack=packaging(data);
		
		ByteArrayEntity param=new ByteArrayEntity(dataPack);
		httpRequest.setEntity(param);
	
		HttpResponse httpResponse=null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);			
			resultFlag=true;
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		if(resultFlag){
			
			byte[] tempData;
			try {
				tempData = EntityUtils.toByteArray(httpResponse.getEntity());
				if(tempData.length>9){
					getData(tempData,data);
					webHostEventhandler.onReceived(data.ReqIdentifying.subSystemID,data.MainCmd,data.SubCmd,data.Data);
				}
			} catch (IOException e) {
				e.printStackTrace();
				webHostEventhandler.onError(Platform.MSG_RECEIVED_FAILED, "传输失败");
			}		
		}
		return resultFlag;
		
		
	}
	
	/**
	 * 根据数据包协议组包
	 * @param dataCell :数据对象
	 * @return byte[]  :根据数据包组成的字节数组数据包
	 * */
	private byte[] packaging(WebDataCell dataCell){
		
		byte dataPack[];
		
		byte []version=new byte[]{1,1};
		short lcid=(short)2052;
		long token=dataCell.Token;
		short biz_code = (short)dataCell.ReqIdentifying.subSystemID;
		short func_code= (short)dataCell.MainCmd;
		
		
		ByteBuffer data_temp=ByteBuffer.allocate(16+dataCell.Data.length);
		data_temp.put(WebUtils.bytesReverseOrder(version));
		data_temp.put(WebUtils.toLH(lcid));
		data_temp.putLong(token);
		
		data_temp.put(WebUtils.toLH(biz_code));
		data_temp.put(WebUtils.toLH(func_code));
		data_temp.put(dataCell.Data);
		
		dataPack=data_temp.array();
				
		return dataPack;
	}
	
	/**
	 * 根据数据包协议拆包
	 * @param dataByteArray  :Web端响应的字节数组数据包
	 * @param data  :用于返回的数据对象
	 * @return WebDataCell   :数据对象 
	 * */
	
	private WebDataCell getData(byte []dataByteArray,WebDataCell data){
		
		byte []responseCode=new byte[4];
		byte []bizCode=new byte[2];
		byte []functionCode=new byte[2];
		byte []responseData=new byte[dataByteArray.length-10];
		
		System.arraycopy(dataByteArray, 2, responseCode, 0, responseCode.length);
		System.arraycopy(dataByteArray,6,bizCode,0,bizCode.length);
		System.arraycopy(dataByteArray, 8, functionCode, 0, functionCode.length);
		System.arraycopy(dataByteArray, 10, responseData, 0, responseData.length);
		
		data.ReqIdentifying.subSystemID=new Short(WebUtils.lBytesToShort(bizCode)).intValue();
		data.MainCmd=new Short(WebUtils.lBytesToShort(functionCode)).intValue();
		
		data.SubCmd=WebUtils.lBytesToInt(responseCode);
		data.Data=responseData;
		
		return data;
	}

	/**
	 * 设置WebHostEventHandler对象，用于接收到Web端响应时的方法回调
	 * @param  handler: 回调函数接口
	 * */
	public void setWebHostEventHandler(IWebHostEventHandler handler) {
		
		this.webHostEventhandler=handler;
	}

}
