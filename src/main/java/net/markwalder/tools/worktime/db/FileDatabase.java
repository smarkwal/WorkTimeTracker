package net.markwalder.tools.worktime.db;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileDatabase extends AbstractDatabase {

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);

	@Override
	protected byte[] readData(String key, int offset, int length) {

		// get database file
		File file = new File(key + ".dat");

		lock.readLock().lock();
		try {

			// prepare a new (empty) data array
			byte[] data = new byte[length];

			// if file does not exists -> return empty data array
			if (!file.exists()) return data;

			// open database file
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(file, "r");

				// jump to offset
				raf.seek(offset);

				// read data
				int pos = 0;
				while (true) {
					int bytes = raf.read(data, pos, length - pos);
					if (bytes < 0) break; // todo: throw an exception ?
					pos = pos + bytes;
					if (pos == length) break;
				}

			} finally {
				IOUtils.closeQuietly(raf);
			}

			// return data array
			return data;

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			lock.readLock().unlock();
		}

	}

	@Override
	protected void writeData(String key, int offset, byte[] data) {

		// get database file
		File file = new File(key + ".dat");

		lock.writeLock().lock();
		try {

			// open database file
			RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(file, "rw");

				// jump to offset
				raf.seek(offset);

				// write data
				raf.write(data);

			} finally {
				IOUtils.closeQuietly(raf);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			lock.writeLock().unlock();
		}

	}

}
