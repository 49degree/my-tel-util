package com.custom.update;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.custom.network.HttpRequest;
import com.custom.utils.SharedPreferencesUtils;


public class CustomUtils {
	final static String TAG = "CustomUtils";
	String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "customDemo"+File.separator;
	Context context = null;
	public CustomUtils(Context context){
		this.context = context;
		//下载之前要判断SD卡路径是否有效，如果用户没插SD卡，要把下载位置改成本机
		// 获取扩展SD卡设备状态   
		String sDStateString = android.os.Environment.getExternalStorageState();   
		// 拥有可读可写权限   
		if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {   
			createDir(path);
		}else{
			path = context.getFilesDir()+File.separator;
		}
	}
    
    public void queryInfo(){
        java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        HashMap<String,String> params = new HashMap<String,String>();
        HttpRequest httpRequest = new HttpRequest(Constant.QUERY_URL,params,context);
        String retStr = httpRequest.getResponsString(false);
        Log.i(TAG, "==================="+retStr);
        try {  
        	//解析数据
        	
            //保存数据到配置文件
            Iterator it = handler.getAppInfo().keySet().iterator();
            while(it.hasNext()){
            	String key = (String)it.next();
            	String[] info = handler.getAppInfo().get(key);
            	StringBuffer infoBuffer = new StringBuffer();
            	for(int i=0;i<info.length;i++){
            		infoBuffer.append(info[i]).append("|"); 
            	}
            	SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.NEW_APP_INFO, key, infoBuffer.toString());
            }
        } catch (SAXException e) {  

            e.printStackTrace();  

        } catch (ParserConfigurationException e) {  

            e.printStackTrace();  

        } catch (IOException e) {  

            e.printStackTrace();  

        } 
    }
    
    public boolean updateInstalledInfo(String packageName){}
    
    public boolean checkAndInstalledApp(){}
    
    public boolean downAndInstall(String packetName,String appinfos){}
	public static void SaveIncludedFileIntoFilesFolder(int resourceid, String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = is.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		is.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}
	
	/**
	 * 判断目录是否存在，不存在则创建文件夹，成功返回文件夹的路径，失败返回�?
	 * @param filePath
	 */
	private String createDir(String filePath) {
		File fileDir = null; // 文件流变
		boolean hasDir = false; // 标示文件流对象是否存
		fileDir = new File(filePath); // 生成文件流对
		hasDir = fileDir.exists(); // 判断文件流对象是否存
		if (!hasDir) {
			String[] fileDirs = filePath.split("/");
			StringBuffer fileDirStr = new StringBuffer();
			for(int i=0;i<fileDirs.length;i++){
				fileDir = new File(fileDirStr.append("/").append(fileDirs[i]).toString());
				if(!fileDir.exists()){
					hasDir = fileDir.mkdir();
				}
			}
			//hasDir = fileDir.mkdir();
		}
		//判断是否成功
		if(!hasDir){
			filePath = null;
		}
		return filePath;
	}
}
