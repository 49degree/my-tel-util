/***********************************************************************
 * Module:  BaseDaoImpl.java
 * Author:  Administrator
 * Purpose: Defines the Class BaseDaoImpl
 ***********************************************************************/

package com.a3650.posserver.core.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.a3650.posserver.core.dao.BaseDaoImplProxy.BaseDaoImplTransaction;
import com.a3650.posserver.core.db.HibernateUtil;

/**
 * 
 * @author Administrator
 *
 * @param <T>
 * @param <PK>
 */
public class BaseDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {
	static Logger logger = Logger.getLogger(BaseDaoImpl.class);
    // 实体类类型(由构造方法自动赋值)
    private Class<T> entityClass;
    private Class<PK> pkClass;
    private Session session = null;//HibernateUtil.getSessionFactory().getCurrentSession();

    // 构造方法，根据实例类自动获取实体类类型
	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		//session = HibernateUtil.getSessionFactory().getCurrentSession();
    	//logger.info("session:"+Thread.currentThread().getId()+":"+session.hashCode());
        this.entityClass = null;
        @SuppressWarnings("rawtypes")
		Class c = getClass();
        Type t = c.getGenericSuperclass();
        //logger.info("++++++++++++++++++++++++++++++++++++++");
        if (t instanceof ParameterizedType) {
        	
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            //logger.info("++++++++++++++++++++++++++++++++++++++"+Arrays.toString(p));
            this.entityClass = (Class<T>) p[0];
        }
    }
	@Override
	public final void setEntityClass(Class<T> clazz){
		this.entityClass = clazz;
	}
	@Override
	public final void setPkClass(Class<PK> clazz){
		this.pkClass = clazz;
	}
	@Override
	public final Session getSession(){
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		return session;
	}
	@Override
	public final Transaction beginTransaction(){
		getSession();
		return session.beginTransaction();
	}
    /**
     * 根据主键获取实体。如果没有相应的实体，返回 null。
     */
	@SuppressWarnings("unchecked")
	public T get(PK id) {
    	try{
    		
    		return (T)session.get(entityClass, id);
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }


	
    /**
     * 根据主键获取实体并加锁。如果没有相应的实体，返回 null。
     */
	@SuppressWarnings("unchecked")
    public T getWithLock(PK id, LockMode lock) {
		T t = (T) session.get(entityClass, id, lock);
        if (t != null) {
            this.flush(); // 立即刷新，否则锁不会生效。
        }
        return t;
    }

    /*
     * 根据主键获取实体。如果没有相应的实体，在使用时抛出异常。(non-Javadoc)
     * @see com.a3650.posserver.core.dao.BaseDao#load(java.io.Serializable)
     */
	@SuppressWarnings({ "unchecked", "unchecked" })
	public T load(PK id) {
        return (T) session.load(entityClass, id);
    }

    /*
     * 根据主键获取实体并加锁。如果没有相应的实体，抛出异常。(non-Javadoc)
     * @see com.a3650.posserver.core.dao.BaseDao#loadWithLock(java.io.Serializable, org.hibernate.LockMode)
     */
    @SuppressWarnings("unchecked")
	public T loadWithLock(PK id, LockMode lock) {
		T t = (T) session.load(entityClass, id, lock);
        if (t != null) {
            this.flush(); // 立即刷新，否则锁不会生效。
        }
        return t;
    }

    /**
     * 获取全部实体。
     */
    @SuppressWarnings("unchecked")
	public List<T> loadAll() {
    	Query query = session.createQuery("from "+entityClass.getSimpleName());
        return (List<T>) query.list();
    }




    /**
     * 存储实体到数据库
     */
    @BaseDaoImplTransaction(autoCommit=true)
    public void save(T entity) {
    	session.save(entity);
    }

    /**
     * 更新实体
     * 
     * update更新前不查询数据库数据是否变更
     * saveOrUpdate更新前查询数据库数据是否更新
     */
    @BaseDaoImplTransaction(autoCommit=true)
    public void update(T entity) {
    	session.update(entity);
    }
    /**
     * 增加或更新实体
     */
    @BaseDaoImplTransaction(autoCommit=true)
    public void saveOrUpdate(T entity) {
    	session.saveOrUpdate(entity);
    }


    /**
     * 删除指定的实体
     */
    @BaseDaoImplTransaction(autoCommit=true)
    public void delete(T entity) {
    	session.delete(entity);
        
    }

    /**
     * 根据主键删除指定实体
     */
    @BaseDaoImplTransaction(autoCommit=true)
    public void deleteByKey(PK id) {
        this.delete(this.load(id));
    }

    /**
     * 创建与会话无关的检索标准
     */
    public final DetachedCriteria createDetachedCriteria() {
        return DetachedCriteria.forClass(this.entityClass);
    }

    /**
     * 创建与会话绑定的检索标准
     */
    public final Criteria createCriteria() {
        return this.createDetachedCriteria().getExecutableCriteria(session) ;
    }


    /**
     * 使用指定的实体及属性检索（满足除主键外属性＝实体值）数据
     */
    public List<T> findEqualByEntity(T entity, String[] propertyNames) {
        Criteria criteria = this.createCriteria();
        for (int i = 0; i < propertyNames.length; ++i) {
			try {
				Object value=null;
				Method method = entity.getClass().getMethod("get"+propertyNames[i].substring(0,1).toUpperCase()+propertyNames[i].substring(1));
				value = method.invoke(entity);
	            criteria.add(Restrictions.eq(propertyNames[i], value));
	            criteria.addOrder(Order.asc(propertyNames[i]));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return (List<T>) criteria.list();
    }

    /*
     * 使用指定的实体及属性检索（满足属性 like 串实体值）数据(non-Javadoc)
     * @see com.a3650.posserver.core.dao.BaseDao#findLikeByEntity(java.io.Serializable, java.lang.String[])
     */
    @SuppressWarnings("unchecked")
	public List<T> findLikeByEntity(T entity, String[] propertyNames) {
        Criteria criteria = this.createCriteria();
        for (String property : propertyNames) {
            try {
            	Object value = entity.getClass().getField(property).get(entity);
                if (value instanceof String) {
                    criteria.add(Restrictions.like(property, (String) value,
                            MatchMode.ANYWHERE));
                    criteria.addOrder(Order.asc(property));
                } else {
                    criteria.add(Restrictions.eq(property, value));
                    criteria.addOrder(Order.asc(property));
                }
            } catch (Exception ex) {
                // 忽略无效的检索参考数据。
            }
        }
        return (List<T>) criteria.list();
    }

    /**
     * 调hql查询数据
     */
    @SuppressWarnings("unchecked")
	public List<Object[]> queryHql(String hql,String[] params){
    	Query query = session.createQuery(hql);
    	for(int i=0;i<params.length;i++){
    		query.setParameter(i, params[i]);
    	}
    	return query.list();
    }

    /**
     * 调hql更新数据
     */
	@BaseDaoImplTransaction(autoCommit=true)
    public int updateHql(String hql,String[] params){
    	Query query = session.createQuery(hql);
    	for(int i=0;i<params.length;i++){
    		query.setParameter(i, params[i]);
    	}
    	return query.executeUpdate();
    }
    /*
     * 加锁指定的实体
     */
	@BaseDaoImplTransaction(autoCommit=true)
    public void lock(T entity, LockMode lock) {
        session.lock(entity, lock);
    }

    // 强制立即更新缓冲数据到数据库（否则仅在事务提交时才更新）
	@BaseDaoImplTransaction(autoCommit=true)
    public final void flush() {
        session.flush();
    }

	@Override
	public final void close() {
		// TODO Auto-generated method stub
		session.close();
	}
	
	@Override
	public final void finalize() {
		// TODO Auto-generated method stub
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}