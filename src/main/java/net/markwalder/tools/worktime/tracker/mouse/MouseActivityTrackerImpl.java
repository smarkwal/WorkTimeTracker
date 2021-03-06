package net.markwalder.tools.worktime.tracker.mouse;

import com.google.inject.Inject;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import net.markwalder.tools.worktime.tracker.ActivityTracker;

public class MouseActivityTrackerImpl extends ActivityTracker implements Runnable {

	private static final int DEFAULT_POLL_INTERVAL = 15 * 1000; // 15 seconds

	private final MouseTracker mouseTracker;

	private long pollInterval = DEFAULT_POLL_INTERVAL;

	private Thread thread = null;
	private volatile boolean stop = false;
	private MousePosition lastPosition = null;

	@Inject
	public MouseActivityTrackerImpl(ActivityListener listener, MouseTracker mouseTracker) {
		super(listener);
		this.mouseTracker = mouseTracker;
	}

	/**
	 * Set poll interval for mouse tracker in milliseconds.
	 *
	 * @param pollInterval Poll interval in milliseconds
	 */
	void setPollInterval(long pollInterval) {
		this.pollInterval = pollInterval;
	}

	@Override
	public void start() {
		if (thread != null) return;
		thread = new Thread(this);
		thread.setName("ActivityTracker");
		thread.setDaemon(false);
		thread.start();
	}

	@Override
	public void stop() {
		if (thread == null) return;
		stop = true;
		thread.interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// ignore
		}
		thread = null;
	}

	@Override
	public void run() {

		try {

			stop = false;
			while (!stop) {

				// check if user is active (mouse has been moved)
				boolean active = mouseHasBeenMoved();

				// report result to controller
				reportActive(active);

				// sleep
				try {
					//noinspection BusyWait
					Thread.sleep(pollInterval);
				} catch (InterruptedException e) {
					// ignore
				}

			}

		} catch (Exception e) {
			System.err.println("unexpected error: " + e.toString());
			// todo: show error dialog
		}

	}

	/**
	 * Check if the mouse has been moved (position has changed)
	 * since the last time this method has been invoked.
	 *
	 * @return <code>true</code> if the mouse has been moved, <code>false</code> otherwise.
	 */
	private boolean mouseHasBeenMoved() {

		// get current mouse position
		MousePosition position = mouseTracker.getMousePosition();

		if (position == null) return false;

		// check if mouse has been moved
		boolean moved = lastPosition != null && !position.equals(lastPosition);

		// remember last position
		lastPosition = position;

		return moved;
	}

}
