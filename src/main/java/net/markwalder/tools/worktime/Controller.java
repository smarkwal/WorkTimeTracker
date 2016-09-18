package net.markwalder.tools.worktime;

import net.markwalder.tools.worktime.db.*;
import net.markwalder.tools.worktime.ui.Window;
import net.markwalder.tools.worktime.ui.WorkYearPanel;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class Controller {

	private final Window window;
	private final ActivityTracker activityTracker;
	private final Database database = new FileDatabase();

	private Date activeDate;
	private WorkDay activeWorkDay;

	private Date displayDate;
	private WorkDay displayWorkDay;
	private WorkYear displayWorkYear;

	private char activeKey;

	public Controller() {

		// open main window
		window = new Window(this, "Work Time Tracker " + Version.getVersion());

		// prepare activity tracker
		activityTracker = new ActivityTracker(this);

	}

	public WorkDay getDisplayWorkDay() {
		return displayWorkDay;
	}

	public WorkYear getDisplayWorkYear() {
		return displayWorkYear;
	}

	public Database getDatabase() {
		return database;
	}

	public char getActiveKey() {
		return activeKey;
	}

	public void setActiveKey(char activeKey) {
		this.activeKey = activeKey;
	}

	public void start() {

		Date date = new Date();
		activeDate = DateTimeUtils.getStartOfDay(date);
		displayDate = activeDate;

		// open database and load time tables
		activeWorkDay = database.getWorkDay(activeDate);
		displayWorkDay = activeWorkDay;
		displayWorkYear = database.getWorkYear(displayDate);

		window.repaint();

		activityTracker.start();

	}

	public void stop() {
		activityTracker.stop();
		System.exit(0);
	}

	public void reportActive(boolean active) {

		// get current time, date and start of day
		long time = System.currentTimeMillis();
		Date date = DateTimeUtils.getStartOfDay(time);
		long startOfDay = date.getTime();

		// if day has changed ...
		if (!date.equals(activeDate)) {
			// load new timeTable
			activeWorkDay = database.getWorkDay(date);
			window.repaint();
			activeDate = date;
			if (displayDate.equals(activeDate)) {
				displayWorkDay = activeWorkDay;
			}
		}

		// check if timeTable needs to be modified ...
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

			// store modified timeTable
			database.storeWorkDay(activeWorkDay);

			// update UI
			if (displayWorkDay == activeWorkDay) {
				window.repaint();
			}

		}

	}

	private boolean working = false;
	private boolean free = false;

	public void workDayMousePressed(int slot, boolean shift) {
		if (displayWorkDay == null) return;
		if (slot < 0 || slot >= displayWorkDay.getSize()) return;

		if (!shift) {
			// invert working mode
			working = !displayWorkDay.isWorking(slot);
			free = false;
		} else {
			// invert free mode
			working = false;
			free = !displayWorkDay.isFree(slot);
		}

		// set new slot value and repaint
		displayWorkDay.setWorking(slot, working);
		displayWorkDay.setFree(slot, free);
		window.repaint();

	}

	public void wordDayMouseDragged(int slot) {
		if (displayWorkDay == null) return;
		if (slot < 0 || slot >= displayWorkDay.getSize()) return;

		// ignore if slot is already set to correct value
		if (displayWorkDay.isWorking(slot) == working && displayWorkDay.isFree(slot) == free) return;

		// set new slot value and repaint
		displayWorkDay.setWorking(slot, working);
		displayWorkDay.setFree(slot, free);
		window.repaint();

	}

	public void workDayMouseReleased() {
		if (displayWorkDay == null) return;

		if (displayWorkDay.isModified()) {
			// store modified time table
			database.storeWorkDay(displayWorkDay);
		}

	}

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
		} else if (mode == WorkYearPanel.MODE_COMPENSATION) {
			if (value > 0) {
				boolean compensation = displayWorkYear.isCompensation(slot);
				if (compensation) {
					displayWorkYear.setCompensation(slot, false);
				}
			} else {
				displayWorkYear.setCompensation(slot, true);
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

	public Date getDisplayDate() {
		return displayDate;
	}

	public void incrementDisplayDate() {
		Date date = DateUtils.addDays(displayDate, 1);
		setDisplayDate(date);
	}

	public void decrementDisplayDate() {
		Date date = DateUtils.addDays(displayDate, -1);
		setDisplayDate(date);
	}

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

}
