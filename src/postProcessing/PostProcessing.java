package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import bloom.BrightFilter;
import bloom.CombineFilter;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	public static BrightFilter brightFilter;
	public static ContrastChanger contrastChanger;
	public static HorizontalBlur hBlur;
	public static VerticalBlur vBlur;
	public static CombineFilter combiner;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		brightFilter = new BrightFilter(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(DisplayManager.WIDTH/3, DisplayManager.HEIGHT/3);
		vBlur = new VerticalBlur(DisplayManager.WIDTH/3, DisplayManager.HEIGHT/3);
		combiner = new CombineFilter();
	}
	
	public static void doPostProcessing(int colorTexture){
		start();

		brightFilter.render(colorTexture);
		hBlur.render(brightFilter.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		hBlur.render(vBlur.getOutputTexture());
		for(int i = 0; i < 3; i++) {
			vBlur.render(hBlur.getOutputTexture());
			hBlur.render(vBlur.getOutputTexture());
		}
		combiner.render(colorTexture, vBlur.getOutputTexture());
		
		end();
	}
	
	public static void loadMix(float amount) {
		combiner.loadMix(amount);
	}
	
	public static void cleanUp(){
		contrastChanger.CleanUP();
		brightFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
}
