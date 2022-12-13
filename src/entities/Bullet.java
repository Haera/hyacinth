package entities;

import java.io.Serializable;

import org.lwjgl.util.vector.Vector3f;

import game.Game;
import game.MainGameLoop;
import models.TexturedModel;
import particles.Particle;
import particles.ParticleTexture;

public class Bullet implements Serializable {
	private static final long serialVersionUID = 266882775302416750L;
	ParticleTexture smokeTrail;
	Vector3f velocity;
	Vector3f position;
	boolean KILLME = false;

	public Bullet(Vector3f position, Vector3f velocity) {
		this.position = position;
		this.velocity = velocity;
		smokeTrail = new ParticleTexture(Game.getLoader().loadTexture("smoke"), 1);
	}
	
	public void update() {
		position.translate(velocity.x, velocity.y, velocity.z);
		velocity.y -= 0.1;
		if(Math.random() < 0.8) {
			new Particle(smokeTrail, new Vector3f(position), new Vector3f(0, 0, 0), 0.05f, 100, 0, 50); // Particles auto add to static arraylist, no add needed
			new Particle(smokeTrail, 
					new Vector3f(position.x - velocity.x/2, position.y - velocity.y/2, position.z - velocity.z/2), // Position that is 1/2 of velocity behind it
					new Vector3f(0, 0, 0), 0.05f, 100, 0, 50);
		}
		if(position.y < 0) {
			KILLME = true;
		}
	}
}
