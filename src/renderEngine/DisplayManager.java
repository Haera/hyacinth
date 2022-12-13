package renderEngine;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import toolbox.Image;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.awt.Dimension;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DisplayManager {

	public static int WIDTH = 1600;
	public static int HEIGHT = 900;
	public static boolean fullscreen = false;
	public static int windowX;
	public static int windowY;
	public static final int TARGET_FRAMES = 144;
	public static float FOV = 100;
	private static final String TITLE = "Hyacinth";
	private static double[] xpos = new double[1];
	private static double[] ypos = new double[1];
	private static long window;

	public static void createDisplay() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
		
		// Create the window
		if(fullscreen) {
			WIDTH = vidmode.width();
			HEIGHT = vidmode.height();
			setWindow(glfwCreateWindow(WIDTH, HEIGHT, TITLE, glfwGetPrimaryMonitor(), NULL));
		} else
			setWindow(glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL));
		glfwSetWindowPos(window, windowX, windowY);
		if (getWindow() == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(getWindow(), (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(getWindow(), pWidth, pHeight);
		}
		
		glfwMakeContextCurrent(getWindow());
		// Disable v-sync
		glfwSwapInterval(0);
		// Make the window visible
		
		glfwShowWindow(getWindow());
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		GL.createCapabilities();
		//Icons 
		try ( GLFWImage.Buffer icons = GLFWImage.malloc(4) ) {
			GLFWImage image128 = GLFWImage.malloc();
			image128.set(128, 128, Image.load("Logo128"));
			GLFWImage image64 = GLFWImage.malloc();
			image64.set(64, 64, Image.load("Logo64"));
			GLFWImage image32 = GLFWImage.malloc();
			image32.set(32, 32, Image.load("Logo32"));
			GLFWImage image16 = GLFWImage.malloc();
			image16.set(16, 16, Image.load("Logo16"));
			icons.put(image128);
			icons.put(image64);
			icons.put(image32);
			icons.put(image16);
			icons.position(0);
			 GLFW.glfwSetWindowIcon(window, icons);
		 }
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
	}

	public static void updateDisplay() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		xpos = new double[1];
		ypos = new double[1];
		GLFW.glfwGetCursorPos(DisplayManager.getWindow(), xpos, ypos);
		
		glfwPollEvents();
	}

	public static void closeDisplay() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static long getWindow() {
		return window;
	}

	public static void setWindow(long window) {
		DisplayManager.window = window;
	}
	
	public static Dimension getDimensions() {
		return new Dimension(WIDTH, HEIGHT);
	}
	
	public static double getxPos() {
		return xpos[0];
	}
	
	public static double getyPos() {
		return ypos[0];
	}

}
