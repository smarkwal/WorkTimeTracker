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

import static java.awt.Color.BLACK;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.GRAY;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import java.awt.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.inject.Inject;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.Statistics;
import net.markwalder.tools.worktime.db.TimeTableUtils;
import net.markwalder.tools.worktime.db.WorkContract;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

@SuppressWarnings("SameParameterValue")
public class WorkDayPainter extends TimeTablePainter {

	private static final Color RUNNING = new Color(204, 204, 204);
	private static final Color ACTIVE = new Color(128, 175, 204);
	private static final Color NOW = new Color(82, 111, 180);
	private static final Color REMAINING = new Color(0, 128, 0);

	private static final BasicStroke LINE_1 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final BasicStroke LINE_5 = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	private final WorkContract workContract;
	private final Database database;
	private final Clock clock;

	@Inject
	public WorkDayPainter(WorkContract workContract, Database database, Clock clock) {
		this.workContract = workContract;
		this.database = database;
		this.clock = clock;
	}

	public void paint(Graphics2D g2, WorkDay workDay) {

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(MARGIN_LEFT, MARGIN_TOP);

		LocalDate today = LocalDate.now(clock);
		LocalDate date = workDay.getDate();

		drawTitle(0, -24, date, today, g2);

		g2.setFont(FONT_PLAIN);
		drawGrid(g2);
		drawBorders(g2);

		int nowSlot = -1;
		if (date.equals(today)) {
			LocalDateTime now = LocalDateTime.now(clock);
			nowSlot = TimeTableUtils.slot(now);
		}

		fillSlots(workDay, nowSlot, g2);

		// statistics

		int workingCount = workDay.getWorkingCount();
		int freeCount = workDay.getFreeCount();
		int x = 12 * SLOT_WIDTH + 20;

		drawStatsForDay(x, 5, workingCount, freeCount, date, today, g2);
		drawStatsForWeek(x, 105, date, today, g2);
		drawStatsForMonth(x, 205, date, today, g2);
		drawStatsForYear(x, 305, date, today, g2);
	}

	private void drawTitle(int x, int y, LocalDate date, LocalDate today, Graphics2D g2) {

		g2.setFont(FONT_TITLE);
		g2.setColor(BLACK);

		String datePrefix;
		DateTimeFormatter dateFormat;
		if (date.equals(today)) {
			datePrefix = "Today | ";
			dateFormat = DateTimeFormatter.ofPattern("EEEE", LOCALE);
		} else if (date.equals(today.plusDays(1))) {
			datePrefix = "Tomorrow | ";
			dateFormat = DateTimeFormatter.ofPattern("EEEE", LOCALE);
		} else if (date.equals(today.minusDays(1))) {
			datePrefix = "Yesterday | ";
			dateFormat = DateTimeFormatter.ofPattern("EEEE", LOCALE);
		} else {
			datePrefix = "";
			dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy | EEEE", LOCALE);
		}

		g2.drawString(datePrefix + dateFormat.format(date), x, y);
	}

	private void drawGrid(Graphics2D g2) {

		int width = 12 * SLOT_WIDTH;
		int height = 24 * SLOT_HEIGHT;

		for (int s = 1; s < 12; s++) {
			int x = s * SLOT_WIDTH;
			if (s % 3 == 0) {
				g2.setColor(GRAY);
			} else {
				g2.setColor(LIGHT_GRAY);
			}
			drawString(g2, String.valueOf(s * 5), x, -4, MIDDLE, BOTTOM);
		}

		g2.setColor(GRAY);
		for (int h = 0; h < 24; h++) {
			int y = h * SLOT_HEIGHT;
			String label = String.valueOf(h);
			drawString(g2, label, -4, y + 10, RIGHT, MIDDLE);
		}

		g2.setColor(WHITE);
		g2.fillRect(0, 0, width, height);

		g2.setStroke(LINE_1);
		for (int s = 0; s <= 12; s++) {
			int x = s * SLOT_WIDTH;
			if (s % 3 == 0) {
				g2.setColor(GRAY);
			} else {
				g2.setColor(LIGHT_GRAY);
			}
			g2.drawLine(x, 0, x, height);
		}

		g2.setColor(GRAY);
		for (int h = 0; h <= 24; h++) {
			int y = h * SLOT_HEIGHT;
			g2.drawLine(0, y, width, y);
		}
	}

	private void drawBorders(Graphics2D g2) {

		g2.setColor(RED);
		g2.setStroke(LINE_1);
		int width = 12 * SLOT_WIDTH;

		g2.drawLine(0, 8 * SLOT_WIDTH, width, 8 * SLOT_WIDTH);
		g2.drawLine(0, 12 * SLOT_WIDTH, width, 12 * SLOT_WIDTH);
		g2.drawLine(0, 17 * SLOT_WIDTH, width, 17 * SLOT_WIDTH);
	}

	@SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high
	private void fillSlots(WorkDay workDay, int nowSlot, Graphics2D g2) {

		for (int h = 0; h < 24; h++) {
			for (int s = 0; s < 12; s++) {

				int x = s * SLOT_WIDTH;
				int y = h * SLOT_HEIGHT;

				int slot = h * 12 + s;
				boolean running = workDay.isRunning(slot);
				boolean active = workDay.isActive(slot);
				boolean working = workDay.isWorking(slot);
				boolean free = workDay.isFree(slot);

				Color color = WHITE;
				if (running) {
					if (active) {
						color = ACTIVE;
					} else {
						color = RUNNING;
					}
				}
				g2.setColor(color);
				g2.fillRect(x + 2, y + 2, SLOT_WIDTH - 3, SLOT_HEIGHT - 3);

				if (working || free) {
					if (working) {
						g2.setColor(DARK_GRAY);
					} else {
						g2.setColor(GRAY);
					}
					g2.setStroke(LINE_5);
					g2.drawLine(x + 5, y + 5, x + SLOT_WIDTH - 5, y + SLOT_HEIGHT - 5);
					g2.drawLine(x + 5, y + SLOT_HEIGHT - 5, x + SLOT_WIDTH - 5, y + 5);
				}

				if (slot == nowSlot) {
					g2.setColor(NOW);
					g2.fillOval(x + 5, y + 5, SLOT_WIDTH - 9, SLOT_HEIGHT - 9);
				}

			}
		}
	}

