package net.markwalder.tools.worktime.tracker;

/**
 * Listener interface for user activities.
 */
public interface ActivityListener {

	/**
	 * This method is invoked in regular intervals and tells the
	 * listener whether the user is current active or not.
	 *
	 * @param active <code>true</code> if the user is currently active,
	 *               <code>false</code> otherwise.
	 */
	void reportActive(boolean active);

}
