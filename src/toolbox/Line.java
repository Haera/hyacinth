package toolbox;

import java.awt.Color;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import game.Game;
import game.MainGameLoop;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class Line {
	static Entity line;
	static RawModel lineModel;
	static ModelTexture lineTex;
	
	public static void init(Loader loader) {
		lineTex = new ModelTexture(Game.getLoader().loadTexture("white"));
		lineModel = loader.loadToVAO(lineVertices, tex, normals, indices);
		lineTex.setReflectivity(1.5f);
		lineTex.setShineDamper(15f);
	}
	
	final static float[] lineVertices = {
			-1, 0, 0,
			1, 0, 0
	};
	final static int[] indices = {
			0, 1, 0
	};
	final static float[] normals = {
			0, 1, 0
	};
	final static float[] tex = {
			-1, 0,
			1, 0,
			-1, 0
	};
	public static void drawLine(Vector3f a, Vector3f b, Color c) {
		line = new Entity(new TexturedModel(lineModel, lineTex), new Vector3f(a), 0, 0, 0, 1f);
		Game.getRenderer().processEntity(line);
	}
}
