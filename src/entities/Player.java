package entities;

import java.io.Serializable;
import java.util.ArrayList;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.Game;
import game.MainGameLoop;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import particles.Particle;
import renderEngine.DisplayManager;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import toolbox.Maths;
import toolbox.Values;
import world.Terrain;

public class Player implements Serializable {
	private double sens;
	private Vector3f position;
	private Vector3f viewAngle;
	private Vector3f viewVector;
	private Matrix4f left;
	private Matrix4f right;
	private double mouseX, mouseY, pMouseX, pMouseY;
	private Vector3f speed;
	private int shotTimer;
	private GuiTexture crosshair;
	private int playerHeight;
	private Entity gun;
	private double roll;
	private boolean scoped, ground;
	private GuiTexture baseHUD, healthSquare, hurtSquare;
	private double pitchOffset;
	private long clID;
	private long playerID = -1;
	private double speedMult = 2;
	
	public Player() {
		clID = System.currentTimeMillis();
		sens = Values.PLAYER_SENS;
		position = new Vector3f((float)Math.random()* Terrain.SIZE, 10000, (float)Math.random()* Terrain.SIZE); 
		viewAngle =  new Vector3f(0, 7.8f, 0);
		viewVector = new Vector3f(0, 0, 0);
		left = new Matrix4f();
		right = new Matrix4f();
		speed = new Vector3f(0, 0, 0);
		RawModel awp = OBJLoader.loadObjModel("awp", Game.getLoader());
		ModelTexture awpGreen = new ModelTexture(Game.getLoader().loadTexture("awpGreen"));
		awpGreen.setReflectivity(1.5f);
		awpGreen.setShineDamper(256f);
		gun = new Entity(new TexturedModel(awp, awpGreen), new Vector3f(position), 0, 0, 0, 0.1f);
		Game.addObject(gun);
		crosshair = new GuiTexture(Game.getLoader().loadTexture("scope"), new Vector2f(0f, 0), new Vector2f(1f, 1f));
		baseHUD = new GuiTexture(Game.getLoader().loadTexture("baseHUD"), new Vector2f(-0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
		healthSquare = new GuiTexture(Game.getLoader().loadTexture("HealthSquare"), new Vector2f(-0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
		hurtSquare = new GuiTexture(Game.getLoader().loadTexture("hurtSquare"), new Vector2f(-0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
		playerHeight = Values.PLAYER_HEIGHT;
		
	}

	public void update(Terrain terrain) {
		flags(terrain);
		
		// Wireframe
		if(GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_Q) == 1) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
		look();
		
		walk(terrain);
		
		position.x += speed.x;
		position.y += speed.y;
		position.z += speed.z;
		
		viewVector.normalise();
		gun.setPosition(new Vector3f(position.x+(right.m03/4)+(viewVector.x/2), position.y-0.4f+(viewVector.y/3), position.z+(right.m23/4)+(viewVector.z/2)));
		
		//gun.setRotZ((float) Math.toDegrees(viewVector.y));
		gun.setRotY((float) Math.toDegrees(-viewAngle.x)+90);
		gun.setRotX((float) (mouseX - pMouseX)*80f);
		
		pitchOffset /= 1.5;
		
		pMouseX = mouseX;
		pMouseY = mouseY;
		
		shoot();
		
		if(scoped) {
			gun.setPosition(new Vector3f((viewVector.x)+position.x+(right.m03/4), (viewVector.y)+position.y-100, (viewVector.z)+position.z+(right.m23/4)));
		}
		
		StatusEffect.update();
		
		saveToInterface();
	}
	
	void saveToInterface() {
		Game.clientData.position = this.position;
		Game.clientData.viewVector = this.viewVector;
		
		for(Bullet b : Game.bullets)
			Game.clientData.bulletPositions.add(b.position);
	}
	
	void walk(Terrain terrain) {
		if (GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_W) == 1) {
			speed.x += viewVector.x*speedMult*MainGameLoop.delta;
			speed.z += viewVector.z*speedMult*MainGameLoop.delta;
		}
		if (GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_S) == 1) {
			speed.x -= viewVector.x*speedMult*MainGameLoop.delta;
			speed.z -= viewVector.z*speedMult*MainGameLoop.delta;
		}
		if (GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_A) == 1) {
			speed.x += left.m03*speedMult*MainGameLoop.delta;
			speed.z += left.m23*speedMult*MainGameLoop.delta;
		}
		if (GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_D) == 1) {
			speed.x += right.m03*speedMult*MainGameLoop.delta;
			speed.z += right.m23*speedMult*MainGameLoop.delta;
		}
		
		if (GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_SPACE) == 1 && ground) {
			position.y += 5*MainGameLoop.delta;
			speed.y = (float) (12*MainGameLoop.delta);
		}
		
		collisions(terrain);
		speed.x /= 1.2;
		speed.z /= 1.2;
		if(pitchOffset < 0.0001) {
			pitchOffset = 0;
		}
	}
	
