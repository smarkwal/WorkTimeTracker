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

import java.time.LocalDate;

public abstract class TimeTable {

	protected final LocalDate date;
	protected final byte[] data;

	protected int updates = 0;

	protected TimeTable(LocalDate date, byte[] data) {
		if (date == null) throw new IllegalArgumentException("date == null");
		if (data == null) throw new IllegalArgumentException("data == null");
		this.date = date;
		this.data = data;
	}

	public boolean isModified() {
		return updates > 0;
	}

	public void resetModified() {
		updates = 0;
	}

	public LocalDate getDate() {
		return date;
	}

	public byte[] getData() {
		return data;
	}

	public int getSize() {
		return data.length;
	}

	public byte getValue(int slot) {
		if (slot < 0 || slot >= data.length) return 0;
		return data[slot];
	}

	protected boolean getValue(int slot, int mask) {
		if (slot < 0 || slot >= data.length) return false;
		int bit = data[slot];
		return (bit & mask) > 0;
	}

	protected boolean setValue(int slot, int mask, boolean value) {
		if (slot < 0 || slot >= data.length) return false;
		int bit = data[slot];
		int oldBit = bit;
		if (value) {
			bit = bit | mask;
		} else {
			bit = bit & (0x7F - mask);
		}
		if (bit == oldBit) return false;
		data[slot] = (byte) bit;
		updates++;
		return true;
	}

}
