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

import com.google.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseUtils;
import net.markwalder.tools.worktime.db.Statistics;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.apache.commons.lang3.time.DateUtils;

@SuppressWarnings("SameParameterValue")
public class WorkDayPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final int MARGIN_TOP = 50;
	private static final int MARGIN_LEFT = 20;

	private static final int SLOT_WIDTH = 20;
	private static final int SLOT_HEIGHT = 20;

	private static final Color RUNNING = new Color(204, 204, 204);
	private static final Color ACTIVE = new Color(128, 175, 204);
	private static final Color NOW = new Color(82, 111, 180);
	private static final Color REMAINING = new Color(0, 128, 0);

	private static final BasicStroke LINE_1 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final BasicStroke LINE_5 = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	private static final String FONT = "DejaVu";
	private static final Font FONT_TITLE = new Font(FONT, Font.BOLD, 16);
	private static final Font FONT_BOLD = new Font(FONT, Font.BOLD, 12);
	private static final Font FONT_PLAIN = new Font(FONT, Font.PLAIN, 11);

	private static final Locale LOCALE = Locale.US;

	private final transient Controller controller;
	private final transient Database database;

	@Inject
	public WorkDayPanel(Controller controller, Database database) {
		this.controller = controller;
		this.database = database;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		Dimension size = new Dimension(420, 540);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);

	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		WorkDay workDay = controller.getDisplayWorkDay();
		int hours = workDay != null ? (workDay.getSize() / 12) : 24;

		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.translate(MARGIN_LEFT, MARGIN_TOP);

		Date today = DateTimeUtils.getToday();

		drawTitle(0, -24, today, g2);

		g2.setFont(FONT_PLAIN);

		drawGrid(hours, g2);
		drawBorders(hours, g2);
		if (workDay == null) return;

		Date date = workDay.getDate();

		int nowSlot = -1;
		if (date.equals(today)) {
			nowSlot = DatabaseUtils.slot(System.currentTimeMillis() - today.getTime());
		}

		fillSlots(workDay, nowSlot, hours, g2);

		// statistics

		int workingCount = workDay.getWorkingCount();
		int freeCount = workDay.getFreeCount();

		int x = 12 * SLOT_WIDTH + 20;

		drawStatsForDay(x, 5, workingCount, freeCount, date, today, g2);
		drawStatsForWeek(x, 105, date, today, g2);
		drawStatsForMonth(x, 205, date, today, g2);
		drawStatsForYear(x, 305, date, today, g2);
	}

	private void drawTitle(int x, int y, Date today, Graphics2D g2) {

		Date displayDate = controller.getDisplayDate();
		if (displayDate != null) {

			g2.setFont(FONT_TITLE);
			g2.setColor(BLACK);

			SimpleDateFormat dateFormat;
			if (displayDate.equals(today)) {
				dateFormat = new SimpleDateFormat("'Today' | EEEE", LOCALE);
			} else if (displayDate.equals(DateUtils.addDays(today, 1))) {
				dateFormat = new SimpleDateFormat("'Tomorrow' | EEEE", LOCALE);
			} else if (displayDate.equals(DateUtils.addDays(today, -1))) {
				dateFormat = new SimpleDateFormat("'Yesterday' | EEEE", LOCALE);
			} else {
				dateFormat = new SimpleDateFormat("dd.MM.yyyy | EEEE", LOCALE);
			}

			g2.drawString(dateFormat.format(displayDate), x, y);
		}
	}

	private void drawGrid(int hours, Graphics2D g2) {

		int width = 12 * SLOT_WIDTH;
		int height = hours * SLOT_HEIGHT;

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
		for (int h = 0; h < hours; h++) {
			int y = h * SLOT_HEIGHT;
			int hour = h;
			if (hours == 23 && h >= 3) {
				hour = h + 1;
			} else if (hours == 25 && h >= 3) {
				hour = h - 1;
			}
			String label = String.valueOf(hour);
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
		for (int h = 0; h <= hours; h++) {
			int y = h * SLOT_HEIGHT;
			g2.drawLine(0, y, width, y);
		}
	}

	private void drawBorders(int hours, Graphics2D g2) {

		g2.setColor(RED);
		g2.setStroke(LINE_1);
		int width = 12 * SLOT_WIDTH;

		if (hours == 24) {
			g2.drawLine(0, 8 * SLOT_WIDTH, width, 8 * SLOT_WIDTH);
			g2.drawLine(0, 12 * SLOT_WIDTH, width, 12 * SLOT_WIDTH);
			g2.drawLine(0, 17 * SLOT_WIDTH, width, 17 * SLOT_WIDTH);
		} else if (hours == 23) {
			g2.drawLine(0, 7 * SLOT_WIDTH, width, 7 * SLOT_WIDTH);
			g2.drawLine(0, 11 * SLOT_WIDTH, width, 11 * SLOT_WIDTH);
			g2.drawLine(0, 16 * SLOT_WIDTH, width, 16 * SLOT_WIDTH);
		} else if (hours == 25) {
			g2.drawLine(0, 9 * SLOT_WIDTH, width, 9 * SLOT_WIDTH);
			g2.drawLine(0, 13 * SLOT_WIDTH, width, 13 * SLOT_WIDTH);
			g2.drawLine(0, 18 * SLOT_WIDTH, width, 18 * SLOT_WIDTH);
		}
	}

	private void fillSlots(WorkDay workDay, int nowSlot, int hours, Graphics2D g2) {

		for (int h = 0; h < hours; h++) {
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

	private void drawStatsForDay(int x, int y, int workingCount, int freeCount, Date date, Date today, Graphics2D g2) {

		drawStats(x, y, date, today, g2, date, workingCount, "Day", DatabaseUtils.getWorkTime(database, date), freeCount);
	}

	private void drawStatsForWeek(int x, int y, Date date, Date today, Graphics2D g2) {

		// create statistics for the current week
		Date startDate = DateTimeUtils.getStartOfWeek(date);
		Date endDate = DateTimeUtils.getEndOfWeek(date);
		Statistics statistics = Statistics.getStatistics(database, startDate, endDate);
		int workingCount = statistics.getWorkingCount();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Week", statistics.getWorkTime(), statistics.getFreeCount());
	}

	private void drawStatsForMonth(int x, int y, Date date, Date today, Graphics2D g2) {

		// create statistics for the current month
		Date startDate = DateTimeUtils.getStartOfMonth(date);
		Date endDate = DateTimeUtils.getEndOfMonth(date);
		Statistics statistics = Statistics.getStatistics(database, startDate, endDate);
		int workingCount = statistics.getWorkingCount();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Month", statistics.getWorkTime(), statistics.getFreeCount());
	}

	private void drawStatsForYear(int x, int y, Date date, Date today, Graphics2D g2) {

		// create statistics for the current year
		Date startDate = DateTimeUtils.getStartOfYear(date);
		Date endDate = DateTimeUtils.getEndOfYear(date);
		Statistics statistics = Statistics.getStatistics(database, startDate, endDate);
		int workingCount = statistics.getWorkingCount();

		drawStats(x, y, date, today, g2, startDate, workingCount, "Year", statistics.getWorkTime(), statistics.getFreeCount());
	}

	private void drawStats(int x, int y, Date date, Date today, Graphics2D g2, Date startDate, int workingCount, String label, int workTime, int freeCount) {

		g2.setColor(new Color(245, 245, 245));
		g2.fillRect(x - 5, y - 5, 130, 86);

		g2.setFont(FONT_BOLD);
		g2.setColor(BLACK);
		drawString(g2, label, x, y + 12);

		if (workingCount > 0 || startDate.compareTo(today) <= 0) {

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
					Date now = DateTimeUtils.getNow();
					Date end = DateTimeUtils.addMinutes(now, remainingTime);
					g2.setColor(DARK_GRAY);
					SimpleDateFormat format = new SimpleDateFormat("H:mm");
					drawString(g2, "End @ " + format.format(end), x + 117, y + 12, RIGHT, BOTTOM);
				}
			}

		}
	}

	//---------------------------------------------------------------------------------
	// interface MouseListener

	@Override
	public void mouseClicked(MouseEvent e) {
		// ignore
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// convert mouse coordinates into slot index
		int slot = getSlot(e);
		boolean shift = e.isShiftDown();
		controller.workDayMousePressed(slot, shift);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		controller.workDayMouseReleased();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// ignore
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// ignore
	}

	//---------------------------------------------------------------------------------
	// interface MouseMotionListener

	@Override
	public void mouseDragged(MouseEvent e) {
		// convert mouse coordinates into slot index
		int slot = getSlot(e);
		controller.workDayMouseDragged(slot);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// ignore
	}

	//---------------------------------------------------------------------------------
	// private methods

	private int getSlot(MouseEvent e) {

		// get relative mouse position relative
		int x = e.getX() - MARGIN_LEFT;
		int y = e.getY() - MARGIN_TOP;

		// check bounds of table
		if (x < 0 || x >= 12 * SLOT_WIDTH) return -1;
		if (y < 0 || y >= 25 * SLOT_HEIGHT) return -1;

		// convert pixel coordinates into slot coordinates
		int h = y / SLOT_HEIGHT;
		int s = x / SLOT_WIDTH;

		// calculate slot index
		return h * 12 + s;
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

	private static final int LEFT = -1;
	private static final int CENTER = 0;
	private static final int RIGHT = 1;

	private static final int BOTTOM = -1;
	private static final int MIDDLE = 0;
	private static final int TOP = 1;

	private void drawString(Graphics2D g2, String text, int x, int y, int alignX, int alignY) {
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
