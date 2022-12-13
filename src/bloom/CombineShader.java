package bloom;

import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/bloom/passThroughVertex.txt";
	private static final String FRAGMENT_FILE = "/bloom/combineFragment.txt";
	
	private int location_colourTexture;
	private int location_highlightTexture;
	private int location_mixAmount;
	
	private float mix = 1;
	
	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformLocation("colourTexture");
		location_highlightTexture = super.getUniformLocation("highlightTexture");
		location_mixAmount = super.getUniformLocation("mixAmount");
	}
	
	protected void loadTexLocations(){
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_highlightTexture, 1);
	}
	
	public void loadMix(float amount) {
		super.loadFloat(location_mixAmount, amount);
	}
	
	public void setMix(float amount) {
		mix = amount;
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void start() {
		super.start();
		loadMix(mix);
	}
}
