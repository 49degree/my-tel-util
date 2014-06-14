package com.skyeyes.storemonitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.content.Context;

import com.skyeyes.base.util.DateUtil;
import com.skyeyes.base.util.LoggerUtil;

/**
 * 全局异常处理类
 * 
 * @author linyun.zheng
 * 
 */
public class UEHandler implements Thread.UncaughtExceptionHandler {
	private Context context;

	LoggerUtil logger = new LoggerUtil(UEHandler.class);

	public UEHandler(Context app) {
		context = app;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();

		try {
			// 将crash log写入文件
			
			FileOutputStream fileOutputStream = new FileOutputStream(
					"/mnt/sdcard/skyeyes_store_crash_log.txt", true);
			fileOutputStream.write((DateUtil.getDefaultTimeStringFormat(DateUtil.TIME_FORMAT_YMDHMS)+"\n").getBytes());
			
			PrintStream printStream = new PrintStream(fileOutputStream);
			ex.printStackTrace(printStream);
			printStream.flush();
			printStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
