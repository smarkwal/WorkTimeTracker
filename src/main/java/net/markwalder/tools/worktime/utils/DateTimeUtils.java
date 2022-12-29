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

package net.markwalder.tools.worktime.utils;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class DateTimeUtils {

	public static Date getNow() {
		return new Date();
	}

	public static Date getToday() {
		Date date = new Date();
		return getStartOfDay(date);
	}

	public static Date getStartOfDay(long time) {
		Date date = new Date(time);
		return getStartOfDay(date);
	}

	public static Date getStartOfDay(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	public static Date getStartOfWeek(Date date) {
		date = getStartOfDay(date);
		int dayOfWeek = getDayOfWeek(date);
		int correction = (dayOfWeek + 5) % 7;
		return DateUtils.addDays(date, -correction);
	}

	public static Date getEndOfWeek(Date date) {
		date = getStartOfWeek(date);
		date = DateUtils.addDays(date, 6);
		return date;
	}

	public static Date getStartOfMonth(Date date) {
		return DateUtils.truncate(date, Calendar.MONTH);
	}

	public static Date getEndOfMonth(Date date) {
		date = getStartOfMonth(date);
		date = DateUtils.addMonths(date, 1);
		date = DateUtils.addDays(date, -1);
		return date;
	}

	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	public static Date getStartOfYear(Date date) {
		return DateUtils.truncate(date, Calendar.YEAR);
	}

	public static Date getEndOfYear(Date date) {
		date = getStartOfYear(date);
		date = DateUtils.addYears(date, 1);
		date = DateUtils.addDays(date, -1);
		return date;
	}

	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static int getDaysInMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getDaysInYear(int year) {
		if (year % 4 == 0) {
			return 366;
		} else {
			return 365;
		}
	}

	public static boolean isWorkday(Date date) {
		return !isWeekend(date);
	}

	public static boolean isWeekend(Date date) {
		int dayOfWeek = getDayOfWeek(date);
		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	public static boolean isFriday(Date date) {
		int dayOfWeek = getDayOfWeek(date);
		return dayOfWeek == Calendar.FRIDAY;
	}

	public static Date max(Date date1, Date date2) {
		if (date1.before(date2)) {
			return date2;
		} else {
			return date1;
		}
	}

	public static Date min(Date date1, Date date2) {
		if (date1.before(date2)) {
			return date1;
		} else {
			return date2;
		}
	}

	public static Date getDate(int year, int month, int day) {
		//noinspection MagicConstant
		return new Date(year - 1900, month - 1, day);
	}

	public static Date addMinutes(Date date, int minutes) {
		return new Date(date.getTime() + minutes * 60 * 1000);
	}

	public static boolean isToday(Date date) {
		int today = getDayOfYear(getToday());
		int day = getDayOfYear(date);
		return day == today;
	}

}
