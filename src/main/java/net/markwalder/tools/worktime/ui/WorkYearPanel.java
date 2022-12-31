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

import com.google.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.*;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

public class WorkYearPanel extends JPanel implements MouseListener, MouseMotionListener {

	private final int marginTop = 50;
	private final int marginLeft = 20;

	private final int slotWidth = 20;
	private final int slotHeight = 20;

	private final int padding = 20;

	public static final int MODE_NONE = 0;
	public static final int MODE_HOLIDAY = 1;
	public static final int MODE_VACATION = 3;
	public static final int MODE_FREE = 4;

	private static final Color COLOR_WEEKEND = new Color(175, 175, 175);
	private static final Color COLOR_HOLIDAY = new Color(250, 150, 150);
	private static final Color COLOR_COMPENSATION = new Color(225, 175, 125);
	private static final Color COLOR_VACATION = new Color(150, 200, 150);
	private static final Color COLOR_FREE = new Color(200, 200, 200);

	private int mode = MODE_NONE;

	private int mouseSlot = -1;

	private final transient Controller controller;
	private final transient Database database;

	@Inject
	public WorkYearPanel(Controller controller, Database database) {
		this.controller = controller;
		this.database = database;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		Dimension size = new Dimension(655, 760);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);

	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		Date date = controller.getDisplayDate();
		if (date == null) return;

		int year = DateTimeUtils.getYear(date);

		g2.translate(marginLeft, marginTop);

		g2.setFont(new Font("DejaVu", Font.BOLD, 16));
		g2.setColor(Color.BLACK);
		g2.drawString("Year " + year, 0, -24);

		Font font = new Font("DejaVu", Font.PLAIN, 11);
		g2.setFont(font);

		drawLegend(g2);
		drawLabels(g2, year);
		drawGrid(g2, year);

		WorkYear workYear = database.getWorkYear(date);
		if (workYear == null) return;

		fillGrid(g2, year, workYear);

		Date today = DateTimeUtils.getNow();
		if (year == DateTimeUtils.getYear(today)) {
			markToday(g2, today);
		}

	}

	private void drawLegend(Graphics2D g2) {

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
			int y = (month - 1) * (slotHeight * 2 + padding);

			g2.setColor(Color.GRAY);
			drawString(g2, String.valueOf(month), -4, y + slotHeight - 2, RIGHT, MIDDLE);

			int days = DateTimeUtils.getDaysInMonth(year, month);

			for (int day = 1; day <= days; day++) {
				int x = day * slotWidth - (slotWidth / 2);
				drawString(g2, String.valueOf(day), x, y - 4, MIDDLE, BOTTOM);
			}

			g2.setColor(Color.WHITE);
			g2.fillRect(0, y, days * slotWidth, 2 * slotHeight);

		}
	}

	private void drawGrid(Graphics2D g2, int year) {

		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int month = 1; month <= 12; month++) {
			int y = (month - 1) * (slotHeight * 2 + padding);

			int days = DateTimeUtils.getDaysInMonth(year, month);

			g2.setColor(Color.GRAY);
			for (int i = 0; i < 3; i++) {
				int w = days * slotWidth;
				int y2 = y + i * slotHeight;
				g2.drawLine(0, y2, w, y2);
			}

			for (int day = 0; day <= days; day++) {
				int x = day * slotWidth;
				int h = y + 2 * slotHeight;
				g2.drawLine(x, y, x, h);
			}

		}
	}

	private void fillGrid(Graphics2D g2, int year, WorkYear workYear) {

		int slot = -1;
		for (int month = 1; month <= 12; month++) {
			int days = DateTimeUtils.getDaysInMonth(year, month);
			int y = (month - 1) * (slotHeight * 2 + padding);

			for (int day = 1; day <= days; day++) {

				Date date = DateTimeUtils.getDate(year, month, day);
				boolean weekend = DateTimeUtils.isWeekend(date);

				for (int h = 0; h < 2; h++) {
					slot++;

					if (weekend) {
						g2.setColor(COLOR_WEEKEND);
						g2.fillRect((day - 1) * slotWidth + 1, y + h * slotHeight + 1, slotWidth - 1, slotHeight - 1);
						continue;
					}

					fillDay(g2, workYear, slot, day, y, h);
				}

			}

		}
	}

	private void fillDay(Graphics2D g2, WorkYear workYear, int slot, int day, int y, int h) {

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
			g2.fillRect((day - 1) * slotWidth + 2, y + h * slotHeight + 2, slotWidth - 3, slotHeight - 3);
		}

		if (mode != MODE_NONE && slot == mouseSlot) {
			boolean none = workYear.getValue(slot) == 0;
			boolean enabled = none || (mode == MODE_HOLIDAY && holiday || mode == MODE_VACATION && vacation || mode == MODE_FREE && free);
			if (enabled) {
				Stroke defaultStroke = g2.getStroke();
				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(2));
				g2.drawRect((day - 1) * slotWidth + 1, y + h * slotHeight + 1, slotWidth - 2, slotHeight - 2);
				g2.setStroke(defaultStroke);
			}
		}
	}

	private void markToday(Graphics2D g2, Date today) {

		int month = DateTimeUtils.getMonth(today);
		int day = DateTimeUtils.getDay(today);
		int hour = DateTimeUtils.getHour(today);

		int x = (day - 1) * slotWidth;
		int y = (month - 1) * (slotHeight * 2 + padding);
		if (hour >= 12) y += slotHeight;

		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2));
		g2.drawRect(x, y, slotWidth, slotHeight);
	}

	//---------------------------------------------------------------------------------
	// interface MouseListener

	@Override
	public void mouseClicked(MouseEvent e) {
		// ignore
	}

	@Override
	public void mousePressed(MouseEvent e) {

		// get relative mouse position relative
		int x = e.getX() - marginLeft;
		int y = e.getY() - marginTop;

		if (y > -40 && y < -24) {
			// click on legend

			if (x > 260 && x < 300) {
				mode = MODE_FREE;
			} else if (x > 320 && x < 410) {
				mode = MODE_HOLIDAY;
			} else if (x > 430 && x < 530) {
				mode = MODE_VACATION;
			}

			repaint();
			return;
		}

		// convert mouse coordinates into slot index
		int slot = getSlot(e);
		controller.workYearMouseClicked(slot, mode);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// ignore
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
		// ignore
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseSlot = getSlot(e);
		this.repaint();
	}

	//---------------------------------------------------------------------------------
	// private methods

	private int getSlot(MouseEvent e) {

		Date displayDate = controller.getDisplayDate();
		if (displayDate == null) return -1;

		int year = DateTimeUtils.getYear(displayDate);

		// get relative mouse position relative
		int x = e.getX() - marginLeft;
		int y = e.getY() - marginTop;

		if (y < 0) return -1;

		int month = y / (slotHeight * 2 + padding) + 1;
		if (month < 1 || month > 12) return -1;

		int daysInMonth = DateTimeUtils.getDaysInMonth(year, month);

		int day = x / slotWidth + 1;
		if (day < 1 || day > daysInMonth) return -1;

		Date date = DateTimeUtils.getDate(year, month, day);
		int dayOfYear = DateTimeUtils.getDayOfYear(date);

		int h = y - (month - 1) * (slotHeight * 2 + padding);
		if (h < slotHeight) {
			// AM
			return (dayOfYear - 1) * 2;
		} else if (h < 2 * slotHeight) {
			// PM
			return (dayOfYear - 1) * 2 + 1;
		} else {
			return -1;
		}

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
