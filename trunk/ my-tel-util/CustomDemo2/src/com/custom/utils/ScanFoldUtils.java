package com.custom.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;

import com.custom.bean.ResourceBean;
import com.custom.utils.Constant.BgType;

public class ScanFoldUtils {
	private static final Logger logger = Logger.getLogger(ScanFoldUtils.class);
	public HashMap<String,String> btnInfo = null;
	public HashMap<String,ResourceBean> resourceInfo = null;

	Context context = null;
	public String foldPath = null;
	public String bgPic = null;
	public BgType bgtype = BgType.pic;
	public int foldDepth = Constant.fistFoldDepth;

	public ScanFoldUtils(Context context,String foldPath){
		this.context = context;
		this.foldPath = foldPath; 
		queryRes();
	}
	
	public ScanFoldUtils(Context context,String foldPath,int foldDepth){
		this.context = context;
		this.foldPath = foldPath;
		this.foldDepth = foldDepth; 
		queryRes();
	}
	public void queryRes(){
		btnInfo = new HashMap<String,String>();
		resourceInfo = new HashMap<String,ResourceBean>();
		try{
			AssetManager assetManager = context.getAssets();
			BufferedReader  fin = new BufferedReader(new InputStreamReader(assetManager.open(foldPath+"/"+Constant.mapFileName)));
			String line = fin.readLine();
			while(line!=null){
				logger.error(line+":"+line.substring(0,line.indexOf("="))+":"+line.substring(line.indexOf("=")+1));
				if(line.indexOf("=")>0){
					btnInfo.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1));
				}
				line = fin.readLine();
			}
			
			String[] lists = assetManager.list(foldPath);
			for(int i=0;i<lists.length;i++){
				logger.error(lists[i]);
				if(lists[i].startsWith(Constant.bgPicName)){
					//是背景图片
					if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.pic;
					}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.swf;
					}	
					bgPic = foldPath+"/"+lists[i];
					//bgtype = BgType.swf;
				}else{
					ResourceBean res = null;
					
					if(lists[i].indexOf(".")<0){
						if(resourceInfo.containsKey(lists[i])){
							res = resourceInfo.get(lists[i]);
						}else{
							res = new ResourceBean();
						}
						
						res.setResourcePath(foldPath+"/"+lists[i]);
						res.setType(ResourceBean.ResourceType.fold);
						resourceInfo.put(lists[i], res);
						continue;
					}
					String btnName = lists[i].substring(0,lists[i].indexOf("."));
					logger.error(btnName);
					if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setBtnKey(btnName);
						res.setBtnPic(foldPath+"/"+lists[i]);
						resourceInfo.put(btnName, res);
					}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setType(ResourceBean.ResourceType.swf);
						res.setResourcePath(foldPath+"/"+lists[i]);
						resourceInfo.put(btnName, res);
					}else if("apk".equals(lists[i].substring(lists[i].indexOf(".")+1))){
						if(resourceInfo.containsKey(btnName)){
							res = resourceInfo.get(btnName);
						}else{
							res = new ResourceBean();
						}	
						res.setType(ResourceBean.ResourceType.apk);
						res.setResourcePath(foldPath+"/"+lists[i]);
						resourceInfo.put(btnName, res);
					}
					if(resourceInfo.get(btnName)!=null){
						logger.error( btnName);
						resourceInfo.get(btnName).setName(btnInfo.get(btnName));
						resourceInfo.get(btnName).setFoldDepth(foldDepth);
					}
					
					
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
