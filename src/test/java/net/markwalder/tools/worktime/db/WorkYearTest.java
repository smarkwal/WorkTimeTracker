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

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import net.markwalder.tools.worktime.TestUtils;
import org.junit.Test;

public class WorkYearTest {

	@Test
	public void test_toString_2022() {

		// prepare
		WorkYear workDay = prepareWorkYear(2022);

		// test
		String result = workDay.toString();

		// assert
		String expectedResult = TestUtils.readStringFromResource("WorkYear-toString-2022.txt");
		assertEquals(expectedResult, result);
	}

	@Test
	public void test_toString_2024() {

		// prepare
		WorkYear workDay = prepareWorkYear(2024);

		// test
		String result = workDay.toString();

		// assert
		String expectedResult = TestUtils.readStringFromResource("WorkYear-toString-2024.txt");
		assertEquals(expectedResult, result);
	}

	private static WorkYear prepareWorkYear(int year) {

		LocalDate date = LocalDate.of(year, 1, 1);

		int daysInYear = date.lengthOfYear();
		int slots = daysInYear * 2;
		byte[] data = new byte[slots];

		byte[] values = new byte[] { 0, 1, 2, 4, 8 };
		for (int i = 0; i < data.length; i++) {
			data[i] = values[i % values.length];
		}

		return new WorkYear(date, data);
	}

}