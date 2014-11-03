package com.yangxp.ginput;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.yangxp.ginput.virtue.JoystickKeyHandler;

public class GameInputService extends InputMethodService {
	
	/**
	 * Constructor
	 */
	public GameInputService() {
		super();
	}

	/***********************************************************************
	 * InputMethodService
	 **********************************************************************/
	/** @see android.inputmethodservice.InputMethodService#onCreate */
	@Override
	public void onCreate() {
		super.onCreate();
		JoystickKeyHandler.init(this);
	}

	/** @see android.inputmethodservice.InputMethodService#onCreateCandidatesView */
	@Override
	public View onCreateCandidatesView() {
		return super.onCreateCandidatesView();
	}

	/** @see android.inputmethodservice.InputMethodService#onCreateInputView */
	@Override
	public View onCreateInputView() {
		return super.onCreateInputView();
	}

	/** @see android.inputmethodservice.InputMethodService#onDestroy */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
    /**
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
    	Log.e(this.getClass().getSimpleName(), "onGenericMotionEvent(): event " + event);
		if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK)){
			if(JoystickKeyHandler.getInstance().changeEvent(getTopApp(), event, 0))
				return true;
		}
        return super.onGenericMotionEvent(event);
    }

	/** @see android.inputmethodservice.InputMethodService#onKeyDown */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(this.getClass().getSimpleName(), "keyCode11:" + keyCode);
		//getWinToken();
		if(JoystickKeyHandler.getInstance().changeEvent(getTopApp(), event, 0))
			return true;
		return super.onKeyDown(keyCode, event);
	}

	/** @see android.inputmethodservice.InputMethodService#onKeyUp */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	static final String nullPackName = "";
	
	private String getTopApp() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		if(runningTasks.size()<1){
			return nullPackName;
		}
		RunningTaskInfo runningTaskInfo = runningTasks.get(0);

		ComponentName topActivity = runningTaskInfo.topActivity;
		
		
		return topActivity.getPackageName();
	}
	
    public static IWindowManager getWindowManagerService() {
    	return IWindowManager.Stub.asInterface(
                ServiceManager.getService("window"));
    }
	
	private void getWinToken() {
		IWindowManager manager = getWindowManagerService();
		
		try {
			Log.e(this.getClass().getSimpleName().toString(), manager.getFocusedWindowToken().getInterfaceDescriptor());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** @see android.inputmethodservice.InputMethodService#onStartInput */
	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
	}

	/** @see android.inputmethodservice.InputMethodService#onStartInputView */
	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
	}

	/** @see android.inputmethodservice.InputMethodService#requestHideSelf */
	@Override
	public void requestHideSelf(int flag) {
		super.requestHideSelf(flag);
	}

	/** @see android.inputmethodservice.InputMethodService#setCandidatesViewShown */
	@Override
	public void setCandidatesViewShown(boolean shown) {
		super.setCandidatesViewShown(shown);
	}

	/** @see android.inputmethodservice.InputMethodService#hideWindow */
	@Override
	public void hideWindow() {
		super.hideWindow();
	}


	/**
	 * Search a character for toggle input.
	 * 
	 * @param prevChar
	 *            The character input previous
	 * @param toggleTable
	 *            Toggle table
	 * @param reverse
	 *            {@code false} if toggle direction is forward, {@code true} if
	 *            toggle direction is backward
	 * @return A character ({@code null} if no character is found)
	 */
	protected String searchToggleCharacter(String prevChar,
			String[] toggleTable, boolean reverse) {
		for (int i = 0; i < toggleTable.length; i++) {
			if (prevChar.equals(toggleTable[i])) {
				if (reverse) {
					i--;
					if (i < 0) {
						return toggleTable[toggleTable.length - 1];
					} else {
						return toggleTable[i];
					}
				} else {
					i++;
					if (i == toggleTable.length) {
						return toggleTable[0];
					} else {
						return toggleTable[i];
					}
				}
			}
		}
		return null;
	}

}
