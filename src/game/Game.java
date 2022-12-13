package game;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import networking.Client;
import networking.ClientData;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleTexture;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import skybox.SkyboxRenderer;
import textures.ModelTexture;
import toolbox.Line;
import toolbox.Maths;
import toolbox.Values;
import world.Terrain;
import world.WorldManager;

public class Game {
	public static boolean running = false;
	static double oldTime;
	static Loader loader = new Loader();
	public static ArrayList<GuiTexture> guis = new ArrayList<GuiTexture>();
	public static GuiRenderer guiRenderer;
	public static Player player;
	static MasterRenderer renderer;
	static List<Entity> objects = new ArrayList<Entity>();
	public static WorldManager world;
	static ModelTexture whiteTexture;
	static ModelTexture awpGreen;
	public static TexturedModel texturedLuigi1p;
	public static TexturedModel texturedLuigi3p;
	public static TexturedModel texturedHorse;
	static boolean multiplayer;
	public static ClientData clientData = new ClientData();
	public static ArrayList<Particle> dust = new ArrayList<Particle>();
	static Light sun;
	public static CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<Bullet>();
	
	static int serverPort;
	static InetAddress serverIP;
	
	static Client c;
	
	static void init(String port, String IP) {
		DisplayManager.createDisplay();
		Line.init(loader);
		multiplayer = false;
		world = new WorldManager(loader);
		RawModel luigi3p = OBJLoader.loadObjModel("luigicide", loader);
		RawModel luigi1p = OBJLoader.loadObjModel("luigicide", loader);
		RawModel AWP = OBJLoader.loadObjModel("awp", loader);
		whiteTexture = new ModelTexture(loader.loadTexture("white"));
		awpGreen = new ModelTexture(Game.getLoader().loadTexture("awpGreen"));
		awpGreen.setReflectivity(1.5f);
		awpGreen.setShineDamper(256f);
		texturedLuigi3p = new TexturedModel(luigi3p, new ModelTexture(loader.loadTexture("luigicide")));
		texturedLuigi1p = new TexturedModel(luigi3p, new ModelTexture(loader.loadTexture("luigicide")));
		TexturedModel texturedAWP = new TexturedModel(AWP, whiteTexture);
		serverPort = Integer.parseInt(port);
		sun = new Light(new Vector3f(4000, 1000, 24000), new Vector3f(0.6f, 0.3f, 0.6f));
		try {
			serverIP = InetAddress.getByName(IP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if(!serverIP.toString().equals(new String("/0.0.0.1"))) {
			multiplayer = true;
		} else {
			multiplayer = false;
		}
		
		if(multiplayer) {
			c = new Client(serverIP, serverPort);
		}
		
		renderer = new MasterRenderer();
		
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		PostProcessing.init(loader);
		
		guiRenderer = new GuiRenderer(loader);
		
		whiteTexture.setShineDamper(256f);
		whiteTexture.setReflectivity(5f);
		
		ParticleTexture part = new ParticleTexture(Game.getLoader().loadTexture("cyan"), 1);
		for(int i = 0; i < 20000; i++) {
			float x = (float)Math.random()*Terrain.SIZE;
			float z = (float)Math.random()*Terrain.SIZE;
			dust.add(new Particle(part, new Vector3f(x, (float) (Math.random()*2000), z), new Vector3f((float)Math.random()/3, (float)Math.random()*-0.3f, (float)Math.random()/3), 0.02f, Integer.MAX_VALUE, 0, 10));
		}
		
		if(multiplayer) {
			c.start();
		}

		player = new Player();
		
	}
	static void update() {
		running = true;
		player.update(world.chunkAtPos());
		world.update();
		ParticleMaster.update();
		
		if(multiplayer) {
			c.setUpdate(true);
		}
		if(multiplayer) {
			for(Vector3f v : clientData.bulletPositions)
				bullets.add(new Bullet(v, new Vector3f(0, 0, 0)));
			
			for(int i = 0; i < c.getPlayers().size(); i++) {
				if(c.getPlayers().get(i) == null) break;
				ClientData p = c.getPlayers().get(i);
				bullets.clear();
				renderer.processEntity(new Entity(texturedLuigi3p, new Vector3f(p.position.x, p.position.y-Values.PLAYER_HEIGHT, p.position.z), 0, (float) Math.toDegrees(p.viewVector.x-Math.PI), 0, 5));
			}
		}
		if(multiplayer) {
			c.setUpdate(false);
		}
		
		for(Bullet b : bullets) {
			b.update();
		}
		
		for(Entity e : objects) {
			renderer.processEntity(e);
		}
		for(int i = 0; i < dust.size(); i++) {
			Particle p = dust.get(i);
			if(p.getPosition().getY() < -100) {
				p.getPosition().y = 2000;
				p.getVelocity().y = -1/3;
			}
			if(p.getPosition().x < 0) {
				p.getPosition().x = Terrain.SIZE;
			}
			if(p.getPosition().x > Terrain.SIZE) {
				p.getPosition().x = 0;
			}
			if(p.getPosition().z < 0) {
				p.getPosition().z = Terrain.SIZE;
			}
			if(p.getPosition().z > Terrain.SIZE) {
				p.getPosition().z = 0;
			}
		}
	}
	static void render() {
		world.render();
		
		renderer.render(sun, player);
		
		guiRenderer.render(guis);
		player.drawGui();
		
		glfwSwapBuffers(DisplayManager.getWindow());
		DisplayManager.updateDisplay();
	}
	static void close() {
		PostProcessing.cleanUp();
		ParticleMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		if(multiplayer) {
			c.close();
		}
		
		DisplayManager.closeDisplay();
	}
	public static ArrayList<GuiTexture> getGuis() {
		return guis;
	}
	public static Loader getLoader() {
		return loader;
	}
	public static Player getPlayer() {
		return player;
	}
	public static MasterRenderer getRenderer() {
		return renderer;
	}
	public static void addObject(Entity e) {
		objects.add(e);
	}
}
