package com.a3650.posserver.core.dao;

import java.io.Serializable;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.log4j.Logger;

public abstract class CglibImpl<T,PK extends Serializable,DAO extends BaseDao<T,PK>> implements MethodInterceptor {
	static Logger logger = Logger.getLogger(CglibImpl.class);
	//protected DAO dao = null;
	protected Enhancer enhancer = new Enhancer();
	protected Class<T> entityClass;
	protected Class<PK> PKClass;
	protected Class<DAO> daoClass;


	public CglibImpl(Class<T> entityClazz,Class<PK> PKClazz,Class<DAO> daoClazz){
		entityClass = entityClazz;
		PKClass = PKClazz;
		daoClass = daoClazz;
	}
//	public DAO getProxy(Class<T> entityClazz,Class<PK> PKClazz,Class<DAO> daoClazz) {
//		// 设置需要创建子类的类
//		enhancer.setSuperclass(daoClazz);
//		enhancer.setCallback(this);
//		// 通过字节码技术动态创建子类实例
//		DAO o = (DAO)enhancer.create();
//		o.setEntityClass(entityClazz);
//		o.setPkClass(PKClazz);
//		return o;
//	}
	
	public DAO getProxy() {
		// 设置需要创建子类的类
		enhancer.setSuperclass(daoClass);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		DAO o = (DAO)enhancer.create();//CGLIB创建代理对象
		o.setEntityClass(entityClass);
		o.setPkClass(PKClass);
		return o;
	}
}
