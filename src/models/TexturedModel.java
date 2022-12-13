package models;

import textures.ModelTexture;

public class TexturedModel {
	private RawModel rawModel;
	private ModelTexture modelTexture;
	
	
	public TexturedModel(RawModel model, ModelTexture texture) {
		this.rawModel = model;
		this.modelTexture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public int getTextureID() {
		return modelTexture.getID();
	}
	
	public ModelTexture getTexture() {
		return modelTexture;
	}
}