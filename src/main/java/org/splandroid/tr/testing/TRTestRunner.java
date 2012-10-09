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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.reporting.IReportFacade;
import org.splandroid.tr.testing.annotations.Test;
import org.splandroid.tr.testing.annotations.TestExecuteSetter;
import org.splandroid.tr.testing.annotations.TestSetter;

class TRTestRunner {
  private static Logger logger = Logger.getLogger(TRTestRunner.class);
  private static final String setterPrefix = "set";
  private static final String testMethodPrefix = "test";

  private final IReportFacade reporter;

  private TRTestCase testCase;

  public TRTestRunner(IReportFacade reporter) {
    this.reporter = reporter;
  }

  public final void setTestCase(TRTestCase testCase) {
    this.testCase = testCase;
  }

  /**
   * Run a test case through its set up, execute and test phases. If all tests
   * in the test case pass then the test case is deemed to have passed
   * 
   * @returns Test case's passed flag
   */
  public final boolean run() {
    assert testCase != null : "Test case not set in test runner";

    final String testCaseId = testCase.getId();

    // Notify test case starting
    reporter.startingTestCase(testCaseId, testCase.getDescription());

    // Run the set up method
    logDebugStarting(TRTestPhase.SETUP);
    try {
      testCase.setUp();
    } catch (Exception ex) {
      logError(TRTestPhase.SETUP, ex.getMessage());
      // Notify of test case set up phase error
      reporter.errorTestCase(testCaseId, ex);
      return false;
    }
    logDebugFinished(TRTestPhase.SETUP);
    logger.debug(String.format("Test case [%s] set up phase: Finshed",
        testCaseId));

    boolean allTestsPassed = false;
    try {
      try {
        runExecutePhase();
      } catch (InternalTestException ex) {
        // Notify of test case execute phase error
        reporter.errorTestCase(testCaseId, ex.getCause());
        return false;
      }

      logDebugStarting(TRTestPhase.TESTING);
      allTestsPassed = runTestPhase();
      logDebugFinished(TRTestPhase.TESTING);
    } finally {
      logDebugStarting(TRTestPhase.TEAR_DOWN);
      testCase.tearDown();
      logDebugFinished(TRTestPhase.TEAR_DOWN);
    }

    String msg = String.format("Test case [%s]: ", testCaseId);
    if (allTestsPassed == false) {
      // TODO: Notify that the test case failed
      msg += "Failed";
      reporter.failedTestCase(testCaseId);
    } else {
      // TODO: Notify listeners on test case passed
      msg += "Passed";
      reporter.passedTestCase(testCaseId);
    }
    logger.debug(msg);

    return allTestsPassed;
  }

  private String getSetterMethodName(String argName) {
    final String setterMethod = setterPrefix
        + argName.substring(0, 1).toUpperCase() + argName.substring(1);
    return setterMethod;
  }

  private void runExecutePhase() throws InternalTestException {
    // Run the test execute setter methods
    logDebugStarting(TRTestPhase.PRE_EXEC_SETUP);
    try {
      invokeTestExecuteSetters();
    } catch (InternalTestException ex) {
      logError(TRTestPhase.PRE_EXEC_SETUP, ex.getCause().getMessage());
      ex.setPhase(TRTestPhase.PRE_EXEC_SETUP);
      throw ex;
    }
    logDebugFinished(TRTestPhase.PRE_EXEC_SETUP);

    // Produce results to test
    logDebugStarting(TRTestPhase.EXECUTION);
    try {
      testCase.execute();
    } catch (Exception ex) {
      logError(TRTestPhase.EXECUTION, ex.getMessage());
      final InternalTestException intEx = new InternalTestException(ex);
      intEx.setPhase(TRTestPhase.EXECUTION);
      throw intEx;
    } catch (Throwable thr) {
      logError(TRTestPhase.EXECUTION, thr.getMessage());
      final InternalTestException intEx = new InternalTestException(thr);
      intEx.setPhase(TRTestPhase.EXECUTION);
      throw intEx;
    }
    logDebugFinished(TRTestPhase.EXECUTION);
  }

  private boolean runTestPhase() {
    boolean allTestsPassed = true;

    // Run tests over the results
    for (ITestDescriptor test : testCase.getTests()) {
      final String testId = test.getId();
      logger.debug(String.format("Starting [%s] test...", testId));
      // Notify of test start
      reporter.startingTest(testId);

      try {
        runTest(test);
      } catch (InternalTestException testEx) {
        testEx.setPhase(TRTestPhase.TESTING);
        reporter.errorTest(testId, testEx);

        final String msg = String.format(
            "%s phase failed for test case [%s]: %s", TRTestPhase.TESTING,
            testCase.getId(), testEx.getMessage());
        logger.error(msg);

        allTestsPassed = false;
        continue;
      } catch (Throwable ex) {
        String infoMsg = null;
        final String message = ex.getMessage();
        if (message == null) {
          infoMsg = String.format("Test [%s] in test case [%s] failed", testId,
              testCase.getId());
        } else {
          infoMsg = String.format("Test [%s] in test case [%s] failed: %s",
              testId, testCase.getId(), message);
        }
        reporter.failedTest(testId, message);
        logger.debug(infoMsg);

        allTestsPassed = false;
        continue;
      }
      reporter.passedTest(testId);

      logger.debug(String.format("Test [%s] in test case [%s] passed", testId,
          testCase.getId()));
    }

    return allTestsPassed;
  }

