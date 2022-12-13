package loader;

import java.awt.Color;

public interface Values {
	public final int SCREEN_WIDTH = 640;
	public final int SCREEN_HEIGHT = 480;
	public final int TARGET_FRAMERATE = 60;
	
	public final int DOT_COUNT = 100;
	public final float DOT_SIZE_SCALAR = 2.92f;
	public final double DOT_SPEED_SCALAR = 0.83;
	
	public final Color BACKGROUND_COLOR = new Color(50, 85, 105); 
	public final Color DOT_COLOR = new Color(0, 160, 190);
	public final double MIN_DOT_DISTANCE = 60; 
	
	public final float LOGO_X = 199;
	public final float LOGO_Y = 40;
	
	// Play Button
	public final float PLAY_BUTTON_X = -10;
	public final float PLAY_BUTTON_Y = 145;
	public final float PLAY_BUTTON_WIDTH = 362;
	public final float PLAY_BUTTON_HEIGHT = 82;
	
	// Debug Button
	public final float DEBUG_BUTTON_X = 0;
	public final float DEBUG_BUTTON_Y = 345;
	public final float DEBUG_BUTTON_WIDTH = 82;
	public final float DEBUG_BUTTON_HEIGHT = 82;
	
	// Server Button
	public final float SERVER_BUTTON_X = -10;
	public final float SERVER_BUTTON_Y = 245;
	public final float SERVER_BUTTON_WIDTH = 362;
	public final float SERVER_BUTTON_HEIGHT = 82;
	
	public final int ACTION_TIMER_COOLDOWN = 60;
	
}
