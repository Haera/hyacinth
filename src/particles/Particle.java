package particles;

import java.io.Serializable;

import org.lwjgl.util.vector.Vector3f;

public class Particle implements Serializable{
	private static final long serialVersionUID = 6035514293282829907L;
	protected Vector3f position;
	protected Vector3f velocity;
	protected float gravityEffect;
	protected double lifeLength;
	protected float rotation;
	protected float scale;
	protected double elapsedTime = 0;
	protected ParticleTexture texture;
	
	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, double lifeLength, float rotation,
			float scale) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		ParticleMaster.addParticle(this);
	}
	
	protected boolean update() {
		position.x += velocity.x;
		position.y += velocity.y;
		position.z += velocity.z;
		velocity.y -= 0.1 * gravityEffect;
		elapsedTime += 1;
		return elapsedTime < lifeLength;
	}
	
	public ParticleTexture getTexture() {
		return texture;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
	public double getAlpha() {
		return 1/(lifeLength/elapsedTime);
	}
}
