/***********************************************************************
 * Module:  BaseDaoImpl.java
 * Author:  Administrator
 * Purpose: Defines the Class BaseDaoImpl
 ***********************************************************************/

package com.a3650.posserver.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.dao.BaseDao;
import com.a3650.posserver.core.dao.BaseDaoCglibProxy;
import com.a3650.posserver.core.dao.BaseDaoImplProxy;

/**
 * 
 * @author Administrator
 *
 * @param <T>
 * @param <DAO>
 * @param <PK>
 */
public class BaseBussProxy<T,PK extends Serializable,DAO extends BaseDao<T,PK>> implements BaseBuss<T,PK,DAO>{
	static Logger logger = Logger.getLogger(BaseBussProxy.class);
	protected Class<T> entityClass;
	protected Class<PK> PKClass;
	protected Class<DAO> daoClass;
	public DAO dao=null;
	private DAO handlerDao=null;
	private Byte lock = new Byte((byte)0);
	public BaseBussProxy(){
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
        	
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.entityClass = (Class<T>) p[0];
            this.PKClass = (Class<PK>) p[1];
            this.daoClass = (Class<DAO>) p[2];
        }
        //使用代理对象
		BaseDaoImplProxy<T,PK,DAO> factory = new BaseDaoImplProxy<T,PK,DAO>(entityClass, PKClass, daoClass);
        dao = (DAO) factory.getProxy(); 
	}
	
	
	public DAO getBaseDao(){
		return dao;
	}
	
	/**
	 * 获取自己控制事务DAO
	 * @return
	 */
	public DAO getHandlerDao(){
		
		if(handlerDao==null){
			synchronized(lock){
				if(handlerDao==null){
			        //使用代理对象
			        BaseDaoCglibProxy<T,PK,DAO> factory = new BaseDaoCglibProxy<T,PK,DAO>(entityClass, PKClass, daoClass);
			        handlerDao = (DAO) factory.getProxy(); 
				}
			}
		}
        return handlerDao;
	}
	
}