package com.skyeyes.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.skyeyes.storemonitor.R;

/** @author stefan 左菜单 */
public class Menu implements OnClickListener {
	private View mMenu;
	private Context mCtx;
	private LinearLayout mAlerm;

	private LinearLayout mSetting;
	private LinearLayout mHelp;
	private LinearLayout mAbout;
	private LinearLayout mExit;

	private MenuListener mListener;

	@SuppressLint("NewApi")
	public Menu(Context context) {
		super();
		this.mCtx = context;
		initView();
		setListener();

	}

	/** 初始化ui */
	private void initView() {
		mMenu = LayoutInflater.from(mCtx).inflate(R.layout.app_left_menu, null);
		mAlerm = (LinearLayout) mMenu.findViewById(R.id.menu_alerm);
		mSetting = (LinearLayout) mMenu.findViewById(R.id.menu_setting);
		mHelp = (LinearLayout) mMenu.findViewById(R.id.menu_help);
		mAbout = (LinearLayout) mMenu.findViewById(R.id.menu_about);
		mExit = (LinearLayout) mMenu.findViewById(R.id.menu_exit);
	}

	private void setListener() {
		mAlerm.setOnClickListener(this);
		mSetting.setOnClickListener(this);
		mHelp.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		mExit.setOnClickListener(this);
	}


	public View getView() {
		return mMenu;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null)
			mListener.onMenuClick(v.getId());

	}

	public interface MenuListener {
		public void onMenuClick(int id);
	}

	public void setMenuListener(MenuListener listener) {
		this.mListener = listener;
	}
}