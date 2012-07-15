package com.custom.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DiskSpace {
	public static final String CRLF = System.getProperty("line.separator");
	public static final int OS_Unknown = 0;
	public static final int OS_WinNT = 1;
	public static final int OS_Win9x = 2;
	public static final int OS_Linux = 3;
	public static final int OS_Unix = 4;

	private static Logger _log = Logger.getLogger(DiskSpace.class);
	private static String _os = System.getProperty("os.name");

	protected static String os_exec(String[] cmds) {
		int ret = 0;
		Process porc = null;
		InputStream perr = null, pin = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader br = null;
		try {
//			for(int i=0; i< cmds.length;i++){
//				porc = Runtime.getRuntime().exec(cmds);//执行编译操作   
//			}
			porc = Runtime.getRuntime().exec("");   
			
			perr = porc.getErrorStream();
			pin = porc.getInputStream();
			// 获取屏幕输出显示
			br = new BufferedReader(new InputStreamReader(pin));
			while ((line = br.readLine()) != null) {
				sb.append(line).append(CRLF);
			}
			// 获取错误输出显示
			br = new BufferedReader(new InputStreamReader(perr));
			while ((line = br.readLine()) != null) {
				System.err.println("exec()E: " + line);
			}
			porc.waitFor(); // 等待编译完成
			ret = porc.exitValue(); // 检查javac错误代码
			if (ret != 0) {
				_log.warn("porc.exitValue() = " + ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(porc!=null)
				porc.destroy();
		}
		return sb.toString();
	}

	protected static int os_type() {
		// _log.debug("os.name = "+os); //Windows XP
		String os = _os.toUpperCase();
		if (os.startsWith("WINDOWS")) {
			if (os.endsWith("NT") || os.endsWith("2000") || os.endsWith("XP"))
				return OS_WinNT;
			else
				return OS_Win9x;
		} else if (os.indexOf("LINUX") > 0)
			return OS_Linux;
		else if (os.indexOf("UX") > 0)
			return OS_Unix;
		else
			return OS_Unknown;
	}

	protected static long os_freesize(String dirName) {
		String[] cmds = null;
		long freeSize = -1;
		int osType = os_type();
		switch (osType) {
		case OS_WinNT:
			cmds = new String[] { "cmd.exe", "/c", "dir", dirName };
			freeSize = os_freesize_win(os_exec(cmds));
			break;
		case OS_Win9x:
			cmds = new String[] { "command.exe", "/c", "dir", dirName };
			freeSize = os_freesize_win(os_exec(cmds));
			break;
		case OS_Linux:
		case OS_Unix:
			cmds = new String[] { "df", dirName };
			freeSize = os_freesize_unix(os_exec(cmds));
			break;
		default:
		}
		return freeSize;
	}

	protected static String[] os_split(String s) {
		// _log.debug("os_split() "+s);
		String[] ss = s.split(" "); // 空格分隔；
		List ssl = new ArrayList(16);
		for (int i = 0; i < ssl.size(); i++) {
			if (ss[i] == null)
				continue;
			ss[i] = ss[i].trim();
			if (ss[i].length() == 0)
				continue;
			ssl.add(ss[i]);
		}
		String[] ss2 = new String[ssl.size()];
		ssl.toArray(ss2);
		return ss2;
	}

	private static long os_freesize_unix(String s) {
		String lastLine = os_lastline(s); // 获取最后一航；
		if (lastLine == null) {
			_log.warn("(lastLine == null)");
			return -1;
		} else
			lastLine = lastLine.trim();
		// 格式：/dev/sda1 101086 12485 83382 14% /boot
		// lastLine = lastLine.replace('\t', ' ');
		String[] items = os_split(lastLine);
		_log.debug("os_freesize_unix() 目录:\t" + items[0]);
		_log.debug("os_freesize_unix() 总共:\t" + items[1]);
		_log.debug("os_freesize_unix() 已用:\t" + items[2]);
		_log.debug("os_freesize_unix() 可用:\t" + items[3]);
		_log.debug("os_freesize_unix() 可用%:\t" + items[4]);
		_log.debug("os_freesize_unix() 挂接:\t" + items[5]);
		if (items[3] == null) {
			_log.warn("(ss[3]==null)");
			return -1;
		}
		return Long.parseLong(items[3]) * 1024; // 按字节算
	}

	private static long os_freesize_win(String s) {
		String lastLine = os_lastline(s); // 获取最后一航；
		if (lastLine == null) {
			_log.warn("(lastLine == null)");
			return -1;
		} else
			lastLine = lastLine.trim().replaceAll(",", "");
		// 分析
		String items[] = os_split(lastLine); // 15 个目录 1,649,696,768 可用字节
		if (items.length < 4) {
			_log.warn("DIR result error: " + lastLine);
			return -1;
		}
		if (items[2] == null) {
			_log.warn("DIR result error: " + lastLine);
			return -1;
		}
		long bytes = Long.parseLong(items[2]); // 1,649,696,768
		return bytes;
	}

	protected static String os_lastline(String s) {
		// 获取多行输出的最后一行；
		BufferedReader br = new BufferedReader(new StringReader(s));
		String line = null, lastLine = null;
		try {
			while ((line = br.readLine()) != null)
				lastLine = line;
		} catch (Exception e) {
			_log.warn("parseFreeSpace4Win() " + e);
		}
		// _log.debug("os_lastline() = "+lastLine);
		return lastLine;
	}

	// private static String os_exec_df_mock() { //模拟df返回数据
	// StringBuffer sb = new StringBuffer();
	// sb.append("Filesystem     1K-块        已用     可用 已用% 挂载点");
	// sb.append(CRLF);
	// sb.append("/dev/sda1    101086     12485     83382  14% /boot");
	// sb.append(CRLF);
	// return sb.toString();
	// }
	public static long getFreeDiskSpace(String dirName) {
		// return os_freesize_unix(os_exec_df_mock()); //测试Linux
		return os_freesize(dirName);// 自动识别操作系统，自动处理
	}

	public static void main1(String[] args) throws IOException {
		args = new String[3];
		int x = 0;
		args[x++] = "C:";
//		args[x++] = "D:";
//		args[x++] = "E:";
		if (args.length == 0) {
			for (char c = 'A'; c <= 'Z'; c++) {
				String dirName = c + ":\\"; // C:\ C:
				_log.info(dirName + " " + getFreeDiskSpace(dirName));
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				_log.info(args[i] + " 剩余空间（B）:" + getFreeDiskSpace(args[i]));
			}
		}
	}
	
	 public static void main(String[] args) {
	        File[] roots = File.listRoots();
	        double constm = 1024 * 1024 * 1024 ;
	        double total = 0d;
	        for (File _file : roots) {
	            System.out.println(_file.getPath());
	            System.out.println("剩余空间 = " + doubleFormat(_file.getFreeSpace()/constm)+" G");
	            System.out.println("已使用空间 = " + doubleFormat(_file.getUsableSpace()/constm)+" G");
	            System.out.println(_file.getPath()+"盘总大小 = " + doubleFormat(_file.getTotalSpace()/constm)+" G");
	            System.out.println();
	            total+=_file.getTotalSpace();
	        }
	        System.out.println("你的硬盘总大小 = "+doubleFormat(total/constm));
	    }
	    
	    public static String doubleFormat(double d){   
	        DecimalFormat df = new DecimalFormat("0.##");   
	        return df.format(d);                   
	    }

}
