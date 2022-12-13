package loader;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import dotArt.Dot;
import elements.ButtonManager;
import game.Game;
import game.MainGameLoop;
import networking.Client;
import renderEngine.DisplayManager;

public class Loader extends JPanel {
	private static List<Image> icons;
	public static String playerName = "I Can't Read";
	private static ArrayList<Dot> dots;
	private static BufferedImage logo;
	private static ButtonManager buttons;
	private float curAlpha;
	public static boolean mousePressed;
	public static boolean starting;
	public static String ip;
	public static String port;
	public static float opacity = 0;
	public static int timer;
	public static int startTime;
	public static boolean shouldClose = false;
	AlphaComposite solid = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);

	public static void init() {
		icons = new ArrayList<Image>();
		try {
			icons.add(ImageIO.read(new File("res/Logo16.png")));
			icons.add(ImageIO.read(new File("res/Logo32.png")));
			icons.add(ImageIO.read(new File("res/Logo64.png")));
			icons.add(ImageIO.read(new File("res/Logo128.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Setup.player.setIconImages(icons);
		dots = new ArrayList<Dot>();
		try {
			logo = ImageIO.read(new File("res/titleText.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < Values.DOT_COUNT; i++) {
			dots.add(new Dot());
		}
		buttons = new ButtonManager();
		buttons.loadButtonImages();
	}

	public static void update() {
		timer++;
		buttons.checkInput(mousePressed, Setup.mouseX, Setup.mouseY);
		updateDots();
	}

	public void render(Graphics g) {
		drawBackground(g);
		drawDots(g);
		loadLogo(g);
		drawButtons(g);
		checkPlay(g);
	}
	
	public void checkPlay(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		AlphaComposite whiteRect = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
		g2d.setComposite(whiteRect);
		g2d.setColor(new Color(255, 255, 255));
		g2d.fillRect(0, 0, 1920, 1080);
		g2d.setComposite(solid);
		if(starting) {
			if(opacity < 1 - 0.005) {
				opacity += 0.005;
				startTime = timer;
			}
			if(opacity >= 1 - 0.01) {
				float percentage = (timer - startTime)/240f;
				if(Setup.player.getWidth() <= DisplayManager.WIDTH) {
					Setup.setWidth((DisplayManager.WIDTH*percentage*(DisplayManager.WIDTH / 942.06f))+Values.SCREEN_WIDTH);
				}
				if(Setup.player.getHeight() <= DisplayManager.HEIGHT) {
					Setup.setHeight((DisplayManager.HEIGHT*percentage)+Values.SCREEN_HEIGHT);
				}
				if(Setup.player.getHeight() >= DisplayManager.HEIGHT-10 && Setup.player.getWidth() >= DisplayManager.WIDTH-10) {
					Thread t = new Thread(new MainGameLoop(ip, port, Setup.xPos+8, Setup.yPos+25, playerName));
					t.start();
					starting = false;
					
					startTime = timer;
				}
			}
		} else if (Game.running){
			if(Setup.player.getWidth() >= Values.SCREEN_WIDTH) {
				Setup.setWidth(((float)Setup.player.getWidth()) - 5f);
			}
			if(Setup.player.getHeight() >= Values.SCREEN_HEIGHT) {
				Setup.setHeight(((float)Setup.player.getHeight()) - 5f);
			}
			if(opacity > 0.005 && Setup.player.getHeight() <= Values.SCREEN_HEIGHT && Setup.player.getWidth() <= Values.SCREEN_WIDTH) 
				opacity -= 0.005;
		}
	}
	
	public static void startGame(String ip, String port) {
		Loader.ip = ip;
		Loader.port = port;
		starting = true;
	}

	private void drawBackground(Graphics g) {
		g.setColor(Values.BACKGROUND_COLOR);
		g.fillRect(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height);
	}

	private static void updateDots() {
		for (Dot d : dots) {
			d.dotMove();
		}
	}

	private void drawDots(Graphics g) {
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
		);
		Graphics2D antialiased = (Graphics2D)g;
		antialiased.setColor(Values.DOT_COLOR);
		antialiased.setRenderingHints(rh);
		for (Dot d : dots) {
			if (d.getVisibility()) {
				antialiased.fillOval((int) d.getX(), (int) d.getY(), (int) d.getSize(), (int) d.getSize());
			}
		}
		dots.get(dots.size() - 1).setX(Setup.mouseX);
		dots.get(dots.size() - 1).setY(Setup.mouseY);
		dots.get(dots.size() - 1).setVisibility(false);
		for (int i = 0; i < dots.size(); i++) {
			for (int j = 0; j < dots.size(); j++) {
				float dotOneX = dots.get(i).getX() + dots.get(i).getSize() / 2;
				float dotOneY = dots.get(i).getY() + dots.get(i).getSize() / 2;
				float dotTwoX = dots.get(j).getX() + dots.get(j).getSize() / 2;
				float dotTwoY = dots.get(j).getY() + dots.get(j).getSize() / 2;
				double dotDistance = Math.hypot(dotOneX - dotTwoX, dotOneY - dotTwoY);
				if (dotDistance < Values.MIN_DOT_DISTANCE) {
					((Graphics2D) g).setColor(new Color(0, 160, 190));
					((Graphics2D) g).drawLine((int) dotOneX, (int) dotOneY, (int) dotTwoX, (int) dotTwoY);
				}
			}
		}
	}

	private void drawButtons(Graphics g) {
		if (curAlpha >= 0.7)
			buttons.displayButtons(g);
	}

	private void loadLogo(Graphics g) {
		AlphaComposite ac = null;
		if (curAlpha < 0.9) {
			curAlpha += .008;
			ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, curAlpha);
		}
		if (ac != null) {
			((Graphics2D) g).setComposite(ac);
		} else {
			((Graphics2D) g).setComposite(solid);
		}
		((Graphics2D)g).translate(Values.LOGO_X, Values.LOGO_Y);
		((Graphics2D)g).scale(0.25, 0.25);
		((Graphics2D)g).drawImage(logo, 0, 0, null);
		((Graphics2D)g).scale(4, 4);
		((Graphics2D)g).translate(-Values.LOGO_X, -Values.LOGO_Y);
		((Graphics2D)g).setComposite(solid);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Setup.SetupThing();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		init();
		while (!shouldClose) {
			if(!Setup.player.hasFocus()) {
				mousePressed = false;
			}
			Setup.player.setSize((int)Setup.width, (int)Setup.height);
			update();
			Setup.mouseX = (int) MouseInfo.getPointerInfo().getLocation().x - Setup.player.getX() - 8;
			Setup.mouseY = (int) MouseInfo.getPointerInfo().getLocation().y - Setup.player.getY() - 33;
			Setup.player.repaint();
			try {
				Thread.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render(g);
	}
}