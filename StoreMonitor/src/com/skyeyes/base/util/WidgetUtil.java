package com.skyeyes.base.util;

import java.util.Calendar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * 控件处理帮助类
 * 
 * @author
 */
public class WidgetUtil {
//
//	/**
//	 * Activity显示的宽和高
//	 * 
//	 * @param activity
//	 * @return
//	 */
//	public static DisplayMetrics getMetric(Activity activity) {
//		DisplayMetrics metric = new DisplayMetrics();
//		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//		return metric;
//	}
//
//	// /**
//	// * 显示Toast
//	// * @param title
//	// * @param type
//	// * @param context
//	// */
//	// public static void showToast(int title, int type, Context context) {
//	// showToast(context.getResources().getString(title), type, context);
//	// }
//	//
//	// public static void showToast(int title, Context context) {
//	// showToast(context.getResources().getString(title), Toast.LENGTH_SHORT,
//	// context);
//	// }
//	//
//	// public static void showToast(String title, Context context) {
//	// showToast(title, Toast.LENGTH_SHORT, context);
//	// }
//	//
//	// public static void showToast(String title, int type, Context context) {
//	// Toast.makeText(context, title, type).show();
//	// }
//	/**
//	 * 显示自定义layout的Toast
//	 * 
//	 * @param content
//	 *            显示内容资源Id
//	 * @param activity
//	 */
//	public static void showToast(int content, Context activity) {
//		String strContent = null;
//		if (activity != null) {
//			if (content != 0) {
//				strContent = activity.getResources().getString(content);
//				showToast(strContent, activity);
//			}
//		}
//	}
//
//	/**
//	 * 显示自定义layout的Toast
//	 * 
//	 * @param content
//	 *            显示内容String
//	 * @param activity
//	 */
//	public static void showToast(String content, Context activity) {
//		if (activity != null) {
//			Toast toast = null;
//			toast = new Toast(activity);
//			View layout = ((Activity) activity).getLayoutInflater().inflate(R.layout.nurse_custom_content_toast_view, null);
//			TextView contentTv = (TextView) layout.findViewById(R.id.custom_toast_content_tv);
//
//			if (content != null) {
//				contentTv.setText(content);
//			}
//			toast.setView(layout);
//			toast.setDuration(Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
//		}
//	}
//
//	/**
//	 * 设置listview的高度
//	 * 
//	 * @param listview
//	 */
//	public static void setListViewHeightBasedOnChildren(ListView listview) {
//		ViewGroup.LayoutParams params = listview.getLayoutParams();
//		params.height = getLvHeight(listview);
//		listview.setLayoutParams(params);
//	}
//
//	/**
//	 * 设置带footview的listview的高度
//	 * 
//	 * @param listview
//	 * @param foot
//	 */
//	public static void setListViewHeightBasedOnFoot(ListView listview, View foot) {
//		ViewGroup.LayoutParams params = listview.getLayoutParams();
//		if (0 == foot.getHeight()) {
//			params.height = getLvHeight(listview) + 40;
//		} else {
//			params.height = getLvHeight(listview) + foot.getHeight();
//		}
//		listview.setLayoutParams(params);
//	}
//
//	/**
//	 * 计算ListView的高度
//	 * 
//	 * @param listview
//	 * @return
//	 */
//	public static int getLvHeight(ListView listview) {
//		int totalHeight = 0;
//		ListAdapter adapter = listview.getAdapter();
//		if (null != adapter) {
//			for (int i = 0, len = adapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
//				View listItem = adapter.getView(i, null, listview);
//				listItem.measure(0, 0); // 计算子项View 的宽高
//				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
//			}
//			totalHeight = totalHeight+ (listview.getDividerHeight() * (listview.getCount() - 1) 
//					+ listview.getPaddingTop() + listview.getPaddingBottom());
//		}
//		return totalHeight;
//	}
//
//	public interface MessageDialogHandler {
//		public void onSureHandler(DialogInterface dialog);
//	}
//
//	public interface InitBarcodeHandler {
//		public void onSuccessHandler();
//	}
//
//	/**
//	 * 显示DIALOG
//	 * 
//	 * @param title
//	 * @param message
//	 * @param xPos
//	 * @param yPos
//	 * @param thisContext
//	 */
//
//	public static void showWrongMessageDialog(int title, String message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		showMessageDialog(R.drawable.nurse_common_dialog_not_match_ico, title, message, xPos, yPos, thisContext, handler);
//	}
//
//	public static void showWrongMessageDialog(int title, int message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		showMessageDialog(R.drawable.nurse_common_dialog_not_match_ico, title, message, xPos, yPos, thisContext, handler);
//	}
//
//	public static void showWrongMessageDialog(int title, String message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(R.drawable.nurse_common_dialog_not_match_ico, title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showWrongMessageDialog(int title, int message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(R.drawable.nurse_common_dialog_not_match_ico, title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showMessageDialog(int title, int message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showMessageDialog(int title, String message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showMessageDialog(String title, String message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showMessageDialog(int dialogIco, int title, int message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(dialogIco, title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showMessageDialog(int dialogIco, String title, String message, int xPos, int yPos, Context thisContext) {
//		showMessageDialog(dialogIco, title, message, xPos, yPos, thisContext, null);
//	}
//
//	public static void showChoiceDialog(int dialogIco, int title, int message, int xPos, int yPos, Context thisContext,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener, OnDismissListener mDismissCallBack) {
//		MnisApplication.getInstance().registerBarcodeListenerNull();
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		customBuilder.setPositiveButton(R.string.nurse_make_sure, sureListener);
//		if (canncelListener != null) { // 只需要一个按钮就传 null
//			customBuilder.setNegativeButton(R.string.nurse_cancel, canncelListener);
//		}
//		if (xPos > 0) {
//			customBuilder.setPositionX(xPos);
//		}
//		if (yPos > 0) {
//			customBuilder.setPositionY(yPos);
//		}
//		CustomDialog dialog = customBuilder.create();
//		dialog.setOnDismissListener(mDismissCallBack);
//		dialog.show();
//	}
//
//	public static void showChoiceDialog(int dialogIco, int title, String message, int xPos, int yPos, Context thisContext,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener, OnDismissListener mDismissCallBack) {
//		MnisApplication.getInstance().registerBarcodeListenerNull();
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		customBuilder.setPositiveButton(R.string.nurse_make_sure, sureListener);
//		if (canncelListener != null) { // 只需要一个按钮就传 null
//			customBuilder.setNegativeButton(R.string.nurse_cancel, canncelListener);
//		}
//		if (xPos > 0) {
//			customBuilder.setPositionX(xPos);
//		}
//		if (yPos > 0) {
//			customBuilder.setPositionY(yPos);
//		}
//		CustomDialog dialog = customBuilder.create();
//		dialog.setOnDismissListener(mDismissCallBack);
//		dialog.show();
//	}
//
//	public static void showMessageDialog(int title, int message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		showMessageDialog(R.drawable.nurse_common_dialog_match_icon, title, message, xPos, yPos, thisContext, handler);
//	}
//
//	public static void showMessageDialog(int title, String message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		showMessageDialog(R.drawable.nurse_common_dialog_match_icon, title, message, xPos, yPos, thisContext, handler);
//	}
//
//	public static void showMessageDialog(String title, String message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		showMessageDialog(R.drawable.nurse_common_dialog_match_icon, title, message, xPos, yPos, thisContext, handler);
//	}
//
//	public static void showMessageDialog(int dialogIco, int title, int message, int xPos, int yPos, Context thisContext, MessageDialogHandler handler) {
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		showMessageDialog(customBuilder, xPos, yPos, handler);
//	}
//
//	public static void showMessageDialog(int dialogIco, int title, String message, int xPos, int yPos, Context thisContext,
//			MessageDialogHandler handler) {
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		showMessageDialog(customBuilder, xPos, yPos, handler);
//	}
//
//	public static void showMessageDialog(int dialogIco, String title, String message, int xPos, int yPos, Context thisContext,
//			MessageDialogHandler handler) {
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		showMessageDialog(customBuilder, xPos, yPos, handler);
//	}
//
//	public static void showMessageDialog(CustomDialog.Builder customBuilder, int xPos, int yPos, final MessageDialogHandler handler) {
//		customBuilder.setFormAlpha(1.0f).setPositiveButton(R.string.nurse_make_sure, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				if (handler != null)
//					handler.onSureHandler(dialog);
//			}
//		});
//		if (xPos > 0) {
//			customBuilder.setPositionX(xPos);
//		}
//		if (yPos > 0) {
//			customBuilder.setPositionY(yPos);
//		}
//		customBuilder.create().show();
//	}
//
//	/**
//	 * 弹出可选择处理的对话框 sureListener 点击确认回调对象 canncelListener 点击取消回调对象
//	 * 
//	 * @param title
//	 * @param message
//	 * @param xPos
//	 * @param yPos
//	 * @param thisContext
//	 * @param sureListener
//	 * @param canncelListener
//	 */
//	public static void showChoiceDialog(int title, int message, int xPos, int yPos, Context thisContext,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener) {
//		WidgetUtil.showChoiceDialog(R.drawable.nurse_common_dialog_ask_icon, title, message, xPos, yPos, thisContext, sureListener, canncelListener);
//	}
//
//	public static void showMatchDialog(int title, int message, int xPos, int yPos, Context thisContext, int left, int right,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener) {
//		WidgetUtil.showMatchDialog(R.drawable.nurse_common_dialog_match_icon, title, message, xPos, yPos, thisContext, left, right, sureListener,
//				canncelListener);
//	}
//
//	public static void showChoiceDialog(int dialogIco, int title, int message, int xPos, int yPos, Context thisContext,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener) {
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		customBuilder.setPositiveButton(R.string.nurse_make_sure, sureListener);
//		if (canncelListener != null) { // 只需要一个按钮就传 null
//			customBuilder.setNegativeButton(R.string.nurse_cancel, canncelListener);
//		}
//		if (xPos > 0) {
//			customBuilder.setPositionX(xPos);
//		}
//		if (yPos > 0) {
//			customBuilder.setPositionY(yPos);
//		}
//		customBuilder.create().show();
//	}
//
//	public static void showMatchDialog(int dialogIco, int title, int message, int xPos, int yPos, Context thisContext, int left, int right,
//			DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener canncelListener) {
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(thisContext);
//		customBuilder.setTitle(title).setMessage(message).setIcon(dialogIco);
//		customBuilder.setPositiveButton(left == 0 ? R.string.nurse_make_sure : left, sureListener);
//		if (canncelListener != null) { // 只需要一个按钮就传 null
//			customBuilder.setNegativeButton(right == 0 ? R.string.nurse_cancel : right, canncelListener);
//		}
//		if (xPos > 0) {
//			customBuilder.setPositionX(xPos);
//		}
//		if (yPos > 0) {
//			customBuilder.setPositionY(yPos);
//		}
//		customBuilder.create().show();
//	}
//
//	/** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
//	public static int dip2px(Context c, float dpValue) {
//		final float scale = c.getResources().getDisplayMetrics().density;
//		return (int) (dpValue * scale + 0.5f);
//	}
//
//	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
//	public static int px2dip(Context c, float pxValue) {
//		final float scale = c.getResources().getDisplayMetrics().density;
//		return (int) (pxValue / scale + 0.5f);
//	}
//
//	/**
//	 * 播放音频
//	 */
//	public static void playAudio(Context context, MediaPlayer player, int audioRes) {
//		SharedPreferences prefereces = context.getSharedPreferences(PreferenceUtil.SYSCONFIG_PREFERENCES, 0);
//		if (prefereces.getBoolean("selectSound", true)) {
//			if (null == player) {
//				player = MediaPlayer.create(context, audioRes);
//				player.start();
//			}
//			player.setOnSeekCompleteListener(new OnSeekCompleteListener() {
//				@Override
//				public void onSeekComplete(MediaPlayer mp) {
//					mp.release();
//					mp = null;
//				}
//			});
//		}
//	}
//
//	public static void playAudioWithSoundpool(SoundPool soundPool,int id) {
//		soundPool.play(id, 1, 1, 0, 0, 1);
//	}
//
//	
//	/**
//	 * 暂停音频
//	 */
//	public static void stopAudio(MediaPlayer player) {
//		if (null != player) {
//			if (player.isPlaying()) {
//				player.stop();
//			}
//			player.release();
//			player = null;
//		}
//	}
//
//	/** 振动提示 */
//	public static void startVibrator(Context context) {
//		Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
//		vibrator.vibrate(new long[] { 50, 400 }, -1);
//	}
	
