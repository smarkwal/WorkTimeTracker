package net.markwalder.tools.worktime.db;

import java.util.Date;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

public class DatabaseUtils {

	/**
	 * Get work time in minutes.
	 */
	public static int getWorkTime(Database database, Date date) {

		if (DateTimeUtils.isWeekend(date)) {
			return 0;
		}

		int time = 0;

		WorkYear workYear = database.getWorkYear(date);
		int dayOfYear = DateTimeUtils.getDayOfYear(date);
		int slot = (dayOfYear - 1) * 2;

		// morning
		byte value = workYear.getValue(slot);
		if (value == 0) {
			time += 252; // 4.2 hours * 60 minutes
		}

		// afternoon
		value = workYear.getValue(slot + 1);
		if (value == 0) {
			time += 252; // 4.2 hours * 60 minutes
		}

		// from 2021-06-01 on, count only 80% for every day
		if (date.getTime() >= 1622498400000L) {
			time = time * 4 / 5; // 80%
		}

		return time;
	}

	public static int slot(long time) {
		return (int) (time / 1000 / 60 / 5);
	}

}
