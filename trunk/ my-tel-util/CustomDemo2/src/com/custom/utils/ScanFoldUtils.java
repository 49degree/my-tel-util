package com.custom.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

import com.custom.bean.ResourceBean;
import com.custom.utils.Constant.BgType;
import com.custom.utils.Constant.DirType;

public class ScanFoldUtils {
	private static final Logger logger = Logger.getLogger(ScanFoldUtils.class);
	public HashMap<String,String> btnInfo = null;
	public HashMap<String,ResourceBean> resourceInfo = null;

	Context context = null;
	public String foldPath = null;
	public String bgPic = null;
	public BgType bgtype = BgType.pic;
	public DirType bgDirtype = DirType.assets;
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
			//读取配置文件
			byte[] buf = LoadResources.loadFile(context, foldPath+"/"+Constant.mapFileName, DirType.assets);
			BufferedReader fin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
			String line = fin.readLine();
			while(line!=null){
				logger.error(line);
				line = line.substring(line.indexOf('=')+1);
				//logger.error(line+":"+line.substring(0,line.indexOf("="))+":"+line.substring(line.indexOf("=")+1));
				if(line.indexOf("=")>0){
					btnInfo.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1).trim());
				}
				line = fin.readLine();
			}
			
			AssetManager assetManager = context.getAssets();
			String[] lists = assetManager.list(foldPath);
			for(int i=0;i<lists.length;i++){
				ResourceBean res = null;
				//logger.error(lists[i]);
				String btnName = null;
				if(lists[i].indexOf(".")<0){//为目录，判断是二级目录还是资源目录
					List<ResourceBean.ResourceRaws> raws = null;
					if(lists[i].indexOf(Constant.resourceFoldEnd)>0){//是资源目录
						btnName = lists[i].substring(0,lists[i].indexOf(Constant.resourceFoldEnd));
						if(!btnInfo.containsKey(btnName))//判断是否为当前目录需要的资源
							continue;
						raws = this.queryRaws(foldPath+"/"+lists[i]);
					}else{//是二级目录
						btnName = lists[i];
						if(!btnInfo.containsKey(lists[i]))//判断是否为当前目录需要的资源
							continue;
						raws = new ArrayList<ResourceBean.ResourceRaws>();
						raws.add(new ResourceBean.ResourceRaws(foldPath+"/"+lists[i], ResourceBean.ResourceType.fold));
					}
					if(resourceInfo.containsKey(btnName)){
						res = resourceInfo.get(btnName);
					}else{
						res = new ResourceBean();
					}
					res.setRaws(raws);
					resourceInfo.put(btnName, res);
					continue;
				}
				
				btnName = lists[i].substring(0,lists[i].indexOf("."));
				if(Constant.bgPicName.equals(btnName.toUpperCase())){
					//是背景图片
					if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.pic;
					}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
						bgtype = BgType.swf;
					}	
					bgPic = foldPath+"/"+lists[i];//背景路径
					bgDirtype = DirType.assets;
					//bgtype = BgType.swf;
				}else if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){//按钮图片
					//logger.error("btn begin:"+btnName+":");
					//logger.error("btn begin:"+Arrays.toString(btnInfo.entrySet().toArray()));
					if(!btnInfo.containsKey(btnName))//判断是否为当前目录需要的资源
						continue;
					if(resourceInfo.containsKey(btnName)){
						res = resourceInfo.get(btnName);
					}else{
						res = new ResourceBean();
					}
					//logger.error("ResourceBean:"+btnName+":");
					res.setBtnKey(btnName);//按钮标示
					res.setBtnPic(foldPath+"/"+lists[i]);//按钮背景图片路径
					res.setName(btnInfo.get(btnName));//按钮对应下面显示的字符
					res.setFoldDepth(foldDepth);//按钮的深度
					res.setFoldPath(this.foldPath);//按钮所在目录
					res.setDirType(DirType.assets);
					res.setBm(LoadResources.loadBitmap(context, foldPath+"/"+lists[i], DirType.assets));
					if(res.getRaws()==null){
						List<ResourceBean.ResourceRaws> raws = this.queryRawsByValue(btnName);
						res.setRaws(raws);
					}
					resourceInfo.put(btnName, res);
					//logger.error("btn end:"+btnName+":"+res.getBtnPic());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private List<ResourceBean.ResourceRaws> queryRaws(String mFoldPath){
		List<ResourceBean.ResourceRaws> raws = new ArrayList<ResourceBean.ResourceRaws>();

		try{
			AssetManager assetManager = context.getAssets();
			String[] lists = assetManager.list(mFoldPath);
			for(int i=0;i<lists.length;i++){
				if(lists[i].indexOf(".")<0){//为目录
					continue;
				}
				String btnName = lists[i].substring(0,lists[i].indexOf("."));
				
				logger.error(btnName);
				ResourceBean.ResourceType type = null;
				if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.pic;
				}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.swf;
				}else if("apk".equals(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.apk;
				}
				if(type!=null){
					if(Constant.raw_first_name.indexOf(btnName.toUpperCase())>-1){
						raws.add(0, new ResourceBean.ResourceRaws(mFoldPath+"/"+lists[i], type));
					}else{
						raws.add(new ResourceBean.ResourceRaws(mFoldPath+"/"+lists[i], type));
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return raws;
	}
	
	private List<ResourceBean.ResourceRaws> queryRawsByValue(String btnKey){
		List<ResourceBean.ResourceRaws> raws = null;

		try{
			AssetManager assetManager = context.getAssets();
			String[] lists = assetManager.list(this.foldPath+"/"+Constant.resourceFold);
			for(int i=0;i<lists.length;i++){
				if(lists[i].indexOf(".")<0){//为目录
					continue;
				}
				String btnName = lists[i].substring(0,lists[i].indexOf("."));
				if(!btnKey.equals(btnName)){
					continue;
				}
				//logger.error(btnName);
				ResourceBean.ResourceType type = null;
				if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.pic;
				}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.swf;
				}else if("apk".equals(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.apk;
				}
				if(type!=null){
					raws = new ArrayList<ResourceBean.ResourceRaws>();
					raws.add(new ResourceBean.ResourceRaws(this.foldPath+"/"+Constant.resourceFold+"/"+lists[i], type));
					//logger.error(this.foldPath+"/"+Constant.resourceFold+"/"+lists[i]+":"+btnKey);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return raws;
	}
}
