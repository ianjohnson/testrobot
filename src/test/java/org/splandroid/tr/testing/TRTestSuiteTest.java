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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.splandroid.tr.MockeryLoggingTestCase;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.reporting.IReportFacade;

public class TRTestSuiteTest extends MockeryLoggingTestCase {
  private static final String TEST_CLASS = "org.splandroid.tr.testing.testclasses.TRTestSuiteTestClass";

  public void testSuite() {
    final String suiteId = "testSuiteId";
    final String testCaseOneId = "testCaseOneId";
    final String testCaseOneDesc = "Test case one description";
    final String testCaseTwoDesc = "Test case two description";
    final String testCaseTwoId = "testCaseTwoId";
    final ITestCaseDescriptor testCaseOne = context.mock(
        ITestCaseDescriptor.class, "TestCaseDescriptorOne");
    final ITestCaseDescriptor testCaseTwo = context.mock(
        ITestCaseDescriptor.class, "TestCaseDescriptorTwo");
    final List<ITestCaseDescriptor> testCases = new ArrayList<ITestCaseDescriptor>();
    testCases.add(testCaseOne);
    testCases.add(testCaseTwo);

    final ITestArguments testArgs = context.mock(ITestArguments.class,
        "TestArguments");

    final String testOneId = "one";
    final ITestDescriptor testOneDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final String testTwoId = "two";
    final ITestDescriptor testTwoDesc = context.mock(ITestDescriptor.class,
        "Test two descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testOneDesc);
    tests.add(testTwoDesc);

    final ITRTestInputProvider inputProv = context.mock(
        ITRTestInputProvider.class, "testInputProv");

    final ITRTestOutputManager outputMgr = context.mock(
        ITRTestOutputManager.class, "testOutputMgr");

    final IReportFacade reporter = context
        .mock(IReportFacade.class, "reporter");

    context.checking(new Expectations() {
      {
        // Test arguments
        exactly(6).of(testArgs).keySet();
        will(returnValue(new HashSet<String>()));

        // Test one
        atLeast(1).of(testOneDesc).getId();
        will(returnValue(testOneId));

        exactly(2).of(testOneDesc).getArguments();
        will(returnValue(testArgs));

        // Test two
        atLeast(1).of(testTwoDesc).getId();
        will(returnValue(testTwoId));

        exactly(2).of(testTwoDesc).getArguments();
        will(returnValue(testArgs));

        // Test case one
        one(testCaseOne).getClassName();
        will(returnValue(TEST_CLASS));

        atLeast(1).of(testCaseOne).getId();
        will(returnValue(testCaseOneId));

        one(testCaseOne).getDescription();
        will(returnValue(testCaseOneDesc));

        one(testCaseOne).getTests();
        will(returnValue(tests));

        one(testCaseOne).getSetUpInfo();
        will(returnValue(testArgs));

        one(testCaseOne).getEnvironment();
        will(returnValue(null));

        // Test case two
        one(testCaseTwo).getClassName();
        will(returnValue(TEST_CLASS));

        atLeast(1).of(testCaseTwo).getId();
        will(returnValue(testCaseTwoId));

        one(testCaseTwo).getDescription();
        will(returnValue(testCaseTwoDesc));

        one(testCaseTwo).getTests();
        will(returnValue(tests));

        one(testCaseTwo).getSetUpInfo();
        will(returnValue(testArgs));

        one(testCaseTwo).getEnvironment();
        will(returnValue(null));

        // Test input provider
        exactly(2).of(inputProv).getComponentInputDirectory();
        will(returnValue(new File("some-directory")));

        // Test output manager
        one(outputMgr).createComponentTestOutputDirectory(testCaseOneId);
        will(returnValue(new File(testCaseOneId)));

        one(outputMgr).createComponentTestOutputDirectory(testCaseTwoId);
        will(returnValue(new File(testCaseTwoId)));

        // Reporter facade
        one(reporter).startingTestSuite(suiteId);
        one(reporter).passedTestSuite(suiteId);
        one(reporter).startingTestCase(testCaseOneId, testCaseOneDesc);
        one(reporter).startingTestCase(testCaseTwoId, testCaseTwoDesc);
        one(reporter).passedTestCase(testCaseOneId);
        one(reporter).passedTestCase(testCaseTwoId);
        exactly(2).of(reporter).startingTest(testOneId);
        exactly(2).of(reporter).passedTest(testOneId);
        exactly(2).of(reporter).startingTest(testTwoId);
        exactly(2).of(reporter).passedTest(testTwoId);
      }
    });

    final TRTestSuite suite = new TRTestSuite(suiteId, testCases, inputProv,
        outputMgr, reporter);
    final boolean suitePassed = suite.run();
    assertTrue(suitePassed == true);

    context.assertIsSatisfied();
  }
}
