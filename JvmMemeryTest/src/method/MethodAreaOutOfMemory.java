package method;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;



/**

 * @Described：方法区溢出测试

 * 使用技术 CBlib

 * @VM args : -XX:PermSize=10M -XX:MaxPermSize=10M

 * @author YHJ create at 2011-11-12 下午08:47:55

 * @FileNmae com.yhj.jvm.memory.methodArea.MethodAreaOutOfMemory.java

 */

public class MethodAreaOutOfMemory {

 

    /**

     * @param args

     * @Author YHJ create at 2011-11-12 下午08:47:51

     */

    public static void main(String[] args) {

       while(true){

           Enhancer enhancer = new Enhancer();

           enhancer.setSuperclass(TestCase.class);

           enhancer.setUseCache(false);

           enhancer.setCallback(new MethodInterceptor() {

              public Object intercept(Object arg0, Method arg1, Object[] arg2,

                     MethodProxy arg3) throws Throwable {

                  return arg3.invokeSuper(arg0, arg2);

              }

           });

           enhancer.create();

       }

    }

    /**

     * @Described：测试用例

     * @author YHJ create at 2011-11-12 下午08:53:09

     * @FileNmae com.yhj.jvm.memory.methodArea.MethodAreaOutOfMemory.java

     */

    public static class TestCase{

       

    } 

}


