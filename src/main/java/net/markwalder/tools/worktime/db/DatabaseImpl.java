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

import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import net.markwalder.tools.worktime.db.store.Store;

public class DatabaseImpl implements Database {

	private final Store store;

	private final Map<LocalDate, WorkDay> workDaysCache = new HashMap<>();
	private final Map<Integer, WorkYear> workYearsCache = new HashMap<>();

	@Inject
	public DatabaseImpl(Store store) {
		this.store = store;
	}

	@Override
	public synchronized WorkDay getWorkDay(LocalDate date) {
		if (date == null) throw new NullPointerException("date");

		WorkDay workDay = workDaysCache.get(date);

		if (workDay == null) {
			workDay = loadWorkDay(date);
			workDaysCache.put(date, workDay);
		}

		return workDay;
	}

	private WorkDay loadWorkDay(LocalDate date) {
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

		LocalDate date = workDay.getDate();

		// calculate day offset and length
		int[] range = getWorkDayRange(date);
		int offset = range[0];
		int length = range[1];

		// get timetable data
		byte[] data = workDay.getData();

		// validate data array size
		if (data.length != length) throw new IllegalArgumentException("data.length: " + data.length + " <> " + length);

		// get key
		String key = getKey("data", date);

		// reset modification flag
		workDay.resetModified();

		// write data to database file
		store.writeData(key, offset, data);

		// add to cache
		workDaysCache.put(date, workDay);

	}

	@Override
	public synchronized WorkYear getWorkYear(LocalDate date) {
		if (date == null) throw new NullPointerException("date");

		int year = date.getYear();
		WorkYear workYear = workYearsCache.get(year);

		if (workYear == null) {
			workYear = loadWorkYear(date);
			workYearsCache.put(year, workYear);
		}

		return workYear;
	}

	private synchronized WorkYear loadWorkYear(LocalDate date) {
		if (date == null) throw new NullPointerException("date");

		// calculate number of days
		int days = date.lengthOfYear();
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

		LocalDate date = workYear.getDate();

		// get timetable data
		byte[] data = workYear.getData();

		// get key
		String key = getKey("calendar", date);

		// reset modification flag
		workYear.resetModified();

		// write data to database file
		store.writeData(key, 0, data);

		// add to cache
		int year = date.getYear();
		workYearsCache.put(year, workYear);

	}

	// ------------------------------------------------------------------------------------------------------------
	// private methods

	private String getKey(String prefix, LocalDate date) {
		return prefix + "-" + date.getYear();
	}

	private int[] getWorkDayRange(LocalDate date) {

		int length = 24 * 12; // number of 5-minute slots per day

		int day = date.getDayOfYear();
		int offset = (day - 1) * length;

		return new int[] { offset, length };

	}

}
