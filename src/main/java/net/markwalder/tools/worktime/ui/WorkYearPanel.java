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

import static net.markwalder.tools.worktime.ui.TimeTablePainter.MARGIN_LEFT;
import static net.markwalder.tools.worktime.ui.TimeTablePainter.MARGIN_TOP;
import static net.markwalder.tools.worktime.ui.TimeTablePainter.PADDING;
import static net.markwalder.tools.worktime.ui.TimeTablePainter.SLOT_HEIGHT;
import static net.markwalder.tools.worktime.ui.TimeTablePainter.SLOT_WIDTH;

import com.google.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.LocalDate;
import javax.swing.*;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

public class WorkYearPanel extends JPanel implements MouseListener, MouseMotionListener {

	public static final int WIDTH = 655;
	public static final int HEIGHT = 760;

	public static final int MODE_NONE = 0;
	public static final int MODE_HOLIDAY = 1;
	public static final int MODE_VACATION = 3;
	public static final int MODE_FREE = 4;

	private int mode = MODE_NONE;
	private int mouseSlot = -1;

	private final transient Controller controller;
	private final transient WorkYearPainter painter;

	@Inject
	public WorkYearPanel(Controller controller, WorkYearPainter painter) {
		this.controller = controller;
		this.painter = painter;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		Dimension size = new Dimension(WIDTH, HEIGHT);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}

	@Override
	public void paint(Graphics g) {

		WorkYear workYear = controller.getDisplayWorkYear();

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (workYear != null) {
			painter.paint(g2, workYear, mode, mouseSlot);
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

		// get relative mouse position relative
		int x = e.getX() - MARGIN_LEFT;
		int y = e.getY() - MARGIN_TOP;

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

		LocalDate displayDate = controller.getDisplayDate();
		if (displayDate == null) return -1;

		int year = displayDate.getYear();

		// get relative mouse position relative
		int x = e.getX() - MARGIN_LEFT;
		int y = e.getY() - MARGIN_TOP;

		if (y < 0) return -1;

		int month = y / (SLOT_HEIGHT * 2 + PADDING) + 1;
		if (month > 12) return -1;

		int days = DateTimeUtils.getDaysInMonth(year, month);

		int day = x / SLOT_WIDTH + 1;
		if (day < 1 || day > days) return -1;

		LocalDate date = LocalDate.of(year, month, day);
		int dayOfYear = date.getDayOfYear();

		int h = y - (month - 1) * (SLOT_HEIGHT * 2 + PADDING);
		if (h < SLOT_HEIGHT) {
			// AM
			return (dayOfYear - 1) * 2;
		} else if (h < 2 * SLOT_HEIGHT) {
			// PM
			return (dayOfYear - 1) * 2 + 1;
		} else {
			return -1;
		}

	}

}
