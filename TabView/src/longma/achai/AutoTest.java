package longma.achai;

import android.os.RemoteException;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class AutoTest {

	public static void main(String args[]) throws Exception {
		String[] mArgs = args;
		try {
			String opt = mArgs[0];
			if (opt.equals("touch")) {
				float x = Float.valueOf(mArgs[1]);
				float y = Float.valueOf(mArgs[2]);
				MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x,
						y, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x,
						y, 0);
				sendPointerSync(e);
			}

			else if (opt.equals("move")) {
				float x = Float.valueOf(mArgs[1]);
				float y = Float.valueOf(mArgs[2]);
				float x2 = Float.valueOf(mArgs[3]);
				float y2 = Float.valueOf(mArgs[4]);
				MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x,
						y, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x,
						y, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x,
						y, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
						x2, y2, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE,
						x2, y2, 0);
				sendPointerSync(e);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x2,
						y2, 0);
				sendPointerSync(e);
			}

			else if (opt.equals("key")) {
				int keycode = Integer.valueOf(mArgs[1]);
				KeyEvent k = new KeyEvent(KeyEvent.ACTION_DOWN, keycode);
				sendKeySync(k);
				k = new KeyEvent(KeyEvent.ACTION_UP, keycode);
				sendKeySync(k);
			} else if (opt.equals("wait")) {
				int millsecond = Integer.valueOf(mArgs[1]);
				Thread.sleep(millsecond);
			} else if (opt.equals("keypress")) {
				int keycode = Integer.valueOf(mArgs[1]);
				int millsecond = Integer.valueOf(mArgs[2]);
				KeyEvent k = new KeyEvent(KeyEvent.ACTION_DOWN, keycode);
				sendKeySync(k);
				Thread.sleep(millsecond);
				k = new KeyEvent(KeyEvent.ACTION_UP, keycode);
				sendKeySync(k);
			} else if (opt.equals("touchpress")) {
				float x = Float.valueOf(mArgs[1]);
				float y = Float.valueOf(mArgs[2]);
				int millsecond = Integer.valueOf(mArgs[3]);
				MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x,
						y, 0);
				sendPointerSync(e);
				Thread.sleep(millsecond);
				e = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x,
						y, 0);
				sendPointerSync(e);
			} else
				System.err.println("** Error: Unknown option: " + opt);
		} catch (RuntimeException ex) {
		}
		Thread.sleep(2000);
	}

	private static void sendPointerSync(MotionEvent event) {
		try {
//			(IWindowManager.Stub.asInterface(ServiceManager
//					.getService("window"))).injectPointerEvent(event, true);
		} catch (Exception e) {
		}
	}

	private static void sendKeySync(KeyEvent event) {
		try {
//			(IWindowManager.Stub.asInterface(ServiceManager
//					.getService("window"))).injectKeyEvent(event, true);
		} catch (Exception e) {
		}
	}
}
