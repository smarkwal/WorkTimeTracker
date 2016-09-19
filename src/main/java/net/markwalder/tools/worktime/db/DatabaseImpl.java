package net.markwalder.tools.worktime.db;

import com.google.inject.Inject;
import net.markwalder.tools.worktime.db.store.Store;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseImpl implements Database {

	private final Store store;

	private Map<Date, WorkDay> workDaysCache = new HashMap<Date, WorkDay>();
	private Map<Date, WorkYear> workYearsCache = new HashMap<Date, WorkYear>();

	@Inject
	public DatabaseImpl(Store store) {
		this.store = store;
	}

	@Override
	public synchronized WorkDay getWorkDay(Date date) {
		if (date == null) throw new NullPointerException("date");

		date = DateTimeUtils.getStartOfDay(date);
		WorkDay workDay = workDaysCache.get(date);

		if (workDay == null) {
			workDay = loadWorkDay(date);
			workDaysCache.put(date, workDay);
		}

		return workDay;
	}

	private WorkDay loadWorkDay(Date date) {
		if (date == null) throw new NullPointerException("date");

		// calculate day offset and length
		int[] range = getWorkDayRange(date);
		int offset = range[0];
		int length = range[1];

		// get key
		String key = getKey("data", date);

		// read data from database file
		byte[] data = store.readData(key, offset, length);

		// return bitmap
		return new WorkDay(date, data);
	}

	@Override
	public synchronized void storeWorkDay(WorkDay workDay) {
		if (workDay == null) throw new NullPointerException("workDay");

		Date date = workDay.getDate();

		// calculate day offset and length
		int[] range = getWorkDayRange(date);
		int offset = range[0];
		int length = range[1];

		// get timeTable data
		byte[] data = workDay.getData();

		// validate data array size
		if (data.length != length) throw new IllegalArgumentException("data.length: " + data.length + " <> " + length);

		// get key
		String key = getKey("data", date);

		// reset modification flag
		workDay.resetModified();

		// write data to database file
		store.writeData(key, offset, data);

	}

	@Override
	public synchronized WorkYear getWorkYear(Date date) {
		if (date == null) throw new NullPointerException("date");

		date = DateTimeUtils.getStartOfYear(date);
		WorkYear workYear = workYearsCache.get(date);

		if (workYear == null) {
			workYear = loadWorkYear(date);
			workYearsCache.put(date, workYear);
		}

		return workYear;
	}

	private synchronized WorkYear loadWorkYear(Date date) {
		if (date == null) throw new NullPointerException("date");

		// calculate number of days
		int year = DateTimeUtils.getYear(date);
		int days = DateTimeUtils.getDaysInYear(year);
		int length = days * 2;

		// get key
		String key = getKey("calendar", date);

		// read data from database file
		byte[] data = store.readData(key, 0, length);

		// return bitmap
		return new WorkYear(date, data);
	}

	@Override
	public synchronized void storeWorkYear(WorkYear workYear) {
		if (workYear == null) throw new NullPointerException("workYear");

		Date date = workYear.getDate();

		// get timeTable data
		byte[] data = workYear.getData();

		// get key
		String key = getKey("calendar", date);

		// reset modification flag
		workYear.resetModified();

		// write data to database file
		store.writeData(key, 0, data);

	}

	// ------------------------------------------------------------------------------------------------------------
	// private methods

	private String getKey(String prefix, Date date) {
		int year = DateTimeUtils.getYear(date);
		return prefix + "-" + year;
	}

	private int[] getWorkDayRange(Date date) {

		// get start of year and start/end of day
		Date startOfYear = DateTimeUtils.getStartOfYear(date);
		Date startOfDay = DateTimeUtils.getStartOfDay(date);
		Date endOfDay = DateUtils.addDays(startOfDay, 1);

		// convert date into timestamp
		long startOfYearTime = startOfYear.getTime();
		long startOfDayTime = startOfDay.getTime();
		long endOfDayTime = endOfDay.getTime();

		// calculate offset and length (number of 5-minute slots)
		int offset = DatabaseUtils.slot(startOfDayTime - startOfYearTime);
		int length = DatabaseUtils.slot(endOfDayTime - startOfDayTime);

		return new int[]{offset, length};

	}

}
