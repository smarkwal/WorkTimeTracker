package net.markwalder.tools.worktime.tracker.mouse;

import java.awt.*;

/**
 * Global mouse position with x and y coordinates.
 */
class MousePosition {

	private final int x;
	private final int y;

	MousePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	MousePosition(Point point) {
		this(point.x, point.y);
	}

	int getX() {
		return x;
	}

	int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MousePosition point = (MousePosition) obj;
		return (point.x == x) && (point.y == y);
	}

}
