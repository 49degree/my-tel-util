package com.guanri.android.lib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import com.guanri.android.lib.context.MainApplication;

/**
 * 读取文件
 * @author Administrator
 *
 */
public class FileReader {
	public final static String DEFAULT_FILE_ENCODE = "GBK";//配置文件编码格式
	private String encodeType = null;
	private String fileName = null;
	private File file = null;
	private InputStreamReader isr = null;
	private BufferedReader input = null;
	
	/**
	 * 使用默认FILE_ENCODE编码读取文件
	 * 默认文件放在assets目录中
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public FileReader(String fileName) throws FileNotFoundException,IOException,UnsupportedEncodingException{
		this(fileName,FileReader.DEFAULT_FILE_ENCODE);    
	}
	
	/**
	 * 使用指定编码读取文件
	 * 默认文件放在assets目录中
	 * @param fileName
	 * @param encodeType
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public FileReader(String fileName,String encodeType) 
	       throws IOException,UnsupportedEncodingException{
		this.fileName = fileName;
		this.encodeType = encodeType;
		//获取文件输入流对象
		
		try{
			InputStream fileIn = MainApplication.getInstance().getResources().getAssets().open(fileName); 		
			isr = new InputStreamReader(fileIn, encodeType);
			input = new BufferedReader(isr);   
		}catch(IOException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * 使用默认FILE_ENCODE编码读取文件对象的内容
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public FileReader(File file) throws FileNotFoundException,IOException,UnsupportedEncodingException{
		this(file,FileReader.DEFAULT_FILE_ENCODE);    
	}
	
	/**
	 * 使用指定编码读取文件读取文件对象的内容
	 * @param file
	 * @param encodeType
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public FileReader(File file,String encodeType) 
	       throws IOException{
		this.file = file;
		this.encodeType = encodeType;
		//获取文件输入流对象
		
		try{
			isr = new InputStreamReader(
					new FileInputStream(file), encodeType);
			input = new BufferedReader(isr);
		}catch(IOException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 读取文件种的一行
	 * @return
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		//获取文件输入流对象
		return input.readLine();
	}
	
	
	/**
	 * 释放资源
	 */
	public void release(){

		if(input != null){
			try{
				input.close();
			}catch(IOException e){
				input = null;
			}
		}
		if(isr != null){
			try{
				isr.close();
			}catch(IOException e){
				isr = null;
			}
			
		}
	}
	
	/**
	 * 示例代码
	 */
	private void test(){
		FileReader fileReader = null;
		//获取文件读取流对象
		try{
			//获取文件读取流对象
			fileReader = new FileReader("文件路径");
			String lineString = null;
			String[] groupHeader = null;
			List<Map<String,Object>> groupList = null;
			while((lineString = fileReader.readLine()) != null){
				System.out.println(lineString);
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{//释放资源
			if(fileReader!=null){
				fileReader.release();
				fileReader = null;
			}
		}
	}
}
