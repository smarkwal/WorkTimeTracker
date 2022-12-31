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

package net.markwalder.tools.worktime.tracker.mouse;

import com.google.inject.Inject;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import net.markwalder.tools.worktime.tracker.ActivityTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MouseActivityTrackerImpl extends ActivityTracker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MouseActivityTrackerImpl.class);

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
			Thread.currentThread().interrupt();
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

				waitForNextCheck();
			}

		} catch (Exception e) {
			LOGGER.error("Unexpected error in activity tracker.", e);
		}

	}

	private void waitForNextCheck() {
		try {
			Thread.sleep(pollInterval);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
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
