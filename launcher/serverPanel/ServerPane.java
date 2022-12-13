package serverPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import game.MainGameLoop;
import networking.Server;

public class ServerPane implements Runnable {
	String enteredPort;
	public void run() {
		JFrame frame = new JFrame("frame");
		enteredPort = JOptionPane.showInputDialog(
				frame,
				"Enter Server Port:",
				"Hyacinth Launcher",
				JOptionPane.PLAIN_MESSAGE
				);
		Thread s = new Thread(new Server(Integer.parseInt(enteredPort)));
		s.start();
	}

}
