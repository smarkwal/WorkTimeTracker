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
