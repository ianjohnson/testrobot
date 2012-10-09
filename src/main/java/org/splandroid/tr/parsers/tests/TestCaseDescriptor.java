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
package org.splandroid.tr.parsers.tests;

import java.util.List;
import java.util.Map;

import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.parsers.ITestDescriptor;

class TestCaseDescriptor implements ITestCaseDescriptor {
  private String id;
  private String description;
  private final String className;
  private List<ITestDescriptor> tests;
  private final ITestArguments setUpInfo;
  private final Map<String, String> environment;

  public TestCaseDescriptor(String id, String description, String className,
      List<ITestDescriptor> tests, ITestArguments setUpInfo,
      Map<String, String> env) {
    super();
    this.id = id;
    this.description = description;
    this.className = className;
    this.tests = tests;
    this.setUpInfo = setUpInfo;
    this.environment = env;
  }

  public String getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getClassName() {
    return className;
  }

  public List<ITestDescriptor> getTests() {
    return tests;
  }

  public ITestArguments getSetUpInfo() {
    return setUpInfo;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }
}
