package net.markwalder.tools.worktime;

public class TestUtils {

	public static void pause(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
