package net.markwalder.tools.worktime.tracker.mouse;

import net.markwalder.tools.worktime.TestUtils;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class MouseActivityTrackerImplTest {

	@Test
	public void testMouseMoved() {

		// TODO: use a powerful mock framework to create a mock objects

		MockActivityListener activityListener = new MockActivityListener();

		MockMouseTracker mouseTracker = new MockMouseTracker();
		mouseTracker.setMouse(100, 100);

		MouseActivityTrackerImpl tracker = new MouseActivityTrackerImpl(activityListener, mouseTracker);

		// track mouse very 10 ms
		long interval = 10;
		tracker.setPollInterval(interval);

		// start tracking
		tracker.start();
		TestUtils.pause(100);
		// TODO: check if thread has been started

		// simulate mouse move
		mouseTracker.moveMouse(10, 0);
		TestUtils.pause(100);

		Assert.assertEquals("Mouse moved", 1, activityListener.getCount());

		// do not move mouse
		TestUtils.pause(100);

		Assert.assertEquals("Mouse not moved", 1, activityListener.getCount());

		// simulate mouse move
		mouseTracker.moveMouse(0, 10);
		TestUtils.pause(100);

		Assert.assertEquals("Mouse moved", 2, activityListener.getCount());

		// stop tracking
		tracker.stop();

		// TODO: check if thread has been stopped

	}

	private class MockMouseTracker implements MouseTracker {

		private MousePosition point;

		void setMouse(int x, int y) {
			this.point = new MousePosition(x, y);
		}

		void moveMouse(int dx, int dy) {
			this.point = new MousePosition(point.getX() + dx, point.getY() + dy);
		}

		// ------------------------------------------------------
		// interface MouseTracker

		@Override
		public MousePosition getMousePosition() {
			return point;
		}

	}

	private class MockActivityListener implements ActivityListener {

		private AtomicInteger count = new AtomicInteger(0);

		int getCount() {
			return count.get();
		}

		// -------------------------------------------------------
		// interface ActivityListener

		@Override
		public void reportActive(boolean active) {
			if (active) {
				count.incrementAndGet();
			}
		}

	}

}