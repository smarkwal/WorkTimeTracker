package net.markwalder.tools.worktime;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

	public static final String UNKNOWN = "[unknown]";

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
		InputStream stream = null;
		try {
			stream = Version.class.getClassLoader().getResourceAsStream("version.properties");
			if (stream != null) {
				properties.load(stream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return properties;
	}

}
