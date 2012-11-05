package test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HelloWorldHandler implements InvocationHandler {

	// 要代理的原始对象

	private Object objOriginal;
	/**
	 * 
	 * 构造函数。
	 * 
	 * @param obj
	 *            要代理的原始对象。
	 */

	public HelloWorldHandler(Object obj) {
		this.objOriginal = obj;
	}

	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		Object result;
		// 方法调用之前
		doBefore();
		// 调用原始对象的方法
		result = method.invoke(this.objOriginal, args);
		// 方法调用之后
		doAfter();
		return result;
	}

	private void doBefore() {
		System.out.println("before method invoke!");
	}

	private void doAfter() {
		System.out.println("after method invoke!");
	}

	public static void main(String[] args) {
           HelloWorld<String,Long> hw = new HelloWorldImpl();
           InvocationHandler handler = new HelloWorldHandler(hw);
           HelloWorld proxy = (HelloWorld) Proxy.newProxyInstance( 
                         hw.getClass().getClassLoader(), 
                         hw.getClass().getInterfaces(), 
                         handler); 
           proxy.sayHelloWorld("aaaaa");
		
//		BaseDao dao = new ApsaiOrderPayInfoDao();
//        //使用代理对象
//        BaseDaoProxy factory = new BaseDaoProxy(dao);
//        dao = (ApsaiOrderPayInfoDao) Proxy.newProxyInstance( 
//                dao.getClass().getClassLoader(), 
//                dao.getClass().getInterfaces(), 
//                factory); 

    }

}

