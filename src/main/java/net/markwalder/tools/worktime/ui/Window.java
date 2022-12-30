/*
 * Copyright 2022 Stephan Markwalder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.markwalder.tools.worktime.ui;

import com.google.inject.Inject;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Date;
import javax.swing.*;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.Version;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.apache.commons.lang3.SystemUtils;

public class Window extends JFrame implements WindowListener, KeyListener {

	private final Controller controller;

	private final WorkDayPanel workDayPanel;
	private final WorkYearPanel workYearPanel;

	private static final String VIEW_DAY = "WorkDay";
	private static final String VIEW_YEAR = "WorkYear";
	private String view = VIEW_DAY;

	public static final String TITLE = "Work Time Tracker";

	@Inject
	public Window(Controller controller, WorkDayPanel workDayPanel, WorkYearPanel workYearPanel) {

		String version = Version.getVersion();
		this.setTitle(TITLE + " " + version);

		this.controller = controller;
		this.workDayPanel = workDayPanel;
		this.workYearPanel = workYearPanel;

		Container contentPane = this.getContentPane();
		contentPane.add(workDayPanel);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);

		URL resource = getClass().getResource("images/main-icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(resource);
		setIconImage(image);

	}

	private void switchView() {
		Container contentPane = this.getContentPane();
		contentPane.removeAll();
		if (view.equals(VIEW_DAY)) {
			view = VIEW_YEAR;
			contentPane.add(workYearPanel);
		} else {
			view = VIEW_DAY;
			contentPane.add(workDayPanel);
		}
		this.pack();
	}

	//----------------------------------------------------------------------------------------
	// interface WindowListener

	@Override
	public void windowOpened(WindowEvent e) {
		// nothing to do
	}

	@Override
	public void windowClosing(WindowEvent e) {
		int answer = JOptionPane.showConfirmDialog(this, "Close " + TITLE + "?", "Close", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			this.setVisible(false);
			this.dispose();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		controller.stop();
	}

	@Override
	public void windowIconified(WindowEvent e) {

		// on Windows: minimize to system tray
		if (SystemUtils.IS_OS_WINDOWS && SystemTray.isSupported()) {

			URL resource = getClass().getResource("images/tray-icon.png");
			Image image = Toolkit.getDefaultToolkit().getImage(resource);
			final TrayIcon trayIcon = new TrayIcon(image);

			ActionListener actionListener = event -> {
				Window.this.setVisible(true);
				Window.this.setExtendedState(NORMAL);
				SystemTray.getSystemTray().remove(trayIcon);
			};

			trayIcon.setToolTip(TITLE);

			final PopupMenu popupMenu = new PopupMenu();
			MenuItem openItem = new MenuItem("Open");
			openItem.addActionListener(actionListener);
			popupMenu.add(openItem);
			trayIcon.setPopupMenu(popupMenu);

			trayIcon.addActionListener(actionListener);

			try {
				SystemTray.getSystemTray().add(trayIcon);
			} catch (AWTException ex) {
				ex.printStackTrace();
			}

			this.setVisible(false);

		}

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// nothing to do
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// nothing to do
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// nothing to do
	}

	//----------------------------------------------------------------------------------------
	// interface KeyListener

	@Override
	public void keyTyped(KeyEvent e) {
		// nothing to do
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (keyCode == 27) { // escape
			this.setState(ICONIFIED);
		} else if (keyCode == 37) { // arrow left
			controller.decrementDisplayDate();
		} else if (keyCode == 39) { // arrow right
			controller.incrementDisplayDate();
		} else if (keyChar == 'y') {
			switchView();
		} else if (keyChar == 't') {
			Date date = DateTimeUtils.getToday();
			controller.setDisplayDate(date);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// nothing to do
	}

}
