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

import java.util.ArrayList;
import java.util.List;

import org.splandroid.tr.MockeryTestCase;
import org.splandroid.tr.events.TRTestEvent;
import org.splandroid.tr.events.TRTestEventKind;
import org.splandroid.tr.events.TRTestSubject;
import org.splandroid.tr.events.IEventObserver;

class TestReporter implements IEventObserver {
  private TRTestEvent current;
  private String component;

  public void setComponent(String component) {
    this.component = component;
  }

  public void update(TRTestEvent event) {
    current = event;
  }

  public boolean isEventEqual(String expectedComponent,
      TRTestEvent expectedEvent) {
    return expectedEvent.equals(current) && expectedComponent.equals(component);
  }
}

public class TRTestSubjectTest extends MockeryTestCase {
  private TRTestSubject subject;

  public TRTestSubjectTest() {
  }

  public TRTestSubjectTest(String name) {
    super(name);
  }

  public void setUp() {
    super.setUp();
    subject = new TRTestSubject();
  }

  public void tearDown() {
    super.tearDown();
    subject = null;
  }

  public void testReporterAttachment() {
    final int noReporters = 31;

    attachMockReporters(noReporters, null);
    assertTrue(noReporters == subject.numberOfReporters());
  }

  public void testReporterDetachment() {
    attachThenDetachReporters(31, 15);
  }

  public void testReporterAllDetached() {
    attachThenDetachReporters(31, 31);
  }

  public void testReportDetachMoreThenAttached() {
    try {
      attachThenDetachReporters(17, 31);
    } catch (Throwable thr) {
      return;
    }
    assertTrue(false);
  }

  public void testReportNotification() {
    final String component = "Feckers";
    final int noReporters = 7;
    final List<TestReporter> reporters = new ArrayList<TestReporter>();

    // Attach reporters
    for (int cnt = 0; cnt < noReporters; cnt++) {
      final TestReporter rep = new TestReporter();
      subject.attachReporter(rep);
      reporters.add(rep);
    }

    subject.setComponent(component);

    // For-each event kind raise a reporting event
    for (TRTestEventKind evtKind : TRTestEventKind.values()) {
      final TRTestEvent event = new TRTestEvent(evtKind, null, "A test id",
          "A test description", false);
      // Notify reporters of this event
      subject.notifyReporters(event);
      // Check that the reporters have been given their event data
      for (TestReporter testReporter : reporters) {
        assertTrue(testReporter.isEventEqual(component, event));
      }
    }
  }

  private void attachMockReporters(int number, List<IEventObserver> reporters) {
    for (int idx = 0; idx < number; idx++) {
      final IEventObserver reporter = context.mock(IEventObserver.class,
          String.format("Reporter-%d", idx));
      subject.attachReporter(reporter);
      if (reporters != null) {
        reporters.add(reporter);
      }
    }
  }

  private void attachThenDetachReporters(int noInitial, int noRemove) {
    final List<IEventObserver> reporters = new ArrayList<IEventObserver>();
    final int noFinal = noInitial - noRemove;
    assertTrue(noFinal >= 0);

    attachMockReporters(noInitial, reporters);
    for (int idx = 0; idx < noRemove; idx++) {
      subject.detachReporter(reporters.get(idx));
    }
    assertTrue(subject.numberOfReporters() == noFinal);
  }
}
