import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Main extends JFrame{
	static final int WIDTH = 400;
	static final int HEIGHT = 300;
	ImageIcon img;
	public static String imgPath;
	public Main() {
		super("资源管理");
		imgPath = System.getProperty("user.dir")+"\\img\\";
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);

		img = new ImageIcon(imgPath+"Backgroud.png");
		JPanel panel = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(img.getImage(), 0, 0, null);
				super.paintComponent(g);
			}
		};
		
        panel.setOpaque(false);
        setContentPane(panel);
        try {
            //jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		setVisible(true);
	}

	
	public static void main(String[] args){
		new Main();
	}
}
