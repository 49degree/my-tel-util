package com.custom.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
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
		this(context,foldPath,Constant.fistFoldDepth);
	}
	
	public ScanFoldUtils(Context context,String foldPath,int foldDepth){
		this.context = context;
		this.foldPath = foldPath;
		this.foldDepth = foldDepth; 
		queryBackGround();
	}
	/**
	 * 获取文件名称
	 * @param files
	 * @return
	 */
	private String[] getFileNames(File[] files){
		String[] lists = null;
		if(files!=null&&files.length>0){
			
			lists = new String[files.length];
			for(int i=0;i<lists.length;i++){
				lists[i] = files[i].getName();
			}
			
		}
		return lists;
	}

	public void queryBackGround(){
		try{

	    	FilenameFilter fl = new FilenameFilter() {//过滤文件名称
				@Override
				public boolean accept(File arg0, String arg1) {
					//logger.error("accept(File arg0, String arg1):"+arg1);
					if(arg1.indexOf(".")<0)
						return false;
					return Constant.bgPicName.equals(arg1.substring(0,arg1.indexOf(".")).toUpperCase());
				}
			};
			//读取背景文件,首先在SD卡上找,在从data目录找，最后在assets目录找
			String[] lists = null;
			if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){//SD卡上找
				
				File sdfile = LoadResources.getFileByType(foldPath,DirType.sd);
				logger.error("foldPath:"+foldPath+":"+sdfile.getPath());
				if(sdfile!=null&&sdfile.exists()){
					lists = getFileNames(sdfile.listFiles(fl));
					if(lists!=null)
						expendBackground(lists,DirType.sd);
				}
			}
			
			if(bgPic==null){//从DATA目录读取
				File sdfile = LoadResources.getFileByType(foldPath,DirType.extSd);
				if(sdfile!=null&&sdfile.exists()){
					lists =  getFileNames(sdfile.listFiles(fl));
					if(lists!=null)
						expendBackground(lists,DirType.extSd);
				}
			}
			
			if(bgPic==null){//从DATA目录读取
				File sdfile = LoadResources.getFileByType(foldPath,DirType.file);
				if(sdfile!=null&&sdfile.exists()){
					lists =  getFileNames(sdfile.listFiles(fl));
					if(lists!=null)
						expendBackground(lists,DirType.file);
				}
			}
			if(bgPic==null){//从ASSETS目录读取
				AssetManager assetManager = context.getAssets();
				lists = assetManager.list(foldPath);
				if(lists!=null)
					expendBackground(lists,DirType.assets);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 查询背景图片
	 * @param path
	 * @param lists
	 * @param dirType
	 */
	private void expendBackground(String[] lists,DirType dirType){
		boolean finded = false;
		for(int i=0;i<lists.length;i++){
			if(lists[i].indexOf(".")<0)//为目录
				continue;
			if(!Constant.bgPicName.equals(lists[i].substring(0,lists[i].indexOf(".")).toUpperCase()))
				continue;
			//是背景图片
			if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
				bgtype = BgType.pic;
				finded = true;
			}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
				bgtype = BgType.swf;
				finded = true;
			}
			if(finded){
				bgPic = foldPath+File.separator+lists[i];//背景路径
				bgDirtype = dirType;
				break;
			}
		}
	}
	
	
	public void queryRes(){
		btnInfo = new HashMap<String,String>();
		resourceInfo = new HashMap<String,ResourceBean>();
		try{
			//读取配置文件,首先在SD卡上找,在从data目录找，最后在assets目录找
			byte[] buf = null;

			if(Constant.getExtSdPath()!=null&&!"".equals(Constant.getExtSdPath())){//SD卡上找
				buf = LoadResources.loadFile(context, foldPath+File.separator+Constant.mapFileName, DirType.extSd);
			}
			
			if(buf==null && Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){//SD卡上找
				buf = LoadResources.loadFile(context, foldPath+File.separator+Constant.mapFileName, DirType.sd);
			}
			
			if(buf==null){//从DATA目录读取
				buf = LoadResources.loadFile(context, foldPath+File.separator+Constant.mapFileName, DirType.file);
			}
			if(buf==null){//从ASSETS目录读取
				buf = LoadResources.loadFile(context, foldPath+"/"+Constant.mapFileName, DirType.assets);
			}
			if(buf==null){
				return ;
			}
			BufferedReader fin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
			String line = fin.readLine();
			while(line!=null){
				line = line.substring(line.indexOf('=')+1);
				if(line.indexOf("=")>0){
					btnInfo.put(line.substring(0,line.indexOf("=")), line.substring(line.indexOf("=")+1).trim());
					//logger.error(line.substring(0,line.indexOf("="))+":" +""+line.substring(line.indexOf("=")+1).trim());
				}
				line = fin.readLine();
			}
			
			//读取资源相关目录按钮,首先在SD卡上找,在从data目录找，最后在assets目录找
			String[] lists = null;
			if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){//SD卡上找
				File sdfile = LoadResources.getFileByType(foldPath,DirType.sd);
				if(sdfile!=null&&sdfile.exists()){
					lists = getFileNames(sdfile.listFiles());
					if(lists!=null)
						expendRes(lists,DirType.sd);
				}
			}
			if(Constant.getExtSdPath()!=null&&!"".equals(Constant.getExtSdPath())){//SD卡上找
				File sdfile = LoadResources.getFileByType(foldPath,DirType.extSd);
				if(sdfile!=null&&sdfile.exists()){
					lists = getFileNames(sdfile.listFiles());
					if(lists!=null)
						expendRes(lists,DirType.extSd);
				}
			}
			//从DATA目录读取
			File sdfile = LoadResources.getFileByType(foldPath,DirType.file);
			if(sdfile!=null&&sdfile.exists()){
				lists = getFileNames(sdfile.listFiles());
				if(lists!=null)
					expendRes(lists,DirType.file);
			}
			//从ASSETS目录读取
			AssetManager assetManager = context.getAssets();
			lists = assetManager.list(foldPath);
			if(lists!=null)
				expendRes(lists,DirType.assets);


			this.queryRawsByValue();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询背景图片
	 * @param path
	 * @param lists
	 * @param dirType
	 */
	private void expendRes(String[] lists,DirType dirType){
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
					raws = this.queryRaws(foldPath+File.separator+lists[i],dirType);
					
				}else{//是二级目录
					btnName = lists[i];
					if(!btnInfo.containsKey(lists[i]))//判断是否为当前目录需要的资源
						continue;
					raws = new ArrayList<ResourceBean.ResourceRaws>();
					raws.add(new ResourceBean.ResourceRaws(foldPath+File.separator+lists[i], ResourceBean.ResourceType.fold,dirType));
				}
				if(resourceInfo.containsKey(btnName)){
					res = resourceInfo.get(btnName);
				}else{
					res = new ResourceBean();
				}
				//判断是否有其他目录的资源
				if(res.getRaws()!=null&&res.getRaws().get(0).getType()!=ResourceBean.ResourceType.fold){
					res.getRaws().addAll(raws);
				}else if(res.getRaws()==null){
					res.setRaws(raws);
				}
				resourceInfo.put(btnName, res);
				continue;
			}
			
			btnName = lists[i].substring(0,lists[i].indexOf("."));
			if(Constant.bgPicName.equals(btnName.toUpperCase())){
				//是背景图片在queryBackGround()方法中已经处理
			}else if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){//按钮图片
				if(!btnInfo.containsKey(btnName))//判断是否为当前目录需要的资源
					continue;
				if(resourceInfo.containsKey(btnName)){
					res = resourceInfo.get(btnName);
					if(res.getBtnPic()!=null)//如果已经有按钮图标信息
						continue;
				}else{
					res = new ResourceBean();
				}
				res.setBtnKey(btnName);//按钮标示
				res.setBtnPic(foldPath+File.separator+lists[i]);//按钮背景图片路径
				res.setName(btnInfo.get(btnName));//按钮对应下面显示的字符
				res.setFoldDepth(foldDepth);//按钮的深度
				res.setFoldPath(this.foldPath);//按钮所在目录
				res.setDirType(dirType);
				resourceInfo.put(btnName, res);
			}
		}
	}
	
	private List<ResourceBean.ResourceRaws> queryRaws(String mFoldPath,DirType dirType){
		List<ResourceBean.ResourceRaws> raws = new ArrayList<ResourceBean.ResourceRaws>();

		try{
			String[] lists = null;
			if(DirType.assets==dirType){
				
				AssetManager assetManager = context.getAssets();
				lists = assetManager.list(mFoldPath);
				//logger.error(mFoldPath+":"+lists.length);
			}else if(DirType.file==dirType||DirType.sd==dirType||DirType.extSd==dirType){
				File sdfile = LoadResources.getFileByType(mFoldPath,dirType);
				if(sdfile!=null&&sdfile.exists())
					lists = getFileNames(sdfile.listFiles());//遍历文件
			}
			
			for(int i=0;i<lists.length;i++){
				if(lists[i].indexOf(".")<0){//为目录
					continue;
				}
				//logger.error(foldPath+":"+i);
				String btnName = lists[i].substring(0,lists[i].indexOf("."));
				
				//logger.error(btnName);
				ResourceBean.ResourceType type = null;
				if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.pic;
				}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.swf;
				}else if("APK".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.apk;
				}else if("TXT".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.txt;
				}else if("PDF".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.pdf;
				}else if("FLV".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.flv;
				}else if("PPT".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.ppt;
				}else if("XLS".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.xls;
				}else if("DOC".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.doc;
				}
				
				

				//logger.error(foldPath+":"+lists[i]);
				if(type!=null){
					if(Constant.raw_first_name.indexOf(btnName.toUpperCase())>-1){
						raws.add(0, new ResourceBean.ResourceRaws(mFoldPath+"/"+lists[i], type,dirType));
					}else{
						raws.add(new ResourceBean.ResourceRaws(mFoldPath+"/"+lists[i], type,dirType));
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return raws;
	}
	
	private void queryRawsByValue(){
		try{
			
			//读取资源相关目录按钮,首先在SD卡上找,在从data目录找，最后在assets目录找
			String tempPath = foldPath+File.separator+Constant.resourceFold;
			String[] lists = null;
			if(Constant.getSdPath()!=null&&!"".equals(Constant.getSdPath())){//SD卡上找
				File sdfile = LoadResources.getFileByType(tempPath,DirType.sd);
				if(sdfile!=null&&sdfile.exists()){
					lists = getFileNames(sdfile.listFiles());
					if(lists!=null)
						expandRawsByValue(tempPath,lists,DirType.sd);
				}
			}
			if(Constant.getExtSdPath()!=null&&!"".equals(Constant.getExtSdPath())){//扩展SD卡上找
				File sdfile = LoadResources.getFileByType(tempPath,DirType.extSd);
				if(sdfile!=null&&sdfile.exists()){
					lists = getFileNames(sdfile.listFiles());
					if(lists!=null)
						expandRawsByValue(tempPath,lists,DirType.extSd);
				}
			}
			//从DATA目录读取
			
			File sdfile = LoadResources.getFileByType(tempPath,DirType.file);
			if(sdfile!=null&&sdfile.exists()){
				lists = getFileNames(sdfile.listFiles());
				if(lists!=null)
					expandRawsByValue(tempPath,lists,DirType.file);
			}
			//从ASSETS目录读取
			AssetManager assetManager = context.getAssets();
			lists = assetManager.list(tempPath);
			if(lists!=null)
				expandRawsByValue(tempPath,lists,DirType.assets);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void expandRawsByValue(String path,String[] lists,DirType dirType){
		List<ResourceBean.ResourceRaws> raws = null;
		try{
			if(resourceInfo==null)
				return;
			for(int i=0;i<lists.length;i++){
				if(lists[i].indexOf(".")<0){//为目录
					continue;
				}
				String btnName = lists[i].substring(0,lists[i].indexOf("."));
				ResourceBean res = resourceInfo.get(btnName);
				if(res==null||res.getRaws()!=null){
					continue;
				}
				ResourceBean.ResourceType type = null;
				if(Constant.picType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.pic;
				}else if(Constant.swfType.containsKey(lists[i].substring(lists[i].indexOf(".")+1))){
					type = ResourceBean.ResourceType.swf;
				}else if("APK".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.apk;
				}else if("TXT".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.txt;
				}else if("PDF".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.pdf;
				}else if("FLV".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.flv;
				}else if("PPT".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.ppt;
				}else if("XLS".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.xls;
				}else if("DOC".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.doc;
				}else if("MP3".equals(lists[i].substring(lists[i].indexOf(".")+1).toUpperCase())){
					type = ResourceBean.ResourceType.mp3;
				}
				
				if(type!=null){
					raws = new ArrayList<ResourceBean.ResourceRaws>();
					raws.add(new ResourceBean.ResourceRaws(path+File.separator+lists[i], type,dirType));
				}
				res.setRaws(raws);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