	private void drawStatsForDay(int x, int y, int workingCount, int freeCount, LocalDate date, LocalDate today, Graphics2D g2) {

		int workTime = workContract.getWorkTime(date, database);

		drawStats(x, y, date, today, g2, date, workingCount, "Day", workTime, freeCount);
	}

	private void drawStatsForWeek(int x, int y, LocalDate date, LocalDate today, Graphics2D g2) {

		// create statistics for the current week
		LocalDate startDate = DateTimeUtils.getStartOfWeek(date);
		LocalDate endDate = DateTimeUtils.getEndOfWeek(date);
		Statistics statistics = Statistics.getStatistics(startDate, endDate, today, workContract, database);
		int workingCount = statistics.getWorkingCount();
		int freeCount = statistics.getFreeCount();
		int workTime = statistics.getWorkTime();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Week", workTime, freeCount);
	}

	private void drawStatsForMonth(int x, int y, LocalDate date, LocalDate today, Graphics2D g2) {

		// create statistics for the current month
		LocalDate startDate = DateTimeUtils.getStartOfMonth(date);
		LocalDate endDate = DateTimeUtils.getEndOfMonth(date);
		Statistics statistics = Statistics.getStatistics(startDate, endDate, today, workContract, database);
		int workingCount = statistics.getWorkingCount();
		int freeCount = statistics.getFreeCount();
		int workTime = statistics.getWorkTime();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Month", workTime, freeCount);
	}

	private void drawStatsForYear(int x, int y, LocalDate date, LocalDate today, Graphics2D g2) {

		// create statistics for the current year
		LocalDate startDate = DateTimeUtils.getStartOfYear(date);
		LocalDate endDate = DateTimeUtils.getEndOfYear(date);
		Statistics statistics = Statistics.getStatistics(startDate, endDate, today, workContract, database);
		int workingCount = statistics.getWorkingCount();
		int freeCount = statistics.getFreeCount();
		int workTime = statistics.getWorkTime();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Year", workTime, freeCount);
	}

	@SuppressWarnings({
			"java:S107", // Methods should not have too many parameters
			"java:S1066" // Collapsible "if" statements should be merged
	})
	private void drawStats(int x, int y, LocalDate date, LocalDate today, Graphics2D g2, LocalDate startDate, int workingCount, String label, int workTime, int freeCount) {

		g2.setColor(new Color(245, 245, 245));
		g2.fillRect(x - 5, y - 5, 130, 86);

		g2.setFont(FONT_BOLD);
		g2.setColor(BLACK);
		drawString(g2, label, x, y + 12);

		if (workingCount > 0 || !startDate.isAfter(today)) {

			workTime = workTime - freeCount * 5;

			g2.setColor(WHITE);
			g2.fillRect(x + 60, y + 20, 60, 16);
			g2.fillRect(x + 60, y + 40, 60, 16);
			g2.fillRect(x + 60, y + 60, 60, 16);

			g2.setFont(FONT_PLAIN);

			g2.setColor(DARK_GRAY);
			drawString(g2, "Total", x + 3, y + 33, LEFT, BOTTOM);
			drawString(g2, formatTime(workTime), x + 117, y + 33, RIGHT, BOTTOM);

			g2.setColor(DARK_GRAY);
			drawString(g2, "Worked", x + 3, y + 53, LEFT, BOTTOM);
			drawString(g2, formatSlots(workingCount), x + 117, y + 53, RIGHT, BOTTOM);

			g2.setColor(DARK_GRAY);
			drawString(g2, "Flexi", x + 3, y + 73, LEFT, BOTTOM);
			int remainingTime = workTime - workingCount * 5;
			if (remainingTime > 0) {
				g2.setColor(RED);
			} else if (remainingTime < 0) {
				g2.setColor(REMAINING);
			} else {
				g2.setColor(GRAY);
			}
			drawString(g2, formatTimeDiff(-remainingTime), x + 117, y + 73, RIGHT, BOTTOM);

			if (date.equals(today)) {
				if (remainingTime > 0 && remainingTime < 24 * 60) {
					LocalDateTime now = LocalDateTime.now(clock);
					LocalDateTime end = now.plusMinutes(remainingTime);
					g2.setColor(DARK_GRAY);
					DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");
					drawString(g2, "End @ " + format.format(end), x + 117, y + 12, RIGHT, BOTTOM);
				}
			}

		}
	}

	private String formatSlots(int slots) {
		return formatTime(slots * 5);
	}

	private String formatTime(int time) {
		int hours = time / 60;
		int minutes = time % 60;
		return String.format("%d:%02d", hours, minutes);
	}

	private String formatTimeDiff(int time) {
		int hours = Math.abs(time) / 60;
		int minutes = Math.abs(time) % 60;
		String text = String.format("%d:%02d", hours, minutes);
		if (time > 0) {
			return "+ " + text;
		} else if (time < 0) {
			return "- " + text;
		} else {
			return "\u00B1 " + text;
		}
	}

	private void drawString(Graphics2D g2, String text, int x, int y) {
		g2.drawString(text, x, y);
	}

}
