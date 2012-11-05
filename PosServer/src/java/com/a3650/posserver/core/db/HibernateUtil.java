/***********************************************************************
 * Module:  HibernateUtil.java
 * Author:  Administrator
 * Purpose: Defines the Class HibernateUtil
 ***********************************************************************/

package com.a3650.posserver.core.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.a3650.posserver.core.init.InitContext;

public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static String database_config = null;
	private static Configuration configration = null;
	private static final ThreadLocal<Session> sessions =new ThreadLocal<Session>();
	
	private static class Init{
		static{
			if(database_config==null||"".equals(database_config)){
				database_config = InitContext.getDatabaseConfig();
			}
			configration = new Configuration().configure(database_config);
			
			sessionFactory = configration.buildSessionFactory();
		}
		public static SessionFactory getSessionFactory(){
			return sessionFactory;
		}
		
		public static Configuration getConfiguration() {
			return configration;
		}
	}
	
	
	public static SessionFactory getSessionFactory() {
		return Init.getSessionFactory();
	}

	public static Configuration getConfiguration() {
		return Init.getConfiguration();
	}


	public static void shutdown(){
		getSessionFactory().close();
		configration = null;
	}
	
	public static void setDatabaseConfig(String databaseConfig) {
		database_config = databaseConfig;
	}
	
//	public static String getDatabaseConfig() {
//		return database_config;
//	}
	
	/**
	 * 以下2方法位采用自己的方式在线程范围管理sesion
	 * 
	 * 目前没有使用
	 * @return
	 * @throws HibernateException
	 */
	public static Session currentSession() throws HibernateException {
		Session s = sessions.get();
		if (s == null) {
			s = sessionFactory.openSession();
			sessions.set(s);
		}
		return s;
	} 

	public static void closeSession() throws HibernateException {
		Session s = sessions.get();
		if (s != null) {
			s.close();
		}
		sessions.set(null);
	}
}