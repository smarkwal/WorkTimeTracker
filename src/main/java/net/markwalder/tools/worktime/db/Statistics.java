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

package net.markwalder.tools.worktime.db;

import java.util.Date;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.apache.commons.lang3.time.DateUtils;

public class Statistics {

	public static Statistics getStatistics(Database database, Date today, Date startDate, Date endDate) {
		if (today == null) throw new NullPointerException("today");
		if (startDate == null) throw new NullPointerException("startDate");
		if (endDate == null) throw new NullPointerException("endDate");

		Statistics statistics = new Statistics(today);
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

	private final long now;

	private int workingCount = 0;
	private int freeCount = 0;
	private int workTime = 0;

	private Statistics(Date today) {
		now = today.getTime();
	}

	private void update(Database database, WorkDay workDay) {
		workingCount += workDay.getWorkingCount();
		freeCount += workDay.getFreeCount();
		Date date = workDay.getDate();
		if (date.getTime() <= now) {
			workTime += DatabaseUtils.getWorkTime(database, date);
		}
	}

	public int getWorkingCount() {
		return workingCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	/**
	 * Get work time in minutes.
	 */
	public int getWorkTime() {
		return workTime;
	}

}
