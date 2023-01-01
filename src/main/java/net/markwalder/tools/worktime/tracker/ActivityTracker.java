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

package net.markwalder.tools.worktime.tracker;

/**
 * An ActivityTracker is used to check if the user is currently working
 * at the computer. The tracker can be started with {@link #start()} and
 * stopped with {@link #stop()}. It has to report the current state in
 * regular intervals to the given {@link ActivityListener}.
 * <p/>
 * "regular interval" means at least once every 5 minutes.
 */
public abstract class ActivityTracker {

	private final ActivityListener activityListener;

	protected ActivityTracker(ActivityListener activityListener) {
		if (activityListener == null) throw new IllegalArgumentException("activityListener == null");
		this.activityListener = activityListener;
	}

	/**
	 * Subclasses call this method to notify the {@link ActivityListener}.
	 *
	 * @param active <code>true</code> if the user is active, <code>false</code> otherwise.
	 */
	protected void reportActive(boolean active) {
		activityListener.reportActive(active);
	}

	/**
	 * Start activity tracking.
	 */
	public abstract void start();

	/**
	 * Stop activity tracking.
	 */
	public abstract void stop();

}
