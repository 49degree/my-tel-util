package stack;
/**

 * @Described��ջ�㼶����̽��

 * @VM args:-Xss2M

 * @author YHJ create at 2011-11-12 ����08:19:28

 * @FileNmae com.yhj.jvm.memory.stack.StackOverFlow.java

 */
public class StackMemoryOverFlow {
	private int i ;

	   

    public void plus() {
    	i++;
       while(true){
    	   new Thread(){
    		   public void run(){
    			     while (true) {

    	              }  
    		   }
    	   }.start();
       }

    }

 

    /**

     * @param args

     * @Author YHJ create at 2011-11-12 ����08:19:21

     */

    public static void main(String[] args) {

       StackMemoryOverFlow stackOverFlow = new StackMemoryOverFlow();

       try {

           stackOverFlow.plus();

       } catch (Exception e) {

           System.out.println("Exception:stack length:"+stackOverFlow.i);

           e.printStackTrace();

       } catch (Error e) {

           System.out.println("Error:stack length:"+stackOverFlow.i);

           e.printStackTrace();

       }

 

    }
}
