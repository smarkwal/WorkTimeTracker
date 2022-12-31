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

package net.markwalder.tools.worktime.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import javax.imageio.ImageIO;
import net.markwalder.tools.worktime.TestUtils;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseImpl;
import net.markwalder.tools.worktime.db.store.FileStoreImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

abstract class AbstractPainterTest {

	static final Instant instant = Instant.parse("2022-12-22T15:42:15.00Z");
	static final Clock clock = Clock.fixed(instant, Clock.systemDefaultZone().getZone());

	final Database database = new DatabaseImpl(new FileStoreImpl(new File("src/test/resources")));

	static void checkImage(BufferedImage image, String fileName) throws IOException {

		File resourceFile = new File("src/test/resources/" + fileName);

		// compare image with expected image
		BufferedImage expectedImage = ImageIO.read(resourceFile);
		long difference = TestUtils.compareImages(expectedImage, image);
		if (difference > 0) {

			if (TestUtils.generateTestResources()) {
				ImageIO.write(image, "png", resourceFile);
			}

			// save generated image for manual inspection
			File reportFile = new File("build/reports/tests/test/images/" + fileName);
			FileUtils.createParentDirectories(reportFile);
			ImageIO.write(image, "png", reportFile);

			// save difference image for manual inspection
			BufferedImage diffImage = TestUtils.diffImages(expectedImage, image);
			File diffFile = new File(reportFile.getParentFile(), fileName.replace(".png", "-diff.png"));
			ImageIO.write(diffImage, "png", diffFile);

			// test failed
			Assert.fail("Images differ by " + difference + " pixels. See " + diffFile.getPath());
		}
	}

}