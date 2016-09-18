package net.markwalder.tools.worktime.db;

import java.util.Date;

public interface Database {

	public WorkDay getWorkDay(Date date);

	public void storeWorkDay(WorkDay workDay);

	public WorkYear getWorkYear(Date date);

	public void storeWorkYear(WorkYear workYear);

}
