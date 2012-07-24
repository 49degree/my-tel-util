package test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Win2 {
	static int idNo = 0;
	public static void main(String args[]) {
		for (int i = 1; i < 2; i++) {
			idNo = i;
			run();
			// new Thread(){
			// @Override
			// public void run() {
			// run();
			// }
			//
			// }.start();

		}
	}

	static String sessionid = null;
	public static void run() {
		OutputStream out = null;
		InputStream in = null;
		HttpURLConnection connection = null;
		try {
			// 确定服务地址
			URL url = new URL("http://localhost:8080/CometProject/Weather");
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
//			 connection.setRequestProperty("Content-type",
//			 "application/x-java-serialized-object");
			// 设定请求的方法为"POST"，默认是GET
			connection.setRequestMethod("POST");

			// 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
			if (sessionid != null) {
				connection.setRequestProperty("Cookie", sessionid);
			}

			// // DataOutputStream流
			out = connection.getOutputStream();


			connection.connect();
			
			Map<String, List<String>> fields = connection.getHeaderFields();
			for (Entry<String, List<String>> entity : fields.entrySet()) {
				for (String value : entity.getValue()) {
					System.out.println("entity:" + entity.getKey() + ":"
							+ entity.getValue().get(0));
				}
			}
			
			System.out.println("============:"+connection.getResponseMessage());
			in = connection.getInputStream();

			if(fields.containsKey("Set-Cookie")){
				sessionid = fields.get("Set-Cookie").get(0).split(";")[0];
			}
			

			int n = -1;
			byte[] b = new byte[1024];
			int times = 0;
			// 从服务端读取数据并打印
			System.out.println("时间1：" + System.currentTimeMillis());
			n = in.read(b);
			while (times++ < 100) {
				if (n == -1) {
					Thread.sleep(100);
				} else {
					System.out.println("时间2：" + System.currentTimeMillis());
					String s = new String(b, 0, n, "GBK");
					System.out.println(s);
					out.write("client".getBytes());
					out.flush();
					// times = 0;
				}
				if ((n = in.available()) != 0) {
					n = in.read(b);
				} else {
					n = -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {

			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

			}
			connection.disconnect();
		}

	}
}
