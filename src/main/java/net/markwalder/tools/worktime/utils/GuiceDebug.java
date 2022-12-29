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

package net.markwalder.tools.worktime.utils;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

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
