package com.skyeyes.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyeyes.storemonitor.R;

public class TopTitleView extends LinearLayout {
	private Context mContext;

	private boolean menuBtnIsShow;
	private boolean centerButtonsIsShow;

	private boolean centerTitleIsShow;
	private Drawable menuBg;
	private LayoutInflater mLayoutInflater;

	private ViewGroup mViewGroup;
	private ImageView menuImg;
	private RelativeLayout centerButtonsRl;
	private TextView centerTitleTv;
	private TextView leftBtn;
	private TextView rightBtn;
	private String leftBtnText;
	private String rightBtnText;
	private String titleValue;

	private OnClickListenerCallback menuButtonClickListener,
			leftButtonClickListener, rightButtonClickListener;

	public interface OnClickListenerCallback {
		void onClick();
	}

	public void setOnMenuButtonClickListener(
			OnClickListenerCallback onItemClickListener) {
		this.menuButtonClickListener = onItemClickListener;
	}

	public void setOnLeftButtonClickListener(
			OnClickListenerCallback onItemClickListener) {
		this.leftButtonClickListener = onItemClickListener;
	}

	public void setOnRightButtonClickListener(
			OnClickListenerCallback onItemClickListener) {
		this.rightButtonClickListener = onItemClickListener;
	}

	public TopTitleView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		this.mContext = context;

	}

	public TopTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TopView);
		menuBtnIsShow = a.getBoolean(R.styleable.TopView_showMenuBtn, false);
		centerButtonsIsShow = a.getBoolean(R.styleable.TopView_showCenterButtons,
				false);
		menuBg = a.getDrawable(R.styleable.TopView_menuButtonBackgound);
		centerTitleIsShow = a
				.getBoolean(R.styleable.TopView_showCenterTitle, false);
		
		leftBtnText = a.getString(R.styleable.TopView_leftButtonText);
		rightBtnText = a.getString(R.styleable.TopView_rightButtonText);
		titleValue = a.getString(R.styleable.TopView_titleValue);
		Log.i("chenlong","leftBtnText "+leftBtnText);
		
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.top_view,
				this, false);
		initView();
		addView(mViewGroup);
		a.recycle();


	}

	private void initView() {
		mViewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.top_view,
				this, false);
		menuImg = (ImageView) mViewGroup.findViewById(R.id.app_left_menu_iv);
		centerButtonsRl = (RelativeLayout) mViewGroup
				.findViewById(R.id.buttons_rl);
		centerTitleTv = (TextView) mViewGroup.findViewById(R.id.center_title);
		leftBtn = (TextView) mViewGroup.findViewById(R.id.left_b);
		rightBtn = (TextView) mViewGroup.findViewById(R.id.right_b);
		
		if(menuBg!=null){
			
	        int sdk = android.os.Build.VERSION.SDK_INT;
	        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	        	menuImg.setBackgroundDrawable(menuBg);
	        }else{
	        	menuImg.setBackground(menuBg);
	        }
		}
		menuImg.setVisibility(menuBtnIsShow ? View.VISIBLE : View.GONE);
		centerButtonsRl.setVisibility(centerButtonsIsShow ? View.VISIBLE : View.GONE);
		centerTitleTv.setVisibility(centerTitleIsShow ? View.VISIBLE : View.GONE);
		leftBtn.setText(leftBtnText!=null?leftBtnText:"");
		rightBtn.setText(rightBtnText!=null?rightBtnText:"");
		centerTitleTv.setText(titleValue!=null?titleValue:"");
		menuImg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(menuButtonClickListener != null)
				menuButtonClickListener.onClick();
			}
		});
		leftBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				rightBtn.setBackgroundResource(R.drawable.textview_style);
				rightBtn.setTextColor(getResources().getColor(R.color.white));
				leftBtn.setBackgroundResource(R.drawable.textview_style_b);
				leftBtn.setTextColor(getResources().getColor(R.color.black));
				if(leftButtonClickListener!=null)
					leftButtonClickListener.onClick();
			}
		});
		rightBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				leftBtn.setBackgroundResource(R.drawable.textview_style);
				leftBtn.setTextColor(getResources().getColor(R.color.white));
				rightBtn.setBackgroundResource(R.drawable.textview_style_b);
				rightBtn.setTextColor(getResources().getColor(R.color.black));
				if(rightButtonClickListener!=null)
					rightButtonClickListener.onClick();
			}
		});
	}
}
