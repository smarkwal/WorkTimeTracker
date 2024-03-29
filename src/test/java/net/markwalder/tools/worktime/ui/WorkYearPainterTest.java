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

import static net.markwalder.tools.worktime.ui.WorkYearPanel.MODE_VACATION;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import net.markwalder.tools.worktime.db.WorkYear;
import org.junit.Test;

public class WorkYearPainterTest extends AbstractPainterTest {

	@Test
	public void paint_2020() throws IOException {
		test(2020);
	}

	@Test
	public void paint_2021() throws IOException {
		test(2021);
	}

	@Test
	public void paint_2022() throws IOException {
		test(2022);
	}

	@Test
	public void paint_2023() throws IOException {
		test(2023);
	}

	private void test(int year) throws IOException {

		// prepare: test data
		LocalDate data = LocalDate.of(year, 12, 31);
		WorkYear workYear = database.getWorkYear(data);

		// prepare: painter
		WorkYearPainter painter = new WorkYearPainter(clock);
		BufferedImage image = new BufferedImage(WorkYearPanel.WIDTH, WorkYearPanel.HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		try {
			g2.setColor(new Color(238, 238, 238));
			g2.fillRect(0, 0, image.getWidth(), image.getHeight());

			// test
			painter.paint(g2, workYear, MODE_VACATION, 2 * 365 - 4);

		} finally {
			g2.dispose();
		}

		checkImage(image, "calendar-" + year + ".png");
	}

}