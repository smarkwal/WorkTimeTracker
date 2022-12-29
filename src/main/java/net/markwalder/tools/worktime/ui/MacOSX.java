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

import java.awt.*;
import java.net.URL;

public class MacOSX {

	/**
	 * Replaces the default Java icon in the Dock with the application icon.
	 */
	public static void setDockIcon() {
		URL url = MacOSX.class.getResource("images/main-icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		setDockIcon(image);
	}

	private static void setDockIcon(Image image) {
		try {
			Taskbar taskbar = Taskbar.getTaskbar();
			taskbar.setIconImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
