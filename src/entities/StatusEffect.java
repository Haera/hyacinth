package entities;

import postProcessing.PostProcessing;

public class StatusEffect {
	private static float bloomMix = 1;
	
	public static void update() {
		if(bloomMix > 1) {
			bloomMix -= 0.05;
		}
		PostProcessing.loadMix(bloomMix);
	}
	
	public static void damage() {
		bloomMix += 4;
	}
	
	public static void die() {
		bloomMix += 15;
	}
	
	public static void shoot() {
		bloomMix += 1;
	}
}
