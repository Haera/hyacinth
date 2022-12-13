package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import game.Game;
import guis.GuiTexture;
import entities.Entity;
import entities.Light;
import entities.Player;
import entities.EntityShader;
import models.TexturedModel;
import particles.ParticleMaster;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import shaders.TerrainShader;
import shaders.WireShader;
import skybox.SkyboxRenderer;
import world.Terrain;

public class MasterRenderer {
		private static final float NEAR_PLANE = 0.1f;
		private static final float FAR_PLANE = 1000000;
		
		private Matrix4f projectionMatrix;

		private EntityShader shader = new EntityShader();
		private EntityRenderer renderer;
		private TerrainRenderer terrainRenderer;
		private TerrainShader terrainShader = new TerrainShader();
		private WireShader wireShader = new WireShader();
		
		Fbo fbo;
		
		static SkyboxRenderer skyboxRenderer;
		
		Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
		private List<Terrain> terrains = new ArrayList<Terrain>();
		
		public MasterRenderer() {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			createProjectionMatrix();
			renderer = new EntityRenderer(shader, this);
			terrainRenderer = new TerrainRenderer(terrainShader, wireShader);
			fbo = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);
			skyboxRenderer = new SkyboxRenderer(Game.getLoader(), projectionMatrix);
		}
		
		public void render(Light lights, Player camera) {
			fbo.bindFrameBuffer();
			prepare();
			
			shader.start();
			shader.loadLight(lights);
			shader.loadViewMatrix(camera);
			shader.loadProjectionMatrix(projectionMatrix);
			renderer.render(entities);
			shader.stop();
			
			terrainShader.start();
			shader.loadLight(lights);
			terrainShader.loadViewMatrix(camera);
			terrainShader.loadProjectionMatrix(projectionMatrix);
			terrainRenderer.preRender(terrains);
			terrainRenderer.render(terrains);
			terrainShader.stop();

			GL11.glEnable (GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			wireShader.start();
			wireShader.loadViewMatrix(camera);
			wireShader.loadProjectionMatrix(projectionMatrix);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			terrainRenderer.preRender(terrains);
			terrainRenderer.renderWire(terrains);
			wireShader.stop();
			terrains.clear();
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			
			skyboxRenderer.render(camera);
			
			ParticleMaster.render(camera);
			
			PostProcessing.doPostProcessing(fbo.getColourTexture());
			fbo.unbindFrameBuffer();
			
			entities.clear();
		}
		
		public void processTerrain(Terrain terrain) {
			terrains.add(terrain);
		}
		
		public void processEntity(Entity entity) {
			TexturedModel entityModel = entity.getModel();
			List<Entity> batch = entities.get(entityModel);
			if(batch != null) {
				batch.add(entity);
			} else {
				List<Entity> newBatch = new ArrayList<Entity>();
				newBatch.add(entity);
				entities.put(entityModel, newBatch);
			}
		}
		
		public void prepare() {
			createProjectionMatrix();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}
		
		private void createProjectionMatrix(){
			float aspectRatio = (float) DisplayManager.getDimensions().getWidth() / (float) DisplayManager.getDimensions().getHeight();
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(DisplayManager.FOV / 2f))) * aspectRatio);
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;

			projectionMatrix = new Matrix4f();
			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
		}
		
		public void cleanUp() {
			shader.cleanUP();
			terrainShader.cleanUP();
			wireShader.cleanUP();
		}
		public Matrix4f getProjectionMatrix() {
			return projectionMatrix;
		}
		public Map<TexturedModel, List<Entity>> entityMap(){
			return entities;
		}
}
