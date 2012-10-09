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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.splandroid.tr.MockeryLoggingTestCase;
import org.splandroid.tr.parsers.ITestArguments;
import org.splandroid.tr.parsers.ITestDescriptor;
import org.splandroid.tr.reporting.IReportFacade;
import org.splandroid.tr.testing.TRExecutableTestCase;
import org.splandroid.tr.testing.TRTestRunner;
import org.splandroid.tr.testing.annotations.Test;
import org.splandroid.tr.testing.annotations.TestExecuteSetter;

public class TRExecutableTestTest extends MockeryLoggingTestCase {
  private static final String ARG_EXECUTABLE = "executable";
  private static final String ARG_WORKINGDIR = "workingDirectory";
  private static final String TEST_CASE_ID = "Executable test";
  private static final String TEST_ID = "lines";

  private static final String UNIX_EXE = "/bin/hostname";
  private static final String UNIX_WD = "/";
  private static final String WINDOWS_EXE = "C:\\WINDOWS\\system32\\hostname";
  private static final String WINDOWS_WD = "C:\\";

  private static boolean isWindows = false;

  static {
    isWindows = (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0);
  }

  public class MyExecutableTestClass extends TRExecutableTestCase {
    private final InputStream inStream = System.in;

    private String executable;
    private File workingDirectory;
    private String[] outputLines;

    public MyExecutableTestClass(String testId, String description,
        List<ITestDescriptor> tests, ITestArguments setUpInfo,
        Map<String, String> environment) {
      super(testId, description, tests, setUpInfo, environment);
    }

    @TestExecuteSetter
    public void setExecutable(String exe) {
      executable = exe;
    }

    @TestExecuteSetter
    public void setWorkingDirectory(String dir) {
      workingDirectory = new File(dir);
    }

    public void execute() throws Exception {
      final OutputStream outStream = new ByteArrayOutputStream();
      final OutputStream errStream = new ByteArrayOutputStream();
      int exitCode = -1;
      exitCode = runProcess(
          executable,
          workingDirectory,
          inStream,
          outStream,
          errStream);
      assertTrue(exitCode == 0);
      outputLines = splitLines(outStream.toString());
    }

    @Test
    public void testLines() {
      assertTrue(outputLines.length == 1);
    }
  }

  private String osExecutable;
  private String osWorkingDir;
  private IReportFacade reporter;
  private TRTestRunner testRunner;

  public void setUp() {
    super.setUp();
    if (isWindows) {
      osExecutable = WINDOWS_EXE;
      osWorkingDir = WINDOWS_WD;
    } else {
      osExecutable = UNIX_EXE;
      osWorkingDir = UNIX_WD;
    }
    reporter = context.mock(IReportFacade.class, "reporter");
    testRunner = new TRTestRunner(reporter);
  }

  public void tearDown() {
    super.tearDown();
    osExecutable = null;
    osWorkingDir = null;
    reporter = null;
    testRunner = null;
  }

  /**
   * Test successful execution of a process
   */
  public void testExecutableSuccessfulExecution() {
    final String desc = "Successful execution test";
    final ITestArguments testArgs = context.mock(ITestArguments.class,
        "Lines arguments");
    final Set<String> testArgSet = new HashSet<String>();
    testArgSet.add(ARG_EXECUTABLE);
    testArgSet.add(ARG_WORKINGDIR);

    final ITestDescriptor testDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    context.checking(new Expectations() {
      {
        // Test arguments
        one(testArgs).keySet();
        will(returnValue(new HashSet<String>()));

        // Setup information
        one(setUpInfo).keySet();
        will(returnValue(testArgSet));

        one(setUpInfo).get(ARG_EXECUTABLE);
        will(returnValue(osExecutable));

        one(setUpInfo).get(ARG_WORKINGDIR);
        will(returnValue(osWorkingDir));

        // Test descriptor
        atLeast(1).of(testDesc).getId();
        will(returnValue(TEST_ID));

        one(testDesc).getArguments();
        will(returnValue(testArgs));

        // Reporter
        one(reporter).startingTestCase(TEST_CASE_ID, desc);
        one(reporter).passedTestCase(TEST_CASE_ID);
        one(reporter).startingTest(TEST_ID);
        one(reporter).passedTest(TEST_ID);
      }
    });

    final MyExecutableTestClass testCase = new MyExecutableTestClass(
        TEST_CASE_ID, desc, tests, setUpInfo, null);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == true);

    context.assertIsSatisfied();
  }

  /**
   * Test with an invalid working directory. The outcome should be that the test
   * run method should return as normal even though the execution phase has
   * failed. In the real world bad tests should not halt the execution of other
   * tests.
   */
  public void testExecutableInvalidWorkingDirectory() {
    final String desc = "Invalid working directory test";
    final Set<String> testArgSet = new HashSet<String>();
    testArgSet.add(ARG_EXECUTABLE);
    testArgSet.add(ARG_WORKINGDIR);

    final ITestDescriptor testDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final String invalidDirectory = "this" + File.separator + "directory"
        + File.separator + "will" + File.separator + "not" + File.separator
        + "exist";

    context.checking(new Expectations() {
      {
        // Setup information
        one(setUpInfo).keySet();
        will(returnValue(testArgSet));

        one(setUpInfo).get(ARG_EXECUTABLE);
        will(returnValue(osExecutable));

        one(setUpInfo).get(ARG_WORKINGDIR);
        will(returnValue(invalidDirectory));

        // Reporter
        one(reporter).startingTestCase(TEST_CASE_ID, desc);
        one(reporter).errorTestCase(with(any(String.class)),
            with(any(Throwable.class)));
      }
    });

    final MyExecutableTestClass testCase = new MyExecutableTestClass(
        TEST_CASE_ID, desc, tests, setUpInfo, null);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == false);

    context.assertIsSatisfied();
  }

  public void testExecutableInvalidExecutable() {
    final String desc = "Invalid executable test";
    final Set<String> testArgSet = new HashSet<String>();
    testArgSet.add(ARG_EXECUTABLE);
    testArgSet.add(ARG_WORKINGDIR);

    final ITestDescriptor testDesc = context.mock(ITestDescriptor.class,
        "Test one descriptor");
    final List<ITestDescriptor> tests = new ArrayList<ITestDescriptor>();
    tests.add(testDesc);

    final ITestArguments setUpInfo = context.mock(ITestArguments.class,
        "Setup info");

    final String invalidExecutable = "this" + File.separator + "exectuable"
        + File.separator + "will" + File.separator + "not" + File.separator
        + "exist";

    context.checking(new Expectations() {
      {
        // Setup information
        one(setUpInfo).keySet();
        will(returnValue(testArgSet));

        one(setUpInfo).get(ARG_EXECUTABLE);
        will(returnValue(invalidExecutable));

        one(setUpInfo).get(ARG_WORKINGDIR);
        will(returnValue(osWorkingDir));

        // Reporter
        one(reporter).startingTestCase(TEST_CASE_ID, desc);
        one(reporter).errorTestCase(with(any(String.class)),
            with(any(Throwable.class)));
      }
    });

    final MyExecutableTestClass testCase = new MyExecutableTestClass(
        "Executable test", desc, tests, setUpInfo, null);

    testRunner.setTestCase(testCase);
    final boolean passed = testRunner.run();
    assertTrue(passed == false);

    context.assertIsSatisfied();
  }
}
