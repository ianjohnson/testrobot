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
package org.splandroid.tr.extensions.simpledemo;

import java.util.List;
import java.util.Map;

import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.testing.TRTestCase;
import org.splandroid.tr.testing.annotations.Test;

public class DemoFailed extends TRTestCase {

  public DemoFailed(String testId, String description,
      List<ITestDescriptor> tests, ITestArguments setUpInfo,
      Map<String, String> environment) {
    super(testId, description, tests, setUpInfo, environment);
  }

  public void execute() throws Exception {
  }

  @Test
  public void testAssertFailure() {
    assertTrue("Expected assert test failure", false);
  }

  @Test
  public void testFailed() {
    fail("Expected test failure");
  }
}
