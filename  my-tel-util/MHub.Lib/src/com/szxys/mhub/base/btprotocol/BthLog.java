package com.szxys.mhub.base.btprotocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.szxys.mhub.common.Logcat;

import android.util.Log;

public class BthLog
{
	public static void config()
	{
		MyBthLogAdaptor.config();
	}

	public static void v(String tag, String msg)
	{
		MyBthLogAdaptor.v(tag, msg);
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		MyBthLogAdaptor.v(tag, msg, tr);
	}

	public static void d(String tag, String msg)
	{
		MyBthLogAdaptor.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		MyBthLogAdaptor.d(tag, msg, tr);
	}

	public static void i(String tag, String msg)
	{
		MyBthLogAdaptor.i(tag, msg);
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		MyBthLogAdaptor.i(tag, msg, tr);
	}

	public static void w(String tag, String msg)
	{
		MyBthLogAdaptor.w(tag, msg);
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		MyBthLogAdaptor.w(tag, msg, tr);
	}

	public static void w(String tag, Throwable tr)
	{
		MyBthLogAdaptor.w(tag, tr);
	}

	public static void e(String tag, String msg)
	{
		MyBthLogAdaptor.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		MyBthLogAdaptor.e(tag, msg, tr);
	}

	private BthLog()
	{
	}
}

class MyBthLog
{
	public static void config()
	{
		InputStream ips = BthLog.class.getResourceAsStream("bthlog.properties");
		Properties props = new Properties();
		try
		{
			props.load(ips);
			fIsD = Integer.parseInt(props.getProperty("D"));
			fIsV = Integer.parseInt(props.getProperty("V"));
			fIsI = Integer.parseInt(props.getProperty("I"));
			fIsW = Integer.parseInt(props.getProperty("W"));
			fIsE = Integer.parseInt(props.getProperty("E"));
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	public static void v(String tag, String msg)
	{
		if ( fIsV > 0 )
			Log.v(tag, msg);
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		if ( fIsV > 0 )
			Log.v(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void d(String tag, String msg)
	{
		if ( fIsD > 0 )
			Log.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		if ( fIsD > 0 )
			Log.d(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void i(String tag, String msg)
	{
		if ( fIsI > 0 )
			Log.i(tag, msg);
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		if ( fIsI > 0 )
			Log.i(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void w(String tag, String msg)
	{
		if ( fIsW > 0 )
			Log.w(tag, msg);
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		if ( fIsW > 0 )
			Log.w(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void w(String tag, Throwable tr)
	{
		if ( fIsW > 0 )
			Log.w(tag, getStackTraceString(tr));
	}

	public static void e(String tag, String msg)
	{
		if ( fIsE > 0 )
			Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		if ( fIsE > 0 )
			Log.e(tag, msg, tr);
	}

	public static String getStackTraceString(Throwable tr)
	{
		return Log.getStackTraceString(tr);
	}

	protected MyBthLog()
	{
	}

	private static int fIsD = 1;
	private static int fIsV = 1;
	private static int fIsI = 1;
	private static int fIsW = 1;
	private static int fIsE = 1;
}

class MyBthLogAdaptor
{
	public static void config()
	{
	}

	public static void v(String tag, String msg)
	{
		Logcat.v(tag, msg);
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		Logcat.v(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void d(String tag, String msg)
	{
		Logcat.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		Logcat.d(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void i(String tag, String msg)
	{
		Logcat.i(tag, msg);
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		Logcat.i(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void w(String tag, String msg)
	{
		Logcat.w(tag, msg);
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		Logcat.w(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static void w(String tag, Throwable tr)
	{
		Logcat.w(tag, getStackTraceString(tr));
	}

	public static void e(String tag, String msg)
	{
		Logcat.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		Logcat.e(tag, msg + '\n' + getStackTraceString(tr));
	}

	public static String getStackTraceString(Throwable tr)
	{
		return Log.getStackTraceString(tr);
	}

	private MyBthLogAdaptor()
	{
	}
}