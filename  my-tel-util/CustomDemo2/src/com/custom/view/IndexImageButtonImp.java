package com.custom.view;


import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.custom.activity.FlashView;
import com.custom.activity.IndexActivity;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;

public abstract class IndexImageButtonImp extends LinearLayout implements OnClickListener{
	private static final String TAG = "IndexImageButtonImp";
	private static final Logger logger = Logger.getLogger(IndexImageButtonImp.class);
	
	protected ResourceBean resourceBean = null;
	protected Context context;
	protected Bitmap bm=null;
	protected int bmWidth = 0;
	protected int bmHeight = 0;
	protected static BitmapDrawable frame1 = null;
	protected static BitmapDrawable frame2 = null;
	protected boolean hasFrame = false;
	protected boolean imageCanMove = false;
	protected float zoom = 1f;

	public IndexImageButtonImp(Context context,ResourceBean resourceBean,boolean hasFrame) {
		super(context);
		this.context = context;
		this.resourceBean = resourceBean;
		this.hasFrame = hasFrame;

	}  
	
	
	public IndexImageButtonImp(Context context,ResourceBean resourceBean) {
		this(context, resourceBean, false);
	}  
	
	protected void setBackground(boolean onClick){
		try{
			if(hasFrame){
				if(frame1==null){
					frame1 = new BitmapDrawable(LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+Constant.framePicName1));	
				}
				if(frame2==null){
					frame2 = new BitmapDrawable(LoadResources.getBitmap(context, Constant.pageNumPicPath+File.separator+Constant.framePicName2));	
				}
				if(onClick){
					this.setBackgroundDrawable(frame2);
					this.setPadding(0, (int)(25*zoom), 0, 0);
					if(text!=null)
						text.setPadding(0, (int)(35*zoom), 0, 0);
				}else{
					this.setBackgroundDrawable(frame1);
					this.setPadding(0, (int)(20*zoom), 0, 0);
					if(text!=null)
						text.setPadding(0, (int)(25*zoom), 0, 0);
				}
			}else{
				if(onClick){
					this.setBackgroundColor(Color.argb(55, 255,   255, 0));
				}else{
					this.setBackgroundColor(0);
				}
			}
			if(onClick){
				text.setTextColor(Color.WHITE);
			}else{
				text.setTextColor(Color.BLACK);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	TextView text = null;
	protected void initView() {
		
		text = new TextView(context);
		text.setText(resourceBean.getName());
		LinearLayout.LayoutParams tlayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		text.setLayoutParams(tlayout);
		text.setTextSize((int)(18*zoom));
		text.setGravity(Gravity.CENTER);
		if(hasFrame){
			text.setTextSize((int)(24*zoom));
			text.setPadding(0, (int)(30*zoom), 0, 0);
		}
		try{
			setBackground(false);
			bm = resourceBean.getBm();
		}catch(Exception e){
			e.printStackTrace();
		}
		ImageView jpgView = new ImageView(context);
		jpgView.setImageBitmap(bm);
		bmWidth = (int)(bm.getWidth()*zoom);
		bmHeight = (int)(bm.getHeight()*zoom);
		LinearLayout.LayoutParams alayout = new LinearLayout.LayoutParams(bmWidth, bmHeight);
		jpgView.setLayoutParams(alayout);
		this.addView(jpgView);
		this.addView(text);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
		
		
		//this.setBackgroundColor(Color.RED);
		AbsoluteLayout.LayoutParams layout = null;
		if(frame1==null){
			layout = new AbsoluteLayout.LayoutParams(
					bmWidth+(int)(20*zoom), bmHeight+(int)(40*zoom), resourceBean.getX(), resourceBean.getY());
		}else{
			layout = new AbsoluteLayout.LayoutParams(
					(int)(frame1.getBitmap().getWidth()*zoom), (int)(frame1.getBitmap().getHeight()*zoom), resourceBean.getX(), resourceBean.getY());
		}

		this.setLayoutParams(layout);
		
	}
	
	
	ProgressDialog progress = null;
	@Override
	public void onClick(View v){
		progress = ProgressDialog.show(context, "请稍候", "正在打开文件....");
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
		setBackground(false);

		final List<ResourceBean.ResourceRaws> raws = resourceBean.getRaws();
		if(raws==null||raws.size()<1){
			return ;
		}
		String path = raws.get(0).getRawPath();
		ResourceBean.ResourceType type = raws.get(0).getType();
		String intentType = "*";
		logger.error(type.toString());
		if(type==ResourceBean.ResourceType.apk){
			fileName = "temp1.apk";
			intentType = "application/vnd.android.package-archive";
		}else if(type==ResourceBean.ResourceType.swf){
			fileName = "temp1.swf";
			intentType = "*/*";
			
			try{
				new Thread(){
					public void run(){
						//播放第一个文件
						ResourceBean.ResourceRaws  raw = null;
						raw = raws.get(0);
						String tempFile = null;
						if(raw.getType()==ResourceBean.ResourceType.swf){
							tempFile = raw.getRawPath().substring(raw.getRawPath().lastIndexOf(File.separator)+1);
							LoadResources.saveToTempFile(context, raw.getRawPath(), raw.getDirType(),tempFile);
							
//							Intent intent = new Intent(Intent.ACTION_VIEW);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//							intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir().getAbsolutePath()+File.separator+tempFile)),"*/*");
//							context.startActivity(intent);
							
							Intent intent = new Intent(context,FlashView.class);
							Bundle bd = new Bundle();
							bd.putString(Constant.foldPath, tempFile);
							intent.putExtras(bd);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
							context.startActivity(intent);	
						}
						
						
						//删除临时文件
						String[] fileName = context.fileList();
						for(int i=0;i<fileName.length;i++){
							if(tempFile.equals(fileName[i])||
									Constant.backGroundSwfName.equals(fileName[i])||
									fileName[i].indexOf(".swf")<1||
									fileName[i].indexOf(".SWF")<1)
								continue;
							context.deleteFile(fileName[i]);
						}
						//复制其他flash文件
						
						for(int i=1;i<raws.size();i++){
							raw = raws.get(i);
							if(raw.getType()==ResourceBean.ResourceType.swf){
								tempFile = raw.getRawPath().substring(raw.getRawPath().lastIndexOf(File.separator)+1);
								LoadResources.saveToTempFile(context, raw.getRawPath(), raw.getDirType(),tempFile);
								if(i==0){ 
//									Intent intent = new Intent(Intent.ACTION_VIEW);
//									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//									intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir().getAbsolutePath()+File.separator+tempFile)),"*/*");
//									context.startActivity(intent);
									
									Intent intent = new Intent(context,FlashView.class);
									Bundle bd = new Bundle();
									bd.putString(Constant.foldPath, tempFile);
									intent.putExtras(bd);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
									context.startActivity(intent);									
								}
							}
						}
						if(progress.isShowing()){
							progress.dismiss();
						}
					}
				}.start();
				
				
			}catch(Exception e){}
			return ;
		}else if(type==ResourceBean.ResourceType.fold){
			intent = new Intent(context, IndexActivity.class);   
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bd = new Bundle();
			bd.putString(Constant.foldPath, path);
			bd.putInt(Constant.foldDepth, resourceBean.getFoldDepth()+1);
			bd.putString(Constant.viewClass, Constant.secondViewClass);
			intent.putExtras(bd);
			context.startActivity(intent);
			if(progress.isShowing()){
				progress.dismiss();
			}
			return ;
		}else if(type==ResourceBean.ResourceType.pdf){
			fileName = "temp1.pdf";
			intentType = "application/pdf";
		}else if(type==ResourceBean.ResourceType.txt){
			fileName = "temp1.txt";
			intentType = "text/plain";
		}else if(type==ResourceBean.ResourceType.ppt){
			fileName = "temp1.ppt";
			intentType = "application/vnd.ms-powerpoint";
		}else if(type==ResourceBean.ResourceType.xls){
			fileName = "temp1.xls";
			intentType = "application/vnd.ms-excel";
		}else if(type==ResourceBean.ResourceType.doc){
			fileName = "temp1.doc";
			intentType = "application/msword";
		}else if(type==ResourceBean.ResourceType.flv){
			fileName = "temp1.flv";
			intentType = "*/*";
		}else{
			fileName = "temp1"+path.substring(path.lastIndexOf("."));
			intentType = "*/*";
		}
		
		try{
			if(LoadResources.saveToTempFile(context, path, resourceBean.getDirType(),fileName)){
 
				if(type==ResourceBean.ResourceType.apk){
					String packageName = LoadResources.getInstalledPackName(context, context.getFilesDir().getAbsolutePath()+File.separator+fileName);
					if(packageName!=null){//已经安装,则打开该activity
						LoadResources.startApp(context, packageName);
						if(progress.isShowing()){
							progress.dismiss();
						}
						return ;
					}
				}
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent.setDataAndType(Uri.fromFile(new File(context.getFilesDir().getAbsolutePath()+File.separator+fileName)),intentType);
				context.startActivity(intent);

			}
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(progress.isShowing()){
			progress.dismiss();
		}

	}
	
	public void setImageMove(boolean canMove){
    	imageCanMove = canMove;
    }
    
    public boolean getImageMove(){
    	return imageCanMove;
    }


	public float getZoom() {
		return zoom;
	}


	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
    
    
}
