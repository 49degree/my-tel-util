package test;
import junit.framework.TestCase;

import com.a3650.posserver.core.init.socket.SocketInitContext;


public class TestPosSocketCase extends TestCase{
	public void testServer(){
		SocketInitContext.start();
	}
}
