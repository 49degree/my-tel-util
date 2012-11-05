/***********************************************************************
 * Module:  InitContext.java
 * Author:  Administrator
 * Purpose: Defines the Class InitContext
 ***********************************************************************/

package com.a3650.posserver.core.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.db.HibernateUtil;


public abstract class InitContext {
	static Logger logger = Logger.getLogger(InitContext.class);
	public static String SERVER_CONFIG = "server.properties";
	private static String AIIC = null;
	private static String databaseConfig = null;
	private static int clientThreadPoolSize = 0;
	private static boolean multPay = false;
	protected PosServer posServer;
	protected String operatorCenter = null;
	protected Properties properties = null;
	

	/**
	 * 初始化
	 **/
	protected void init() throws IOException{
		// TODO:
		try {
			// 加载服务配置文件
			properties = new Properties();
			properties.load(new FileInputStream(System.getProperty("user.dir")
					+ File.separator +"bin"+File.separator+ SERVER_CONFIG));

			operatorCenter = properties.getProperty("OperatorCenter");
			AIIC = properties.getProperty("AIIC");
			databaseConfig = properties.getProperty("database_config");
			HibernateUtil.getSessionFactory();//初始化数据库信息
			//SQLITE数据库，只能使用1个线程池，防止数据库更新死锁，其他数据库初始线程池为10
			if("com.a3650.posserver.core.db.SQLiteDialect".equals(
					HibernateUtil.getConfiguration().getProperty("dialect")))
					clientThreadPoolSize = 1;
			else
				clientThreadPoolSize = 100;
			try{
				multPay = Boolean.parseBoolean(properties.getProperty("mult_pay"));
			}catch(Exception e){
				
			}
					
		} catch (IOException e) {
			throw e;
		}

	}

	public PosServer getPosServer() {
		return posServer;
	}

	public String getOperatorCenter() {
		return operatorCenter;
	}

	public Properties getProperties() {
		return properties;
	}

	public static String getAIIC() {
		return AIIC;
	}
	
	

	public static int getClientThreadPoolSize() {
		return clientThreadPoolSize;
	}

	public static String getDatabaseConfig() {
		if(databaseConfig==null){
			try{
				Properties properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir")
						+ File.separator +"bin"+File.separator+ SERVER_CONFIG));
				databaseConfig = properties.getProperty("database_config");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return databaseConfig;
	}

	public static boolean isMultPay() {
		return multPay;
	}
	
}