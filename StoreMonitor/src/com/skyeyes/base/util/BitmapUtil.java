package com.skyeyes.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.skyeyes.base.BaseApplication;

public class BitmapUtil {
	public static void saveBitmap(Bitmap bitmap,String filename)
	{
		File file;  
	    FileOutputStream out = null;
	    String path=BaseApplication.getAppContext().getApplicationContext().getFilesDir().getAbsolutePath();
		new File(path).mkdirs();
	    file =new File(path,filename);//输出路径  
        try {  
            out=new FileOutputStream(file);//设置输出流   
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
            out.flush();  
            out.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }          
	}
	
	public static Drawable readBitmap(String filename)
	{
//		File file =AgentApplication.getAppContext().getFileStreamPath(filename);
		File file =new File(BaseApplication.getAppContext().getApplicationContext().getFilesDir().getAbsolutePath(),filename);
		if(file.isFile()){
			 try {
				    System.gc();
		            // Decode image size
		            BitmapFactory.Options o = new BitmapFactory.Options();
		            o.inJustDecodeBounds = true;
		            BitmapFactory.decodeStream(new FileInputStream(file), null, o);
		            // The new size we want to scale to
		            final int REQUIRED_SIZE = 400;
		            // Find the correct scale value. It should be the power of 2.
		            int scale = 1;
		            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
		                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
		                scale *= 2;

		            // Decode with inSampleSize
		            BitmapFactory.Options o2 = new BitmapFactory.Options();
		            o2.inSampleSize = scale;
		            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file),
		                    null, o2);
		            BitmapDrawable bd=new BitmapDrawable(bitmap);
		            return bd;
		        } catch (FileNotFoundException e) {

		        }
			return Drawable.createFromPath(BaseApplication.getAppContext().getApplicationContext().getFilesDir().getAbsolutePath()+"/"+filename);
		}
		return null;
	}
}
