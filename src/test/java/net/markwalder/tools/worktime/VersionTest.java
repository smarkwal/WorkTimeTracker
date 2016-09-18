package net.markwalder.tools.worktime;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

	@Test
	public void getVersion() throws Exception {
		String version = Version.getVersion();
		Assert.assertNotEquals("Version is defined", Version.UNKNOWN, version);
	}

}