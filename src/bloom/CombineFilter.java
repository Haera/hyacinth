package bloom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import postProcessing.ImageRenderer;

public class CombineFilter {
	
	private ImageRenderer renderer;
	private CombineShader shader;
	
	public CombineFilter(){
		shader = new CombineShader();
		shader.start();
		shader.loadTexLocations();
		shader.stop();
		renderer = new ImageRenderer();
	}
	
	public void render(int colorTexture, int brightTexture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightTexture);
		renderer.renderQuad();
		shader.stop();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUP();
	}

	public void loadMix(float amount) {
		shader.setMix(amount);
	}
}
