package loader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Setup extends JFrame{
	public static JFrame player = new JFrame("null");
	public static Color BGColor = new Color(0, 0, 0);
	static String gameName = "Hyacinth Loader";
	public static int mouseY = MouseInfo.getPointerInfo().getLocation().y;
    public static int mouseX = MouseInfo.getPointerInfo().getLocation().x; 
	static float width = Values.SCREEN_WIDTH;
	static float height = Values.SCREEN_HEIGHT;
	static int xPos = -7;
	static int yPos = 0;
	
	public Setup(){
	}
	public Setup(int x, int y){
		xPos = x;
		yPos = y;
	}
	public Setup(int x, int y, int screenWidth, int screenHeight){
		xPos = x;
		yPos = y;
		width = screenWidth;
		height = screenHeight;
	}
	public Setup(int x, int y, int screenWidth, int screenHeight, String gameN){
		xPos = x;
		yPos = y;
		width = screenWidth;
		height = screenHeight;
		gameName = gameN;
	}
	public Setup(int x, int y, String gameN){
		xPos = x;
		yPos = y;
		gameName = gameN;
	}
	
	public static void setWidth(float nwidth) {
		width = nwidth;
	}
	
	public static void setHeight(float nheight) {
		height = nheight;
	}
	
	public static void SetupThing() throws InterruptedException{
		player = new JFrame(gameName);
		player.setBackground(Values.BACKGROUND_COLOR);
		player.setSize((int)width, (int)height);
		player.setDefaultCloseOperation(EXIT_ON_CLOSE);
		player.setLocation(xPos, yPos);
		player.setVisible(true);
		JPanel panel = new Loader();
		player.pack();
		player.getContentPane().add(panel, BorderLayout.CENTER);
		player.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(player.isFocused())
				Loader.mousePressed = true;
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Loader.mousePressed = false;
			}
		});
	}
}
