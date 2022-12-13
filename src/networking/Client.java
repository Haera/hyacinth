package networking;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import entities.Bullet;
import game.Game;
import world.WorldManager;

public class Client extends Thread {
	private InetAddress host;
	private Socket socket;
	private ObjectOutputStream dOut;
	private ObjectInputStream dIn;
	private CopyOnWriteArrayList<ClientData> players;
	private int PID;
	private int port;
	private float testInfo;
	private boolean update;
	
	public Client(InetAddress IP, int port) {
		players = new CopyOnWriteArrayList<ClientData>();
		host = IP;
		this.port = port;
	}
	
	public void connect() {
		try {
			socket = new Socket(host, port);
			socket.setTcpNoDelay(true);
			System.out.println("opened socket, host: " + host + ", port: " + port);
			dIn = new ObjectInputStream(socket.getInputStream());
			dOut = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("initialized inputstream");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		connect();
		
		try {
			//Game.world.chunkAtPos().generateTerrain(Game.getLoader(), Game.world.getSeed(), (float[])dIn.readObject(), (float[][])dIn.readObject());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(true) {
			try {
				sendData();
				recieveData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setUpdate(boolean b) {
		update = b;
	}
	
	public boolean getUpdate() {
		return update;
	}
	
	public void close() {
		try {
			dOut.close();
			dIn.close();
			
			socket.close();
			System.out.println("Server closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendData() { //sends coords
		try {
			dOut.reset();
			dOut.writeObject(Game.clientData);
			dOut.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void recieveData() {
		try {
			players = (CopyOnWriteArrayList<ClientData>) dIn.readObject();
			dIn.skip(dIn.available());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getPID() {  
		return PID;
	}
	
	public float getTestInfo() {
		return testInfo;
	}
	
	public CopyOnWriteArrayList<ClientData> getPlayers() {
		return players;
	}
}
