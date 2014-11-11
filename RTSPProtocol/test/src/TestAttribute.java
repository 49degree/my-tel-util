import java.net.URL;

import org.apache.log4j.Logger;

import com.yangxp.rtsp.utils.MyStreamHandlerFactory;


public class TestAttribute {
	static Logger logger = Logger.getLogger(TestAttribute.class);
	static{
		URL.setURLStreamHandlerFactory(new MyStreamHandlerFactory());
	}
	
	public static void main(String[] args){
		//AttributeInt inta = new AttributeInt("a:111");
		do{
			logger.debug("------");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(true);
		
	}
}
