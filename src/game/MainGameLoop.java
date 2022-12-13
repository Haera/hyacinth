package game;

import static org.lwjgl.glfw.GLFW.*;
import renderEngine.DisplayManager;

public class MainGameLoop implements Runnable{
	String port;
	String IP;
	public static double delta;
	long lastLoopTime = System.nanoTime();
	long lastFpsTime = 0;
	long fps;
	final long OPTIMAL_TIME = 1000000000 / DisplayManager.TARGET_FRAMES;
	public MainGameLoop(String ip, String port, int windowX, int windowY, String playerName){
		this.IP = ip;
		this.port = port;
		DisplayManager.windowX = windowX;
		DisplayManager.windowY = windowY;
		Game.clientData.playerName = playerName;
	}
	public void run() {
		Game.init(port, IP);
		
		while (!glfwWindowShouldClose(DisplayManager.getWindow())) {
			long now = System.nanoTime();
		    long updateLength = now - lastLoopTime;
		    lastLoopTime = now;
		    delta = updateLength / ((double)OPTIMAL_TIME);
		    lastFpsTime += updateLength;
		    fps++;
		    
		    if (lastFpsTime >= 1000000000) {
		         //System.out.println("FPS: "+fps);
		         lastFpsTime = 0;
		         fps = 0;
		    }
			Game.update();
			
			Game.render();
			
            try {
            	Thread.sleep((lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000);
            } catch(Exception e) {}
		}
		Game.close();
	}
}
