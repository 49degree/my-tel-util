
public class Hello {
	   public native void sayHello();
	    static
	    {
	        //System.loadLibrary("liblibhello");
	        System.load("E:\\workgroup\\JavaHello\\bin\\liblibhello.dll");
	    }
	    
	    
	    public static void main(String[] args)
	    {
	    	String str = "17|http://223.4.87.42/pkg/Yuele.apk|Yuele.apk|com.yuele.activity|.StartActivity|";
	    	String[] test = str.split("\\|");
	    	System.out.println(test[0]+":"+test[1]+":"+test[2]);
	    }

}
