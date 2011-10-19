package com.guanri.android.insurance.insuplan;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guanri.android.lib.log.Logger;
import com.guanri.android.lib.utils.FileReader;

/**
 * 读取配置文件
 * @author 杨雪平
 *
 */
public class InsuConfigFileReader {
	private static Logger logger = new Logger(InsuConfigFileReader.class);//日志对象
	
	
	
	private File file = null;//配置文件名称
	private String splitString = null;
	private List<List<Map<String,Object>>> fileGroupList = null;//文件内容分组列表
	private String groupBegingStr = null;//文件每组开始标题
	
	public InsuConfigFileReader(File file,String splitString){
		this.file = file;
		this.splitString = splitString; 
		fileGroupList = new ArrayList<List<Map<String,Object>>>();
	}
	/**
	 * 获取文件内容分组列表
	 * @return
	 */
	public List<List<Map<String,Object>>> getFileGroupList(){
		if(groupBegingStr==null){
			pareConfigFile();
		}
		return fileGroupList;
	}
	
	/**
	 * 获取文件中第groupId分组的内容
	 * @param groupId 分组ID
	 * @return
	 */
	public List<Map<String,Object>> getGroup(int groupId){
		if(groupBegingStr==null){
			pareConfigFile();
		}
		if(fileGroupList.size()>=groupId-1){
			return fileGroupList.get(groupId);
		}else{
			return null;
		}
		
	}
	
	/**
	 * 解析文件
	 */
	private void pareConfigFile(){
		FileReader fileReader = null;
		try{
			//获取文件读取流对象
			fileReader = new FileReader(file);
			String lineString = null;
			String[] groupHeader = null;
			List<Map<String,Object>> groupList = null;
			String compareSplit = splitString;
			
			//用竖线 | 分隔字符串，你将得不到预期的结果 "\\|" 这样才能得到正确的结果,所以在比较的时候要剔除
			if(splitString.indexOf("\\")>-1){
				compareSplit = compareSplit.substring(1);
			}
			while((lineString = fileReader.readLine()) != null){
				if(lineString.trim().equals(""))//
					continue;
				if(lineString.indexOf(PareFileToObject.END_FLAG)>-1)//已经读到文件内容结尾
					break;
				
				//由于正常情况下是不会有第一和结尾的字符为分割符的情况,需要判断
				//判断第一个字符是否为分割符
				if(lineString.startsWith(compareSplit)){
					lineString=" "+lineString;
				}
				//判断最后一个字符是否为分割符
				if(lineString.endsWith(compareSplit)){
					lineString=lineString+" ";
				}
				String[] strs = lineString.split(splitString);
				
				if(groupBegingStr==null){//判断是否第一行数据，
					strs = lineString.trim().split(splitString);
					groupBegingStr = strs[0];//获取每组开始字符串
				}
				
				
				
				//判断是否某组的标题行
				if(groupBegingStr.equals(strs[0])){
					groupHeader = strs;
					groupList = new ArrayList<Map<String,Object>>();
					fileGroupList.add(groupList);
					continue;
				}else{
					Map<String,Object> groupValue = new HashMap<String,Object>();
					try{
						for(int i=0;i<groupHeader.length;i++){
							groupValue.put(groupHeader[i], strs[i].trim());
						}
						groupList.add(groupValue);
					}catch(Exception ex){
						throw ex;
					}

				}
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{//释放资源
			fileReader.release();
			fileReader = null;
		}
		
	}

}
