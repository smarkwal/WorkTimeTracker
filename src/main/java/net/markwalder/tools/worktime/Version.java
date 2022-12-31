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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {

	private static final Logger LOGGER = LoggerFactory.getLogger(Version.class);

	public static final String UNKNOWN = "[unknown]";

	private Version() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Get the current version number.
	 *
	 * @return Version number
	 */
	public static String getVersion() {
		Properties properties = loadProperties();
		return properties.getProperty("version", UNKNOWN);
	}

	/**
	 * Load properties from version.properties on classpath.
	 *
	 * @return Properties
	 */
	private static Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream stream = Version.class.getClassLoader().getResourceAsStream("version.properties")) {
			if (stream != null) {
				properties.load(stream);
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to load version.properties.", e);
		}
		return properties;
	}

}
