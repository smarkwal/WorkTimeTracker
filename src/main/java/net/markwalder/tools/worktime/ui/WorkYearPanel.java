package net.markwalder.tools.worktime.ui;

import com.google.inject.Inject;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

public class WorkYearPanel extends JPanel implements MouseListener, MouseMotionListener {

	private int marginTop = 50;
	private int marginLeft = 20;

	private int slotWidth = 20;
	private int slotHeight = 20;

	private int padding = 20;

	public static final int MODE_NONE = 0;
	public static final int MODE_HOLIDAY = 1;
	public static final int MODE_COMPENSATION = 2;
	public static final int MODE_VACATION = 3;
	public static final int MODE_FREE = 4;

	private int mode = MODE_NONE;

	private int mouseSlot = -1;

	private final Controller controller;
	private final Database database;

	@Inject
	public WorkYearPanel(Controller controller, Database database) {
		this.controller = controller;
		this.database = database;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		Dimension size = new Dimension(800, 800);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);

	}

	@Override
	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color COLOR_WEEKEND = new Color(175, 175, 175);
		Color COLOR_HOLIDAY = new Color(250, 150, 150);
		Color COLOR_COMPENSATION = new Color(225, 175, 125);
		Color COLOR_VACATION = new Color(150, 200, 150);
		Color COLOR_FREE = new Color(200, 200, 200);

		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		Date date = controller.getDisplayDate();
		if (date == null) return;

		int year = DateTimeUtils.getYear(date);

		g2.translate(marginLeft, marginTop);

		Date displayDate = controller.getDisplayDate();
		if (displayDate != null) {
			g2.setFont(new Font("Arial", Font.BOLD, 16));
			g2.setColor(Color.BLACK);
			g2.drawString("Year " + year, 0, -24);
		}

		Font font = new Font("Arial", Font.PLAIN, 11);
		g2.setFont(font);

		// draw legend

		g2.setColor(Color.WHITE);
		g2.fillRect(100, -40, 16, 16);
		g2.fillRect(180, -40, 16, 16);
		g2.fillRect(260, -40, 16, 16);
		g2.fillRect(320, -40, 16, 16);
		g2.fillRect(420, -40, 16, 16);
		g2.fillRect(520, -40, 16, 16);

		g2.setColor(Color.GRAY);
		g2.drawRect(100, -40, 16, 16);
		g2.drawRect(180, -40, 16, 16);
		g2.drawRect(260, -40, 16, 16);
		g2.drawRect(320, -40, 16, 16);
		g2.drawRect(420, -40, 16, 16);
		g2.drawRect(520, -40, 16, 16);

		g2.setColor(COLOR_WEEKEND);
		g2.fillRect(181, -39, 15, 15);
		g2.setColor(COLOR_FREE);
		g2.fillRect(262, -38, 13, 13);
		g2.setColor(COLOR_HOLIDAY);
		g2.fillRect(322, -38, 13, 13);
		g2.setColor(COLOR_COMPENSATION);
		g2.fillRect(422, -38, 13, 13);
		g2.setColor(COLOR_VACATION);
		g2.fillRect(522, -38, 13, 13);

		g2.setColor(Color.BLACK);
		drawString(g2, "Workday", 120, -34, LEFT, MIDDLE);
		drawString(g2, "Weekend", 200, -34, LEFT, MIDDLE);
		drawString(g2, "Free", 280, -34, LEFT, MIDDLE);
		drawString(g2, "Public Holiday", 340, -34, LEFT, MIDDLE);
		drawString(g2, "Compensation", 440, -34, LEFT, MIDDLE);
		drawString(g2, "Private Vacation", 540, -34, LEFT, MIDDLE);

		Stroke defaultStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2));
		if (mode == MODE_FREE) {
			g2.drawRect(260, -40, 16, 16);
		} else if (mode == MODE_HOLIDAY) {
			g2.drawRect(320, -40, 16, 16);
		} else if (mode == MODE_COMPENSATION) {
			g2.drawRect(420, -40, 16, 16);
		} else if (mode == MODE_VACATION) {
			g2.drawRect(520, -40, 16, 16);
		}
		g2.setStroke(defaultStroke);

		// draw grid

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

		WorkYear workYear = database.getWorkYear(date);
		if (workYear == null) return;

		int weekendTotal = 0;
		int holidayTotal = 0;
		int compensationTotal = 0;
		int vacationTotal = 0;
		int freeTotal = 0;
		int workTotal = 0;

		int xs = 31 * slotWidth + 40;

		int slot = -1;
		for (int month = 1; month <= 12; month++) {
			int days = DateTimeUtils.getDaysInMonth(year, month);
			int y = (month - 1) * (slotHeight * 2 + padding);

			int weekendCount = 0;
			int holidayCount = 0;
			int compensationCount = 0;
			int vacationCount = 0;
			int freeCount = 0;

			for (int day = 1; day <= days; day++) {

				boolean weekend = DateTimeUtils.isWeekend(DateTimeUtils.getDate(year, month, day));

				for (int h = 0; h < 2; h++) {
					slot++;

					if (weekend) {
						g2.setColor(COLOR_WEEKEND);
						g2.fillRect((day - 1) * slotWidth + 1, y + h * slotHeight + 1, slotWidth - 1, slotHeight - 1);
						weekendCount++;
						continue;
					}

					boolean holiday = workYear.isHoliday(slot);
					boolean compensation = workYear.isCompensation(slot);
					boolean vacation = workYear.isVacation(slot);
					boolean free = workYear.isFree(slot);

					Color color = null;
					if (holiday) {
						color = COLOR_HOLIDAY;
						holidayCount++;
					} else if (compensation) {
						color = COLOR_COMPENSATION;
						compensationCount++;
					} else if (vacation) {
						color = COLOR_VACATION;
						vacationCount++;
					} else if (free) {
						color = COLOR_FREE;
						freeCount++;
					}
					if (color != null) {
						g2.setColor(color);
						g2.fillRect((day - 1) * slotWidth + 2, y + h * slotHeight + 2, slotWidth - 3, slotHeight - 3);
					}

					if (mode != MODE_NONE && slot == mouseSlot) {
						boolean none = workYear.getValue(slot) == 0;
						boolean enabled = none || (mode == MODE_HOLIDAY && holiday || mode == MODE_COMPENSATION && compensation || mode == MODE_VACATION && vacation || mode == MODE_FREE && free);
						if (enabled) {
							g2.setColor(Color.BLACK);
							g2.setStroke(new BasicStroke(2));
							g2.drawRect((day - 1) * slotWidth + 1, y + h * slotHeight + 1, slotWidth - 2, slotHeight - 2);
							g2.setStroke(defaultStroke);
						}
					}

				}

			}

			int daysInMonth = DateTimeUtils.getDaysInMonth(year, month);
			int workCount = daysInMonth * 2 - weekendCount - holidayCount - compensationCount;

			weekendTotal += weekendCount;
			holidayTotal += holidayCount;
			compensationTotal += compensationCount;
			vacationTotal += vacationCount;
			freeTotal += freeCount;
			workTotal += workCount;

			// statistics
			printStatistics(xs, y + slotHeight - 2, workCount, vacationCount, freeCount, g2);
		}
		printStatistics(xs, 725, workTotal, vacationTotal, freeTotal, g2);

		/*
		int yearTotal = DateTimeUtils.getDaysInYear(year) * 2;

		g2.setColor(Color.BLACK);
		NumberFormat numberFormat = new DecimalFormat("0.0");
		drawString(g2, String.valueOf(yearTotal / 2), 0, 725, LEFT, MIDDLE);
		drawString(g2, String.valueOf(weekendTotal / 2), 100, 725, LEFT, MIDDLE);

		int weekdayTotal = yearTotal - weekendTotal;
		drawString(g2, String.valueOf(weekdayTotal / 2), 200, 725, LEFT, MIDDLE);

		drawString(g2, numberFormat.format(holidayTotal * 0.5), 300, 725, LEFT, MIDDLE);
		drawString(g2, numberFormat.format(compensationTotal * 0.5), 400, 725, LEFT, MIDDLE);

		int workingDays = weekdayTotal - holidayTotal - compensationTotal;
		drawString(g2, numberFormat.format(workingDays * 0.5), 500, 725, LEFT, MIDDLE);

		drawString(g2, numberFormat.format(vacationTotal * 0.5), 600, 725, LEFT, MIDDLE);
		*/

		// draw marker for today

		Date today = DateTimeUtils.getNow();
		if (year == DateTimeUtils.getYear(today)) {

			int month = today.getMonth() + 1;
			int day = today.getDate();
			int hour = today.getHours();

			int x = (day - 1) * slotWidth;
			int y = (month - 1) * (slotHeight * 2 + padding);
			if (hour >= 12) y += slotHeight;

			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(2));
			g2.drawRect(x, y, slotWidth, slotHeight);
		}

	}

	private void printStatistics(int x, int y, int workCount, int vacationCount, int freeCount, Graphics2D g2) {
		g2.setColor(Color.BLACK);

		double rate = ((double) workCount - freeCount) * 100 / workCount;

		NumberFormat numberFormat = new DecimalFormat("0.0");
		g2.setColor(vacationCount > 0 ? Color.BLACK : Color.GRAY);
		drawString(g2, numberFormat.format(vacationCount * 0.5), x, y, RIGHT, MIDDLE);
		g2.setColor(freeCount > 0 ? Color.BLACK : Color.GRAY);
		drawString(g2, numberFormat.format(freeCount * 0.5), x + 30, y, RIGHT, MIDDLE);
		g2.setColor(rate < 100 ? Color.BLACK : Color.GRAY);
		drawString(g2, numberFormat.format(rate) + " %", x + 80, y, RIGHT, MIDDLE);

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
			} else if (x > 420 && x < 510) {
				mode = MODE_COMPENSATION;
			} else if (x > 520 && x < 620) {
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

	private String formatTime(int slots) {
		int hours = slots / 12;
		int minutes = (slots % 12) * 5;
		return String.format("%d:%02d", hours, minutes);
	}

	private String formatTimeDiff(int slots) {
		int hours = Math.abs(slots) / 12;
		int minutes = (Math.abs(slots) % 12) * 5;
		String text = String.format("%d:%02d", hours, minutes);
		if (slots > 0) {
			return "+ " + text;
		} else if (slots < 0) {
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
