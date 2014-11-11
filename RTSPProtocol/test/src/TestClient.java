import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.yangxp.rtsp.client.RtspClient;
import com.yangxp.rtsp.client.impl.RtspClientImpl;
import com.yangxp.rtsp.message.ContentBase;
import com.yangxp.rtsp.message.Message;
import com.yangxp.rtsp.message.RequestMessage;


public class TestClient {
	static Logger logger = Logger.getLogger(TestClient.class);
	
	public static void main1(String[] args){
		String a = "abcd\r\n";
		char[] buffer = new char[10];
		System.out.println(a.indexOf("\r\n")+":"+Arrays.toString(a.getBytes()));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(a.getBytes())));
		try {
			reader.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		a = new String(buffer);
		System.out.println(a.indexOf("\r\n")+":"+Arrays.toString(buffer));
		
		
		
	}

	public static void main(String[] args){
		
		RtspClient client = new RtspClientImpl("rtsp://172.0.0.200:8086/spydroid.sdp");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RequestMessage inta = new RequestMessage();
		inta.setMethod(Message.Method.OPTIONS);
		inta.setPVersion("RTSP/1.0");
		inta.setCseq(1);
		inta.setServer("my test server");
		inta.setContent(ContentBase.Factory.getDefault().setContent("v=0\r\n  o=MediaBox 127992 137813 IN IP4 0.0.0.0\r\n  s=RTSP Session\r\n  i=Starv Box Live Cast\r\n  c=IN IP4 218.207.101.236\r\n  t=0 0"));
		
		
		
		client.request(inta);
		client.request(inta); 
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//client.request(inta);
		
		
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
