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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.splandroid.tr.testing.helpers.argument.parsers.IArgumentParser;
import org.splandroid.tr.testing.helpers.argument.parsers.ListParser;

class CommandLineOption extends Option {
  private Object defaultValue;

  public CommandLineOption(String opt, boolean hasArg, String description,
      String defaultValue) throws IllegalArgumentException {
    super(opt, hasArg, createFullDescription(description, defaultValue));
    this.defaultValue = defaultValue;
  }

  public CommandLineOption(String opt, String longOpt, boolean hasArg,
      String description, String defaultValue) throws IllegalArgumentException {
    super(opt, longOpt, hasArg,
        createFullDescription(description, defaultValue));
    this.defaultValue = defaultValue;
  }

  public CommandLineOption(String opt, String longOpt, boolean hasArg,
      String description) {
    super(opt, longOpt, hasArg, description);
    this.defaultValue = null;
  }

  public CommandLineOption(String opt, String longOpt, String description) {
    super(opt, longOpt, false, description);
  }

  private static String createFullDescription(String description,
      Object defaultValue) {
    final String desc = description
        + String.format(CommandLineOptions.DEFAULT_FMT, defaultValue);
    return desc;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }
}

class CommandLineProcessor {
  private static final int consoleWidth = 80;

  private final List<CommandLineOption> supportedOptions;
  private final Options options;
  private final String applicationName;

  private CommandLine commandLine = null;

  /**
   * Build a command line processor
   * 
   * @param appName
   *          - Name of the applicaton
   * @param defaultTestInfDirName
   *          - A default TEST-INF directory
   * @param defaultResultDirName
   *          - A default TEST-RES directory
   * @param defaultPropsFileName
   *          - A default properties file
   * @param defaultLogFileName
   *          - A default logging file
   */
  public CommandLineProcessor(
      final String appName,
      final String defaultTestInfDirName,
      final String defaultResultDirName,
      final String defaultPropsFileName,
      final String defaultLogFileName) {
    supportedOptions = new ArrayList<CommandLineOption>();

    // Help
    supportedOptions.add(new CommandLineOption(CommandLineOptions.OPTION_HELP,
        CommandLineOptions.OPTION_LONG_HELP,
        CommandLineOptions.OPTION_HELP_HELP));

    // Test info directory
    supportedOptions.add(new CommandLineOption(
        CommandLineOptions.OPTION_TEST_INF_DIR,
        CommandLineOptions.OPTION_LONG_TEST_INF_DIR, true,
        CommandLineOptions.OPTION_TEST_INF_DIR_HELP,
        defaultTestInfDirName));

    // Results directory
    supportedOptions.add(new CommandLineOption(
        CommandLineOptions.OPTION_RESULT_DIR,
        CommandLineOptions.OPTION_LONG_RESULT_DIR, true,
        CommandLineOptions.OPTION_RESULT_DIR_HELP,
        defaultResultDirName));

    // Props file
    supportedOptions.add(new CommandLineOption(
        CommandLineOptions.OPTION_PROPS_FILE,
        CommandLineOptions.OPTION_LONG_PROPS_FILE, true,
        CommandLineOptions.OPTION_PROPS_FILE_HELP,
        defaultPropsFileName));

    // Log level
    supportedOptions.add(new CommandLineOption(
        CommandLineOptions.OPTION_LOG_LEVEL,
        CommandLineOptions.OPTION_LONG_LOG_LEVEL, true,
        CommandLineOptions.OPTION_LOG_LEVEL_HELP,
        CommandLineOptions.LogLevel.INFO.getLogLevel()));

    // Log file
    supportedOptions.add(new CommandLineOption(
        CommandLineOptions.OPTION_LOG_FILE,
        CommandLineOptions.OPTION_LONG_LOG_FILE, true,
        CommandLineOptions.OPTION_LOG_FILE_HELP,
        defaultLogFileName));

    options = new Options();
    for (CommandLineOption option : supportedOptions) {
      options.addOption(option);
    }
    applicationName = appName;
  }

  public boolean process(String[] args) throws TRException {
    final BasicParser parser = new BasicParser();

    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException ex) {
      throw new TRException(ex);
    }

