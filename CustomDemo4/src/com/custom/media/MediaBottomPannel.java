package com.custom.media;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.custom.client.Main;
import com.custom.mypad.MyPadView;
import com.custom.utils.Constant;
import com.custom.utils.LoadResources;
import com.custom.utils.Logger;

public class MediaBottomPannel extends JPanel {
	private static final Logger logger = Logger.getLogger(MediaBottomPannel.class);
	private MediaView mediaView = null;
	
	
	public MediaBottomPannel(MediaView mMediaView) {
		super();
		this.mediaView = mMediaView;
		ImageIcon backguandPic = new ImageIcon(Main.imgPath+"btn2_p.png");
			
		
		JButton mediaBtn = new MyButton(backguandPic);
		mediaBtn.setText("视频");
		
		mediaBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e); 
				createChooser(0);
			}
		});
		
		JButton picBtn = new MyButton(backguandPic);
		picBtn.setText("图片");
		picBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				createChooser(1);
			}
		});
		
		JButton mp3Btn = new MyButton(backguandPic);
		mp3Btn.setText("音频");
		mp3Btn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e); 
				createChooser(2);
			}
		});
		
		
		JButton sendBtn = new MyButton(backguandPic);
		sendBtn.setText("上传");
		sendBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e); 
				mediaView.uploadFile();
			}
		});
		
		JButton clearBtn = new MyButton(backguandPic);
		clearBtn.setText("清除");
		clearBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e); 
				mediaView.clearFilePath();
			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 5;
		constraints.weighty = 1;
		add(mediaBtn, constraints, 1, 1, 1, 1);
		add(picBtn, constraints, 2, 1, 1, 1);
		add(mp3Btn, constraints, 3, 1, 1, 1);

		add(sendBtn, constraints, 4, 1, 1, 1);
		add(clearBtn, constraints, 5, 1, 1, 1);

	}

	public void add(Component c, GridBagConstraints constraints, int x, int y,
			int w, int h) {
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = w;
		constraints.gridheight = h;
		//add(c, constraints);
		this.add(c);
		//this.setBounds(x*c.getBounds().width, 0, c.getBounds().width, c.getBounds().height);
	}
	int xIndex = 0;
	
	public class MyButton extends JButton{
		public MyButton(ImageIcon icon){
			super();
			setIcon(icon);
			setBounds(xIndex, 0, 40, 25);
		    setBackground(new Color(0,0,0,0));
		    setBorder(null);
		    xIndex+=(45);
		    setHorizontalTextPosition(SwingConstants.CENTER);   
		    setVerticalTextPosition(SwingConstants.CENTER);  
		    
		    setForeground(Color.WHITE);

		}
		
		
	}
	
	
	public class MyButtonOnclickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println(e.getActionCommand());
			
			
			
		}
		
	}
	
	/**
	 * 0=视频；1=图片；2=MP3
	 * @param type
	 */
	public void createChooser(int type) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("选择资源目录");
		
		chooser.setAcceptAllFileFilterUsed(true);
		chooser.setMultiSelectionEnabled(true);
		if(type==0){
			chooser.setFileFilter(new MYFileFilter(Constant.mediaTypes));
		}else if(type==1){
			logger.debug(Arrays.toString(Constant.picTypes));
			chooser.setFileFilter(new MYFileFilter(Constant.picTypes));
		}else if(type==2){
			chooser.setFileFilter(new MYFileFilter(Constant.mp3Types));
		}
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] files = chooser.getSelectedFiles();
			for(File file:files)
				mediaView.addFilePath(file.getAbsolutePath().toString(),type);
		}
	}

	/**
	*自己定义文件选择过滤器，只要实现filefilter这个接口就可以了
	*这是个简单的实现，可以增加允许类型集合字段以更方便的进行使用
	*/
	private class MYFileFilter extends FileFilter{
		    String[] types = null;
		    public MYFileFilter(String[] types){
		    	this.types = types;
		    }
			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
                String filename = arg0.getName();
                if(filename==null){
                	return false;
                }
                for(String type:types){
                	
                    if(filename.toUpperCase().indexOf("."+type)!=-1
                    		||arg0.isDirectory()){
                    	return true;
                    }
                }
                return false;
			}
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
	}
	

}
