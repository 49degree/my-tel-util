package method;

import java.util.ArrayList;
import java.util.List;

/**

 * @Described���������ڴ����̽��

 * @VM args : -XX:PermSize=10M -XX:MaxPermSize=10M

 * @author YHJ create at 2011-10-30 ����04:28:30

 * @FileNmae com.yhj.jvm.memory.constant.ConstantOutOfMemory.java

 */

public class ConstantOutOfMemory {

 

    /**

     * @param args

     * @throws Exception

     * @Author YHJ create at 2011-10-30 ����04:28:25

     */

    public static void main(String[] args) throws Exception {

       try {

           List<String> strings = new ArrayList<String>();

           int i = 0;

           while(true){

              strings.add(String.valueOf(i));

              System.out.println(strings.get(strings.size()-1).hashCode());
           }

       } catch (Exception e) {

           e.printStackTrace();

           throw e;

       }

 

    }

 

}
