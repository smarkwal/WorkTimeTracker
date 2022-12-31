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

package net.markwalder.tools.worktime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class TestUtils {

	public static void pause(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean generateTestResources() {
		return System.getProperty("generate.test.resources") != null;
	}

	public static String readStringFromResource(String resourceName) {
		return new String(readByteArrayFromResource(resourceName));
	}

	public static byte[] readByteArrayFromResource(String resourceName) {
		try (InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(resourceName)) {
			if (stream == null) throw new IllegalArgumentException("Resource not found: " + resourceName);
			return stream.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	/**
	 * Compares two images pixel by pixel.
	 *
	 * @param image1 Image 1
	 * @param image2 Image 2
	 * @return Number of different pixels.
	 */
	public static int compareImages(BufferedImage image1, BufferedImage image2) {

		int width = image1.getWidth();
		int height = image1.getHeight();
		if (image2.getWidth() != width || image2.getHeight() != height) {
			throw new IllegalArgumentException("Error: Images dimensions mismatch");
		}

		// count number of differing pixels
		int diff = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int pixel1 = image1.getRGB(x, y);
				Color color1 = new Color(pixel1, true);
				int r1 = color1.getRed();
				int g1 = color1.getGreen();
				int b1 = color1.getBlue();

				int pixel2 = image2.getRGB(x, y);
				Color color2 = new Color(pixel2, true);
				int r2 = color2.getRed();
				int g2 = color2.getGreen();
				int b2 = color2.getBlue();

				// compare RGB values of the pixels
				if (r1 != r2 || g1 != g2 || b1 != b2) {
					diff++;
				}

			}
		}

		return diff;
	}

	public static BufferedImage diffImages(BufferedImage image1, BufferedImage image2) {

		int width = image1.getWidth();
		int height = image1.getHeight();
		if (image2.getWidth() != width || image2.getHeight() != height) {
			throw new IllegalArgumentException("Error: Images dimensions mismatch");
		}

		BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int red = Color.RED.getRGB();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int pixel1 = image1.getRGB(x, y);
				Color color1 = new Color(pixel1, true);
				int r1 = color1.getRed();
				int g1 = color1.getGreen();
				int b1 = color1.getBlue();

				int pixel2 = image2.getRGB(x, y);
				Color color2 = new Color(pixel2, true);
				int r2 = color2.getRed();
				int g2 = color2.getGreen();
				int b2 = color2.getBlue();

				// compare RGB values of the pixels
				if (r1 != r2 || g1 != g2 || b1 != b2) {
					diffImage.setRGB(x, y, red);
				}

			}
		}

		return diffImage;
	}

}
