import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.yangxp.rtsp.message.ContentBase;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.RequestMessage;
import com.yangxp.rtsp.utils.MyStreamHandlerFactory;


public class TestMessage {
	static Logger logger = Logger.getLogger(TestMessage.class);
	
	static{
		URL.setURLStreamHandlerFactory(new MyStreamHandlerFactory());
	}
	public static void main(String[] args){
		RequestMessage inta = new RequestMessage();
		inta.setMethod(Message.Method.OPTIONS);
		try {
			inta.setUrl(new URL("rtsp://218.207.101.236:554/mobile/3/67A451E937422331/8jH5QPU5GWS07Ugn.sdp/trackID=1"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inta.setPVersion("RTSP/1.0");
		inta.setCseq(1);
		inta.setServer("my test server");
		inta.setContent(ContentBase.Factory.getDefault().setContent("v=0\r\n  o=MediaBox 127992 137813 IN IP4 0.0.0.0\r\n  s=RTSP Session\r\n  i=Starv Box Live Cast\r\n  c=IN IP4 218.207.101.236\r\n  t=0 0 "));
		
		
		
		
		logger.debug(inta.getContent().getContentBuffer().length+"\n"+inta.packageMessage());
		logger.debug("parseRequest:"+Arrays.toString(inta.packageMessage().getBytes()));
		logger.debug("=============================================");
		
		/*
		String msg = inta.packageMessage();
		
		logger.debug(msg+"\n=============================================");
		
		RequestMessage inta1 = new RequestMessage(msg);
		logger.debug(inta1.getContent().getContent());
		logger.debug("=============================================");
		
		ResponseMessage resp = new ResponseMessage();
		resp.setResultCode(Message.RESULT.STATUS_OK.resultCode);
		resp.setResultMsg(Message.RESULT.STATUS_OK.resultMsg);
		try {
			inta.setUrl(new URL("rtsp://218.207.101.236:554/mobile/3/67A451E937422331/8jH5QPU5GWS07Ugn.sdp/trackID=1"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.setPVersion("RTSP/1.0");
		resp.setCseq(1);
		resp.setServer("my test server");
		resp.setContent(ContentBase.Factory.getDefault().setContent("v=0\r\n  o=MediaBox 127992 137813 IN IP4 0.0.0.0\r\n  s=RTSP Session\r\n  i=Starv Box Live Cast\r\n  c=IN IP4 218.207.101.236\r\n  t=0 0 "));
		logger.debug(resp.packageMessage());
		*/
	}
}
