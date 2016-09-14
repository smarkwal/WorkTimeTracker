package net.markwalder.tools.worktime.db;

import java.util.Date;

public class Statistics {

	private final transient Database database;

	private final long now = System.currentTimeMillis();

	private int runningCount = 0;
	private int activeCount = 0;
	private int workingCount = 0;
	private int freeCount = 0;
	private int workTimeSlots = 0;

	public Statistics(Database database) {
		this.database = database;
	}

	public void update(WorkDay workDay) {
		runningCount += workDay.getRunningCount();
		activeCount += workDay.getActiveCount();
		workingCount += workDay.getWorkingCount();
		freeCount += workDay.getFreeCount();
		Date date = workDay.getDate();
		if (date.getTime() <= now) {
			workTimeSlots += database.getWorkTimeSlots(date);
		}
	}

	public int getRunningCount() {
		return runningCount;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public int getWorkingCount() {
		return workingCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	public int getWorkTimeSlots() {
		return workTimeSlots;
	}

}
