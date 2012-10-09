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
package org.splandroid.tr;

import java.util.Collection;

/**
 * Describes a test suite as specified on the command line. This structure holds
 * the name of the component, and the test cases that should be included in and
 * excluded from the test suite run.
 */
class TestSuiteInfo {
  private final String componentName;
  private final Collection<String> includesTestCases;
  private final Collection<String> excludesTestCases;

  public TestSuiteInfo(String component, Collection<String> includes,
      Collection<String> excludes) {
    componentName = component;
    includesTestCases = includes;
    excludesTestCases = excludes;
  }

  public String getComponent() {
    return componentName;
  }

  public Collection<String> getTestCaseIncludes() {
    return includesTestCases;
  }

  public Collection<String> getTestCaseExcludes() {
    return excludesTestCases;
  }
}
