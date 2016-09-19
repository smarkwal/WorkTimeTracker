package net.markwalder.tools.worktime;

import com.google.inject.*;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseImpl;
import net.markwalder.tools.worktime.db.store.FileStoreImpl;
import net.markwalder.tools.worktime.db.store.Store;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import net.markwalder.tools.worktime.tracker.ActivityTracker;
import net.markwalder.tools.worktime.tracker.mouse.DefaultMouseTrackerImpl;
import net.markwalder.tools.worktime.tracker.mouse.MouseActivityTrackerImpl;
import net.markwalder.tools.worktime.tracker.mouse.MouseTracker;
import net.markwalder.tools.worktime.ui.Window;

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
			int answer = JOptionPane.showConfirmDialog(null, "Start " + Window.TITLE + "?", "Start", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

		}

		// GuiceDebug.enable();

		// configure dependency injection
		Module module = new Module() {

			@Override
			public void configure(Binder binder) {

				// main controller
				binder.bind(Controller.class).to(ControllerImpl.class).in(Singleton.class);

				// user activity tracking
				binder.bind(ActivityListener.class).to(ControllerImpl.class).in(Singleton.class);
				binder.bind(ActivityTracker.class).to(MouseActivityTrackerImpl.class).in(Singleton.class);
				binder.bind(MouseTracker.class).to(DefaultMouseTrackerImpl.class).in(Singleton.class);

				// persistence
				binder.bind(Database.class).to(DatabaseImpl.class).in(Singleton.class);
				binder.bind(Store.class).to(FileStoreImpl.class).in(Singleton.class);

			}

		};

		Injector injector = Guice.createInjector(module);
		Controller controller = injector.getInstance(Controller.class);

		controller.start();

	}

}
