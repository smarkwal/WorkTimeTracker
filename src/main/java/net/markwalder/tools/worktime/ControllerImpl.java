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

package net.markwalder.tools.worktime;

import com.google.inject.Inject;
import java.time.Clock;
import java.util.Date;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseUtils;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.db.WorkYear;
import net.markwalder.tools.worktime.tracker.ActivityListener;
import net.markwalder.tools.worktime.tracker.ActivityTracker;
import net.markwalder.tools.worktime.ui.Window;
import net.markwalder.tools.worktime.ui.WorkYearPanel;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.apache.commons.lang3.time.DateUtils;

public class ControllerImpl implements Controller, ActivityListener {

	private final Window window;
	private final ActivityTracker activityTracker;
	private final Database database;
	private final Clock clock;

	private Date activeDate;
	private WorkDay activeWorkDay;

	private Date displayDate;
	private WorkDay displayWorkDay;
	private WorkYear displayWorkYear;

	@Inject
	public ControllerImpl(Window window, ActivityTracker activityTracker, Database database, Clock clock) {
		this.window = window;
		this.activityTracker = activityTracker;
		this.database = database;
		this.clock = clock;
	}

	@Override
	public WorkDay getDisplayWorkDay() {
		return displayWorkDay;
	}

	@Override
	public WorkYear getDisplayWorkYear() {
		return displayWorkYear;
	}

	@Override
	public void start() {

		activeDate = DateTimeUtils.getToday(clock);
		displayDate = activeDate;

		// open database and load timetables
		activeWorkDay = database.getWorkDay(activeDate);
		displayWorkDay = activeWorkDay;
		displayWorkYear = database.getWorkYear(displayDate);

		window.setVisible(true);
		window.repaint();

		activityTracker.start();

	}

	@Override
	public void stop() {
		activityTracker.stop();
		System.exit(0);
	}

	@Override
	public void reportActive(boolean active) {

		// get current time, date and start of day
		long time = clock.millis();
		Date today = DateTimeUtils.getToday(clock);
		long startOfDay = today.getTime();

		// if day has changed ...
		if (!today.equals(activeDate)) {
			// load new timetable
			activeWorkDay = database.getWorkDay(today);
			window.repaint();
			activeDate = today;
			if (displayDate.equals(activeDate)) {
				displayWorkDay = activeWorkDay;
			}
		}

		// check if timetable needs to be modified ...
		int slot = DatabaseUtils.slot(time - startOfDay);

		// make sure that the "application is running" flag is set
		if (!activeWorkDay.isRunning(slot)) {
			activeWorkDay.setRunning(slot, true);
		}

		// if user is active ...
		if (active) {
			// make sure that the "user is active" flag is set
			if (!activeWorkDay.isActive(slot)) {
				activeWorkDay.setActive(slot, true);
			}
		}

		if (activeWorkDay.isModified()) {

			// store modified timetable
			database.storeWorkDay(activeWorkDay);

			// update UI
			if (displayWorkDay == activeWorkDay) {
				window.repaint();
			}

		}

	}

	private boolean markWorking = false;
	private boolean markFree = false;

	@Override
	public void workDayMousePressed(int slot, boolean shift) {
		if (displayWorkDay == null) return;
		if (slot < 0 || slot >= displayWorkDay.getSize()) return;

		if (!shift) {
			// invert working mode
			markWorking = !displayWorkDay.isWorking(slot);
			markFree = false;
		} else {
			// invert free mode
			markWorking = false;
			markFree = !displayWorkDay.isFree(slot);
		}

		// set new slot value and repaint
		displayWorkDay.setWorking(slot, markWorking);
		displayWorkDay.setFree(slot, markFree);
		window.repaint();

	}

	@Override
	public void workDayMouseDragged(int slot) {
		if (displayWorkDay == null) return;
		if (slot < 0 || slot >= displayWorkDay.getSize()) return;

		// ignore if slot is already set to correct value
		if (displayWorkDay.isWorking(slot) == markWorking && displayWorkDay.isFree(slot) == markFree) return;

		// set new slot value and repaint
		displayWorkDay.setWorking(slot, markWorking);
		displayWorkDay.setFree(slot, markFree);
		window.repaint();

	}

	@Override
	public void workDayMouseReleased() {
		if (displayWorkDay == null) return;

		if (displayWorkDay.isModified()) {
			// store modified timetable
			database.storeWorkDay(displayWorkDay);
		}

	}

	@Override
	public void workYearMouseClicked(int slot, int mode) {
		if (displayWorkYear == null) return;
		if (slot < 0 || slot >= displayWorkYear.getSize()) return;

		byte value = displayWorkYear.getValue(slot);
		if (mode == WorkYearPanel.MODE_HOLIDAY) {
			if (value > 0) {
				boolean holiday = displayWorkYear.isHoliday(slot);
				if (holiday) {
					displayWorkYear.setHoliday(slot, false);
				}
			} else {
				displayWorkYear.setHoliday(slot, true);
			}
		} else if (mode == WorkYearPanel.MODE_VACATION) {
			if (value > 0) {
				boolean vacation = displayWorkYear.isVacation(slot);
				if (vacation) {
					displayWorkYear.setVacation(slot, false);
				}
			} else {
				displayWorkYear.setVacation(slot, true);
			}
		} else if (mode == WorkYearPanel.MODE_FREE) {
			if (value > 0) {
				boolean free = displayWorkYear.isFree(slot);
				if (free) {
					displayWorkYear.setFree(slot, false);
				}
			} else {
				displayWorkYear.setFree(slot, true);
			}
		}

		if (displayWorkYear.isModified()) {
			database.storeWorkYear(displayWorkYear);
			window.repaint();
		}

	}

	@Override
	public Date getDisplayDate() {
		return displayDate;
	}

	@Override
	public void setDisplayDate(Date date) {

		date = DateTimeUtils.getStartOfDay(date);

		if (date.equals(activeDate)) {
			displayDate = activeDate;
			displayWorkDay = activeWorkDay;
		} else {
			displayWorkDay = database.getWorkDay(date);
			displayDate = displayWorkDay.getDate();
		}

		window.repaint();

	}

	@Override
	public void incrementDisplayDate() {
		Date date = DateUtils.addDays(displayDate, 1);
		setDisplayDate(date);
	}

	@Override
	public void decrementDisplayDate() {
		Date date = DateUtils.addDays(displayDate, -1);
		setDisplayDate(date);
	}

}
