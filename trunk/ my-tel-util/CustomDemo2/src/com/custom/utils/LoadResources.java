package com.custom.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.custom.update.ZipToFile;
import com.custom.utils.Constant.DirType;

public class LoadResources {
	private static final Logger logger = Logger.getLogger(LoadResources.class);
	static boolean secrete = true;
	
	/**
	 * 根据路径和文件名称获取文件对象
	 * @param foldPath
	 * @param dirType
	 * @return
	 */
	public static File getFileByType(String foldPath,DirType dirType){
		if(DirType.sd == dirType){
			return new File( Constant.getSdPath()+File.separator+foldPath);
		}else if(DirType.file == dirType){
			return new File( Constant.getDataPath()+File.separator+foldPath);
		}
		return null;
	}
	
	/**
	 * 根据路径和路径类型获取bitmap
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 * @throws Exception
	 */
	public static Bitmap loadBitmap(Context context,String filePath,DirType dirType) throws Exception{
		InputStream in = null;
		try{
			byte[] buffer = LoadResources.loadFile(context, filePath, dirType);
			if(buffer!=null){
				logger.error("buffer size"+buffer.length);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				Bitmap bm = BitmapFactory.decodeStream(new BufferedInputStream(new ByteArrayInputStream(buffer)),null,opts);
				FileOutputStream f = new FileOutputStream(Constant.getSdPath()+File.separator+"test.jpg");
				f.write(buffer);
				f.flush();
				f.close();
				return bm;
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){
				}
			}
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
	public static byte[] loadFile(Context context,String filePath,DirType dirType){
		InputStream in= null;
		logger.error("filePath:"+filePath+":dirType:"+dirType);
		try{
			if(dirType==DirType.assets){
				AssetManager assetManager = context.getAssets();
				in= assetManager.open(filePath);
			}else if(dirType==DirType.file){
				in= new FileInputStream(Constant.getDataPath()+File.separator+filePath);
			}else if(dirType==DirType.sd){
				in= new FileInputStream(Constant.getSdPath()+File.separator+filePath);
			}
			byte[] buf = new byte[in.available()];
			//in.read(buf,0,buf.length);
			if(in.read(buf,0,buf.length)>=ZipToFile.encrypLength&&LoadResources.secrete){
				//解密文件头
				byte[] encrypByte = new byte[ZipToFile.encrypLength];
				System.arraycopy(buf, 0, encrypByte, 0, ZipToFile.encrypLength);
				byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, ZipToFile.rootKey);  
				System.arraycopy(temp, 0, buf, 0, ZipToFile.encrypLength);
			}
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
	 * 保存临时文件
	 * @param context
	 * @param filePath
	 * @param dirType
	 * @return
	 */
	public static boolean saveToTempFile(Context context,String filePath,DirType dirType,String tempSavePath){
		FileOutputStream fos = null;
		InputStream in = null;
		byte[] buffer = new byte[1024];
		int readLength = 0;
		boolean result = false;
		try{
			//复制文件
			try{
				File f = new File(context.getFilesDir().getAbsolutePath()+"/"+tempSavePath);
				if(f.exists()){
					logger.error("ffffffffff:"+f.length());
				}
				context.deleteFile(tempSavePath);
			}catch(Exception e){
				e.printStackTrace();
			}
			fos = context.openFileOutput(tempSavePath, Context.MODE_WORLD_READABLE);
			logger.error("tempfile:"+tempSavePath);
			//读取数据
			if(dirType==DirType.assets){
				AssetManager assetManager = context.getAssets();
				in= assetManager.open(filePath);
			}else if(dirType==DirType.file){
				in= new FileInputStream(Constant.getDataPath()+File.separator+filePath);
			}else if(dirType==DirType.sd){
				in= new FileInputStream(Constant.getSdPath()+File.separator+filePath);
			}
			readLength=in.read(buffer);
			if (readLength >= ZipToFile.encrypLength&&LoadResources.secrete) {
				// 解密文件头
				byte[] encrypByte = new byte[ZipToFile.encrypLength];
				System.arraycopy(buffer, 0, encrypByte, 0,ZipToFile.encrypLength);
				byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, ZipToFile.rootKey);
				System.arraycopy(temp, 0, buffer, 0, ZipToFile.encrypLength);
			}	
			while(readLength>0){
				//logger.error("readLength:"+readLength);
				fos.write(buffer,0,readLength);
				fos.flush();
				readLength=in.read(buffer);
			}
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try{
					fos.flush();
					fos.close();
				}catch(Exception e){	
				}
			}
			if(in!=null){
				try{
					in.close();
				}catch(Exception e){	
				}
			}
		}
		return result;
	}
	
