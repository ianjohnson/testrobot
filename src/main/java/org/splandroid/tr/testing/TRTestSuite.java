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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.reporting.IReportFacade;

public class TRTestSuite {
  private final static Logger logger = Logger.getLogger(TRTestSuite.class);

  private final String id;
  private final List<ITestCaseDescriptor> testCases;
  private final ITRTestInputProvider inputProvider;
  private final ITRTestOutputManager outputMgr;
  private final IReportFacade reporter;

  public TRTestSuite(String suiteId, List<ITestCaseDescriptor> testCasesList,
      ITRTestInputProvider inputProv, ITRTestOutputManager suiteOutputMgr,
      IReportFacade testReporter) {
    id = suiteId;
    testCases = testCasesList;
    inputProvider = inputProv;
    outputMgr = suiteOutputMgr;
    reporter = testReporter;
  }

  public boolean run() {
    boolean suitePassed = true;

    reporter.startingTestSuite(id);
    try {
      suitePassed = runSuite();
      if (suitePassed == true) {
        reporter.passedTestSuite(id);
      } else {
        reporter.failedTestSuite(id);
      }
    } catch (InternalTestException ex) {
      reporter.errorTestSuite(id, ex);
      suitePassed = false;
    }

    return suitePassed;
  }

  /**
   * Run all the test cases in the suite. If all test cases pass then the suite
   * is deemed to have passed.
   */
  private boolean runSuite() throws InternalTestException {
    final TRTestRunner testRunner = new TRTestRunner(reporter);
    boolean allTestCasesPassed = true;

    for (ITestCaseDescriptor testCaseDesc : testCases) {
      final String klassName = testCaseDesc.getClassName();

      logger.debug(String.format("[%s]: Loading class [%s]...", id, klassName));
      Class<? extends TRTestCase> klass;
      try {
        klass = Class.forName(klassName).asSubclass(TRTestCase.class);
      } catch (ClassNotFoundException notFoundEx) {
        throw new InternalTestException(
            String.format("Test class [%s] not found: %s", klassName,
                notFoundEx.getMessage()));
      } catch (ClassCastException castEx) {
        throw new InternalTestException(String.format(
            "Test class [%s] does not extend %s: %s", klassName,
            TRTestCase.class.getName(), castEx.getMessage()));
      }

      // Build the test case object
      logger.debug(String.format("[%s]: Retrieving constructor for class [%s]",
          id, klassName));
      Constructor<?> klassConstructor;
      try {
        klassConstructor = klass.getConstructor(String.class, String.class,
            List.class, ITestArguments.class, Map.class);
      } catch (NoSuchMethodException noSuchMethodEx) {
        throw new InternalTestException(String.format(
            "Expected constructor for class [%s] does " + "not exist",
            klassName), noSuchMethodEx);
      } catch (SecurityException secEx) {
        throw new InternalTestException(String.format(
            "Constructor for class [%s] does " + "not allow execution",
            klassName), secEx);
      }
      logger.debug(String.format("[%s]: Instantiating class [%s]", id,
          klassName));
      Object testCaseObj;
      try {
        testCaseObj = klassConstructor.newInstance(testCaseDesc.getId(),
            testCaseDesc.getDescription(), testCaseDesc.getTests(),
            testCaseDesc.getSetUpInfo(), testCaseDesc.getEnvironment());
      } catch (Exception ex) {
        throw new InternalTestException(String.format(
            "Could not construct [%s] object ", klassName), ex);
      }

      // Set provider's data on test case object
      final TRTestCase testCase = (TRTestCase )testCaseObj;
      testCase.setInputDirectory(inputProvider.getComponentInputDirectory());
      testCase.setOutputManager(outputMgr);

      // The test runner shall run this test case
      testRunner.setTestCase(testCase);
      final boolean passed = testRunner.run();
      if (passed == false) {
        allTestCasesPassed = false;
      }
    }

    return allTestCasesPassed;
  }
}
