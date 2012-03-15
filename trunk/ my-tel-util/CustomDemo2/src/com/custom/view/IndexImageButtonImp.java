package com.custom.view;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.activity.IndexActivity;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.Logger;
import com.custom.utils.MondifyIndexImageIndex;

public abstract class IndexImageButtonImp extends LinearLayout implements OnClickListener{
	private static final String TAG = "IndexImageButtonImp";
	private static final Logger logger = Logger.getLogger(IndexImageButtonImp.class);
	protected boolean imageCanMove = false;
	protected ResourceBean resourceBean = null;
	protected Context context;
	protected Bitmap bm=null;
	public IndexImageButtonImp(Context context,ResourceBean resourceBean) {
		super(context);
		this.context = context;
		this.resourceBean = resourceBean;
	}  
	
	protected void initView() {
		try{
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(resourceBean.getBtnPic());
			bm = BitmapFactory.decodeStream(in);
		}catch(Exception e){
			
		}
		ImageView jpgView = new ImageView(context);
		jpgView.setImageBitmap(bm);
		int with = bm.getWidth();
		int height = bm.getHeight();
		LinearLayout.LayoutParams alayout = new LinearLayout.LayoutParams(60, 60);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);
		
		TextView text = new TextView(context);
		text.setText(resourceBean.getName());
		LinearLayout.LayoutParams tlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		text.setLayoutParams(tlayout);
		this.addView(text);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);
		this.setBackgroundColor(Color.RED);
		AbsoluteLayout.LayoutParams layout = null;
		if(resourceBean.getFoldDepth()==Constant.fistFoldDepth){
			int[] indexs = MondifyIndexImageIndex.getImageIndexs(resourceBean.getBtnKey());
			if(indexs==null||indexs.length<2){
				indexs = new int[2];
				indexs[0] = 100;
				indexs[1] = 100;
			}else{
				if(indexs[0] <0){
					indexs[0] =100;
				}
				if(indexs[1]<0){
					indexs[1] = 100;
				}
			}
			layout = new AbsoluteLayout.LayoutParams(
					100, 100, indexs[0], indexs[1]);
		}else{
			layout = new AbsoluteLayout.LayoutParams(
					100, 100, 100, 100);
		}
		this.setLayoutParams(layout);
		
	}
	
	@Override
	public void onClick(View v){
		logger.error(resourceBean.getType().toString());
		String fileName = null;
		Intent intent = null;
//		打开不同类型的文件只需要修改参数type即可：
//		打开APK——application/vnd.android.package-archive
//		打开PPT——application/vnd.ms-powerpoint
//		打开Excel——application/vnd.ms-excel
//		打开Word——application/msword
//		打开CHM——application/x-chm
//		打开文本txt——text/plain
//		打开PDF——application/pdf
//		打开VCF——text/x-vcard
//		打开SWF——flash/*


		
		if(resourceBean.getType()==ResourceBean.ResourceType.apk){
			try{
				fileName = "temp.apk";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir()+File.separator+fileName)),
						"application/vnd.android.package-archive");
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(context,"安装失败，请稍后再试或者联系系统维护人员！",Toast.LENGTH_SHORT);
			}
		}else if(resourceBean.getType()==ResourceBean.ResourceType.swf){
			try{
				fileName = "temp.swf";
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//				AssetManager assetManager = context.getAssets();
				intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir()+File.separator+fileName)),"*/*");
//				context.startActivity(intent);
			}catch(Exception e){
				e.printStackTrace();
				Toast.makeText(context,"安装失败，请稍后再试或者联系系统维护人员！",Toast.LENGTH_SHORT);
			}
		}else if(resourceBean.getType()==ResourceBean.ResourceType.fold){
			intent = new Intent(context, IndexActivity.class);   
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bd = new Bundle();
			bd.putString(Constant.foldPath, resourceBean.getResourcePath());
			bd.putString(Constant.foldDepth, resourceBean.getResourcePath()+1);
			intent.putExtras(bd);
			context.startActivity(intent);
			return ;
		}
		try{
			//复制文件
			try{
				context.deleteFile(fileName);
			}catch(Exception e){
				e.printStackTrace();
			}
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			AssetManager assetManager = context.getAssets();
			InputStream in = assetManager.open(resourceBean.getResourcePath());
			byte[] buffer = new byte[10240];
			int len = in.read(buffer);
			while(len>0){
				fos.write(buffer, 0, len);
				fos.flush();
				len = in.read(buffer);
			}
			fos.close();
			in.close();
			context.startActivity(intent);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }
}
