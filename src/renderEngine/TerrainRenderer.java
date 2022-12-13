package renderEngine;

import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.TerrainShader;
import shaders.WireShader;
import textures.ModelTexture;
import toolbox.Maths;
import world.Terrain;

public class TerrainRenderer {

	private TerrainShader shader;
	private WireShader wireShader;

	public TerrainRenderer(TerrainShader shader, WireShader wireShader) {
		this.shader = shader;
		this.wireShader = wireShader;
	}
	public void preRender(List<Terrain> terrain) {
//		for(Terrain terr : terrain) {
//			prepareTerrain(terr);
//			loadModelMatrix(terr);
//		}
	}
	public void render(List<Terrain> terrain) {
		for(Terrain terr : terrain) {
			prepareTerrain(terr);
			loadModelMatrix(terr);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terr.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
	}
	public void renderWire(List<Terrain> terrain) {
		GL11.glLineWidth(2f);
		for(Terrain terr : terrain) {
			prepareTerrain(terr);
			loadModelMatrix(terr);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terr.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
			try {
				for(Entity e : terr.getProps()) {
					prepareTexturedModel(e.getModel());
					prepareInstance(e);
					GL11.glDrawElements(GL11.GL_TRIANGLES, e.getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
			} catch(Exception e) {}
			unbindTexturedModel();
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
		entity.getRotY(), entity.getRotZ(), entity.getScale());
		wireShader.loadTransformationMatrix(transformationMatrix);
	}
	
	public void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		ModelTexture texture = model.getTexture();
		shader.loadShineValue(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureID());
	}

	public void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = terrain.getTexture();
		shader.loadShineValue(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}

	public void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
		wireShader.loadTransformationMatrix(transformationMatrix);
	}

}
