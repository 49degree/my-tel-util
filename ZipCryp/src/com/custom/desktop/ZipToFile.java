package com.custom.desktop;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipToFile {
	private static byte[] rootKey = TypeConversion.hexStringToByte("DBED28F6415162BD");

	public static final int BUFFER = 1024;// 缓存大小
	public static final String ZIP_FILENAME = "E:\\English";// 需要解压缩的文件名
	public static final String ZIP_DIR = "E:\\English";// 需要压缩的文件夹
	public static final String UN_ZIP_DIR = "E:\\unEnglish";// 要解压的文件目录
	
	public static void main(String[] args){
		try{
			boolean encode = true;
			new ZipToFile().zipFile(ZIP_DIR, ZIP_FILENAME,encode);
			new ZipToFile().upZipFile(ZIP_FILENAME,UN_ZIP_DIR,encode);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	boolean stopZipFile = false;
	public void stopZipFile(){
		stopZipFile = true;
	}
	
	/**
	 * zip压缩功能. 压缩baseDir(文件夹目录)下所有文件，包括子目录
	 * 
	 * @throws Exception
	 */
	final static int encrypLength = 128;
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
			System.out.println("name:"+f);
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
	public static void upZipFile(String zipFile,String unZipDir,boolean decrypt) throws Exception {

		
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[BUFFER];
		byte[] encrypByte = new byte[encrypLength];
		int readLen = 0;
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			
			if (ze.isDirectory()) {
				File f = new File(unZipDir + ze.getName());
				f.mkdir();
				continue;
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(unZipDir, ze.getName())));
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
	public static File getRealFileName(String baseDir, String absFileName) {
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

}
