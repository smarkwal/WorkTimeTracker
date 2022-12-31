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
import static net.markwalder.tools.worktime.ui.TimeTablePainter.SLOT_HEIGHT;
import static net.markwalder.tools.worktime.ui.TimeTablePainter.SLOT_WIDTH;

import com.google.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
import net.markwalder.tools.worktime.Controller;
import net.markwalder.tools.worktime.db.WorkDay;

public class WorkDayPanel extends JPanel implements MouseListener, MouseMotionListener {

	public static final int WIDTH = 420;
	public static final int HEIGHT = 540;

	private final transient Controller controller;
	private final transient WorkDayPainter painter;

	@Inject
	public WorkDayPanel(Controller controller, WorkDayPainter painter) {
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

		WorkDay workDay = controller.getDisplayWorkDay();

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());

		if (workDay != null) {
			painter.paint(g2, workDay);
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

}
