
public class Hello {
	   public native void sayHello();
	    static
	    {
	        //System.loadLibrary("liblibhello");
	        System.load("E:\\workgroup\\JavaUtils\\bin\\liblibhello.dll");
	    }
	    
	    
	    public static void main(String[] args)
	    {
	    	String str = "17|http://223.4.87.42/pkg/Yuele.apk|Yuele.apk|com.yuele.activity|.StartActivity|";
	    	String[] test = str.split("\\|");
	    	System.out.println(test[0]+":"+test[1]+":"+test[2]);
	    	
	    	
	    	
	    	String c = "b";
	    	String d = String.valueOf("b");
	    	System.out.print("c=d?");
	    	if(c==d){
	    		System.out.println(true);
	    	}else{
	    		System.out.println(false);
	    	}
	    	c = "b";
	    	d = new String("b");
	    	System.out.print("c=d?");
	    	if(c==d){
	    		System.out.println(true);
	    	}else{
	    		System.out.println(false);
	    	}
	    	
	    }

}
