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

import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeUtilsTest {

	private static final LocalDate DATE = LocalDate.of(1977, 2, 24);

	@Test
	public void getStartOfWeek() {
		// test
		LocalDate result = DateTimeUtils.getStartOfWeek(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 2, 21), result);
	}

	@Test
	public void getEndOfWeek() {
		// test
		LocalDate result = DateTimeUtils.getEndOfWeek(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 2, 27), result);
	}

	@Test
	public void getStartOfMonth() {
		// test
		LocalDate result = DateTimeUtils.getStartOfMonth(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 2, 1), result);
	}

	@Test
	public void getEndOfMonth() {
		// test
		LocalDate result = DateTimeUtils.getEndOfMonth(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 2, 28), result);
	}

	@Test
	public void getStartOfYear() {
		// test
		LocalDate result = DateTimeUtils.getStartOfYear(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 1, 1), result);
	}

	@Test
	public void getEndOfYear() {
		// test
		LocalDate result = DateTimeUtils.getEndOfYear(DATE);
		// assert
		Assert.assertEquals(LocalDate.of(1977, 12, 31), result);
	}

	@Test
	public void getDaysInMonth() {
		// test
		int days = DateTimeUtils.getDaysInMonth(1977, 2);
		// assert
		Assert.assertEquals(28, days);
	}

	@Test
	public void isWeekend() {
		// test
		boolean result = DateTimeUtils.isWeekend(DATE);
		// assert
		Assert.assertFalse(result);
	}

}