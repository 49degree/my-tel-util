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
 * webservice������
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
	 * �����������
	 * @param name
	 * @param value
	 */
	public void addProperty(String name,String value){
		mEnvelopData.inParameters.put(name, value);
	}
	
	/**
	 * �������ӳ�ʱʱ��\��ȡ���ݳ�ʱʱ��
	 * @param name
	 * @param value
	 */
	public void setTimeOut(int mConnectTimeout,int mReadTimeout){
		mHttpConnection.setConnectTimeout(mConnectTimeout);
		mHttpConnection.setReadTimeout(mReadTimeout);
	}
	/**
	 * ��������
	 * @return
	 * @throws Exception
	 */
	public String sendRequest(){
		makeRequestStr();//�������
		mHttpConnection.setConnectionHeader();
		String returnInfo = null;
		InputStream reponseStream = null;
		try{
			mHttpConnection.connect();//��������
			reponseStream = mHttpConnection.call();//��ȡ����������
		}catch(IOException ioe){
			ioe.printStackTrace();
			returnInfo = Base64Coder.encodeByte(("F���ӷ�����ʧ��").getBytes());
			return returnInfo;
		}
			
		try {	
			/**��ȡ���������ص��ַ���
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
					returnInfo = Base64Coder.encodeByte(new String("F��������Ϊ��").getBytes());
				}else{
					returnInfo = parserHttpResult.getResultString();
				}
			}else{
				returnInfo = Base64Coder.encodeByte(new String("F�������������ݴ���").getBytes());
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
		//�����������
		soapRequestStr = new StringBuffer();
		soapRequestStr.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//����SOAP�����ռ�
		soapRequestStr.append("<soap:Envelope ");
		soapRequestStr.append("xmlns:soap=\"").append(mEnvelopHeader.mEnv).append("\" ");
		soapRequestStr.append("soap:encodingStyle=\"").append(mEnvelopHeader.mEnc).append("\" ");
		soapRequestStr.append("xmlns:xsd=\"").append(mEnvelopHeader.mXsd).append("\" ");
		soapRequestStr.append("xmlns:xsi=\"").append(mEnvelopHeader.mXsi).append("\">");

		//��ʼ��������
		soapRequestStr.append("<soap:Body>");
		
		//���ʵķ��������ռ�
		soapRequestStr.append("<").append(mEnvelopData.strMethodName);
		soapRequestStr.append(" xmlns=\"").append(mEnvelopData.strNameSpace).append("\">");
		//�������
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
	 * ��װHTTP���Ӷ���
	 * @author Administrator
	 *
	 */
	public class SoapHttpConnection{
		private HttpURLConnection connection;//http���Ӷ���
		private int mConnectTimeout = 12000;//���ӳ�ʱʱ��
		private int mReadTimeout = 12000;//��ȡ��ʱʱ��
		
		/**
		 * ��ʼ�����Ӷ���
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
		 * �������ӳ�ʱʱ��
		 * @param milliseconds
		 */
		public void setConnectTimeout(int milliseconds){
			mConnectTimeout = milliseconds;
		}
		
		/**
		 * ���ö�ȡ���ݳ�ʱʱ��
		 * @param milliseconds
		 */
		public void setReadTimeout(int milliseconds){
			mReadTimeout = milliseconds;
		}
		
		/**
		 * ����HTTP����ͷ��Ϣ
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
		 * �����������󣬷���������
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
		 * ����
		 * @throws IOException
		 */
		public void connect() throws IOException {
			this.connection.connect();
		}

		/**
		 * �Ͽ�����
		 */
		public void disconnect() {
			this.connection.disconnect();
		}
	}
	/**
	 * ����SOAP���ص�����
	 * @author Administrator
	 *
	 */
	public class ParserHttpResult {	
		
		private InputStream inputStream = null;//���󷵻ص�����������
		private String outParameterName = null;//���ز�������
		private String resultString = null;
		
		
		public ParserHttpResult(InputStream inputStream,String outParameterName){
			this.inputStream = inputStream;
			this.outParameterName = outParameterName; 
		}
		
		public String getResultString(){
			return resultString;
		}

		/**
		 * ������������
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
