import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
基于HTTP的协议的网络文件下载，工作原理建立在请求/响应模式(Request/Response) 上：一个客户端与服务器建立连接后，客户端向Web服务器发出一个HTTP请求行；Web服务器在收到有效的请求后，返回一个状态行或多个响应标题、一个空白行和相关文档。HTTP协议使用的端口号，通常为80。开发基于http协议的网络文件下载必须掌握HTTP协议,包括向服务器发送请求和获取服务器响应状态的过程。HTTP1.0协议不支持断点续传功能. 本文用java提供了一个简单的实现.

1.HTTP协议工作方式
    基于HTTP协议的客户/服务器模式的信息交换过程，它分四个过程：建立连接、发送请求信息、获取服务器响应状态、关闭连接。其中较为复杂的过程是:发送请求信息、获取服务器响应状态.
1)向服务器发送请求信息 
　　一个HTTP请求由一个请求行、可选数目的请求标题、一个空白行，以及在POST情况下的一些额外的数据组成。请求行的格式是：
     Method Request-URI HTTP-Vsersion CRLF
      Method表示请求方法，如GET和POST
      Request-URI标识了要请求的资源，
      HTTP-Version表示请求的HTTP协议版本，
      CRLF表示回车换行。

     HTTP请求报文格式如下:

 
        HTTP Command:       //方法字段(GET方法,POST方法)
        URI:                //URL字段，发送请求至保存该网站的服务器。
        HTTP Version:       //http协议版本字段
        Accept:             //指示可被接受的请求回应的介质类型范围列表。
        Accept-Language:    //限制了请求回应中首选的语言为简体中文，否则使用默认值。
        Accept-Encoding:    //限制了回应中可接受的内容编码值
        User-Agent:         //定义用户代理，即发送请求的浏览器类型
        Host:               //定义了目标所在的主机
        Connection:         //告诉服务器使用连接类型 

2)获取服务器响应状态 
　　在发送HTTP请求行以后，程序就可以读取服务器的响应状态了。HTTP响应状态行包括：HTTP 状态码和一些HTTP响应标题。 
　　HTTP回应报文

        HTTP Version: HTTP/1.1       //服务器用的是HTTP/1.1版本
        HTTP Status: 200             //请求成功，信息可以读取，包含在响应的报文中
        Date:                        //响应报文的时间
        Server:                      //响应报文的服务器
        X-Powered-By:                //表明运行环境
        Set- cookie:                 //
        Vary:                        //
        Content-Length:             //表明实体的长度
        Connection:                 //告诉客户机在报文发送完毕后连接的状态
        Content-Type:              //表明实体中的对象类型(html文档)
        Binary Data:              //二进制数据 

状态码表示响应类型，常用的有：
1××　　保留
2××　　表示请求成功地接收
3××　　为完成请求客户需进一步细化请求
4××　　客户错误
5××　　服务器错误
在程序中间，如果读到“HTTP/1.1 200 OK”这样的字符串，表明欲下载文件存在、该服务器支持断点续传，可以使用多线程下载。如果读到“HTTP/1.0 200 OK”这样的字符串，表明欲下载文件存在、但该服务器不支持断点续传，只可以使用单线程下载。 


http://zhangwenzhuo.iteye.com/blog/141900
http://blog.csdn.net/zliu789/article/details/3885919
*/

