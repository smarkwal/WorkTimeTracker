package net.markwalder.tools.worktime;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Main {

	public static void main(String[] args) throws IOException {

		// check if headless mode is enabled
		boolean headless = GraphicsEnvironment.isHeadless();

		if (!headless) {

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// ignore
			}

			// ask user if work time tracker should get started ...
			int answer = JOptionPane.showConfirmDialog(null, "Start Work Time Tracker?", "Start", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

		}

		Controller controller = new Controller();
		controller.start();

	}

}
