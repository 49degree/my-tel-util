package com.custom.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CheckFileUi extends JFrame {


	static final int WIDTH = 400;
	static final int HEIGHT = 300;
	private static int screenX = 400;
	private static int screenY = 400;

	public static void main(String[] args) {
		new CheckFileUi();
	}

	public CheckFileUi() {
		super("加密压缩文件");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		// 设置GUI为windows风格
		// try{
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		// }catch (Exception ex){
		//			
		// }

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		setLocation(screenX, screenY);// 定位

		Container contentPane = this.getContentPane();
		// 设置为BorderLayout布局方式
		BorderLayout lay = new BorderLayout();
		setLayout(lay);

//		ButtonPanel buttonPanel = new ButtonPanel();
		DecodeLogFilePanel buttonPanel = new DecodeLogFilePanel();
		contentPane.add(buttonPanel, BorderLayout.CENTER);
		setVisible(true);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				realeas();
				System.exit(0);
			}
		});
	}

	public void realeas() {
		//System.out.println("关闭了。。。。。。");
		if(zipTofile!=null){
			zipTofile.stopZipFile();
		}
		if(prossThread!=null){
			prossThread.stopBar();
		}
	}
	
//	public static final String ZIP_FILENAME = "C:\\ziptest.zip";// 需要解压缩的文件名
//	public static final String ZIP_DIR = "F:\\eclipse3.5\\CustomDemo2\\assets";// 需要压缩的文件夹
//	public static final String UN_ZIP_DIR = "C:\\unziptest";// 要解压的文件目录
	
	public static final String ZIP_FILENAME = "E:\\English";// 需要解压缩的文件名
	public static final String ZIP_DIR = "E:\\English";// 需要压缩的文件夹
	public static final String UN_ZIP_DIR = "E:\\unEnglish";// 要解压的文件目录
	
	
	
	public void createDialog(String msg){
		JDialog diolog = new JDialog(CheckFileUi.this,"提示");
		JLabel lable = new JLabel(msg);
		diolog.getContentPane().add(lable, BorderLayout.CENTER);
		diolog.setLocation(screenX+50, screenY+50);// 定位
		diolog.setSize(200, 100);
		diolog.setModal(true);
		diolog.show();
	}
	
	
	
	
	
	
	/**
	 * 解密日志
	 * @author Administrator
	 *
	 */
	DecodeLogFile decodeLogFile = null;
	public class DecodeLogFilePanel extends JPanel {
		public DecodeLogFilePanel() {
			super();
			GridBagLayout lay = new GridBagLayout();
			setLayout(lay);
			// this.setSize(800, 200);
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

			JLabel dirLable = new JLabel("解密文件：");
			final JTextField dirPath = new JTextField(15);
			JButton choiseBtn = new JButton("选择");
			choiseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle("选择解密文件");
					//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showOpenDialog(DecodeLogFilePanel.this) == JFileChooser.APPROVE_OPTION) {
						dirPath.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			JLabel packNameLable = new JLabel("包名：");
			final JTextField packNameText = new JTextField(15);
			JButton sureBtn = new JButton("解密");
			sureBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 判断路径是否为空
					if(dirPath.getText()==null||"".equals(dirPath.getText().trim())){
						createDialog("路径为空!");
						return;
					}
					
					final File rootFile = new File(dirPath.getText());
					if(!rootFile.exists()){
						createDialog("路径不存在!");
						return;
					}
					// 判断路径是否为空
					if(packNameText.getText()==null||"".equals(packNameText.getText().trim())){
						createDialog("包名为空!");
						return;
					}
					
					new Thread(){
						public void run(){
							try{
								boolean encode = true;
								decodeLogFile = new DecodeLogFile(rootFile.getParent(),rootFile.getName(),packNameText.getText().trim());
								decodeLogFile.decodeFile();
							}catch(Exception e){
								e.printStackTrace();
								createDialog(e.getMessage());
								
							}finally{
								if(prossThread!=null){
									//System.out.println("prossThread");
									prossThread.stopBar();
								}
								//System.out.println("prossThread111");
							}
						}
					}.start();
					
					prossThread = new ThreadDiag("加密资源","请稍候,正在进行加密...");
					//System.out.println("prossThrea22");
				}
			});

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 3;
			constraints.weighty = 3;
			add(dirLable, constraints, 1, 1, 1, 1); 
			add(dirPath, constraints, 2, 1, 1, 1);
			add(choiseBtn, constraints, 3, 1, 1, 1);
			
			add(packNameLable, constraints, 1, 2, 1, 1);
			add(packNameText, constraints, 2, 2, 1, 1);
			
			add(sureBtn, constraints, 2, 3, 1, 1);
			
			
			

		}

		public void add(Component c, GridBagConstraints constraints, int x,
				int y, int w, int h) {
			constraints.gridx = x;
			constraints.gridy = y;
			constraints.gridwidth = w;
			constraints.gridheight = h;
			add(c, constraints);
		}		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 压缩解压文件
	 */
	ZipToFile zipTofile = null;
	ThreadDiag prossThread = null; 
	public class ButtonPanel extends JPanel {
		public ButtonPanel() {
			super();
			GridBagLayout lay = new GridBagLayout();
			setLayout(lay);
			// this.setSize(800, 200);
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

			JLabel dirLable = new JLabel("加密目录：");
			final JTextField dirPath = new JTextField(15);
			JButton choiseBtn = new JButton("选择");
			choiseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle("选择资源目录");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 只能选择目录
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showOpenDialog(ButtonPanel.this) == JFileChooser.APPROVE_OPTION) {
						//System.out.println("getCurrentDirectory(): "
						//		+ chooser.getCurrentDirectory());
						//System.out.println("getSelectedFile() : "
						//		+ chooser.getSelectedFile());
						dirPath.setText(chooser.getSelectedFile().getAbsolutePath());
					} else {
						//System.out.println("No Selection ");
					}
				}
			});
			
			JButton sureBtn = new JButton("加密");
			sureBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 判断路径是否为空
					if(dirPath.getText()==null||"".equals(dirPath.getText().trim())){
						createDialog("路径为空!");
						return;
					}
					
					final File rootFile = new File(dirPath.getText());
					if(!rootFile.exists()){
						createDialog("路径不存在!");
						return;
					}
					
					new Thread(){
						public void run(){
							try{
								boolean encode = true;
								zipTofile = new ZipToFile();
								zipTofile.zipFile(rootFile.getAbsolutePath(),rootFile.getAbsolutePath()+File.separator+rootFile.getName(),encode);
							}catch(Exception e){
								e.printStackTrace();
								createDialog(e.getMessage());
								
							}finally{
								if(prossThread!=null){
									//System.out.println("prossThread");
									prossThread.stopBar();
								}
								//System.out.println("prossThread111");
							}
						}
					}.start();
					
					prossThread = new ThreadDiag("加密资源","请稍候,正在进行加密...");
					//System.out.println("prossThrea22");
				}
			});

			
			JLabel decodeLable = new JLabel("解密文件：");
			final JTextField decodePath = new JTextField(15);
			JButton decodeBtn = new JButton("选择");
			decodeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setDialogTitle("选择资源目录");
					chooser.setAcceptAllFileFilterUsed(false);
					if (chooser.showOpenDialog(ButtonPanel.this) == JFileChooser.APPROVE_OPTION) {
						//System.out.println("getCurrentDirectory(): "
						//		+ chooser.getCurrentDirectory());
						//System.out.println("getSelectedFile() : "
						//		+ chooser.getSelectedFile());
						decodePath.setText(chooser.getSelectedFile().getAbsolutePath());
					} else {
						//System.out.println("No Selection ");
					}
				}
			});
			
			JButton decodesureBtn = new JButton("解密");
			decodesureBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 判断路径是否为空
					if(decodePath.getText()==null||"".equals(decodePath.getText().trim())){
						createDialog("路径为空!");
						return;
					}
					
					final File rootFile = new File(decodePath.getText());
					if(!rootFile.exists()){
						createDialog("文件不存在!");
						return;
					}
					new Thread(){
						public void run(){
							try{
								boolean encode = true;
								zipTofile = new ZipToFile();
								String path = rootFile.getAbsolutePath();
								zipTofile.upZipFile(path,path.substring(0,path.lastIndexOf(".")),encode);
								//System.out.println("prossThread111");
							}catch(Exception e){
								e.printStackTrace();
								createDialog(e.getMessage());
							}finally{
								if(prossThread!=null){
									//System.out.println("prossThread");
									prossThread.stopBar();
								}
								//System.out.println("prossThread111");
							}
						}
					}.start();
					
					prossThread = new ThreadDiag("加密资源","请稍候,正在进行加密...");
					//System.out.println("prossThrea22");
				}
			});


			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 2;
			constraints.weighty = 4;
			add(dirLable, constraints, 1, 1, 1, 1); 
			add(dirPath, constraints, 2, 1, 1, 1);
			add(choiseBtn, constraints, 3, 1, 1, 1);
			add(sureBtn, constraints, 4, 1, 1, 1);
			
			add(decodeLable, constraints, 1, 2, 1, 1);
			add(decodePath, constraints, 2, 2, 1, 1);
			add(decodeBtn, constraints, 3, 2, 1, 1);
			add(decodesureBtn, constraints, 4, 2, 1, 1);
			
			

		}

		public void add(Component c, GridBagConstraints constraints, int x,
				int y, int w, int h) {
			constraints.gridx = x;
			constraints.gridy = y;
			constraints.gridwidth = w;
			constraints.gridheight = h;
			add(c, constraints);
		}
	}

	public class ThreadDiag extends JDialog {
		private JProgressBar proBar;// 进度条
		private int min = 0;// 进度条的起始值
		private int max = 10;// 进度条的最大值
		private TaskThread proThread;
		private String title;
		private String msg ;
		private boolean stop = false;

		public ThreadDiag(String title,String msg) {
			this.title = title;
			this.msg = msg;
			init();
		}
		
		public void stopBar(){
			
			//System.out.println("complete");
			this.stop = true;
		}
		/**
		 * 初始化对话框
		 */
		public void init() {
			//System.out.println("ThreadDiag11111111111111111");
			this.setTitle(title);
			this.setModal(true);
			proBar = new JProgressBar();
			proBar.setValue(0);
			proBar.setStringPainted(true);// 设置显示字符串
			this.add(proBar);
			proThread = new TaskThread();
			proThread.start();
			
			new Thread(){
				public void run(){
					setLocation(screenX+50, screenY+50);// 定位
					ThreadDiag.this.setSize(200, 100);
					ThreadDiag.this.setLocationRelativeTo(null);

					
					ThreadDiag.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					// 添加窗口关闭事件
					ThreadDiag.this.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							if(zipTofile!=null){
								zipTofile.stopZipFile();
							}
							dispose();
						}
					});
					ThreadDiag.this.setVisible(true);
				}
			}.start();
			
			//System.out.println("ThreadDiag222");

		}

		/**
		 * 进度条线程
		 * 
		 * @author 黄根华
		 */
		class TaskThread extends Thread {
			public void run() {
				//setSize(WIDTH-100, HEIGHT-100);

				
				
				int min = 0;
				proBar.setValue(min);
				proBar.setMinimum(min);
				proBar.setMaximum(max);
				proBar.setString(msg);

				Runnable runner = new Runnable() {
					public void run() {
						int value = proBar.getValue();
						value++;
						if(value==max){
							value = 0;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						proBar.setValue(value);
//						if (proBar.getValue() == max) {
//							dispose();
//						}
					}
				};
				while(!stop){
					try {
						SwingUtilities.invokeAndWait(runner);// 组件和线程同步进行
					} catch (InvocationTargetException e) {
						break;
					} catch (InterruptedException e) {
					}
				}
				dispose();
				proThread = null;
			}
		}

	}

}