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

package net.markwalder.tools.worktime.db.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStoreImpl implements Store {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreImpl.class);

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

	private final File directory;

	@Inject
	public FileStoreImpl() {
		this(new File("."));
	}

	// visible for testing
	public FileStoreImpl(File directory) {
		this.directory = directory;
	}

	@Override
	public byte[] readData(String key, int offset, int length) {

		LOGGER.debug("Reading data for key '{}' from offset {} with length {}.", key, offset, length);

		// get database file
		File file = getFile(key);

		lock.readLock().lock();
		try {

			// prepare a new (empty) data array
			byte[] data = new byte[length];

			// if file does not exist -> return empty data array
			if (!file.exists()) return data;

			// open database file
			try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

				// jump to offset
				raf.seek(offset);

				// read data
				int pos = 0;
				while (true) {
					int remaining = length - pos;
					int bytes = raf.read(data, pos, remaining);
					if (bytes < 0 || bytes == remaining) break;
					pos = pos + bytes;
				}

			}

			// return data array
			return data;

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			lock.readLock().unlock();
		}

	}

	@Override
	public void writeData(String key, int offset, byte[] data) {

		LOGGER.debug("Writing data for key '{}' from offset {} with length {}.", key, offset, data.length);

		// get database file
		File file = getFile(key);

		lock.writeLock().lock();

		// open database file
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

			// jump to offset
			raf.seek(offset);

			// write data
			raf.write(data);

		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			lock.writeLock().unlock();
		}

	}

	private File getFile(String key) {
		return new File(directory, key + ".dat");
	}

}
