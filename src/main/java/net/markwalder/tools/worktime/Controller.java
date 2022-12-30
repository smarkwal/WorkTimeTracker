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

import java.util.Date;
import net.markwalder.tools.worktime.db.WorkDay;
import net.markwalder.tools.worktime.db.WorkYear;

public interface Controller {

	WorkDay getDisplayWorkDay();

	WorkYear getDisplayWorkYear();

	void start();

	void stop();

	void workDayMousePressed(int slot, boolean shift);

	void workDayMouseDragged(int slot);

	void workDayMouseReleased();

	void workYearMouseClicked(int slot, int mode);

	Date getDisplayDate();

	void setDisplayDate(Date date);

	void incrementDisplayDate();

	void decrementDisplayDate();

}
