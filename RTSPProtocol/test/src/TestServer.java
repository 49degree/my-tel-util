import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.yangxp.rtsp.server.impl.RtspServerImpl;


public class TestServer {
	static Logger logger = Logger.getLogger(TestServer.class);

	public static void main(String[] args){

		RtspServerImpl server = new RtspServerImpl(8086);
	}
}