public class TestDownFile extends Thread {
	public static void main(String[] args){
		String url = "http://www.a3650.com/pos_update/TB_FSK_POS/FSK_POS_1.0.1.6.apk";
		String filePath = "e://temp.apk";
		try{
			TestDownFile.httpOnLoad(filePath, url);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//new TestDownFile1().down();
		
	}
	
	

	/**
	 * 多线程下载
	 */
	URL url;
	int startPosition;
	RandomAccessFile threadFile;
	int block;

	public TestDownFile(URL url, int startPosition,
			RandomAccessFile threadFile, int block) {
		this.url = url;
		this.startPosition = startPosition;
		this.threadFile = threadFile;
		this.block = block;
	}

	@Override
	public void run() {
		super.run();
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) url.openConnection();
			//con.setRequestMethod("POST");
			//con.setRequestProperty("Range", "betys" + startPosition + "-");
			con.setRequestProperty("User-Agent","Internet Explorer");
			String sProperty = "bytes=" + startPosition + "-";
			// 告诉服务器book.rar这个文件从nStartPos字节开始传
			con.setRequestProperty("Range", sProperty);
			
			con.setReadTimeout(6000);
			InputStream inputStream = con.getInputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			int readFileSize = 0;
			System.out.println(this.getName()+":"+this.block);
			while (readFileSize <= block
					&& (len = inputStream.read(buffer)) != -1) {
				threadFile.write(buffer, 0, len);
				readFileSize += len;// 累计下载文件大小
			}
			threadFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 断点续传和多线程下载
	public static void httpOnLoad(String fileName, String urlpath) throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET");
		conn.connect();
		int responseCode = conn.getResponseCode();
		
		
		System.err.println("Code : " + responseCode);
		System.err.println("getResponseMessage : " + conn.getResponseMessage());
		
		if (responseCode >= 400) {
			
			return ; // -2 represent access is error
		}
		
//		String sHeader;
//		for (int i = 1;; i++) {
//			sHeader = conn.getHeaderFieldKey(i);
//			if (sHeader != null) {
//				System.out.println(sHeader+":"+conn.getHeaderField(sHeader));
//			} else
//				break;
//		}
		
		int threadSize = 3;
		int fileLength = conn.getContentLength();
		System.out.println("fileLength:"+fileLength);
		int block = fileLength / threadSize;
		int lastBlock = fileLength-(block*(threadSize-1));
		conn.disconnect();
		File file = new File(fileName);
		RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
		randomFile.setLength(fileLength);
		randomFile.close();
		for (int i = 2; i < 3; i++) {
			int startPosition = i * block;// 从网络文件的什么位置开始下载
			if(i==threadSize-1){
				block = lastBlock;
			}
			RandomAccessFile threadFile = new RandomAccessFile(file, "rw");
			threadFile.seek(startPosition);
			new TestDownFile(url, startPosition, threadFile, block).start();
		}
	}
	/*********************************************************************************
	 * 多线程下载结束
	 ********************************************************************************/

	/**
	 * 单线程下载
	 * @author Administrator
	 *
	 */
	public static class TestDownFile1 {
		public  void down() {
			String sURL = "http://www.a3650.com/pos_update/TB_FSK_POS/FSK_POS_1.0.1.6.apk";
			int nStartPos = 0;
			int nRead = 0;
			String sName = "temp.apk";
			String sPath = "e://";
			try {
				URL url = new URL(sURL);
				// 打开连接
				HttpURLConnection httpConnection = (HttpURLConnection) url
						.openConnection();
				// 获得文件长度
				long nEndPos = getFileSize(sURL);
				RandomAccessFile oSavedFile = new RandomAccessFile(sPath + "//"
						+ sName, "rw");
				httpConnection.setRequestProperty("User-Agent","Internet Explorer");
				String sProperty = "bytes=" + nStartPos + "-";
				// 告诉服务器book.rar这个文件从nStartPos字节开始传
				httpConnection.setRequestProperty("RANGE", sProperty);
				System.out.println(sProperty);
				InputStream input = httpConnection.getInputStream();
				byte[] b = new byte[1024];
				// 读取网络文件,写入指定的文件中
				while ((nRead = input.read(b, 0, 1024)) > 0
						&& nStartPos < nEndPos) {
					oSavedFile.write(b, 0, nRead);
					nStartPos += nRead;
				}
				httpConnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 获得文件长度

		public static long getFileSize(String sURL) {
			int nFileLength = -1;
			try {
				URL url = new URL(sURL);
				HttpURLConnection httpConnection = (HttpURLConnection) url
						.openConnection();
				httpConnection.setRequestProperty("User-Agent",
						"Internet Explorer");
				int responseCode = httpConnection.getResponseCode();
				if (responseCode >= 400) {
					System.err.println("Error Code : " + responseCode);
					return -2; // -2 represent access is error
				}
				System.out.println(httpConnection.getContentLength());
				String sHeader;
				for (int i = 1;; i++) {
					sHeader = httpConnection.getHeaderFieldKey(i);
					if (sHeader != null) {
						if (sHeader.equals("Content-Length")) {
							nFileLength = Integer.parseInt(httpConnection.getHeaderField(sHeader));
							break;
						}
					} else
						break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(nFileLength);
			return nFileLength;
		}
	}  
}
