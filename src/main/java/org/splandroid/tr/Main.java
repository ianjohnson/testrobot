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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.splandroid.tr.commons.Conditionals;
import org.splandroid.tr.events.IEventObserver;
import org.splandroid.tr.parsers.IParser;
import org.splandroid.tr.parsers.ISymbolMap;
import org.splandroid.tr.parsers.ITestCaseDescriptor;
import org.splandroid.tr.parsers.profile.ProfileParser;
import org.splandroid.tr.parsers.tests.TestsParser;
import org.splandroid.tr.reporting.IReportFacade;
import org.splandroid.tr.reporting.TRTestReportFactory;
import org.splandroid.tr.testing.ITRTestInputProvider;
import org.splandroid.tr.testing.ITRTestOutputManager;
import org.splandroid.tr.testing.TRTestInputProvider;
import org.splandroid.tr.testing.TRTestOutputManager;
import org.splandroid.tr.testing.TRTestSuite;

public class Main {
  interface Comparator {
    public int compare(String currentTestCaseId, Collection<String> testCaseIds);
  }

  private static final String applicationName = "tr";
  private static final String usageMessage = " [options] <test suites>";

  private static final String propertiesFile = applicationName + ".prop";
  private static final String logFile = applicationName + ".log";

  private static final String loggingFilePropertyName = "log4j.appender.A1.File";

  // Command line option default values
  private static final String defaultTestInfoDirName;
  private static final String defaultTestResultsDirName;
  private static final String defaultPropertiesFileName;
  private static final String defaultLogFileName;

  private static Logger logger;

  static {
    defaultTestInfoDirName =
        FilenameUtils.concat(".", ITRTestInputProvider.testInfDirName);
    defaultTestResultsDirName =
        FilenameUtils.concat(".", ITRTestOutputManager.testResDirName);
    defaultPropertiesFileName =
        FilenameUtils.concat(defaultTestInfoDirName, propertiesFile);
    defaultLogFileName = FilenameUtils.concat(".", logFile);
  }

  /**
   * Remove test cases from the list of test-cases. This allows only certain
   * test cases to be run in a test suite. The comparator defines which test
   * case is to be removed.
   * 
   * @param testCases
   * @param testCaseIds
   * @param comparator
   */
  private static void removeTestCaseDescriptors(
      List<ITestCaseDescriptor> testCases, Collection<String> testCaseIds,
      Comparator comparator) {
    final Iterator<ITestCaseDescriptor> iterator = testCases.iterator();

    while (iterator.hasNext()) {
      final ITestCaseDescriptor testCase = iterator.next();
      final String currentTestCaseId = testCase.getId();
      if (comparator.compare(currentTestCaseId, testCaseIds) == 0) {
        logger.info(String.format("Removing test case with ID [%s]",
            testCase.getId()));
        iterator.remove();
      }
    }
  }

  /**
   * Setup the configuration for a test suite. This method reads the profile.xml
   * for the component to define the tags/attributes in the component's
   * tests.xml file. The tests.xml file then drives the running of the tests.
   * 
   * @param component
   *          - Name of the component in RCS
   * @throws TRException
   */
  private static List<ITestCaseDescriptor> setUpTestSuite(TestSuiteInfo suite,
      ITRTestInputProvider testInputProv, ITRTestOutputManager resultMgr)
      throws Exception {
    final String component = suite.getComponent();
    final InputStream profileStream = testInputProv.getProfileFileStream();

    logger.debug(String.format("Parsing profile for [%s]...", component));
    final ISymbolMap symbols = new SymbolMap();
    final IParser profileParser = new ProfileParser();
    profileParser.setSymbolTable(symbols).parse(profileStream);

    logger.debug(String.format("Parsing tests for [%s]...", component));
    final InputStream testsFile = testInputProv.getTestsFileStream();
    final List<ITestCaseDescriptor> testCases = new ArrayList<ITestCaseDescriptor>();
    final IParser testsParser = new TestsParser(testCases);
    testsParser.setSymbolTable(symbols).parse(testsFile);

    return testCases;
  }

  /**
   * Removes test cases from the test suite. The test cases to be removed are
   * derived from the includes and excludes collections contained in the test
   * suite info object.
   * 
   * @param suite
   *          - The test suite
   * @param testCases
   *          - The list of test cases to consider
   */
  private static void removeTestCases(TestSuiteInfo suite,
      List<ITestCaseDescriptor> testCases) {
    final Collection<String> includes = suite.getTestCaseIncludes();
    final Collection<String> excludes = suite.getTestCaseExcludes();
    final int includesSize = includes.size();
    final int excludesSize = excludes.size();

    assert Conditionals.implies(includesSize > 0, excludesSize == 0);
    assert Conditionals.implies(excludesSize > 0, includesSize == 0);

    /*
     * Handle the include test cases by removing test cases by removing test
     * case that are NOT in the includes set.
     */
    if (includesSize > 0) {
      final Comparator comp = new Comparator() {
        public int compare(final String currentTestCaseId,
            final Collection<String> testCaseIds) {
          int comparison = 0;
          for (String testCaseId : testCaseIds) {
            comparison += currentTestCaseId.equals(testCaseId) ? 1 : 0;
          }
          return comparison;
        }
      };
      removeTestCaseDescriptors(testCases, includes, comp);
    }

    /*
     * Handle the excludes test cases by removing the test cases specified in
     * the exclude set.
     */
    if (excludesSize > 0) {
      final Comparator comp = new Comparator() {
        public int compare(final String currentTestCaseId,
            final Collection<String> testCaseIds) {
          for (String testCaseId : testCaseIds) {
            if (currentTestCaseId.equals(testCaseId)) {
              return 0;
            }
          }
          return 1;
        }
      };
      removeTestCaseDescriptors(testCases, excludes, comp);
    }
  }

