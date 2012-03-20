package com.custom.utils;

import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.custom.utils.Constant.DirType;

public class LoadResources {
	public static Bitmap loadBitmap(Context context,String filePath,DirType dirType) throws Exception{
		try{
			if(dirType==DirType.assets){
				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open(filePath);
				Bitmap bm= BitmapFactory.decodeStream(in);
				in.close();
				return bm;
			}else if(dirType==DirType.file||dirType==DirType.sd){
				return null;
			}
		}catch(Exception e){
			throw e;
		}
		return null;
	}
}
