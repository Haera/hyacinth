package networking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;

public class ClientData implements Serializable {
	private static final long serialVersionUID = -851491039155737992L;
	
	public CopyOnWriteArrayList<Vector3f> bulletPositions = new CopyOnWriteArrayList<Vector3f>();
	
	public Vector3f position = new Vector3f(0, 0, 0);
	public Vector3f viewVector = new Vector3f(0, 0, 0);
	
	public String playerName;
	public int playerID = 0;
}