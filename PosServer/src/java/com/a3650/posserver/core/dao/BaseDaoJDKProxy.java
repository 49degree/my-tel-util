package com.a3650.posserver.core.dao;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;


public class BaseDaoJDKProxy {
	static Logger logger = Logger.getLogger(BaseDaoJDKProxy.class);
	private Object tarjectObject;
	public BaseDaoJDKProxy(Object obj) {
		logger.info("ProxyFactory init");
		this.tarjectObject = obj;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		logger.info("ProxyFactory invoke");
		Object result = null;
		//tarjectObject.openSession();
		if (tarjectObject != null) {
			result = method.invoke(tarjectObject, args);
		}
		return result;
	}
}
