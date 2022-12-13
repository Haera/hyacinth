package textures;

public class ModelTexture {
	private int textureID;
	
	private float shineDamper = 10f;
	private float reflectivity = 1.0f;
	
	public ModelTexture(int texID) {
		this.textureID = texID;
	}
	
	public int getID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamperr) {
		this.shineDamper = shineDamperr;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivityy) {
		this.reflectivity = reflectivityy;
	}
}
