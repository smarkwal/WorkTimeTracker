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

public class WorkDay extends TimeTable {

	private static final int RUNNING = 0x01;
	private static final int ACTIVE = 0x02;
	private static final int WORKING = 0x04;
	private static final int FREE = 0x08;

	private int runningCount = 0;
	private int activeCount = 0;
	private int workingCount = 0;
	private int freeCount = 0;

	public WorkDay(Date date, byte[] data) {
		super(date, data);
		for (byte bit : data) {
			if ((bit & RUNNING) > 0) {
				runningCount++;
			}
			if ((bit & ACTIVE) > 0) {
				activeCount++;
			}
			if ((bit & WORKING) > 0) {
				workingCount++;
			}
			if ((bit & FREE) > 0) {
				freeCount++;
			}
		}
	}

	public boolean isRunning(int slot) {
		return getValue(slot, RUNNING);
	}

	public void setRunning(int slot, boolean value) {
		boolean modified = setValue(slot, RUNNING, value);
		if (modified) {
			if (value) {
				runningCount++;
			} else {
				runningCount--;
			}
		}
	}

	public boolean isActive(int slot) {
		return getValue(slot, ACTIVE);
	}

	public void setActive(int slot, boolean value) {
		boolean modified = setValue(slot, ACTIVE, value);
		if (modified) {
			if (value) {
				activeCount++;
			} else {
				activeCount--;
			}
		}
	}

	public boolean isWorking(int slot) {
		return getValue(slot, WORKING);
	}

	public void setWorking(int slot, boolean value) {
		boolean modified = setValue(slot, WORKING, value);
		if (modified) {
			if (value) {
				workingCount++;
			} else {
				workingCount--;
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

	public int getRunningCount() {
		return runningCount;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public int getWorkingCount() {
		return workingCount;
	}

	public int getFreeCount() {
		return freeCount;
	}

	@Override
	public String toString() {

		StringBuilder buffer = new StringBuilder(data.length);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		buffer.append("Date: ").append(dateFormat.format(date)).append("\n");

		for (int i = 0; i < data.length; i++) {
			if (i % 72 == 0) {
				if (i > 0) {
					buffer.append("|\n");
				}
				for (int x = 0; x < 72; x++) {
					if (x % 12 == 0) buffer.append("+");
					buffer.append("-");
				}
				buffer.append("+\n");
			}
			if (i % 12 == 0) buffer.append("|");

			byte bit = data[i];
			char sign = '?';
			switch (bit) {
				case 0: // no data
					sign = ' ';
					break;
				case 1: // running
				case 11: // free + running + active
					sign = '=';
					break;
				case 3: // running + active
					sign = '0';
					break;
				case 4: // working
				case 5: // running + working (but not active)
					sign = 'x';
					break;
				case 7: // running + active + working
					sign = 'X';
					break;
				case 8: // free
				case 9: // free + running (but not active)
					sign = '-';
					break;
			}
			buffer.append(sign);
		}

		int lastLineLength = data.length % 72;
		if (lastLineLength == 0) lastLineLength = 72;
		buffer.append("|\n");
		for (int x = 0; x < lastLineLength; x++) {
			if (x % 12 == 0) buffer.append("+");
			buffer.append("-");
		}
		buffer.append("+\n");

		buffer.append("Running: ").append(runningCount);
		buffer.append(", Active: ").append(activeCount);
		buffer.append(", Working: ").append(workingCount);
		buffer.append(", Free: ").append(freeCount).append("\n");

		return buffer.toString();
	}

}
