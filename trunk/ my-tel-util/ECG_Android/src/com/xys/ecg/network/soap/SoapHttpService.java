package com.xys.ecg.network.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.xys.ecg.utils.Base64Coder;


/**
 * webservice连接类
 * @author Administrator
 *
 */
public class SoapHttpService {
	private SoapHttpConnection mHttpConnection = null;
	private SoapEnvelopHeader mEnvelopHeader = null;
	private SoapEnvelopData mEnvelopData = null;
	private StringBuffer soapRequestStr = null;
	
	public SoapHttpService(String nameSpace,String methodName,String wsdlAddr,String outParameterName)
	{
		mEnvelopHeader = new SoapEnvelopHeader(SoapEnvelopHeader.VER11);
		mEnvelopData = new SoapEnvelopData(nameSpace,methodName,outParameterName);
		try{
			mHttpConnection = new SoapHttpConnection(wsdlAddr);
		}catch(IOException io){
		}
	}
	
	/**
	 * 增加请求参数
	 * @param name
	 * @param value
	 */
	public void addProperty(String name,String value){
		mEnvelopData.inParameters.put(name, value);
	}
	
	/**
	 * 配置连接超时时间\读取数据超时时间
	 * @param name
	 * @param value
	 */
	public void setTimeOut(int mConnectTimeout,int mReadTimeout){
		mHttpConnection.setConnectTimeout(mConnectTimeout);
		mHttpConnection.setReadTimeout(mReadTimeout);
	}
	/**
	 * 发送请求
	 * @return
	 * @throws Exception
	 */
	public String sendRequest(){
		makeRequestStr();//构造入参
		mHttpConnection.setConnectionHeader();
		String returnInfo = null;
		InputStream reponseStream = null;
		try{
			mHttpConnection.connect();//建立连接
			reponseStream = mHttpConnection.call();//获取返回输入流
		}catch(IOException ioe){
			ioe.printStackTrace();
			returnInfo = Base64Coder.encodeByte(("F连接服务器失败").getBytes());
			return returnInfo;
		}
			
		try {	
			/**获取输入流返回的字符串
			byte[] bt = new byte[1024];
			int bytesRead = 0;
			String res = "";
	         while (true) {
	              bytesRead = reponseStream.read(bt, 0, 1024); // return final read bytes counts
	              if (bytesRead == -1) {// end of InputStream
	                     break;
	              }
	              res += new String(bt, 0, bytesRead, "UTF-8"); // convert to string using bytes
	         }
	         Log.i("inputStream",res);
			*/
			ParserHttpResult parserHttpResult = new ParserHttpResult(reponseStream,mEnvelopData.outParameterName);
			if(parserHttpResult.parseResponse()){
				if(parserHttpResult.getResultString()==null){
					returnInfo = Base64Coder.encodeByte(new String("F返回数据为空").getBytes());
				}else{
					returnInfo = parserHttpResult.getResultString();
				}
			}else{
				returnInfo = Base64Coder.encodeByte(new String("F服务器返回数据错误").getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnInfo = Base64Coder.encodeByte(("F"+e.toString()).getBytes());

		}finally{
			mHttpConnection.disconnect();
		}
		
		return returnInfo;
	}
	
	public void makeRequestStr()
	{
		//定义编码类型
		soapRequestStr = new StringBuffer();
		soapRequestStr.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//构造SOAP命名空间
		soapRequestStr.append("<soap:Envelope ");
		soapRequestStr.append("xmlns:soap=\"").append(mEnvelopHeader.mEnv).append("\" ");
		soapRequestStr.append("soap:encodingStyle=\"").append(mEnvelopHeader.mEnc).append("\" ");
		soapRequestStr.append("xmlns:xsd=\"").append(mEnvelopHeader.mXsd).append("\" ");
		soapRequestStr.append("xmlns:xsi=\"").append(mEnvelopHeader.mXsi).append("\">");

		//开始构造数据
		soapRequestStr.append("<soap:Body>");
		
		//访问的方法命名空间
		soapRequestStr.append("<").append(mEnvelopData.strMethodName);
		soapRequestStr.append(" xmlns=\"").append(mEnvelopData.strNameSpace).append("\">");
		//构造入参
		Set parametersKey = mEnvelopData.inParameters.keySet();
		String parameter = null;
		for (Object key:parametersKey)
		{
			parameter = mEnvelopData.inParameters.get(key);
			soapRequestStr.append("<").append(key).append(">");
			soapRequestStr.append(parameter);
			soapRequestStr.append("</").append(key).append(">");
		}
		soapRequestStr.append("</").append(mEnvelopData.strMethodName).append(">");
		soapRequestStr.append("</soap:Body>");
		soapRequestStr.append("</soap:Envelope>");
	}
	
	
	
	/**
	 * 封装HTTP连接对象
	 * @author Administrator
	 *
	 */
	public class SoapHttpConnection{
		private HttpURLConnection connection;//http连接对象
		private int mConnectTimeout = 12000;//连接超时时间
		private int mReadTimeout = 12000;//读取超时时间
		
		/**
		 * 初始化连接对象
		 * @param wsdlAddr
		 * @throws IOException
		 */
		public SoapHttpConnection(String wsdlAddr) throws IOException{
			connection = ((HttpURLConnection)new URL(wsdlAddr).openConnection());
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
		}

		/**
		 * 配置连接超时时间
		 * @param milliseconds
		 */
		public void setConnectTimeout(int milliseconds){
			mConnectTimeout = milliseconds;
		}
		
		/**
		 * 配置读取数据超时时间
		 * @param milliseconds
		 */
		public void setReadTimeout(int milliseconds){
			mReadTimeout = milliseconds;
		}
		
		/**
		 * 配置HTTP请求头信息
		 */
		public void setConnectionHeader()
		{		
			//set timeout
			connection.setConnectTimeout(mConnectTimeout);
			connection.setReadTimeout(mReadTimeout);
			connection.setRequestProperty("Content-Length",String.valueOf(soapRequestStr.length()));
			connection.setRequestProperty("Content-Type","text/xml; charset=utf-8");
			connection.setRequestProperty("SOAPAction",mEnvelopData.strNameSpace+mEnvelopData.strMethodName);
		}
		

		/**
		 * 发送连接请求，返回输入流
		 * @return
		 * @throws IOException
		 */
		public InputStream call() throws IOException
		{
			OutputStream os = connection.getOutputStream();
		    os.write(soapRequestStr.toString().getBytes(), 0, soapRequestStr.length());
		    os.flush();
		    os.close();
		    return connection.getInputStream();
		}
		
		/**
		 * 连接
		 * @throws IOException
		 */
		public void connect() throws IOException {
			this.connection.connect();
		}

		/**
		 * 断开连接
		 */
		public void disconnect() {
			this.connection.disconnect();
		}
	}
	/**
	 * 解析SOAP返回的数据
	 * @author Administrator
	 *
	 */
	public class ParserHttpResult {	
		
		private InputStream inputStream = null;//请求返回的输入流对象
		private String outParameterName = null;//返回参数名称
		private String resultString = null;
		
		
		public ParserHttpResult(InputStream inputStream,String outParameterName){
			this.inputStream = inputStream;
			this.outParameterName = outParameterName; 
		}
		
		public String getResultString(){
			return resultString;
		}

		/**
		 * 解析返回数据
		 * @return
		 */
		public boolean parseResponse() throws Exception
		{
			boolean isFailure = false;
			try{
				if ( inputStream == null )
					return isFailure;
				XmlPullParser parser = Xml.newPullParser(); 
				parser.setInput(inputStream,"UTF-8");
				
				int event = parser.getEventType();
				while ( event != XmlPullParser.END_DOCUMENT ){
					
					switch ( event ) {	
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:	

						if ( outParameterName.equals(parser.getName()))
						{
							String xmlText = parser.nextText();
							isFailure = true;
							if ( xmlText != null )
							{
								resultString = xmlText;
							}
						}
						break;
					case XmlPullParser.END_DOCUMENT:
						break;
					default:
						break;	
					}
					event = parser.next();
				}
			}catch(Exception e){
				throw e;
			}

			return isFailure;
		}
	}
}
