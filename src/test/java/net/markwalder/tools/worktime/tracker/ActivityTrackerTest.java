package net.markwalder.tools.worktime.tracker;

import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.TestUtils;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.tracker.ActivityTracker;
import net.markwalder.tools.worktime.tracker.MousePosition;
import net.markwalder.tools.worktime.tracker.MouseTracker;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityTrackerTest {

	@Test
	public void testMouseMoved() {

		// TODO: use a powerful mock framework to create a mock controller
		MockController controller = new MockController();

		MockMouseTracker mouseTracker = new MockMouseTracker();
		mouseTracker.setMouse(100, 100);

		ActivityTracker tracker = new ActivityTracker(controller, mouseTracker);

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

		Assert.assertEquals("Mouse moved", 1, controller.getCount());

		// do not move mouse
		TestUtils.pause(100);

		Assert.assertEquals("Mouse not moved", 1, controller.getCount());

		// simulate mouse move
		mouseTracker.moveMouse(0, 10);
		TestUtils.pause(100);

		Assert.assertEquals("Mouse moved", 2, controller.getCount());

		// stop tracking
		tracker.stop();

		// TODO: check if thread has been stopped

	}

	private class MockMouseTracker implements MouseTracker {

		private MousePosition point;

		public void setMouse(int x, int y) {
			this.point = new MousePosition(x, y);
		}

		public void moveMouse(int dx, int dy) {
			this.point = new MousePosition(point.getX() + dx, point.getY() + dy);
		}

		// ------------------------------------------------------
		// interface MouseTracker

		@Override
		public MousePosition getMousePosition() {
			return point;
		}

	}

	private class MockController implements Controller {

		private AtomicInteger count = new AtomicInteger(0);

		public int getCount() {
			return count.get();
		}

		// -------------------------------------------------------
		// interface Controller

		@Override
		public void reportActive(boolean active) {
			if (active) {
				count.incrementAndGet();
			}
		}

		// -----------------------------------------------------
		// all other methods throw UnsupportedOperationException

		@Override
		public WorkDay getDisplayWorkDay() {
			throw new UnsupportedOperationException();
		}

		@Override
		public WorkYear getDisplayWorkYear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Database getDatabase() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void start() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void stop() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void workDayMousePressed(int slot, boolean shift) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void wordDayMouseDragged(int slot) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void workDayMouseReleased() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void workYearMouseClicked(int slot, int mode) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Date getDisplayDate() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setDisplayDate(Date date) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void incrementDisplayDate() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void decrementDisplayDate() {
			throw new UnsupportedOperationException();
		}

	}

}