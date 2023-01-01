/*
 * Copyright 2023 Stephan Markwalder
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
import net.markwalder.tools.worktime.utils.DateTimeUtils;

public class WorkContract {

	private static final int WORK_MINUTES_PER_DAY = 504; // 8.4 h

	/**
	 * Get work time in minutes.
	 */
	public int getWorkTime(LocalDate date, Database database) {

		// do not work on weekends
		if (DateTimeUtils.isWeekend(date)) {
			return 0;
		}

		int time = 0;

		WorkYear workYear = database.getWorkYear(date);
		int dayOfYear = date.getDayOfYear();
		int slot = (dayOfYear - 1) * 2;

		// morning
		byte value = workYear.getValue(slot);
		if (value == 0) { // no holiday, no vacation, ...
			time += WORK_MINUTES_PER_DAY / 2;
		}

		// afternoon
		value = workYear.getValue(slot + 1);
		if (value == 0) { // no holiday, no vacation, ...
			time += WORK_MINUTES_PER_DAY / 2;
		}

		// from 2021-06-01 on, count only 80% for every day
		// before: Monday is marked as free day
		// after: work 80% every day of the week
		if (date.isAfter(LocalDate.of(2021, 5, 31))) {
			time = time * 4 / 5; // 80%
		}

		return time;
	}

}
