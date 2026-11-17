/*
 * Copyright 2026 Stephan Markwalder
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

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;

public class WindowTest {

	@Test
	public void restoreFromTrayShowsWindowBeforeRemovingIcon() {
		Window window = mock(Window.class);
		TrayIcon trayIcon = mock(TrayIcon.class);
		SystemTray tray = mock(SystemTray.class);
		doCallRealMethod().when(window).restoreFromTray(trayIcon);

		try (MockedStatic<SystemTray> systemTray = mockStatic(SystemTray.class)) {
			systemTray.when(SystemTray::getSystemTray).thenReturn(tray);

			window.restoreFromTray(trayIcon);

			InOrder order = inOrder(window, tray);
			order.verify(window).setVisible(true);
			order.verify(window).setExtendedState(Frame.NORMAL);
			order.verify(tray).remove(trayIcon);
		}
	}
}
