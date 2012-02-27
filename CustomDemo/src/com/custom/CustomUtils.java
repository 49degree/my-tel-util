package com.custom;
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

import com.custom.Constant;
import com.custom.SharedPreferencesUtils;
import com.custom.XMLHandler;
import com.custom.network.HttpRequest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;


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
//		if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {   
//			createDir(path);
//		}else{
//			path = context.getFilesDir()+File.separator;
//		}
		path = context.getFilesDir()+File.separator;
	}
	
	public void wakeUpApp(){
		Log.i(TAG, "===================wakeUpApp");
		Map<String,String> appInfos = (Map<String,String>)SharedPreferencesUtils.getConfigAll(SharedPreferencesUtils.INSTALLED_APP_INFO);
		Log.i("getInstallerPackageName",appInfos.size()+"");
		if(appInfos.size()<=0)
			return ;
		
		// 查询是否存在应该安装的应用
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		PackageInfo packageInfo = null;
        Iterator it = appInfos.keySet().iterator();
        
        while(it.hasNext()){
        	String key = (String)it.next();
			for (int i = 0; i < packages.size(); i++) {
				packageInfo = packages.get(i);
				if(packageInfo.packageName.equals(key)){
						try {
							//Log.i(TAG, "==================="+key);
							PackageManager packageManager = context.getPackageManager();
							Intent intent = new Intent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
							intent = packageManager
									.getLaunchIntentForPackage(key);
							context.startActivity(intent);
							/*
							 * 都知道，Context中有一个startActivity方法，
							 * Activity继承自Context，重载了startActivity方法。
							 * 如果使用Activity的startActivity方法，不会有任何限制，
							 * 而如果使用Context的startActivity方法的话，就需要开启一个新的task，
							 * 遇到上面那个异常的，都是因为使用了Context的startActivity方法。解决办法是，加一个flag。 
							 */
							Intent MyIntent = new Intent(Intent.ACTION_MAIN);
							MyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  

							MyIntent.addCategory(Intent.CATEGORY_HOME);
							context.startActivity(MyIntent);
							//依次打开软件的时候要有一定的间隔，防止同时打开软件会出现死机现象
							Thread.sleep(5*1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
				}
			}
        }
        

	}
	
	
	public void install() {
		Log.i(TAG, "===================install");
		try {
			queryInfo();//查询信息
			try {
				checkAndInstalledApp();
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

		} catch (Exception e) {

		}

	}
    
    
    public void queryInfo(){
    	//我们说到的和手机、卡相关的号码数据包括IMSI,MSISDN,ICCID，IMEIIMSI：
    	//international mobiles subscriber identity国际移动用户号码标识，这个一般大家是不知道，GSM必须写在卡内相关文件中；
    	//MSISDN:mobile subscriber ISDN用户号码，这个是我们说的139，136那个号码；
    	//ICCID:ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的；
    	//IMEI：international mobile Equipment identity
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();       //取出IMEI
        String tel = tm.getLine1Number();     //取出MSISDN，很可能为空
        String iccid =tm.getSimSerialNumber();  //取出ICCID
        String imsi =tm.getSubscriberId();     //取出IMSI
       
        long time = new Date().getTime();
        imei = String.valueOf(100000000001171L+time);
        imsi=String.valueOf(110260000000117L+time);
        iccid = String.valueOf(1901410321111851071L+time);

//        cmdid	:命令序列号		必填
//        imei		:手机IMEI		必填
//        imsi		:手机卡IMSI		必填
//        iccid		:手机卡ICCID		必填
//        mobile	:手机号			可为空
//        ctime	:手机当前时间	必填	格式:yyyyMMddHHmmss如:20111001132050
//        osver	:系统版本		必填	"1"
//        cver		:客户端版本		必填	"010101"
//        uid		:渠道ID(5位数字)	必填	可变

        java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("id", "123456");
        params.put("imei", imei);
        params.put("imsi", imsi);
        params.put("iccid",iccid);
        params.put("mobile", tel);
        params.put("ctime", sf.format(new Date()));
        params.put("osver", "1");
        params.put("cver", "010101");
        params.put("uid", "10001");
        
        HttpRequest HttpRequest = new HttpRequest(Constant.QUERY_URL,params,context);
        String retStr = HttpRequest.getResponsString(false);
        
        
       // retStr = "<response><id value =\"123456\"/><ret code=\"1\" msg=\"\" /><data><entity class=\"DataVO\"><prop name=\"ntime\" value=\"20120223060624\"/><list ref=\"items\"><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"3\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/Setting.apk\" /><prop name=\"name\" value=\"Setting.apk\" /><prop name=\"package\" value=\"com.nl\" /><prop name=\"activity\" value=\".test\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"17\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/Yuele.apk\" /><prop name=\"name\" value=\"Yuele.apk\" /><prop name=\"package\" value=\"com.yuele.activity\" /><prop name=\"activity\" value=\".StartActivity\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"23\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/1900057009_GX9.apk\" /><prop name=\"name\" value=\"1900057009_GX9.apk\" /><prop name=\"package\" value=\"com.tempus.frtravel.app\" /><prop name=\"activity\" value=\".Loading\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"24\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/FundMaster_1.0.1_htwlk.apk\" /><prop name=\"name\" value=\"FundMaster_1.0.1_htwlk.apk\" /><prop name=\"package\" value=\"wind.fundmaster\" /><prop name=\"activity\" value=\".WStockAppDelegate\" /></entity></list></entity></data></response>";
        Log.i(TAG, "==================="+retStr);
        
        try {  
        	//解析数据
        	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance(); 
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();  
            XMLHandler handler = new XMLHandler(); 
            xmlReader.setContentHandler(handler);  
            xmlReader.parse(new InputSource(new StringReader(retStr)));
            //每次获取到的软件列表要复盖之前写到那个文件中的软件列表，防止安装过期的软件
            if(handler.getAppInfo().keySet().size()>0){
            	SharedPreferencesUtils.removeConfigAll(SharedPreferencesUtils.NEW_APP_INFO);
            	
            }
            //保存数据到配置文件
            Iterator it = handler.getAppInfo().keySet().iterator();
            while(it.hasNext()){
            	String key = (String)it.next();
            	String[] info = handler.getAppInfo().get(key);
            	StringBuffer infoBuffer = new StringBuffer();
            	for(int i=0;i<info.length;i++){
            		infoBuffer.append(info[i]).append("|");
            	}
            	//Log.i("getAppInfo", "===================:"+key+":"+infoBuffer.toString());
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
    
    public boolean updateInstalledInfo(String packageName){
    	//我们说到的和手机、卡相关的号码数据包括IMSI,MSISDN,ICCID，IMEIIMSI：
    	//international mobiles subscriber identity国际移动用户号码标识，这个一般大家是不知道，GSM必须写在卡内相关文件中；
    	//MSISDN:mobile subscriber ISDN用户号码，这个是我们说的139，136那个号码；
    	//ICCID:ICC identity集成电路卡标识，这个是唯一标识一张卡片物理号码的；
    	//IMEI：international mobile Equipment identity
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();       //取出IMEI
        String tel = tm.getLine1Number();     //取出MSISDN，很可能为空
        String iccid =tm.getSimSerialNumber();  //取出ICCID
        String imsi =tm.getSubscriberId();     //取出IMSI
       
//        long time = new Date().getTime();
//        imei = String.valueOf(100000000001171L+time);
//        imsi=String.valueOf(110260000000117L+time);
//        iccid = String.valueOf(1901410321111851071L+time);

//        cmdid	:命令序列号		必填
//        imei		:手机IMEI		必填
//        imsi		:手机卡IMSI		必填
//        iccid		:手机卡ICCID		必填
//        mobile	:手机号			可为空
//        ctime	:手机当前时间	必填	格式:yyyyMMddHHmmss如:20111001132050
//        osver	:系统版本		必填	"1"
//        cver		:客户端版本		必填	"010101"
//        uid		:渠道ID(5位数字)	必填	可变

        java.text.SimpleDateFormat sf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("id", "123456");
        params.put("imei", imei);
        params.put("imsi", imsi);
        params.put("iccid",iccid);
        params.put("mobile", tel);
        params.put("ctime", sf.format(new Date()));
        params.put("osver", "1");
        params.put("cver", "010101");
        params.put("uid", "10001");
        params.put("softid", packageName);
        
        HttpRequest HttpRequest = new HttpRequest(Constant.INSTALLED_URL,params,context);
        String retStr = HttpRequest.getResponsString(false);
        Log.i(TAG, "==================="+retStr);
        if(retStr!=null&&retStr.indexOf("<ret code=\"1\"")>-1){
        	return true;
        }else{
        	return false;
        }
        
       // retStr = "<response><id value =\"123456\"/><ret code=\"1\" msg=\"\" /><data><entity class=\"DataVO\"><prop name=\"ntime\" value=\"20120223060624\"/><list ref=\"items\"><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"3\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/Setting.apk\" /><prop name=\"name\" value=\"Setting.apk\" /><prop name=\"package\" value=\"com.nl\" /><prop name=\"activity\" value=\".test\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"17\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/Yuele.apk\" /><prop name=\"name\" value=\"Yuele.apk\" /><prop name=\"package\" value=\"com.yuele.activity\" /><prop name=\"activity\" value=\".StartActivity\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"23\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/1900057009_GX9.apk\" /><prop name=\"name\" value=\"1900057009_GX9.apk\" /><prop name=\"package\" value=\"com.tempus.frtravel.app\" /><prop name=\"activity\" value=\".Loading\" /></entity><entity class=\"SoftwareVO\"><prop name=\"id\" value=\"24\" /><prop name=\"url\" value=\"http://223.4.87.42/pkg/FundMaster_1.0.1_htwlk.apk\" /><prop name=\"name\" value=\"FundMaster_1.0.1_htwlk.apk\" /><prop name=\"package\" value=\"wind.fundmaster\" /><prop name=\"activity\" value=\".WStockAppDelegate\" /></entity></list></entity></data></response>";
       
        

    }
    
    public boolean checkAndInstalledApp(){
		try {
			Map<String,String> appInfos = (Map<String,String>)SharedPreferencesUtils.getConfigAll(SharedPreferencesUtils.NEW_APP_INFO);
			//Log.i("getInstallerPackageName",appInfos.size()+"");
			if(appInfos.size()<=0)
				return false;
			
			// 查询是否存在应该安装的应用
			List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
			PackageInfo packageInfo = null;
			boolean hasInstalled = false;

			Map<String,String> installedAppInfos = (Map<String,String>)SharedPreferencesUtils.getConfigAll(SharedPreferencesUtils.INSTALLED_APP_INFO);
           
			Iterator it = appInfos.keySet().iterator();
            while(it.hasNext()){
            	String key = (String)it.next();
            	hasInstalled = false;
            	
        		if(installedAppInfos.containsKey(key)){//判断之前是否安装过
        			hasInstalled = true;
        		}
            	
    			for (int i = 0; i < packages.size(); i++) {
    				packageInfo = packages.get(i);;
    				if(packageInfo.packageName.equals(key)){
    						hasInstalled = true;
    						SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.INSTALLED_APP_INFO, key, appInfos.get(key));
    						break;
    				}
    			}
    			if(hasInstalled)
    				continue;
    			else{
    				return downAndInstall(key,appInfos.get(key));
    			}
            }
			
		} catch (Exception e) {

			return false;
		}
		return true;
    }
    
    public boolean downAndInstall(String packetName,String appinfos){
		try {

			String appInfo[]=appinfos.split("\\|");
			
			//判断文件是否存在
			File file = new File(path, appInfo[2]);//如果存在，则退出下载
			if(file.exists()){
				file.delete();
			}
			//Log.d("appInfo",appInfo[1]+":");
			URL url = new URL(appInfo[1]);   
			HttpURLConnection conn =(HttpURLConnection) url.openConnection();   
			conn.setDoInput(true);   
			conn.connect();   
			
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				InputStream is = conn.getInputStream(); 
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					file = new File(path, appInfo[2]);
					file.createNewFile();
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					int count = 0;
					while ((ch = is.read(buf)) != -1) {
						//Log.d("update count",count+"");
						fileOutputStream.write(buf, 0, ch);
						count += ch;
					}
				}
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				if(is!=null){
					is.close();
				}


			}else{//下载失败,发送通知
				throw new Exception("down file failed");
			}
			//安装软件
			Runtime.getRuntime().exec(
					"pm install -l " + path + appInfo[2]);
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.INSTALLED_APP_INFO, packetName, appinfos);
			updateInstalledInfo(appInfo[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
    }
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
