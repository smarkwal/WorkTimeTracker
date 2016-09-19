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

	// TODO: invert the communication pattern
	// so that the tracker is asked for the last time
	// the user has been active.

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