	void collisions(Terrain terrain) {
		if(position.x > Terrain.SIZE-25) {
			position.x -= (position.x - Terrain.SIZE-15);
		}
		if(position.z > Terrain.SIZE-25) {
			position.z -= (position.z - Terrain.SIZE-15);
		}
		if(position.x < 25) {
			position.x -= (position.x - 15);
		}
		if(position.z < 25) {
			position.z -= (position.z - 15);
		}
		
		float terrainHeight = terrain.getHeight((int)position.x, (int)position.z);
		if(position.y < terrainHeight+playerHeight+4) {
			speed.y = 0;
			position.y = terrainHeight+playerHeight;
		} else {
			speed.y -= Values.UNIVERSAL_GRAVITY*MainGameLoop.delta;
		}
		
		try {
			for(Entity e : terrain.getProps()) {
				if(e.getModel().getRawModel().getVertexCount() < 200) { // Is a rock
					double distance = toolbox.Maths.distance2D(e.getPosition(), position);
					if(distance < 200 && Math.abs(position.y - playerHeight - e.getPosition().y) < 100) {
						// TODO unit circle stuff to find what to subtract
						Vector3f Vectorto = Maths.vectorTo(position, e.getPosition());
						position.x -= Vectorto.x/(150f-distance);
						position.z -= Vectorto.z/(150f-distance);
					}
				} else { // is not a rock(tree)
					double distance = toolbox.Maths.distance2D(e.getPosition(), position);
					if(distance < 150 && Math.abs(position.y - playerHeight - e.getPosition().y) < 1000) {
						Vector3f Vectorto = Maths.vectorTo(position, e.getPosition());
						position.x -= Vectorto.x/(100f-distance);
						position.z -= Vectorto.z/(100f-distance);
					}
				}
			}
		} catch(Exception e) {
			
		}
	}
	
	void look() {
		mouseX = DisplayManager.getxPos() / 500;
		mouseY = DisplayManager.getyPos() / 500;
		
		double distX = mouseX - pMouseX;
		double distY = mouseY - pMouseY;
		viewAngle.x += distX*sens;
		if(pitchOffset > 0) {
			viewAngle.y += (distY-((pitchOffset)-0.00525))*sens;
		} else {
			viewAngle.y += distY*sens;
		}
		if (viewAngle.y >= 7.8) {
			viewAngle.y = 7.8f;
		} else if (viewAngle.y < 4.6) {
			viewAngle.y = 4.6f;
		}

		viewVector = new Vector3f((float) Math.sin(viewAngle.x), (float) Math.tan(Math.PI - viewAngle.y),
				(float) -Math.cos(viewAngle.x));

		left.m03 = viewVector.x;
		left.m13 = viewVector.y;
		left.m23 = viewVector.z;
		
		right.m03 = viewVector.x;
		right.m13 = viewVector.y;
		right.m23 = viewVector.z;
		
		left.rotate((float) Math.toRadians(-90), new Vector3f(0, 1, 0));
		right.rotate((float) Math.toRadians(90), new Vector3f(0, 1, 0));
	}
	
	public void shoot() {
		if(GLFW.glfwGetMouseButton(DisplayManager.getWindow(), GLFW.GLFW_MOUSE_BUTTON_1) == 1 && shotTimer < 0 && scoped) {
			Game.bullets.add(new Bullet(new Vector3f(position.x, position.y-10, position.z), new Vector3f(viewVector.x * 500, viewVector.y * 500, viewVector.z * 500)));
			shotTimer = 40;
			pitchOffset += 0.105;
			StatusEffect.shoot();
		}
	}
	
	public void scope() {
		if (scoped) {
			Game.guiRenderer.render(crosshair);
			DisplayManager.FOV = 25;
			sens = Values.PLAYER_SENS*0.15;
		} else {
			DisplayManager.FOV = 100;
			sens = Values.PLAYER_SENS;
		}
	}
	
	void flags(Terrain terrain) {
		shotTimer--;
		if(position.y - Values.PLAYER_HEIGHT - terrain.getHeight((int)position.x, (int)position.z) < Values.PLAYER_HEIGHT/7) {
			ground = true;
		} else {
			ground = false;
		}
		if(GLFW.glfwGetMouseButton(DisplayManager.getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == 1) {
			scoped = true;
		} else scoped = false;
		
		if(GLFW.glfwGetKey(DisplayManager.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == 1 && !scoped && ground)
			speedMult = 2;
		else if(scoped && ground)
			speedMult = 0.25;
		else if(!ground)
			speedMult = 0.75;
		 else if(ground)speedMult = 1;
	}
	
	public void drawGui() {
		scope();
		
		//Game.guiRenderer.render(hurtSquare);
		//Game.guiRenderer.render(healthSquare);
		Game.guiRenderer.render(baseHUD);
	}
	
	public long getID() {
		return playerID;
	}
	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getAngles() {
		return viewAngle;
	}
	
	public long getclID() {
		return clID;
	}

	public void setID(long id) {
		playerID = id;
	}
}
