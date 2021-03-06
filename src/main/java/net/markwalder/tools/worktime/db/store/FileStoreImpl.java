package net.markwalder.tools.worktime.db.store;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileStoreImpl implements Store {

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

	private final File directory;

	public FileStoreImpl() {
		directory = new File(".");
	}

	@Override
	public byte[] readData(String key, int offset, int length) {

		// get database file
		File file = getFile(key);

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
	public void writeData(String key, int offset, byte[] data) {

		// get database file
		File file = getFile(key);

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

	private File getFile(String key) {
		return new File(directory, key + ".dat");
	}

}