  /**
   * Run a suite of tests for a CHAINworks component.
   * 
   * @param component
   *          - Name of the component in RCS
   * @throws TRException
   */
  private static boolean runTestSuite(String suiteId,
      List<ITestCaseDescriptor> testCases, ITRTestInputProvider testInputProv,
      ITRTestOutputManager resultMgr, IReportFacade reporter) {
    final TRTestSuite testSuite = new TRTestSuite(suiteId, testCases,
        testInputProv, resultMgr, reporter);
    return testSuite.run();
  }

  /**
   * Set up and run a test suite
   * 
   * @param suite
   * @param testInputProv
   * @param resultMgr
   * @param reporter
   * @throws Exception
   */
  private static boolean setUpAndRunTestSuite(TestSuiteInfo suite,
      ITRTestInputProvider testInputProv, ITRTestOutputManager resultMgr,
      IReportFacade reporter) {
    final String component = suite.getComponent();
    List<ITestCaseDescriptor> testCases = null;

    // Setup the test suite
    // TODO: Move the suite setup to org.splandroid.tr.testing pkg?
    reporter.startingTestSuiteSetUp(component);
    try {
      testCases = setUpTestSuite(suite, testInputProv, resultMgr);
      removeTestCases(suite, testCases);
    } catch (Exception ex) {
      logger.fatal(String.format(
          "Processing of test suite set-up for component [%s] failed: %s",
          component, ex.getMessage()), ex);
      reporter.errorTestSuiteSetUp(component, ex);
      return false;
    }
    reporter.finishedTestSuiteSetUp(component);

    // Run the test suite
    final boolean passed = runTestSuite(component, testCases, testInputProv,
        resultMgr, reporter);

    logger.info(String.format("Finished test suite for [%s]", component));

    return passed;
  }

  /**
   * Load the harness' properties file
   * 
   * @param propsFile
   *          - Property file name
   * @param logFile
   *          - The file name of the harness' logfile
   * @return A java.util.Properties object representing the property file
   * @throws Exception
   */
  private static Properties loadProperties(String propsFile, String logFile)
      throws Exception {
    final InputStream propsStream = new FileInputStream(propsFile);
    final Properties props = new Properties();

    props.load(propsStream);
    props.setProperty(loggingFilePropertyName, logFile);

    return props;
  }

  public static void main(String[] args) {
    // Create the command line processor
    final CommandLineProcessor cliProcessor =
        new CommandLineProcessor(
            applicationName.toUpperCase() + usageMessage,
            defaultTestInfoDirName,
            defaultTestResultsDirName,
            defaultPropertiesFileName,
            defaultLogFileName);

    // Process command line
    try {
      final boolean shouldExit = cliProcessor.process(args);
      if (shouldExit == true) {
        System.exit(0);
      }
    } catch (TRException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    // Time of test
    final Date timeNow = new Date();
    
    // Build test input and result providers/managers.
    final String testInfoDir = cliProcessor.getTestInfoDirectory();
    final ITRTestInputProvider testInputProv = new TRTestInputProvider(
        testInfoDir);
    final String testResultsDir = cliProcessor.getTestResultsDirectory();
    final ITRTestOutputManager resultMgr = new TRTestOutputManager(
        testResultsDir, timeNow);
    final File suiteResultDir = resultMgr.createSuiteResultsDirectory();

    // Load properties
    final String propsFile = cliProcessor.getPropertiesFile();
    final String logFile =
        new File(suiteResultDir, cliProcessor.getLogFile()).getAbsolutePath();
    try {
      final Properties properties = loadProperties(propsFile, logFile);
      PropertyConfigurator.configure(properties);
    } catch (Exception ex) {
      System.out.printf("Failed to load properties: %s\n", ex.getMessage());
      System.exit(1);
    }

    // Sort out log level
    Level logLevel = null;
    switch (cliProcessor.getLogLevel()) {
    case DEBUG:
      logLevel = Level.DEBUG;
      break;

    case INFO:
      logLevel = Level.INFO;
      break;

    case WARN:
      logLevel = Level.WARN;
      break;

    case ERROR:
      logLevel = Level.ERROR;
      break;

    case FATAL:
      logLevel = Level.FATAL;
      break;

    default:
      assert false : "Bad log level";
    }
    Logger.getRootLogger().setLevel(logLevel);
    logger = Logger.getLogger(Main.class);

    
    // Create reporters
    final IEventObserver logFileReporter = new org.splandroid.tr.reporters.logfile.Reporter();
    // final IReporter htmlFileReporter =
    // new org.splandroid.tr.reporting.html.Reporter(resultMgr);
    final IReportFacade reporter = TRTestReportFactory
        .getReporter(logFileReporter);

    reporter.started("Test harness");

    // For each test suite do...
    final Collection<TestSuiteInfo> suites = cliProcessor.getTestSuites();

    for (TestSuiteInfo suite : suites) {
      // Notify objects of the current component under test
      final String componentUnderTest = suite.getComponent();
      testInputProv.setComponent(componentUnderTest);
      resultMgr.setComponent(componentUnderTest);
      reporter.setComponent(componentUnderTest);

      reporter.startingComponent();
      final boolean passed = setUpAndRunTestSuite(suite, testInputProv,
          resultMgr, reporter);
      if (passed == true) {
        reporter.passedComponent();
      } else {
        reporter.failedComponent();
      }
    }

    reporter.finished("Test harness");
  }
}
