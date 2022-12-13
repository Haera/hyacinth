package shaders;

import org.lwjgl.util.vector.Matrix4f;

import entities.Light;
import entities.Player;
import toolbox.Maths;

public class BlurShader extends ShaderProgram {

	public static final String VERTEX_FILE = "/shaders/blurVert.vert";
	public static final String FRAGMENT_FILE = "/shaders/blurFrag.frag";
//	uniform sampler2D u_texture;
//
//	uniform float resolution;
//
//	uniform float radius;
//
//	uniform vec2 dir;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_radius;
	private int location_resolution;
	private int location_dir;

	public BlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "Position");
		super.bindAttribute(1, "texCoord");
		super.bindAttribute(2, "Color");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_radius = super.getUniformLocation("resolution");
		location_resolution = super.getUniformLocation("radius");
		location_dir = super.getUniformLocation("dir");
	}
	
	public void loadBlur(float radius, float resolution, float dir) {
		super.loadFloat(location_radius, radius);
		super.loadFloat(location_resolution, resolution);
		super.loadFloat(location_dir, dir);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void loadViewMatrix(Player camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
