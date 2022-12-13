package particles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lwjgl.util.vector.Matrix4f;

import entities.Player;
import renderEngine.Loader;

public class ParticleMaster implements Serializable{
	private static ConcurrentMap<ParticleTexture, List<Particle>> particles = new ConcurrentHashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer renderer;
	
	public static void init(Loader loader, Matrix4f projectionMatrix) {
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}
	
	public static void update() {
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()) {
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			while(iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillAlive = p.update();
				if(!stillAlive) {
					iterator.remove();
					if(list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
		}
	}
	
	public static void render(Player camera) {
		renderer.render(particles, camera);
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
	
	public static void addParticle(Particle particle) {
		List<Particle> list = particles.get(particle.getTexture());
		if(list == null) {
			list = new ArrayList<Particle>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}
	public static int getCount(Object key) {
		return particles.get(key).size();
	}
}
