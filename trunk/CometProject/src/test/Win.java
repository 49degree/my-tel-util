package test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Win {
	static int idNo = 0;
	public static void main(String args[]) {
		for(int i=1;i<2;i++){
			idNo = i;
			new Thread(){
				public void run(){
					OutputStream out = null;
					InputStream in= null;
					HttpURLConnection connection =  null;
					try {
						//确定服务地址
					    URL url = new URL("http://localhost:8080/CometProject/keeper?idNo="+idNo);
					    connection =  (HttpURLConnection)url.openConnection();
					    connection.setDoInput(true);
					    connection.setDoOutput(true);
					    // Post 请求不能使用缓存  
					    connection.setUseCaches(false);  
					    // 设定传送的内容类型是可序列化的java对象  
					    // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)  
					    connection.setRequestProperty("Content-type", "application/x-java-serialized-object");  
					    // 设定请求的方法为"POST"，默认是GET  
					    connection.setRequestMethod("POST");
					    // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，  
					    connection.connect();  
					    
					    out = connection.getOutputStream();
					    
					    
	                	out.write("收到心跳包".getBytes("GBK"));
	                	out.flush();
	                	
	                	in  =connection.getInputStream();
	                	
					    int n = -1;
					    byte[] b = new byte[1024];
					    int times = 0;
					    //从服务端读取数据并打印
					    System.out.println("时间1："+System.currentTimeMillis());
					    while((n=in.read(b))!=-1&&times++<10)
			            {
					    	System.out.println("时间2："+System.currentTimeMillis());
			                String s=new String(b,0,n,"GBK");
			                if(s.indexOf("发送心跳")!=-1){
			                	System.out.println("时间3："+System.currentTimeMillis());
			                	out.write("收到心跳包".getBytes("GBK"));
			                	out.flush();
			                }
			                System.out.println(s);   
			                Thread.sleep(1000);
			            }
					    
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						try{
							if(in!=null)
								in.close();
						}catch(Exception e){
							
						}
						try{
							if(out!=null)
								out.close();
						}catch(Exception e){
							
						}
					}

				}
			}.start();

		}


		    

	}
}
