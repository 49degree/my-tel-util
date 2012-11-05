/***********************************************************************
 * Module:  BaseDao.java
 * Author:  Administrator
 * Purpose: Defines the Interface BaseDao
 ***********************************************************************/

package com.a3650.posserver.core.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;

/**
 * 
 * @author Administrator
 *
 * @param <T>
 * @param <PK>
 */
public interface BaseDao <T, PK extends Serializable> {
    
	public void setEntityClass(Class<T> clazz);
	public void setPkClass(Class<PK> clazz);
	public Session getSession();
	public Transaction beginTransaction();
    // -------------------- 基本检索、增加、修改、删除操作 --------------------
	
    // 根据主键获取实体。如果没有相应的实体，返回 null。
    public T get(PK id);
    // 根据主键获取实体并加锁。如果没有相应的实体，返回 null。
    public T getWithLock(PK id, LockMode lock);

    // 根据主键获取实体。如果没有相应的实体，抛出异常。
    public T load(PK id);

    // 根据主键获取实体并加锁。如果没有相应的实体，抛出异常。
    public T loadWithLock(PK id, LockMode lock);

    // 获取全部实体。
    public List<T> loadAll();

    // 更新实体
    public void update(T entity);


    // 存储实体到数据库
    public void save(T entity);

    // 增加或更新实体
    public void saveOrUpdate(T entity);


    // 删除指定的实体
    public void delete(T entity);


    // 根据主键删除指定实体
    public void deleteByKey(PK id);



    /*-------------------- HSQL ----------------------------------------------

    // 使用HSQL语句直接增加、更新、删除实体
    public int bulkUpdate(String queryString);

    // 使用带参数的HSQL语句增加、更新、删除实体
    public int bulkUpdate(String queryString, Object[] values);

    // 使用HSQL语句检索数据
    public List<?> find(String queryString);

    // 使用带参数的HSQL语句检索数据
    public List<?> find(String queryString, Object[] values);

    // 使用带命名的参数的HSQL语句检索数据
    public List<?> findByNamedParam(String queryString, String[] paramNames,
            Object[] values);

    // 使用命名的HSQL语句检索数据
    public List<?> findByNamedQuery(String queryName);

    // 使用带参数的命名HSQL语句检索数据
    public List<?> findByNamedQuery(String queryName, Object[] values);

    // 使用带命名参数的命名HSQL语句检索数据
    public List<?> findByNamedQueryAndNamedParam(String queryName,
            String[] paramNames, Object[] values);

    // 使用HSQL语句检索数据，返回 Iterator
    public Iterator<?> iterate(String queryString);

    // 使用带参数HSQL语句检索数据，返回 Iterator
    public Iterator<?> iterate(String queryString, Object[] values);

    // 关闭检索返回的 Iterator
    public void closeIterator(Iterator<?> it);
    */
    // -------------------------------- Criteria ------------------------------

    // 创建与会话无关的检索标准对象
    public DetachedCriteria createDetachedCriteria();

    // 创建与会话绑定的检索标准对象
    public Criteria createCriteria();


    // 使用指定的实体及属性检索（满足除主键外属性＝实体值）数据
    public List<T> findEqualByEntity(T entity, String[] propertyNames);

    // 使用指定的实体及属性(非主键)检索（满足属性 like 串实体值）数据
    public List<T> findLikeByEntity(T entity, String[] propertyNames);

    public List<Object[]> queryHql(String hql,String[] params);

    // 加锁指定的实体
    public void lock(T entity, LockMode lockMode);

    // 强制立即更新缓冲数据到数据库（否则仅在事务提交时才更新）
    public void flush();
    
    public void close();

}