	/**
	 * 获取时间，转换成Calendar
	 * 
	 * @param button
	 * @return
	 */
	public static Calendar gerCalendarByView(View view) {
		String date = null;
		Calendar calendar = null;
		if (view instanceof Button) {
			Button button = (Button) view;
			date = button.getText().toString().trim();
		} else if (view instanceof TextView) {
			TextView textview = (TextView) view;
			date = textview.getText().toString().trim();
		} else if (view instanceof EditText) {
			EditText editText = (EditText) view;
			date = editText.getText().toString().trim();
		}
		if (date != null && date.length() > 0) {
			calendar = DateUtil.getCalendarByString(date, DateUtil.TIME_FORMAT_YMD);
		}
		return calendar;
	}

	public static Calendar gerCalendarByViewHhMm(View view) {
		String date = null;
		Calendar calendar = null;
		if (view instanceof Button) {
			Button button = (Button) view;
			date = button.getText().toString().trim();
		} else if (view instanceof TextView) {
			TextView textview = (TextView) view;
			date = textview.getText().toString().trim();
		} else if (view instanceof EditText) {
			EditText editText = (EditText) view;
			date = editText.getText().toString().trim();
		}
		if (date != null && date.length() > 0) {
			calendar = DateUtil.getCalendarByString(date, DateUtil.TIME_FORMAT_HM);
		}
		return calendar;
	}

}
