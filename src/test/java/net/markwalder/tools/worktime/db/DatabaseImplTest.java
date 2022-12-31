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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import net.markwalder.tools.worktime.db.store.Store;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseImplTest {

	private static final LocalDate DATE = LocalDate.of(2022, 12, 30);

	@Mock
	Store store;

	@InjectMocks
	DatabaseImpl database;

	@Test
	public void getWorkDay() {

		// prepare
		byte[] data = RandomUtils.nextBytes(24 * 12);
		when(store.readData("data-2022", 104544, data.length)).thenReturn(data);

		// test
		WorkDay workDay = database.getWorkDay(DATE);

		// assert
		assertNotNull(workDay);
		assertEquals(DATE, workDay.getDate());
		assertArrayEquals(data, workDay.getData());

		// verify
		verify(store).readData("data-2022", 104544, data.length);
		verifyNoMoreInteractions(store);

		// test: cached
		WorkDay workDay2 = database.getWorkDay(DATE);

		// assert
		assertSame(workDay2, workDay);

		// verify
		verifyNoMoreInteractions(store);
	}

	@Test
	public void storeWorkDay() {

		// prepare
		byte[] data = RandomUtils.nextBytes(24 * 12);
		WorkDay workDay = new WorkDay(DATE, data);

		// test
		database.storeWorkDay(workDay);

		// verify
		verify(store).writeData("data-2022", 104544, data);
		verifyNoMoreInteractions(store);

		// test: cached
		WorkDay workDay2 = database.getWorkDay(DATE);
		assertSame(workDay2, workDay);
		verifyNoMoreInteractions(store);
	}

	@Test
	public void getWorkYear() {

		// prepare
		byte[] data = RandomUtils.nextBytes(365 * 2);
		when(store.readData("calendar-2022", 0, data.length)).thenReturn(data);

		// test
		WorkYear workYear = database.getWorkYear(DATE);

		// assert
		assertNotNull(workYear);
		assertEquals(DATE.withDayOfYear(1), workYear.getDate()); // date must have been rounded down to the beginning of the year
		assertArrayEquals(data, workYear.getData());

		// verify
		verify(store).readData("calendar-2022", 0, data.length);
		verifyNoMoreInteractions(store);

		// test: cached
		WorkYear workYear2 = database.getWorkYear(DATE);
		assertSame(workYear2, workYear);
		verifyNoMoreInteractions(store);
	}

	@Test
	public void storeWorkYear() {

		// prepare
		byte[] data = RandomUtils.nextBytes(365 * 2);
		WorkYear workYear = new WorkYear(DATE, data);

		// test
		database.storeWorkYear(workYear);

		// verify
		verify(store).writeData("calendar-2022", 0, data);
		verifyNoMoreInteractions(store);

		// test: cached
		WorkYear workYear2 = database.getWorkYear(DATE);
		assertSame(workYear2, workYear);
		verifyNoMoreInteractions(store);
	}

}