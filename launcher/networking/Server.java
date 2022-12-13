package networking;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.lwjgl.util.vector.Vector3f;

import entities.Bullet;
import entities.Entity;
import models.RawModel;
import renderEngine.Loader;
import world.HeightsGenerator;
import world.Terrain;
import world.WorldManager;

public class Server implements Runnable {
	public static TextAreaOutputStream taos;
	public static PrintStream ps;
	public static InetAddress host;
	Socket socket;
	JFrame console;
	public int port;
	public static boolean listenThreadCreated;
	public static float[] vertices;
	public static float[][] heights;
	static int seed;
	
	public Server(int port){
		makeConsole();
		this.port = port;
		listenThreadCreated = false;
		seed = new Random().nextInt(1000000000);
		generateTerrain(seed);
		Thread thread = new ServerThread(this.port, socket, ps);
	}
	
	public void run() {
		
		//ps.println("Initializing Server");
		//ps.println("Connecting to BTC Mining server...");-
		//ps.println("Installing Rootkit...");
		
//		try {
//			ps.println("Attempting Server Establishment on " + InetAddress.getLocalHost() + ", PORT: " + port);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		ps.println("Server Connection Established!");
		try {
			main();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void main() throws InterruptedException {
		//ps.println("Initialization Complete, entering server loop");
		//ps.println("ServerThread created");
		
		while(true) {
			console.repaint();
			
			Thread.sleep(1);
		}
	}
	public static void close() {
		ps.println("Server closed.");
		System.exit(0);
	}
	
	public static boolean getListenThreadCreated() {
		return listenThreadCreated;
	}
	
	public static void setListenThreadCreated(boolean b) {
		listenThreadCreated = b;
	}
	
	private void makeConsole() {
		console = new JFrame();
		console.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		console.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(console, 
		            "wh o/", "Are you close the server.", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	try {
		        		ps.println("Shutdownhook initialized");
		        		close();
		    		} catch (Exception e) {
		    			ps.println(e.getMessage());
		    		}
		        	ps.println("Shutdownhook successful");
		        	System.exit(0);
		        }
		    }
		});
		
		console.add( new JLabel(" "), BorderLayout.NORTH );
		
        JTextArea ta = new JTextArea();
        taos = new TextAreaOutputStream( ta, 60 );
        ps = new PrintStream( taos );
        System.setOut( ps );
        System.setErr( ps );
        
        JTabbedPane pane = new JTabbedPane();
        
        pane.addTab("Console", new JScrollPane(ta));
        pane.addTab("Performance", new PerfPane());

        console.add(pane);

        console.pack();
        console.setVisible( true );
        console.setSize(800,600);
	}
	
	private void generateTerrain(int seed) {
		HeightsGenerator generator = new HeightsGenerator(seed);

		int VERTEX_COUNT = 256;
		float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * Terrain.SIZE;
				float height = generator.generateHeight(j, i);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * Terrain.SIZE;
				vertexPointer++;
				double propRandom = Math.random();
			}
		}
		this.vertices = vertices;
		this.heights = heights;
	}
}
