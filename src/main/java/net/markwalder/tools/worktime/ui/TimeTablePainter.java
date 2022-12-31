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

package net.markwalder.tools.worktime.ui;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

abstract class TimeTablePainter {

	public static final int MARGIN_TOP = 50;
	public static final int MARGIN_LEFT = 20;

	public static final int SLOT_WIDTH = 20;
	public static final int SLOT_HEIGHT = 20;
	public static final int PADDING = 20;

	protected static final String FONT = "DejaVu";
	protected static final Font FONT_TITLE = new Font(FONT, Font.BOLD, 16);
	protected static final Font FONT_BOLD = new Font(FONT, Font.BOLD, 12);
	protected static final Font FONT_PLAIN = new Font(FONT, Font.PLAIN, 11);

	protected static final Locale LOCALE = Locale.US;

	protected static final int LEFT = -1;
	protected static final int CENTER = 0;
	protected static final int RIGHT = 1;

	protected static final int BOTTOM = -1;
	protected static final int MIDDLE = 0;
	protected static final int TOP = 1;

	protected void drawString(Graphics2D g2, String text, int x, int y, int alignX, int alignY) {
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(text, g2);
		if (alignX == CENTER) {
			x = (int) (x - bounds.getWidth() / 2);
		} else if (alignX == RIGHT) {
			x = (int) (x - bounds.getWidth());
		}
		if (alignY == MIDDLE) {
			y = (int) (y + bounds.getHeight() / 2);
		} else if (alignY == TOP) {
			y = (int) (y + bounds.getHeight());
		}
		g2.drawString(text, x, y);
	}

}
