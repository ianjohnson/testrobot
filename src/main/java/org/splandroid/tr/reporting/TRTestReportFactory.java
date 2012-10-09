/*
 * Copyright Ian Johnson 2012
 *
 * This file is part of TestRobot.
 *
 * TestRobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TestRobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TestRobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.splandroid.tr.reporting;

import org.splandroid.tr.events.TRTestSubject;
import org.splandroid.tr.events.IEventObserver;

public class TRTestReportFactory {
  public static IReportFacade getReporter(IEventObserver... reporters) {
    final TRTestSubject subject = new TRTestSubject();
    for (IEventObserver reporter : reporters) {
      subject.attachReporter(reporter);
    }

    final TRReportFacade facade = new TRReportFacade(subject);

    return facade;
  }
}
