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

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateTimeUtils {

	private DateTimeUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static LocalDate getStartOfWeek(LocalDate date) {
		return date.minusDays(date.getDayOfWeek().getValue() - 1);
	}

	public static LocalDate getEndOfWeek(LocalDate date) {
		return date.plusDays(7 - date.getDayOfWeek().getValue());
	}

	public static LocalDate getStartOfMonth(LocalDate date) {
		return date.minusDays(date.getDayOfMonth() - 1);
	}

	public static LocalDate getEndOfMonth(LocalDate date) {
		return date.plusDays(date.lengthOfMonth() - date.getDayOfMonth());
	}

	public static LocalDate getStartOfYear(LocalDate date) {
		return date.withDayOfYear(1);
	}

	public static LocalDate getEndOfYear(LocalDate date) {
		return date.withDayOfYear(date.lengthOfYear());
	}

	public static int getDaysInMonth(int year, int month) {
		LocalDate date = LocalDate.of(year, month, 1);
		return date.lengthOfMonth();
	}

	public static boolean isWeekend(LocalDate date) {
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
	}

}
