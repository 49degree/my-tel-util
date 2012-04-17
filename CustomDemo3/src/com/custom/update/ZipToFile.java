package com.custom.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.custom.utils.CryptionControl;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.TypeConversion;

public class ZipToFile {
	private static final Logger logger = Logger.getLogger(ZipToFile.class);
	public static byte[] rootKey = TypeConversion.hexStringToByte("DBED28F6415162BD");
	public static final int BUFFER = 1024;// 缓存大小
	public final static int encrypLength = 128;
	private String extSdPath = null;
	private String sdPath = null;
	boolean stopZipFile = false;
	
	public ZipToFile(){
		sdPath = Constant.getSdPath();
		extSdPath = Constant.getExtSdPath();

	}
	
	public void stopZipFile(){
		stopZipFile = true;
	}
	
	
	/**
	 * zip压缩功能. 压缩baseDir(文件夹目录)下所有文件，包括子目录
	 * 
	 * @throws Exception
	 */
	
	public void zipFile(String baseDir, String fileName,boolean encrypt)
			throws Exception {
		List fileList = getSubFiles(new File(baseDir));
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(fileName+".temp"));
		ZipEntry ze = null;
		byte[] buf = new byte[BUFFER];
		byte[] encrypByte = new byte[encrypLength];
		int readLen = 0;
		for (int i = 0; i < fileList.size(); i++) {
			if(stopZipFile){//如果停止了，则退出
				zos.close();
				File zipFile = new File(fileName+".temp");
				if(zipFile.exists())
					zipFile.delete();
				break;
			}
			
			File f = (File) fileList.get(i);
			
			if(f.getAbsoluteFile().equals(fileName+".temp"))//如果为当前压缩临时文件，这条过
				continue;
			
			ze = new ZipEntry(getAbsFileName(baseDir, f));
			ze.setSize(f.length());
			ze.setTime(f.lastModified());
			zos.putNextEntry(ze);
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			readLen = is.read(buf, 0, BUFFER);
			if(encrypt){
				if(readLen>=encrypLength){//如果文件大于128个字节
					//加密文件头
					System.arraycopy(buf, 0, encrypByte, 0, encrypLength);
				}else if(readLen>0){//如果文件小于128个字节
					//加密文件头
					Arrays.fill(encrypByte, (byte)0);
					System.arraycopy(buf, 0, encrypByte, 0, readLen);
					readLen = encrypLength;
				}
				byte[] temp = CryptionControl.getInstance().encryptoECB(encrypByte, rootKey);
				System.arraycopy(temp, 0, buf, 0, encrypLength);
			}
			while (readLen != -1) {
				zos.write(buf, 0, readLen);
				readLen = is.read(buf, 0, BUFFER);
			}
			is.close();
		}
		zos.close();
		
		File zipFile = new File(fileName+".temp");
		if(zipFile.exists())
			zipFile.renameTo(new File(fileName+".zip"));
	}

	/**
	 * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
	 * 
	 * @param baseDir
	 *            java.lang.String 根目录
	 * @param realFileName
	 *            java.io.File 实际的文件名
	 * @return 相对文件名
	 */
	private static String getAbsFileName(String baseDir, File realFileName) {
		File real = realFileName;
		File base = new File(baseDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base))
				break;
			else
				ret = real.getName() + "/" + ret;
		}
		return ret;
	}

	/**
	 * 取得指定目录下的所有文件列表，包括子目录.
	 * 
	 * @param baseDir
	 *            File 指定的目录
	 * @return 包含java.io.File的List
	 */
	private static List getSubFiles(File baseDir) {
		List ret = new ArrayList();
		File[] tmp = baseDir.listFiles();
		for (int i = 0; i < tmp.length; i++) {
			
			if (tmp[i].isFile())
				ret.add(tmp[i]);
			if (tmp[i].isDirectory())
				ret.addAll(getSubFiles(tmp[i]));
		}
		return ret;
	}

	/**
	 * 解压缩功能. 将ZIP_FILENAME文件解压到ZIP_DIR目录下.
	 * 
	 * @throws Exception
	 */
	public void upZipFile(String zipFile,boolean decrypt) throws Exception {
		this.upZipFile(zipFile, decrypt, null);

	}

	/**
	 * 解压缩功能. 将ZIP_FILENAME文件解压到ZIP_DIR目录下.
	 * 
	 * @throws Exception
	 */
	//public final static long setAside = 500*1024*1024;//内存要预留50M空间
	public void upZipFile(String zipFile,boolean decrypt,String specifiedDir) throws Exception {
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[BUFFER];
		byte[] encrypByte = new byte[encrypLength];
		int readLen = 0;
		
		long[] sDCardRealease = LoadResources.readSDCard();
		long[] extSDCardRealease = LoadResources.readExtSDCard();
		
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if(stopZipFile){//如果停止了，则退出
				break;
			}
			
			if(specifiedDir!=null&&ze.getName().indexOf(specifiedDir)<0)
				continue;
			
			if (ze.isDirectory()) {
				File f = new File(sdPath + ze.getName());
				f.mkdir();
				continue;
			}
			File tempFile = null;
			RandomAccessFile os = null;
			try{
				if(sDCardRealease[1]>=ze.getSize()){
					tempFile = getRealFileName(sdPath, ze.getName(),specifiedDir);
					os = new RandomAccessFile(tempFile.getAbsoluteFile(),"rw");
					sDCardRealease[1] = sDCardRealease[1]-ze.getSize();
				}else if(extSDCardRealease[1]>=ze.getSize()){
					tempFile = getRealFileName(extSdPath, ze.getName(),specifiedDir);
					os = new RandomAccessFile(tempFile.getAbsoluteFile(),"rw");
					extSDCardRealease[1] = extSDCardRealease[1]-ze.getSize();
				}else{
					throw new IOException("空间不足");
				}
				logger.error("解压文件："+ze.getName());
				InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
				readLen = is.read(buf, 0, BUFFER);
				if(decrypt){
					//解密文件头
					System.arraycopy(buf, 0, encrypByte, 0, encrypLength);
					byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, rootKey);  
					System.arraycopy(temp, 0, buf, 0, encrypLength);
				}
				while (readLen != -1) {
					os.write(buf, 0, readLen);
					readLen = is.read(buf, 0, BUFFER);
				}
				is.close();
				os.close();
			}catch(IOException e){
				throw new IOException("解压失败");
			}

		}
		zfile.close();
	}
	
	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName,String specifiedDir) {
		if(specifiedDir!=null){
			absFileName = absFileName.substring(absFileName.indexOf(specifiedDir));	
		}
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		if (dirs.length > 0) {
			
			for (int i = 0; i < dirs.length - 1; i++) {
				ret = new File(ret, dirs[i]);
			}
			if (!ret.exists())
				ret.mkdirs();
			ret = new File(ret, dirs[dirs.length - 1]);
			return ret;
		}
		return ret;
	}
	
	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		return getRealFileName(baseDir,absFileName,null);
	}


}
