package com.custom.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.custom.utils.Constant.DirType;

public class LoadResources {
	private static final Logger logger = Logger.getLogger(LoadResources.class);
	static boolean secrete = false; 
	public static Bitmap loadBitmap(Context context,String filePath,DirType dirType) throws Exception{
		InputStream in = null;
		try{
			if(dirType==DirType.assets){
				String fileName = "bitMapPic.png";
				try{
					//复制文件
					LoadResources.saveToTempFile(context, filePath, dirType, fileName);
				}catch(Exception e){
					e.printStackTrace();
				}
//				
//				in = new FileInputStream(new File(context.getFilesDir()+File.separator+fileName));
//                
				//BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				//bitmapOptions.inSampleSize = 4;
				//bitmapOptions.inTempStorage=new byte[12 * 1024];
//				AssetManager assetManager = context.getAssets();
//				in= assetManager.open(filePath);
				Bitmap bm= BitmapFactory.decodeFile(context.getFilesDir()+File.separator+fileName);
				return bm;		
//				byte[] buffer = LoadResources.loadFile(context, filePath, dirType);
//				if(buffer!=null){
//					logger.error("bm:"+buffer.length+":"+960*540);
//					BitmapFactory.Options opts = new BitmapFactory.Options();
//					//设置inJustDecodeBounds为 true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height
//					//opts.inJustDecodeBounds = true;
//					Bitmap bm = BitmapFactory.decodeStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
//					logger.error("bm:"+bm.getWidth()+":"+bm.getHeight());
//					
//					return bm;
//				}

				//设置堆内存
//				int CWJ_HEAP_SIZE = 6* 1024* 1024 ; 
//				VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE);
			}else if(dirType==DirType.file||dirType==DirType.sd){
				return null;
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
	
	

	
	public static byte[] loadFile(Context context,String filePath,DirType dirType) throws Exception{
		InputStream in= null;
		try{
			if(dirType==DirType.assets){
				AssetManager assetManager = context.getAssets();
				in= assetManager.open(filePath);
				byte[] buf = new byte[in.available()];
				in.read(buf,0,buf.length);
				if(in.read(buf,0,buf.length)>=ZipToFile.encrypLength&&LoadResources.secrete){
					//解密文件头
					byte[] encrypByte = new byte[ZipToFile.encrypLength];
					System.arraycopy(buf, 0, encrypByte, 0, ZipToFile.encrypLength);
					byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, ZipToFile.rootKey);  
					System.arraycopy(temp, 0, buf, 0, ZipToFile.encrypLength);
				}
				return buf;

			}else if(dirType==DirType.file||dirType==DirType.sd){
				return null;
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
	
	public static boolean saveToTempFile(Context context,String filePath,DirType dirType,String tempSavePath){
		FileOutputStream fos = null;
		InputStream in = null;
		byte[] buffer = new byte[1024];
		int readLength = 0;
		boolean result = false;
		try{
			//复制文件
			try{
				context.deleteFile(tempSavePath);
			}catch(Exception e){
				e.printStackTrace();
			}
			fos = context.openFileOutput(tempSavePath, Context.MODE_WORLD_READABLE);
			//读取数据
			if(dirType==DirType.assets){
				AssetManager assetManager = context.getAssets();
				in= assetManager.open(filePath);
				readLength=in.read(buffer);
				if (readLength >= ZipToFile.encrypLength&&LoadResources.secrete) {
					// 解密文件头
					byte[] encrypByte = new byte[ZipToFile.encrypLength];
					System.arraycopy(buffer, 0, encrypByte, 0,ZipToFile.encrypLength);
					byte[] temp = CryptionControl.getInstance().decryptECB(encrypByte, ZipToFile.rootKey);
					System.arraycopy(temp, 0, buffer, 0, ZipToFile.encrypLength);
				}	
				while(readLength>0){
					fos.write(buffer,0,readLength);
					fos.flush();
					readLength=in.read(buffer);
				}
				result = true;
			}else if(dirType==DirType.file||dirType==DirType.sd){
				
			}
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
