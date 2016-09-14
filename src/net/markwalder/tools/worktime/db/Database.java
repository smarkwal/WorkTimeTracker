package net.markwalder.tools.worktime.db;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);

	private Map<Date, WorkDay> workDaysCache = new HashMap<Date, WorkDay>();
	private Map<Date, WorkYear> workYearsCache = new HashMap<Date, WorkYear>();

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

		// get database file
		String fileName = getFileName("data", date);
		File file = new File(fileName);

		// read data from database file
		byte[] data = readData(file, offset, length);

		// return bitmap
		return new WorkDay(date, data);
	}

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

		// get database file
		String fileName = getFileName("data", date);
		File file = new File(fileName);

		// reset modification flag
		workDay.resetModified();

		// write data to database file
		writeData(file, offset, data);

	}

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

		// get database file
		String fileName = getFileName("calendar", date);
		File file = new File(fileName);

		// read data from database file
		byte[] data = readData(file, 0, length);

		// return bitmap
		return new WorkYear(date, data);
	}

	public synchronized void storeWorkYear(WorkYear workYear) {
		if (workYear == null) throw new NullPointerException("workYear");

		Date date = workYear.getDate();

		// get timeTable data
		byte[] data = workYear.getData();

		// get database file
		String fileName = getFileName("calendar", date);
		File file = new File(fileName);

		// reset modification flag
		workYear.resetModified();

		// write data to database file
		writeData(file, 0, data);

	}

	public Statistics getStatistics(Date startDate, Date endDate) {
		if (startDate == null) throw new NullPointerException("startDate");
		if (endDate == null) throw new NullPointerException("endDate");

		Statistics statistics = new Statistics(this);
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
			WorkDay workDay = getWorkDay(date);
			statistics.update(workDay);
			date = DateUtils.addDays(date, 1);
		}

		return statistics;

	}

	public int getWorkTimeSlots(Date date) {

		if (DateTimeUtils.isWeekend(date)) {
			return 0;
		}

		int slots = 0;

		WorkYear workYear = getWorkYear(date);
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

	// ------------------------------------------------------------------------------------------------------------
	// private methods

	private byte[] readData(File file, int offset, int length) {

		lock.readLock().lock();
		try {

			// prepare a new (empty) data array
			byte[] data = new byte[length];

			// if file does not exists -> return empty data array
			if (!file.exists()) return data;

			long time = System.nanoTime();

			// open database file
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(file, "r");

				// jump to offset
				raf.seek(offset);

				// read data
				int pos = 0;
				while (true) {
					int bytes = raf.read(data, pos, length - pos);
					if (bytes < 0) break; // todo: throw an exception ?
					pos = pos + bytes;
					if (pos == length) break;
				}

			} finally {
				IOUtils.closeQuietly(raf);
			}

			// return data array
			return data;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			lock.readLock().unlock();
		}

	}

	private void writeData(File file, int offset, byte[] data) {

		lock.writeLock().lock();
		try {

			long time = System.nanoTime();

			// open database file
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(file, "rw");

				// jump to offset
				raf.seek(offset);

				// write data
				raf.write(data);

			} finally {
				IOUtils.closeQuietly(raf);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			lock.writeLock().unlock();
		}

	}

	private String getFileName(String prefix, Date date) {
		int year = DateTimeUtils.getYear(date);
		return prefix + "-" + year + ".dat";
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
		int offset = slot(startOfDayTime - startOfYearTime);
		int length = slot(endOfDayTime - startOfDayTime);

		return new int[]{offset, length};

	}

	// ------------------------------------------------------------------------------------------------------------
	// utility methods

	public static int slot(long time) {
		return (int) (time / 1000 / 60 / 5);
	}


}
