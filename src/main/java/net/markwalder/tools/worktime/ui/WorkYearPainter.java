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

import static net.markwalder.tools.worktime.ui.WorkYearPanel.MODE_FREE;
import static net.markwalder.tools.worktime.ui.WorkYearPanel.MODE_HOLIDAY;
import static net.markwalder.tools.worktime.ui.WorkYearPanel.MODE_NONE;
import static net.markwalder.tools.worktime.ui.WorkYearPanel.MODE_VACATION;

import com.google.inject.Inject;
import java.awt.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

public class WorkYearPainter extends TimeTablePainter {

	private static final Color COLOR_WEEKEND = new Color(175, 175, 175);
	private static final Color COLOR_HOLIDAY = new Color(250, 150, 150);
	private static final Color COLOR_COMPENSATION = new Color(225, 175, 125);
	private static final Color COLOR_VACATION = new Color(150, 200, 150);
	private static final Color COLOR_FREE = new Color(200, 200, 200);

	private final Clock clock;

	@Inject
	public WorkYearPainter(Clock clock) {
		this.clock = clock;
	}

	public void paint(Graphics2D g2, WorkYear workYear, int mode, int mouseSlot) {

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(MARGIN_LEFT, MARGIN_TOP);

		LocalDate date = workYear.getDate();
		int year = date.getYear();

		g2.setFont(FONT_TITLE);
		g2.setColor(Color.BLACK);
		g2.drawString("Year " + year, 0, -24);

		g2.setFont(FONT_PLAIN);
		drawLegend(g2, mode);
		drawLabels(g2, year);
		drawGrid(g2, year);
		fillGrid(g2, year, workYear, mode, mouseSlot);

		LocalDateTime now = LocalDateTime.now(clock);
		if (year == now.getYear()) {
			markToday(g2, now);
		}

	}

