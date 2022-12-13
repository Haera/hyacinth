package networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Player;
import game.Game;

public class ServerThread extends Thread {
	public ObjectOutputStream dOut;
	public static CopyOnWriteArrayList<ClientData> players;
	public static CopyOnWriteArrayList<Vector3f> bullets;
	public ObjectInputStream dIn;
	public Socket socket;
	public int port;
	public PrintStream ps;
	public int clID;
	
	//Are you polish'd?
	public ServerThread(int port, Socket sock, PrintStream ps) {
		this.ps = ps;
		this.port = port;
		this.socket = sock;
		
		ps.println("ServerThread " + this + " constructor called with port " + port);
		try {
			ps.println("Server hosted on: " + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ps.println("Server.getListenThreadCreated() = " + Server.getListenThreadCreated());
		if(!Server.getListenThreadCreated()) {
			ps.println("listenThreadCreated bool set to true, entering main");
			Server.setListenThreadCreated(true);
			players = new CopyOnWriteArrayList<ClientData>();
			bullets = new CopyOnWriteArrayList<Vector3f>();
			main();
		} else {
			players.add(null);
			clID = players.size()-1;
		}
	}
	
	public void run() {
		ps.println("Run Called");
		connect();
		ps.println("Connected");
		
		//sendTerrain();
		
		while(true) {
			sendData();
			recieveData();
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void connect() {
		ps.println("Inside of connect called, attempting to establish input/output streams");
		try {
			dOut = new ObjectOutputStream(socket.getOutputStream());
			dIn = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void main() {
		ServerSocket serversocket;
        try {
			serversocket = new ServerSocket(port);
	        while (true) {
	            Socket clientsocket = serversocket.accept();
	            clientsocket.setTcpNoDelay(true);
	            new ServerThread(port, clientsocket, ps).start();
	            Thread.sleep(2);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendTerrain() {
		try {
			dOut.writeObject(Server.vertices);
			dOut.writeObject(Server.heights);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void recieveData() {
		try {
			players.set(clID, (ClientData) dIn.readObject());
			for(Vector3f b : players.get(0).bulletPositions) {
				boolean imliterallyinlecalc = true;
				for(Vector3f v : bullets) {
					if(b.equals(v)) imliterallyinlecalc = false;
				}
				if(imliterallyinlecalc) bullets.add(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		ps.println("Server closed.");
		try {
			dIn.close();
			dOut.close();
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public void sendData() { //sends coords
		try {
			for(ClientData p : players)
				p.bulletPositions = bullets;
			ArrayList<ClientData> temp = new ArrayList<ClientData>();
			temp.addAll(players);
            dOut.reset();
            dOut.writeObject(temp);
			dOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}