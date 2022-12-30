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

import java.awt.*;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

}
