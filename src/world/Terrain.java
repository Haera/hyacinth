package world;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import game.Game;
import game.MainGameLoop;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import toolbox.Maths;

public class Terrain {

	public static final float SIZE = 15000;
	private static ModelTexture bloo = new ModelTexture(Game.getLoader().loadTexture("floor"));
	private static ModelTexture bloo2 = new ModelTexture(Game.getLoader().loadTexture("floor"));
	private static final TexturedModel rocc = new TexturedModel(OBJLoader.loadObjModel("rocc", Game.getLoader()), bloo);
	private static final TexturedModel tree1 = new TexturedModel(OBJLoader.loadObjModel("tree1", Game.getLoader()), bloo2);
	
	private static ArrayList<Entity> props = new ArrayList<Entity>();
	
	private float x;
	private float z;
	private RawModel model;
	private ModelTexture texture;
	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, int seed) {
		bloo.setReflectivity(5);
		bloo.setShineDamper(500);
		bloo2.setReflectivity(3);
		bloo2.setShineDamper(5000);
		this.texture = texture;
		this.x = gridX;
		this.z = gridZ;
		this.model = generateTerrain(loader, seed);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public float getHeight(int worldX, int worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = (SIZE / (float) heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX< 0 || gridZ < 0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float solution;
		if (xCoord <= (1 - zCoord)) {
			solution = Maths.baryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			solution = Maths.baryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return solution;
	}

	private RawModel generateTerrain(Loader loader, int seed) {
		
		HeightsGenerator generator = new HeightsGenerator(seed);

		int VERTEX_COUNT = 256;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(j, i, generator);
				heights[j][i] = height;
				
				double propRandom = Math.random();
				if(propRandom <= 0.004) {
					props.add(new Entity(rocc, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, 0, (float)Math.random()*10-15, (float)(80+(Math.random()*30))));
				} else if(propRandom <= 0.008) {
					props.add(new Entity(tree1, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, (float)Math.random()*10-5, (float)Math.random()*10-5, (float)(50+(Math.random()))));
				} else if(propRandom <= 0.012) {
					//Game.addObject(new Entity(elite1, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, 0, (float)Math.random()*10-15, (float)(80+(Math.random()*30))));
				}
				
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	public void generateTerrain(Loader loader, int seed, float[] vertices, float[][] heights) {
		
		HeightsGenerator generator = new HeightsGenerator(seed);
		this.heights = heights;
		
		int VERTEX_COUNT = 128;
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				float height = heights[i][j];
				
				double propRandom = Math.random();
				if(propRandom <= 0.004) {
					props.add(new Entity(rocc, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, 0, (float)Math.random()*10-15, (float)(80+(Math.random()*30))));
				} else if(propRandom <= 0.008) {
					props.add(new Entity(tree1, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, (float)Math.random()*10-5, (float)Math.random()*10-5, (float)(50+(Math.random()))));
				} else if(propRandom <= 0.012) {
					//Game.addObject(new Entity(elite1, new Vector3f((SIZE/VERTEX_COUNT)*j+x, height, (SIZE/VERTEX_COUNT)*i+z), (float)Math.random()*10-5, 0, (float)Math.random()*10-15, (float)(80+(Math.random()*30))));
				}
				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
	}

	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
		float heightL = getHeight(x - 1, z, generator);
		float heightR = getHeight(x + 1, z, generator);
		float heightD = getHeight(x, z - 1, generator);
		float heightU = getHeight(x, z + 1, generator);

		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}

	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
	
	public ArrayList<Entity> getProps(){
		return props;
	}
}
