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

package net.markwalder.tools.worktime;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import java.awt.*;
import java.time.Clock;
import javax.swing.*;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseImpl;
import net.markwalder.tools.worktime.db.store.FileStoreImpl;
import net.markwalder.tools.worktime.db.store.Store;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import net.markwalder.tools.worktime.tracker.ActivityTracker;
import net.markwalder.tools.worktime.tracker.mouse.DefaultMouseTrackerImpl;
import net.markwalder.tools.worktime.tracker.mouse.MouseActivityTrackerImpl;
import net.markwalder.tools.worktime.tracker.mouse.MouseTracker;
import net.markwalder.tools.worktime.ui.MacOSX;
import net.markwalder.tools.worktime.ui.Window;


public class Main {

	public static void main(String[] args) {

		// check if headless mode is enabled
		boolean headless = GraphicsEnvironment.isHeadless();

		if (!headless) {

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// ignore
			}

			MacOSX.setDockIcon();

			// ask user if work time tracker should get started ...
			int answer = JOptionPane.showConfirmDialog(null, "Start " + Window.TITLE + "?", "Start", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

		}

		// GuiceDebug.enable();

		// configure dependency injection
		Module module = binder -> {

			// current date and time
			binder.bind(Clock.class).toInstance(Clock.systemDefaultZone());

			// main controller
			binder.bind(Controller.class).to(ControllerImpl.class).in(Singleton.class);

			// user activity tracking
			binder.bind(ActivityListener.class).to(ControllerImpl.class).in(Singleton.class);
			binder.bind(ActivityTracker.class).to(MouseActivityTrackerImpl.class).in(Singleton.class);
			binder.bind(MouseTracker.class).to(DefaultMouseTrackerImpl.class).in(Singleton.class);

			// persistence
			binder.bind(Database.class).to(DatabaseImpl.class).in(Singleton.class);
			binder.bind(Store.class).to(FileStoreImpl.class).in(Singleton.class);

		};

		Injector injector = Guice.createInjector(module);
		Controller controller = injector.getInstance(Controller.class);

		controller.start();

	}

}
