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

import java.util.Date;
import net.markwalder.tools.worktime.utils.DateTimeUtils;

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
		super(DateTimeUtils.getStartOfYear(date), data);
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

		int year = DateTimeUtils.getYear(date);
		buffer.append("Year: ").append(year).append("\n");
		buffer.append("+");
		buffer.append("-".repeat(31));
		buffer.append("+\n");

		for (int month = 1; month <= 12; month++) {
			int days = DateTimeUtils.getDaysInMonth(year, month);
			int daysBeforeMonth = DateTimeUtils.getDayOfYear(DateTimeUtils.getDate(year, month, 1)) - 1;

			for (int h = 0; h < 2; h++) { // morning (h = 0) and afternoon (h = 1)
				buffer.append("|");
				for (int day = 1; day <= days; day++) {
					int slot = (daysBeforeMonth + day - 1) * 2 + h;
					int bits = data[slot];
					switch (bits) {
						case 0:
							buffer.append(" ");
							break;
						case HOLIDAY:
							buffer.append("H");
							break;
						case COMPENSATION:
							buffer.append("C");
							break;
						case VACATION:
							buffer.append("V");
							break;
						case FREE:
							buffer.append("F");
							break;
						default:
							String sign = Integer.toHexString(bits);
							buffer.append(sign);
							break;
					}
				}
				buffer.append("|\n");
			}

			buffer.append("+");
			buffer.append("-".repeat(31));
			buffer.append("+\n");
		}

		buffer.append("Holiday: ").append(holidayCount);
		buffer.append(", Compensation: ").append(compensationCount);
		buffer.append(", Vacation: ").append(vacationCount);
		buffer.append(", Free: ").append(freeCount).append("\n");

		return buffer.toString();
	}

}
