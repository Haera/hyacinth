package dotArt;

import loader.Values;

public class Dot {
	private float x, y;
	private float size;
	private float xSpeed, ySpeed;
	private boolean visibility;
	
	public Dot() {
		visibility = true;
		x = (float) Math.random()*Values.SCREEN_WIDTH;
		y = (float) Math.random()*Values.SCREEN_HEIGHT;
		while(size < 1) {
			size = (float) Math.random() * Values.DOT_SIZE_SCALAR;
		}
		xSpeed = (float) (((Math.random() - 0.5) * Values.DOT_SPEED_SCALAR) / (size));
		ySpeed = (float) ((Math.random() - 0.5) * Values.DOT_SPEED_SCALAR / (size));
	}
	
	public void dotMove() {
		x += xSpeed;
		y += ySpeed;
		if(x + size >= Values.SCREEN_WIDTH) 
			xSpeed = -xSpeed;
		if(x <= 0)
			xSpeed = -xSpeed;
		if(y + size >=  Values.SCREEN_HEIGHT)
			ySpeed = -ySpeed;
		if(y <= 0)
			ySpeed = -ySpeed;
	}
	public float getSize() {
		return size;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}

	public void setVisibility(boolean v) {
		visibility = v;
	}
	public boolean getVisibility() {
		return visibility;
	}
	
	
}
