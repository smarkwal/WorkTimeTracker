package net.markwalder.tools.worktime;

import java.awt.*;

public class ActivityTracker implements Runnable {

	private final Controller controller;
	private final MouseTracker mouseTracker;
	private final long pollInterval = 15 * 1000; // 15 seconds // TODO: make configurable

	private Thread thread = null;
	private volatile boolean stop = false;
	private Point lastPosition = null;

	public ActivityTracker(Controller controller, MouseTracker mouseTracker) {
		if (controller == null) throw new IllegalArgumentException("controller == null");
		if (mouseTracker == null) throw new IllegalArgumentException("mouseTracker == null");
		this.controller = controller;
		this.mouseTracker = mouseTracker;
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
		Point position = mouseTracker.getMousePosition();

		if (position == null) return false;

		// check if mouse has been moved
		boolean moved = lastPosition != null && !position.equals(lastPosition);

		// remember last position
		lastPosition = position;

		return moved;
	}

}