    if (args.length == 0
        || commandLine.hasOption(CommandLineOptions.OPTION_HELP)) {
      final HelpFormatter helpFormatter = new HelpFormatter();
      helpFormatter.printHelp(consoleWidth, applicationName, "\n"
          + CommandLineOptions.HELP_HEADER, options, "\n"
          + CommandLineOptions.HELP_FOOTER);
      return true;
    }

    return false;
  }

  public String getPropertiesFile() {
    final String propertiesFile = getStringOption(CommandLineOptions.OPTION_PROPS_FILE);
    return propertiesFile;
  }

  public String getTestResultsDirectory() {
    final String testResultsDir = getStringOption(CommandLineOptions.OPTION_RESULT_DIR);
    return testResultsDir;
  }

  public String getTestInfoDirectory() {
    final String testInfoDir = getStringOption(CommandLineOptions.OPTION_TEST_INF_DIR);
    return testInfoDir;
  }

  public CommandLineOptions.LogLevel getLogLevel() {
    final String logLevelValue = getStringOption(
        CommandLineOptions.OPTION_LOG_LEVEL).toLowerCase();

    CommandLineOptions.LogLevel logLevel = null;
    for (CommandLineOptions.LogLevel ll : CommandLineOptions.LogLevel.values()) {
      if (ll.getLogLevel().equals(logLevelValue)) {
        logLevel = ll;
        break;
      }
    }

    if (logLevel == null) {
      System.err.printf(CommandLineOptions.LOG_LEVEL_ARGUMENT_ERROR_FMT,
          logLevel);
      System.exit(1);
    }

    return logLevel;
  }

  public String getLogFile() {
    final String logFile = getStringOption(CommandLineOptions.OPTION_LOG_FILE);
    return logFile;
  }

  /**
   * Test suite specs have the format:
   * <p>
   * &lt;component name&gt;[:[-]&lt;test case id&gt;[,[-]&lt;test case
   * id&gt;]*].
   * <p>
   * <p>
   * Prefixing a test case ID with a '-' character excludes the test case from
   * the component's test suite. Otherwise, the test case is included in the
   * components test suite.
   * <p>
   * <p>
   * Mixing includes and excludes is not supported.
   * 
   * @return A collection of specified test suites
   */
  public Collection<TestSuiteInfo> getTestSuites() {
    final String[] suiteSpecs = commandLine.getArgs();
    final Collection<TestSuiteInfo> suites = new ArrayList<TestSuiteInfo>();

    for (String suiteSpec : suiteSpecs) {
      final String[] parts = suiteSpec
          .split(CommandLineOptions.OPTION_SUITE_DELIMITER);
      final Collection<String> includes = new HashSet<String>();
      final Collection<String> excludes = new HashSet<String>();
      String componentName = null;
      boolean first = true;
      for (String part : parts) {
        if (first) {
          componentName = part;
          first = false;
        } else {
          final IArgumentParser parser = new ListParser(part);
          for (String testCase : parser.getArguments()) {
            if (testCase
                .startsWith(CommandLineOptions.OPTION_SUITE_EXCLUDE_PREFIX)) {
              excludes.add(testCase
                  .substring(CommandLineOptions.OPTION_SUITE_EXCLUDE_PREFIX
                      .length()));
            } else {
              includes.add(testCase);
            }
          }
        }
      }
      assert componentName != null : "No component name";

      if ((includes.size() == 0 && excludes.size() == 0)
          || (includes.size() > 0 && excludes.size() == 0)
          || (includes.size() == 0 && excludes.size() > 0)) {
        final TestSuiteInfo info = new TestSuiteInfo(componentName, includes,
            excludes);
        suites.add(info);
      } else {
        System.err.printf(CommandLineOptions.SUITE_ARGUMENT_ERROR_FMT,
            suiteSpec);
        System.exit(1);
      }
    }

    return suites;
  }

  private String getStringOption(String optionName) {
    final CommandLineOption option = getOption(optionName);
    final String value = option.getValue((String )option.getDefaultValue());
    return value;
  }

  private CommandLineOption getOption(String optionName) {
    final CommandLineOption option = (CommandLineOption )options
        .getOption(optionName);
    assert option != null : String.format("Option [%s] does not exist",
        optionName);
    return option;
  }
}
