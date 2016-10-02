package net.markwalder.tools.worktime.db.store;

public interface Store {

	byte[] readData(String key, int offset, int length);

	void writeData(String key, int offset, byte[] data);

}
