package com.yangxp.config.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yangxp.ginput.R;

/**
 * 提示信息栏
 * 1. 更好的引导用户使用
 * 2. 提高用户体验,显示操作提示信息
 * */
public class InformationBar 
{
	private final static String TAG = "InformationBar";
	private LayoutInflater mInflater;
	
	//显示文本内容
	private boolean mShowText;
	
	private WindowManager.LayoutParams mParams;
	private RelativeLayout mLayout;
	private TextView mTextView;
	//透明度的设置
	private AlphaAnimation mAnimation;
	
	//informationBar 是否显示 在addview 和removeView后赋值
	private boolean mInforBarShow;
	
	private String mTextStr;
	private Context mContext;
	private Handler mHandler;
	
	//信息布局是否可视
	private boolean mVisible = false;
	
	private WindowManager mWindowManager;

	public InformationBar(Context context) {
		mHandler = new AnimationHandler(this);
		mContext = context;
        
		//创建
		mInflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mWindowManager = (WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE);
        
		//创建WindowManager.LayoutParams()对象去设置悬浮窗的参数
		mParams = new WindowManager.LayoutParams();
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.format = PixelFormat.TRANSLUCENT;
		
		//悬浮窗口不不响应任何时间
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		
        //先将information_bar布局文件实例化
		mLayout = (RelativeLayout)mInflater.inflate(R.layout.information_bar, null);
		
		//然后找到information_bar布局中的information_text，并用TextView显示出来
		mTextView = (TextView)mLayout.findViewById(R.id.information_text);
	}

	/**
	 * 动画开始显示
	 * */
	private void animationShow() {
		//
		animationHide();
		initAnimation();
		//延时1秒，发送值为0的消息
		mHandler.sendEmptyMessageDelayed(0, 1000);
		Log.i(TAG, "animationShow sendEmptyMessageDelayed(0, 1000)");
	}

	/**
	 * 动画隐藏
	 * */
	//Register a callback to be invoked when a touch event is sent to this view
	private void animationHide() {
		this.mHandler.removeMessages(0);
		if (mAnimation == null) {
			return;
		}
		mAnimation.cancel();
		mAnimation = null;
	}

	private void initAnimation() {
		//fromAlpha开始设置为1表示透明，toAlpha设置为0表示完全透明
		mAnimation = new AlphaAnimation(1, 0);
		//动画持续的时间
		mAnimation.setDuration(3000);
		//动画的监听
		AnimationListener listener = new AnimationListener(this);
		mAnimation.setAnimationListener(listener);
	}

	/**
	 * 显示提示信息内容
	 * @param id stringId
	 * @param 是否动画显示效果
	 * 
	 * */
	public void informationShow(int id, boolean show) {
		String str = mContext.getResources().getString(id);
		informationShow(str, show);
	}

	public void informationShow(String str, boolean show){
		//信息窗口没有显示
		if (!mInforBarShow) {
			mTextStr = str;
			mShowText = show;
		}

		mTextView.setText(str);

		if (!mVisible) {
			//设置添加的mLayout的状态
			mLayout.setVisibility(View.VISIBLE);
			mVisible = true;
		}

		if (show) {
			animationShow();
		} else {
			animationHide();
		}
	}

	/**
	 * 添加提示窗口
	 * 
	 * */
	public void informationBarShow() {
		mInforBarShow = true;

		//This view is invisible, but it still takes up space for layout purposes
		mLayout.setVisibility(View.INVISIBLE);
		mWindowManager.addView(mLayout, mParams);

		if (this.mTextStr != null) {
			String str = this.mTextStr;
			boolean show = mShowText;
			informationShow(str, show);
		}
		this.mTextStr = null;
	}

	/**
	 * 隐藏提示窗口
	 * 
	 * */
	public void informationBarHide() {
		hide();
		mWindowManager.removeView(mLayout);
		this.mTextStr = null;
	}

	public void hide() {
		if (!mInforBarShow) {
			this.mTextStr = null;
		}

		if (mVisible) {
			animationHide();
			this.mLayout.setVisibility(View.INVISIBLE);
			this.mVisible = false;
		}
	}

	/**
	 * 根据id显示文本内容
	 * 1. 外部调用接口
	 * */
	public void showInformations(int id) {
		informationShow(id, true);
	}
	
	public TextView getTextView () {
		if (this.mTextView != null) {
			return mTextView;
		}
		
		return null;
	}
	
	public AlphaAnimation getAnimation() {
		if (this.mAnimation != null) {
			return mAnimation;
		}
		
		return null;
	}
	
	public RelativeLayout getLayout() {
		if (this.mLayout != null) {
			return mLayout;
		}
		
		return null;
	}
	
	/**
	 * 提示信息布局是否显示
	 * */
	public Boolean getVisibale() {
	   return mVisible;
	}
	
	/**
	 * 设置信息布局显示状态
	 * */
	public void setVisible(boolean visible) {
		mVisible = visible;
	}

	/**
	 * 提示框显示和隐藏动画处理handler
	 * 
	 * */
	class AnimationHandler extends Handler {
		private InformationBar mInformationBar;
		private TextView mTextView;
		private AlphaAnimation mAnimation;
		
		public AnimationHandler (InformationBar information) {
			mInformationBar = information;
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mTextView = mInformationBar.getTextView();
				mAnimation = mInformationBar.getAnimation();
				mTextView.startAnimation(mAnimation);
				break;			
				
			default:
				break;
			}
		}
	}


	/**
	 * An animation listener receives notifications from an animation.
	 * Notifications indicate animation related events, such as the end or the
	 * repetition of the animation.
	 */
	class AnimationListener implements Animation.AnimationListener {
		private InformationBar mInformationBar;
		
		AnimationListener(InformationBar information){
			mInformationBar = information;
		}

		public void onAnimationEnd(Animation animation) {
			if (mInformationBar.getAnimation() != animation) {
				return;
			}
		    
			//动画结束设置信息
			if (mInformationBar.getVisibale()) {
			    mInformationBar.getLayout().setVisibility(View.INVISIBLE);
			    mInformationBar.setVisible(false);
			}
		}

		// Notifies the repetition of the animation
		public void onAnimationRepeat(Animation animation) {		
		}

		//Notifies the start of the animation
		public void onAnimationStart(Animation animation) {
		}
	}
}