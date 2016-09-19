package net.markwalder.tools.worktime.ui;

import com.google.inject.Inject;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.Version;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Date;

public class Window extends JFrame implements ActionListener, WindowListener, KeyListener {

	private final Controller controller;

	private final Container contentPane;

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

		this.contentPane = this.getContentPane();
		contentPane.add(workDayPanel);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.addKeyListener(this);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);

		URL resource = getClass().getResource("images/main-icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(resource);
		setIconImage(image);

		this.setVisible(true);

	}

	public void switchView() {
		contentPane.removeAll();
		if (view == VIEW_DAY) {
			view = VIEW_YEAR;
			contentPane.add(workYearPanel);
		} else {
			view = VIEW_DAY;
			contentPane.add(workDayPanel);
		}
		this.pack();
	}

	//----------------------------------------------------------------------------------------
	// interface ActionListener

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	//----------------------------------------------------------------------------------------
	// interface WindowListener

	@Override
	public void windowOpened(WindowEvent e) {
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

		if (SystemTray.isSupported()) {

			URL resource = getClass().getResource("images/tray-icon.png");
			Image image = Toolkit.getDefaultToolkit().getImage(resource);
			final TrayIcon trayIcon = new TrayIcon(image);

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Window.this.setVisible(true);
					Window.this.setExtendedState(Window.NORMAL);
					SystemTray.getSystemTray().remove(trayIcon);
				}
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
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	//----------------------------------------------------------------------------------------
	// interface KeyListener

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		char keyChar = e.getKeyChar();
		if (keyCode == 27) { // escape
			this.setState(Window.ICONIFIED);
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
