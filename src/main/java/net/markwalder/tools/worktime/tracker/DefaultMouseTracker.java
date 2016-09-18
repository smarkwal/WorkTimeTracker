package net.markwalder.tools.worktime.tracker;

import java.awt.*;

public class DefaultMouseTracker implements MouseTracker {

	public MousePosition getMousePosition() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		if (pointerInfo == null) return null;
		Point point = pointerInfo.getLocation();
		if (point == null) return null;
		return new MousePosition(point);
	}

}
