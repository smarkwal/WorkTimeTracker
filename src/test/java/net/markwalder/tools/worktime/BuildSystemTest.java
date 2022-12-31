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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;
import org.junit.Test;

public class BuildSystemTest {

	@Test
	public void defaultTimeZone() {
		assertEquals(TimeZone.getTimeZone("Europe/Zurich"), TimeZone.getDefault());
	}

	@Test
	public void defaultCharset() {
		assertEquals("UTF-8", System.getProperty("file.encoding"));
		assertEquals(StandardCharsets.UTF_8, Charset.defaultCharset());
	}

	@Test
	public void headlessMode() {
		assertEquals("true", System.getProperty("java.awt.headless"));
		assertTrue(GraphicsEnvironment.isHeadless());
	}

}
