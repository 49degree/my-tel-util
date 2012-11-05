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

import com.a3650.posserver.core.db.HibernateUtil;

/** @pdOid e4c6079f-8554-442d-bb07-99c12304cdd9 */
public class BaseDaoImpl_0516<T extends Serializable, PK extends Serializable> implements BaseDao<T, PK> {
	static Logger logger = Logger.getLogger(BaseDaoImpl_0516.class);
    // 实体类类型(由构造方法自动赋值)
    private Class<T> entityClass;
    public final static Session session = HibernateUtil.getSessionFactory().openSession();;
    public final static Byte lock = new Byte((byte)0);

    // 构造方法，根据实例类自动获取实体类类型
    public BaseDaoImpl_0516() {
        this.entityClass = null;
        Class c = getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.entityClass = (Class<T>) p[0];
        }
    }
    
	public final void setEntityClass(Class clazz){
	}
	public final void setPkClass(Class clazz){
	}
	@Override
	public final Session getSession(){
		return null;
	}
	@Override
	public final Transaction beginTransaction(){
		getSession();
		return null;
	}
    // -------------------- 基本检索、增加、修改、删除操作 --------------------

    // 根据主键获取实体。如果没有相应的实体，返回 null。
    public T get(PK id) {
    	try{
    		return (T)session.get(entityClass, id);
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
        //return (T) session.get(entityClass, id);
    }

    // 根据主键获取实体。如果没有相应的实体，返回 null。
    public T get(String id) {
    	try{
    		T entity = (T)entityClass.newInstance();
    		String identifierPropertyName = HibernateUtil.getSessionFactory().getClassMetadata(entityClass).getIdentifierPropertyName();
    		
    		
    		Criteria criteria = this.createCriteria();
    		criteria.add(Restrictions.eq(identifierPropertyName, String.valueOf(id)));
    		criteria.addOrder(Order.asc(identifierPropertyName));
    		List<T> entitys= criteria.list();
    		if(entitys==null||entitys.size()==0)
    			return null;
    		else
    			return entitys.get(0);
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
        //return (T) session.get(entityClass, id);
    }
    // 根据主键获取实体并加锁。如果没有相应的实体，返回 null。
    public T getWithLock(PK id, LockMode lock) {
        T t = (T) session.get(entityClass, id, lock);
        if (t != null) {
            this.flush(); // 立即刷新，否则锁不会生效。
        }
        return t;
    }

    // 根据主键获取实体。如果没有相应的实体，抛出异常。
    public T load(PK id) {
        return (T) session.load(entityClass, id);
    }

    // 根据主键获取实体并加锁。如果没有相应的实体，抛出异常。
    public T loadWithLock(PK id, LockMode lock) {
        T t = (T) session.load(entityClass, id, lock);
        if (t != null) {
            this.flush(); // 立即刷新，否则锁不会生效。
        }
        return t;
    }

    // 获取全部实体。
    public List<T> loadAll() {
    	Query query = session.createQuery("from "+entityClass.getSimpleName());
        return (List<T>) query.list();
    }

    // loadAllWithLock() ?

    // 更新实体
    public void update(T entity) {
        
    	synchronized (lock) {
    	   	Transaction  tr =  session.beginTransaction();
    	   	session.update(entity);
            tr.commit();
		}
    }


    // 存储实体到数据库
    public void save(T entity) {
    	synchronized (lock) {
    	   	Transaction  tr =  session.beginTransaction();
    	   	
            session.save(entity);
            tr.commit();
		}
    }

    // saveWithLock()？

    // 增加或更新实体
    public void saveOrUpdate(T entity) {
    	synchronized (lock) {
    		session.clear();
    	   	Transaction  tr =  session.beginTransaction();
            session.saveOrUpdate(entity);
            tr.commit();
            
		}
    }


    // 删除指定的实体
    public void delete(T entity) {
    	synchronized (lock) {
    	   	Transaction  tr =  session.beginTransaction();
    	   	session.delete(entity);
            tr.commit();
		}
        
    }



    // 根据主键删除指定实体
    public void deleteByKey(PK id) {
        this.delete(this.load(id));
    }

    // 创建与会话无关的检索标准
    public DetachedCriteria createDetachedCriteria() {
        return DetachedCriteria.forClass(this.entityClass);
    }

    // 创建与会话绑定的检索标准
    public Criteria createCriteria() {
        return this.createDetachedCriteria().getExecutableCriteria(session) ;
    }


    // 使用指定的实体及属性检索（满足除主键外属性＝实体值）数据
    public List<T> findEqualByEntity(T entity, String[] propertyNames) {
    	//logger.info("findEqualByEntity:"+entity.getClass());
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

    // 使用指定的实体及属性检索（满足属性 like 串实体值）数据
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

    public List<Object[]> queryHql(String hql,String[] params){
    	logger.info(hql);
    	Query query = session.createQuery(hql);
    	for(int i=0;i<params.length;i++){
    		query.setParameter(i, params[i]);
    	}
    	
    	return query.list();
    }

    // -------------------------------- Others --------------------------------

    // 加锁指定的实体
    public void lock(T entity, LockMode lock) {
        session.lock(entity, lock);
    }

    // 强制立即更新缓冲数据到数据库（否则仅在事务提交时才更新）
    public void flush() {
        session.flush();
    }

	@Override
	public void close() {
		// TODO Auto-generated method stub
		flush();
	}




}