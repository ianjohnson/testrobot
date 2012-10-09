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
package org.splandroid.tr.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TRTestSubject extends Observable {
  private final Map<IEventObserver, Observer> reportingObservers;

  public TRTestSubject() {
    reportingObservers = new HashMap<IEventObserver, Observer>();
  }

  public synchronized void attachReporter(final IEventObserver reporter) {
    final Observer observer = new Observer() {
      public void update(Observable subject, Object _event) {
        if (_event instanceof TRTestEvent) {
          final TRTestEvent event = (TRTestEvent )_event;
          reporter.update(event);
        }
      }
    };

    addObserver(observer);
    reportingObservers.put(reporter, observer);
    assert countObservers() == reportingObservers.size();
  }

  public synchronized void detachReporter(IEventObserver reporter) {
    if (reportingObservers.containsKey(reporter)) {
      final Observer observer = reportingObservers.get(reporter);
      deleteObserver(observer);
      reportingObservers.remove(reporter);
    }
    assert countObservers() == reportingObservers.size();
  }

  public synchronized int numberOfReporters() {
    final int noObservers = countObservers();
    assert noObservers == reportingObservers.size();
    return noObservers;
  }

  public synchronized void notifyReporters(TRTestEvent event) {
    setChanged();
    notifyObservers(event);
  }

  public synchronized void setComponent(String component) {
    for (IEventObserver reporter : reportingObservers.keySet()) {
      reporter.setComponent(component);
    }
  }
}
