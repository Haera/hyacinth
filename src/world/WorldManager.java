package world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import game.Game;
import game.MainGameLoop;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class WorldManager implements Serializable {
	ArrayList<Terrain> world = new ArrayList<Terrain>();
	RawModel model;
	ModelTexture texture;
	TexturedModel texturedModel;
	Random random = new Random();
	int seed = random.nextInt(1000000000);
	Loader loader;
	public WorldManager(Loader loader) {
		this.loader = loader;
		texture = new ModelTexture(Game.getLoader().loadTexture("floor"));
		texture.setReflectivity(0.2f);
		texture.setShineDamper(10f);
		texturedModel = new TexturedModel(model, texture);
		world.add(new Terrain(0, 0, loader, texture, seed));
	}
	
	public void update() {}
	
	public void render() {
		Game.getRenderer().processTerrain(world.get(0));
		try {
			for(Entity e : world.get(0).getProps()) {
				Game.getRenderer().processEntity(e);
			}
		} catch(Exception e) {}
	}
	
	ArrayList<Terrain> getAdjacentChunks(Vector3f position) {
		ArrayList<Terrain> adjacents = new ArrayList<Terrain>();
		for(Terrain c : world) {
			if(Math.abs(c.getX() - position.x) <= 8000 && Math.abs(c.getZ() - position.z) <= 8000)  {
				adjacents.add(c);
			}
		}
		return adjacents;
	}
	public Terrain chunkAtPos() {
		return world.get(0);
	}
	
	public void setSeed(int seed) {
		this.seed = seed;
		world.clear();
		world.add(new Terrain(0, 0, loader, texture, seed));
	}

	public int getSeed() {
		return seed;
	}
}