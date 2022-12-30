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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileStoreImplTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private FileStoreImpl store;

	@Before
	public void setUp() {
		store = new FileStoreImpl(tempFolder.getRoot());
	}

	@Test
	public void readData() throws IOException {

		// prepare
		File file = tempFolder.newFile("test.dat");
		FileUtils.writeStringToFile(file, "0123456789abcdefghijklmnopqrstuvwxyz", StandardCharsets.US_ASCII);

		// test
		byte[] data = store.readData("test", 10, 8);

		// assert
		assertArrayEquals(new byte[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' }, data);
	}

	@Test
	public void readData_whenFileDoesNotExist() {

		// assume
		File file = new File(tempFolder.getRoot(), "test.dat");
		Assume.assumeFalse(file.exists());

		// test
		byte[] data = store.readData("test", 10, 8);

		// assert
		assertArrayEquals(new byte[8], data);
	}

	@Test
	public void readData_whenFileIsShorter() throws IOException {

		// prepare
		File file = tempFolder.newFile("test.dat");
		FileUtils.writeStringToFile(file, "0123456789abcdefghijklmnopqrstuvwxyz", StandardCharsets.US_ASCII);

		// assume
		Assume.assumeTrue(file.isFile());
		Assume.assumeTrue(file.length() == 36);

		// test
		byte[] data = store.readData("test", 32, 8);

		// assert
		assertArrayEquals(new byte[] { 'w', 'x', 'y', 'z', 0, 0, 0, 0 }, data);
	}

	@Test
	public void readData_whenFileIsTooShort() throws IOException {

		// prepare
		File file = tempFolder.newFile("test.dat");
		FileUtils.writeStringToFile(file, "0123456789abcdefghijklmnopqrstuvwxyz", StandardCharsets.US_ASCII);

		// assume
		Assume.assumeTrue(file.isFile());
		Assume.assumeTrue(file.length() == 36);

		// test
		byte[] data = store.readData("test", 1024, 8);

		// assert
		assertArrayEquals(new byte[8], data);
	}

	@Test
	public void writeData() throws IOException {

		// assume
		File file = new File(tempFolder.getRoot(), "test.dat");
		Assume.assumeFalse(file.exists());

		// test
		store.writeData("test", 4, "abcdefgh".getBytes(StandardCharsets.UTF_8));

		// assert
		assertTrue(file.isFile());
		assertEquals(12, file.length());
		byte[] data = FileUtils.readFileToByteArray(file);
		assertArrayEquals(new byte[] { 0, 0, 0, 0, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' }, data);
	}

}