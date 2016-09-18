package net.markwalder.tools.worktime.db;

import java.util.Date;

public class DatabaseUtils {

	public static int getWorkTimeSlots(Database database, Date date) {

		if (DateTimeUtils.isWeekend(date)) {
			return 0;
		}

		int slots = 0;

		WorkYear workYear = database.getWorkYear(date);
		int dayOfYear = DateTimeUtils.getDayOfYear(date);
		int slot = (dayOfYear - 1) * 2;

		// morning
		byte value = workYear.getValue(slot);
		if (value == 0) {
			slots += 50; // 4:10
		}

		// afternoon
		value = workYear.getValue(slot + 1);
		if (value == 0) {
			if (DateTimeUtils.isFriday(date)) {
				slots += 50; // 4:10
			} else {
				slots += 51; // 4:15
			}
		}

		return slots;
	}

	public static int slot(long time) {
		return (int) (time / 1000 / 60 / 5);
	}

}
