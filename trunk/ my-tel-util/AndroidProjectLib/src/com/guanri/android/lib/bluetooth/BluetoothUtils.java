package com.guanri.android.lib.bluetooth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * @author 杨雪平
 *
 */
public class BluetoothUtils {
	private static Random channelRandom = new Random();

	/**
	 * 创建蓝牙连接socket
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static BluetoothSocket createRfcommSocket(Class btClass, BluetoothDevice btDevice)throws Exception {
		Method m = btClass.getMethod("createRfcommSocket", new Class[] {int.class});
		BluetoothSocket socket = (BluetoothSocket) m.invoke(btDevice, 1);
		return socket;
	}

	
	
	
	/**
	 * 取消绑定进程
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static boolean cancelBondProcess(Class btClass, BluetoothDevice btDevice)throws Exception {
		Method m = btClass.getMethod("cancelBondProcess");
		return (Boolean)m.invoke(btDevice);
	}
	
	/**
	 * 绑定指定的设备btDevice
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static boolean createBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}
	
	/**
	 * 移除已经绑定的设备btDevice
	 * @param btClass
	 * @param btDevice
	 * @return
	 * @throws Exception
	 */
	public static  boolean removeBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 与指定的设备进行配对
	 * @param btClass
	 * @param btDevice
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static  boolean setPin(Class btClass, BluetoothDevice btDevice,
			String str) throws Exception {
		try {			
			Method removeBondMethod = btClass.getDeclaredMethod("setPin",
					new Class[] { byte[].class });

			BluetoothUtils.removeBond(btClass, btDevice);
			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,str.getBytes("UTF8"));
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	/**
	 * 
	 * @param clsShow
	 */
	public static  void printAllInform(Class clsShow) {
		try {
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				Log.e("method name", hideMethod[i].getName() + ";and the i is:" + i);				
			
			}
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				Log.e("Field name", allFields[i].getName());
			}
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
