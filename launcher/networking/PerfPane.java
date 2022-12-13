package networking;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import javax.swing.JPanel;
import javax.swing.UIManager;
import loader.Setup;
import loader.Values;
import world.Terrain;

public class PerfPane extends JPanel {
	static Runtime runtime = Runtime.getRuntime();
	double usedMemory;
	double totalMemory;

	public PerfPane() {}

	public void render(Graphics g) {
		totalMemory = runtime.maxMemory()/1024;
		usedMemory = runtime.totalMemory()/1024;
		//System.out.println(usedMemory/totalMemory);
		drawBackground(g);
		
		g.setColor(new Color(0, 255, 0));
		g.fillRect(0, 0, 400, 30);
		
		g.setColor(new Color(255, 0, 0));
		g.fillRect(0, 0, (int)((usedMemory/totalMemory)*400), 30);
		
		g.setColor(new Color(150, 150, 150));
		g.fillRect(0, 60, 500, 500);
		
		g.setColor(new Color(255, 0, 0));
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(ClientData data : ServerThread.players) {
			if(data != null) {
				int x = (int)((data.position.getX()/Terrain.SIZE)*500f) - 5;
				int y = (int)((data.position.getZ()/Terrain.SIZE)*500f) + 55;
				g.fillOval(x, y, 10, 10);
				g.drawLine(x+5, y+5, (int)(x + data.viewVector.x*20)+5, (int)(y + data.viewVector.z*20)+5);
			}
		}
		try {
			Thread.sleep(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		repaint();
	}

	private void drawBackground(Graphics g) {
		g.setColor(Values.BACKGROUND_COLOR);
		g.fillRect(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render(g);
	}
}
