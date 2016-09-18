package net.markwalder.tools.worktime.tracker;

import java.awt.*;

public class MousePosition {

	private final int x;
	private final int y;

	public MousePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MousePosition(Point point) {
		this(point.x, point.y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
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
