package net.markwalder.tools.worktime.db;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class TimeTable {

	protected final Date date;
	protected final byte[] data;

	protected int updates = 0;

	public TimeTable(Date date, byte[] data) {
		if (date == null) throw new NullPointerException("date");
		if (data == null) throw new NullPointerException("data");
		this.date = DateUtils.truncate(date, Calendar.DATE);
		this.data = data;
	}

	public boolean isModified() {
		return updates > 0;
	}

	public void resetModified() {
		updates = 0;
	}

	public Date getDate() {
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
