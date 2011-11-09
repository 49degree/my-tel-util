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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;



public class WaveAnalyse extends  JFrame{


	static final int WIDTH=1400;
    static final int HEIGHT=700;
    private int scringWidth=0;
    private int scringHeight=0;
    ImagePanel imagePanel = null;
    List<CureLineBean> mLineList = null;

	public WaveAnalyse(List<CureLineBean> lineList) {
		super("FSK分析");
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
		imagePanel = new ImagePanel(1000, lineList);
		JScrollPane pane = new JScrollPane(imagePanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		contentPane.add(pane, BorderLayout.CENTER);
		contentPane.add(new ButtonPanel(), BorderLayout.SOUTH);

	}
	

	
	public class ButtonPanel extends JPanel{
		public ButtonPanel() {
			super();
			GridBagLayout lay = new GridBagLayout();
			setLayout(lay);
			//this.setSize(800, 200);
			this.setPreferredSize(new Dimension(400,100));

			JButton beLarge = new JButton("水平方向放大");
			beLarge.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					xPxPerPoint = xPxPerPoint*1.5f;
					imagePanel.repaint();
				}
			});
			JButton beLess = new JButton("水平方向缩小");
			beLess.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					xPxPerPoint = xPxPerPoint*0.75f;
					imagePanel.repaint();
				}
			});
			
			
			JButton yBeLarge = new JButton("纵向放大");
			yBeLarge.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					yPointWeight = Math.round(yPointWeight*0.8f);
					imagePanel.repaint();
				}
			});
			JButton xBeLess = new JButton("纵向缩小");
			xBeLess.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					yPointWeight = Math.round(yPointWeight*1.2f);
					imagePanel.repaint();
				}
			});
			
			JButton moveLeft = new JButton("左移");
			moveLeft.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					xBeginIndex+=10/xPxPerPoint;
					imagePanel.repaint();
				}
			});
			JButton moveRight = new JButton("右移");
			moveRight.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(xBeginIndex>10){
						xBeginIndex-=10/xPxPerPoint;
					}else{
						xBeginIndex=0;
					}
					
					imagePanel.repaint();
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
	
	float xPxPerPoint = 1f;//曲线点之间的X轴的像素
	int xBeginIndex = 0;//
	int yPointWeight =10000;//Y轴方向每格表示的大小
	public class ImagePanel extends JPanel{

		
		List<CureLineBean> mLineList = null;
		
		public ImagePanel(int pointXLength,List<CureLineBean> lineList){
			super();
			this.mLineList = lineList;
			this.addMouseMotionListener(new MouseMotionListener(){
				private int startX,curX;
				
				public void mouseMoved(MouseEvent me) {
					startX = me.getPoint().x;
					//System.out.println("mouseMoved"+startX);
					   
				}

				public void mouseDragged(MouseEvent me) {
					curX = (int) me.getPoint().getX();
					int stepLength = (int)((curX-startX)/xPxPerPoint);
					if(xBeginIndex-stepLength>0){
						xBeginIndex = xBeginIndex-stepLength;
						
					}else{
						xBeginIndex = 0;
					}
					repaint();
					startX = curX;
					
				}
			});
			
			
			
		}
		
		int width = 1200;//控件宽
		int height = 400;//控件高
		
		int borderLength = 40;//边经
		int pointLength = 15;
		
		int yMaxWeight = 0;
		int xPointWeight = 0;
		int zeroIndexPx = 0;
		
		
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			this.setPreferredSize(new Dimension(width,height));
			
			width = this.getWidth();
			height = this.getHeight();
			zeroIndexPx = height/2;
			g.clearRect(0, 0, width, height);
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
}
