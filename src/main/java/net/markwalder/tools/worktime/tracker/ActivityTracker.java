package net.markwalder.tools.worktime.tracker;

import net.markwalder.tools.worktime.Controller;

public class ActivityTracker implements Runnable {

	private final Controller controller;
	private final MouseTracker mouseTracker;
	private long pollInterval = 15 * 1000; // 15 seconds

	private Thread thread = null;
	private volatile boolean stop = false;
	private MousePosition lastPosition = null;

	public ActivityTracker(Controller controller, MouseTracker mouseTracker) {
		if (controller == null) throw new IllegalArgumentException("controller == null");
		if (mouseTracker == null) throw new IllegalArgumentException("mouseTracker == null");
		this.controller = controller;
		this.mouseTracker = mouseTracker;
	}

	/**
	 * Set poll interval for mouse tracker in milliseconds.
	 *
	 * @param pollInterval Poll interval in milliseconds
	 */
	public void setPollInterval(long pollInterval) {
		this.pollInterval = pollInterval;
	}

	public void start() {
		if (thread != null) return;
		thread = new Thread(this);
		thread.setName("ActivityTracker");
		thread.setDaemon(false);
		thread.start();
	}

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
				controller.reportActive(active);

				// sleep
				try {
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
