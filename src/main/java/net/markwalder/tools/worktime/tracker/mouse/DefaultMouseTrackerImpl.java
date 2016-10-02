package net.markwalder.tools.worktime.tracker.mouse;

import java.awt.*;

/**
 * Mouse tracker based on {@link java.awt.MouseInfo}.
 */
public class DefaultMouseTrackerImpl implements MouseTracker {

	public MousePosition getMousePosition() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		if (pointerInfo == null) return null;
		Point point = pointerInfo.getLocation();
		if (point == null) return null;
		return new MousePosition(point);
	}

}
