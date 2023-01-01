/*
 * Copyright 2023 Stephan Markwalder
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

import static net.markwalder.tools.worktime.db.TimeTableUtils.slot;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkContractTest {

	private static final LocalDate YEAR_2022 = LocalDate.of(2022, 1, 1);
	private static final LocalDate MONDAY = LocalDate.of(2022, 12, 12);
	private static final LocalDate TUESDAY = LocalDate.of(2022, 12, 13);
	private static final LocalDate WEDNESDAY = LocalDate.of(2022, 12, 14);
	private static final LocalDate SATURDAY = LocalDate.of(2022, 12, 17);
	private static final LocalDate SUNDAY = LocalDate.of(2022, 12, 18);

	@Mock
	Database database;

	@Spy
	WorkYear workYear = new WorkYear(YEAR_2022, new byte[YEAR_2022.lengthOfYear() * 2]);

	WorkContract contract = new WorkContract();


	@Before
	public void setUp() {
		when(database.getWorkYear(any())).thenReturn(workYear);

		// Tuesday = holiday
		workYear.setHoliday(slot(TUESDAY), true);
		workYear.setHoliday(slot(TUESDAY) + 1, true);

		// Wednesday = half day vacation
		workYear.setVacation(slot(WEDNESDAY) + 1, true);

		Mockito.clearInvocations(workYear);
	}

	@Test
	public void getWorkTime_onMonday() {

		// test
		int result = contract.getWorkTime(MONDAY, database);

		// assert
		assertEquals(403, result);

		// verify
		verifyDatabaseAccess(MONDAY);
	}

	@Test
	public void getWorkTime_onHoliday() {

		// test
		int result = contract.getWorkTime(TUESDAY, database);

		// assert
		assertEquals(0, result);

		// verify
		verifyDatabaseAccess(TUESDAY);
	}

	@Test
	public void getWorkTime_onHalfDayVacation() {

		// test
		int result = contract.getWorkTime(WEDNESDAY, database);

		// assert
		assertEquals(201, result);

		// verify
		verifyDatabaseAccess(WEDNESDAY);
	}

	@Test
	public void getWorkTime_onSaturday() {

		// test
		int result = contract.getWorkTime(SATURDAY, database);

		// assert
		assertEquals(0, result);

		// verify
		verifyNoInteractions(database, workYear);
	}

	@Test
	public void getWorkTime_onSunday() {

		// test
		int result = contract.getWorkTime(SUNDAY, database);

		// assert
		assertEquals(0, result);

		// verify
		verifyNoInteractions(database, workYear);
	}

	@Test
	public void getWorkTime_beforeJune2021() {

		// prepare
		LocalDate date = LocalDate.of(2021, 5, 31);

		// test
		int result = contract.getWorkTime(date, database);

		// assert
		assertEquals(504, result);

		// verify
		verifyDatabaseAccess(date);
	}

	private void verifyDatabaseAccess(LocalDate date) {
		verify(database).getWorkYear(date);
		verify(workYear).getValue(slot(date));
		verify(workYear).getValue(slot(date) + 1);
		verifyNoMoreInteractions(database, workYear);
	}

}