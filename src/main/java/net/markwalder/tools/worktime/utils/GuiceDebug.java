package net.markwalder.tools.worktime.utils;

import java.util.logging.*;

/**
 * Enable or disable Guice debug output on the console.
 * Source: https://garbagecollected.org/2007/12/08/guice-debug-output/
 */
public class GuiceDebug {

	private static final Handler HANDLER;

	static {

		Formatter formatter = new Formatter() {
			public String format(LogRecord record) {
				String message = record.getMessage();
				Level level = record.getLevel();
				return String.format("[Guice %s] %s%n", level.getName(), message);
			}
		};

		HANDLER = new StreamHandler(System.out, formatter);

		HANDLER.setLevel(Level.ALL);
	}

	private GuiceDebug() {
	}

	private static Logger getLogger() {
		return Logger.getLogger("com.google.inject");
	}

	public static void enable() {
		Logger logger = getLogger();
		logger.addHandler(HANDLER);
		logger.setLevel(Level.ALL);
	}

	public static void disable() {
		Logger logger = getLogger();
		logger.setLevel(Level.OFF);
		logger.removeHandler(HANDLER);
	}

}
