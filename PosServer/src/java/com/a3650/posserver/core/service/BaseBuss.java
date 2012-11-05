/***********************************************************************
 * Module:  BaseDao.java
 * Author:  Administrator
 * Purpose: Defines the Interface BaseDao
 ***********************************************************************/

package com.a3650.posserver.core.service;

import java.io.Serializable;

import com.a3650.posserver.core.dao.BaseDao;

/**
 * 
 * @author Administrator
 *
 * @param <T>
 * @param <DAO>
 */
public interface BaseBuss <T,PK extends Serializable, DAO extends BaseDao<T,PK>>{
	public DAO getBaseDao();
	
	/**
	 * 获取自己控制事务DAO
	 * @return
	 */
	public DAO getHandlerDao();

}