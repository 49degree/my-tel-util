package com.skyeyes.storemonitor.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.skyeyes.base.util.ViewUtils;
import com.skyeyes.base.view.Menu;
import com.skyeyes.base.view.Menu.MenuListener;
import com.skyeyes.base.view.SlidingMenu;
import com.skyeyes.base.view.SlidingMenu.OnOpenedListener;
import com.skyeyes.storemonitor.R;
import com.skyeyes.storemonitor.service.DevicesService;



@SuppressWarnings("deprecation")
public class HomeActivity extends SlidingActivity implements MenuListener,OnOpenedListener {
	private LocalActivityManager mManager;
	
	private static HomeActivity instance = null;
	// ui
	private SlidingMenu mSlidingMenu;
	private FrameLayout mActivities;
	private TextView mTip;
	private TabHost mTabHost;
	// Tab id
	public static final String TAB_CATEGORY = "category";
	public static final String TAB_AMUSE = "amuse";
	public static final String TAB_PLAY = "play";
	public static final String TAB_BOUTIQUE = "boutique";
	public static final String TAB_NECESSARY = "necessary";
	public static final String TAB_MANAGE = "manage";

	private Menu mMenu;
	private Stack<Class<?>> mIndex = new Stack<Class<?>>();
	private long mExitTime;
	private int mPage = 0;
	private int mCurPage = -1;

