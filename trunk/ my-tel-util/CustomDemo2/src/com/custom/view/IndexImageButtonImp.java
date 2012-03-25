package com.custom.view;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.custom.R;
import com.custom.activity.IndexActivity;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;
import com.custom.utils.Constant.DirType;

public abstract class IndexImageButtonImp extends LinearLayout implements OnClickListener{
	private static final String TAG = "IndexImageButtonImp";
	private static final Logger logger = Logger.getLogger(IndexImageButtonImp.class);
	protected boolean imageCanMove = false;
	protected ResourceBean resourceBean = null;
	protected Context context;
	protected Bitmap bm=null;
	protected int bmWidth = 0;
	protected int bmHeight = 0;
	protected static BitmapDrawable frame = null;
	protected boolean hasFrame = false;

	public IndexImageButtonImp(Context context,ResourceBean resourceBean,boolean hasFrame) {
		super(context);
		this.context = context;
		this.resourceBean = resourceBean;
		this.hasFrame = hasFrame;
		try{
			imageCanMove = Boolean.parseBoolean(context.getString(R.string.modify_index));
		}catch(Exception e){
			
		}
	}  
	
	
	public IndexImageButtonImp(Context context,ResourceBean resourceBean) {
		this(context, resourceBean, false);
	}  
	
	protected void setBackground(){
		try{
			if(hasFrame){
				if(frame==null){
					frame = new BitmapDrawable(LoadResources.loadBitmap(context, Constant.pageNumPicPath+"/frame.png", DirType.assets));
				}
				this.setBackgroundDrawable(frame);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void initView() {

		try{
			setBackground();
			bm = resourceBean.getBm();//LoadResources.loadBitmap(context, resourceBean.getBtnPic(), resourceBean.getDirType());
		}catch(Exception e){
			e.printStackTrace();
		}
		ImageView jpgView = new ImageView(context);
		jpgView.setImageBitmap(bm);
		bmWidth = 100;//bm.getWidth();
		bmHeight = 100;bm.getHeight();
		LinearLayout.LayoutParams alayout = new LinearLayout.LayoutParams(bmWidth, bmHeight);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);
		
		TextView text = new TextView(context);
		text.setText(resourceBean.getName());
		LinearLayout.LayoutParams tlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		text.setLayoutParams(tlayout);
		this.addView(text);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);
		//this.setBackgroundColor(Color.RED);
		AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(
				bmWidth+20, bmHeight+60, resourceBean.getX(), resourceBean.getY());
		this.setLayoutParams(layout);
		
	}
	
	@Override
	public void onClick(View v){
		
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
		

		List<ResourceBean.ResourceRaws> raws = resourceBean.getRaws();
		logger.error("raws:"+raws.size());
		if(raws==null||raws.size()<1){
			return ;
		}
		String path = raws.get(0).getRawPath();
		ResourceBean.ResourceType type = raws.get(0).getType();
		logger.error(path+":"+type.toString());
		if(type==ResourceBean.ResourceType.apk){
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
		}else if(type==ResourceBean.ResourceType.swf){
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
		}else if(type==ResourceBean.ResourceType.fold){
			intent = new Intent(context, IndexActivity.class);   
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bd = new Bundle();
			bd.putString(Constant.foldPath, path);
			bd.putInt(Constant.foldDepth, resourceBean.getFoldDepth()+1);
			bd.putString(Constant.viewClass, Constant.secondViewClass);
			intent.putExtras(bd);
			context.startActivity(intent);
			return ;
		}
		try{
			//复制文件
			if(LoadResources.saveToTempFile(context, path, resourceBean.getDirType(), fileName))
			context.startActivity(intent);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		this.setBackground();

	}
	
	public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }
}
