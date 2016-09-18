package net.markwalder.tools.worktime.ui;

import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.*;
import org.apache.commons.lang3.time.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkDayPanel extends JPanel implements MouseListener, MouseMotionListener {

	private int marginTop = 50;
	private int marginLeft = 20;

	private int slotWidth = 20;
	private int slotHeight = 20;

	private final Controller controller;

	public WorkDayPanel(Controller controller) {
		this.controller = controller;
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

		int width = 12 * slotWidth;
		int height = hours * slotHeight;

		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.translate(marginLeft, marginTop);

		Date today = DateTimeUtils.getToday();


		Date displayDate = controller.getDisplayDate();
		if (displayDate != null) {
			g2.setFont(new Font("Arial", Font.BOLD, 16));
			g2.setColor(Color.BLACK);

			SimpleDateFormat dateFormat;
			if (displayDate.equals(today)) {
				dateFormat = new SimpleDateFormat("'Today' | EEEE", Locale.US);
			} else if (displayDate.equals(DateUtils.addDays(today, 1))) {
				dateFormat = new SimpleDateFormat("'Tomorrow' | EEEE", Locale.US);
			} else if (displayDate.equals(DateUtils.addDays(today, -1))) {
				dateFormat = new SimpleDateFormat("'Yesterday' | EEEE", Locale.US);
			} else {
				dateFormat = new SimpleDateFormat("dd.MM.yyyy | EEEE", Locale.US);
			}

			g2.drawString(dateFormat.format(displayDate), 0, -24);
		}

		Font font = new Font("Arial", Font.PLAIN, 11);
		g2.setFont(font);

		for (int s = 1; s < 12; s++) {
			int x = s * slotWidth;
			if (s % 3 == 0) {
				g2.setColor(Color.GRAY);
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			drawString(g2, String.valueOf(s * 5), x, -4, MIDDLE, BOTTOM);
		}
		g2.setColor(Color.GRAY);
		for (int h = 0; h < hours; h++) {
			int y = h * slotHeight;
			int hour = h;
			if (hours == 23 && h >= 3) {
				hour = h + 1;
			} else if (hours == 25 && h >= 3) {
				hour = h - 1;
			}
			String label = String.valueOf(hour);
			drawString(g2, label, -4, y + 10, RIGHT, MIDDLE);
		}

		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);

		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int s = 0; s <= 12; s++) {
			int x = s * slotWidth;
			if (s % 3 == 0) {
				g2.setColor(Color.GRAY);
			} else {
				g2.setColor(Color.LIGHT_GRAY);
			}
			g2.drawLine(x, 0, x, height);
		}
		g2.setColor(Color.GRAY);
		for (int h = 0; h <= hours; h++) {
			int y = h * slotHeight;
			g2.drawLine(0, y, width, y);
		}

		g2.setColor(Color.RED);
		if (hours == 24) {
			g2.drawLine(0, 8 * slotWidth, width, 8 * slotWidth);
			g2.drawLine(0, 12 * slotWidth, width, 12 * slotWidth);
			g2.drawLine(0, 17 * slotWidth, width, 17 * slotWidth);
		} else if (hours == 23) {
			g2.drawLine(0, 7 * slotWidth, width, 7 * slotWidth);
			g2.drawLine(0, 11 * slotWidth, width, 11 * slotWidth);
			g2.drawLine(0, 16 * slotWidth, width, 16 * slotWidth);
		} else if (hours == 25) {
			g2.drawLine(0, 9 * slotWidth, width, 9 * slotWidth);
			g2.drawLine(0, 13 * slotWidth, width, 13 * slotWidth);
			g2.drawLine(0, 18 * slotWidth, width, 18 * slotWidth);
		}

		if (workDay == null) return;

		Database database = controller.getDatabase();

		Date date = workDay.getDate();
		int runningCount = workDay.getRunningCount();
		int activeCount = workDay.getActiveCount();
		int workingCount = workDay.getWorkingCount();
		int freeCount = workDay.getFreeCount();

		int nowSlot = -1;
		if (date.equals(today)) {
			nowSlot = DatabaseUtils.slot(System.currentTimeMillis() - today.getTime());
		}

		Color COLOR_RUNNING = new Color(204, 204, 204);
		Color COLOR_ACTIVE = new Color(128, 175, 204);
		Color COLOR_NOW = new Color(82, 111, 180);

		for (int h = 0; h < hours; h++) {
			for (int s = 0; s < 12; s++) {
				int x = s * slotWidth;
				int y = h * slotHeight;

				int slot = h * 12 + s;
				boolean running = workDay.isRunning(slot);
				boolean active = workDay.isActive(slot);
				boolean working = workDay.isWorking(slot);
				boolean free = workDay.isFree(slot);

				Color color = Color.WHITE;
				if (running) {
					if (active) {
						color = COLOR_ACTIVE;
					} else {
						color = COLOR_RUNNING;
					}
				}
				g2.setColor(color);
				g2.fillRect(x + 2, y + 2, slotWidth - 3, slotHeight - 3);

				if (working || free) {
					if (working) {
						g2.setColor(Color.DARK_GRAY);
					} else {
						g2.setColor(Color.GRAY);
					}
					g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					g2.drawLine(x + 5, y + 5, x + slotWidth - 5, y + slotHeight - 5);
					g2.drawLine(x + 5, y + slotHeight - 5, x + slotWidth - 5, y + 5);
				}

				if (slot == nowSlot) {
					g2.setColor(COLOR_NOW);
					// g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
					// g2.drawRect(x, y, dx, dy);
					g2.fillOval(x + 5, y + 5, slotWidth - 9, slotHeight - 9);
				}

			}
		}

		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int x = 12 * slotWidth + 20;
		int x2 = x + 30;
		int x3 = x + 120;

		g2.setColor(Color.WHITE);
		g2.fillRect(x, 0, slotWidth, slotHeight);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, 0, slotWidth, slotHeight);
		g2.setColor(COLOR_RUNNING);
		g2.fillRect(x + 2, 2, slotWidth - 3, slotHeight - 3);
		g2.setColor(Color.BLACK);
		g2.drawString("Running :", x2, 15);
		drawString(g2, formatTime(runningCount), x3, 15, RIGHT, BOTTOM);

		g2.setColor(Color.WHITE);
		g2.fillRect(x, 30, slotWidth, slotHeight);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, 30, slotWidth, slotHeight);
		g2.setColor(COLOR_ACTIVE);
		g2.fillRect(x + 2, 32, slotWidth - 3, slotHeight - 3);
		g2.setColor(Color.BLACK);
		g2.drawString("Active :", x2, 45);
		drawString(g2, formatTime(activeCount), x3, 45, RIGHT, BOTTOM);

		g2.setColor(Color.WHITE);
		g2.fillRect(x, 60, slotWidth, slotHeight);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, 60, slotWidth, slotHeight);
		g2.setColor(Color.DARK_GRAY);
		g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawLine(x + 5, 65, x + slotWidth - 4, 60 + slotHeight - 4);
		g2.drawLine(x + 5, 60 + slotHeight - 4, x + slotWidth - 4, 65);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawString("Working :", x2, 75);
		drawString(g2, formatTime(workingCount), x3, 75, RIGHT, BOTTOM);

		int y = 105;

		if (freeCount > 0) {

			g2.setColor(Color.WHITE);
			g2.fillRect(x, 90, slotWidth, slotHeight);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, 90, slotWidth, slotHeight);
			g2.setColor(Color.GRAY);
			g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawLine(x + 5, 95, x + slotWidth - 4, 90 + slotHeight - 4);
			g2.drawLine(x + 5, 90 + slotHeight - 4, x + slotWidth - 4, 95);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.drawString("Free :", x2, y);
			drawString(g2, formatTime(freeCount), x3, y, RIGHT, BOTTOM);

			y += 30;
		}

		y = 140;
		x2 = x + 50;
		g2.setColor(Color.GRAY);
		g2.drawLine(x, y, x3, y);
		g2.setColor(Color.BLACK);
		drawString(g2, "Day", x, y + 15);

		if (workingCount > 0 || date.compareTo(today) <= 0) {

			int workTimeSlots = DatabaseUtils.getWorkTimeSlots(database, date) - freeCount;

			int remainingCount = workTimeSlots - workingCount;

			if (remainingCount > 0) {
				g2.setColor(Color.RED);
			} else if (remainingCount < 0) {
				g2.setColor(new Color(0, 128, 0));
			} else {
				g2.setColor(Color.GRAY);
			}
			drawString(g2, formatTimeDiff(-remainingCount), x3, y + 15, RIGHT, BOTTOM);

			if (date.equals(today)) {
				if (remainingCount > 0) {
					int endSlot = nowSlot + remainingCount;
					endSlot = endSlot % (24 * 12);
					y += 30;
					g2.setColor(Color.GRAY);
					g2.drawString("End :", x2, y);
					drawString(g2, formatTime(endSlot), x3, y, RIGHT, BOTTOM);
				}
			}

		}

		y = 180;
		g2.setColor(Color.GRAY);
		g2.drawLine(x, y, x3, y);
		g2.setColor(Color.BLACK);
		drawString(g2, "Week", x, y + 15);

		// create statistics for the current week
		Date startOfWeek = DateTimeUtils.getStartOfWeek(date);
		Date endOfWeek = DateTimeUtils.getEndOfWeek(date);
		Statistics statistics = Statistics.getStatistics(database, startOfWeek, endOfWeek);

		workingCount = statistics.getWorkingCount();
		if (workingCount > 0 || startOfWeek.compareTo(today) <= 0) {

			int workTimeSlots = statistics.getWorkTimeSlots() - statistics.getFreeCount();

			int remainingCount = workTimeSlots - workingCount;

			if (remainingCount > 0) {
				g2.setColor(Color.RED);
			} else if (remainingCount < 0) {
				g2.setColor(new Color(0, 128, 0));
			} else {
				g2.setColor(Color.GRAY);
			}
			drawString(g2, formatTimeDiff(-remainingCount), x3, y + 15, RIGHT, BOTTOM);

			if (date.equals(today)) {
				if (remainingCount > 0) {
					int endSlot = nowSlot + remainingCount;
					endSlot = endSlot % (24 * 12);
					y += 30;
					g2.setColor(Color.GRAY);
					g2.drawString("End :", x2, y);
					drawString(g2, formatTime(endSlot), x3, y, RIGHT, BOTTOM);
				}
			}

		}

		y = 220;
		g2.setColor(Color.GRAY);
		g2.drawLine(x, y, x3, y);
		g2.setColor(Color.BLACK);
		drawString(g2, "Month", x, y + 15);

		// create statistics for the current month
		Date startOfMonth = DateTimeUtils.getStartOfMonth(date);
		Date endOfMonth = DateTimeUtils.getEndOfMonth(date);
		statistics = Statistics.getStatistics(database, startOfMonth, endOfMonth);

		workingCount = statistics.getWorkingCount();
		if (workingCount > 0 || startOfMonth.compareTo(today) <= 0) {

			int workTimeSlots = statistics.getWorkTimeSlots() - statistics.getFreeCount();

			int remainingCount = workTimeSlots - workingCount;

			if (remainingCount > 0) {
				g2.setColor(Color.RED);
			} else if (remainingCount < 0) {
				g2.setColor(new Color(0, 128, 0));
			} else {
				g2.setColor(Color.GRAY);
			}
			drawString(g2, formatTimeDiff(-remainingCount), x3, y + 15, RIGHT, BOTTOM);

			if (date.equals(today)) {
				if (remainingCount > 0) {
					int endSlot = nowSlot + remainingCount;
					endSlot = endSlot % (24 * 12);
					y += 30;
					g2.setColor(Color.GRAY);
					g2.drawString("End :", x2, y);
					drawString(g2, formatTime(endSlot), x3, y, RIGHT, BOTTOM);
				}
			}

		}

		y = 260;
		g2.setColor(Color.GRAY);
		g2.drawLine(x, y, x3, y);
		g2.setColor(Color.BLACK);
		drawString(g2, "Year", x, y + 15);

		// create statistics for the current year
		Date startOfYear = DateTimeUtils.getStartOfYear(date);
		Date endOfYear = DateTimeUtils.getEndOfYear(date);
		statistics = Statistics.getStatistics(database, startOfYear, endOfYear);

		workingCount = statistics.getWorkingCount();
		if (workingCount > 0 || startOfYear.compareTo(today) <= 0) {

			int workTimeSlots = statistics.getWorkTimeSlots() - statistics.getFreeCount();

			int remainingCount = workTimeSlots - workingCount;

			if (remainingCount > 0) {
				g2.setColor(Color.RED);
			} else if (remainingCount < 0) {
				g2.setColor(new Color(0, 128, 0));
			} else {
				g2.setColor(Color.GRAY);
			}
			drawString(g2, formatTimeDiff(-remainingCount), x3, y + 15, RIGHT, BOTTOM);

			if (date.equals(today)) {
				if (remainingCount > 0) {
					int endSlot = nowSlot + remainingCount;
					endSlot = endSlot % (24 * 12);
					y += 30;
					g2.setColor(Color.GRAY);
					g2.drawString("End :", x2, y);
					drawString(g2, formatTime(endSlot), x3, y, RIGHT, BOTTOM);
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
		controller.wordDayMouseDragged(slot);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// ignore
	}

	//---------------------------------------------------------------------------------
	// private methods

	private int getSlot(MouseEvent e) {

		// get relative mouse position relative
		int x = e.getX() - marginLeft;
		int y = e.getY() - marginTop;

		// check bounds of table
		if (x < 0 || x >= 12 * slotWidth) return -1;
		if (y < 0 || y >= 25 * slotHeight) return -1;

		// convert pixel coordinates into slot coordinates
		int h = y / slotHeight;
		int s = x / slotWidth;

		// calculate slot index
		return h * 12 + s;
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
