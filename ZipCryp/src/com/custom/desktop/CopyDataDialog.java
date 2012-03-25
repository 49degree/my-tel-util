package com.custom.desktop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * 备份数据对话框
 * 
 * @author 黄根华
 */
public class CopyDataDialog extends JDialog {

	private JProgressBar proBar;// 进度条

	private int min = 0;// 进度条的起始值

	private int max = 100;// 进度条的最大值

	private TaskThread proThread;

	private String title;

	public static void main(String[] args) {
		new CopyDataDialog("test");
	}

	public CopyDataDialog(String title) {
		this.title = title;
		init();
	}

	/**
	 * 初始化对话框
	 */
	public void init() {
		this.setTitle(title);
		this.setModal(true);
		proBar = new JProgressBar();
		proBar.setValue(0);
		proBar.setStringPainted(true);// 设置显示字符串
		this.add(proBar);
		proThread = new TaskThread();
		proThread.start();
		// this.pack();
		this.setSize(400, 50);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// 添加窗口关闭事件
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		this.setVisible(true);
	}

	/**
	 * 进度条线程
	 * 
	 * @author 黄根华
	 */
	class TaskThread extends Thread {

		public void run() {
			int min = 0;
			proBar.setValue(min);
			proBar.setMinimum(min);
			proBar.setMaximum(max);

			Runnable runner = new Runnable() {

				public void run() {
					int value = proBar.getValue();
					value++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					proBar.setValue(value);
					if (proBar.getValue() == max) {
						dispose();
					}
				}

			};

			for (int i = min; i < max; i++) {
				try {
					SwingUtilities.invokeAndWait(runner);// 组件和线程同步进行
				} catch (InvocationTargetException e) {
					break;
				} catch (InterruptedException e) {
				}

			}
			proThread = null;
		}
	}

}
