package com.szxys.mhub.base.communication.webservice;

import com.szxys.mhub.base.communication.AbstractWebHostFactory;
import com.szxys.mhub.base.communication.IWebHost;
import com.szxys.mhub.base.communication.WebHostEndPoint;


/**
 * 	用于返回一个WebServiceHost的工厂类
 * @author sujinyi
 * */

public class WebServiceFactory extends AbstractWebHostFactory {

	/**
	 * 根据远程服务器终结点创建一个基于WebService通信的服务器通信对象。
	 * 
	 * @param remoteEndPoint :WebService远程服务器终结点
	 * @return IWebHost      :基于WebService通信的服务器通信对象
	 *          
	 */
	public IWebHost createWebHost(WebHostEndPoint remoteEndPoint) {
		
		IWebHost webHost=new WebServiceHost(remoteEndPoint);
		
		return webHost;
	}

}
