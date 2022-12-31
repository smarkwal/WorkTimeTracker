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

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class DateTimeUtilsTest {

	private static final Date DATE = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	@BeforeClass
	public static void beforeClass() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zurich"));
	}

	@Test
	public void getNow() {
		// prepare
		Clock clock = Clock.systemDefaultZone();
		// test
		Date result = DateTimeUtils.getNow(clock);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Assert.assertEquals(format.format(new Date(clock.millis())), format.format(result));
	}

	@Test
	public void getToday() {
		// prepare
		Clock clock = Clock.systemDefaultZone();
		// test
		Date result = DateTimeUtils.getToday(clock);
		// assert
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Assert.assertEquals(dateFormat.format(new Date(clock.millis())) + " 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getStartOfDay() {
		// test
		Date result = DateTimeUtils.getStartOfDay(DATE);
		// assert
		Assert.assertEquals("1977-02-24 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getStartOfWeek() {
		// test
		Date result = DateTimeUtils.getStartOfWeek(DATE);
		// assert
		Assert.assertEquals("1977-02-21 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getEndOfWeek() {
		// test
		Date result = DateTimeUtils.getEndOfWeek(DATE);
		// assert
		Assert.assertEquals("1977-02-27 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getStartOfMonth() {
		// test
		Date result = DateTimeUtils.getStartOfMonth(DATE);
		// assert
		Assert.assertEquals("1977-02-01 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getEndOfMonth() {
		// test
		Date result = DateTimeUtils.getEndOfMonth(DATE);
		// assert
		Assert.assertEquals("1977-02-28 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getYear() {
		// test
		int year = DateTimeUtils.getYear(DATE);
		// assert
		Assert.assertEquals(1977, year);
	}

	@Test
	public void getMonth() {
		// test
		int month = DateTimeUtils.getMonth(DATE);
		// assert
		Assert.assertEquals(2, month);
	}

	@Test
	public void getDay() {
		// test
		int day = DateTimeUtils.getDay(DATE);
		// assert
		Assert.assertEquals(24, day);
	}

	@Test
	public void getHour() {
		// test
		int hour = DateTimeUtils.getHour(DATE);
		// assert
		Assert.assertEquals(13, hour);
	}

	@Test
	public void getStartOfYear() {
		// test
		Date result = DateTimeUtils.getStartOfYear(DATE);
		// assert
		Assert.assertEquals("1977-01-01 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getEndOfYear() {
		// test
		Date result = DateTimeUtils.getEndOfYear(DATE);
		// assert
		Assert.assertEquals("1977-12-31 00:00:00.000", FORMAT.format(result));
	}

	@Test
	public void getDayOfWeek() {
		// test
		int day = DateTimeUtils.getDayOfWeek(DATE);
		// assert
		Assert.assertEquals(5, day);
	}

	@Test
	public void getDayOfYear() {
		// test
		int day = DateTimeUtils.getDayOfYear(DATE);
		// assert
		Assert.assertEquals(55, day);
	}

	@Test
	public void getDaysInMonth() {
		// test
		int days = DateTimeUtils.getDaysInMonth(1977, 2);
		// assert
		Assert.assertEquals(28, days);
	}

	@Test
	public void getDaysInYear() {
		// test
		int days = DateTimeUtils.getDaysInYear(1977);
		// assert
		Assert.assertEquals(365, days);
		// test
		days = DateTimeUtils.getDaysInYear(1980);
		// assert
		Assert.assertEquals(366, days);
	}

	@Test
	public void isWorkday() {
		// test
		boolean result = DateTimeUtils.isWorkday(DATE);
		// assert
		Assert.assertTrue(result);
	}

	@Test
	public void isWeekend() {
		// test
		boolean result = DateTimeUtils.isWeekend(DATE);
		// assert
		Assert.assertFalse(result);
	}

	@Test
	public void isFriday() {
		// test
		boolean result = DateTimeUtils.isFriday(DATE);
		// assert
		Assert.assertFalse(result);
	}

	@Test
	public void max() {
		// prepare
		Date date2 = new Date(80, Calendar.MARCH, 13, 22, 9, 11);
		// test
		Date max = DateTimeUtils.max(DATE, date2);
		// assert
		Assert.assertSame(date2, max);
		// test
		max = DateTimeUtils.max(date2, DATE);
		// assert
		Assert.assertSame(date2, max);
	}

	@Test
	public void min() {
		// prepare
		Date date2 = new Date(80, Calendar.MARCH, 13, 22, 9, 11);
		// test
		Date min = DateTimeUtils.min(DATE, date2);
		// assert
		Assert.assertSame(DATE, min);
		// test
		min = DateTimeUtils.min(date2, DATE);
		// assert
		Assert.assertSame(DATE, min);
	}

	@Test
	public void addMinutes() {
		// test
		Date result = DateTimeUtils.addMinutes(DATE, 145);
		// assert
		Assert.assertEquals("1977-02-24 15:35:51.000", FORMAT.format(result));
	}

	@Test
	public void getDate() {
		// test
		Date date = DateTimeUtils.getDate(1977, 2, 24);
		// assert
		Assert.assertEquals("1977-02-24 00:00:00.000", FORMAT.format(date));
	}

	@Test
	public void getDate_withTime() {
		// test
		Date date = DateTimeUtils.getDate(1977, 2, 24, 13, 10, 51);
		// assert
		Assert.assertEquals("1977-02-24 13:10:51.000", FORMAT.format(date));
	}

}