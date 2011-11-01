package com.guanri.fsk.view;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.guanri.fsk.pc.AudioOperator;



public class RecordPlayView extends  JFrame{


	static final int WIDTH=1400;
    static final int HEIGHT=800;
    private int scringWidth=0;
    private int scringHeight=0;

	public RecordPlayView() {
		super("录音&放音");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		scringWidth = screenSize.width;
		scringHeight = screenSize.height;

		Container contentPane = this.getContentPane();

		// 设置为BorderLayout布局方式
		BorderLayout lay = new BorderLayout();
		this.setLayout(lay);
		ImagePanel playImagePanel = new ImagePanel(1000);
		JScrollPane pane = new JScrollPane(playImagePanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ImagePanel recordImagePanel = new ImagePanel(1000);
		JScrollPane pane2 = new JScrollPane(recordImagePanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel playView = new JPanel();
		playView.setLayout(new  BorderLayout());
		playView.add(pane, BorderLayout.CENTER);
		playView.add(new ButtonPanel(playImagePanel), BorderLayout.SOUTH);
		

		
		JPanel recordView = new JPanel();
		recordView.setLayout(new   BorderLayout());
		recordView.add(pane2, BorderLayout.CENTER);
		recordView.add(new ButtonPanel(recordImagePanel), BorderLayout.SOUTH);

		
//		JScrollPane playViewScr = new JScrollPane(playView,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		JScrollPane recordViewScr = new JScrollPane(recordView,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		contentPane.add(playView, BorderLayout.NORTH);
		contentPane.add(recordView, BorderLayout.CENTER);
		
		AudioPanel audioPanel = new AudioPanel(playImagePanel,recordImagePanel);
		contentPane.add(audioPanel, BorderLayout.SOUTH);

	}
	

	
	public class ButtonPanel extends JPanel{
		ImagePanel mImagePanel = null;
		public ButtonPanel(ImagePanel imagePanel) {
			super();
			this.mImagePanel = imagePanel;
			GridBagLayout lay = new GridBagLayout();
			setLayout(lay);
			//this.setSize(800, 200);
			this.setPreferredSize(new Dimension(400,40));

			JButton beLarge = new JButton("水平方向放大");
			beLarge.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					mImagePanel.xPxPerPoint = mImagePanel.xPxPerPoint*1.5f;
					mImagePanel.repaint();
				}
			});
			JButton beLess = new JButton("水平方向缩小");
			beLess.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					mImagePanel.xPxPerPoint = mImagePanel.xPxPerPoint*0.75f;
					mImagePanel.repaint();
				}
			});
			
			
			JButton yBeLarge = new JButton("纵向放大");
			yBeLarge.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					mImagePanel.yPointWeight = Math.round(mImagePanel.yPointWeight*0.8f);
					mImagePanel.repaint();
				}
			});
			JButton xBeLess = new JButton("纵向缩小");
			xBeLess.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					mImagePanel.yPointWeight = Math.round(mImagePanel.yPointWeight*1.2f);
					mImagePanel.repaint();
				}
			});
			
			JButton moveLeft = new JButton("左移");
			moveLeft.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					mImagePanel.xBeginIndex+=10;
					mImagePanel.repaint();
				}
			});
			JButton moveRight = new JButton("右移");
			moveRight.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(mImagePanel.xBeginIndex>10){
						mImagePanel.xBeginIndex-=10;
					}else{
						mImagePanel.xBeginIndex=0;
					}
					
					mImagePanel.repaint();
				}
			});			
			
			
			JLabel lb = new JLabel("文件：");
			JFileChooser fc=new JFileChooser();
			fc.setDialogTitle("Open class File");
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 6;
			constraints.weighty = 4;
			add(yBeLarge, constraints, 1, 1, 1, 1);
			add(xBeLess, constraints, 2, 1, 1, 1);
			add(beLarge, constraints, 3, 1, 1, 1);
			add(beLess, constraints, 4, 1, 1, 1);
			add(moveLeft, constraints, 5, 1, 1, 1);
			add(moveRight, constraints, 6, 1, 1, 1);
			
		}
		
	    public void add(Component c,GridBagConstraints constraints,int x,int y,int w,int h)
	    {
	        constraints.gridx=x;
	        constraints.gridy=y;
	        constraints.gridwidth=w;
	        constraints.gridheight=h;
	        add(c,constraints);
	    } 
	}
	

	public static class ImagePanel extends JPanel{

		float xPxPerPoint = 1f;//曲线点之间的X轴的像素
		int xBeginIndex = 0;//
		int yPointWeight =10000;//Y轴方向每格表示的大小
		
		int width = 1200;//控件宽
		int height = 250;//控件高
		
		int borderLength = 30;//边经
		int pointLength = 15;
		
		int yMaxWeight = 0;
		int xPointWeight = 0;
		int zeroIndexPx = 0;
		
		List<CureLineBean> mLineList = null;
		
		public void setCureLineBean(List<CureLineBean> lineList){
			this.mLineList = lineList;
		}
		public ImagePanel(int pointXLength){
			super();
			this.setPreferredSize(new Dimension(width,height));
			this.addMouseMotionListener(new MouseMotionListener(){
				private int startX,curX;
				
				public void mouseMoved(MouseEvent me) {
					startX = me.getPoint().x;
					//System.out.println("mouseMoved"+startX);
				}

				public void mouseDragged(MouseEvent me) {
					curX = (int) me.getPoint().getX();
					if(xBeginIndex-(curX-startX)>0){
						xBeginIndex = xBeginIndex-(curX-startX);
						
					}else{
						xBeginIndex = 0;
					}
					repaint();
					startX = curX;
					
				}
			});
			
			
			
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			this.setPreferredSize(new Dimension(width,height));
			width = this.getWidth();
			height = this.getHeight();
			zeroIndexPx = height/2;
			g.clearRect(0, 0, width, height);
			
			//System.out.println("width:"+width+"height:"+height+"yPointWeight:"+yPointWeight);
			
			this.setBackground(Color.BLUE);
			g.setColor(Color.BLACK);
			g.drawRect(borderLength, borderLength, width-2*borderLength, height-2*borderLength);
			g.setColor(Color.blue);
			
			g.drawLine(borderLength,zeroIndexPx,width-borderLength,zeroIndexPx);
			for(int i = 0;i<zeroIndexPx/pointLength-1;i++){
				g.drawLine(borderLength-10,zeroIndexPx-i*pointLength,borderLength,zeroIndexPx-i*pointLength);
				g.drawLine(borderLength-10,zeroIndexPx+i*pointLength,borderLength,zeroIndexPx+i*pointLength);
				if(i%3==0){//每3格绘制一个刻度
					g.drawString(String.valueOf(yPointWeight*i), borderLength-30,zeroIndexPx-i*pointLength);
					g.drawString(String.valueOf(-yPointWeight*i), borderLength-30,zeroIndexPx+i*pointLength);
				}
				//System.out.println("i:"+width+"height:"+height+"yPointWeight:"+yPointWeight);
				yMaxWeight = i*yPointWeight;
			}
			
			Graphics2D g2 = (Graphics2D)g;
			Stroke dash = new BasicStroke(0.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL,3.5f,new float[]{15,10,},1f);
			g2.setStroke(dash);
			g2.setColor(Color.GRAY);
			for(int i = 0;i<zeroIndexPx/pointLength-1;i++){
				if(i%3==0){//每3格绘制一个刻度
					g.drawLine(borderLength,zeroIndexPx-i*pointLength,width-borderLength,zeroIndexPx-i*pointLength);
					g.drawLine(borderLength,zeroIndexPx+i*pointLength,width-borderLength,zeroIndexPx+i*pointLength);
				}

			}
			for(int i=1;i*pointLength<width-borderLength*2;i++){
				g.drawLine(borderLength+i*pointLength,height-borderLength,borderLength+i*pointLength,height-borderLength+20);
				if(i%6==0){//每6格绘制一个刻度
					g.drawLine(borderLength+i*pointLength,borderLength,borderLength+i*pointLength,height-borderLength);
				}
			}
			
			dash = new BasicStroke();
			g2.setStroke(dash);
			if(mLineList!=null&&mLineList.size()>0){
				//System.out.println("yMaxWeight:"+yMaxWeight);
				for(CureLineBean cureLine:mLineList){
					int[] pointHeight = cureLine.getPointHeight();
					g.setColor(cureLine.lineColor);
					if(xBeginIndex+(width-2*borderLength)/(2*xPxPerPoint)>pointHeight.length){//最少留半页的数据进行显示
						xBeginIndex=(int)(pointHeight.length-(width-2*borderLength)/(2*xPxPerPoint));
					}
					if(xBeginIndex<0){
						xBeginIndex = 0;
					}
					for(int i=0;xBeginIndex+i<pointHeight.length-1&&Math.round(borderLength+xPxPerPoint*(i+1))<width-borderLength;i++){
						
						int y1 = zeroIndexPx-(pointHeight[xBeginIndex+i]*zeroIndexPx/yMaxWeight);
						int y2 = zeroIndexPx-(pointHeight[xBeginIndex+i+1]*zeroIndexPx/yMaxWeight);
						
						g.drawLine(Math.round(borderLength+xPxPerPoint*i),y1,Math.round(borderLength+xPxPerPoint*(i+1)),y2);
					}

				}
			}
			
			
		}
	}
	

	public class AudioPanel extends JPanel{
		ImagePanel mPlayImagePanel=null, mRecordImagePanel = null;
		public AudioPanel(ImagePanel playImagePanel,final ImagePanel recordImagePanel) {
			super();
			mPlayImagePanel=playImagePanel;
			mRecordImagePanel = recordImagePanel;
			GridBagLayout lay = new GridBagLayout();
			setLayout(lay);
			//this.setSize(800, 200);
			this.setPreferredSize(new Dimension(400,80));
			JLabel sendmsgL = new JLabel("发送信息：");
			final JTextField sendmsgT= new JTextField(15);
			sendmsgT.setSize(50, 10);
			final JLabel recmsgL = new JLabel("接收的信息：");
			JTextField recmsgT= new JTextField(15);
			
			JLabel sendWavFile = new JLabel("发送WAV数据文件：");
			JTextField sendWav= new JTextField(15);
			
			
			JLabel recWavFile = new JLabel("接收的WAV数据文件：");
			JTextField recWav= new JTextField(15);
			
			final AudioOperator audioOperator = new AudioOperator(playImagePanel,recordImagePanel);
			
			final JButton begin = new JButton("开始");
			final JButton stop = new JButton("停止");
			stop.setEnabled(false);
			begin.setEnabled(true);
			begin.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					stop.setEnabled(true);
					begin.setEnabled(false);
					audioOperator.start(sendmsgT.getText().getBytes());
					
				}
			});
			stop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					stop.setEnabled(false);
					begin.setEnabled(true);
					audioOperator.stop();
					
				}
			});
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 6;
			constraints.weighty = 2;
			add(sendmsgL, constraints, 1, 1, 1, 1);
			add(sendmsgT, constraints, 2, 1, 1, 1);
			add(recmsgL, constraints, 3, 1, 1, 1);
			add(recmsgT, constraints, 4, 1, 1, 1);
			
			add(begin, constraints, 5, 1, 1, 1);
			add(stop, constraints, 6, 1, 1, 1);
			
			add(sendWavFile, constraints, 1, 2, 1, 1);
			add(sendWav, constraints, 2, 2, 1, 1);
			add(recWavFile, constraints, 3, 2, 1, 1);
			add(recWav, constraints, 4, 2, 1, 1);
			
		}
		
	    public void add(Component c,GridBagConstraints constraints,int x,int y,int w,int h)
	    {
	        constraints.gridx=x;
	        constraints.gridy=y;
	        constraints.gridwidth=w;
	        constraints.gridheight=h;
	        add(c,constraints);
	    } 
	}
	public static void main(String[] args)
	{
		RecordPlayView test = new RecordPlayView();
		test.setVisible(true);	
	}
}
