package net.markwalder.tools.worktime.ui;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
			Class<?> clazz = Class.forName("com.apple.eawt.Application");
			Method getApplication = clazz.getMethod("getApplication");
			Object application = getApplication.invoke(clazz);
			Method setDockIconImage = clazz.getMethod("setDockIconImage", Image.class);
			setDockIconImage.invoke(application, image);
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			// ignore
		}
	}

}
