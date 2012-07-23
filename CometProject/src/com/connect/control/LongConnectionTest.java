package com.connect.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Date;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class LongConnectionTest extends HttpServlet implements Servlet {
	static Logger logger = Logger.getLogger(LongConnectionTest.class);
	static final long serialVersionUID = 1L;

	public LongConnectionTest() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		
		
		String idNo = request.getParameter("idNo");
		InputStream reader = request.getInputStream();
		OutputStream pr = response.getOutputStream();
		LongConnectManager longConnectManager = new LongConnectManager(reader,pr);
		int n = -1;
		byte[] cbuf = new byte[1024];
		int times = 0;
		try {
			String msg = "发送心跳";
			longConnectManager.start();
			while (true) {
				logger.debug(System.currentTimeMillis()+":out put:" + idNo+":发送消息："+msg);
				pr.write(msg.getBytes("GBK"));
				pr.flush();
				while ((n = reader.read(cbuf)) == -1&&times++<100) {
					//logger.debug("未收到消息");//连接已经断开
					Thread.sleep(100);
				}
				if(n==-1){
					logger.debug("未收到消息");//连接已经断开
					longConnectManager.setStop(true);
					longConnectManager.interrupt();
					break;
				}else{
					String s = new String(cbuf, 0, n,"GBK");
					logger.debug("收到消息："+s);
				}
				Thread.sleep(1000);
			}
			pr.write("断开连接".getBytes("GBK"));
			pr.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(reader!=null)
				reader.close();
			if(pr!=null)
				reader.close();
		}

	}
	
	public class LongConnectManager extends Thread{
		InputStream reader;
		OutputStream pr;
		boolean stop = false;
		public LongConnectManager(InputStream reader,
				OutputStream pr){
			this.reader = reader;
			this.pr = pr;
		}
		
		public void run(){
			try {
				Date now = new Date();
				int n = -1;
				byte[] cbuf = new byte[1024];
				int times = 0;
				
				String msg = "服务器时间：";
				while (!stop) {
					try {
						pr.write((msg+new Date()).getBytes("GBK"));
						// flush的作用很重要，当你任务写给客户端的数据总够多的时候
						// 调用之，客户端方能读取到。
						// 否则，在数据长度达到上限或者连接关闭之前，客户端读不到数据
						pr.flush();
						// 从服务端读取数据并打印
						while ((n = reader.read(cbuf)) == -1&&times++<100) {
							Thread.sleep(100);
						}
						if(n==-1){
							logger.debug("未收到消息");
						}else{
							String s = new String(cbuf, 0, n,"GBK");
							logger.debug("收到消息："+s);
						}
						Thread.sleep(5000);
					} catch (SocketException se) {
						se.printStackTrace();
						break;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void setStop(boolean stop){
			this.stop = stop;
		}
	}
	
}