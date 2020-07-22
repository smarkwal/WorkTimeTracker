package net.markwalder.tools.worktime.db;

import java.util.Date;

public interface Database {

	WorkDay getWorkDay(Date date);

	void storeWorkDay(WorkDay workDay);

	WorkYear getWorkYear(Date date);

	void storeWorkYear(WorkYear workYear);

}