	private void drawLegend(Graphics2D g2, int mode) {

		g2.setColor(Color.WHITE);
		g2.fillRect(100, -40, 16, 16);
		g2.fillRect(180, -40, 16, 16);
		g2.fillRect(260, -40, 16, 16);
		g2.fillRect(320, -40, 16, 16);
		g2.fillRect(430, -40, 16, 16);

		g2.setColor(Color.GRAY);
		g2.drawRect(100, -40, 16, 16);
		g2.drawRect(180, -40, 16, 16);
		g2.drawRect(260, -40, 16, 16);
		g2.drawRect(320, -40, 16, 16);
		g2.drawRect(430, -40, 16, 16);

		g2.setColor(COLOR_WEEKEND);
		g2.fillRect(181, -39, 15, 15);
		g2.setColor(COLOR_FREE);
		g2.fillRect(262, -38, 13, 13);
		g2.setColor(COLOR_HOLIDAY);
		g2.fillRect(322, -38, 13, 13);
		g2.setColor(COLOR_VACATION);
		g2.fillRect(432, -38, 13, 13);

		g2.setColor(Color.BLACK);
		drawString(g2, "Workday", 120, -34, LEFT, MIDDLE);
		drawString(g2, "Weekend", 200, -34, LEFT, MIDDLE);
		drawString(g2, "Free", 280, -34, LEFT, MIDDLE);
		drawString(g2, "Public Holiday", 340, -34, LEFT, MIDDLE);
		drawString(g2, "Private Vacation", 450, -34, LEFT, MIDDLE);

		Stroke defaultStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2));
		if (mode == MODE_FREE) {
			g2.drawRect(260, -40, 16, 16);
		} else if (mode == MODE_HOLIDAY) {
			g2.drawRect(320, -40, 16, 16);
		} else if (mode == MODE_VACATION) {
			g2.drawRect(430, -40, 16, 16);
		}
		g2.setStroke(defaultStroke);
	}

	private void drawLabels(Graphics2D g2, int year) {

		for (int month = 1; month <= 12; month++) {
			int y = (month - 1) * (SLOT_HEIGHT * 2 + PADDING);

			g2.setColor(Color.GRAY);
			drawString(g2, String.valueOf(month), -4, y + SLOT_HEIGHT - 2, RIGHT, MIDDLE);

			int days = DateTimeUtils.getDaysInMonth(year, month);

			for (int day = 1; day <= days; day++) {
				int x = day * SLOT_WIDTH - (SLOT_WIDTH / 2);
				drawString(g2, String.valueOf(day), x, y - 4, MIDDLE, BOTTOM);
			}

			g2.setColor(Color.WHITE);
			g2.fillRect(0, y, days * SLOT_WIDTH, 2 * SLOT_HEIGHT);

		}
	}

	private void drawGrid(Graphics2D g2, int year) {

		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int month = 1; month <= 12; month++) {
			int y = (month - 1) * (SLOT_HEIGHT * 2 + PADDING);

			int days = DateTimeUtils.getDaysInMonth(year, month);

			g2.setColor(Color.GRAY);
			for (int i = 0; i < 3; i++) {
				int w = days * SLOT_WIDTH;
				int y2 = y + i * SLOT_HEIGHT;
				g2.drawLine(0, y2, w, y2);
			}

			for (int day = 0; day <= days; day++) {
				int x = day * SLOT_WIDTH;
				int h = y + 2 * SLOT_HEIGHT;
				g2.drawLine(x, y, x, h);
			}

		}
	}

	private void fillGrid(Graphics2D g2, int year, WorkYear workYear, int mode, int mouseSlot) {

		int slot = -1;
		for (int month = 1; month <= 12; month++) {
			int days = DateTimeUtils.getDaysInMonth(year, month);
			int y = (month - 1) * (SLOT_HEIGHT * 2 + PADDING);

			for (int day = 1; day <= days; day++) {

				LocalDate date = LocalDate.of(year, month, day);
				boolean weekend = DateTimeUtils.isWeekend(date);

				for (int h = 0; h < 2; h++) {
					slot++;

					if (weekend) {
						g2.setColor(COLOR_WEEKEND);
						g2.fillRect((day - 1) * SLOT_WIDTH + 1, y + h * SLOT_HEIGHT + 1, SLOT_WIDTH - 1, SLOT_HEIGHT - 1);
						continue;
					}

					fillDay(g2, workYear, slot, day, y, h, mode, mouseSlot);
				}

			}

		}
	}

	private void fillDay(Graphics2D g2, WorkYear workYear, int slot, int day, int y, int h, int mode, int mouseSlot) {

		boolean holiday = workYear.isHoliday(slot);
		boolean compensation = workYear.isCompensation(slot);
		boolean vacation = workYear.isVacation(slot);
		boolean free = workYear.isFree(slot);

		Color color = null;
		if (holiday) {
			color = COLOR_HOLIDAY;
		} else if (compensation) {
			color = COLOR_COMPENSATION;
		} else if (vacation) {
			color = COLOR_VACATION;
		} else if (free) {
			color = COLOR_FREE;
		}
		if (color != null) {
			g2.setColor(color);
			g2.fillRect((day - 1) * SLOT_WIDTH + 2, y + h * SLOT_HEIGHT + 2, SLOT_WIDTH - 3, SLOT_HEIGHT - 3);
		}

		if (mode != MODE_NONE && slot == mouseSlot) {
			boolean none = workYear.getValue(slot) == 0;
			boolean enabled = none || (mode == MODE_HOLIDAY && holiday || mode == MODE_VACATION && vacation || mode == MODE_FREE && free);
			if (enabled) {
				Stroke defaultStroke = g2.getStroke();
				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(2));
				g2.drawRect((day - 1) * SLOT_WIDTH + 1, y + h * SLOT_HEIGHT + 1, SLOT_WIDTH - 2, SLOT_HEIGHT - 2);
				g2.setStroke(defaultStroke);
			}
		}
	}

	private void markToday(Graphics2D g2, LocalDateTime now) {

		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		int hour = now.getHour();

		int x = (day - 1) * SLOT_WIDTH;
		int y = (month - 1) * (SLOT_HEIGHT * 2 + PADDING);
		if (hour >= 12) y += SLOT_HEIGHT;

		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2));
		g2.drawRect(x, y, SLOT_WIDTH, SLOT_HEIGHT);
	}

}
