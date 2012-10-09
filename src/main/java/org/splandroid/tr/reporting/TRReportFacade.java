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

import org.splandroid.tr.events.TRTestEvent;
import org.splandroid.tr.events.TRTestEventKind;
import org.splandroid.tr.events.TRTestSubject;

class TRReportFacade implements IReportFacade {
  final private TRTestSubject reportSubject;

  public TRReportFacade(TRTestSubject subject) {
    reportSubject = subject;
  }

  //
  // Notification of component name
  //
  public void setComponent(String component) {
    reportSubject.setComponent(component);
  }

  //
  // Starting and finishing the test harness process
  //
  public void started(String msg) {
    sendEventObject(TRTestEventKind.STARTED, null, null, msg, false);
  }

  public void finished(String msg) {
    sendEventObject(TRTestEventKind.FINISHED, null, null, msg, false);
  }

  //
  // Starting and finishing component
  //
  public void startingComponent() {
    sendEventObject(TRTestEventKind.COMPONENT_STARTED, null, null, null, false);
  }

  public void passedComponent() {
    sendEventObject(TRTestEventKind.COMPONENT_FINISHED, null, null, null, false);
  }

  public void failedComponent() {
    sendEventObject(TRTestEventKind.COMPONENT_FINISHED, null, null, null, true);
  }

  //
  // Test suite set up
  //
  public void startingTestSuiteSetUp(String testSuiteId) {
    sendEventObject(TRTestEventKind.PRE_TEST_SUITE_CONFIG, null, testSuiteId,
        null, false);
  }

  public void finishedTestSuiteSetUp(String testSuiteId) {
    sendEventObject(TRTestEventKind.POST_TEST_SUITE_CONFIG, null, testSuiteId,
        null, false);
  }

  public void errorTestSuiteSetUp(String testSuiteId, Throwable throwable) {
    sendEventObject(TRTestEventKind.POST_TEST_SUITE_CONFIG, throwable,
        testSuiteId, null, true);
  }

  //
  // Starting and finishing a test suite
  //
  public void startingTestSuite(String id) {
    sendEventObject(TRTestEventKind.TEST_SUITE_STARTED, null, id, null, false);
  }

  public void passedTestSuite(String id) {
    finishedTestSuite(id, false, null);
  }

  public void failedTestSuite(String id) {
    finishedTestSuite(id, true, null);
  }

  public void errorTestSuite(String id, Throwable thr) {
    finishedTestSuite(id, true, thr);
  }

  private void finishedTestSuite(String id, boolean failed, Throwable thr) {
    sendEventObject(TRTestEventKind.TEST_SUITE_FINISHED, thr, id, null, failed);
  }

  //
  // Starting and finishing a test case
  //
  public void startingTestCase(String testCaseId, String testCaseDesc) {
    sendEventObject(TRTestEventKind.TEST_CASE_STARTED, null, testCaseId,
        testCaseDesc, false);
  }

  public void passedTestCase(String testCaseId) {
    finishedTestCase(testCaseId, false, null);
  }

  public void failedTestCase(String testCaseId) {
    finishedTestCase(testCaseId, true, null);
  }

  public void errorTestCase(String testCaseId, Throwable throwable) {
    finishedTestCase(testCaseId, true, throwable);
  }

  private void finishedTestCase(String testCaseId, boolean failed, Throwable thr) {
    sendEventObject(TRTestEventKind.TEST_CASE_FINISHED, thr, testCaseId, null,
        failed);
  }

  /*
   * // Test case execute setup public void finishedTestCaseExecuteSetUp(String
   * id, String msg) { finishedTestCaseExecuteSetUp(id, msg, null); }
   * 
   * public void finishedTestCaseExecuteSetUp(String id, String msg, Throwable
   * throwable) { sendEventObject( CWTRTestEventKind.TEST_CASE_EXEC_SETUP,
   * throwable, id, msg, false); }
   * 
   * 
   * // Test case execute phase public void finishedTestCaseExecute(String id,
   * String msg) { finishedTestCaseExecute(id, msg, null); }
   * 
   * public void finishedTestCaseExecute(String id, String msg, Throwable
   * throwable) { sendEventObject( CWTRTestEventKind.TEST_CASE_EXEC, throwable,
   * id, msg, false); }
   * 
   * 
   * // Test setup public void finishedTestSetUp(String id, String msg) {
   * finishedTestSetUp(id, msg, null); }
   * 
   * public void finishedTestSetUp(String id, String msg, Throwable throwable) {
   * sendEventObject(CWTRTestEventKind.TEST_SETUP, throwable, id, msg, false); }
   */

  // Test run
  public void startingTest(String testId) {
    sendEventObject(TRTestEventKind.TEST_STARTED, null, testId, null, false);
  }

  public void passedTest(String testId) {
    finishedTest(testId, null, false, null);
  }

  public void failedTest(String testId, String message) {
    finishedTest(testId, message, true, null);
  }

  public void errorTest(String testId, Throwable throwable) {
    finishedTest(testId, null, true, throwable);
  }

  private void finishedTest(String id, String msg, boolean failed,
      Throwable throwable) {
    sendEventObject(TRTestEventKind.TEST_FINISHED, throwable, id, msg, failed);
  }

  private void sendEventObject(TRTestEventKind kind, Throwable throwable,
      String id, String description, boolean failed) {
    final TRTestEvent event = new TRTestEvent(kind, throwable, id, description,
        failed);
    reportSubject.notifyReporters(event);
  }
}
