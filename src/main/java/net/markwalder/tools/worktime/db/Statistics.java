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

import java.time.LocalDate;

public class Statistics {

	public static Statistics getStatistics(LocalDate startDate, LocalDate endDate, LocalDate today, WorkContract workContract, Database database) {
		if (startDate == null) throw new NullPointerException("startDate");
		if (endDate == null) throw new NullPointerException("endDate");
		if (today == null) throw new NullPointerException("today");

		Statistics statistics = new Statistics(today);

		// if start date is after end date ...
		if (startDate.isAfter(endDate)) {
			// switch start and end date
			LocalDate tmp = startDate;
			startDate = endDate;
			endDate = tmp;
		}

		LocalDate date = startDate;
		while (!date.isAfter(endDate)) {
			WorkDay workDay = database.getWorkDay(date);
			statistics.update(workContract, database, workDay);
			date = date.plusDays(1);
		}

		return statistics;

	}

	// ---------------------------------------------------------------------------------

	private final LocalDate today;

	private int workingCount = 0;
	private int freeCount = 0;
	private int workTime = 0;

	private Statistics(LocalDate today) {
		this.today = today;
	}

	private void update(WorkContract workContract, Database database, WorkDay workDay) {
		workingCount += workDay.getWorkingCount();
		freeCount += workDay.getFreeCount();
		LocalDate date = workDay.getDate();
		if (!date.isAfter(today)) {
			workTime += workContract.getWorkTime(date, database);
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
