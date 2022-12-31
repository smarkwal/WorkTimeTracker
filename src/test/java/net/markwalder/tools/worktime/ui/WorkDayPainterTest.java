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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import net.markwalder.tools.worktime.TestUtils;
import net.markwalder.tools.worktime.db.Database;
import net.markwalder.tools.worktime.db.DatabaseImpl;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.db.store.FileStoreImpl;
import org.junit.Assert;
import org.junit.Test;

public class WorkDayPainterTest {

	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final Instant instant = Instant.parse("2022-12-22T15:42:15.00Z");
	private static final Clock clock = Clock.fixed(instant, Clock.systemDefaultZone().getZone());

	private final Database database = new DatabaseImpl(new FileStoreImpl(new File("src/test/resources")));

	@Test
	public void paint_2020_12_31() throws IOException {
		test(LocalDate.of(2020, 12, 31));
	}

	@Test
	public void paint_2021_12_31() throws IOException {
		test(LocalDate.of(2021, 12, 31));
	}

	@Test
	public void paint_2022_01_01() throws IOException {
		test(LocalDate.of(2022, 1, 1));
	}

	@Test
	public void paint_2022_03_27() throws IOException {
		test(LocalDate.of(2022, 3, 27));
	}

	@Test
	public void paint_2022_05_16() throws IOException {
		test(LocalDate.of(2022, 5, 16));
	}

	@Test
	public void paint_2022_02_24() throws IOException {
		test(LocalDate.of(2022, 2, 24));
	}

	@Test
	public void paint_2022_08_24() throws IOException {
		test(LocalDate.of(2022, 8, 24));
	}

	@Test
	public void paint_2022_10_30() throws IOException {
		test(LocalDate.of(2022, 10, 30));
	}

	@Test
	public void paint_2022_11_24() throws IOException {
		test(LocalDate.of(2022, 11, 24));
	}

	@Test
	public void paint_2022_12_21() throws IOException {
		test(LocalDate.of(2022, 12, 21));
	}

	@Test
	public void paint_2022_12_22() throws IOException {
		test(LocalDate.of(2022, 12, 22));
	}

	@Test
	public void paint_2022_12_23() throws IOException {
		test(LocalDate.of(2022, 12, 23));
	}

	@Test
	public void paint_2022_12_31() throws IOException {
		test(LocalDate.of(2022, 12, 31));
	}

	@Test
	public void paint_2023_01_01() throws IOException {
		test(LocalDate.of(2023, 1, 1));
	}

	private void test(LocalDate date) throws IOException {

		// prepare: test data
		WorkDay workDay = database.getWorkDay(date);

		// prepare: painter
		WorkDayPainter painter = new WorkDayPainter(database, clock);
		BufferedImage image = new BufferedImage(WorkDayPanel.WIDTH, WorkDayPanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		try {
			g2.setColor(new Color(238, 238, 238));
			g2.fillRect(0, 0, image.getWidth(), image.getHeight());

			// test
			painter.paint(g2, workDay);

		} finally {
			g2.dispose();
		}

		File file = new File("src/test/resources/data-" + dateFormat.format(date) + ".png");
		if (TestUtils.generateTestResources()) {
			ImageIO.write(image, "png", file);
		}

		BufferedImage expectedImage = ImageIO.read(file);

		long difference = TestUtils.compareImages(expectedImage, image);
		Assert.assertEquals(0, difference);
	}

}