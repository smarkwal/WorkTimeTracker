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

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkYear extends TimeTable {

	private static final int HOLIDAY = 0x01;
	private static final int COMPENSATION = 0x02;
	private static final int VACATION = 0x04;
	private static final int FREE = 0x08;

	private int holidayCount = 0;
	private int compensationCount = 0;
	private int vacationCount = 0;
	private int freeCount = 0;

	public WorkYear(Date date, byte[] data) {
		super(date, data);
		for (byte bit : data) {
			if ((bit & HOLIDAY) > 0) {
				holidayCount++;
			}
			if ((bit & COMPENSATION) > 0) {
				compensationCount++;
			}
			if ((bit & VACATION) > 0) {
				vacationCount++;
			}
			if ((bit & FREE) > 0) {
				freeCount++;
			}
		}
	}

	public boolean isHoliday(int slot) {
		return getValue(slot, HOLIDAY);
	}

	public void setHoliday(int slot, boolean value) {
		boolean modified = setValue(slot, HOLIDAY, value);
		if (modified) {
			if (value) {
				holidayCount++;
			} else {
				holidayCount--;
			}
		}
	}

	public boolean isCompensation(int slot) {
		return getValue(slot, COMPENSATION);
	}

	public void setCompensation(int slot, boolean value) {
		boolean modified = setValue(slot, COMPENSATION, value);
		if (modified) {
			if (value) {
				compensationCount++;
			} else {
				compensationCount--;
			}
		}
	}

	public boolean isVacation(int slot) {
		return getValue(slot, VACATION);
	}

	public void setVacation(int slot, boolean value) {
		boolean modified = setValue(slot, VACATION, value);
		if (modified) {
			if (value) {
				vacationCount++;
			} else {
				vacationCount--;
			}
		}
	}

	public boolean isFree(int slot) {
		return getValue(slot, FREE);
	}

	public void setFree(int slot, boolean value) {
		boolean modified = setValue(slot, FREE, value);
		if (modified) {
			if (value) {
				freeCount++;
			} else {
				freeCount--;
			}
		}
	}

	public int getHolidayCount() {
		return holidayCount;
	}

	public int getCompensationCount() {
		return compensationCount;
	}

	public int getVacationCount() {
		return vacationCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	@Override
	public String toString() {

		StringBuilder buffer = new StringBuilder(data.length);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		buffer.append("Date: ").append(dateFormat.format(date)).append("\n");

		int h = 4;
		int w = data.length / h;
		for (int x = 0; x < w; x++) {
			if (x % 12 == 0) buffer.append("+");
			buffer.append("-");
		}
		buffer.append("+\n");
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x % 12 == 0) buffer.append("|");
				int i = y * w + x;
				byte bit = data[i];
				char sign = '?';
				switch (bit) {
					case 0: // no data
						sign = ' ';
						break;
					case 1: // running
						sign = '=';
						break;
					case 3: // running + active
						sign = '0';
						break;
					case 4: // working
						sign = 'x';
						break;
					case 5: // running + working (but not active)
						sign = 'x';
						break;
					case 7: // running + active + working
						sign = 'X';
						break;
					case 8: // free
						sign = '-';
						break;
					case 9: // free + running (but not active)
						sign = '-';
						break;
					case 11: // free + running + active
						sign = '=';
						break;
				}
				buffer.append(sign);
			}
			buffer.append("|\n");
			for (int x = 0; x < w; x++) {
				if (x % 12 == 0) buffer.append("+");
				buffer.append("-");
			}
			buffer.append("+\n");
		}
		buffer.append("Running: ").append(holidayCount);
		buffer.append(", Active: ").append(compensationCount);
		buffer.append(", Working: ").append(vacationCount);
		buffer.append(", Free: ").append(freeCount).append("\n");

		return buffer.toString();
	}

}
