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

package net.markwalder.tools.worktime.tracker.mouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import org.junit.Test;

public class MousePositionTest {

	MousePosition mousePosition = new MousePosition(100, 200);

	@Test
	public void constructor_withPoint() {
		MousePosition mousePosition2 = new MousePosition(new Point(100, 200));
		assertEquals(mousePosition2, mousePosition);
	}

	@Test
	public void getX() {
		assertEquals(100, mousePosition.getX());
	}

	@Test
	public void getY() {
		assertEquals(200, mousePosition.getY());
	}

	@Test
	@SuppressWarnings("SimplifiableAssertion")
	public void testEquals() {
		assertTrue(mousePosition.equals(new MousePosition(100, 200)));
		assertFalse(mousePosition.equals(new MousePosition(110, 200)));
		assertFalse(mousePosition.equals(new MousePosition(100, 220)));
		assertFalse(mousePosition.equals(new MousePosition(200, 100)));
	}

	@Test
	public void testHashCode() {
		assertEquals(4261, mousePosition.hashCode());
	}

}