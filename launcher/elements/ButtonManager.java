package elements;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import debug.DebugPane;
import game.MainGameLoop;
import loader.Loader;
import loader.Values;
import networking.Client;
import play.PlayPane;
import serverPanel.ServerPane;

public class ButtonManager {
	private BufferedImage playButton;
	private BufferedImage debugButton;
	private BufferedImage serverButton;
	private boolean overPlay;
	private boolean overHost;
	private float buttonCurAlpha;
	
	private int actionTimer;
	
	public ButtonManager() {
		buttonCurAlpha = 0;
		actionTimer  = 0;
	}
	
	public void loadButtonImages() {
		try {
			playButton = ImageIO.read(new File("res/newPlay.png"));
			debugButton = ImageIO.read(new File("res/newDebug.png"));
			serverButton = ImageIO.read(new File("res/newServer.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void displayButtons(Graphics g) {
		Graphics2D g2d = ((Graphics2D) g);
		if(buttonCurAlpha < .7) {
			buttonCurAlpha += 0.01;
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,buttonCurAlpha));
		
		if(overPlay) g2d.translate(Values.PLAY_BUTTON_X+10, Values.PLAY_BUTTON_Y);
		else g2d.translate(Values.PLAY_BUTTON_X, Values.PLAY_BUTTON_Y);
		g2d.scale(Values.PLAY_BUTTON_WIDTH/playButton.getWidth(), Values.PLAY_BUTTON_HEIGHT/playButton.getHeight());
		g2d.drawImage(playButton, 0, 0, null);
		g2d.scale(1/g2d.getTransform().getScaleX(), 1/g2d.getTransform().getScaleY());
		if(overPlay) g2d.translate(-Values.PLAY_BUTTON_X-10, -Values.PLAY_BUTTON_Y);
		else g2d.translate(-Values.PLAY_BUTTON_X, -Values.PLAY_BUTTON_Y);
		
		g2d.translate(Values.DEBUG_BUTTON_X, Values.DEBUG_BUTTON_Y);
		g2d.scale(Values.DEBUG_BUTTON_WIDTH/debugButton.getWidth(), Values.DEBUG_BUTTON_HEIGHT/debugButton.getHeight());
		g2d.drawImage(debugButton, 0, 0, null);
		g2d.scale(1/g2d.getTransform().getScaleX(), 1/g2d.getTransform().getScaleY());
		g2d.translate(-Values.DEBUG_BUTTON_X, -Values.DEBUG_BUTTON_Y);
		
		if(overHost) g2d.translate(Values.SERVER_BUTTON_X+10, Values.SERVER_BUTTON_Y);
		else g2d.translate(Values.SERVER_BUTTON_X, Values.SERVER_BUTTON_Y);
		g2d.scale(Values.SERVER_BUTTON_WIDTH/serverButton.getWidth(), Values.SERVER_BUTTON_HEIGHT/serverButton.getHeight());
		g2d.drawImage(serverButton, 0, 0, null);
		g2d.scale(1/g2d.getTransform().getScaleX(), 1/g2d.getTransform().getScaleY());
		if(overHost) g2d.translate(-Values.SERVER_BUTTON_X-10, -Values.SERVER_BUTTON_Y);
		else g2d.translate(-Values.SERVER_BUTTON_X, -Values.SERVER_BUTTON_Y);
		
		// draw debug + server button when image is readied
	}

	public void checkInput(boolean mouseDown, int mouseX, int mouseY) {
		if(mouseX > Values.PLAY_BUTTON_X && mouseX < (Values.PLAY_BUTTON_X+Values.PLAY_BUTTON_WIDTH) && mouseY > Values.PLAY_BUTTON_Y && mouseY < (Values.PLAY_BUTTON_Y+Values.PLAY_BUTTON_HEIGHT)) {
			overPlay = true;
			if(actionTimer <= 0 && mouseDown) {
				System.out.println("Play Button Clicked!");
				Thread g = new Thread(new PlayPane());
				g.start();
				actionTimer = Values.ACTION_TIMER_COOLDOWN;
			}
		} else overPlay = false;
		if(actionTimer <= 0 && mouseDown && mouseX > Values.DEBUG_BUTTON_X && mouseX < (Values.DEBUG_BUTTON_X+Values.DEBUG_BUTTON_WIDTH) && mouseY > Values.DEBUG_BUTTON_Y && mouseY < (Values.DEBUG_BUTTON_Y+Values.DEBUG_BUTTON_HEIGHT)) {
			System.out.println("Debug Button Clicked!");
			Thread d = new Thread(new DebugPane());
			d.start();
			actionTimer = Values.ACTION_TIMER_COOLDOWN;
		}
		if(mouseX > Values.SERVER_BUTTON_X && mouseX < (Values.SERVER_BUTTON_X+Values.SERVER_BUTTON_WIDTH) && mouseY > Values.SERVER_BUTTON_Y && mouseY < (Values.SERVER_BUTTON_Y+Values.SERVER_BUTTON_HEIGHT)) {
			overHost = true;
			if(actionTimer <= 0 && mouseDown) {
				System.out.println("Server Button Clicked!");
				Thread s = new Thread(new ServerPane());
				s.start();
				actionTimer = Values.ACTION_TIMER_COOLDOWN;
			}
		} else overHost = false;
		
		if(actionTimer > 0) {
			actionTimer--;
		}
	}
	public float getAlpha() {
		return buttonCurAlpha;
	}
	
}