	public static HomeActivity getInstance(){
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_home_page);
		mManager = getLocalActivityManager();
		instance = this;
		doInitView();
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 清除所有API缓存
		try{
			stopService(new Intent(this,DevicesService.class));
		}catch(Exception e){
			
		}
		
	}

	/** 初始化视图 */
	private void doInitView() {
		initSlidingUI();
		initTab();
		
	}

	/** 初始化侧边栏 */
	private void initSlidingUI() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = (int) (metric.density * 55);
		mMenu = new Menu(this);
		mActivities = (FrameLayout) findViewById(R.id.frame_activities);
		mActivities.setVisibility(View.GONE);
		setBehindContentView(mMenu.getView());
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setSlidingEnabled(true);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mSlidingMenu.setBehindOffsetInterger(width);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindScrollScale(0.25f);
		mSlidingMenu.setFadeDegree(0.25f);
		mMenu.setMenuListener(this);
		mSlidingMenu.setOnOpenedListener(this);
	}
	TabSpec tab4 = null;
	/** 初始化底部标签栏 */
	private void initTab() {
		mTabHost = (TabHost) findViewById(R.id.home_tabhost);
		mTabHost.setup(getLocalActivityManager());

		TabSpec tab1 = mTabHost
				.newTabSpec(TAB_CATEGORY)
				.setIndicator(
						createTabView(getApplicationContext(), getString(R.string.home_tab_screen),
								R.drawable.home_tab_screen_selector))
				.setContent(new Intent(this, MainPageActivity.class));
		mTabHost.addTab(tab1);

		TabSpec tab2 = mTabHost
				.newTabSpec(TAB_PLAY)
				.setIndicator(
						createTabView(getApplicationContext(), getString(R.string.home_tab_play),
								R.drawable.home_tab_recording_selector)).setContent(new Intent(this, DoorRecordActivity.class));
		mTabHost.addTab(tab2);

		TabSpec tab3 = mTabHost
				.newTabSpec(TAB_BOUTIQUE)
				.setIndicator(
						createTabView(getApplicationContext(), getString(R.string.home_tab_traffic_statistic),
								R.drawable.home_tab_abortion_selector))
				.setContent(new Intent(this, TrafficStatisticsActivity.class));
		mTabHost.addTab(tab3);

		tab4 = mTabHost
				.newTabSpec(TAB_NECESSARY)
				.setIndicator(
						createTabView(getApplicationContext(), getString(R.string.home_tab_necessary),
								R.drawable.home_tab_status_selector))
				.setContent(new Intent(this, DevicesStatusActivity.class));
		mTabHost.addTab(tab4);
		setCurrentTab(mPage);
	}

	public void setCurrentTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	/** 创建TabWidget的View */
	private View createTabView(Context context, String text, int imageResource) {
		View view = LayoutInflater.from(context).inflate(R.layout.app_bottom_tab_view, null);
		((ImageView) view.findViewById(R.id.tab_widget_icon)).setImageResource(imageResource);
		((TextView) view.findViewById(R.id.tab_widget_content)).setText(text);
//		if (text.equals(getString(R.string.home_tab_manage))) {
//			mTip = (TextView) view.findViewById(R.id.tab_widget_tip);
//		}
		return view;
	}
	





	/** 显示或关闭左侧菜单 */
	public void toggleMenu() {
		setSlidingEnabled(true);
		mSlidingMenu.toggle();
	}

	public void switchActivity(Class<?> cls, Bundle bundle) {
		mActivities.setVisibility(View.VISIBLE);
		mTabHost.setVisibility(View.GONE);
		mActivities.clearFocus();
		mCurPage = mTabHost.getCurrentTab();
		mTabHost.setCurrentTab(0);
		setSlidingEnabled(false);
		mActivities.removeAllViews();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(HomeActivity.this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		addView(cls);
		View v = mManager.startActivity(cls.getName(), intent).getDecorView();
		mActivities.addView(v);
		postDelayed(new Runnable() {
			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}

	public boolean destroyActivity(String id) {
		if (mManager != null) {
			mManager.destroyActivity(id, false);
			try {
				final Field ActivitiesField = LocalActivityManager.class.getDeclaredField("mActivities");
				if (ActivitiesField != null) {
					ActivitiesField.setAccessible(true);
					@SuppressWarnings("unchecked")
					final Map<String, Object> mActivities = (Map<String, Object>) ActivitiesField.get(mManager);
					if (mActivities != null) {
						mActivities.remove(id);
					}
					final Field ActivityArrayField = LocalActivityManager.class.getDeclaredField("mActivityArray");
					if (ActivityArrayField != null) {
						ActivityArrayField.setAccessible(true);
						@SuppressWarnings("unchecked")
						final ArrayList<Object> ActivityArray = (ArrayList<Object>) ActivityArrayField.get(mManager);
						if (ActivityArray != null) {
							for (Object record : ActivityArray) {
								final Field idField = record.getClass().getDeclaredField("id");
								if (idField != null) {
									idField.setAccessible(true);
									final String _id = (String) idField.get(record);
									if (id.equals(_id)) {
										ActivityArray.remove(record);
										break;
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggleMenu();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSlidingMenu.isMenuShowing()) {
				toggleMenu();
				return true;
			}
			if (mManager.getCurrentActivity() != null && mTabHost.getVisibility() == View.GONE) {
				mManager.getCurrentActivity().onKeyDown(keyCode, event);
				return true;
			}
		}
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				ViewUtils.show(getApplication(), "再按一次退出程序");
				
				mExitTime = System.currentTimeMillis();
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onMenuClick(int id) {
		switch (id) {
		case R.id.menu_about:
//			AboutDialog aboutDialog = new AboutDialog(this, R.style.dialog);
//			aboutDialog.show();
			break;
		case R.id.menu_alerm:
//			switchActivity(FeedBackActivity.class, null);
			break;
		case R.id.menu_help:
			break;
		case R.id.menu_setting:
			switchActivity(SettingActivity.class, null);
			break;
		case R.id.menu_exit:
			break;
		}

	}

	
	

	public void setTouchMode(boolean enable) {
		if (enable) {
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	public void setSlidingEnabled(boolean enable) {
		mSlidingMenu.setSlidingEnabled(enable);
	}

	public void backToPreView() {
		String preId = mManager.getCurrentId();
		if (mIndex.size() > 1) {
			getPreView();
			switchActivity(getPreView(), null);
		} else {
			getPreView();
			mActivities.clearFocus();
			mActivities.removeAllViews();
			mActivities.setVisibility(View.GONE);
			mTabHost.setCurrentTab(mCurPage);
			mTabHost.setVisibility(View.VISIBLE);
			mSlidingMenu.setSlidingEnabled(true);
			postDelayed(new Runnable() {
				@Override
				public void run() {
					toggleMenu();
				}
			}, 50);
		}
		destroyActivity(preId);
	}

	public void backToView(int page) {
		String preId = mManager.getCurrentId();
		if (mIndex.size() > 1) {
			getPreView();
			destroyActivity(preId);
		}
		mActivities.clearFocus();
		mActivities.removeAllViews();
		mActivities.setVisibility(View.GONE);
		mTabHost.requestFocus();
		mTabHost.setVisibility(View.VISIBLE);
		if (page > 0 && mTabHost.getCurrentTab() != page) {
			mTabHost.setCurrentTab(page);
		}
		mSlidingMenu.setSlidingEnabled(true);
		postDelayed(new Runnable() {
			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}

	private void addView(Class<?> index) {
		removeView(index);
		mIndex.add(index);
	}

	private Class<?> getPreView() {
		if (mIndex.size() > 0) {
			Class<?> viewClass = mIndex.pop();
			return viewClass;
		}
		return null;
	}

	private void removeView(Class<?> index) {
		for (Class<?> Index : mIndex) {
			if (index == Index) {
				mIndex.remove(Index);
				break;
			}
		}
	}

	@Override
	public void onOpened() {
		
	}
	
	
	public class DeviceStatusChangeBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(DevicesService.DeviceAlarmBroadCast)){
//				tab4.setIndicator(createTabView(getApplicationContext(), getString(R.string.home_tab_necessary),
//										R.drawable.home_tab_status_selector));
//				tab4.setContent(new Intent(HomeActivity.this, DevicesStatusActivity.class));
			}
			
		}
		
	}
}
