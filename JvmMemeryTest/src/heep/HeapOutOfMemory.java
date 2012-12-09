package heep;

import java.util.ArrayList;
import java.util.List;

public class HeapOutOfMemory {
    /**

     * @param args

     * @Author YHJ create at 2011-11-12 ÏÂÎç07:52:18

     */

    public static void main(String[] args) {

       List<String> cases = new ArrayList<String>();
       String b = "b";
       while(true){

           cases.add(b);

       }

    }
    
    /**

     * @Described£º²âÊÔÓÃÀı

     * @author YHJ create at 2011-11-12 ÏÂÎç07:55:50

     * @FileNmae com.yhj.jvm.memory.heap.HeapOutOfMemory.java

     */

    public static class TestCase{

       String a = new String("bb");
       String b = "cc";

    }
}
