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

class CommandLineOptions {
  public enum LogLevel {
    DEBUG("debug"), INFO("info"), WARN("warn"), ERROR("error"), FATAL("fatal");

    private String logLevelDesc;

    LogLevel(String logLevelString) {
      logLevelDesc = logLevelString;
    }

    public String getLogLevel() {
      return logLevelDesc;
    }
  }

  public static final String OPTION_TEST_INF_DIR = "d";
  public static final String OPTION_LONG_TEST_INF_DIR = "testdirectory";
  public static final String OPTION_TEST_INF_DIR_HELP = "Specifies the test suite info directory";

  public static final String OPTION_RESULT_DIR = "r";
  public static final String OPTION_LONG_RESULT_DIR = "resultsdirectory";
  public static final String OPTION_RESULT_DIR_HELP = "Specifies a test case results directory";

  public static final String OPTION_LOG_LEVEL = "ll";
  public static final String OPTION_LONG_LOG_LEVEL = "loglevel";
  public static final String OPTION_LOG_LEVEL_HELP = "Sets the logging level for the logging sub-system. Valid log levels "
      + "are debug, info, warn, error, and fatal";

  public static final String OPTION_LOG_FILE = "l";
  public static final String OPTION_LONG_LOG_FILE = "logfile";
  public static final String OPTION_LOG_FILE_HELP = "Specifies the file for logging";

  public static final String OPTION_PROPS_FILE = "p";
  public static final String OPTION_LONG_PROPS_FILE = "propfile";
  public static final String OPTION_PROPS_FILE_HELP = "Specifies the properties file to use";

  public static final String OPTION_HELP = "h";
  public static final String OPTION_LONG_HELP = "help";
  public static final String OPTION_HELP_HELP = "Displays application usage";

  public static final String DEFAULT_FMT = " (default: %s)";
  public static final String SUITE_ARGUMENT_ERROR_FMT = "Invalid test suite argument. Argument cannot contain both "
      + "includes and excludes: [%s]\n\n";
  public static final String LOG_LEVEL_ARGUMENT_ERROR_FMT = "Invalid log level: [%s]";

  public static final String OPTION_SUITE_DELIMITER = ":";
  public static final String OPTION_SUITE_EXCLUDE_PREFIX = "-";

  public static final String HELP_HEADER = "Where options are:";
  public static final String HELP_FOOTER = "Test suites are specified using the following format:\n"
      + "<component name>[:[-]<test case id>[,[-]<test case id>]*]\n"
      + "Prefixing a test case ID with a '-' character excludes the "
      + "test case from the component's test suite. Otherwise, only the "
      + "test cases specified are included in the components test suite. "
      + "Mixing includes and excludes is not supported.";
}
