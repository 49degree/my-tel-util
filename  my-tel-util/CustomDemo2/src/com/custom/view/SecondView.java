package com.custom.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.custom.bean.PageNumBean;
import com.custom.bean.ResourceBean;
import com.custom.utils.Constant;
import com.custom.utils.Logger;
import com.custom.utils.SharedPreferencesUtils;





public class SecondView extends ViewImp{
	private static final Logger logger = Logger.getLogger(SecondView.class);
	ArrayList<Entry<String,ResourceBean>> resourceInfo = null;
	int curPageNum = 0;
	PageNumBean pageNumBean = null;
	PageNumView pageNumView = null;
	SecondViewGroup viewGroup = null;
	FrameLayout frameLayout = null;
	public boolean isFirstPage = false;
	
	public SecondView(Context context,String foldPath,int foldDepth,boolean isFirstPage){
		super(context,foldPath,foldDepth);
		this.isFirstPage = isFirstPage;
		
	}
	
	public SecondView(Context context,String foldPath,int foldDepth){
		super(context,foldPath,foldDepth);
	}
	
	public SecondView(Context context, AttributeSet attr,String foldPath,int foldDepth){
        super(context, attr,foldPath,foldDepth);
	}

	@Override
	protected void createIndexButton() {
		//对资源进行排序
		if(resourceInfo==null){
			resourceInfo = new ArrayList<Entry<String,ResourceBean>>(scanFoldUtils.resourceInfo.entrySet());     
			Collections.sort(resourceInfo,new Comparator<Map.Entry<String, ResourceBean>>() {
						@Override
						public int compare(Entry<String, ResourceBean> object1,
								Entry<String, ResourceBean> object2) {
							// TODO Auto-generated method stub
							return object1.getKey().compareTo(object2.getKey());
						}
					}); 
		}

//		Entry<String,ResourceBean> data=  resourceInfo.get(0);
//		Entry<String,ResourceBean> data2=  resourceInfo.get(0);
//		resourceInfo.removeAll(resourceInfo);
//		for(int i=0;i<32;i++){
//			resourceInfo.add(data);
//		}
//		
//		for(int i=0;i<12;i++){
//			resourceInfo.add(data2);
//		}
//		for(int i=0;i<40;i++){
//			resourceInfo.add(data);
//		}
		
		if(pageNumBean==null){
			pageNumBean = new PageNumBean(resourceInfo.size());	
		}
		
		float zoom = 0.6f;
		
		frameLayout = new FrameLayout(this.context);
		LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
				screenWidth, screenHeight);
		frameLayout.setLayoutParams(frameLayoutParams);
		if(!isFirstPage&&!Constant.noPageNum){
			pageNumView = new PageNumView(this.context,pageNumBean,scanFoldUtils.foldPath);
			pageNumView.setZoom(zoom);
		}
		
		
		if(pageNumView==null){
			pageNumBean.setButtonPerPage(6);
		}else{
			pageNumBean.setButtonPerPage(8);
		}
		pageNumBean.setCurPageNum(curPageNum);
		
		SecondViewGroup viewGroup = new SecondViewGroup(this.context,resourceInfo,pageNumView,pageNumBean,scanFoldUtils.foldPath,zoom);
		frameLayout.addView(viewGroup);

		if(pageNumView!=null){
			pageNumView.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT) );
			frameLayout.addView(pageNumView);
			pageNumView.initView();
		}

		mLayout.addView(frameLayout);
		
	}
	@Override
	protected void setXY(ResourceBean resourceBean) {
	}
	@Override
	protected int[] calBackGroudView(Bitmap bm){
		return super.calBackGroudView(bm);
		
	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		if(this.pageNumBean!=null){
			SharedPreferencesUtils.setConfigString(SharedPreferencesUtils.COMFIG_INFO, 
					SharedPreferencesUtils.CURPAGENUM, String.valueOf(this.pageNumBean.getCurPageNum()));	
		}
		super.onStop();

	}
	@Override
	public void onStart() {
		try{
			curPageNum = Integer.parseInt(SharedPreferencesUtils.getConfigString(SharedPreferencesUtils.COMFIG_INFO, 
					SharedPreferencesUtils.CURPAGENUM));
		}catch(Exception e){
			curPageNum = 0;
		}

		super.onStart();
	}
	
	public void onDestroy(){
		logger.equals("onDestroy");
		
		if(bm!=null&&!bm.isRecycled()){
			logger.error("onDestroy:"+bm.hashCode());
			bm.recycle();
		}
		bm = null;
		if(resourceInfo!=null){
			for(int i=0;i<resourceInfo.size();i++){
				ResourceBean resourceBean = resourceInfo.get(i).getValue();
				if(resourceBean.getBm()!=null&&!resourceBean.getBm().isRecycled()){
					//logger.error("+++++++++++onDestroy resourceBean:"+resourceBean.getBm().hashCode());
					resourceBean.getBm().recycle();
				}
			}
		}
		resourceInfo = null;
		super.onDestroy();
		
	}

	public boolean isFirstPage() {
		return isFirstPage;
	}

	public void setFirstPage(boolean isFirstPage) {
		this.isFirstPage = isFirstPage;
	}
	
}
