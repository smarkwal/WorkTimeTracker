package net.markwalder.tools.worktime;

import java.awt.*;

public class DefaultMouseTracker implements MouseTracker {

	public Point getMousePosition() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		if (pointerInfo == null) return null;
		return pointerInfo.getLocation();
	}

}
