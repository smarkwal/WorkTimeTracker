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

import java.util.Date;
import net.markwalder.tools.worktime.TestUtils;
import net.markwalder.tools.worktime.utils.DateTimeUtils;
import org.junit.Test;

public class WorkDayTest {

	@Test
	public void test_toString() {

		// prepare
		Date date = DateTimeUtils.getDate(2022, 2, 24);
		byte[] data = new byte[24 * 12];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i % 12);
		}
		WorkDay workDay = new WorkDay(date, data);

		// test
		String result = workDay.toString();

		// assert
		String expectedResult = TestUtils.readStringFromResource("WorkDay-toString.txt");
		assertEquals(expectedResult, result);
	}

	@Test
	public void test_toString_forDSTStart() {

		// prepare
		Date date = DateTimeUtils.getDate(2022, 3, 27);
		byte[] data = new byte[23 * 12]; // 1 less hour due to DST change
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i % 12);
		}
		WorkDay workDay = new WorkDay(date, data);

		// test
		String result = workDay.toString();

		// assert
		String expectedResult = TestUtils.readStringFromResource("WorkDay-toString-DST-start.txt");
		assertEquals(expectedResult, result);
	}

	@Test
	public void test_toString_forDSTEnd() {

		// prepare
		Date date = DateTimeUtils.getDate(2022, 10, 30);
		byte[] data = new byte[25 * 12]; // 1 extra hour due to DST change
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i % 12);
		}
		WorkDay workDay = new WorkDay(date, data);

		// test
		String result = workDay.toString();

		// assert
		String expectedResult = TestUtils.readStringFromResource("WorkDay-toString-DST-end.txt");
		assertEquals(expectedResult, result);

	}

}