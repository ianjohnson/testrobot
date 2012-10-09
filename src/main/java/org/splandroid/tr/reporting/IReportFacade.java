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

public interface IReportFacade {
  /**
   * Notifies all reporters of the current component under test
   * 
   * @param component
   *          - The name of the component under test
   */
  public void setComponent(String component);

  //
  // Starting and finishing the test harness process
  //
  /**
   * The test harness process has started
   * 
   * @param msg
   *          - The reported message
   */
  public void started(String msg);

  /**
   * The test harness process has finished
   * 
   * @param msg
   *          - The reported message
   */
  public void finished(String msg);

  //
  // Starting and finishing component
  //
  public void startingComponent();

  public void passedComponent();

  public void failedComponent();

  //
  // Pre- and post-test suite setup events
  // TODO: When the test suite set up in a part of the testing package
  // remove these test-suite-set-up reporting methods. They're not needed
  // since a test suite shall error if its set up fails.
  //
  public void startingTestSuiteSetUp(String testSuiteId);

  public void finishedTestSuiteSetUp(String testSuiteId);

  public void errorTestSuiteSetUp(String testSuiteId, Throwable throwable);

  //
  // Starting and finishing a test suite
  //
  public void startingTestSuite(String id);

  public void passedTestSuite(String id);

  public void failedTestSuite(String id);

  public void errorTestSuite(String id, Throwable throwable);

  // Starting and finishing a test case
  public void startingTestCase(String testCaseid, String testDesc);

  public void passedTestCase(String testCaseId);

  public void failedTestCase(String testCaseId);

  public void errorTestCase(String testCaseId, Throwable throwable);

  /*
   * // Test case execute setup public void finishedTestCaseExecuteSetUp(String
   * id, String msg); public void finishedTestCaseExecuteSetUp(String id, String
   * msg, Throwable throwable);
   * 
   * // Test case execute phase public void finishedTestCaseExecute(String id,
   * String msg); public void finishedTestCaseExecute(String id, String msg,
   * Throwable throwable);
   * 
   * // Test setup public void finishedTestSetUp(String id, String msg); public
   * void finishedTestSetUp(String id, String msg, Throwable throwable);
   */

  // Test run
  public void startingTest(String testId);

  public void passedTest(String testId);

  public void failedTest(String testId, String message);

  public void errorTest(String testId, Throwable throwable);
}
