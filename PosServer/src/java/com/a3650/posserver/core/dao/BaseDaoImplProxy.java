package com.a3650.posserver.core.dao;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

public class BaseDaoImplProxy<T,PK extends Serializable,DAO extends BaseDao<T,PK>>  extends CglibImpl<T,PK,DAO>  {
	static Logger logger = Logger.getLogger(BaseDaoImplProxy.class);
	
	public BaseDaoImplProxy(Class<T> entityClazz,Class<PK> PKClazz,Class<DAO> daoClazz){
		super(entityClazz,PKClazz,daoClazz);
	}
	

	
	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		// 通过代理类调用父类中的方法
		Transaction tx = null;
		Object result = null;
		try{
			//logger.info("a:"+Thread.currentThread().getId()+":::::::"+((DAO)arg0).getSession().hashCode()+"++++++++++"+arg3.getSuperName());
			//判断方法中是否有指定注解类型的注解
			boolean hasAnnotation = arg1.isAnnotationPresent(BaseDaoImplTransaction.class);
			//1.HibernateUtil.getSessionFactory().getCurrentSession()获得的session正常
			//的操作都必须在transcation.isActive()条件下才能执行
			tx = ((DAO)arg0).beginTransaction();
			result = arg3.invokeSuper(arg0, arg2);
			if (hasAnnotation) {
				//根据注解类型返回方法的指定类型注解
				BaseDaoImplTransaction annotation = arg1.getAnnotation(BaseDaoImplTransaction.class);
				if(annotation.autoCommit()){
					//当事务结束的时候，不管是提交还是回滚，Hibernate会把Session从当前线程剥离，并且关闭它。
					//不用手动调用session的close方法，也可以在不需要提交时手动调session的close方法,Hibernate也会把Session从当前线程剥离
					//假若你再次调用getCurrentSession()，你会得到一个新的Session，并且开始一个新的工作单元
					tx.commit();
				}
			}
			//因为socket连接后的业务使用了线程池，如果session没有关闭，可能不同的连接业务会使用相同的session
			// If session close() is called, guarantee unbind()   
			//if ( "close".equals( method.getName()) ) {  
			//	unbind( realSession.getSessionFactory() );//从当前线程解开绑定
			//} 
			//logger.info("b:"+Thread.currentThread().getId()+":::::::"+((DAO)arg0).getSession().hashCode()+"++++++++++"+arg3.getSuperName());
		}catch(Exception e){
			e.printStackTrace();
			try{
				if(tx!=null)
					tx.rollback();
			}catch(Exception ex){
				
			}
			try{
				((DAO)arg0).close();
			}catch(Exception ex){
				
			}
			
		}finally{

		}
		return result;
	}
	
	/*
	 * 元注解@Target,@Retention,@Documented,@Inherited
	 * 
	 *     @Target 表示该注解用于什么地方，可能的 ElemenetType 参数包括：
	 *         ElemenetType.CONSTRUCTOR 构造器声明
	 *         ElemenetType.FIELD 域声明（包括 enum 实例）
	 *         ElemenetType.LOCAL_VARIABLE 局部变量声明
	 *         ElemenetType.METHOD 方法声明
	 *         ElemenetType.PACKAGE 包声明
	 *         ElemenetType.PARAMETER 参数声明
	 *         ElemenetType.TYPE 类，接口（包括注解类型）或enum声明
	 *         
	 *     @Retention 表示在什么级别保存该注解信息。可选的 RetentionPolicy 参数包括：
	 *         RetentionPolicy.SOURCE 注解将被编译器丢弃
	 *         RetentionPolicy.CLASS 注解在class文件中可用，但会被VM丢弃
	 *         RetentionPolicy.RUNTIME VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息。
	 *         
	 *     @Documented 将此注解包含在 javadoc 中
	 *     
	 *     @Inherited 允许子类继承父类中的注解
	 *   
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Inherited
	/*
	 * 定义注解 BaseDaoImplTransaction
	 * 注解中含有两个元素 autoCommit 和 description
	 * autoCommit 元素 有默认值 false 不主动提交
	 * description 元素 有默认值 "no description"
	 */
	public @interface BaseDaoImplTransaction {
		public boolean autoCommit() default false;
		public String description() default "no description";
	}
}
