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
package org.splandroid.tr.testing;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;

public abstract class TRTestCase extends TRAssert {
  private final String id;
  private final String description;
  private final List<ITestDescriptor> tests;
  private final ITestArguments setUpInfo;
  private final Map<String, String> environment;

  private File inputRootDir;
  private ITRTestOutputManager outputMgr;
  private File outputDir;

  public TRTestCase(String testId, String description,
      List<ITestDescriptor> tests, ITestArguments setUpInfo,
      Map<String, String> environment) {
    assert testId != null;
    assert description != null;
    assert tests != null;
    this.id = testId;
    this.description = description;
    this.tests = tests;
    this.setUpInfo = setUpInfo;
    this.environment = environment;
  }

  public final String getId() {
    return id;
  }

  public final String getDescription() {
    return description;
  }

  public final List<ITestDescriptor> getTests() {
    return tests;
  }

  public final ITestArguments getTestSetUpArguments() {
    return setUpInfo;
  }

  public final Map<String, String> getEnvironment() {
    return environment;
  }

  public final void setInputDirectory(File inputDir) {
    inputRootDir = inputDir;
  }

  public final File getInputDirectory() {
    return inputRootDir;
  }

  public final void setOutputManager(ITRTestOutputManager testOutMgr) {
    outputMgr = testOutMgr;
    if (outputMgr != null) {
      outputDir = outputMgr.createComponentTestOutputDirectory(id);
    }
  }

  public final ITRTestOutputManager getOutputManager() {
    return outputMgr;
  }

  public final File getOutputDirectory() {
    return outputDir;
  }

  /**
   * Optional set up phase that can be used to allocate static resources needed
   * by the test. The state of the base class shall be set and correct before
   * invoking this method. By default does nothing.
   * 
   * @see org.splandroid.tr.testing.TRTestCase#tearDown()
   * @throws Exception
   */
  public void setUp() throws Exception {
  }

  /**
   * Optional tear down phase that can be used to release any resources
   * allocated in the set up phase. By default does nothing.
   * 
   * @see org.splandroid.tr.testing.TRTestCase#setUp
   */
  public void tearDown() {
  }

  public abstract void execute() throws Exception;
}
