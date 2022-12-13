package debug;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import loader.Loader;
import renderEngine.DisplayManager;

public class DebugPane implements Runnable {
	public void run() {
		JTextField playerName = new JTextField();
		JTextField SSNum = new JTextField();
		JTextField xRes = new JTextField();
		JTextField yRes = new JTextField();
		JCheckBox fullscreen = new JCheckBox();
		Object[] messages = {
		    "Username:", playerName,
		    "Social Security #:", SSNum,
		    "X Resolution", xRes,
		    "Y Resolution", yRes,
		    "Fullscreen", fullscreen
		};

		int option = JOptionPane.showConfirmDialog(null, messages, "Settings", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if(playerName.getText() != null)
				Loader.playerName = playerName.getText();
		    if(xRes.getText().length() > 0 && yRes.getText().length() > 0) {
			    int x = Integer.parseInt(xRes.getText());
			    int y = Integer.parseInt(yRes.getText());
			    if((float)x/(float)y == 16f/9f) {
				    DisplayManager.WIDTH = Integer.parseInt(xRes.getText());
				    DisplayManager.HEIGHT = Integer.parseInt(yRes.getText());
			    } else {
			    	JOptionPane.showConfirmDialog(null, "Aspect Ratio is not 16:9", "Error", JOptionPane.OK_CANCEL_OPTION);
			    }
		    }
		    DisplayManager.fullscreen = fullscreen.isSelected();
		}
	}
}