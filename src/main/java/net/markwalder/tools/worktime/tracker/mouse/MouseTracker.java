package net.markwalder.tools.worktime.tracker.mouse;

/**
 * A MouseTracker is used to get access to the current global mouse position.
 */
public interface MouseTracker {

	/**
	 * Get the current global mouse position.
	 *
	 * @return Mouse position, or <code>null</code> if the mouse position is not known.
	 */
	MousePosition getMousePosition();

}
