package net.markwalder.tools.worktime.db;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class Statistics {

	public static Statistics getStatistics(Database database, Date startDate, Date endDate) {
		if (startDate == null) throw new NullPointerException("startDate");
		if (endDate == null) throw new NullPointerException("endDate");

		Statistics statistics = new Statistics();
		startDate = DateTimeUtils.getStartOfDay(startDate);
		endDate = DateTimeUtils.getStartOfDay(endDate);

		// if start date is after end date ...
		if (startDate.after(endDate)) {
			// switch start and end date
			Date tmp = startDate;
			startDate = endDate;
			endDate = tmp;
		}

		Date date = startDate;
		while (!date.after(endDate)) {
			WorkDay workDay = database.getWorkDay(date);
			statistics.update(database, workDay);
			date = DateUtils.addDays(date, 1);
		}

		return statistics;

	}

	// ---------------------------------------------------------------------------------

	private final long now = System.currentTimeMillis();

	private int runningCount = 0;
	private int activeCount = 0;
	private int workingCount = 0;
	private int freeCount = 0;
	private int workTimeSlots = 0;

	private Statistics() {
	}

	private void update(Database database, WorkDay workDay) {
		runningCount += workDay.getRunningCount();
		activeCount += workDay.getActiveCount();
		workingCount += workDay.getWorkingCount();
		freeCount += workDay.getFreeCount();
		Date date = workDay.getDate();
		if (date.getTime() <= now) {
			workTimeSlots += DatabaseUtils.getWorkTimeSlots(database, date);
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