	public static Bitmap getBitmap(Context context,String filePath){
		Bitmap bm = null;
		if(bm==null){
			try{
				bm = LoadResources.loadBitmap(context, filePath, DirType.sd);	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(bm==null){
			try{
				bm = LoadResources.loadBitmap(context, filePath, DirType.file);	
			}catch(Exception e){
				e.printStackTrace();
			}
		}				
		if(bm==null){
			try{
				bm = LoadResources.loadBitmap(context, filePath, DirType.assets);
			}catch(Exception e){
				e.printStackTrace();
			}
		}	
		return bm;
	}
	
	
	/**
	 * 一下代码没有用到
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	
	public static int computeSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels); 
		int roundedSize;if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
				.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {// return the larger one when there is no
										// overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	public static class InputStreamCryp extends InputStream{
		InputStream in = null;
		byte[] header = new byte[ZipToFile.encrypLength];
		int headerLength = 0;
		int readedLength = 0;
		public InputStreamCryp(InputStream in){
			this.in = in;
			try{
				if((headerLength=in.read(header))==ZipToFile.encrypLength){
					logger.error("headerLength:"+headerLength+"readedLength:"+readedLength);
					//解密文件头
//					byte[] temp = CryptionControl.getInstance().decryptECB(header, ZipToFile.rootKey);  
//					System.arraycopy(temp, 0, header, 0, ZipToFile.encrypLength);				
				}
			}catch(Exception e){
				e.printStackTrace();
				headerLength = 0;
			}
		}
		@Override
		public int read() throws IOException{
			if(headerLength>readedLength){
				logger.error("header[readedLength++]&0xFF:"+readedLength);
				return header[readedLength++]&0xFF;
			}
			readedLength++;
			return in.read();
		}

        public final boolean markSupported() {
        	logger.error("markSupported:");
            return in.markSupported();
        }
        public final int available() throws IOException {
        	logger.error("available:");
            return in.available();
        }
        public final void close() throws IOException {
        	logger.error("close:");
            in.close();
        }
        public final void mark(int readlimit) {
        	logger.error("mark:");
           in.mark(readlimit);
        }
        public final void reset() throws IOException {
        	logger.error("reset:");
        	in.reset();
            
        }
        public final int read(byte[] b) throws IOException {
        	logger.error("read(byte[] b) :");
        	return read(b,0,b.length);
        }
        public final int read(byte[] b, int off, int len) throws IOException {
        	logger.error("len:"+len);
        	if(b==null||off>b.length||off+len>b.length){
        		throw new IOException("buff is error");
        	}
        	int readL = 0;
			if(headerLength>readedLength){
				readL = len>(headerLength-readedLength)?(headerLength-readedLength):len;
				System.arraycopy(header, readedLength, b, off, readL);
			}
        	if(readL<len){
        		readL = readL+in.read(b,off+readL,len-readL);
        	}
        	readedLength +=(readL<0?0:readL);
        	logger.error("read(byte[] b, int off, int len) readedLength"+readedLength);
            return readL;
        }
        public final long skip(long n) throws IOException {
        	logger.error("skip:");
            return in.skip(n);
        }

        protected void finalize() throws Throwable
        {
        	logger.error("finalize:");
            close();
        }
	}
}