  private void invokeTestExecuteSetters() throws InternalTestException {
    final ITestArguments setUpInfo = testCase.getTestSetUpArguments();
    if (setUpInfo == null) {
      return;
    }

    for (String argName : setUpInfo.keySet()) {
      final String setterMethod = getSetterMethodName(argName);
      final Object argValue = setUpInfo.get(argName);

      logger.debug(String.format("Setting argument [%s] with [%s] to [%s]",
          argName, setterMethod, argValue.toString()));

      Method method = null;
      try {
        method = testCase.getClass().getMethod(setterMethod,
            argValue.getClass());
      } catch (NoSuchMethodException ex) {
        throw new InternalTestException(String.format(
            "Failed to find setup setter method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), ex);
      }

      final TestExecuteSetter annotation = method
          .getAnnotation(TestExecuteSetter.class);
      if (annotation == null) {
        throw new InternalTestException(
            new IllegalAccessException(String.format(
                "Attempt made, in test case [%s], to access "
                    + "a non-annotated test execute setter "
                    + "method [%s]. Aborting test.", testCase.getId(),
                setterMethod)));
      }

      try {
        method.invoke(testCase, argValue);
      } catch (IllegalAccessException accessEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke setup method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), accessEx);
      } catch (IllegalArgumentException argEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke setup method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), argEx);
      } catch (InvocationTargetException invocEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke setup method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), invocEx);
      }
    }
  }

  private void runTest(ITestDescriptor test) throws Throwable {
    final String testId = test.getId();
    final Map<String, Object> args = test.getArguments();

    for (String argName : args.keySet()) {
      final String setterMethod = getSetterMethodName(argName);
      final Object argValue = args.get(argName);
      Method setMethod = null;
      try {
        setMethod = testCase.getClass().getMethod(setterMethod,
            argValue.getClass());
      } catch (NoSuchMethodException ex) {
        throw new InternalTestException(String.format(
            "Failed to find test setter method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), ex);
      }

      final TestSetter annotation = setMethod.getAnnotation(TestSetter.class);
      if (annotation == null) {
        throw new InternalTestException(new IllegalAccessException(
            String.format("Attempt made, in test [%s], to access a "
                + "non-annotated test setter method [%s]. " + "Aborting test.",
                testId, setterMethod)));
      }

      logger.debug(String.format(
          "Setting test argument [%s] with [%s] to [%s]", argName,
          setterMethod, argValue));

      try {
        setMethod.invoke(testCase, argValue);
      } catch (IllegalAccessException accessEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke test setter method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), accessEx);
      } catch (IllegalArgumentException argEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke test setter method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), argEx);
      } catch (InvocationTargetException invocEx) {
        throw new InternalTestException(String.format(
            "Failed to invoke test setter method [%s] in " + "test case [%s]",
            setterMethod, testCase.getId()), invocEx);
      }
    }

    final String testMethodName = testMethodPrefix
        + testId.substring(0, 1).toUpperCase() + testId.substring(1);
    Method testMethod = null;
    try {
      testMethod = testCase.getClass().getMethod(testMethodName);
    } catch (NoSuchMethodException ex) {
      throw new InternalTestException(String.format(
          "Failed to find test method [%s] in test case [%s]", testMethodName,
          testCase.getId()), ex);
    }

    final Test testAnnotation = testMethod.getAnnotation(Test.class);
    if (testAnnotation == null) {
      throw new InternalTestException(new IllegalAccessException(String.format(
          "Attempt made, in test [%s], to access a "
              + "non-annotated test method [%s]. " + "Aborting test.", testId,
          testMethodName)));
    }

    try {
      testMethod.invoke(testCase);
    } catch (IllegalAccessException accessEx) {
      throw new InternalTestException(String.format(
          "Failed to invoke test method [%s] in test case [%s]",
          testMethodName, testCase.getId()), accessEx);
    } catch (IllegalArgumentException argEx) {
      throw new InternalTestException(String.format(
          "Failed to invoke test method [%s] in test case [%s]",
          testMethodName, testCase.getId()), argEx);
    } catch (InvocationTargetException invocEx) {
      final Throwable target = invocEx.getTargetException();
      if (target != null && target instanceof TRTestFailed) {
        throw invocEx.getTargetException();
      }
      throw new InternalTestException(String.format(
          "Failed to invoke test method [%s] in test case [%s]",
          testMethodName, testCase.getId()), invocEx);
    }
  }

  private void logDebugStarting(TRTestPhase phase) {
    logDebug(phase, "Starting");
  }

  private void logDebugFinished(TRTestPhase phase) {
    logDebug(phase, "Finished");
  }

  private void logError(TRTestPhase phase, String extra) {
    logger.error(String.format("Test case [%s]: %s phase error\n%s",
        testCase.getId(), phase, extra));
  }

  private void logDebug(TRTestPhase phase, String extra) {
    logger.debug(String.format("Test case [%s] %s phase: %s", testCase.getId(),
        phase.toString().toLowerCase(), extra));
  }
}
