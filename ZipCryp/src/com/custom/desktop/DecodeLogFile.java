package com.custom.desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class DecodeLogFile {
	public String logFilePath = System.getProperty("user.dir")+File.separator+"logs"+File.separator;
	public String fileName = "2012-03-16.data";
	byte[] rootKey = TypeConversion.hexStringToByte("DF83647F322E113D");
	byte[] workKey = null;
	String packName = "FSK_POS&";
	byte[] infos = null;
	byte[] lineEnd = null;
	public DecodeLogFile(String logFilePath,String fileName){
		this.logFilePath = logFilePath; 
		this.fileName = fileName;
		workKey = encryptoWorkKey(rootKey,packName+fileName.substring(0,10));
		try{
			lineEnd = TypeConversion.stringToAscii("\n");
			//System.out.println(lineEnd.length+":"+lineEnd[0]);
		}catch(Exception e){
			
		}
		

	}
	
	
//	public static void main(String[] args){
//		DecodeLogFile dec = new DecodeLogFile(logFilePath,fileName); 
//		dec.decodeFile();
//	}
	
	public void decodeFile(){
		try{
			//判断日志文件是否存在
			File f=new File(logFilePath,fileName+"_decode"); 
			if(f.exists()){//检查fileName是否存在
				f.delete();//在当前目录下建立一个名为fileName的文件 
			}
			//定义一个类RandomAccessFile的对象，并实例化 
			java.io.RandomAccessFile rf=new java.io.RandomAccessFile(logFilePath + "/"+fileName+"_decode","rw"); 
			rf.seek(rf.length());//将指针移动到文件末尾 
			File f1=new File(logFilePath,fileName);
			BufferedReader rd = new BufferedReader(new InputStreamReader( new FileInputStream(f1)));
			String readStr = null;
			while((readStr=rd.readLine())!=null){
				
				try{
					
					infos = TypeConversion.hexStringToByte(readStr);
					infos = CryptionControl.getInstance().decryptECB(infos, workKey);
					int i=infos.length-1;
					for(;i>-1;i--){
						if(infos[i]==lineEnd[0]){
							break;
						}
					}
					//System.out.println(i+":LOG Res:"+TypeConversion.byte2hex(infos));
					
					rf.write(infos,0,i+1);//对原始数据进行BASE64编码
					//rf.write("\n".getBytes());
				}catch(Exception e){
					e.printStackTrace();
				} 

				
			}
			rf.close();//关闭文件流 
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 加密工作密钥
	 * @param workkeyInfo
	 * @return
	 */
	public byte[] encryptoWorkKey(byte[] rootKey, String workkeyInfo){
		if(workkeyInfo==null){
			return null;
		}
		byte[] workkeyInfoBytes = TypeConversion.hexStringToByte(workkeyInfo);
		if(workkeyInfoBytes.length%8>0){//补足8字节整数倍
			byte[] temp = new byte[(workkeyInfoBytes.length/8+1)*8];
			System.arraycopy(workkeyInfoBytes, 0, temp, 0, workkeyInfoBytes.length);
			workkeyInfoBytes = temp;
		}
		workkeyInfoBytes = CryptionControl.getInstance().encryptoECB(workkeyInfoBytes, rootKey);
		byte[] key = new byte[8];
		if(workkeyInfoBytes.length>8){
			System.arraycopy(workkeyInfoBytes, workkeyInfoBytes.length-8, key,0 , 8);
		}else{
			System.arraycopy(workkeyInfoBytes, 0, key,0, workkeyInfoBytes.length);
		}
		return key;
	}
	
}
