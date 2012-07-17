package com.custom.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.custom.media.MediaView;
import com.custom.mypad.MyPadView;
import com.custom.update.Update;
import com.custom.userinfo.UserInfoView;
import com.custom.utils.Constant;

public class BottomPannel extends JPanel {
	private MyPadView myPadView = null;
	
	
	JPanel leftPanel,rightPanel;
	public BottomPannel(final LeftPanel leftPanel,final RightPanel rightPanel) {
		super();
		this.leftPanel = leftPanel;
		this.rightPanel = rightPanel;
		
		
		myPadView = new MyPadView(leftPanel,rightPanel); 
		

		ImageIcon MYPAD = new ImageIcon(Main.imgPath+"MYPAD.png");
		ImageIcon Uploadpic = new ImageIcon(Main.imgPath+"Uploadpic.png");
		ImageIcon Uploadbook = new ImageIcon(Main.imgPath+"Uploadbook.png");
		ImageIcon update = new ImageIcon(Main.imgPath+"update.png");
		ImageIcon download = new ImageIcon(Main.imgPath+"download.png");
		ImageIcon passwd = new ImageIcon(Main.imgPath+"passwd.png");
		
		
//		LeftPanel chLeftPanel = new LeftPanel();
//		
//		chLeftPanel.setLayout(null); 
//		chLeftPanel.setPreferredSize(new   Dimension(420,100)); 
//		chLeftPanel.setBounds(0, 0, 125, 235);
//		//chLeftPanel.setBorder(BorderFactory.createLineBorder(Color.black)); 
//		this.leftPanel.add(chLeftPanel);
//		
//		
//		Update chRightPanel = new Update();
//		
//		chRightPanel.setLayout(null); 
//		chRightPanel.setPreferredSize(new   Dimension(420,100)); 
//		chRightPanel.setBounds(0, 0, 125, 235);
//		//chRightPanel.setBorder(BorderFactory.createLineBorder(Color.black)); 
//		this.rightPanel.add(chRightPanel);	
		
		JButton MYPADBtn = new MyButton(MYPAD);
		MYPADBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				//super.actionPerformed(e);
				myPadView = new MyPadView(leftPanel,rightPanel); 
			}
		});
		
		
		JButton UploadpicBtn = new MyButton(Uploadpic);
		UploadpicBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if(Constant.getWinSdPath(0)!=null){
					new MediaView(leftPanel,rightPanel,0);//多媒体
				}
				
			}
		});
		
		
		JButton UploadbookBtn = new MyButton(Uploadbook);
		UploadbookBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if(Constant.getWinSdPath(0)!=null){
					new MediaView(leftPanel,rightPanel,1);//电子书
				}
			}
		});
		
		
		JButton updateBtn = new MyButton(update);
		updateBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if(Constant.getWinSdPath(0)!=null){
					new Update(leftPanel,rightPanel);
				}
				
			}
		});
		
		
		JButton downloadBtn = new MyButton(download);
		downloadBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
			}
		});
		
		
		JButton passwdBtn = new MyButton(passwd);
		passwdBtn.addActionListener(new MyButtonOnclickListener() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				if(Constant.getWinSdPath(0)!=null){
					new UserInfoView(leftPanel,rightPanel);
				}
			}
		});
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 6;
		constraints.weighty = 1;
		add(MYPADBtn, constraints, 1, 1, 1, 1);
		add(UploadpicBtn, constraints, 2, 1, 1, 1);
		add(UploadbookBtn, constraints, 3, 1, 1, 1);

		add(updateBtn, constraints, 4, 1, 1, 1);
		add(downloadBtn, constraints, 5, 1, 1, 1);

		add(passwdBtn, constraints, 6, 1, 1, 1);

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
			setBounds(xIndex, 0, icon.getIconWidth(), icon.getIconHeight());
		    setBackground(new Color(0,0,0,0));
		    setBorder(null);
		    xIndex+=(icon.getIconWidth()-4);

		}
		
		
	}
	
	
	public class MyButtonOnclickListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(Constant.getWinSdPath(0)==null){
				Main.createDialog("PAD已经断开");
				return;
			}else{
				
			}
		}
		
	}
	

	
	
}
