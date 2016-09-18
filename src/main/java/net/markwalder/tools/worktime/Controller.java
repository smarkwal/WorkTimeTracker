package net.markwalder.tools.worktime;

import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.db.WorkYear;

import java.util.Date;

public interface Controller {

	WorkDay getDisplayWorkDay();

	WorkYear getDisplayWorkYear();

	Database getDatabase();

	void start();

	void stop();

	void reportActive(boolean active);

	void workDayMousePressed(int slot, boolean shift);

	void wordDayMouseDragged(int slot);

	void workDayMouseReleased();

	void workYearMouseClicked(int slot, int mode);

	Date getDisplayDate();

	void setDisplayDate(Date date);

	void incrementDisplayDate();

	void decrementDisplayDate();

}
