package com.a3650.posserver.core.dao;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;


public class BaseDaoCglibProxy<T,PK extends Serializable,DAO extends BaseDao<T,PK>> extends CglibImpl<T,PK,DAO> {
	static Logger logger = Logger.getLogger(BaseDaoCglibProxy.class);

	public BaseDaoCglibProxy(Class<T> entityClazz,Class<PK> PKClazz,Class<DAO> daoClazz){
		super(entityClazz,PKClazz,daoClazz);
	}
	
	
	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		((DAO)arg0).beginTransaction();
		// 通过代理类调用父类中的方法
		Object result = arg3.invokeSuper(arg0, arg2);
		
		
		//logger.info("c:"+Thread.currentThread().getId()+":::::::"+((DAO)arg0).getSession().hashCode()+"++++++++++"+arg3.getSuperName());
		return result;
	}
	

}
