package com.custom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;


public class MondifyIndexImageIndex {
	private static final Logger logger = Logger.getLogger(MondifyIndexImageIndex.class);
	private static HashMap<String,int[]> imageIndexs = new HashMap<String,int[]>();
	public static void initImageIndexs(Context context,boolean imageCanMove){
		try{
			String filePath = Constant.path+File.separator+Constant.foldName+"_"+Constant.imageIndexFileName;
			InputStream in = null;
			if(imageCanMove){
				filePath = Environment.getExternalStorageDirectory()+File.separator+Constant.foldName+"_"+Constant.imageIndexFileName;
				in = new FileInputStream(filePath);
			}else{
				AssetManager am = context.getAssets();
				in = am.open(filePath);
			}
			BufferedReader  fin = new BufferedReader(new InputStreamReader(in));
			String line = fin.readLine();
			while(line!=null){
				logger.error(line.substring(0,line.indexOf("="))+":"+line.substring(line.indexOf("=")+1));
				if(line.indexOf("=")>0){
					String indexs =  line.substring(line.indexOf("=")+1);
					if(indexs.length()<3||indexs.indexOf(":")<0){
						imageIndexs.put(line.substring(0,line.indexOf("=")),null);
					}else{
						int[] indexArgs = new int[2];
						try{
							indexArgs[0] = Integer.parseInt(indexs.substring(0,indexs.indexOf(":")));
							indexArgs[1] = Integer.parseInt(indexs.substring(indexs.indexOf(":")+1));
							imageIndexs.put(line.substring(0,line.indexOf("=")),indexArgs);
						}catch(Exception e){
							e.printStackTrace();
							imageIndexs.put(line.substring(0,line.indexOf("=")),null);
						}
					}
				}
				line = fin.readLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void modifyImageIndexs(Context context){
		try{
			String filePath = Environment.getExternalStorageDirectory()+File.separator+Constant.foldName+"_"+Constant.imageIndexFileName;
			//清空文件
			RandomAccessFile   raf   =   new   RandomAccessFile(filePath,   "rw"); 
			raf.setLength(0); 
			raf.close(); 
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			
			
			Iterator it = imageIndexs.keySet().iterator();

			while(it.hasNext()){
				String key = (String)it.next();
				int[] indexArgs = imageIndexs.get(key);
				key = key+"="+indexArgs[0]+":"+indexArgs[1]+"\n"; 
				fos.write(key.getBytes());
			}
			fos.getChannel().force(true);
			fos.flush();
			fos.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static void modifyImageIndexs(Context context,String btName,int[] indexs){
		imageIndexs.put(btName, indexs);
		modifyImageIndexs(context);
	}
	
	public static int[] getImageIndexs(String btName){
		return imageIndexs.get(btName);
	}
	
}
