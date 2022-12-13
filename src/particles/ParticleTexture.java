package particles;

import java.io.Serializable;

public class ParticleTexture implements Serializable {
	private static final long serialVersionUID = 492246700344675378L;
	private int textureID;
	private int numberOfRows;
	public ParticleTexture(int textureID, int numberOfRows) {
		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
	}
	public int getTextureID() {
		return textureID;
	}
	public int getNumberOfRows() {
		return numberOfRows;
	}
}
