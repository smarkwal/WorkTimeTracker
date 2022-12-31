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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class DateTimeUtilsTest {

	@BeforeClass
	public static void beforeClass() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zurich"));
	}

	@Test
	public void getNow() {
		// test
		Date now = DateTimeUtils.getNow();
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Assert.assertEquals(format.format(new Date()), format.format(now));
	}

	@Test
	public void getToday() {
		// test
		Date now = DateTimeUtils.getToday();
		// assert
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Assert.assertEquals(dateFormat.format(new Date()) + " 00:00:00", dateTimeFormat.format(now));
	}

	@Test
	public void getStartOfDay() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getStartOfDay(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-24 00:00:00,000", format.format(date));

		// test
		date = DateTimeUtils.getStartOfDay(1234567890123L);
		// assert
		Assert.assertEquals("2009-02-14 00:00:00,000", format.format(date));
	}

	@Test
	public void getStartOfWeek() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getStartOfWeek(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-21 00:00:00,000", format.format(date));
	}

	@Test
	public void getEndOfWeek() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getEndOfWeek(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-27 00:00:00,000", format.format(date));
	}

	@Test
	public void getStartOfMonth() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getStartOfMonth(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-01 00:00:00,000", format.format(date));
	}

	@Test
	public void getEndOfMonth() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getEndOfMonth(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-28 00:00:00,000", format.format(date));
	}

	@Test
	public void getYear() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int year = DateTimeUtils.getYear(date);
		// assert
		Assert.assertEquals(1977, year);
	}

	@Test
	public void getMonth() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int month = DateTimeUtils.getMonth(date);
		// assert
		Assert.assertEquals(2, month);
	}

	@Test
	public void getDay() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int day = DateTimeUtils.getDay(date);
		// assert
		Assert.assertEquals(24, day);
	}

	@Test
	public void getHour() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int hour = DateTimeUtils.getHour(date);
		// assert
		Assert.assertEquals(13, hour);
	}

	@Test
	public void getStartOfYear() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getStartOfYear(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-01-01 00:00:00,000", format.format(date));
	}

	@Test
	public void getEndOfYear() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.getEndOfYear(date);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-12-31 00:00:00,000", format.format(date));
	}

	@Test
	public void getDayOfWeek() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int day = DateTimeUtils.getDayOfWeek(date);
		// assert
		Assert.assertEquals(5, day);
	}

	@Test
	public void getDayOfYear() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		int day = DateTimeUtils.getDayOfYear(date);
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
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		boolean result = DateTimeUtils.isWorkday(date);
		// assert
		Assert.assertTrue(result);
	}

	@Test
	public void isWeekend() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		boolean result = DateTimeUtils.isWeekend(date);
		// assert
		Assert.assertFalse(result);
	}

	@Test
	public void isFriday() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		boolean result = DateTimeUtils.isFriday(date);
		// assert
		Assert.assertFalse(result);
	}

	@Test
	public void max() {
		// prepare
		Date date1 = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		Date date2 = new Date(80, Calendar.MARCH, 13, 22, 9, 11);
		// test
		Date max = DateTimeUtils.max(date1, date2);
		// assert
		Assert.assertSame(date2, max);
		// test
		max = DateTimeUtils.max(date2, date1);
		// assert
		Assert.assertSame(date2, max);
	}

	@Test
	public void min() {
		// prepare
		Date date1 = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		Date date2 = new Date(80, Calendar.MARCH, 13, 22, 9, 11);
		// test
		Date min = DateTimeUtils.min(date1, date2);
		// assert
		Assert.assertSame(date1, min);
		// test
		min = DateTimeUtils.min(date2, date1);
		// assert
		Assert.assertSame(date1, min);
	}

	@Test
	public void addMinutes() {
		// prepare
		Date date = new Date(77, Calendar.FEBRUARY, 24, 13, 10, 51);
		// test
		date = DateTimeUtils.addMinutes(date, 145);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-24 15:35:51,000", format.format(date));
	}

	@Test
	public void getDate() {
		// test
		Date date = DateTimeUtils.getDate(1977, 2, 24);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-24 00:00:00,000", format.format(date));
	}

	@Test
	public void getDate_withTime() {
		// test
		Date date = DateTimeUtils.getDate(1977, 2, 24, 13, 10, 51);
		// assert
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		Assert.assertEquals("1977-02-24 13:10:51,000", format.format(date));
	}

}