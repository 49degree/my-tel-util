package test.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;

import com.a3650.posserver.core.bean.ApsaiOrderPayInfo;
import com.a3650.posserver.core.dao.BaseDao;
import com.a3650.posserver.core.dao.BaseDaoImpl;
import com.a3650.posserver.core.dao.impl.ApsaiOrderPayInfoDao;

public class HelloWorldCglib<T,PK> implements MethodInterceptor  {
	private Enhancer enhancer = new Enhancer();
	 public BaseDao getProxy(Class clazz,Class entityClazz,Class pkClazz){
	  //设置需要创建子类的类
		 
	  enhancer.setSuperclass(clazz);
	  enhancer.setCallback(this);
	  //通过字节码技术动态创建子类实例
	  BaseDaoImpl o = (BaseDaoImpl)enhancer.create();
	  o.setEntityClass(entityClazz);
	  o.setPkClass(pkClazz);
	  return o;
	 }

	 
	 public Object getProxy(Class clazz){
		  //设置需要创建子类的类
			 
		  enhancer.setSuperclass(clazz);
		  enhancer.setCallback(this);
		  //通过字节码技术动态创建子类实例
		  Object o = enhancer.create();
		  return o;
	}
	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		// TODO Auto-generated method stub
		  logger.info("前置代理"+arg1+":"+arg2);
		  //通过代理类调用父类中的方法
		  //((BaseDao)arg0).beginTransaction();
		  Object result = arg3.invokeSuper(arg0, arg2);
		  logger.info("后置代理");
		  return result;

	}

	static Logger logger = Logger.getLogger(HelloWorldCglib.class);
	public static void main(String[] args) {
           HelloWorldCglib handler = new HelloWorldCglib();
           BaseDao<ApsaiOrderPayInfo,Long> proxy = (ApsaiOrderPayInfoDao)handler.getProxy(ApsaiOrderPayInfoDao.class,ApsaiOrderPayInfo.class,Long.class);
           ApsaiOrderPayInfo o = proxy.get(1L);
           logger.info("aaa:"+o.getPayTime());
           
           
           
//           HelloWorld<String,Long> world = (HelloWorldImpl)handler.getProxy(HelloWorldImpl.class);
//           world.sayHelloWorld("1111");

    }





}

