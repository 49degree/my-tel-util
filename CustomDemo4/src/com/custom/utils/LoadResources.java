package com.custom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;


import com.custom.update.Constant;
import com.custom.update.ZipToFile;
import com.custom.update.Constant.DirType;

public class LoadResources {
	private static final Logger logger = Logger.getLogger(LoadResources.class);
	static boolean secrete = true;

	public final static HashMap<String,JSONObject> updateInstalledInfo = new HashMap<String,JSONObject>();//升级信息
	public static String lastModifyTime = "";
	public static String noModifyTime = "0";
	private static JSONObject json = null;
	
	public final static HashMap<String,Integer> installedfolds = new HashMap<String,Integer>();
	public final static HashMap<String,Integer> noInstalledfolds = new HashMap<String,Integer>();
	static{
//		installedfolds.put("语文", 0);
//		installedfolds.put("英语", 0);
//		installedfolds.put("数学", 0);
	}
	static{
//		noInstalledfolds.put("语文", 0);
//		noInstalledfolds.put("英语", 0);
//		noInstalledfolds.put("数学", 0);
	}	
	
	/**
	 * 更新数据
	 * @param installed
	 */
	public static void updateInstalledInfo(JSONObject installed,boolean unZip){
		updateInstalledInfo(installed);
		if(unZip){
			addInstalledInfo(installed);//更新已经获取目录
		}
	}
	/**
	 * 更新数据
	 * @param installed
	 */
	public static void updateInstalledInfo(JSONObject installed){
		logger.error("updateInstalledInfo");
		String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
		try{
			if(json==null){
				json = new JSONObject();
				json.put(Constant.root, new JSONArray());
				logger.error("new json:"+json.toString());
			}

			if(updateInstalledInfo.containsKey(installed.getString(Constant.updateId))){
				JSONArray list = json.getJSONArray(Constant.root);
				for(int i=0;i<list.length();i++){//如果已经存在，则替换
					JSONObject temp = list.getJSONObject(i);
					logger.error("installedInfo json:"+list.length());
					if(temp.getString(Constant.updateId).equals(installed.getString(Constant.updateId))){
						list.put(i, installed);
						break;
					}
				}
			}else{
				json.getJSONArray(Constant.root).put(installed);
			}
			
			updateInstalledInfo.put(installed.getString(Constant.updateId), installed);
			initNoInstalledInfo();//更新未获取目录
			
			json.put(Constant.modifyTime, new SimpleDateFormat(Constant.timeFormate).format(new Date()));
			logger.error("update json:"+json.toString());
			writeFile(filePath,json.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public static void loadUpdateInstalledInfo(){
		logger.error("loadUpdateInstalledInfo");
		try{
			String filePath = Constant.getDataPath()+File.separator+Constant.installedInfo;
			byte[] buf = LoadResources.loadFile(filePath);
			if(buf==null){
				return ;
			}
			String info = new String(buf,"GBK");
			logger.error(info);
			json = new JSONObject(info);
			JSONArray list = null;
			try{
				list = json.getJSONArray(Constant.root);
			}catch(Exception e){
				list = new JSONArray();
				json.put(Constant.root, list);
			}
			for(int i=0;i<list.length();i++){
				try{
					JSONObject installed = list.getJSONObject(i);
					if(installed==null)
						continue;
					logger.error(installed.getString(Constant.updateId));
					updateInstalledInfo.put(installed.getString(Constant.updateId), installed);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			lastModifyTime = json.getString(Constant.modifyTime);
			noModifyTime = String.valueOf((new SimpleDateFormat(Constant.timeFormate).parse(lastModifyTime).getTime()-new Date().getTime())/(24*60*60*1000));

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询升级信息
	 */
	public static void addInstalledInfo(JSONObject installed){
		logger.error("addInstalledInfo");
		String unZipflag = null;
		try{
			unZipflag = installed.getString(Constant.fileUnziped);
		}catch(Exception e){}
		if ( unZipflag!=null){
			try{
				JSONArray contents = installed.getJSONArray(Constant.fileContent);
				for(int j=0;j<contents.length();j++){
					logger.error("Installed:"+contents.length()+":"+contents.toString());
					try{
						JSONObject content = contents.getJSONObject(j);
						String name = content.getString("name");
						int value = content.getInt("value");
						if(installedfolds.containsKey(name)&&installedfolds.get(name)>0)
							installedfolds.put(name, installedfolds.get(name)+value);
						else
							installedfolds.put(name, value);
						logger.error("installedfolds:"+name+":"+installedfolds.get(name));
					}catch(Exception e){
						e.printStackTrace();
					}		
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * 查询升级信息
	 */
	public static void initNoInstalledInfo(){
		logger.error("initNoInstalledInfo");
		try{
			//清空未安装情况
			try{
		    	Iterator it = noInstalledfolds.keySet().iterator();
		    	while(it.hasNext()){
		    		String name = (String)it.next();
		    		noInstalledfolds.put(name, 0);
		    	}
			}catch(Exception e){
				
			}
			Iterator it = updateInstalledInfo.keySet().iterator();
			while(it.hasNext()){
				try{
					JSONObject installed = updateInstalledInfo.get(it.next());
					if(installed==null)
						continue;
					String unZipflag = null;
					try{
						unZipflag = installed.getString(Constant.fileUnziped);
					}catch(Exception e){}
					if ( unZipflag==null){
						try{
							JSONArray contents = installed.getJSONArray(Constant.fileContent);
							logger.error("installedfolds:"+contents.length()+":"+contents.toString());
							for(int j=0;j<contents.length();j++){
								JSONObject content = contents.getJSONObject(j);
								String name = content.getString("name");
								int value = content.getInt("value");
								if(noInstalledfolds.containsKey(name))
									noInstalledfolds.put(name, noInstalledfolds.get(name)+value);
								else
									noInstalledfolds.put(name, value);
								logger.error("noInstalledfolds:"+noInstalledfolds.get(name));
										
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static byte[] loadFile(String filePath){
		InputStream in= null;
		try{
			in= new FileInputStream(filePath);
			byte[] buf = new byte[in.available()];
			in.read(buf,0,buf.length);
			return buf;
		}catch(Exception e){
			return null;
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}	
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static byte[] loadFile(String filePath,int dirType){
		InputStream in= null;
		logger.error("filePath:"+filePath+":dirType:"+dirType);
		try{
			if(dirType==2){
				in= new FileInputStream(Constant.getSdPath()+File.separator+filePath);
			}
			byte[] buf = new byte[in.available()];
			in.read(buf,0,buf.length);
			return buf;
		}catch(Exception e){
			return null;
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}
	
	/**
	 * 根据路径和文件名称获取文件对象
	 * @param foldPath
	 * @param dirType
	 * @return
	 */
	public static File getFileByType(String foldPath,DirType dirType){
		if(DirType.sd == dirType&&Constant.getSdPath()!=null){
			return new File( Constant.getSdPath()+File.separator+foldPath);
		}else if(DirType.file == dirType&&Constant.getDataPath()!=null){
			return new File( Constant.getDataPath()+File.separator+foldPath);
		}else if(DirType.extSd == dirType&&Constant.getExtSdPath()!=null){
			return new File( Constant.getExtSdPath()+File.separator+foldPath);
		}
		return null;
	}
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static byte[] loadFile(String filePath,DirType dirType){
		return loadFile(filePath,dirType,LoadResources.secrete);
	}
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static byte[] loadFile(String filePath,DirType dirType,boolean secrete){
		InputStream in= null;
		//logger.error("filePath:"+filePath+":dirType:"+dirType);


		try{
			if(dirType==DirType.sd&&Constant.getSdPath()!=null){
				in= new FileInputStream(Constant.getSdPath()+File.separator+filePath);
			}else if(dirType==DirType.extSd&&Constant.getExtSdPath()!=null){
				in= new FileInputStream(Constant.getExtSdPath()+File.separator+filePath);
			}
			if(in==null)
				return null;
			byte[] buf = new byte[in.available()];
			//in.read(buf,0,buf.length);
			if(in.read(buf,0,buf.length)>=ZipToFile.encrypLength&&secrete){
				//解密文件头
				byte[] encrypByte = new byte[ZipToFile.encrypLength];
				System.arraycopy(buf, 0, encrypByte, 0, ZipToFile.encrypLength);
				byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, ZipToFile.rootKey);  
				System.arraycopy(temp, 0, buf, 0, ZipToFile.encrypLength);
			}
			return buf;
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}
	
	/**
	 * 根据路径和路径类型读取文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static void writeFile(String filePath,String value){
		FileOutputStream in= null;
		try{
			in= new FileOutputStream(filePath);
			byte[] buff = value.getBytes("GBK");
			in.write(buff);
			in.flush();
		}catch(Exception e){
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
					
				}
				
			}
		}
	}
	
	public static long[] readExtSDCard() {   
        File sdcardDir = new File(Constant.getExtSdPath());  
        long[] datas = new long[3];
     
       return datas;
   }
	public static long[] readSDCard() {   
        File root = new File(Constant.getSdPath());   
        long[] datas = new long[3];

        return datas;
	}
	
}
