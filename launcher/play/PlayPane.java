package play;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import game.MainGameLoop;
import loader.Loader;

public class PlayPane implements Runnable {
	String enteredIP;
	String enteredPort;
	public void run() {
		JFrame frame = new JFrame("frame");
		enteredIP = JOptionPane.showInputDialog(
				frame,
				"Enter server IP:",
				"Hyacinth Launcher",
				JOptionPane.PLAIN_MESSAGE
		);
		if(!enteredIP.equals("1")) {
			enteredPort = JOptionPane.showInputDialog(
					frame,
					"Enter Server Port:",
					"Hyacinth Launcher",
					JOptionPane.PLAIN_MESSAGE
					);
		} else {
			enteredPort = "1";
		}
		Loader.startGame(enteredIP, enteredPort);
	}

}
